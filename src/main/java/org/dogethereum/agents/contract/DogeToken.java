package org.dogethereum.agents.contract;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.StaticStruct;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes20;
import org.web3j.abi.datatypes.generated.Uint24;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint32;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tuples.generated.Tuple3;
import org.web3j.tuples.generated.Tuple7;
import org.web3j.tuples.generated.Tuple8;
import org.web3j.tuples.generated.Tuple9;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 4.8.2.
 */
@SuppressWarnings("rawtypes")
public class DogeToken extends Contract {
    public static final String BINARY = "Bin file was not provided";

    public static final String FUNC_DOGETHEREUM_COLLATERAL_RATIO_FRACTION = "DOGETHEREUM_COLLATERAL_RATIO_FRACTION";

    public static final String FUNC_DOGETHEREUM_FEE_FRACTION = "DOGETHEREUM_FEE_FRACTION";

    public static final String FUNC_DOGE_DUST = "DOGE_DUST";

    public static final String FUNC_DOGE_TX_FEE_RATE = "DOGE_TX_FEE_RATE";

    public static final String FUNC_DOGE_TX_FIXED_SIZE = "DOGE_TX_FIXED_SIZE";

    public static final String FUNC_DOGE_TX_INPUT_SIZE = "DOGE_TX_INPUT_SIZE";

    public static final String FUNC_DOGE_TX_OUTPUT_SIZE = "DOGE_TX_OUTPUT_SIZE";

    public static final String FUNC_MIN_LOCK_VALUE = "MIN_LOCK_VALUE";

    public static final String FUNC_MIN_UNLOCK_VALUE = "MIN_UNLOCK_VALUE";

    public static final String FUNC_OPERATOR_LOCK_FEE = "OPERATOR_LOCK_FEE";

    public static final String FUNC_OPERATOR_UNLOCK_FEE = "OPERATOR_UNLOCK_FEE";

    public static final String FUNC_SUPERBLOCK_SUBMITTER_LOCK_FEE = "SUPERBLOCK_SUBMITTER_LOCK_FEE";

    public static final String FUNC_ADDOPERATOR = "addOperator";

    public static final String FUNC_ADDOPERATORDEPOSIT = "addOperatorDeposit";

    public static final String FUNC_ALLOWANCE = "allowance";

    public static final String FUNC_APPROVE = "approve";

    public static final String FUNC_AUCTIONMINIMUMDURATION = "auctionMinimumDuration";

    public static final String FUNC_BALANCEOF = "balanceOf";

    public static final String FUNC_CLOSELIQUIDATIONAUCTION = "closeLiquidationAuction";

    public static final String FUNC_DECIMALS = "decimals";

    public static final String FUNC_DELETEOPERATOR = "deleteOperator";

    public static final String FUNC_DOUNLOCK = "doUnlock";

    public static final String FUNC_DOGEETHPRICE = "dogeEthPrice";

    public static final String FUNC_DOGEUSDORACLE = "dogeUsdOracle";

    public static final String FUNC_ETHUSDORACLE = "ethUsdOracle";

    public static final String FUNC_ETHEREUMTIMEGRACEPERIOD = "ethereumTimeGracePeriod";

    public static final String FUNC_GETOPERATORSLENGTH = "getOperatorsLength";

    public static final String FUNC_GETUNLOCK = "getUnlock";

    public static final String FUNC_GETUTXO = "getUtxo";

    public static final String FUNC_GETUTXOSLENGTH = "getUtxosLength";

    public static final String FUNC_INITIALIZE = "initialize";

    public static final String FUNC_LIQUIDATIONBID = "liquidationBid";

    public static final String FUNC_LIQUIDATIONTHRESHOLD = "liquidationThreshold";

    public static final String FUNC_LOCKCOLLATERALRATIO = "lockCollateralRatio";

    public static final String FUNC_NAME = "name";

    public static final String FUNC_OPERATORKEYS = "operatorKeys";

    public static final String FUNC_OPERATORS = "operators";

    public static final String FUNC_PROCESSLOCKTRANSACTION = "processLockTransaction";

    public static final String FUNC_PROCESSREPORTOPERATORFREEUTXOSPEND = "processReportOperatorFreeUtxoSpend";

    public static final String FUNC_PROCESSUNLOCKTRANSACTION = "processUnlockTransaction";

    public static final String FUNC_REPORTOPERATORMISSINGUNLOCK = "reportOperatorMissingUnlock";

    public static final String FUNC_REPORTOPERATORUNSAFECOLLATERAL = "reportOperatorUnsafeCollateral";

    public static final String FUNC_SUPERBLOCKS = "superblocks";

    public static final String FUNC_SUPERBLOCKSHEIGHTGRACEPERIOD = "superblocksHeightGracePeriod";

    public static final String FUNC_SYMBOL = "symbol";

    public static final String FUNC_TOTALSUPPLY = "totalSupply";

    public static final String FUNC_TRANSFER = "transfer";

    public static final String FUNC_TRANSFERFROM = "transferFrom";

    public static final String FUNC_TRUSTEDRELAYERCONTRACT = "trustedRelayerContract";

    public static final String FUNC_UNLOCKIDX = "unlockIdx";

    public static final String FUNC_UNLOCKS = "unlocks";

    public static final String FUNC_WASDOGETXPROCESSED = "wasDogeTxProcessed";

    public static final String FUNC_WITHDRAWOPERATORDEPOSIT = "withdrawOperatorDeposit";

    public static final Event APPROVAL_EVENT = new Event("Approval", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event LIQUIDATIONBID_EVENT = new Event("LiquidationBid", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes20>() {}, new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event NEWTOKEN_EVENT = new Event("NewToken", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event OPERATORCOLLATERALAUCTIONED_EVENT = new Event("OperatorCollateralAuctioned", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes20>() {}, new TypeReference<Address>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event OPERATORLIQUIDATED_EVENT = new Event("OperatorLiquidated", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes20>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event TRANSFER_EVENT = new Event("Transfer", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event UNLOCKREQUEST_EVENT = new Event("UnlockRequest", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Bytes20>() {}));
    ;

    @Deprecated
    protected DogeToken(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected DogeToken(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected DogeToken(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected DogeToken(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public List<ApprovalEventResponse> getApprovalEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(APPROVAL_EVENT, transactionReceipt);
        ArrayList<ApprovalEventResponse> responses = new ArrayList<ApprovalEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ApprovalEventResponse typedResponse = new ApprovalEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.owner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.spender = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<ApprovalEventResponse> approvalEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, ApprovalEventResponse>() {
            @Override
            public ApprovalEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(APPROVAL_EVENT, log);
                ApprovalEventResponse typedResponse = new ApprovalEventResponse();
                typedResponse.log = log;
                typedResponse.owner = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.spender = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<ApprovalEventResponse> approvalEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(APPROVAL_EVENT));
        return approvalEventFlowable(filter);
    }

    public List<LiquidationBidEventResponse> getLiquidationBidEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(LIQUIDATIONBID_EVENT, transactionReceipt);
        ArrayList<LiquidationBidEventResponse> responses = new ArrayList<LiquidationBidEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            LiquidationBidEventResponse typedResponse = new LiquidationBidEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.operatorPublicKeyHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.bidder = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.bid = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<LiquidationBidEventResponse> liquidationBidEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, LiquidationBidEventResponse>() {
            @Override
            public LiquidationBidEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(LIQUIDATIONBID_EVENT, log);
                LiquidationBidEventResponse typedResponse = new LiquidationBidEventResponse();
                typedResponse.log = log;
                typedResponse.operatorPublicKeyHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.bidder = (String) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.bid = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<LiquidationBidEventResponse> liquidationBidEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(LIQUIDATIONBID_EVENT));
        return liquidationBidEventFlowable(filter);
    }

    public List<NewTokenEventResponse> getNewTokenEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(NEWTOKEN_EVENT, transactionReceipt);
        ArrayList<NewTokenEventResponse> responses = new ArrayList<NewTokenEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            NewTokenEventResponse typedResponse = new NewTokenEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.user = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<NewTokenEventResponse> newTokenEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, NewTokenEventResponse>() {
            @Override
            public NewTokenEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(NEWTOKEN_EVENT, log);
                NewTokenEventResponse typedResponse = new NewTokenEventResponse();
                typedResponse.log = log;
                typedResponse.user = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<NewTokenEventResponse> newTokenEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(NEWTOKEN_EVENT));
        return newTokenEventFlowable(filter);
    }

    public List<OperatorCollateralAuctionedEventResponse> getOperatorCollateralAuctionedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(OPERATORCOLLATERALAUCTIONED_EVENT, transactionReceipt);
        ArrayList<OperatorCollateralAuctionedEventResponse> responses = new ArrayList<OperatorCollateralAuctionedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            OperatorCollateralAuctionedEventResponse typedResponse = new OperatorCollateralAuctionedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.operatorPublicKeyHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.winner = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.tokensBurned = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.etherSold = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<OperatorCollateralAuctionedEventResponse> operatorCollateralAuctionedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, OperatorCollateralAuctionedEventResponse>() {
            @Override
            public OperatorCollateralAuctionedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(OPERATORCOLLATERALAUCTIONED_EVENT, log);
                OperatorCollateralAuctionedEventResponse typedResponse = new OperatorCollateralAuctionedEventResponse();
                typedResponse.log = log;
                typedResponse.operatorPublicKeyHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.winner = (String) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.tokensBurned = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
                typedResponse.etherSold = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<OperatorCollateralAuctionedEventResponse> operatorCollateralAuctionedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(OPERATORCOLLATERALAUCTIONED_EVENT));
        return operatorCollateralAuctionedEventFlowable(filter);
    }

    public List<OperatorLiquidatedEventResponse> getOperatorLiquidatedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(OPERATORLIQUIDATED_EVENT, transactionReceipt);
        ArrayList<OperatorLiquidatedEventResponse> responses = new ArrayList<OperatorLiquidatedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            OperatorLiquidatedEventResponse typedResponse = new OperatorLiquidatedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.operatorPublicKeyHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.endTimestamp = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<OperatorLiquidatedEventResponse> operatorLiquidatedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, OperatorLiquidatedEventResponse>() {
            @Override
            public OperatorLiquidatedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(OPERATORLIQUIDATED_EVENT, log);
                OperatorLiquidatedEventResponse typedResponse = new OperatorLiquidatedEventResponse();
                typedResponse.log = log;
                typedResponse.operatorPublicKeyHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.endTimestamp = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<OperatorLiquidatedEventResponse> operatorLiquidatedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(OPERATORLIQUIDATED_EVENT));
        return operatorLiquidatedEventFlowable(filter);
    }

    public List<TransferEventResponse> getTransferEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(TRANSFER_EVENT, transactionReceipt);
        ArrayList<TransferEventResponse> responses = new ArrayList<TransferEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            TransferEventResponse typedResponse = new TransferEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<TransferEventResponse> transferEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, TransferEventResponse>() {
            @Override
            public TransferEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(TRANSFER_EVENT, log);
                TransferEventResponse typedResponse = new TransferEventResponse();
                typedResponse.log = log;
                typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<TransferEventResponse> transferEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(TRANSFER_EVENT));
        return transferEventFlowable(filter);
    }

    public List<UnlockRequestEventResponse> getUnlockRequestEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(UNLOCKREQUEST_EVENT, transactionReceipt);
        ArrayList<UnlockRequestEventResponse> responses = new ArrayList<UnlockRequestEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            UnlockRequestEventResponse typedResponse = new UnlockRequestEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.id = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.operatorPublicKeyHash = (byte[]) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<UnlockRequestEventResponse> unlockRequestEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, UnlockRequestEventResponse>() {
            @Override
            public UnlockRequestEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(UNLOCKREQUEST_EVENT, log);
                UnlockRequestEventResponse typedResponse = new UnlockRequestEventResponse();
                typedResponse.log = log;
                typedResponse.id = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.operatorPublicKeyHash = (byte[]) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<UnlockRequestEventResponse> unlockRequestEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(UNLOCKREQUEST_EVENT));
        return unlockRequestEventFlowable(filter);
    }

    public RemoteFunctionCall<BigInteger> DOGETHEREUM_COLLATERAL_RATIO_FRACTION() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_DOGETHEREUM_COLLATERAL_RATIO_FRACTION, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> DOGETHEREUM_FEE_FRACTION() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_DOGETHEREUM_FEE_FRACTION, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> DOGE_DUST() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_DOGE_DUST, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> DOGE_TX_FEE_RATE() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_DOGE_TX_FEE_RATE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> DOGE_TX_FIXED_SIZE() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_DOGE_TX_FIXED_SIZE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> DOGE_TX_INPUT_SIZE() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_DOGE_TX_INPUT_SIZE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> DOGE_TX_OUTPUT_SIZE() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_DOGE_TX_OUTPUT_SIZE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> MIN_LOCK_VALUE() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_MIN_LOCK_VALUE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> MIN_UNLOCK_VALUE() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_MIN_UNLOCK_VALUE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> OPERATOR_LOCK_FEE() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_OPERATOR_LOCK_FEE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> OPERATOR_UNLOCK_FEE() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_OPERATOR_UNLOCK_FEE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> SUPERBLOCK_SUBMITTER_LOCK_FEE() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_SUPERBLOCK_SUBMITTER_LOCK_FEE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> addOperator(byte[] operatorPublicKeyCompressed, byte[] signature) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_ADDOPERATOR, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicBytes(operatorPublicKeyCompressed), 
                new org.web3j.abi.datatypes.DynamicBytes(signature)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> addOperatorDeposit(byte[] operatorPublicKeyHash, BigInteger weiValue) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_ADDOPERATORDEPOSIT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes20(operatorPublicKeyHash)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteFunctionCall<BigInteger> allowance(String owner, String spender) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_ALLOWANCE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(owner), 
                new org.web3j.abi.datatypes.Address(spender)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> approve(String spender, BigInteger value) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_APPROVE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(spender), 
                new org.web3j.abi.datatypes.generated.Uint256(value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> auctionMinimumDuration() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_AUCTIONMINIMUMDURATION, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> balanceOf(String owner) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_BALANCEOF, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(owner)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> closeLiquidationAuction(byte[] operatorPublicKeyHash) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_CLOSELIQUIDATIONAUCTION, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes20(operatorPublicKeyHash)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> decimals() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_DECIMALS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> deleteOperator(byte[] operatorPublicKeyHash) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_DELETEOPERATOR, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes20(operatorPublicKeyHash)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> doUnlock(byte[] dogeAddress, BigInteger value, byte[] operatorPublicKeyHash) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_DOUNLOCK, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes20(dogeAddress), 
                new org.web3j.abi.datatypes.generated.Uint256(value), 
                new org.web3j.abi.datatypes.generated.Bytes20(operatorPublicKeyHash)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> dogeEthPrice() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_DOGEETHPRICE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<String> dogeUsdOracle() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_DOGEUSDORACLE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> ethUsdOracle() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_ETHUSDORACLE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<BigInteger> ethereumTimeGracePeriod() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_ETHEREUMTIMEGRACEPERIOD, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> getOperatorsLength() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETOPERATORSLENGTH, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint24>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<Tuple9<String, byte[], BigInteger, BigInteger, BigInteger, BigInteger, List<BigInteger>, byte[], Boolean>> getUnlock(BigInteger index) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETUNLOCK, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(index)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Bytes20>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<DynamicArray<Uint32>>() {}, new TypeReference<Bytes20>() {}, new TypeReference<Bool>() {}));
        return new RemoteFunctionCall<Tuple9<String, byte[], BigInteger, BigInteger, BigInteger, BigInteger, List<BigInteger>, byte[], Boolean>>(function,
                new Callable<Tuple9<String, byte[], BigInteger, BigInteger, BigInteger, BigInteger, List<BigInteger>, byte[], Boolean>>() {
                    @Override
                    public Tuple9<String, byte[], BigInteger, BigInteger, BigInteger, BigInteger, List<BigInteger>, byte[], Boolean> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple9<String, byte[], BigInteger, BigInteger, BigInteger, BigInteger, List<BigInteger>, byte[], Boolean>(
                                (String) results.get(0).getValue(), 
                                (byte[]) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue(), 
                                (BigInteger) results.get(3).getValue(), 
                                (BigInteger) results.get(4).getValue(), 
                                (BigInteger) results.get(5).getValue(), 
                                convertToNative((List<Uint32>) results.get(6).getValue()), 
                                (byte[]) results.get(7).getValue(), 
                                (Boolean) results.get(8).getValue());
                    }
                });
    }

    public RemoteFunctionCall<Tuple3<BigInteger, BigInteger, BigInteger>> getUtxo(byte[] operatorPublicKeyHash, BigInteger i) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETUTXO, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes20(operatorPublicKeyHash), 
                new org.web3j.abi.datatypes.generated.Uint256(i)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint32>() {}));
        return new RemoteFunctionCall<Tuple3<BigInteger, BigInteger, BigInteger>>(function,
                new Callable<Tuple3<BigInteger, BigInteger, BigInteger>>() {
                    @Override
                    public Tuple3<BigInteger, BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple3<BigInteger, BigInteger, BigInteger>(
                                (BigInteger) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue());
                    }
                });
    }

    public RemoteFunctionCall<BigInteger> getUtxosLength(byte[] operatorPublicKeyHash) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETUTXOSLENGTH, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes20(operatorPublicKeyHash)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> initialize(String relayerContract, String initSuperblocks, String initDogeUsdOracle, String initEthUsdOracle, BigInteger initLockCollateralRatio, BigInteger initLiquidationThreshold, BigInteger timeGracePeriod, BigInteger superblocksGracePeriod) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_INITIALIZE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(relayerContract), 
                new org.web3j.abi.datatypes.Address(initSuperblocks), 
                new org.web3j.abi.datatypes.Address(initDogeUsdOracle), 
                new org.web3j.abi.datatypes.Address(initEthUsdOracle), 
                new org.web3j.abi.datatypes.generated.Uint256(initLockCollateralRatio), 
                new org.web3j.abi.datatypes.generated.Uint256(initLiquidationThreshold), 
                new org.web3j.abi.datatypes.generated.Uint256(timeGracePeriod), 
                new org.web3j.abi.datatypes.generated.Uint256(superblocksGracePeriod)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> liquidationBid(byte[] operatorPublicKeyHash, BigInteger tokenAmount) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_LIQUIDATIONBID, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes20(operatorPublicKeyHash), 
                new org.web3j.abi.datatypes.generated.Uint256(tokenAmount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> liquidationThreshold() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_LIQUIDATIONTHRESHOLD, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> lockCollateralRatio() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_LOCKCOLLATERALRATIO, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<String> name() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_NAME, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<Tuple2<byte[], Boolean>> operatorKeys(BigInteger param0) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_OPERATORKEYS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes20>() {}, new TypeReference<Bool>() {}));
        return new RemoteFunctionCall<Tuple2<byte[], Boolean>>(function,
                new Callable<Tuple2<byte[], Boolean>>() {
                    @Override
                    public Tuple2<byte[], Boolean> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple2<byte[], Boolean>(
                                (byte[]) results.get(0).getValue(), 
                                (Boolean) results.get(1).getValue());
                    }
                });
    }

    public RemoteFunctionCall<Tuple7<String, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, Auction>> operators(byte[] param0) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_OPERATORS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes20(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint32>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint24>() {}, new TypeReference<Auction>() {}));
        return new RemoteFunctionCall<Tuple7<String, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, Auction>>(function,
                new Callable<Tuple7<String, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, Auction>>() {
                    @Override
                    public Tuple7<String, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, Auction> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple7<String, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, Auction>(
                                (String) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue(), 
                                (BigInteger) results.get(3).getValue(), 
                                (BigInteger) results.get(4).getValue(), 
                                (BigInteger) results.get(5).getValue(), 
                                (Auction) results.get(6));
                    }
                });
    }

    public RemoteFunctionCall<TransactionReceipt> processLockTransaction(byte[] dogeTx, BigInteger dogeTxHash, byte[] operatorPublicKeyHash, String superblockSubmitterAddress) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_PROCESSLOCKTRANSACTION, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicBytes(dogeTx), 
                new org.web3j.abi.datatypes.generated.Uint256(dogeTxHash), 
                new org.web3j.abi.datatypes.generated.Bytes20(operatorPublicKeyHash), 
                new org.web3j.abi.datatypes.Address(superblockSubmitterAddress)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> processReportOperatorFreeUtxoSpend(byte[] dogeTx, BigInteger dogeTxHash, byte[] operatorPublicKeyHash, BigInteger operatorTxOutputReference, BigInteger unlawfulTxInputIndex) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_PROCESSREPORTOPERATORFREEUTXOSPEND, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicBytes(dogeTx), 
                new org.web3j.abi.datatypes.generated.Uint256(dogeTxHash), 
                new org.web3j.abi.datatypes.generated.Bytes20(operatorPublicKeyHash), 
                new org.web3j.abi.datatypes.generated.Uint32(operatorTxOutputReference), 
                new org.web3j.abi.datatypes.generated.Uint32(unlawfulTxInputIndex)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> processUnlockTransaction(byte[] dogeTx, BigInteger dogeTxHash, byte[] operatorPublicKeyHash, BigInteger unlockIndex) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_PROCESSUNLOCKTRANSACTION, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicBytes(dogeTx), 
                new org.web3j.abi.datatypes.generated.Uint256(dogeTxHash), 
                new org.web3j.abi.datatypes.generated.Bytes20(operatorPublicKeyHash), 
                new org.web3j.abi.datatypes.generated.Uint256(unlockIndex)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> reportOperatorMissingUnlock(byte[] operatorPublicKeyHash, BigInteger unlockIndex) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_REPORTOPERATORMISSINGUNLOCK, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes20(operatorPublicKeyHash), 
                new org.web3j.abi.datatypes.generated.Uint256(unlockIndex)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> reportOperatorUnsafeCollateral(byte[] operatorPublicKeyHash) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_REPORTOPERATORUNSAFECOLLATERAL, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes20(operatorPublicKeyHash)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> superblocks() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_SUPERBLOCKS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<BigInteger> superblocksHeightGracePeriod() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_SUPERBLOCKSHEIGHTGRACEPERIOD, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<String> symbol() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_SYMBOL, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<BigInteger> totalSupply() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_TOTALSUPPLY, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> transfer(String to, BigInteger value) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_TRANSFER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(to), 
                new org.web3j.abi.datatypes.generated.Uint256(value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> transferFrom(String from, String to, BigInteger value) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_TRANSFERFROM, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(from), 
                new org.web3j.abi.datatypes.Address(to), 
                new org.web3j.abi.datatypes.generated.Uint256(value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> trustedRelayerContract() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_TRUSTEDRELAYERCONTRACT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<BigInteger> unlockIdx() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_UNLOCKIDX, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<Tuple8<String, byte[], BigInteger, BigInteger, BigInteger, BigInteger, byte[], Boolean>> unlocks(BigInteger param0) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_UNLOCKS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Bytes20>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Bytes20>() {}, new TypeReference<Bool>() {}));
        return new RemoteFunctionCall<Tuple8<String, byte[], BigInteger, BigInteger, BigInteger, BigInteger, byte[], Boolean>>(function,
                new Callable<Tuple8<String, byte[], BigInteger, BigInteger, BigInteger, BigInteger, byte[], Boolean>>() {
                    @Override
                    public Tuple8<String, byte[], BigInteger, BigInteger, BigInteger, BigInteger, byte[], Boolean> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple8<String, byte[], BigInteger, BigInteger, BigInteger, BigInteger, byte[], Boolean>(
                                (String) results.get(0).getValue(), 
                                (byte[]) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue(), 
                                (BigInteger) results.get(3).getValue(), 
                                (BigInteger) results.get(4).getValue(), 
                                (BigInteger) results.get(5).getValue(), 
                                (byte[]) results.get(6).getValue(), 
                                (Boolean) results.get(7).getValue());
                    }
                });
    }

    public RemoteFunctionCall<Boolean> wasDogeTxProcessed(BigInteger txHash) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_WASDOGETXPROCESSED, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(txHash)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<TransactionReceipt> withdrawOperatorDeposit(byte[] operatorPublicKeyHash, BigInteger value) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_WITHDRAWOPERATORDEPOSIT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes20(operatorPublicKeyHash), 
                new org.web3j.abi.datatypes.generated.Uint256(value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static DogeToken load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new DogeToken(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static DogeToken load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new DogeToken(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static DogeToken load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new DogeToken(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static DogeToken load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new DogeToken(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static class Auction extends StaticStruct {
        public BigInteger bestBid;

        public String bestBidder;

        public BigInteger status;

        public BigInteger endTimestamp;

        public Auction(BigInteger bestBid, String bestBidder, BigInteger status, BigInteger endTimestamp) {
            super(new org.web3j.abi.datatypes.generated.Uint256(bestBid),new org.web3j.abi.datatypes.Address(bestBidder),new org.web3j.abi.datatypes.generated.Uint8(status),new org.web3j.abi.datatypes.generated.Uint256(endTimestamp));
            this.bestBid = bestBid;
            this.bestBidder = bestBidder;
            this.status = status;
            this.endTimestamp = endTimestamp;
        }

        public Auction(Uint256 bestBid, Address bestBidder, Uint8 status, Uint256 endTimestamp) {
            super(bestBid,bestBidder,status,endTimestamp);
            this.bestBid = bestBid.getValue();
            this.bestBidder = bestBidder.getValue();
            this.status = status.getValue();
            this.endTimestamp = endTimestamp.getValue();
        }
    }

    public static class ApprovalEventResponse extends BaseEventResponse {
        public String owner;

        public String spender;

        public BigInteger value;
    }

    public static class LiquidationBidEventResponse extends BaseEventResponse {
        public byte[] operatorPublicKeyHash;

        public String bidder;

        public BigInteger bid;
    }

    public static class NewTokenEventResponse extends BaseEventResponse {
        public String user;

        public BigInteger value;
    }

    public static class OperatorCollateralAuctionedEventResponse extends BaseEventResponse {
        public byte[] operatorPublicKeyHash;

        public String winner;

        public BigInteger tokensBurned;

        public BigInteger etherSold;
    }

    public static class OperatorLiquidatedEventResponse extends BaseEventResponse {
        public byte[] operatorPublicKeyHash;

        public BigInteger endTimestamp;
    }

    public static class TransferEventResponse extends BaseEventResponse {
        public String from;

        public String to;

        public BigInteger value;
    }

    public static class UnlockRequestEventResponse extends BaseEventResponse {
        public BigInteger id;

        public byte[] operatorPublicKeyHash;
    }
}
