package org.dogethereum.agents.core;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import lombok.extern.slf4j.Slf4j;
import org.dogethereum.agents.constants.SystemProperties;
import org.dogethereum.agents.core.eth.EthWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Informs DogeToken of the Doge-Eth price
 * @author Oscar Guindzberg
 */
@Service
@Slf4j(topic = "PriceOracleClient")
public class PriceOracleClient {

    @Autowired
    private EthWrapper ethWrapper;

    private SystemProperties config;

    private long latestPrice;

    public PriceOracleClient() {}


    @PostConstruct
    public void setup() throws Exception {
        config = SystemProperties.CONFIG;
        if (config.isPriceOracleEnabled()) {
            new Timer("Oracle update doge-eth price").scheduleAtFixedRate(new UpdateDogeEthPriceTimerTask(), getFirstExecutionDate(), config.getAgentConstants().getPriceOracleTimerTaskPeriod());
        }
    }

    private Date getFirstExecutionDate() {
        Calendar firstExecution = Calendar.getInstance();
        firstExecution.add(Calendar.SECOND, 5);
        return firstExecution.getTime();
    }


    public long getCoinmarketcapPrice() {
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
                if (!ethWrapper.isEthNodeSyncing()) {
                    long price = getCoinmarketcapPrice();
                    if (priceHasChanged(price, latestPrice)) {
                        latestPrice = price;
                        ethWrapper.updateContractFacadesGasPrice();
                        ethWrapper.updatePrice(price);
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

