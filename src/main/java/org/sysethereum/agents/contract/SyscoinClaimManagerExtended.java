package org.sysethereum.agents.contract;

import org.web3j.protocol.Web3j;
import org.web3j.tx.TransactionManager;

import java.math.BigInteger;

public class SyscoinClaimManagerExtended extends SyscoinClaimManager {

    public SyscoinClaimManagerExtended(
            String contractAddress,
            Web3j web3j,
            TransactionManager transactionManager,
            BigInteger gasPrice,
            BigInteger gasLimit
    ) {
        super(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

}
