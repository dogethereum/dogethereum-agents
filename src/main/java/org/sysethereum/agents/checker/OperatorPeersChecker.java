/*
 * Copyright (C) 2017 RSK Labs Ltd.
 * Copyright (C) 2018 Coinfabrik and Oscar Guindzberg.
 * Copyright (C) 2019 Jagdeep Sidhu
 */
package org.sysethereum.agents.checker;

import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.PeerAddress;
import org.springframework.stereotype.Component;
import org.sysethereum.agents.constants.AgentConstants;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

/**
 * Makes sure the syscoin peer is up. Otherwise prevents the agent from starting (fail fast strategy)
 */
@Component
@Slf4j(topic = "OperatorPeersChecker")
public class OperatorPeersChecker {

    private final AgentConstants agentConstants;
    private final SyscoinPeerFactory syscoinPeerFactory;

    public OperatorPeersChecker(
            AgentConstants agentConstants,
            SyscoinPeerFactory syscoinPeerFactory
    ) {
        this.agentConstants = agentConstants;
        this.syscoinPeerFactory = syscoinPeerFactory;
    }

    @PostConstruct
    public void setup() throws Exception {
        int defaultPort = agentConstants.getSyscoinParams().getPort();
        List<String> peerStrings = List.of("127.0.0.1");
        List<PeerAddress> peerAddresses = syscoinPeerFactory.buildSyscoinPeerAddresses(defaultPort, peerStrings);

        if (peerAddresses.isEmpty()) {
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
        } catch (IOException ex) {
            throw new RuntimeException("Cannot connect to Syscoin node " + address.getSocketAddress().getHostName() + ":" + address.getSocketAddress().getPort());
        }
    }

}
