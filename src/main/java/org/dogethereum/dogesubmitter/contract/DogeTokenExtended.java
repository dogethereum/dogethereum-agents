package org.dogethereum.dogesubmitter.contract;

import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes20;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint32;
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
 * Extends autogenerated DogeToken to be able to get UnlockRequest events synchronically
 * @author Catalina Juarros
 */
public class DogeTokenExtended extends DogeToken {

    protected DogeTokenExtended(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static DogeTokenExtended load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new DogeTokenExtended(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public List<DogeToken.UnlockRequestEventResponse> getUnlockRequestEvents(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) throws IOException {
        final Event event = new Event("UnlockRequest",
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint32>() {}, new TypeReference<Bytes20>() {}));
        List<DogeToken.UnlockRequestEventResponse> result = new ArrayList<>();
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        EthLog ethLog = web3j.ethGetLogs(filter).send();
        List<EthLog.LogResult> logResults = ethLog.getLogs();
        for (EthLog.LogResult logResult : logResults) {
            Log log = (Log) logResult.get();
            Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);
            DogeToken.UnlockRequestEventResponse typedResponse = new DogeToken.UnlockRequestEventResponse();
            typedResponse.log = log;
            typedResponse.id = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.operatorPublicKeyHash = (byte[]) eventValues.getNonIndexedValues().get(1).getValue();
            result.add(typedResponse);
        }
        return result;
    }
}
