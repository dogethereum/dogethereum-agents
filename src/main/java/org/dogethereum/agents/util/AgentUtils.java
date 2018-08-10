package org.dogethereum.agents.util;

import org.bitcoinj.core.*;
import org.bitcoinj.script.Script;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.wallet.Wallet;
import org.dogethereum.agents.constants.AgentConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AgentUtils {

    private static final Logger logger = LoggerFactory.getLogger("AgentUtils");

    public static StoredBlock getStoredBlockAtHeight(BlockStore blockStore, int height) throws BlockStoreException {
        StoredBlock storedBlock = blockStore.getChainHead();
        int headHeight = storedBlock.getHeight();
        if (height > headHeight) {
            return null;
        }
        for (int i = 0; i < (headHeight - height); i++) {
            if (storedBlock==null) {
                return null;
            }

            Sha256Hash prevBlockHash = storedBlock.getHeader().getPrevBlockHash();
            storedBlock = blockStore.get(prevBlockHash);
        }
        if (storedBlock!=null) {
            if (storedBlock.getHeight() != height) {
                throw new IllegalStateException("Block height is " + storedBlock.getHeight() + " but should be " + headHeight);
            }
            return storedBlock;
        } else {
            return null;
        }
    }

    public static boolean isLockTx(Transaction tx, Wallet wallet, AgentConstants agentConstants, OperatorPublicKeyHandler keyHandler) {
        // First, check tx is not a release tx.
        int i = 0;
        for (TransactionInput transactionInput : tx.getInputs()) {
            try {
                transactionInput.getScriptSig().correctlySpends(tx, i, keyHandler.getOutputScript(), Script.ALL_VERIFY_FLAGS);
                // There is an input spending from the operator address, this is not a lock tx
                return false;
            } catch (ScriptException se) {
                // do-nothing, input does not spends from the operator address
            }
            i++;
        }
        Coin valueSentToMe = tx.getValueSentToMe(wallet);

        int valueSentToMeSignum = valueSentToMe.signum();
        if (valueSentToMe.isLessThan(agentConstants.getMinimumLockTxValue())) {
            logger.warn("Someone sent to the operator less than {} satoshis", agentConstants.getMinimumLockTxValue());
        }
        return (valueSentToMeSignum > 0 && !valueSentToMe.isLessThan(agentConstants.getMinimumLockTxValue()));
    }

    public static boolean isReleaseTx(Transaction tx, Wallet wallet, OperatorPublicKeyHandler keyHandler) {
        int i = 0;
        for (TransactionInput transactionInput : tx.getInputs()) {
            try {
                transactionInput.getScriptSig().correctlySpends(tx, i, keyHandler.getOutputScript(), Script.ALL_VERIFY_FLAGS);
                // There is an input spending from the operator address, this is a release tx
                Coin valueSentToMe = tx.getValueSentToMe(wallet);
                int valueSentToMeSignum = valueSentToMe.signum();
                if (valueSentToMeSignum <= 0) {
                    // there is no change to be submitted to the contract
                    continue;
                }
                return true;
            } catch (ScriptException se) {
                // do-nothing, input does not spends from the operator address
            }
            i++;
        }
        return false;
    }



}
