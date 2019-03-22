/*
 * Copyright (C) 2017 RSK Labs Ltd.
 * Copyright (C) 2018 Coinfabrik and Oscar Guindzberg.
 */
package org.dogethereum.agents.core;


import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.Sha256Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.abi.datatypes.generated.Uint256;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages REST requests for SPV Proofs
 * @author Jag Sidhu
 */
@Service
@Slf4j(topic = "RestServer")
public class RestServer {
    private static final Logger log = LoggerFactory.getLogger("LocalAgentConstants");

    public RestServer() {}
    @Autowired
    protected DogeToEthClient dogeToEthClient;

    @PostConstruct
    public void setup() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/", new InfoHandler());
        server.createContext("/spvproof", new GetHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }
    // http://localhost:8000/info
    static class InfoHandler implements HttpHandler {
        public void handle(HttpExchange httpExchange) throws IOException {
            String response = "Use /spvproof?hash=<uint256> to get Superblock SPV Proof";
            RestServer.writeResponse(httpExchange, response.toString());
        }
    }
    private class GetHandler implements HttpHandler {
        public void handle(HttpExchange httpExchange) throws IOException {
            StringBuilder response = new StringBuilder();
            Map<String,String> parms = RestServer.queryToMap(httpExchange.getRequestURI().getQuery());

            try {
                String spvString = dogeToEthClient.getSuperblockSPVProof(Sha256Hash.wrap(parms.get("hash")));
                response.append(spvString);
            }
            catch(java.lang.Exception exception){
                response.append("Could not get SPV proof, internal error!");
            }
            RestServer.writeResponse(httpExchange, response.toString());
        }
    }

    /**
     * returns the url parameters in a map
     * @param query
     * @return map
     */
    public static Map<String, String> queryToMap(String query){
        Map<String, String> result = new HashMap<String, String>();
        for (String param : query.split("&")) {
            String pair[] = param.split("=");
            if (pair.length>1) {
                result.put(pair[0], pair[1]);
            }else{
                result.put(pair[0], "");
            }
        }
        return result;
    }
    public static void writeResponse(HttpExchange httpExchange, String response) throws IOException {
        httpExchange.sendResponseHeaders(200, response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }





}

