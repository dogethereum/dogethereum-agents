package org.sysethereum.agents.service.rest;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
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
        if (setOriginAndHandleOptionsMethod(httpExchange)) return;

        LinkedHashMap<String, String> parms = queryToMap(httpExchange.getRequestURI().getQuery());
        StringBuilder response = new StringBuilder();

        try {
            String hash = parms.get("hash");
            if (hash != null && hash.startsWith("0x"))
                hash = hash.substring(2);
            String height = parms.get("height");
            String superblockString = syscoinToEthClient.getSuperblock(hash != null ? Keccak256Hash.wrap(hash) : null, height != null ? Integer.decode(height) : -1);
            response.append(superblockString);
        } catch (Exception exception) {
            RestError error = new RestError("Could not get Superblock, internal error!");
            response.append(gson.toJson(error));
        }
        writeResponse(httpExchange, response.toString());
    }

}
