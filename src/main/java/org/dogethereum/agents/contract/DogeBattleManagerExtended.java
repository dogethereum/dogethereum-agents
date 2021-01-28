package org.dogethereum.agents.contract;

import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
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

public class DogeBattleManagerExtended extends  DogeBattleManager {
    protected DogeBattleManagerExtended(String contractAddress, Web3j web3j, TransactionManager transactionManager,
                                BigInteger gasPrice, BigInteger gasLimit) {
        super(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }
    public static DogeBattleManagerExtended load(String contractAddress, Web3j web3j,
                                                 TransactionManager transactionManager, BigInteger gasPrice,
                                                 BigInteger gasLimit) {
        return new DogeBattleManagerExtended(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public List<QueryBlockHeaderEventResponse> getQueryBlockHeaderEventResponses(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock)
            throws IOException {
        final Event event = QUERYBLOCKHEADER_EVENT;

        List<QueryBlockHeaderEventResponse> result = new ArrayList<>();
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        EthLog ethLog = web3j.ethGetLogs(filter).send();
        List<EthLog.LogResult> logResults = ethLog.getLogs();

        for (EthLog.LogResult logResult : logResults) {
            Log log = (Log) logResult.get();
            EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);

            QueryBlockHeaderEventResponse queryBlockHeaderEventResponse = new QueryBlockHeaderEventResponse();
            queryBlockHeaderEventResponse.log = eventValues.getLog();
            queryBlockHeaderEventResponse.superblockHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            queryBlockHeaderEventResponse.sessionId = (byte[]) eventValues.getNonIndexedValues().get(1).getValue();
            queryBlockHeaderEventResponse.submitter = (String) eventValues.getNonIndexedValues().get(2).getValue();
            queryBlockHeaderEventResponse.blockSha256Hash = (byte[]) eventValues.getNonIndexedValues().get(3).getValue();
            result.add(queryBlockHeaderEventResponse);
        }

        return result;
    }

    public List<QueryMerkleRootHashesEventResponse> getQueryMerkleRootHashesEventResponses(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock)
            throws IOException {
        final Event event = QUERYMERKLEROOTHASHES_EVENT;

        List<QueryMerkleRootHashesEventResponse> result = new ArrayList<>();
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        EthLog ethLog = web3j.ethGetLogs(filter).send();
        List<EthLog.LogResult> logResults = ethLog.getLogs();

        for (EthLog.LogResult logResult : logResults) {
            Log log = (Log) logResult.get();
            EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);

            QueryMerkleRootHashesEventResponse queryMerkleRootHashesEventResponse =
                    new QueryMerkleRootHashesEventResponse();
            queryMerkleRootHashesEventResponse.log = eventValues.getLog();
            queryMerkleRootHashesEventResponse.superblockHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            queryMerkleRootHashesEventResponse.sessionId = (byte[]) eventValues.getNonIndexedValues().get(1).getValue();
            queryMerkleRootHashesEventResponse.submitter = (String) eventValues.getNonIndexedValues().get(2).getValue();
            result.add(queryMerkleRootHashesEventResponse);
        }

        return result;
    }

    public List<NewBattleEventResponse> getNewBattleEventResponses(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock)
            throws IOException {
        final Event event = NEWBATTLE_EVENT;

        List<NewBattleEventResponse> result = new ArrayList<>();
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        EthLog ethLog = web3j.ethGetLogs(filter).send();
        List<EthLog.LogResult> logResults = ethLog.getLogs();

        for (EthLog.LogResult logResult : logResults) {
            Log log = (Log) logResult.get();
            EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);

            NewBattleEventResponse newBattleEventResponse =
                    new NewBattleEventResponse();
            newBattleEventResponse.log = eventValues.getLog();
            newBattleEventResponse.superblockHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            newBattleEventResponse.sessionId = (byte[]) eventValues.getNonIndexedValues().get(1).getValue();
            newBattleEventResponse.submitter = (String) eventValues.getNonIndexedValues().get(2).getValue();
            newBattleEventResponse.challenger = (String) eventValues.getNonIndexedValues().get(3).getValue();
            result.add(newBattleEventResponse);
        }

        return result;
    }

    public List<ChallengerConvictedEventResponse> getChallengerConvictedEventResponses(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock)
            throws IOException {
        final Event event = CHALLENGERCONVICTED_EVENT;

        List<ChallengerConvictedEventResponse> result = new ArrayList<>();
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        EthLog ethLog = web3j.ethGetLogs(filter).send();
        List<EthLog.LogResult> logResults = ethLog.getLogs();

        for (EthLog.LogResult logResult : logResults) {
            Log log = (Log) logResult.get();
            EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);

            ChallengerConvictedEventResponse newChallengerConvictedEventResponse =
                    new ChallengerConvictedEventResponse();
            newChallengerConvictedEventResponse.log = eventValues.getLog();
            newChallengerConvictedEventResponse.superblockHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            newChallengerConvictedEventResponse.sessionId = (byte[]) eventValues.getNonIndexedValues().get(1).getValue();
            newChallengerConvictedEventResponse.challenger = (String) eventValues.getNonIndexedValues().get(2).getValue();
            result.add(newChallengerConvictedEventResponse);
        }

        return result;
    }

    public List<SubmitterConvictedEventResponse> getSubmitterConvictedEventResponses(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock)
            throws IOException {
        final Event event = SUBMITTERCONVICTED_EVENT;

        List<SubmitterConvictedEventResponse> result = new ArrayList<>();
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        EthLog ethLog = web3j.ethGetLogs(filter).send();
        List<EthLog.LogResult> logResults = ethLog.getLogs();

        for (EthLog.LogResult logResult : logResults) {
            Log log = (Log) logResult.get();
            EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);

            SubmitterConvictedEventResponse newSubmitterConvictedEventResponse =
                    new SubmitterConvictedEventResponse();
            newSubmitterConvictedEventResponse.log = eventValues.getLog();
            newSubmitterConvictedEventResponse.superblockHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            newSubmitterConvictedEventResponse.sessionId = (byte[]) eventValues.getNonIndexedValues().get(1).getValue();
            newSubmitterConvictedEventResponse.submitter = (String) eventValues.getNonIndexedValues().get(2).getValue();
            result.add(newSubmitterConvictedEventResponse);
        }

        return result;
    }

    public List<RespondMerkleRootHashesEventResponse> getRespondMerkleRootHashesEventResponses(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock)
            throws IOException {
        final Event event = RESPONDMERKLEROOTHASHES_EVENT;

        List<RespondMerkleRootHashesEventResponse> result = new ArrayList<>();
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        EthLog ethLog = web3j.ethGetLogs(filter).send();
        List<EthLog.LogResult> logResults = ethLog.getLogs();

        for (EthLog.LogResult logResult : logResults) {
            Log log = (Log) logResult.get();
            EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);

            RespondMerkleRootHashesEventResponse newRespondMerkleRootHashesEventResponse =
                    new RespondMerkleRootHashesEventResponse();
            newRespondMerkleRootHashesEventResponse.log = eventValues.getLog();
            newRespondMerkleRootHashesEventResponse.superblockHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            newRespondMerkleRootHashesEventResponse.sessionId = (byte[]) eventValues.getNonIndexedValues().get(1).getValue();
            newRespondMerkleRootHashesEventResponse.challenger = (String) eventValues.getNonIndexedValues().get(2).getValue();
            newRespondMerkleRootHashesEventResponse.blockHashes = new ArrayList<>();
            List<Bytes32> rawBytes32Hashes = (List<Bytes32>) eventValues.getNonIndexedValues().get(3).getValue();
            for (Bytes32 rawBytes32Hash : rawBytes32Hashes) {
                newRespondMerkleRootHashesEventResponse.blockHashes.add(rawBytes32Hash.getValue());
            }
            result.add(newRespondMerkleRootHashesEventResponse);
        }

        return result;
    }

    public List<RespondBlockHeaderEventResponse> getRespondBlockHeaderEventResponses(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock)
            throws IOException {
        final Event event = RESPONDBLOCKHEADER_EVENT;

        List<RespondBlockHeaderEventResponse> result = new ArrayList<>();
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        EthLog ethLog = web3j.ethGetLogs(filter).send();
        List<EthLog.LogResult> logResults = ethLog.getLogs();

        for (EthLog.LogResult logResult : logResults) {
            Log log = (Log) logResult.get();
            EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);

            RespondBlockHeaderEventResponse newRespondBlockHeaderEventResponse =
                    new RespondBlockHeaderEventResponse();
            newRespondBlockHeaderEventResponse.log = eventValues.getLog();
            newRespondBlockHeaderEventResponse.superblockHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            newRespondBlockHeaderEventResponse.sessionId = (byte[]) eventValues.getNonIndexedValues().get(1).getValue();
            newRespondBlockHeaderEventResponse.challenger = (String) eventValues.getNonIndexedValues().get(2).getValue();
            newRespondBlockHeaderEventResponse.blockScryptHash = (byte[]) eventValues.getNonIndexedValues().get(3).getValue();
            newRespondBlockHeaderEventResponse.blockHeader = (byte[]) eventValues.getNonIndexedValues().get(4).getValue();
            newRespondBlockHeaderEventResponse.powBlockHeader = (byte[]) eventValues.getNonIndexedValues().get(5).getValue();
            result.add(newRespondBlockHeaderEventResponse);
        }

        return result;
    }

    public List<ErrorBattleEventResponse> getErrorBattleEventResponses(DefaultBlockParameter startBlock,
                                                                       DefaultBlockParameter endBlock)
            throws IOException {
        final Event event = ERRORBATTLE_EVENT;

        List<ErrorBattleEventResponse> result = new ArrayList<>();
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        EthLog ethLog = web3j.ethGetLogs(filter).send();
        List<EthLog.LogResult> logResults = ethLog.getLogs();

        for (EthLog.LogResult logResult : logResults) {
            Log log = (Log) logResult.get();
            Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);

            ErrorBattleEventResponse newErrorBattleEventResponse =
                    new ErrorBattleEventResponse();
            newErrorBattleEventResponse.log = eventValues.getLog();
            newErrorBattleEventResponse.sessionId = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            newErrorBattleEventResponse.err = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            result.add(newErrorBattleEventResponse);
        }

        return result;
    }

    public List<RequestScryptHashValidationEventResponse> getRequestScryptHashValidationEventResponses(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock)
            throws IOException {
        final Event event = REQUESTSCRYPTHASHVALIDATION_EVENT;

        List<RequestScryptHashValidationEventResponse> result = new ArrayList<>();
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        EthLog ethLog = web3j.ethGetLogs(filter).send();
        List<EthLog.LogResult> logResults = ethLog.getLogs();

        for (EthLog.LogResult logResult : logResults) {
            Log log = (Log) logResult.get();
            EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);

            RequestScryptHashValidationEventResponse newRequestScryptHashValidationEventResponse =
                    new RequestScryptHashValidationEventResponse();
            newRequestScryptHashValidationEventResponse.log = eventValues.getLog();
            newRequestScryptHashValidationEventResponse.superblockHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            newRequestScryptHashValidationEventResponse.sessionId = (byte[]) eventValues.getNonIndexedValues().get(1).getValue();
            newRequestScryptHashValidationEventResponse.blockScryptHash = (byte[]) eventValues.getNonIndexedValues().get(2).getValue();
            newRequestScryptHashValidationEventResponse.blockHeader = (byte[]) eventValues.getNonIndexedValues().get(3).getValue();
            newRequestScryptHashValidationEventResponse.proposalId = (byte[]) eventValues.getNonIndexedValues().get(4).getValue();
            newRequestScryptHashValidationEventResponse.submitter = (String) eventValues.getNonIndexedValues().get(5).getValue();
            result.add(newRequestScryptHashValidationEventResponse);
        }

        return result;
    }

    public List<ResolvedScryptHashValidationEventResponse> getResolvedScryptHashValidationEventResponses(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock)
            throws IOException {
        final Event event = RESOLVEDSCRYPTHASHVALIDATION_EVENT;

        List<ResolvedScryptHashValidationEventResponse> result = new ArrayList<>();
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        EthLog ethLog = web3j.ethGetLogs(filter).send();
        List<EthLog.LogResult> logResults = ethLog.getLogs();

        for (EthLog.LogResult logResult : logResults) {
            Log log = (Log) logResult.get();
            EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);

            ResolvedScryptHashValidationEventResponse newResolvedScryptHashValidationEventResponse =
                    new ResolvedScryptHashValidationEventResponse();
            newResolvedScryptHashValidationEventResponse.log = eventValues.getLog();
            newResolvedScryptHashValidationEventResponse.superblockHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            newResolvedScryptHashValidationEventResponse.sessionId = (byte[]) eventValues.getNonIndexedValues().get(1).getValue();
            newResolvedScryptHashValidationEventResponse.blockScryptHash = (byte[]) eventValues.getNonIndexedValues().get(2).getValue();
            newResolvedScryptHashValidationEventResponse.blockSha256Hash = (byte[]) eventValues.getNonIndexedValues().get(3).getValue();
            newResolvedScryptHashValidationEventResponse.proposalId = (byte[]) eventValues.getNonIndexedValues().get(4).getValue();
            newResolvedScryptHashValidationEventResponse.challenger = (String) eventValues.getNonIndexedValues().get(5).getValue();
            newResolvedScryptHashValidationEventResponse.valid = (Boolean) eventValues.getNonIndexedValues().get(6).getValue();
            result.add(newResolvedScryptHashValidationEventResponse);
        }

        return result;
    }
}
