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
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes20;
import org.web3j.abi.datatypes.generated.Uint16;
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
import org.web3j.tuples.generated.Tuple6;
import org.web3j.tuples.generated.Tuple7;
import org.web3j.tuples.generated.Tuple8;
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

    public static final String FUNC_MIN_LOCK_VALUE = "MIN_LOCK_VALUE";

    public static final String FUNC_MIN_UNLOCK_VALUE = "MIN_UNLOCK_VALUE";

    public static final String FUNC_OPERATOR_LOCK_FEE = "OPERATOR_LOCK_FEE";

    public static final String FUNC_OPERATOR_UNLOCK_FEE = "OPERATOR_UNLOCK_FEE";

    public static final String FUNC_SUPERBLOCK_SUBMITTER_LOCK_FEE = "SUPERBLOCK_SUBMITTER_LOCK_FEE";

    public static final String FUNC_ADDOPERATOR = "addOperator";

    public static final String FUNC_ADDOPERATORDEPOSIT = "addOperatorDeposit";

    public static final String FUNC_ALLOWANCE = "allowance";

    public static final String FUNC_APPROVE = "approve";

    public static final String FUNC_APPROVEANDCALL = "approveAndCall";

    public static final String FUNC_BALANCEOF = "balanceOf";

    public static final String FUNC_COLLATERALRATIO = "collateralRatio";

    public static final String FUNC_DECIMALS = "decimals";

    public static final String FUNC_DELETEOPERATOR = "deleteOperator";

    public static final String FUNC_DOUNLOCK = "doUnlock";

    public static final String FUNC_DOGEETHPRICE = "dogeEthPrice";

    public static final String FUNC_GETOPERATORSLENGTH = "getOperatorsLength";

    public static final String FUNC_GETUNLOCKPENDINGINVESTORPROOF = "getUnlockPendingInvestorProof";

    public static final String FUNC_GETUTXO = "getUtxo";

    public static final String FUNC_GETUTXOSLENGTH = "getUtxosLength";

    public static final String FUNC_NAME = "name";

    public static final String FUNC_OPERATORKEYS = "operatorKeys";

    public static final String FUNC_OPERATORS = "operators";

    public static final String FUNC_PROCESSTRANSACTION = "processTransaction";

    public static final String FUNC_SETDOGEETHPRICE = "setDogeEthPrice";

    public static final String FUNC_SYMBOL = "symbol";

    public static final String FUNC_TOTALSUPPLY = "totalSupply";

    public static final String FUNC_TRANSFER = "transfer";

    public static final String FUNC_TRANSFERFROM = "transferFrom";

    public static final String FUNC_TRUSTEDDOGEETHPRICEORACLE = "trustedDogeEthPriceOracle";

    public static final String FUNC_TRUSTEDRELAYERCONTRACT = "trustedRelayerContract";

    public static final String FUNC_UNLOCKIDX = "unlockIdx";

    public static final String FUNC_UNLOCKSPENDINGINVESTORPROOF = "unlocksPendingInvestorProof";

    public static final String FUNC_VERSION = "version";

    public static final String FUNC_WASDOGETXPROCESSED = "wasDogeTxProcessed";

    public static final String FUNC_WITHDRAWOPERATORDEPOSIT = "withdrawOperatorDeposit";

    public static final Event APPROVAL_EVENT = new Event("Approval", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event ERRORDOGETOKEN_EVENT = new Event("ErrorDogeToken", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    public static final Event NEWTOKEN_EVENT = new Event("NewToken", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event TRANSFER_EVENT = new Event("Transfer", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event UNLOCKREQUEST_EVENT = new Event("UnlockRequest", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint32>() {}, new TypeReference<Bytes20>() {}));
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
            typedResponse._owner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._spender = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse._value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
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
                typedResponse._owner = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._spender = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse._value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<ApprovalEventResponse> approvalEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(APPROVAL_EVENT));
        return approvalEventFlowable(filter);
    }

    public List<ErrorDogeTokenEventResponse> getErrorDogeTokenEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(ERRORDOGETOKEN_EVENT, transactionReceipt);
        ArrayList<ErrorDogeTokenEventResponse> responses = new ArrayList<ErrorDogeTokenEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ErrorDogeTokenEventResponse typedResponse = new ErrorDogeTokenEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.err = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<ErrorDogeTokenEventResponse> errorDogeTokenEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, ErrorDogeTokenEventResponse>() {
            @Override
            public ErrorDogeTokenEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(ERRORDOGETOKEN_EVENT, log);
                ErrorDogeTokenEventResponse typedResponse = new ErrorDogeTokenEventResponse();
                typedResponse.log = log;
                typedResponse.err = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<ErrorDogeTokenEventResponse> errorDogeTokenEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(ERRORDOGETOKEN_EVENT));
        return errorDogeTokenEventFlowable(filter);
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

    public List<TransferEventResponse> getTransferEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(TRANSFER_EVENT, transactionReceipt);
        ArrayList<TransferEventResponse> responses = new ArrayList<TransferEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            TransferEventResponse typedResponse = new TransferEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._from = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._to = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse._value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
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
                typedResponse._from = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._to = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse._value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
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

    public RemoteFunctionCall<BigInteger> allowance(String _owner, String _spender) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_ALLOWANCE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_owner), 
                new org.web3j.abi.datatypes.Address(_spender)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> approve(String _spender, BigInteger _value) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_APPROVE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_spender), 
                new org.web3j.abi.datatypes.generated.Uint256(_value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> approveAndCall(String _spender, BigInteger _value, byte[] _extraData) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_APPROVEANDCALL, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_spender), 
                new org.web3j.abi.datatypes.generated.Uint256(_value), 
                new org.web3j.abi.datatypes.DynamicBytes(_extraData)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> balanceOf(String _owner) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_BALANCEOF, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_owner)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> collateralRatio() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_COLLATERALRATIO, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
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

    public RemoteFunctionCall<BigInteger> getOperatorsLength() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETOPERATORSLENGTH, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint24>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<Tuple8<String, byte[], BigInteger, BigInteger, BigInteger, List<BigInteger>, BigInteger, byte[]>> getUnlockPendingInvestorProof(BigInteger index) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETUNLOCKPENDINGINVESTORPROOF, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint32(index)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Bytes20>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<DynamicArray<Uint32>>() {}, new TypeReference<Uint256>() {}, new TypeReference<Bytes20>() {}));
        return new RemoteFunctionCall<Tuple8<String, byte[], BigInteger, BigInteger, BigInteger, List<BigInteger>, BigInteger, byte[]>>(function,
                new Callable<Tuple8<String, byte[], BigInteger, BigInteger, BigInteger, List<BigInteger>, BigInteger, byte[]>>() {
                    @Override
                    public Tuple8<String, byte[], BigInteger, BigInteger, BigInteger, List<BigInteger>, BigInteger, byte[]> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple8<String, byte[], BigInteger, BigInteger, BigInteger, List<BigInteger>, BigInteger, byte[]>(
                                (String) results.get(0).getValue(), 
                                (byte[]) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue(), 
                                (BigInteger) results.get(3).getValue(), 
                                (BigInteger) results.get(4).getValue(), 
                                convertToNative((List<Uint32>) results.get(5).getValue()), 
                                (BigInteger) results.get(6).getValue(), 
                                (byte[]) results.get(7).getValue());
                    }
                });
    }

    public RemoteFunctionCall<Tuple3<BigInteger, BigInteger, BigInteger>> getUtxo(byte[] operatorPublicKeyHash, BigInteger i) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETUTXO, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes20(operatorPublicKeyHash), 
                new org.web3j.abi.datatypes.generated.Uint256(i)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint16>() {}));
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

    public RemoteFunctionCall<Tuple6<String, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger>> operators(byte[] param0) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_OPERATORS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes20(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint32>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint24>() {}));
        return new RemoteFunctionCall<Tuple6<String, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger>>(function,
                new Callable<Tuple6<String, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger>>() {
                    @Override
                    public Tuple6<String, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple6<String, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger>(
                                (String) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue(), 
                                (BigInteger) results.get(3).getValue(), 
                                (BigInteger) results.get(4).getValue(), 
                                (BigInteger) results.get(5).getValue());
                    }
                });
    }

    public RemoteFunctionCall<TransactionReceipt> processTransaction(byte[] dogeTx, BigInteger txHash, byte[] operatorPublicKeyHash, String superblockSubmitterAddress) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_PROCESSTRANSACTION, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicBytes(dogeTx), 
                new org.web3j.abi.datatypes.generated.Uint256(txHash), 
                new org.web3j.abi.datatypes.generated.Bytes20(operatorPublicKeyHash), 
                new org.web3j.abi.datatypes.Address(superblockSubmitterAddress)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> setDogeEthPrice(BigInteger _dogeEthPrice) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_SETDOGEETHPRICE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_dogeEthPrice)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
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

    public RemoteFunctionCall<TransactionReceipt> transfer(String _to, BigInteger _value) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_TRANSFER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_to), 
                new org.web3j.abi.datatypes.generated.Uint256(_value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> transferFrom(String _from, String _to, BigInteger _value) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_TRANSFERFROM, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_from), 
                new org.web3j.abi.datatypes.Address(_to), 
                new org.web3j.abi.datatypes.generated.Uint256(_value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> trustedDogeEthPriceOracle() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_TRUSTEDDOGEETHPRICEORACLE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
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
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint32>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<Tuple7<String, byte[], BigInteger, BigInteger, BigInteger, BigInteger, byte[]>> unlocksPendingInvestorProof(BigInteger param0) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_UNLOCKSPENDINGINVESTORPROOF, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint32(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Bytes20>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Bytes20>() {}));
        return new RemoteFunctionCall<Tuple7<String, byte[], BigInteger, BigInteger, BigInteger, BigInteger, byte[]>>(function,
                new Callable<Tuple7<String, byte[], BigInteger, BigInteger, BigInteger, BigInteger, byte[]>>() {
                    @Override
                    public Tuple7<String, byte[], BigInteger, BigInteger, BigInteger, BigInteger, byte[]> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple7<String, byte[], BigInteger, BigInteger, BigInteger, BigInteger, byte[]>(
                                (String) results.get(0).getValue(), 
                                (byte[]) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue(), 
                                (BigInteger) results.get(3).getValue(), 
                                (BigInteger) results.get(4).getValue(), 
                                (BigInteger) results.get(5).getValue(), 
                                (byte[]) results.get(6).getValue());
                    }
                });
    }

    public RemoteFunctionCall<String> version() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_VERSION, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
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

    public static class ApprovalEventResponse extends BaseEventResponse {
        public String _owner;

        public String _spender;

        public BigInteger _value;
    }

    public static class ErrorDogeTokenEventResponse extends BaseEventResponse {
        public BigInteger err;
    }

    public static class NewTokenEventResponse extends BaseEventResponse {
        public String user;

        public BigInteger value;
    }

    public static class TransferEventResponse extends BaseEventResponse {
        public String _from;

        public String _to;

        public BigInteger _value;
    }

    public static class UnlockRequestEventResponse extends BaseEventResponse {
        public BigInteger id;

        public byte[] operatorPublicKeyHash;
    }
}
