package org.sysethereum.agents.core.eth;

import java.math.BigInteger;


public class BridgeTransferInfo {
    public enum BridgeTransferStatus {
        Uninitialized,
        Ok,
        CancelRequested,
        CancelChallenged,
        CancelOk;
        public static BridgeTransferStatus fromInteger(int x) {
            switch(x) {
                case 0:
                    return Uninitialized;
                case 1:
                    return Ok;
                case 2:
                    return CancelRequested;
                case 3:
                    return CancelChallenged;
                case 4:
                    return CancelOk;
            }
            return null;
        }
    }
    public Integer timestamp;
    public BigInteger value;
    public String erc20ContractAddress;
    public String tokenFreezerAddress;
    public Integer assetGUID;
    public BridgeTransferStatus status;
}
