package org.dogethereum.dogesubmitter.core;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.*;
import org.bitcoinj.store.BlockStoreException;
import org.dogethereum.dogesubmitter.constants.BridgeConstants;
import org.dogethereum.dogesubmitter.constants.SystemProperties;
import org.dogethereum.dogesubmitter.core.dogecoin.BlockListener;
import org.dogethereum.dogesubmitter.core.dogecoin.DogecoinWrapper;
import org.dogethereum.dogesubmitter.core.dogecoin.DogecoinWrapperImpl;
import org.dogethereum.dogesubmitter.core.dogecoin.TransactionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Informs DogeToken the doge-eth price
 * @author Oscar Guindzberg
 */
@Service
@Slf4j(topic = "DogeEthPriceOracle")
public class DogeEthPriceOracle {

    @Autowired
    private FederatorSupport federatorSupport;

    private SystemProperties config;

    private long latestPrice;

    public DogeEthPriceOracle() {}


    @PostConstruct
    public void setup() throws Exception {
        config = SystemProperties.CONFIG;
        if (config.isPriceOracleEnabled()) {
            new Timer("Oracle update doge-eth price").scheduleAtFixedRate(new UpdateDogeEthPriceTimerTask(), Calendar.getInstance().getTime(), 30 * 1000);
        }
    }

    public long getPrice() {
        String baseUrl = "https://api.coinmarketcap.com/v1/ticker/dogecoin/?convert=ETH";

        Client client = ClientBuilder.newClient();
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        JacksonJaxbJsonProvider provider = new JacksonJaxbJsonProvider();
        provider.setMapper(mapper);
        client.register(provider);
        WebTarget endpoint = client.target(baseUrl);
        Response response = endpoint
                //.request(MediaType.APPLICATION_JSON)
                .request()
                .get();
        switch (response.getStatus()) {
            case 200:
            case 201:
                DogeEthPriceResponse[] priceResponses = response.readEntity(DogeEthPriceResponse[].class);
                double price = priceResponses[0].price_eth;
                long priceDogeSatoshiToEthWei = (long) (price * Math.pow(10, 10));
                return priceDogeSatoshiToEthWei;
            default:
                throw new RuntimeException("Price feed exception");
        }
    }

    private static class DogeEthPriceResponse {
        public double price_eth;
    }

    private boolean priceHasChanged(long price, long latestPrice) {
        return price != latestPrice;
    }


    private class UpdateDogeEthPriceTimerTask extends TimerTask {
        @Override
        public void run() {
            try {
                if (!federatorSupport.isEthNodeSyncing()) {
                    long price = getPrice();
                    if (priceHasChanged(price, latestPrice)) {
                        latestPrice = price;
                        federatorSupport.updatePrice(price);
                    }
                } else {
                    log.warn("UpdateDogeEthPriceTimerTask skipped because the eth node is syncing blocks");
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

    }


}

