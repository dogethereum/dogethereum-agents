package org.dogethereum.dogesubmitter;

import org.bitcoinj.wallet.Wallet;
import org.dogethereum.dogesubmitter.constants.BridgeConstants;
import org.bitcoinj.core.*;
import org.bitcoinj.script.Script;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.BlockStoreException;
import org.dogethereum.dogesubmitter.util.OperatorKeyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BridgeUtils {

    private static final Logger logger = LoggerFactory.getLogger("BridgeUtils");

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

    public static boolean isLockTx(Transaction tx, Wallet wallet, BridgeConstants bridgeConstants, OperatorKeyHandler keyHandler) {
        // First, check tx is not a typical release tx (tx spending from the federation address and
        // optionally sending some change to the federation address)
        int i = 0;
        for (TransactionInput transactionInput : tx.getInputs()) {
            try {
                transactionInput.getScriptSig().correctlySpends(tx, i, keyHandler.getOutputScript(), Script.ALL_VERIFY_FLAGS);
                // There is an input spending from the federation address, this is not a lock tx
                return false;
            } catch (ScriptException se) {
                // do-nothing, input does not spends from the federation address
            }
            i++;
        }
        Coin valueSentToMe = tx.getValueSentToMe(wallet);

        int valueSentToMeSignum = valueSentToMe.signum();
        if (valueSentToMe.isLessThan(bridgeConstants.getMinimumLockTxValue())) {
            logger.warn("Someone sent to the federation less than {} satoshis", bridgeConstants.getMinimumLockTxValue());
        }
        return (valueSentToMeSignum > 0 && !valueSentToMe.isLessThan(bridgeConstants.getMinimumLockTxValue()));
    }

    public static boolean isReleaseTx(Transaction tx, BridgeConstants bridgeConstants, OperatorKeyHandler keyHandler) {
        int i = 0;
        for (TransactionInput transactionInput : tx.getInputs()) {
            try {
                transactionInput.getScriptSig().correctlySpends(tx, i, keyHandler.getOutputScript(), Script.ALL_VERIFY_FLAGS);
                // There is an input spending from the federation address, this is a release tx
                return true;
            } catch (ScriptException se) {
                // do-nothing, input does not spends from the federation address
            }
            i++;
        }
        return false;
    }



}
