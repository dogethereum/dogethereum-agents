package org.dogethereum.dogesubmitter.checker;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.PeerAddress;
import org.dogethereum.dogesubmitter.constants.SystemProperties;
import org.dogethereum.dogesubmitter.core.dogecoin.DogecoinPeerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

@Component
@Slf4j(topic = "OperatorPeersChecker")
public class OperatorPeersChecker {

    public OperatorPeersChecker() {
    }

    @PostConstruct
    public void setup() throws Exception {
        SystemProperties config = SystemProperties.CONFIG;
        int defaultPort = config.getBridgeConstants().getDogeParams().getPort();
        List<String> peerStrings = Lists.newArrayList("127.0.0.1");
        List<PeerAddress> peerAddresses = DogecoinPeerFactory.buildDogecoinPeerAddresses(defaultPort, peerStrings);
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
