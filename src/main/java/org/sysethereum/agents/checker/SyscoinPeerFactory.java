/*
 * Copyright (C) 2017 RSK Labs Ltd.
 * Copyright (C) 2018 Coinfabrik and Oscar Guindzberg.
 * Copyright (C) 2019 Jagdeep Sidhu
 */
package org.sysethereum.agents.checker;

import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.PeerAddress;
import org.springframework.stereotype.Service;
import org.sysethereum.agents.constants.AgentConstants;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;


/**
 * Builds a PeerAddress list based on a String list and a default port
 */
@Service
@Slf4j(topic = "SyscoinPeerFactory")
public class SyscoinPeerFactory {

    private final AgentConstants agentConstants;

    public SyscoinPeerFactory(AgentConstants agentConstants) {
        this.agentConstants = agentConstants;
    }

    /**
     * @param defaultPort Port will be used if no port is specified for a syscoinPeerAddresses element
     * @param syscoinPeerAddresses List of Syscoin peer addresses in format: "host[:port]"
     * @return List of Syscoin peer addresses
     * @throws UnknownHostException
     */
    public List<PeerAddress> buildSyscoinPeerAddresses(int defaultPort, List<String> syscoinPeerAddresses) throws UnknownHostException {
        NetworkParameters networkParams = agentConstants.getSyscoinParams();
        List<PeerAddress> result = new ArrayList<>();

        if (syscoinPeerAddresses != null) {
            for (String addr : syscoinPeerAddresses) {
                PeerAddress peerAddress;
                if (addr.indexOf(':') == -1) {
                    peerAddress = new PeerAddress(networkParams, InetAddress.getByName(addr), defaultPort);
                } else {
                    String host = addr.substring(0, addr.indexOf(':'));
                    String port = addr.substring(addr.indexOf(':') + 1);
                    peerAddress = new PeerAddress(networkParams, InetAddress.getByName(host), Integer.parseInt(port));
                }
                result.add(peerAddress);
            }
        }
        return result;
    }
}
