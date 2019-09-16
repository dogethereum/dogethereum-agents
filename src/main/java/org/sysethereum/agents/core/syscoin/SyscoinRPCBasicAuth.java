/*
 * Copyright (C) 2019 Jagdeep Sidhu
 */
package org.sysethereum.agents.core.syscoin;


import com.thetransactioncompany.jsonrpc2.client.ConnectionConfigurator;
import lombok.extern.slf4j.Slf4j;

import java.net.HttpURLConnection;
import java.util.Base64;

@Slf4j(topic = "SyscoinRPCBasicAuth")
public class SyscoinRPCBasicAuth implements ConnectionConfigurator {

    private String username;
    private String password;

    public SyscoinRPCBasicAuth(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void configure(HttpURLConnection conn) {
        String encoding = Base64.getEncoder().encodeToString((this.username + ":" + this.password).getBytes());
        conn.setRequestProperty("Authorization", "Basic " + encoding);
    }
}
