package org.dogethereum.dogesubmitter.core;


import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.*;
import org.dogethereum.dogesubmitter.constants.SystemProperties;
import org.dogethereum.dogesubmitter.contract.DogeRelay;
import org.dogethereum.dogesubmitter.contract.DogeToken;
import org.dogethereum.dogesubmitter.contract.DogeTokenExtended;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.libdohj.core.ScryptHash;
import org.spongycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tuples.generated.Tuple3;
import org.web3j.tuples.generated.Tuple6;
import org.web3j.tx.ClientTransactionManager;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Helps the agent communication with the Eth blockchain.
 * @author Oscar Guindzberg
 */
@Component
@Slf4j(topic = "AgentSupport")
public class AgentSupport {

    private Web3j web3;
    private DogeRelay dogeRelay;
    private DogeRelay dogeRelayForRelayTx;
    private DogeTokenExtended dogeToken;
    private SystemProperties config;
    private BigInteger gasPriceMinimum;

    @Autowired
    public AgentSupport() throws Exception {
        config = SystemProperties.CONFIG;
        web3 = Web3j.build(new HttpService());  // defaults to http://localhost:8545/
        String dogeRelayContractAddress;
        String dogeTokenContractAddress;
        String fromAddressGeneralPurposeAndSendBlocks;
        String fromAddressRelayTxs;
        String fromAddressPriceOracle;
        if (config.isRegtest()) {
            dogeRelayContractAddress = getContractAddress("DogeRelay");
            dogeTokenContractAddress = getContractAddress("DogeToken");
            List<String> accounts = web3.ethAccounts().send().getAccounts();
            fromAddressGeneralPurposeAndSendBlocks = accounts.get(0);
            fromAddressRelayTxs = accounts.get(1);
            fromAddressPriceOracle = accounts.get(2);
        } else {
            dogeRelayContractAddress = config.dogeRelayContractAddress();
            dogeTokenContractAddress = config.dogeTokenContractAddress();
            fromAddressGeneralPurposeAndSendBlocks = config.addressGeneralPurposeAndSendBlocks();
            fromAddressRelayTxs = config.addressRelayTxs();
            fromAddressPriceOracle = config.addressPriceOracle();
        }
        gasPriceMinimum = BigInteger.valueOf(config.gasPriceMinimum());
        BigInteger gasLimit = BigInteger.valueOf(config.gasLimit());
        dogeRelay = DogeRelay.load(dogeRelayContractAddress, web3, new ClientTransactionManager(web3, fromAddressGeneralPurposeAndSendBlocks), gasPriceMinimum, gasLimit);
        assert dogeRelay.isValid();
        dogeRelayForRelayTx = DogeRelay.load(dogeRelayContractAddress, web3, new ClientTransactionManager(web3, fromAddressRelayTxs), gasPriceMinimum, gasLimit);
        assert dogeRelayForRelayTx.isValid();
        dogeToken = DogeTokenExtended.load(dogeTokenContractAddress, web3, new ClientTransactionManager(web3, fromAddressPriceOracle), gasPriceMinimum, gasLimit);
        assert dogeToken.isValid();
    }

    /**
     * Get the deployed contract address from a truffle json file
     * @param contractName
     * @return The contract address
     * @throws Exception
     */
    private String getContractAddress(String contractName) throws Exception {
        String basePath = config.truffleBuildContractsDirectory();
        FileReader dogeRelaySpecFile = new FileReader(basePath + "/" + contractName + ".json");
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(dogeRelaySpecFile);
        JSONObject jsonObject =  (JSONObject) obj;
        JSONObject jsonObject2 =  (JSONObject) jsonObject.get("networks");
        JSONObject jsonObject3 =  (JSONObject) jsonObject2.values().iterator().next();
        String value4 = (String) jsonObject3.get("address");
        return value4;
    }

    public int getDogeBestBlockHeight() throws Exception {
        return dogeRelay.getBestBlockHeight().send().intValue();
    }

    public String getDogeBestBlockHash() throws Exception {
        BigInteger result = dogeRelay.getBestBlockHash().send();
        return hashBigIntegerToString(result);
    }

    private String hashBigIntegerToString(BigInteger input) {
        String input2 = input.toString(16);
        StringBuilder output = new StringBuilder(input2);
        while (output.length()<64) {
            output.insert(0,"0");
        }
        return output.toString();
    }


    public List<String> getDogeBlockchainBlockLocator() throws Exception {
        List<BigInteger> result = dogeRelay.getBlockLocator().send();
        List<String> formattedResult = new ArrayList<>();
        for (BigInteger biHash : result) {
            formattedResult.add(hashBigIntegerToString(biHash));
        }
        return formattedResult;
    }

    public void sendStoreHeaders(org.bitcoinj.core.Block headers[]) throws Exception {
        log.info("About to send to the bridge headers from {} to {}", headers[0].getHash(), headers[headers.length - 1].getHash());
//  Commented out solution that used bulkStoreHeaders
//        ByteArrayOutputStream baosHeaders = new ByteArrayOutputStream();
//        ByteArrayOutputStream baosHashes = new ByteArrayOutputStream();
//        for (int i = 0; i < headers.length; i++) {
//            byte[] serializedHeader = headers[i].bitcoinSerialize();
//            byte[] headerSize = calculateHeaderSize(serializedHeader);
//            baosHeaders.write(headerSize);
//            baosHeaders.write(serializedHeader);
//            NetworkParameters params = config.getAgentConstants().getDogeParams();
//            AltcoinBlock block = new AltcoinBlock(params, serializedHeader);
//            if (block.getAuxPoW() == null) {
//                baosHashes.write(block.getScryptHash().getBytes());
//            } else {
//                baosHashes.write(block.getAuxPoW().getParentBlockHeader().getScryptHash().getBytes());
//            }
//        }
//
//        CompletableFuture<TransactionReceipt> futureReceipt = dogeRelay.bulkStoreHeaders(baosHeaders.toByteArray(), baosHashes.toByteArray(), BigInteger.valueOf(headers.length)).sendAsync();
//        futureReceipt.thenAcceptAsync( (TransactionReceipt receipt) -> log.info("BulkStoreHeaders receipt {}.", toString(receipt)) );
        for (Block header : headers) {
            AltcoinBlock dogeHeader = (AltcoinBlock) header;
            ScryptHash scryptHash;
            if (dogeHeader.getAuxPoW() == null) {
                scryptHash = dogeHeader.getScryptHash();
            } else {
                scryptHash = dogeHeader.getAuxPoW().getParentBlockHeader().getScryptHash();
            }
            BigInteger scryptHashBI = ScryptHash.wrapReversed(scryptHash.getBytes()).toBigInteger();
            CompletableFuture<TransactionReceipt> futureReceipt = dogeRelay.storeBlockHeader(dogeHeader.bitcoinSerialize(), scryptHashBI).sendAsync();
            log.info("Sent storeBlockHeader {}", dogeHeader.getHash());
            futureReceipt.thenAcceptAsync( (TransactionReceipt receipt) ->
                    log.info("StoreBlockHeader receipt {}.", receipt.toString())
            );
            // Sleep a couple of millis. Before this "hack", when using ganache I used to get some tx receipt with "no previous block" error
            Thread.sleep(200);
        }
    }

    // Return the size of the header as a 4-byte byte[]
    private byte[] calculateHeaderSize (byte[] header) {
        String size = BigInteger.valueOf(header.length).toString(16);
        while (size.length() < 8) {
            size = "0" + size;
        }
        return Hex.decode(size);
    }

    public boolean wasLockTxProcessed(Sha256Hash txHash) throws Exception {
        return dogeToken.wasDogeTxProcessed(txHash.toBigInteger()).send();

    }

    public void sendRelayTx(org.bitcoinj.core.Transaction tx, Sha256Hash blockHash, PartialMerkleTree pmt) throws Exception {
        log.info("About to send to the bridge doge tx hash {}. Block hash {}", tx.getHash(), blockHash);

        byte[] txSerialized = tx.bitcoinSerialize();

        BigInteger txIndex = BigInteger.valueOf(pmt.getTransactionIndex(tx.getHash()));
        List<Sha256Hash> siblingsSha256Hash = pmt.getTransactionPath(tx.getHash());
        List<BigInteger> siblingsBigInteger = new ArrayList<>();
        for (Sha256Hash sha256Hash : siblingsSha256Hash) {
            siblingsBigInteger.add(sha256Hash.toBigInteger());
        }
        BigInteger blockHashBigInteger = blockHash.toBigInteger();
        String targetContract = dogeToken.getContractAddress();
        CompletableFuture<TransactionReceipt> futureReceipt = dogeRelayForRelayTx.relayTx(txSerialized, txIndex, siblingsBigInteger, blockHashBigInteger, targetContract).sendAsync();
        log.info("Sent relayTx {}", tx.getHash());
        futureReceipt.thenAcceptAsync( (TransactionReceipt receipt) ->
                log.info("RelayTx receipt {}.", receipt.toString())
        );
    }

    public boolean isEthNodeSyncing() throws IOException {
        return web3.ethSyncing().send().isSyncing();
    }

    public void updateContractFacadesGasPrice() throws IOException {
        BigInteger gasPriceSuggestedByEthNode = web3.ethGasPrice().send().getGasPrice();
        BigInteger gasPrice;
        if (gasPriceSuggestedByEthNode.compareTo(gasPriceMinimum) > 0) {
            gasPrice = gasPriceSuggestedByEthNode;
        } else {
            gasPrice = gasPriceMinimum;
        }
        dogeRelay.setGasPrice(gasPrice);
        dogeToken.setGasPrice(gasPrice);
    }



//    Used just for release process

    public void updatePrice(long price) {
        BigInteger priceBI = BigInteger.valueOf(price);
        CompletableFuture<TransactionReceipt> futureReceipt = dogeToken.setDogeEthPrice(priceBI).sendAsync();
        log.info("Sent update doge-eth price tx. Price: {}", price);
        futureReceipt.thenAcceptAsync( (TransactionReceipt receipt) ->
                log.info("Update doge-eth price tx receipt {}.", receipt.toString())
        );
    }

    public long getEthBlockCount() throws IOException {
        return web3.ethBlockNumber().send().getBlockNumber().longValue();
    }

    public List<Long> getNewUnlockRequestIds(long latestEthBlockProcessed, long topBlock) throws ExecutionException, InterruptedException, IOException {
        List<Long> result = new ArrayList<>();
        List<DogeToken.UnlockRequestEventResponse> unlockRequestEvents = dogeToken.getUnlockRequestEvents(DefaultBlockParameter.valueOf(BigInteger.valueOf(latestEthBlockProcessed)), DefaultBlockParameter.valueOf(BigInteger.valueOf(topBlock)));
        for (DogeToken.UnlockRequestEventResponse unlockRequestEvent : unlockRequestEvents) {
            result.add(unlockRequestEvent.id.longValue());
        }
        return result;
    }

    public Unlock getUnlock(Long unlockRequestId) throws Exception {
        Tuple6<String, String, BigInteger, BigInteger, List<BigInteger>, BigInteger> tuple =
                dogeToken.getUnlockPendingInvestorProof(BigInteger.valueOf(unlockRequestId)).send();
        Unlock unlock = new Unlock();
        unlock.from = tuple.getValue1();
        unlock.dogeAddress = tuple.getValue2();
        unlock.value = tuple.getValue3().longValue();
        unlock.timestamp =  tuple.getValue4().longValue();
        unlock.fee = tuple.getValue6().longValue();

        List<BigInteger> selectedUtxosIndexes = tuple.getValue5();
        List<UTXO> selectedUtxosOutpoints = new ArrayList<>();
        for (BigInteger selectedUtxo : selectedUtxosIndexes) {
            Tuple3<BigInteger, BigInteger, BigInteger> utxo = dogeToken.utxos(selectedUtxo).send();
            long value = utxo.getValue1().longValue();
            Sha256Hash txHash = Sha256Hash.wrap(hashBigIntegerToString(utxo.getValue2()));
            long outputIndex = utxo.getValue3().longValue();
            selectedUtxosOutpoints.add(new UTXO(txHash, outputIndex, Coin.valueOf(value), 0 ,false, null));
        }
        unlock.selectedUtxos = selectedUtxosOutpoints;
        return unlock;
    }

    public static class Unlock {
        public String from;
        public String dogeAddress;
        public long value;
        public long timestamp;
        public List<UTXO> selectedUtxos;
        public long fee;
    }

}
