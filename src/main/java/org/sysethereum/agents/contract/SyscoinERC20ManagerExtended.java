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

public class SyscoinERC20ManagerExtended extends  SyscoinERC20Manager {
    public SyscoinERC20ManagerExtended(String contractAddress, Web3j web3j, TransactionManager transactionManager,
                                       BigInteger gasPrice, BigInteger gasLimit) {
        super(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static String getAddress(String networkId) {
        return getPreviouslyDeployedAddress(networkId);
    }


    public List<CancelTransferEventResponse> getCancelTransferEvents(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock)
            throws IOException {
        List<CancelTransferEventResponse> result = new ArrayList<>();
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(CANCELTRANSFER_EVENT));
        EthLog ethLog = web3j.ethGetLogs(filter).send();
        List<EthLog.LogResult> logResults = ethLog.getLogs();

        for (EthLog.LogResult logResult : logResults) {
            Log log = (Log) logResult.get();
            EventValuesWithLog eventValues = extractEventParametersWithLog(CANCELTRANSFER_EVENT, log);

            CancelTransferEventResponse newCancelTransferEventResponse =
                    new CancelTransferEventResponse();
            newCancelTransferEventResponse.log = eventValues.getLog();
            newCancelTransferEventResponse.canceller = (Address) eventValues.getNonIndexedValues().get(0);
            newCancelTransferEventResponse.bridgetransferid = (Uint256) eventValues.getNonIndexedValues().get(1);
            result.add(newCancelTransferEventResponse);
        }

        return result;
    }


}
