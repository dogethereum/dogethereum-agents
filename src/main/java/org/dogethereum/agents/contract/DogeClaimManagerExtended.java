package org.dogethereum.agents.contract;

import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthLog;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DogeClaimManagerExtended extends DogeClaimManager {
    protected DogeClaimManagerExtended(String contractAddress, Web3j web3j, TransactionManager transactionManager,
                                        BigInteger gasPrice, BigInteger gasLimit) {
        super(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static DogeClaimManagerExtended load(String contractAddress, Web3j web3j,
                                                 TransactionManager transactionManager, BigInteger gasPrice,
                                                 BigInteger gasLimit) {
        return new DogeClaimManagerExtended(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public List<QueryBlockHeaderEventResponse> getQueryBlockHeaderEventResponses(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock)
            throws IOException {
        final Event event = new Event("QueryBlockHeader",
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}));

        List<QueryBlockHeaderEventResponse> result = new ArrayList<>();
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        EthLog ethLog = web3j.ethGetLogs(filter).send();
        List<EthLog.LogResult> logResults = ethLog.getLogs();

        for (EthLog.LogResult logResult : logResults) {
            Log log = (Log) logResult.get();
            Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);

            QueryBlockHeaderEventResponse queryBlockHeaderEventResponse = new QueryBlockHeaderEventResponse();
            queryBlockHeaderEventResponse.log = eventValues.getLog();
            queryBlockHeaderEventResponse.sessionId = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            queryBlockHeaderEventResponse.claimant = (String) eventValues.getNonIndexedValues().get(1).getValue();
            result.add(queryBlockHeaderEventResponse);
        }

        return result;
    }

    public List<QueryMerkleRootHashesEventResponse> getQueryMerkleRootHashesEventResponses(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock)
            throws IOException {
        final Event event = new Event("QueryMerkleRootHashes",
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}));

        List<QueryMerkleRootHashesEventResponse> result = new ArrayList<>();
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        EthLog ethLog = web3j.ethGetLogs(filter).send();
        List<EthLog.LogResult> logResults = ethLog.getLogs();

        for (EthLog.LogResult logResult : logResults) {
            Log log = (Log) logResult.get();
            Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);

            QueryMerkleRootHashesEventResponse queryMerkleRootHashesEventResponse =
                    new QueryMerkleRootHashesEventResponse();
            queryMerkleRootHashesEventResponse.log = eventValues.getLog();
            queryMerkleRootHashesEventResponse.sessionId = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            queryMerkleRootHashesEventResponse.claimant = (String) eventValues.getNonIndexedValues().get(1).getValue();
            result.add(queryMerkleRootHashesEventResponse);
        }

        return result;
    }
}
