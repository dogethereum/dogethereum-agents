package org.sysethereum.agents.contract;

import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint32;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthLog;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.tuples.generated.Tuple6;
import org.web3j.tx.TransactionManager;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

public class SyscoinERC20ManagerExtended extends  SyscoinERC20Manager {
    public SyscoinERC20ManagerExtended(String contractAddress, Web3j web3j, TransactionManager transactionManager,
                                       BigInteger gasPrice, BigInteger gasLimit) {
        super(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static String getAddress(String networkId) {
        return getPreviouslyDeployedAddress(networkId);
    }


    public List<CancelTransferRequestEventResponse> getCancelTransferRequestEvents(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock)
            throws IOException {
        List<CancelTransferRequestEventResponse> result = new ArrayList<>();
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
    filter.addSingleTopic(EventEncoder.encode(CANCELTRANSFERREQUEST_EVENT));
        EthLog ethLog = web3j.ethGetLogs(filter).send();
        List<EthLog.LogResult> logResults = ethLog.getLogs();

        for (EthLog.LogResult logResult : logResults) {
            Log log = (Log) logResult.get();
            EventValuesWithLog eventValues = extractEventParametersWithLog(CANCELTRANSFERREQUEST_EVENT, log);

            CancelTransferRequestEventResponse newCancelTransferEventResponse =
                    new CancelTransferRequestEventResponse();
            newCancelTransferEventResponse.log = eventValues.getLog();
            newCancelTransferEventResponse.canceller = (Address) eventValues.getNonIndexedValues().get(0);
            newCancelTransferEventResponse.bridgetransferid = (Uint32) eventValues.getNonIndexedValues().get(1);
            result.add(newCancelTransferEventResponse);
        }

        return result;
    }

}
