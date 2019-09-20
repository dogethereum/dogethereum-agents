/*
 * Copyright (C) 2019 Jagdeep Sidhu
 */
package org.sysethereum.agents.core.syscoin;


import lombok.extern.slf4j.Slf4j;
// The Client sessions package
import com.thetransactioncompany.jsonrpc2.client.*;

// The Base package for representing JSON-RPC 2.0 messages
import com.thetransactioncompany.jsonrpc2.*;

// The JSON Smart package for JSON encoding/decoding (optional)
import net.minidev.json.*;

// For creating URLs
import java.net.*;
import java.util.List;

import org.springframework.stereotype.Component;
import org.sysethereum.agents.constants.SystemProperties;

@Slf4j(topic = "SyscoinRPCClient")
public class SyscoinRPCClient {

    private int requestId = 0;

    private JSONRPC2Session rpcSession;
    public SyscoinRPCClient() throws MalformedURLException {
        SystemProperties config = SystemProperties.CONFIG;
        rpcSession = new JSONRPC2Session(new URL(config.syscoinRPCURL()));
        rpcSession.getOptions().ignoreVersion(true);
        rpcSession.setConnectionConfigurator(new SyscoinRPCBasicAuth(config.syscoinRPCUser(),config.syscoinRPCPassword()));
    }

    public String makeCoreCall(String method, List<Object> params) throws JSONRPC2SessionException {
        JSONObject result = rpcCall(method, params);
        if (result != null) {
            return result.toJSONString();
        }
        throw new JSONRPC2SessionException("Empty response not expected");
    }

    private JSONObject rpcCall(String method, List<Object> params) throws JSONRPC2SessionException {
        JSONRPC2Request request = new JSONRPC2Request(method, params, ++requestId);
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
