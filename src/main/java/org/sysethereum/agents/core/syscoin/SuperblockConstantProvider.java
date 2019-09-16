package org.sysethereum.agents.core.syscoin;

import java.math.BigInteger;

/**
 * Interface for interacting with superblock chain.
 */
public interface SuperblockConstantProvider {
    BigInteger getSuperblockDuration() throws Exception;
    BigInteger getSuperblockDelay() throws Exception;
    BigInteger getSuperblockTimeout() throws Exception;
}
