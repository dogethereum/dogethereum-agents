package org.dogethereum.agents.contract;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.DynamicBytes;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple12;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import rx.Observable;
import rx.functions.Func1;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 3.3.1.
 */
public class DogeBattleManager extends Contract {
    private static final String BINARY = "0x";

    protected static final HashMap<String, String> _addresses;

    static {
        _addresses = new HashMap<>();
    }

    protected DogeBattleManager(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected DogeBattleManager(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public List<NewBattleEventResponse> getNewBattleEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("NewBattle", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}, new TypeReference<Address>() {}));
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(event, transactionReceipt);
        ArrayList<NewBattleEventResponse> responses = new ArrayList<NewBattleEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            NewBattleEventResponse typedResponse = new NewBattleEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.sessionId = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.submitter = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.challenger = (String) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<NewBattleEventResponse> newBattleEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("NewBattle", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}, new TypeReference<Address>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, NewBattleEventResponse>() {
            @Override
            public NewBattleEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);
                NewBattleEventResponse typedResponse = new NewBattleEventResponse();
                typedResponse.log = log;
                typedResponse.sessionId = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.submitter = (String) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.challenger = (String) eventValues.getNonIndexedValues().get(2).getValue();
                return typedResponse;
            }
        });
    }

    public List<ChallengerConvictedEventResponse> getChallengerConvictedEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("ChallengerConvicted", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}));
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(event, transactionReceipt);
        ArrayList<ChallengerConvictedEventResponse> responses = new ArrayList<ChallengerConvictedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ChallengerConvictedEventResponse typedResponse = new ChallengerConvictedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.sessionId = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.challenger = (String) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<ChallengerConvictedEventResponse> challengerConvictedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("ChallengerConvicted", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, ChallengerConvictedEventResponse>() {
            @Override
            public ChallengerConvictedEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);
                ChallengerConvictedEventResponse typedResponse = new ChallengerConvictedEventResponse();
                typedResponse.log = log;
                typedResponse.sessionId = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.challenger = (String) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public List<SubmitterConvictedEventResponse> getSubmitterConvictedEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("SubmitterConvicted", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}));
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(event, transactionReceipt);
        ArrayList<SubmitterConvictedEventResponse> responses = new ArrayList<SubmitterConvictedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            SubmitterConvictedEventResponse typedResponse = new SubmitterConvictedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.sessionId = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.submitter = (String) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<SubmitterConvictedEventResponse> submitterConvictedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("SubmitterConvicted", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, SubmitterConvictedEventResponse>() {
            @Override
            public SubmitterConvictedEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);
                SubmitterConvictedEventResponse typedResponse = new SubmitterConvictedEventResponse();
                typedResponse.log = log;
                typedResponse.sessionId = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.submitter = (String) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public List<QueryMerkleRootHashesEventResponse> getQueryMerkleRootHashesEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("QueryMerkleRootHashes", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}));
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(event, transactionReceipt);
        ArrayList<QueryMerkleRootHashesEventResponse> responses = new ArrayList<QueryMerkleRootHashesEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            QueryMerkleRootHashesEventResponse typedResponse = new QueryMerkleRootHashesEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.superblockId = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.sessionId = (byte[]) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.submitter = (String) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<QueryMerkleRootHashesEventResponse> queryMerkleRootHashesEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("QueryMerkleRootHashes", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, QueryMerkleRootHashesEventResponse>() {
            @Override
            public QueryMerkleRootHashesEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);
                QueryMerkleRootHashesEventResponse typedResponse = new QueryMerkleRootHashesEventResponse();
                typedResponse.log = log;
                typedResponse.superblockId = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.sessionId = (byte[]) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.submitter = (String) eventValues.getNonIndexedValues().get(2).getValue();
                return typedResponse;
            }
        });
    }

    public List<RespondMerkleRootHashesEventResponse> getRespondMerkleRootHashesEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("RespondMerkleRootHashes", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}, new TypeReference<DynamicArray<Bytes32>>() {}));
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(event, transactionReceipt);
        ArrayList<RespondMerkleRootHashesEventResponse> responses = new ArrayList<RespondMerkleRootHashesEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            RespondMerkleRootHashesEventResponse typedResponse = new RespondMerkleRootHashesEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.sessionId = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.challenger = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.blockHashes = (List<byte[]>) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<RespondMerkleRootHashesEventResponse> respondMerkleRootHashesEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("RespondMerkleRootHashes", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}, new TypeReference<DynamicArray<Bytes32>>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, RespondMerkleRootHashesEventResponse>() {
            @Override
            public RespondMerkleRootHashesEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);
                RespondMerkleRootHashesEventResponse typedResponse = new RespondMerkleRootHashesEventResponse();
                typedResponse.log = log;
                typedResponse.sessionId = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.challenger = (String) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.blockHashes = (List<byte[]>) eventValues.getNonIndexedValues().get(2).getValue();
                return typedResponse;
            }
        });
    }

    public List<QueryBlockHeaderEventResponse> getQueryBlockHeaderEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("QueryBlockHeader", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}, new TypeReference<Bytes32>() {}));
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(event, transactionReceipt);
        ArrayList<QueryBlockHeaderEventResponse> responses = new ArrayList<QueryBlockHeaderEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            QueryBlockHeaderEventResponse typedResponse = new QueryBlockHeaderEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.sessionId = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.submitter = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.blockHash = (byte[]) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<QueryBlockHeaderEventResponse> queryBlockHeaderEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("QueryBlockHeader", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}, new TypeReference<Bytes32>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, QueryBlockHeaderEventResponse>() {
            @Override
            public QueryBlockHeaderEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);
                QueryBlockHeaderEventResponse typedResponse = new QueryBlockHeaderEventResponse();
                typedResponse.log = log;
                typedResponse.sessionId = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.submitter = (String) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.blockHash = (byte[]) eventValues.getNonIndexedValues().get(2).getValue();
                return typedResponse;
            }
        });
    }

    public List<RespondBlockHeaderEventResponse> getRespondBlockHeaderEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("RespondBlockHeader", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}, new TypeReference<Bytes32>() {}, new TypeReference<DynamicBytes>() {}));
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(event, transactionReceipt);
        ArrayList<RespondBlockHeaderEventResponse> responses = new ArrayList<RespondBlockHeaderEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            RespondBlockHeaderEventResponse typedResponse = new RespondBlockHeaderEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.sessionId = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.challenger = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.blockScryptHash = (byte[]) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.blockHeader = (byte[]) eventValues.getNonIndexedValues().get(3).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<RespondBlockHeaderEventResponse> respondBlockHeaderEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("RespondBlockHeader", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}, new TypeReference<Bytes32>() {}, new TypeReference<DynamicBytes>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, RespondBlockHeaderEventResponse>() {
            @Override
            public RespondBlockHeaderEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);
                RespondBlockHeaderEventResponse typedResponse = new RespondBlockHeaderEventResponse();
                typedResponse.log = log;
                typedResponse.sessionId = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.challenger = (String) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.blockScryptHash = (byte[]) eventValues.getNonIndexedValues().get(2).getValue();
                typedResponse.blockHeader = (byte[]) eventValues.getNonIndexedValues().get(3).getValue();
                return typedResponse;
            }
        });
    }

    public List<ErrorBattleEventResponse> getErrorBattleEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("ErrorBattle", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Uint256>() {}));
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(event, transactionReceipt);
        ArrayList<ErrorBattleEventResponse> responses = new ArrayList<ErrorBattleEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ErrorBattleEventResponse typedResponse = new ErrorBattleEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.sessionId = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.err = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<ErrorBattleEventResponse> errorBattleEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("ErrorBattle", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, ErrorBattleEventResponse>() {
            @Override
            public ErrorBattleEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);
                ErrorBattleEventResponse typedResponse = new ErrorBattleEventResponse();
                typedResponse.log = log;
                typedResponse.sessionId = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.err = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public RemoteCall<BigInteger> superblockTimeout() {
        final Function function = new Function("superblockTimeout", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> sessionsCount() {
        final Function function = new Function("sessionsCount", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> superblockDelay() {
        final Function function = new Function("superblockDelay", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> numScryptHashVerifications() {
        final Function function = new Function("numScryptHashVerifications", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<Tuple12<byte[], byte[], String, String, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, byte[], BigInteger>> sessions(byte[] param0) {
        final Function function = new Function("sessions", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}, new TypeReference<Address>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Uint8>() {}));
        return new RemoteCall<Tuple12<byte[], byte[], String, String, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, byte[], BigInteger>>(
                new Callable<Tuple12<byte[], byte[], String, String, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, byte[], BigInteger>>() {
                    @Override
                    public Tuple12<byte[], byte[], String, String, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, byte[], BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple12<byte[], byte[], String, String, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, byte[], BigInteger>(
                                (byte[]) results.get(0).getValue(), 
                                (byte[]) results.get(1).getValue(), 
                                (String) results.get(2).getValue(), 
                                (String) results.get(3).getValue(), 
                                (BigInteger) results.get(4).getValue(), 
                                (BigInteger) results.get(5).getValue(), 
                                (BigInteger) results.get(6).getValue(), 
                                (BigInteger) results.get(7).getValue(), 
                                (BigInteger) results.get(8).getValue(), 
                                (BigInteger) results.get(9).getValue(), 
                                (byte[]) results.get(10).getValue(), 
                                (BigInteger) results.get(11).getValue());
                    }
                });
    }

    public RemoteCall<Tuple2<byte[], byte[]>> scryptHashVerifications(byte[] param0) {
        final Function function = new Function("scryptHashVerifications", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}));
        return new RemoteCall<Tuple2<byte[], byte[]>>(
                new Callable<Tuple2<byte[], byte[]>>() {
                    @Override
                    public Tuple2<byte[], byte[]> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple2<byte[], byte[]>(
                                (byte[]) results.get(0).getValue(), 
                                (byte[]) results.get(1).getValue());
                    }
                });
    }

    public RemoteCall<BigInteger> superblockDuration() {
        final Function function = new Function("superblockDuration", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public static RemoteCall<DogeBattleManager> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, BigInteger _network, BigInteger _superblockDuration, BigInteger _superblockDelay, BigInteger _superblockTimeout) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint8(_network), 
                new org.web3j.abi.datatypes.generated.Uint256(_superblockDuration), 
                new org.web3j.abi.datatypes.generated.Uint256(_superblockDelay), 
                new org.web3j.abi.datatypes.generated.Uint256(_superblockTimeout)));
        return deployRemoteCall(DogeBattleManager.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static RemoteCall<DogeBattleManager> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, BigInteger _network, BigInteger _superblockDuration, BigInteger _superblockDelay, BigInteger _superblockTimeout) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint8(_network), 
                new org.web3j.abi.datatypes.generated.Uint256(_superblockDuration), 
                new org.web3j.abi.datatypes.generated.Uint256(_superblockDelay), 
                new org.web3j.abi.datatypes.generated.Uint256(_superblockTimeout)));
        return deployRemoteCall(DogeBattleManager.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public RemoteCall<TransactionReceipt> beginBattleSession(byte[] superblockId, String submitter, String challenger) {
        final Function function = new Function(
                "beginBattleSession", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(superblockId), 
                new org.web3j.abi.datatypes.Address(submitter), 
                new org.web3j.abi.datatypes.Address(challenger)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> queryMerkleRootHashes(byte[] superblockId, byte[] sessionId) {
        final Function function = new Function(
                "queryMerkleRootHashes", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(superblockId), 
                new org.web3j.abi.datatypes.generated.Bytes32(sessionId)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> respondMerkleRootHashes(byte[] sessionId, List<byte[]> blockHashes) {
        final Function function = new Function(
                "respondMerkleRootHashes", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(sessionId), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Bytes32>(
                        org.web3j.abi.Utils.typeMap(blockHashes, org.web3j.abi.datatypes.generated.Bytes32.class))), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> queryBlockHeader(byte[] sessionId, byte[] blockHash) {
        final Function function = new Function(
                "queryBlockHeader", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(sessionId), 
                new org.web3j.abi.datatypes.generated.Bytes32(blockHash)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> respondBlockHeader(byte[] sessionId, byte[] blockScryptHash, byte[] blockHeader) {
        final Function function = new Function(
                "respondBlockHeader", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(sessionId), 
                new org.web3j.abi.datatypes.generated.Bytes32(blockScryptHash), 
                new org.web3j.abi.datatypes.DynamicBytes(blockHeader)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> verifySuperblock(byte[] sessionId) {
        final Function function = new Function(
                "verifySuperblock", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(sessionId)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> timeout(byte[] sessionId) {
        final Function function = new Function(
                "timeout", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(sessionId)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public static DogeBattleManager load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new DogeBattleManager(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static DogeBattleManager load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new DogeBattleManager(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected String getStaticDeployedAddress(String networkId) {
        return _addresses.get(networkId);
    }

    public static String getPreviouslyDeployedAddress(String networkId) {
        return _addresses.get(networkId);
    }

    public static class NewBattleEventResponse {
        public Log log;

        public byte[] sessionId;

        public String submitter;

        public String challenger;
    }

    public static class ChallengerConvictedEventResponse {
        public Log log;

        public byte[] sessionId;

        public String challenger;
    }

    public static class SubmitterConvictedEventResponse {
        public Log log;

        public byte[] sessionId;

        public String submitter;
    }

    public static class QueryMerkleRootHashesEventResponse {
        public Log log;

        public byte[] superblockId;

        public byte[] sessionId;

        public String submitter;
    }

    public static class RespondMerkleRootHashesEventResponse {
        public Log log;

        public byte[] sessionId;

        public String challenger;

        public List<byte[]> blockHashes;
    }

    public static class QueryBlockHeaderEventResponse {
        public Log log;

        public byte[] sessionId;

        public String submitter;

        public byte[] blockHash;
    }

    public static class RespondBlockHeaderEventResponse {
        public Log log;

        public byte[] sessionId;

        public String challenger;

        public byte[] blockScryptHash;

        public byte[] blockHeader;
    }

    public static class ErrorBattleEventResponse {
        public Log log;

        public byte[] sessionId;

        public BigInteger err;
    }
}
