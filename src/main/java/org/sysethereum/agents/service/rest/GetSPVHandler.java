package org.sysethereum.agents.service.rest;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.Sha256Hash;
import org.springframework.stereotype.Service;
import org.sysethereum.agents.core.SyscoinToEthClient;
import org.sysethereum.agents.util.RestError;

import java.io.IOException;
import java.util.LinkedHashMap;

@Service
@Slf4j(topic = "GetSPVHandler")
public class GetSPVHandler extends CommonHttpHandler {

    private final Gson gson;
    private final SyscoinToEthClient syscoinToEthClient;

    public GetSPVHandler(
            Gson gson,
            SyscoinToEthClient syscoinToEthClient
    ) {
        this.gson = gson;
        this.syscoinToEthClient = syscoinToEthClient;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        if (setOriginAndHandleOptionsMethod(httpExchange)) return;

        StringBuilder response = new StringBuilder();
        LinkedHashMap<String, String> params = queryToMap(httpExchange.getRequestURI().getQuery());

        try {
            String hash = params.get("hash");
            if (hash != null && hash.startsWith("0x"))
                hash = hash.substring(2);
            String height = params.get("height");
            String spvString = syscoinToEthClient.getSuperblockSPVProof(hash != null ? Sha256Hash.wrap(hash) : null, height != null ? Integer.decode(height) : -1);
            response.append(spvString);
        } catch (Exception exception) {
            RestError error = new RestError("Could not get SPV proof, internal error!");
            response.append(gson.toJson(error));
        }
        writeResponse(httpExchange, response.toString());
    }
}
