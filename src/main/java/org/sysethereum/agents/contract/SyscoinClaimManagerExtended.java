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

public class SyscoinClaimManagerExtended extends SyscoinClaimManager {
    protected SyscoinClaimManagerExtended(String contractAddress, Web3j web3j, TransactionManager transactionManager,
                                          BigInteger gasPrice, BigInteger gasLimit) {
        super(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static SyscoinClaimManagerExtended load(String contractAddress, Web3j web3j,
                                                   TransactionManager transactionManager, BigInteger gasPrice,
                                                   BigInteger gasLimit) {
        return new SyscoinClaimManagerExtended(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }
    public static String getAddress(String networkId) {
        return getPreviouslyDeployedAddress(networkId);
    }
    public List<SuperblockBattleDecidedEventResponse> getSuperblockBattleDecidedEventResponses(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock)
            throws IOException {

        List<SuperblockBattleDecidedEventResponse> result = new ArrayList<>();
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(SUPERBLOCKBATTLEDECIDED_EVENT));
        EthLog ethLog = web3j.ethGetLogs(filter).send();
        List<EthLog.LogResult> logResults = ethLog.getLogs();

        for (EthLog.LogResult logResult : logResults) {
            Log log = (Log) logResult.get();
            EventValuesWithLog eventValues = extractEventParametersWithLog(SUPERBLOCKBATTLEDECIDED_EVENT, log);

            SuperblockBattleDecidedEventResponse newSuperblockBattleDecidedEventResponse =
                    new SuperblockBattleDecidedEventResponse();
            newSuperblockBattleDecidedEventResponse.log = eventValues.getLog();
            newSuperblockBattleDecidedEventResponse.sessionId = new Bytes32((byte[])eventValues.getNonIndexedValues().get(0).getValue());
            newSuperblockBattleDecidedEventResponse.winner = new Address((String)eventValues.getNonIndexedValues().get(1).getValue());
            newSuperblockBattleDecidedEventResponse.loser = new Address((String)eventValues.getNonIndexedValues().get(2).getValue());
            result.add(newSuperblockBattleDecidedEventResponse);
        }

        return result;
    }
}
