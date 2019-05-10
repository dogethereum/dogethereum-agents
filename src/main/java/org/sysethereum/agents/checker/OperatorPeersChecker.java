/*
 * Copyright (C) 2017 RSK Labs Ltd.
 * Copyright (C) 2018 Coinfabrik and Oscar Guindzberg.
 * Copyright (C) 2019 Jagdeep Sidhu
 */
package org.sysethereum.agents.checker;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.PeerAddress;
import org.sysethereum.agents.constants.SystemProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

@Component
@Slf4j(topic = "OperatorPeersChecker")
/**
 * Makes sure the syscoin peer is up. Otherwise prevents the agent from starting (fail fast strategy)
 */
public class OperatorPeersChecker {

    public OperatorPeersChecker() {
    }

    @PostConstruct
    public void setup() throws Exception {
        SystemProperties config = SystemProperties.CONFIG;
        int defaultPort = config.getAgentConstants().getSyscoinParams().getPort();
        List<String> peerStrings = Lists.newArrayList("127.0.0.1");
        List<PeerAddress> peerAddresses = SyscoinPeerFactory.buildSyscoinPeerAddresses(defaultPort, peerStrings);
        if (peerAddresses == null || peerAddresses.isEmpty()) {
            // Can't happen until we implement peer list configuration
            throw new RuntimeException("No Syscoin Peers");
        }
        for (PeerAddress address : peerAddresses) {
            checkPeerAddress(address);
        }
    }

    private void checkPeerAddress(PeerAddress address) {
        InetSocketAddress saddr = address.getSocketAddress();
        String host = saddr.getHostName();
        int port = saddr.getPort();

        try {
            Socket socket = new Socket(host, port);
            socket.close();
        }
        catch (IOException ex) {
            throw new RuntimeException("Cannot connect to Syscoin node " + address.getSocketAddress().getHostName() + ":" + address.getSocketAddress().getPort());
        }
    }

}
