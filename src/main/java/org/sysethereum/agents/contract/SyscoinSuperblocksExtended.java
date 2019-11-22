package org.sysethereum.agents.contract;

import org.web3j.abi.EventEncoder;
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
import java.util.List;


/**
 * Extension of web3j auto-generated SyscoinSuperblocks class
 * with event polling methods for SuperblockDefenderClient.
 *
 * @author Catalina Juarros
 */
public class SyscoinSuperblocksExtended extends SyscoinSuperblocks {

    public SyscoinSuperblocksExtended(String contractAddress, Web3j web3j, TransactionManager transactionManager,
                                         BigInteger gasPrice, BigInteger gasLimit) {
        //noinspection deprecation
        super(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static String getAddress(String networkId) {
        return getPreviouslyDeployedAddress(networkId);
    }
    /* ---- EVENTS FOR POLLING ---- */

    public List<NewSuperblockEventResponse> getNewSuperblockEvents(long startBlock, long endBlock) throws IOException {

        List<NewSuperblockEventResponse> result = new ArrayList<>();
        List<EthLog.LogResult> logResults = filterLog(startBlock, endBlock, NEWSUPERBLOCK_EVENT);

        for (EthLog.LogResult logResult : logResults) {
            Log log = (Log) logResult.get();
            EventValuesWithLog eventValues = extractEventParametersWithLog(NEWSUPERBLOCK_EVENT, log);

            NewSuperblockEventResponse response = new NewSuperblockEventResponse();
            response.log = eventValues.getLog();
            response.superblockHash = new Bytes32((byte[]) eventValues.getNonIndexedValues().get(0).getValue());
            response.who = new Address((String) eventValues.getNonIndexedValues().get(1).getValue());
            result.add(response);
        }

        return result;
    }

    public List<SemiApprovedSuperblockEventResponse> getSemiApprovedSuperblockEvents(long startBlock, long endBlock) throws IOException {

        List<SemiApprovedSuperblockEventResponse> result = new ArrayList<>();
        List<EthLog.LogResult> logResults = filterLog(startBlock, endBlock, SEMIAPPROVEDSUPERBLOCK_EVENT);

        for (EthLog.LogResult logResult : logResults) {
            Log log = (Log) logResult.get();
            EventValuesWithLog eventValues = extractEventParametersWithLog(SEMIAPPROVEDSUPERBLOCK_EVENT, log);

            SemiApprovedSuperblockEventResponse response = new SemiApprovedSuperblockEventResponse();
            response.log = eventValues.getLog();
            response.superblockHash = new Bytes32((byte[])eventValues.getNonIndexedValues().get(0).getValue());
            response.who =  new Address((String)eventValues.getNonIndexedValues().get(1).getValue());
            result.add(response);
        }

        return result;
    }


    private List<EthLog.LogResult> filterLog(long startBlock, long endBlock, Event event) throws IOException {

        EthFilter filter = new EthFilter(
                DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock)),
                DefaultBlockParameter.valueOf(BigInteger.valueOf(endBlock)),
                getContractAddress()
        );

        filter.addSingleTopic(EventEncoder.encode(event));
        EthLog ethLog = web3j.ethGetLogs(filter).send();
        return ethLog.getLogs();
    }
}