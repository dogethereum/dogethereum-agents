package org.dogethereum.dogesubmitter.util;

import org.bitcoinj.core.Address;
import org.bitcoinj.script.Script;

/**
 * Operator public key information provider
 */

public interface OperatorPublicKeyHandler {
    Script getOutputScript();

    Address getAddress();

    long getAddressCreationTime();
}
