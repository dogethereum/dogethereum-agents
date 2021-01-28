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


/**
 * Extension of web3j auto-generated DogeSuperblocks class
 * with event polling methods for SuperblockDefenderClient.
 * @author Catalina Juarros
 */

public class DogeSuperblocksExtended extends DogeSuperblocks {
    protected DogeSuperblocksExtended(String contractAddress, Web3j web3j, TransactionManager transactionManager,
                                      BigInteger gasPrice, BigInteger gasLimit) {
        super(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static DogeSuperblocksExtended load(String contractAddress, Web3j web3j,
                                               TransactionManager transactionManager, BigInteger gasPrice,
                                               BigInteger gasLimit) {
        return new DogeSuperblocksExtended(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }


    /* ---- EVENTS FOR POLLING ---- */

    public List<NewSuperblockEventResponse> getNewSuperblockEvents(DefaultBlockParameter startBlock,
                                                                   DefaultBlockParameter endBlock) throws IOException {
        final Event event = NEWSUPERBLOCK_EVENT;

        List<NewSuperblockEventResponse> result = new ArrayList<>();
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        EthLog ethLog = web3j.ethGetLogs(filter).send();
        List<EthLog.LogResult> logResults = ethLog.getLogs();

        for (EthLog.LogResult logResult : logResults) {
            Log log = (Log) logResult.get();
            EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);

            NewSuperblockEventResponse newSuperblockResponse = new NewSuperblockEventResponse();
            newSuperblockResponse.log = eventValues.getLog();
            newSuperblockResponse.superblockHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            newSuperblockResponse.who = (String) eventValues.getNonIndexedValues().get(1).getValue();
            result.add(newSuperblockResponse);
        }

        return result;
    }

    public List<ApprovedSuperblockEventResponse> getApprovedSuperblockEvents(DefaultBlockParameter startBlock,
                                                                             DefaultBlockParameter endBlock)
            throws IOException {
        final Event event = APPROVEDSUPERBLOCK_EVENT;

        List<ApprovedSuperblockEventResponse> result = new ArrayList<>();
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        EthLog ethLog = web3j.ethGetLogs(filter).send();
        List<EthLog.LogResult> logResults = ethLog.getLogs();

        for (EthLog.LogResult logResult : logResults) {
            Log log = (Log) logResult.get();
            EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);

            ApprovedSuperblockEventResponse approvedSuperblockResponse = new ApprovedSuperblockEventResponse();
            approvedSuperblockResponse.log = eventValues.getLog();
            approvedSuperblockResponse.superblockHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            approvedSuperblockResponse.who = (String) eventValues.getNonIndexedValues().get(1).getValue();
            result.add(approvedSuperblockResponse);
        }

        return result;
    }

    public List<ChallengeSuperblockEventResponse> getChallengeSuperblockEvents(DefaultBlockParameter startBlock,
                                                                               DefaultBlockParameter endBlock)
            throws IOException {
        final Event event = CHALLENGESUPERBLOCK_EVENT;

        List<ChallengeSuperblockEventResponse> result = new ArrayList<>();
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        EthLog ethLog = web3j.ethGetLogs(filter).send();
        List<EthLog.LogResult> logResults = ethLog.getLogs();

        for (EthLog.LogResult logResult : logResults) {
            Log log = (Log) logResult.get();
            EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);

            ChallengeSuperblockEventResponse challengeSuperblockResponse = new ChallengeSuperblockEventResponse();
            challengeSuperblockResponse.log = eventValues.getLog();
            challengeSuperblockResponse.superblockHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            challengeSuperblockResponse.who = (String) eventValues.getNonIndexedValues().get(1).getValue();
            result.add(challengeSuperblockResponse);
        }

        return result;
    }

    public List<SemiApprovedSuperblockEventResponse> getSemiApprovedSuperblockEvents(DefaultBlockParameter startBlock,
                                                                               DefaultBlockParameter endBlock)
            throws IOException {
        final Event event = SEMIAPPROVEDSUPERBLOCK_EVENT;

        List<SemiApprovedSuperblockEventResponse> result = new ArrayList<>();
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        EthLog ethLog = web3j.ethGetLogs(filter).send();
        List<EthLog.LogResult> logResults = ethLog.getLogs();

        for (EthLog.LogResult logResult : logResults) {
            Log log = (Log) logResult.get();
            EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);

            SemiApprovedSuperblockEventResponse semiApprovedSuperblockResponse = new SemiApprovedSuperblockEventResponse();
            semiApprovedSuperblockResponse.log = eventValues.getLog();
            semiApprovedSuperblockResponse.superblockHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            semiApprovedSuperblockResponse.who = (String) eventValues.getNonIndexedValues().get(1).getValue();
            result.add(semiApprovedSuperblockResponse);
        }

        return result;
    }

    public List<InvalidSuperblockEventResponse> getInvalidSuperblockEvents(DefaultBlockParameter startBlock,
                                                                               DefaultBlockParameter endBlock)
            throws IOException {
        final Event event = INVALIDSUPERBLOCK_EVENT;

        List<InvalidSuperblockEventResponse> result = new ArrayList<>();
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        EthLog ethLog = web3j.ethGetLogs(filter).send();
        List<EthLog.LogResult> logResults = ethLog.getLogs();

        for (EthLog.LogResult logResult : logResults) {
            Log log = (Log) logResult.get();
            EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);

            InvalidSuperblockEventResponse invalidSuperblockEventResponse = new InvalidSuperblockEventResponse();
            invalidSuperblockEventResponse.log = eventValues.getLog();
            invalidSuperblockEventResponse.superblockHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            invalidSuperblockEventResponse.who = (String) eventValues.getNonIndexedValues().get(1).getValue();
            result.add(invalidSuperblockEventResponse);
        }

        return result;
    }
}