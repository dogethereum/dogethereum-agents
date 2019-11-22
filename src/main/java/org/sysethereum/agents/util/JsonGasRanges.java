package org.sysethereum.agents.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j(topic = "JsonGasRanges")
public class JsonGasRanges {
    private static final Logger logger = LoggerFactory.getLogger("JsonGasRanges");
    private final Timer timer;
    private BigInteger gasPrice;
    private int errorCount = 0;
    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }
    @Autowired
    public JsonGasRanges() {
        this.gasPrice = BigInteger.ZERO;
        this.timer = new Timer("JsonGasRanges", true);
    }
    public boolean setup() {
        try {
            timer.scheduleAtFixedRate(
                    new UpdateGasPrice(),
                    1_000, // 1 second
                    10 * 60 * 1000 // 10 min
            );
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }

        return true;
    }
    public BigInteger gasPrice(){
        return gasPrice;
    }
    public void cleanUp() {
        timer.cancel();
        timer.purge();
    }
    // pull in gas price ranges and find a target price that allows to confirm within 1 minute
    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }

    private class UpdateGasPrice extends TimerTask {
        @Override
        public void run() {
            try {
                JSONObject json = readJsonFromUrl("https://ethgasstation.info/json/ethgasAPI.json");
                JSONObject rangeObject = (JSONObject)json.get("gasPriceRange");
                TreeMap<Double,Double> ranges = new TreeMap<Double,Double>();
                Iterator<String> keys = rangeObject.keys();

                while(keys.hasNext()) {
                    String key = keys.next();
                    Double value = (Double)rangeObject.get(key);
                    Integer keyInt = Integer.parseInt(key);
                    Double keygWei = keyInt.doubleValue()/10;
                    ranges.put(keygWei,value);
                }
                Set set = ranges.entrySet();
                Iterator i = set.iterator();

                // convert to gas price
                long multiple = 1000000000L;
                Double key = Double.NaN;
                while(i.hasNext()) {
                    Map.Entry me = (Map.Entry)i.next();
                    Double value = (Double)me.getValue();
                    key = (Double)me.getKey();
                    // target is 1 min or less for fee
                    if(value.doubleValue() <= 1.0){
                        // convert to gas price
                        gasPrice = BigInteger.valueOf((long)key.doubleValue()*multiple);
                        errorCount = 0;
                        return;
                    }
                }
                if(!key.isNaN()) {
                    // set in case we don't find a target try to use highest target fee found in response
                    gasPrice = BigInteger.valueOf((long) key.doubleValue() * multiple);
                }
                errorCount = 0;
            } catch (Exception e) {
                errorCount++;
                if(errorCount > 5){
                    gasPrice = BigInteger.ZERO;
                }
                logger.error(e.getMessage(), e);
            }
        }
    }
}