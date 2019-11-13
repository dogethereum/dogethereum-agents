package org.sysethereum.agents.service.rest;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpsExchange;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.sysethereum.agents.core.SyscoinToEthClient;
import org.sysethereum.agents.core.syscoin.Keccak256Hash;
import org.sysethereum.agents.util.RestError;

import java.io.IOException;
import java.util.LinkedHashMap;

@Service
@Slf4j(topic = "GetSuperblockHandler")
public class GetSuperblockHandler extends CommonHttpHandler {

    private final Gson gson;
    private final SyscoinToEthClient syscoinToEthClient;

    public GetSuperblockHandler(
            Gson gson,
            SyscoinToEthClient syscoinToEthClient
    ) {
        this.gson = gson;
        this.syscoinToEthClient = syscoinToEthClient;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        HttpsExchange httpsExchange = (HttpsExchange) httpExchange;
        if (setOriginAndHandleOptionsMethod(httpsExchange)) return;

        LinkedHashMap<String, String> params = queryToMap(httpsExchange.getRequestURI().getQuery());
        String response;

        try {
            String hash = sanitizeHash(params.get("hash"));
            String height = params.get("height");
            response = gson.toJson(syscoinToEthClient.getSuperblock(Keccak256Hash.wrapNullable(hash), height != null ? Integer.decode(height) : -1));
        } catch (Exception exception) {
            response = gson.toJson(new RestError("Could not get Superblock, internal error!"));
        }

        writeResponse(httpsExchange, response);
    }

}
