package org.dogethereum.agents.contract;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicBytes;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple4;
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
public class ClaimManager extends Contract {
    public static final String BINARY = "Bin file was not provided";

    public static final String FUNC_ACCEPTALL = "acceptAll";

    public static final String FUNC_CHECKSCRYPT = "checkScrypt";

    public static final String FUNC_HASHSTORAGE = "hashStorage";

    public static final String FUNC_PENDINGREQUESTS = "pendingRequests";

    public static final String FUNC_SENDFAILED = "sendFailed";

    public static final String FUNC_SENDVERIFICATION = "sendVerification";

    public static final String FUNC_STORESCRYPTHASH = "storeScryptHash";

    @Deprecated
    protected ClaimManager(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected ClaimManager(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected ClaimManager(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected ClaimManager(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<Boolean> acceptAll() {
        final Function function = new Function(FUNC_ACCEPTALL, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<TransactionReceipt> checkScrypt(byte[] _data, byte[] _hash, byte[] _proposalId, String _scryptDependent, BigInteger weiValue) {
        final Function function = new Function(
                FUNC_CHECKSCRYPT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicBytes(_data), 
                new org.web3j.abi.datatypes.generated.Bytes32(_hash), 
                new org.web3j.abi.datatypes.generated.Bytes32(_proposalId), 
                new org.web3j.abi.datatypes.Address(_scryptDependent)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteFunctionCall<byte[]> hashStorage(byte[] param0) {
        final Function function = new Function(FUNC_HASHSTORAGE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteFunctionCall<Tuple4<byte[], byte[], String, byte[]>> pendingRequests(byte[] param0) {
        final Function function = new Function(FUNC_PENDINGREQUESTS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicBytes>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}, new TypeReference<Bytes32>() {}));
        return new RemoteFunctionCall<Tuple4<byte[], byte[], String, byte[]>>(function,
                new Callable<Tuple4<byte[], byte[], String, byte[]>>() {
                    @Override
                    public Tuple4<byte[], byte[], String, byte[]> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple4<byte[], byte[], String, byte[]>(
                                (byte[]) results.get(0).getValue(), 
                                (byte[]) results.get(1).getValue(), 
                                (String) results.get(2).getValue(), 
                                (byte[]) results.get(3).getValue());
                    }
                });
    }

    public RemoteFunctionCall<TransactionReceipt> sendFailed(byte[] _hash, String _scryptDependent) {
        final Function function = new Function(
                FUNC_SENDFAILED, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_hash), 
                new org.web3j.abi.datatypes.Address(_scryptDependent)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> sendVerification(byte[] _hash, String _scryptDependent) {
        final Function function = new Function(
                FUNC_SENDVERIFICATION, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_hash), 
                new org.web3j.abi.datatypes.Address(_scryptDependent)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> storeScryptHash(byte[] _data, byte[] _hash) {
        final Function function = new Function(
                FUNC_STORESCRYPTHASH, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicBytes(_data), 
                new org.web3j.abi.datatypes.generated.Bytes32(_hash)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static ClaimManager load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new ClaimManager(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static ClaimManager load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new ClaimManager(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static ClaimManager load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new ClaimManager(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static ClaimManager load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new ClaimManager(contractAddress, web3j, transactionManager, contractGasProvider);
    }
}
