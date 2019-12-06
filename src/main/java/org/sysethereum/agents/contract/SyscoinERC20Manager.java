package org.sysethereum.agents.contract;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicBytes;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint32;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
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
 * <p>Generated with web3j version 4.5.4.
 */
@SuppressWarnings("rawtypes")
public class SyscoinERC20Manager extends Contract {
    private static final String BINARY = "0x608060405234801561001057600080fd5b50610e28806100206000396000f3fe608060405234801561001057600080fd5b50600436106100935760003560e01c8063af56f15811610066578063af56f158146101de578063b52d521d14610236578063b85278a914610250578063cf496b1014610258578063fe2e97181461027c57610093565b806319ab453c1461009857806359caecb5146100c05780635f959b69146100dd578063a71d75ca146101c1575b600080fd5b6100be600480360360208110156100ae57600080fd5b50356001600160a01b031661029f565b005b6100be600480360360208110156100d657600080fd5b503561035d565b6101ad600480360360a08110156100f357600080fd5b81359163ffffffff602082013516916001600160a01b036040830135169160ff6060820135169181019060a08101608082013564010000000081111561013857600080fd5b82018360208201111561014a57600080fd5b8035906020019184600183028401116401000000008311171561016c57600080fd5b91908080601f01602080910402602001604051908101604052809392919081815260200183838082843760009201919091525092955061039b945050505050565b604080519115158252519081900360200190f35b6101ad600480360360208110156101d757600080fd5b50356106c9565b6100be600480360360e08110156101f457600080fd5b5080359060208101359060408101356001600160a01b039081169160608101358216916080820135169060a081013563ffffffff169060c0013560ff166106dc565b61023e610a41565b60408051918252519081900360200190f35b61023e610a46565b610260610a4c565b604080516001600160a01b039092168252519081900360200190f35b61023e6004803603602081101561029257600080fd5b503563ffffffff16610a5b565b600054610100900460ff16806102b857506102b8610a6d565b806102c6575060005460ff16155b6103015760405162461bcd60e51b815260040180806020018281038252602e815260200180610dc6602e913960400191505060405180910390fd5b600054610100900460ff1615801561032c576000805460ff1961ff0019909116610100171660011790555b603380546001600160a01b0319166001600160a01b0384161790558015610359576000805461ff00191690555b5050565b604080513381526020810183905281517fa564e0b43dde1bca7bbedfb3ac4676e13faa85bce6024c76b36867a89af9e795929181900390910190a150565b600083866000826001600160a01b031663313ce5676040518163ffffffff1660e01b815260040160206040518083038186803b1580156103da57600080fd5b505afa1580156103ee573d6000803e3d6000fd5b505050506040513d602081101561040457600080fd5b505160ff16905061041f600a82810a9063ffffffff610a7316565b82101561045d5760405162461bcd60e51b815260040180806020018281038252602c815260200180610d79602c913960400191505060405180910390fd5b60008551116104b3576040805162461bcd60e51b815260206004820152601d60248201527f737973636f696e416464726573732063616e6e6f74206265207a65726f000000604482015290519081900360640190fd5b60008863ffffffff161161050e576040805162461bcd60e51b815260206004820152601860248201527f41737365742047554944206d757374206e6f7420626520300000000000000000604482015290519081900360640190fd5b6000879050806001600160a01b031663313ce5676040518163ffffffff1660e01b815260040160206040518083038186803b15801561054c57600080fd5b505afa158015610560573d6000803e3d6000fd5b505050506040513d602081101561057657600080fd5b505160ff8881169116146105bb5760405162461bcd60e51b8152600401808060200182810382526031815260200180610d486031913960400191505060405180910390fd5b604080516323b872dd60e01b8152336004820152306024820152604481018c905290516001600160a01b038316916323b872dd9160648083019260209291908290030181600087803b15801561061057600080fd5b505af1158015610624573d6000803e3d6000fd5b505050506040513d602081101561063a57600080fd5b505063ffffffff808a1660009081526034602052604090205461065f918c90610abc16565b63ffffffff8a1660009081526034602090815260408083209390935582513381529081018d90528083019190915290517f9c6dea23fe3b510bb5d170df49dc74e387692eaa3258c691918cd3aa94f5fb749181900360600190a15060019998505050505050505050565b60006106d482610b16565b90505b919050565b6033546001600160a01b031633146107255760405162461bcd60e51b8152600401808060200182810382526021815260200180610da56021913960400191505060405180910390fd5b60008390506000816001600160a01b031663313ce5676040518163ffffffff1660e01b815260040160206040518083038186803b15801561076557600080fd5b505afa158015610779573d6000803e3d6000fd5b505050506040513d602081101561078f57600080fd5b5051905060ff80841690821611156107b35782810360ff16600a0a880297506107d6565b8260ff168160ff1610156107d65780830360ff16600a0a88816107d257fe5b0497505b6107e08189610b2b565b6107e989610bd1565b610831576040805162461bcd60e51b8152602060048201526014602482015273151608185b1c9958591e481c1c9bd8d95cdcd95960621b604482015290519081900360640190fd5b63ffffffff808516600090815260346020526040902054610854918a90610c0916565b63ffffffff80861660009081526034602052604081209290925561087e908a9061271090610a7316565b905060006108928a8363ffffffff610c0916565b9050836001600160a01b031663a9059cbb89846040518363ffffffff1660e01b815260040180836001600160a01b03166001600160a01b0316815260200182815260200192505050602060405180830381600087803b1580156108f457600080fd5b505af1158015610908573d6000803e3d6000fd5b505050506040513d602081101561091e57600080fd5b5050604080516001600160a01b038a1681526020810184905281517f378dbe173f6ed6e11630b29573f719ec4cefc9b49f430deed915911c5f78a080929181900390910190a1836001600160a01b031663a9059cbb8a836040518363ffffffff1660e01b815260040180836001600160a01b03166001600160a01b0316815260200182815260200192505050602060405180830381600087803b1580156109c457600080fd5b505af11580156109d8573d6000803e3d6000fd5b505050506040513d60208110156109ee57600080fd5b5050604080516001600160a01b038b1681526020810183905281517fb925ba840e2f36bcb317f8179bd8b5ed01aba4a22abf5f169162c0894dea87ab929181900390910190a15050505050505050505050565b600a81565b61271081565b6033546001600160a01b031681565b60346020526000908152604090205481565b303b1590565b6000610ab583836040518060400160405280601a81526020017f536166654d6174683a206469766973696f6e206279207a65726f000000000000815250610c4b565b9392505050565b600082820183811015610ab5576040805162461bcd60e51b815260206004820152601b60248201527f536166654d6174683a206164646974696f6e206f766572666c6f770000000000604482015290519081900360640190fd5b60009081526035602052604090205460ff1690565b60ff821681610b7a576040805162461bcd60e51b815260206004820152601660248201527556616c7565206d75737420626520706f73697469766560501b604482015290519081900360640190fd5b610b8e600a82810a9063ffffffff610a7316565b821015610bcc5760405162461bcd60e51b815260040180806020018281038252602c815260200180610d79602c913960400191505060405180910390fd5b505050565b6000610bdc82610b16565b15610be9575060006106d7565b506000908152603560205260409020805460ff1916600190811790915590565b6000610ab583836040518060400160405280601e81526020017f536166654d6174683a207375627472616374696f6e206f766572666c6f770000815250610ced565b60008183610cd75760405162461bcd60e51b81526004018080602001828103825283818151815260200191508051906020019080838360005b83811015610c9c578181015183820152602001610c84565b50505050905090810190601f168015610cc95780820380516001836020036101000a031916815260200191505b509250505060405180910390fd5b506000838581610ce357fe5b0495945050505050565b60008184841115610d3f5760405162461bcd60e51b8152602060048201818152835160248401528351909283926044909101919085019080838360008315610c9c578181015183820152602001610c84565b50505090039056fe446563696d616c732077657265206e6f742070726f766964656420776974682074686520636f72726563742076616c756556616c7565206d75737420626520626967676572206f7220657175616c204d494e5f4c4f434b5f56414c554543616c6c206d7573742062652066726f6d20747275737465642072656c61796572436f6e747261637420696e7374616e63652068617320616c7265616479206265656e20696e697469616c697a6564a265627a7a72315820cc31731882d757db4cdf289891b234d234b676ede53d9253cb3defa84fb7f5c564736f6c634300050d0032";

    public static final String FUNC_MIN_LOCK_VALUE = "MIN_LOCK_VALUE";

    public static final String FUNC_SUPERBLOCK_SUBMITTER_LOCK_FEE = "SUPERBLOCK_SUBMITTER_LOCK_FEE";

    public static final String FUNC_ASSETBALANCES = "assetBalances";

    public static final String FUNC_TRUSTEDRELAYERCONTRACT = "trustedRelayerContract";

    public static final String FUNC_INIT = "init";

    public static final String FUNC_WASSYSCOINTXPROCESSED = "wasSyscoinTxProcessed";

    public static final String FUNC_PROCESSTRANSACTION = "processTransaction";

    public static final String FUNC_CANCELTRANSFER = "cancelTransfer";

    public static final String FUNC_FREEZEBURNERC20 = "freezeBurnERC20";

    public static final Event CANCELTRANSFER_EVENT = new Event("CancelTransfer",
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event TOKENFREEZE_EVENT = new Event("TokenFreeze",
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event TOKENUNFREEZE_EVENT = new Event("TokenUnfreeze",
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event TOKENUNFREEZEFEE_EVENT = new Event("TokenUnfreezeFee",
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
    ;

    protected static final HashMap<String, String> _addresses;

    static {
        _addresses = new HashMap<String, String>();
        _addresses.put("4", "0x38945d8004cf4671c45686853452A6510812117c");
    }

    @Deprecated
    protected SyscoinERC20Manager(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected SyscoinERC20Manager(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected SyscoinERC20Manager(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected SyscoinERC20Manager(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public List<CancelTransferEventResponse> getCancelTransferEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(CANCELTRANSFER_EVENT, transactionReceipt);
        ArrayList<CancelTransferEventResponse> responses = new ArrayList<CancelTransferEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            CancelTransferEventResponse typedResponse = new CancelTransferEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.canceller = (Address) eventValues.getNonIndexedValues().get(0);
            typedResponse.bridgetransferid = (Uint256) eventValues.getNonIndexedValues().get(1);
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<CancelTransferEventResponse> cancelTransferEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, CancelTransferEventResponse>() {
            @Override
            public CancelTransferEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(CANCELTRANSFER_EVENT, log);
                CancelTransferEventResponse typedResponse = new CancelTransferEventResponse();
                typedResponse.log = log;
                typedResponse.canceller = (Address) eventValues.getNonIndexedValues().get(0);
                typedResponse.bridgetransferid = (Uint256) eventValues.getNonIndexedValues().get(1);
                return typedResponse;
            }
        });
    }

    public Flowable<CancelTransferEventResponse> cancelTransferEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(CANCELTRANSFER_EVENT));
        return cancelTransferEventFlowable(filter);
    }

    public List<TokenFreezeEventResponse> getTokenFreezeEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(TOKENFREEZE_EVENT, transactionReceipt);
        ArrayList<TokenFreezeEventResponse> responses = new ArrayList<TokenFreezeEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            TokenFreezeEventResponse typedResponse = new TokenFreezeEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.freezer = (Address) eventValues.getNonIndexedValues().get(0);
            typedResponse.value = (Uint256) eventValues.getNonIndexedValues().get(1);
            typedResponse.bridgetransferid = (Uint256) eventValues.getNonIndexedValues().get(2);
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<TokenFreezeEventResponse> tokenFreezeEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, TokenFreezeEventResponse>() {
            @Override
            public TokenFreezeEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(TOKENFREEZE_EVENT, log);
                TokenFreezeEventResponse typedResponse = new TokenFreezeEventResponse();
                typedResponse.log = log;
                typedResponse.freezer = (Address) eventValues.getNonIndexedValues().get(0);
                typedResponse.value = (Uint256) eventValues.getNonIndexedValues().get(1);
                typedResponse.bridgetransferid = (Uint256) eventValues.getNonIndexedValues().get(2);
                return typedResponse;
            }
        });
    }

    public Flowable<TokenFreezeEventResponse> tokenFreezeEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(TOKENFREEZE_EVENT));
        return tokenFreezeEventFlowable(filter);
    }

    public List<TokenUnfreezeEventResponse> getTokenUnfreezeEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(TOKENUNFREEZE_EVENT, transactionReceipt);
        ArrayList<TokenUnfreezeEventResponse> responses = new ArrayList<TokenUnfreezeEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            TokenUnfreezeEventResponse typedResponse = new TokenUnfreezeEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.receipient = (Address) eventValues.getNonIndexedValues().get(0);
            typedResponse.value = (Uint256) eventValues.getNonIndexedValues().get(1);
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<TokenUnfreezeEventResponse> tokenUnfreezeEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, TokenUnfreezeEventResponse>() {
            @Override
            public TokenUnfreezeEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(TOKENUNFREEZE_EVENT, log);
                TokenUnfreezeEventResponse typedResponse = new TokenUnfreezeEventResponse();
                typedResponse.log = log;
                typedResponse.receipient = (Address) eventValues.getNonIndexedValues().get(0);
                typedResponse.value = (Uint256) eventValues.getNonIndexedValues().get(1);
                return typedResponse;
            }
        });
    }

    public Flowable<TokenUnfreezeEventResponse> tokenUnfreezeEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(TOKENUNFREEZE_EVENT));
        return tokenUnfreezeEventFlowable(filter);
    }

    public List<TokenUnfreezeFeeEventResponse> getTokenUnfreezeFeeEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(TOKENUNFREEZEFEE_EVENT, transactionReceipt);
        ArrayList<TokenUnfreezeFeeEventResponse> responses = new ArrayList<TokenUnfreezeFeeEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            TokenUnfreezeFeeEventResponse typedResponse = new TokenUnfreezeFeeEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.receipient = (Address) eventValues.getNonIndexedValues().get(0);
            typedResponse.value = (Uint256) eventValues.getNonIndexedValues().get(1);
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<TokenUnfreezeFeeEventResponse> tokenUnfreezeFeeEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, TokenUnfreezeFeeEventResponse>() {
            @Override
            public TokenUnfreezeFeeEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(TOKENUNFREEZEFEE_EVENT, log);
                TokenUnfreezeFeeEventResponse typedResponse = new TokenUnfreezeFeeEventResponse();
                typedResponse.log = log;
                typedResponse.receipient = (Address) eventValues.getNonIndexedValues().get(0);
                typedResponse.value = (Uint256) eventValues.getNonIndexedValues().get(1);
                return typedResponse;
            }
        });
    }

    public Flowable<TokenUnfreezeFeeEventResponse> tokenUnfreezeFeeEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(TOKENUNFREEZEFEE_EVENT));
        return tokenUnfreezeFeeEventFlowable(filter);
    }

    public RemoteFunctionCall<Uint256> MIN_LOCK_VALUE() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_MIN_LOCK_VALUE,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public RemoteFunctionCall<Uint256> SUPERBLOCK_SUBMITTER_LOCK_FEE() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_SUPERBLOCK_SUBMITTER_LOCK_FEE,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public RemoteFunctionCall<Uint256> assetBalances(Uint32 param0) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_ASSETBALANCES,
                Arrays.<Type>asList(param0),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public RemoteFunctionCall<Address> trustedRelayerContract() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_TRUSTEDRELAYERCONTRACT,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public RemoteFunctionCall<TransactionReceipt> init(Address _trustedRelayerContract) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_INIT,
                Arrays.<Type>asList(_trustedRelayerContract),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Bool> wasSyscoinTxProcessed(Uint256 txHash) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_WASSYSCOINTXPROCESSED,
                Arrays.<Type>asList(txHash),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public RemoteFunctionCall<TransactionReceipt> processTransaction(Uint256 txHash, Uint256 value, Address destinationAddress, Address superblockSubmitterAddress, Address erc20ContractAddress, Uint32 assetGUID, Uint8 precision) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_PROCESSTRANSACTION,
                Arrays.<Type>asList(txHash, value, destinationAddress, superblockSubmitterAddress, erc20ContractAddress, assetGUID, precision),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> cancelTransfer(Uint256 bridgeTransferId) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_CANCELTRANSFER,
                Arrays.<Type>asList(bridgeTransferId),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> freezeBurnERC20(Uint256 value, Uint32 assetGUID, Address erc20ContractAddress, Uint8 precision, DynamicBytes syscoinAddress) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_FREEZEBURNERC20,
                Arrays.<Type>asList(value, assetGUID, erc20ContractAddress, precision, syscoinAddress),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static SyscoinERC20Manager load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new SyscoinERC20Manager(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static SyscoinERC20Manager load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new SyscoinERC20Manager(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static SyscoinERC20Manager load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new SyscoinERC20Manager(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static SyscoinERC20Manager load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new SyscoinERC20Manager(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<SyscoinERC20Manager> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(SyscoinERC20Manager.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<SyscoinERC20Manager> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(SyscoinERC20Manager.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<SyscoinERC20Manager> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(SyscoinERC20Manager.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<SyscoinERC20Manager> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(SyscoinERC20Manager.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    protected String getStaticDeployedAddress(String networkId) {
        return _addresses.get(networkId);
    }

    public static String getPreviouslyDeployedAddress(String networkId) {
        return _addresses.get(networkId);
    }

    public static class CancelTransferEventResponse extends BaseEventResponse {
        public Address canceller;

        public Uint256 bridgetransferid;
    }

    public static class TokenFreezeEventResponse extends BaseEventResponse {
        public Address freezer;

        public Uint256 value;

        public Uint256 bridgetransferid;
    }

    public static class TokenUnfreezeEventResponse extends BaseEventResponse {
        public Address receipient;

        public Uint256 value;
    }

    public static class TokenUnfreezeFeeEventResponse extends BaseEventResponse {
        public Address receipient;

        public Uint256 value;
    }
}
