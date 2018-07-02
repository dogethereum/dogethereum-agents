package org.dogethereum.dogesubmitter.core.dogecoin;

import java.math.BigInteger;

public interface SuperblockConstantProvider {
    public BigInteger getSuperblockDuration() throws Exception;
    public BigInteger getSuperblockDelay() throws Exception;
    public BigInteger getSuperblockTimeout() throws Exception;
}
