package org.sysethereum.agents.contract;

import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.DynamicBytes;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthLog;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.tx.TransactionManager;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SyscoinBattleManagerExtended extends  SyscoinBattleManager {
    protected SyscoinBattleManagerExtended(String contractAddress, Web3j web3j, TransactionManager transactionManager,
                                           BigInteger gasPrice, BigInteger gasLimit) {
        super(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }
    public static SyscoinBattleManagerExtended load(String contractAddress, Web3j web3j,
                                                    TransactionManager transactionManager, BigInteger gasPrice,
                                                    BigInteger gasLimit) {
        return new SyscoinBattleManagerExtended(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }
    public static String getAddress(String networkId) {
        return getPreviouslyDeployedAddress(networkId);
    }
    public List<QueryBlockHeaderEventResponse> getQueryBlockHeaderEventResponses(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock)
            throws IOException {
        List<QueryBlockHeaderEventResponse> result = new ArrayList<>();
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(QUERYBLOCKHEADER_EVENT));
        EthLog ethLog = web3j.ethGetLogs(filter).send();
        List<EthLog.LogResult> logResults = ethLog.getLogs();

        for (EthLog.LogResult logResult : logResults) {
            Log log = (Log) logResult.get();
            EventValuesWithLog eventValues = extractEventParametersWithLog(QUERYBLOCKHEADER_EVENT, log);

            QueryBlockHeaderEventResponse queryBlockHeaderEventResponse = new QueryBlockHeaderEventResponse();
            queryBlockHeaderEventResponse.log = eventValues.getLog();
            queryBlockHeaderEventResponse.superblockHash = new Bytes32((byte[]) eventValues.getNonIndexedValues().get(0).getValue());
            queryBlockHeaderEventResponse.sessionId = new Bytes32((byte[]) eventValues.getNonIndexedValues().get(1).getValue());
            queryBlockHeaderEventResponse.submitter = new Address((String)eventValues.getNonIndexedValues().get(2).getValue());
            queryBlockHeaderEventResponse.blockSha256Hash = new Bytes32((byte[]) eventValues.getNonIndexedValues().get(3).getValue());
            result.add(queryBlockHeaderEventResponse);
        }

        return result;
    }

    public List<QueryMerkleRootHashesEventResponse> getQueryMerkleRootHashesEventResponses(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock)
            throws IOException {
        List<QueryMerkleRootHashesEventResponse> result = new ArrayList<>();
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(QUERYMERKLEROOTHASHES_EVENT));
        EthLog ethLog = web3j.ethGetLogs(filter).send();
        List<EthLog.LogResult> logResults = ethLog.getLogs();

        for (EthLog.LogResult logResult : logResults) {
            Log log = (Log) logResult.get();
            EventValuesWithLog eventValues = extractEventParametersWithLog(QUERYMERKLEROOTHASHES_EVENT, log);

            QueryMerkleRootHashesEventResponse queryMerkleRootHashesEventResponse =
                    new QueryMerkleRootHashesEventResponse();
            queryMerkleRootHashesEventResponse.log = eventValues.getLog();
            queryMerkleRootHashesEventResponse.superblockHash = new Bytes32((byte[]) eventValues.getNonIndexedValues().get(0).getValue());
            queryMerkleRootHashesEventResponse.sessionId = new Bytes32((byte[]) eventValues.getNonIndexedValues().get(1).getValue());
            queryMerkleRootHashesEventResponse.submitter = new Address ((String)eventValues.getNonIndexedValues().get(2).getValue());
            result.add(queryMerkleRootHashesEventResponse);
        }

        return result;
    }

    public List<NewBattleEventResponse> getNewBattleEventResponses(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock)
            throws IOException {
        List<NewBattleEventResponse> result = new ArrayList<>();
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(NEWBATTLE_EVENT));
        EthLog ethLog = web3j.ethGetLogs(filter).send();
        List<EthLog.LogResult> logResults = ethLog.getLogs();

        for (EthLog.LogResult logResult : logResults) {
            Log log = (Log) logResult.get();
            EventValuesWithLog eventValues = extractEventParametersWithLog(NEWBATTLE_EVENT, log);

            NewBattleEventResponse newBattleEventResponse =
                    new NewBattleEventResponse();
            newBattleEventResponse.log = eventValues.getLog();
            newBattleEventResponse.superblockHash = new Bytes32((byte[]) eventValues.getNonIndexedValues().get(0).getValue());
            newBattleEventResponse.sessionId = new Bytes32((byte[]) eventValues.getNonIndexedValues().get(1).getValue());
            newBattleEventResponse.submitter = new Address ((String)eventValues.getNonIndexedValues().get(2).getValue());
            newBattleEventResponse.challenger = new Address ((String)eventValues.getNonIndexedValues().get(3).getValue());
            result.add(newBattleEventResponse);
        }

        return result;
    }

    public List<ChallengerConvictedEventResponse> getChallengerConvictedEventResponses(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock)
            throws IOException {
        List<ChallengerConvictedEventResponse> result = new ArrayList<>();
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(CHALLENGERCONVICTED_EVENT));
        EthLog ethLog = web3j.ethGetLogs(filter).send();
        List<EthLog.LogResult> logResults = ethLog.getLogs();

        for (EthLog.LogResult logResult : logResults) {
            Log log = (Log) logResult.get();
            EventValuesWithLog eventValues = extractEventParametersWithLog(CHALLENGERCONVICTED_EVENT, log);

            ChallengerConvictedEventResponse newChallengerConvictedEventResponse =
                    new ChallengerConvictedEventResponse();
            newChallengerConvictedEventResponse.log = eventValues.getLog();
            newChallengerConvictedEventResponse.superblockHash = new Bytes32((byte[]) eventValues.getNonIndexedValues().get(0).getValue());
            newChallengerConvictedEventResponse.sessionId = new Bytes32((byte[]) eventValues.getNonIndexedValues().get(1).getValue());
            newChallengerConvictedEventResponse.challenger = new Address ((String)eventValues.getNonIndexedValues().get(2).getValue());
            result.add(newChallengerConvictedEventResponse);
        }

        return result;
    }

    public List<SubmitterConvictedEventResponse> getSubmitterConvictedEventResponses(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock)
            throws IOException {
        List<SubmitterConvictedEventResponse> result = new ArrayList<>();
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(SUBMITTERCONVICTED_EVENT));
        EthLog ethLog = web3j.ethGetLogs(filter).send();
        List<EthLog.LogResult> logResults = ethLog.getLogs();

        for (EthLog.LogResult logResult : logResults) {
            Log log = (Log) logResult.get();
            EventValuesWithLog eventValues = extractEventParametersWithLog(SUBMITTERCONVICTED_EVENT, log);

            SubmitterConvictedEventResponse newSubmitterConvictedEventResponse =
                    new SubmitterConvictedEventResponse();
            newSubmitterConvictedEventResponse.log = eventValues.getLog();
            newSubmitterConvictedEventResponse.superblockHash = new Bytes32((byte[]) eventValues.getNonIndexedValues().get(0).getValue());
            newSubmitterConvictedEventResponse.sessionId = new Bytes32((byte[]) eventValues.getNonIndexedValues().get(1).getValue());
            newSubmitterConvictedEventResponse.submitter = new Address ((String)eventValues.getNonIndexedValues().get(2).getValue());
            result.add(newSubmitterConvictedEventResponse);
        }

        return result;
    }

    public List<RespondMerkleRootHashesEventResponse> getRespondMerkleRootHashesEventResponses(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock)
            throws IOException {

        List<RespondMerkleRootHashesEventResponse> result = new ArrayList<>();
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(RESPONDMERKLEROOTHASHES_EVENT));
        EthLog ethLog = web3j.ethGetLogs(filter).send();
        List<EthLog.LogResult> logResults = ethLog.getLogs();

        for (EthLog.LogResult logResult : logResults) {
            Log log = (Log) logResult.get();
            EventValuesWithLog eventValues = extractEventParametersWithLog(RESPONDMERKLEROOTHASHES_EVENT, log);

            RespondMerkleRootHashesEventResponse newRespondMerkleRootHashesEventResponse =
                    new RespondMerkleRootHashesEventResponse();
            newRespondMerkleRootHashesEventResponse.log = eventValues.getLog();
            newRespondMerkleRootHashesEventResponse.superblockHash = new Bytes32((byte[]) eventValues.getNonIndexedValues().get(0).getValue());
            newRespondMerkleRootHashesEventResponse.sessionId = new Bytes32((byte[]) eventValues.getNonIndexedValues().get(1).getValue());
            newRespondMerkleRootHashesEventResponse.challenger = new Address ((String)eventValues.getNonIndexedValues().get(2).getValue());
            newRespondMerkleRootHashesEventResponse.blockHashes = (DynamicArray<Bytes32>) eventValues.getNonIndexedValues().get(3);
            result.add(newRespondMerkleRootHashesEventResponse);
        }

        return result;
    }

    public List<RespondBlockHeaderEventResponse> getRespondBlockHeaderEventResponses(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock)
            throws IOException {
        List<RespondBlockHeaderEventResponse> result = new ArrayList<>();
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(RESPONDBLOCKHEADER_EVENT));
        EthLog ethLog = web3j.ethGetLogs(filter).send();
        List<EthLog.LogResult> logResults = ethLog.getLogs();

        for (EthLog.LogResult logResult : logResults) {
            Log log = (Log) logResult.get();
            EventValuesWithLog eventValues = extractEventParametersWithLog(RESPONDBLOCKHEADER_EVENT, log);

            RespondBlockHeaderEventResponse newRespondBlockHeaderEventResponse =
                    new RespondBlockHeaderEventResponse();
            newRespondBlockHeaderEventResponse.log = eventValues.getLog();
            newRespondBlockHeaderEventResponse.superblockHash = new Bytes32((byte[]) eventValues.getNonIndexedValues().get(0).getValue());
            newRespondBlockHeaderEventResponse.sessionId = new Bytes32((byte[]) eventValues.getNonIndexedValues().get(1).getValue());
            newRespondBlockHeaderEventResponse.challenger = new Address ((String)eventValues.getNonIndexedValues().get(2).getValue());
            newRespondBlockHeaderEventResponse.blockHeader = new DynamicBytes ((byte[])eventValues.getNonIndexedValues().get(3).getValue());
            newRespondBlockHeaderEventResponse.powBlockHeader = new DynamicBytes ((byte[])eventValues.getNonIndexedValues().get(4).getValue());
            result.add(newRespondBlockHeaderEventResponse);
        }

        return result;
    }

    public List<ErrorBattleEventResponse> getErrorBattleEventResponses(DefaultBlockParameter startBlock,
                                                                       DefaultBlockParameter endBlock)
            throws IOException {

        List<ErrorBattleEventResponse> result = new ArrayList<>();
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(ERRORBATTLE_EVENT));
        EthLog ethLog = web3j.ethGetLogs(filter).send();
        List<EthLog.LogResult> logResults = ethLog.getLogs();

        for (EthLog.LogResult logResult : logResults) {
            Log log = (Log) logResult.get();
            EventValuesWithLog eventValues = extractEventParametersWithLog(ERRORBATTLE_EVENT, log);

            ErrorBattleEventResponse newErrorBattleEventResponse =
                    new ErrorBattleEventResponse();
            newErrorBattleEventResponse.log = eventValues.getLog();
            newErrorBattleEventResponse.sessionId = new Bytes32((byte[]) eventValues.getNonIndexedValues().get(0).getValue());
            newErrorBattleEventResponse.err = new Uint256((BigInteger) eventValues.getNonIndexedValues().get(0).getValue());
            result.add(newErrorBattleEventResponse);
        }

        return result;
    }
}
