package org.sysethereum.agents.service.rest;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpsExchange;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.sysethereum.agents.core.syscoin.SyscoinRPCClient;
import org.sysethereum.agents.util.RestError;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

@Service
@Slf4j(topic = "GetSyscoinRPCHandler")
public class GetSyscoinRPCHandler extends CommonHttpHandler {

    private final Gson gson;
    private final SyscoinRPCClient syscoinRPCClient;

    public GetSyscoinRPCHandler(Gson gson, SyscoinRPCClient syscoinRPCClient) {
        this.gson = gson;
        this.syscoinRPCClient = syscoinRPCClient;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        HttpsExchange httpsExchange = (HttpsExchange) httpExchange;
        if (setOriginAndHandleOptionsMethod(httpsExchange)) return;

        LinkedHashMap<String, String> params = queryToMap(httpsExchange.getRequestURI().getQuery());
        String response;
        try {
            String method = params.get("method");
            params.remove("method");
            ArrayList<Object> paramList = new ArrayList<>(params.values());
            response = syscoinRPCClient.makeCoreCall(method, paramList);
        } catch (Exception e) {
            response = gson.toJson(new RestError(e.toString()));
        }
        writeResponse(httpsExchange, response);
    }
}
