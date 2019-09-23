package org.sysethereum.agents.service.rest;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
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
        if (setOriginAndHandleOptionsMethod(httpExchange)) return;

        LinkedHashMap<String, String> params = queryToMap(httpExchange.getRequestURI().getQuery());
        StringBuilder response = new StringBuilder();
        try {
            String method = params.get("method");
            params.remove("method");
            ArrayList<Object> paramList = new ArrayList<>(params.values());
            response.append(syscoinRPCClient.makeCoreCall(method, paramList));
        } catch (Exception e) {
            RestError error = new RestError(e.toString());
            response.append(gson.toJson(error));
        }
        writeResponse(httpExchange, response.toString());
    }
}
