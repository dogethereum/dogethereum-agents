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

    public List<SuperblockBattleDecidedEventResponse> getSuperblockBattleDecidedEventResponses(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock)
            throws IOException {
        final Event event = SUPERBLOCKBATTLEDECIDED_EVENT;

        List<SuperblockBattleDecidedEventResponse> result = new ArrayList<>();
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        EthLog ethLog = web3j.ethGetLogs(filter).send();
        List<EthLog.LogResult> logResults = ethLog.getLogs();

        for (EthLog.LogResult logResult : logResults) {
            Log log = (Log) logResult.get();
            Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);

            SuperblockBattleDecidedEventResponse newSuperblockBattleDecidedEventResponse =
                    new SuperblockBattleDecidedEventResponse();
            newSuperblockBattleDecidedEventResponse.log = eventValues.getLog();
            newSuperblockBattleDecidedEventResponse.sessionId = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            newSuperblockBattleDecidedEventResponse.winner = (String) eventValues.getNonIndexedValues().get(1).getValue();
            newSuperblockBattleDecidedEventResponse.loser = (String) eventValues.getNonIndexedValues().get(2).getValue();
            result.add(newSuperblockBattleDecidedEventResponse);
        }

        return result;
    }
}
