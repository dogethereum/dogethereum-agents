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
    public List<SyscoinClaimManager.SuperblockClaimSuccessfulEventResponse> getSuperblockClaimSuccessfulEventResponses(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock)
            throws IOException {
        List<SyscoinClaimManager.SuperblockClaimSuccessfulEventResponse> result = new ArrayList<>();
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(SUPERBLOCKCLAIMSUCCESSFUL_EVENT));
        EthLog ethLog = web3j.ethGetLogs(filter).send();
        List<EthLog.LogResult> logResults = ethLog.getLogs();

        for (EthLog.LogResult logResult : logResults) {
            Log log = (Log) logResult.get();
            EventValuesWithLog eventValues = extractEventParametersWithLog(SUPERBLOCKCLAIMSUCCESSFUL_EVENT, log);

            SyscoinClaimManager.SuperblockClaimSuccessfulEventResponse newEventResponse =
                    new SyscoinClaimManager.SuperblockClaimSuccessfulEventResponse();
            newEventResponse.log = eventValues.getLog();
            newEventResponse.superblockHash = new Bytes32((byte[]) eventValues.getNonIndexedValues().get(0).getValue());
            newEventResponse.submitter = new Address((String)eventValues.getNonIndexedValues().get(1).getValue());
            result.add(newEventResponse);
        }

        return result;
    }
    public List<SyscoinClaimManager.SuperblockClaimFailedEventResponse> getSuperblockClaimFailedEventResponses(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock)
            throws IOException {
        List<SyscoinClaimManager.SuperblockClaimFailedEventResponse> result = new ArrayList<>();
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(SUPERBLOCKCLAIMFAILED_EVENT));
        EthLog ethLog = web3j.ethGetLogs(filter).send();
        List<EthLog.LogResult> logResults = ethLog.getLogs();

        for (EthLog.LogResult logResult : logResults) {
            Log log = (Log) logResult.get();
            EventValuesWithLog eventValues = extractEventParametersWithLog(SUPERBLOCKCLAIMFAILED_EVENT, log);

            SyscoinClaimManager.SuperblockClaimFailedEventResponse newEventResponse =
                    new SyscoinClaimManager.SuperblockClaimFailedEventResponse();
            newEventResponse.log = eventValues.getLog();
            newEventResponse.superblockHash = new Bytes32((byte[]) eventValues.getNonIndexedValues().get(0).getValue());
            newEventResponse.challenger = new Address((String)eventValues.getNonIndexedValues().get(1).getValue());
            result.add(newEventResponse);
        }

        return result;
    }
}
