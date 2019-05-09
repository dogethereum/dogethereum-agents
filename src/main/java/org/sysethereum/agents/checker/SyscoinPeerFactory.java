/*
 * Copyright (C) 2017 RSK Labs Ltd.
 * Copyright (C) 2018 Coinfabrik and Oscar Guindzberg.
 * Copyright (C) 2019 Jagdeep Sidhu
 */
package org.sysethereum.agents.checker;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.PeerAddress;
import org.sysethereum.agents.constants.SystemProperties;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;



/**
 * Builds a PeerAddress list based on a String list and a default port
 */
public class SyscoinPeerFactory {

    public static List<PeerAddress> buildSyscoinPeerAddresses(int defaultPort, List<String> syscoinPeerAddressesString) throws UnknownHostException {
        SystemProperties config = SystemProperties.CONFIG;
        NetworkParameters networkParams = config.getAgentConstants().getSyscoinParams();
        List<PeerAddress> syscoinPeerAddresses = new ArrayList<>();
        if(syscoinPeerAddressesString != null) {
            for (String syscoinPeerAddressString : syscoinPeerAddressesString) {
                PeerAddress syscoinPeerAddress;
                if (syscoinPeerAddressString.indexOf(':') == -1) {
                    syscoinPeerAddress = new PeerAddress(networkParams, InetAddress.getByName(syscoinPeerAddressString), defaultPort);
                } else {
                    String syscoinPeerAddressesHost = syscoinPeerAddressString.substring(0, syscoinPeerAddressString.indexOf(':'));
                    String syscoinPeerAddressesPort = syscoinPeerAddressString.substring(syscoinPeerAddressString.indexOf(':') + 1);
                    syscoinPeerAddress = new PeerAddress(networkParams, InetAddress.getByName(syscoinPeerAddressesHost), Integer.valueOf(syscoinPeerAddressesPort));
                }
                syscoinPeerAddresses.add(syscoinPeerAddress);
            }
        }
        return syscoinPeerAddresses;
    }
}
