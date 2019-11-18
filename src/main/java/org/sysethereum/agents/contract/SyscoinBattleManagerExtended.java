package org.sysethereum.agents.contract;

import org.web3j.abi.EventEncoder;
import org.web3j.abi.datatypes.Address;
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
import java.util.List;

public class SyscoinBattleManagerExtended extends  SyscoinBattleManager {
    public SyscoinBattleManagerExtended(String contractAddress, Web3j web3j, TransactionManager transactionManager,
                                           BigInteger gasPrice, BigInteger gasLimit) {
        super(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static String getAddress(String networkId) {
        return getPreviouslyDeployedAddress(networkId);
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
            newBattleEventResponse.submitter = new Address ((String)eventValues.getNonIndexedValues().get(1).getValue());
            newBattleEventResponse.challenger = new Address ((String)eventValues.getNonIndexedValues().get(2).getValue());
            result.add(newBattleEventResponse);
        }

        return result;
    }
    public List<RespondBlockHeadersEventResponse> getNewBlockHeadersEventResponses(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock)
            throws IOException {
        List<RespondBlockHeadersEventResponse> result = new ArrayList<>();
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(RESPONDBLOCKHEADERS_EVENT));
        EthLog ethLog = web3j.ethGetLogs(filter).send();
        List<EthLog.LogResult> logResults = ethLog.getLogs();

        for (EthLog.LogResult logResult : logResults) {
            Log log = (Log) logResult.get();
            EventValuesWithLog eventValues = extractEventParametersWithLog(RESPONDBLOCKHEADERS_EVENT, log);

            RespondBlockHeadersEventResponse newBlockHeadersEventResponse =
                    new RespondBlockHeadersEventResponse();
            newBlockHeadersEventResponse.log = eventValues.getLog();
            newBlockHeadersEventResponse.superblockHash = new Bytes32((byte[]) eventValues.getNonIndexedValues().get(0).getValue());
            newBlockHeadersEventResponse.merkleHashCount = (Uint256) eventValues.getNonIndexedValues().get(1);
            newBlockHeadersEventResponse.submitter = new Address ((String)eventValues.getNonIndexedValues().get(2).getValue());
            result.add(newBlockHeadersEventResponse);
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
            newChallengerConvictedEventResponse.err = (Uint256) eventValues.getNonIndexedValues().get(1);
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
            newSubmitterConvictedEventResponse.err = (Uint256) eventValues.getNonIndexedValues().get(1);
            newSubmitterConvictedEventResponse.submitter = new Address ((String)eventValues.getNonIndexedValues().get(2).getValue());
            result.add(newSubmitterConvictedEventResponse);
        }

        return result;
    }

}
