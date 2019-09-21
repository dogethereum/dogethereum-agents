/*
 * Copyright (C) 2019 Jagdeep Sidhu
 */
package org.sysethereum.agents.core.syscoin;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2Session;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.stereotype.Service;
import org.sysethereum.agents.constants.SystemProperties;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

@Service
@Slf4j(topic = "SyscoinRPCClient")
public class SyscoinRPCClient {

    private final JSONRPC2Session rpcSession;

    public SyscoinRPCClient(SystemProperties config) throws MalformedURLException {
        rpcSession = new JSONRPC2Session(new URL(config.syscoinRPCURL()));
        rpcSession.getOptions().ignoreVersion(true);
        rpcSession.setConnectionConfigurator(new SyscoinRPCBasicAuth(config.syscoinRPCUser(), config.syscoinRPCPassword()));
    }

    public String makeCoreCall(String method, List<Object> params) throws JSONRPC2SessionException {
        JSONObject result = rpcCall(method, params);
        if (result != null) {
            return result.toJSONString();
        }
        throw new JSONRPC2SessionException("Empty response not expected");
    }

    private JSONObject rpcCall(String method, List<Object> params) throws JSONRPC2SessionException {
        JSONRPC2Request request = new JSONRPC2Request(method, params, 1);
        JSONRPC2Response response = rpcSession.send(request);
        JSONObject result;
        if (response.indicatesSuccess()) {
            result = (JSONObject) response.getResult();
        } else {
            throw new JSONRPC2SessionException(response.getError().getMessage());
        }
        return result;
    }
}
