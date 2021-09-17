/*
 * Copyright (C) 2017 RSK Labs Ltd.
 * Copyright (C) 2018 Coinfabrik and Oscar Guindzberg.
 */
package org.dogethereum.agents.checker;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.PeerAddress;
import org.dogethereum.agents.constants.SystemProperties;
import org.libdohj.params.AbstractDogecoinParams;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

@Component
@Slf4j(topic = "OperatorPeersChecker")
/**
 * Makes sure the dogecoin peer is up. Otherwise prevents the agent from starting (fail fast strategy)
 */
public class OperatorPeersChecker {

    public OperatorPeersChecker() {
    }

    @PostConstruct
    public void setup() throws Exception {
        SystemProperties config = SystemProperties.CONFIG;
        AbstractDogecoinParams dogeParams = config.getAgentConstants().getDogeParams();
        List<String> peerStrings = Lists.newArrayList("127.0.0.1");
        List<PeerAddress> peerAddresses = DogecoinPeerFactory.buildDogecoinPeerAddresses(dogeParams, peerStrings);
        if (peerAddresses == null || peerAddresses.isEmpty()) {
            // Can't happen until we implement peer list configuration
            throw new RuntimeException("No Dogecoin Peers");
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
            throw new RuntimeException("Cannot connect to Dogecoin node " + address.getSocketAddress().getHostName() + ":" + address.getSocketAddress().getPort());
        }
    }

}
