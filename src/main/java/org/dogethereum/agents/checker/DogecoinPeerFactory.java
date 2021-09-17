/*
 * Copyright (C) 2017 RSK Labs Ltd.
 * Copyright (C) 2018 Coinfabrik and Oscar Guindzberg.
 */
package org.dogethereum.agents.checker;

import org.bitcoinj.core.PeerAddress;
import org.libdohj.params.AbstractDogecoinParams;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;


/**
 * Builds a PeerAddress list based on a String list and a default port
 */
public class DogecoinPeerFactory {

    public static List<PeerAddress> buildDogecoinPeerAddresses(AbstractDogecoinParams dogeParams, List<String> dogecoinPeerAddressesString) throws UnknownHostException {
        List<PeerAddress> dogecoinPeerAddresses = new ArrayList<>();
        if(dogecoinPeerAddressesString != null) {
            for (String dogecoinPeerAddressString : dogecoinPeerAddressesString) {
                PeerAddress dogecoinPeerAddress;
                if (dogecoinPeerAddressString.indexOf(':') == -1) {
                    dogecoinPeerAddress = new PeerAddress(dogeParams, InetAddress.getByName(dogecoinPeerAddressString), dogeParams.getPort());
                } else {
                    String dogecoinPeerAddressesHost = dogecoinPeerAddressString.substring(0, dogecoinPeerAddressString.indexOf(':'));
                    String dogecoinPeerAddressesPort = dogecoinPeerAddressString.substring(dogecoinPeerAddressString.indexOf(':') + 1);
                    dogecoinPeerAddress = new PeerAddress(dogeParams, InetAddress.getByName(dogecoinPeerAddressesHost), Integer.valueOf(dogecoinPeerAddressesPort));
                }
                dogecoinPeerAddresses.add(dogecoinPeerAddress);
            }
        }
        return dogecoinPeerAddresses;
    }
}
