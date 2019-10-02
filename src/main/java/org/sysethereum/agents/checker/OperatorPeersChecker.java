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

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
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

    public void setup() {
        int defaultPort = agentConstants.getSyscoinParams().getPort();
        List<String> peerStrings = List.of("127.0.0.1");
        List<PeerAddress> peerAddresses;

        try {
            peerAddresses = syscoinPeerFactory.buildSyscoinPeerAddresses(defaultPort, peerStrings);

            if (peerAddresses.isEmpty()) {
                // Can't happen until we implement peer list configuration
                throw new RuntimeException("No Syscoin Peers");
            }
            for (PeerAddress address : peerAddresses) {
                checkAddressOrFail(address.getSocketAddress());
            }
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkAddressOrFail(InetSocketAddress isa) {
        try {
            Socket socket = new Socket(isa.getHostName(), isa.getPort());
            socket.close();
        } catch (IOException ex) {
            throw new RuntimeException("Cannot connect to Syscoin node " + isa.getHostName() + ":" + isa.getPort());
        }
    }

}
