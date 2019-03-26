package org.sysethereum.agents.core.syscoin;

import java.math.BigInteger;

/**
 * Interface for interacting with superblock chain.
 */
public interface SuperblockConstantProvider {
    public BigInteger getSuperblockDuration() throws Exception;
    public BigInteger getSuperblockDelay() throws Exception;
    public BigInteger getSuperblockTimeout() throws Exception;
}
