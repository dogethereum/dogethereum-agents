package org.dogethereum.agents.util;

import org.bitcoinj.core.LegacyAddress;
import org.bitcoinj.script.Script;

/**
 * Exposes Operator public key related information
 */

public interface OperatorPublicKeyHandler {
    Script getOutputScript();

    LegacyAddress getAddress();

    byte[] getPublicKeyHash();

    /**
     * Returns address creation time expressed in seconds since the epoch.
     */
    long getAddressCreationTime();
}
