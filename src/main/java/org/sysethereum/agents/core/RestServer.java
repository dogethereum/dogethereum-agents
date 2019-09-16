/*
 * Copyright (C) 2019 Jagdeep Sidhu
 */
package org.sysethereum.agents.core;


import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.Sha256Hash;
import org.sysethereum.agents.core.syscoin.Keccak256Hash;
import org.sysethereum.agents.core.syscoin.SyscoinRPCClient;
import org.sysethereum.agents.util.RestError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Manages REST requests for SPV Proofs
 * @author Jag Sidhu
 */
@Service
@Slf4j(topic = "RestServer")
public class RestServer {
    private static final Logger logger = LoggerFactory.getLogger("RestServer");

    private final SyscoinToEthClient syscoinToEthClient;

    public RestServer(SyscoinToEthClient syscoinToEthClient) {
        this.syscoinToEthClient = syscoinToEthClient;
    }

    @PostConstruct
    public void setup() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/", new InfoHandler());
        server.createContext("/spvproof", new GetSPVHandler());
        server.createContext("/superblockbysyscoinblock", new GetSuperblockBySyscoinHandler());
        server.createContext("/superblock", new GetSuperblockHandler());
        server.createContext("/syscoinrpc", new GetSyscoinRPCHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }
    // http://localhost:8000/info
    static class InfoHandler implements HttpHandler {
        public void handle(HttpExchange httpExchange) throws IOException {
            String response = "Valid Superblock calls: " + System.lineSeparator() +
                    "\t/spvproof?hash=<blockhash>" + System.lineSeparator() +
                    "\t/spvproof?height=<blockheight>" + System.lineSeparator() +
                    "\t/superblockbysyscoinblock?hash=<blockhash>" + System.lineSeparator() +
                    "\t/superblockbysyscoinblock?height=<blockheight>" + System.lineSeparator() +
                    "\t/superblock?hash=<superblockid>" + System.lineSeparator() +
                    "\t/superblock?height=<superblockheight>" + System.lineSeparator() + System.lineSeparator() +
                    "Valid Syscoin RPC calls: " + System.lineSeparator() +
                    "\t/syscoinrpc?method=<methodname>&param1name=<param1value>&paramNname=<paramNvalue>...";
            RestServer.writeResponse(httpExchange, response);
        }
    }
    private class GetSPVHandler implements HttpHandler {
        public void handle(HttpExchange httpExchange) throws IOException {
            httpExchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

            if (httpExchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                httpExchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS");
                httpExchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
                httpExchange.sendResponseHeaders(204, -1);
                return;
            }
            StringBuilder response = new StringBuilder();
            LinkedHashMap<String,String> parms = RestServer.queryToMap(httpExchange.getRequestURI().getQuery());

            try {
                String hash = parms.get("hash");
                if(hash != null && hash.startsWith("0x"))
                    hash = hash.substring(2);
                String height = parms.get("height");
                String spvString = syscoinToEthClient.getSuperblockSPVProof(hash != null? Sha256Hash.wrap(hash): null, height != null? Integer.decode(height): -1);
                response.append(spvString);
            }
            catch(java.lang.Exception exception){
                RestError error = new RestError("Could not get SPV proof, internal error!");
                Gson g = new Gson();
                response.append(g.toJson(error));
            }
            RestServer.writeResponse(httpExchange, response.toString());
        }
    }
    private class GetSuperblockBySyscoinHandler implements HttpHandler {
        public void handle(HttpExchange httpExchange) throws IOException {
            httpExchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

            if (httpExchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                httpExchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS");
                httpExchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
                httpExchange.sendResponseHeaders(204, -1);
                return;
            }
            StringBuilder response = new StringBuilder();
            LinkedHashMap<String,String> parms = RestServer.queryToMap(httpExchange.getRequestURI().getQuery());

            try {
                String hash = parms.get("hash");
                if(hash != null && hash.startsWith("0x"))
                    hash = hash.substring(2);
                String height = parms.get("height");
                String superblockString = syscoinToEthClient.getSuperblockBySyscoinBlock(hash != null? Sha256Hash.wrap(hash): null, height != null? Integer.decode(height): -1);
                response.append(superblockString);
            }
            catch(java.lang.Exception exception){
                RestError error = new RestError("Could not get Superblock, internal error!");
                Gson g = new Gson();
                response.append(g.toJson(error));
            }
            RestServer.writeResponse(httpExchange, response.toString());
        }
    }

    private class GetSuperblockHandler implements HttpHandler {
        public void handle(HttpExchange httpExchange) throws IOException {
            httpExchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

            if (httpExchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                httpExchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS");
                httpExchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
                httpExchange.sendResponseHeaders(204, -1);
                return;
            }
            StringBuilder response = new StringBuilder();
            LinkedHashMap<String,String> parms = RestServer.queryToMap(httpExchange.getRequestURI().getQuery());

            try {
                String hash = parms.get("hash");
                if(hash != null && hash.startsWith("0x"))
                    hash = hash.substring(2);
                String height = parms.get("height");
                String superblockString = syscoinToEthClient.getSuperblock(hash != null? Keccak256Hash.wrap(hash): null, height != null? Integer.decode(height): -1);
                response.append(superblockString);
            }
            catch(java.lang.Exception exception){
                RestError error = new RestError("Could not get Superblock, internal error!");
                Gson g = new Gson();
                response.append(g.toJson(error));
            }
            RestServer.writeResponse(httpExchange, response.toString());
        }
    }

    public static class GetSyscoinRPCHandler implements HttpHandler {
        public void handle(HttpExchange httpExchange) throws IOException {
            httpExchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

            if (httpExchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                httpExchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS");
                httpExchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
                httpExchange.sendResponseHeaders(204, -1);
                return;
            }
            StringBuilder response = new StringBuilder();
            LinkedHashMap<String,String> params = RestServer.queryToMap(httpExchange.getRequestURI().getQuery());
            try {
                String method = params.get("method");
                params.remove("method");
                ArrayList<Object> paramList = new ArrayList<Object>(params.values());
                SyscoinRPCClient sc = new SyscoinRPCClient();
                response.append(sc.makeCoreCall(method, paramList));
            } catch (Exception e) {
                RestError error = new RestError(e.toString());
                Gson g = new Gson();
                response.append(g.toJson(error));
            }
            RestServer.writeResponse(httpExchange, response.toString());
        }
    }

    /**
     * returns the url parameters in a map preserving order of insertion
     * @param query
     * @return map
     */
    public static LinkedHashMap<String, String> queryToMap(String query){
        LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
        for (String param : query.split("&")) {
            String[] pair = param.split("=");
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

