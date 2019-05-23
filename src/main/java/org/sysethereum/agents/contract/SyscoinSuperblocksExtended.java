package org.sysethereum.agents.contract;

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
import org.web3j.tx.TransactionManager;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Extension of web3j auto-generated SyscoinSuperblocks class
 * with event polling methods for SuperblockDefenderClient.
 * @author Catalina Juarros
 */

public class SyscoinSuperblocksExtended extends SyscoinSuperblocks {
    protected SyscoinSuperblocksExtended(String contractAddress, Web3j web3j, TransactionManager transactionManager,
                                         BigInteger gasPrice, BigInteger gasLimit) {
        super(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static SyscoinSuperblocksExtended load(String contractAddress, Web3j web3j,
                                                  TransactionManager transactionManager, BigInteger gasPrice,
                                                  BigInteger gasLimit) {
        return new SyscoinSuperblocksExtended(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static String getAddress(String networkId) {
        return getPreviouslyDeployedAddress(networkId);
    }
    /* ---- EVENTS FOR POLLING ---- */

    public List<NewSuperblockEventResponse> getNewSuperblockEvents(DefaultBlockParameter startBlock,
                                                                   DefaultBlockParameter endBlock) throws IOException {

        List<NewSuperblockEventResponse> result = new ArrayList<>();
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(NEWSUPERBLOCK_EVENT));
        EthLog ethLog = web3j.ethGetLogs(filter).send();
        List<EthLog.LogResult> logResults = ethLog.getLogs();

        for (EthLog.LogResult logResult : logResults) {
            Log log = (Log) logResult.get();
            EventValuesWithLog eventValues = extractEventParametersWithLog(NEWSUPERBLOCK_EVENT, log);

            NewSuperblockEventResponse newSuperblockResponse = new NewSuperblockEventResponse();
            newSuperblockResponse.log = eventValues.getLog();
            newSuperblockResponse.superblockHash = new Bytes32((byte[])eventValues.getNonIndexedValues().get(0).getValue());
            newSuperblockResponse.who =  new Address((String)eventValues.getNonIndexedValues().get(1).getValue());
            result.add(newSuperblockResponse);
        }

        return result;
    }

    public List<ApprovedSuperblockEventResponse> getApprovedSuperblockEvents(DefaultBlockParameter startBlock,
                                                                             DefaultBlockParameter endBlock)
            throws IOException {

        List<ApprovedSuperblockEventResponse> result = new ArrayList<>();
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(APPROVEDSUPERBLOCK_EVENT));
        EthLog ethLog = web3j.ethGetLogs(filter).send();
        List<EthLog.LogResult> logResults = ethLog.getLogs();

        for (EthLog.LogResult logResult : logResults) {
            Log log = (Log) logResult.get();
            EventValuesWithLog eventValues = extractEventParametersWithLog(APPROVEDSUPERBLOCK_EVENT, log);

            ApprovedSuperblockEventResponse approvedSuperblockResponse = new ApprovedSuperblockEventResponse();
            approvedSuperblockResponse.log = eventValues.getLog();

            approvedSuperblockResponse.superblockHash = new Bytes32((byte[])eventValues.getNonIndexedValues().get(0).getValue());
            approvedSuperblockResponse.who =  new Address((String)eventValues.getNonIndexedValues().get(1).getValue());
            result.add(approvedSuperblockResponse);
        }

        return result;
    }

    public List<ChallengeSuperblockEventResponse> getChallengeSuperblockEvents(DefaultBlockParameter startBlock,
                                                                               DefaultBlockParameter endBlock)
            throws IOException {

        List<ChallengeSuperblockEventResponse> result = new ArrayList<>();
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(CHALLENGESUPERBLOCK_EVENT));
        EthLog ethLog = web3j.ethGetLogs(filter).send();
        List<EthLog.LogResult> logResults = ethLog.getLogs();

        for (EthLog.LogResult logResult : logResults) {
            Log log = (Log) logResult.get();
            EventValuesWithLog eventValues = extractEventParametersWithLog(CHALLENGESUPERBLOCK_EVENT, log);

            ChallengeSuperblockEventResponse challengeSuperblockResponse = new ChallengeSuperblockEventResponse();
            challengeSuperblockResponse.log = eventValues.getLog();
            challengeSuperblockResponse.superblockHash = new Bytes32((byte[])eventValues.getNonIndexedValues().get(0).getValue());
            challengeSuperblockResponse.who = new Address((String)eventValues.getNonIndexedValues().get(1).getValue());
            result.add(challengeSuperblockResponse);
        }

        return result;
    }

    public List<SemiApprovedSuperblockEventResponse> getSemiApprovedSuperblockEvents(DefaultBlockParameter startBlock,
                                                                               DefaultBlockParameter endBlock)
            throws IOException {


        List<SemiApprovedSuperblockEventResponse> result = new ArrayList<>();
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(SEMIAPPROVEDSUPERBLOCK_EVENT));
        EthLog ethLog = web3j.ethGetLogs(filter).send();
        List<EthLog.LogResult> logResults = ethLog.getLogs();

        for (EthLog.LogResult logResult : logResults) {
            Log log = (Log) logResult.get();
            EventValuesWithLog eventValues = extractEventParametersWithLog(SEMIAPPROVEDSUPERBLOCK_EVENT, log);

            SemiApprovedSuperblockEventResponse semiApprovedSuperblockResponse = new SemiApprovedSuperblockEventResponse();
            semiApprovedSuperblockResponse.log = eventValues.getLog();
            semiApprovedSuperblockResponse.superblockHash = new Bytes32((byte[])eventValues.getNonIndexedValues().get(0).getValue());
            semiApprovedSuperblockResponse.who =  new Address((String)eventValues.getNonIndexedValues().get(1).getValue());
            result.add(semiApprovedSuperblockResponse);
        }

        return result;
    }

    public List<InvalidSuperblockEventResponse> getInvalidSuperblockEvents(DefaultBlockParameter startBlock,
                                                                               DefaultBlockParameter endBlock)
            throws IOException {

        List<InvalidSuperblockEventResponse> result = new ArrayList<>();
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(INVALIDSUPERBLOCK_EVENT));
        EthLog ethLog = web3j.ethGetLogs(filter).send();
        List<EthLog.LogResult> logResults = ethLog.getLogs();

        for (EthLog.LogResult logResult : logResults) {
            Log log = (Log) logResult.get();
            EventValuesWithLog eventValues = extractEventParametersWithLog(INVALIDSUPERBLOCK_EVENT, log);

            InvalidSuperblockEventResponse invalidSuperblockEventResponse = new InvalidSuperblockEventResponse();
            invalidSuperblockEventResponse.log = eventValues.getLog();
            invalidSuperblockEventResponse.superblockHash = new Bytes32((byte[])eventValues.getNonIndexedValues().get(0).getValue());
            invalidSuperblockEventResponse.who =  new Address((String)eventValues.getNonIndexedValues().get(1).getValue());
            result.add(invalidSuperblockEventResponse);
        }

        return result;
    }
}