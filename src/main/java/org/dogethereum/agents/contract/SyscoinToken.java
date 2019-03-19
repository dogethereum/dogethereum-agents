package org.dogethereum.agents.contract;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicBytes;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint32;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;
import rx.Observable;
import rx.functions.Func1;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 3.6.0.
 */
public class SyscoinToken extends Contract {
    private static final String BINARY = "0x60c0604052600460808190527f48302e310000000000000000000000000000000000000000000000000000000060a090815262000040916006919062000180565b503480156200004e57600080fd5b50604051604080620013c283398101604081815282516020938401518284018352600c8085527f537973636f696e546f6b656e0000000000000000000000000000000000000000868601908152845180860186529182527f535953434f494e544f4b454e000000000000000000000000000000000000000082880152336000908152600190975293862086905585805584519295919491939192600892620000fa916003919062000180565b506004805460ff191660ff841617905580516200011f90600590602084019062000180565b50506007805463ffffffff909516740100000000000000000000000000000000000000000260a060020a63ffffffff0219600160a060020a03909716600160a060020a03199096169590951795909516939093179093555062000225915050565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f10620001c357805160ff1916838001178555620001f3565b82800160010185558215620001f3579182015b82811115620001f3578251825591602001919060010190620001d6565b506200020192915062000205565b5090565b6200022291905b808211156200020157600081556001016200020c565b90565b61118d80620002356000396000f3006080604052600436106100ed5763ffffffff60e060020a60003504166305347cdc81146100f257806306fdde0314610119578063095ea7b3146101a357806318160ddd146101db57806323b872dd146101f0578063285c5bc61461021a578063313ce5671461027357806331f3017b1461029e57806354fd4d50146102db57806370a08231146102f057806381ffaa031461031157806395d89b411461033f578063a71d75ca14610354578063a9059cbb1461036c578063b52d521d146100f2578063b85278a914610390578063cae9ca51146103a5578063cf496b101461040e578063dd62ed3e1461043f575b600080fd5b3480156100fe57600080fd5b50610107610466565b60408051918252519081900360200190f35b34801561012557600080fd5b5061012e61046e565b6040805160208082528351818301528351919283929083019185019080838360005b83811015610168578181015183820152602001610150565b50505050905090810190601f1680156101955780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b3480156101af57600080fd5b506101c7600160a060020a03600435166024356104fc565b604080519115158252519081900360200190f35b3480156101e757600080fd5b50610107610563565b3480156101fc57600080fd5b506101c7600160a060020a0360043581169060243516604435610569565b604080516020600460443581810135601f81018490048402850184019095528484526101c7948235946024803563ffffffff16953695946064949201919081908401838280828437509497506106b49650505050505050565b34801561027f57600080fd5b50610288610858565b6040805160ff9092168252519081900360200190f35b3480156102aa57600080fd5b50610107600435602435600160a060020a0360443581169063ffffffff606435169060843581169060a43516610861565b3480156102e757600080fd5b5061012e610b2e565b3480156102fc57600080fd5b50610107600160a060020a0360043516610b89565b34801561031d57600080fd5b50610326610ba4565b6040805163ffffffff9092168252519081900360200190f35b34801561034b57600080fd5b5061012e610bc8565b34801561036057600080fd5b506101c7600435610c23565b34801561037857600080fd5b506101c7600160a060020a0360043516602435610cb2565b34801561039c57600080fd5b50610107610d6c565b3480156103b157600080fd5b50604080516020600460443581810135601f81018490048402850184019095528484526101c7948235600160a060020a0316946024803595369594606494920191908190840183828082843750949750610d719650505050505050565b34801561041a57600080fd5b50610423610f0c565b60408051600160a060020a039092168252519081900360200190f35b34801561044b57600080fd5b50610107600160a060020a0360043581169060243516610f1b565b6311e1a30081565b6003805460408051602060026001851615610100026000190190941693909304601f810184900484028201840190925281815292918301828280156104f45780601f106104c9576101008083540402835291602001916104f4565b820191906000526020600020905b8154815290600101906020018083116104d757829003601f168201915b505050505081565b336000818152600260209081526040808320600160a060020a038716808552908352818420869055815186815291519394909390927f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925928290030190a35060015b92915050565b60005481565b600160a060020a03831660009081526001602052604081205482118015906105b45750600160a060020a03841660009081526002602090815260408083203384529091529020548211155b15156105bf57600080fd5b600160a060020a0383166000908152600160205260409020546105e8908363ffffffff610f4616565b600160a060020a03808516600090815260016020526040808220939093559086168152205461061d908363ffffffff610f5316565b600160a060020a038516600090815260016020908152604080832093909355600281528282203383529052205461065a908363ffffffff610f5316565b600160a060020a0380861660008181526002602090815260408083203384528252918290209490945580518681529051928716939192600080516020611122833981519152929181900390910190a35060015b9392505050565b60075460009063ffffffff848116740100000000000000000000000000000000000000009092041614610709576040805161eac4815290516000805160206111428339815191529181900360200190a16106ad565b6311e1a30084101561073d576040805161eab0815290516000805160206111428339815191529181900360200190a16106ad565b3360009081526001602052604090205484111561077c576040805161eaba815290516000805160206111428339815191529181900360200190a16106ad565b3360008181526001602090815260408083208054899003905582548890038355805188815263ffffffff881681840152606091810182815287519282019290925286517f496f2146a902cabfc1b17d4cd1de13001dc9a8e792c8f89ecb7eb3c941bf8344948a948a948a949390926080850192860191908190849084905b838110156108125781810151838201526020016107fa565b50505050905090810190601f16801561083f5780820380516001836020036101000a031916815260200191505b5094505050505060405180910390a25060019392505050565b60045460ff1681565b6007546000908190600160a060020a0316331461087d57600080fd5b60075463ffffffff8681167401000000000000000000000000000000000000000090920416146108cf576040805161eac4815290516000805160206111428339815191529181900360200190a1610b23565b600160a060020a038416158015906109fb5750604080516c01000000000000000000000000300260208083019190915282518083036014018152603490920192839052815191929182918401908083835b6020831061093f5780518252601f199092019160209182019101610920565b51815160209384036101000a600019018019909216911617905260408051929094018290038220600160a060020a038b166c0100000000000000000000000002838301528451601481850301815260349093019485905282519096509194508392508401908083835b602083106109c75780518252601f1990920191602091820191016109a8565b6001836020036101000a03801982511681845116808217855250505050505090500191505060405180910390206000191614155b15610a28576040805161eace815290516000805160206111428339815191529181900360200190a1610b23565b73__Set___________________________________63831cb73960088a6040518363ffffffff1660e060020a028152600401808381526020018281526020019250505060206040518083038186803b158015610a8357600080fd5b505af4158015610a97573d6000803e3d6000fd5b505050506040513d6020811015610aad57600080fd5b50519050801515610ae0576040805161eaa6815290516000805160206111428339815191529181900360200190a1610b23565b6311e1a300871015610b14576040805161eb14815290516000805160206111428339815191529181900360200190a1610b23565b610b1f868885610f65565b8691505b509695505050505050565b6006805460408051602060026001851615610100026000190190941693909304601f810184900484028201840190925281815292918301828280156104f45780601f106104c9576101008083540402835291602001916104f4565b600160a060020a031660009081526001602052604090205490565b60075474010000000000000000000000000000000000000000900463ffffffff1681565b6005805460408051602060026001851615610100026000190190941693909304601f810184900484028201840190925281815292918301828280156104f45780601f106104c9576101008083540402835291602001916104f4565b600073__Set___________________________________636ce8e0816008846040518363ffffffff1660e060020a028152600401808381526020018281526020019250505060206040518083038186803b158015610c8057600080fd5b505af4158015610c94573d6000803e3d6000fd5b505050506040513d6020811015610caa57600080fd5b505192915050565b33600090815260016020526040812054821115610cce57600080fd5b33600090815260016020526040902054610cee908363ffffffff610f5316565b3360009081526001602052604080822092909255600160a060020a03851681522054610d20908363ffffffff610f4616565b600160a060020a0384166000818152600160209081526040918290209390935580518581529051919233926000805160206111228339815191529281900390910190a350600192915050565b600181565b336000818152600260209081526040808320600160a060020a038816808552908352818420879055815187815291519394909390927f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925928290030190a383600160a060020a031660405180807f72656365697665417070726f76616c28616464726573732c75696e743235362c81526020017f616464726573732c627974657329000000000000000000000000000000000000815250602e019050604051809103902060e060020a9004338530866040518563ffffffff1660e060020a0281526004018085600160a060020a0316600160a060020a0316815260200184815260200183600160a060020a0316600160a060020a03168152602001828051906020019080838360005b83811015610eb1578181015183820152602001610e99565b50505050905090810190601f168015610ede5780820380516001836020036101000a031916815260200191505b509450505050506000604051808303816000875af1925050501515610f0257600080fd5b5060019392505050565b600754600160a060020a031681565b600160a060020a03918216600090815260026020908152604080832093909416825291909152205490565b8181018281101561055d57fe5b600082821115610f5f57fe5b50900390565b6000806103e8610f7c85600163ffffffff6110f816565b811515610f8557fe5b600160a060020a0385166000908152600160205260409020549190049250610fb3908363ffffffff610f4616565b600160a060020a038416600081815260016020908152604091829020939093558051858152905191927f275a11e033325e9b28a23131fcc7e243a49bb38beb686051976dd8c8899d9a3c92918290030190a2604080518381529051600160a060020a038516916000916000805160206111228339815191529181900360200190a3611044848363ffffffff610f5316565b600160a060020a038616600090815260016020526040902054909150611070908263ffffffff610f4616565b600160a060020a038616600081815260016020908152604091829020939093558051848152905191927f275a11e033325e9b28a23131fcc7e243a49bb38beb686051976dd8c8899d9a3c92918290030190a2604080518281529051600160a060020a038716916000916000805160206111228339815191529181900360200190a35050505050565b60008215156111095750600061055d565b5081810281838281151561111957fe5b041461055d57fe00ddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef6932a67ab00ac3e6c2e9cbca2fcf65a6a3fb95f710e81b2cac4811e66e78ff7ea165627a7a723058200ece4049ae75539db36c1d545aa3d93849142c97880dce2a4d734527f6fa828e0029";

    public static final String FUNC_MIN_UNLOCK_VALUE = "MIN_UNLOCK_VALUE";

    public static final String FUNC_NAME = "name";

    public static final String FUNC_APPROVE = "approve";

    public static final String FUNC_TOTALSUPPLY = "totalSupply";

    public static final String FUNC_TRANSFERFROM = "transferFrom";

    public static final String FUNC_DECIMALS = "decimals";

    public static final String FUNC_VERSION = "version";

    public static final String FUNC_BALANCEOF = "balanceOf";

    public static final String FUNC_ASSETGUID = "assetGUID";

    public static final String FUNC_SYMBOL = "symbol";

    public static final String FUNC_TRANSFER = "transfer";

    public static final String FUNC_MIN_LOCK_VALUE = "MIN_LOCK_VALUE";

    public static final String FUNC_SUPERBLOCK_SUBMITTER_LOCK_FEE = "SUPERBLOCK_SUBMITTER_LOCK_FEE";

    public static final String FUNC_APPROVEANDCALL = "approveAndCall";

    public static final String FUNC_TRUSTEDRELAYERCONTRACT = "trustedRelayerContract";

    public static final String FUNC_ALLOWANCE = "allowance";

    public static final String FUNC_WASSYSCOINTXPROCESSED = "wasSyscoinTxProcessed";

    public static final String FUNC_PROCESSTRANSACTION = "processTransaction";

    public static final String FUNC_BURN = "burn";

    public static final Event ERRORSYSCOINTOKEN_EVENT = new Event("ErrorSyscoinToken", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    public static final Event NEWTOKEN_EVENT = new Event("NewToken", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event BURN_EVENT = new Event("Burn", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}, new TypeReference<Uint32>() {}, new TypeReference<DynamicBytes>() {}));
    ;

    public static final Event TRANSFER_EVENT = new Event("Transfer", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event APPROVAL_EVENT = new Event("Approval", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    protected static final HashMap<String, String> _addresses;

    static {
        _addresses = new HashMap<String, String>();
        _addresses.put("32001", "0xd4360fbdf5896ef67158d95242dd4247650283b7");
    }

    @Deprecated
    protected SyscoinToken(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected SyscoinToken(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected SyscoinToken(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected SyscoinToken(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteCall<Uint256> MIN_UNLOCK_VALUE() {
        final Function function = new Function(FUNC_MIN_UNLOCK_VALUE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public RemoteCall<Utf8String> name() {
        final Function function = new Function(FUNC_NAME, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public RemoteCall<TransactionReceipt> approve(Address _spender, Uint256 _value) {
        final Function function = new Function(
                FUNC_APPROVE, 
                Arrays.<Type>asList(_spender, _value), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Uint256> totalSupply() {
        final Function function = new Function(FUNC_TOTALSUPPLY, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public RemoteCall<TransactionReceipt> transferFrom(Address _from, Address _to, Uint256 _value) {
        final Function function = new Function(
                FUNC_TRANSFERFROM, 
                Arrays.<Type>asList(_from, _to, _value), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Uint8> decimals() {
        final Function function = new Function(FUNC_DECIMALS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public RemoteCall<Utf8String> version() {
        final Function function = new Function(FUNC_VERSION, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public RemoteCall<Uint256> balanceOf(Address _owner) {
        final Function function = new Function(FUNC_BALANCEOF, 
                Arrays.<Type>asList(_owner), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public RemoteCall<Uint32> assetGUID() {
        final Function function = new Function(FUNC_ASSETGUID, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint32>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public RemoteCall<Utf8String> symbol() {
        final Function function = new Function(FUNC_SYMBOL, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public RemoteCall<TransactionReceipt> transfer(Address _to, Uint256 _value) {
        final Function function = new Function(
                FUNC_TRANSFER, 
                Arrays.<Type>asList(_to, _value), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Uint256> MIN_LOCK_VALUE() {
        final Function function = new Function(FUNC_MIN_LOCK_VALUE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public RemoteCall<Uint256> SUPERBLOCK_SUBMITTER_LOCK_FEE() {
        final Function function = new Function(FUNC_SUPERBLOCK_SUBMITTER_LOCK_FEE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public RemoteCall<TransactionReceipt> approveAndCall(Address _spender, Uint256 _value, DynamicBytes _extraData) {
        final Function function = new Function(
                FUNC_APPROVEANDCALL, 
                Arrays.<Type>asList(_spender, _value, _extraData), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Address> trustedRelayerContract() {
        final Function function = new Function(FUNC_TRUSTEDRELAYERCONTRACT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public RemoteCall<Uint256> allowance(Address _owner, Address _spender) {
        final Function function = new Function(FUNC_ALLOWANCE, 
                Arrays.<Type>asList(_owner, _spender), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public static RemoteCall<SyscoinToken> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider, Address _trustedRelayerContract, Uint32 _assetGUID) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(_trustedRelayerContract, _assetGUID));
        return deployRemoteCall(SyscoinToken.class, web3j, credentials, contractGasProvider, BINARY, encodedConstructor);
    }

    public static RemoteCall<SyscoinToken> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider, Address _trustedRelayerContract, Uint32 _assetGUID) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(_trustedRelayerContract, _assetGUID));
        return deployRemoteCall(SyscoinToken.class, web3j, transactionManager, contractGasProvider, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<SyscoinToken> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, Address _trustedRelayerContract, Uint32 _assetGUID) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(_trustedRelayerContract, _assetGUID));
        return deployRemoteCall(SyscoinToken.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<SyscoinToken> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, Address _trustedRelayerContract, Uint32 _assetGUID) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(_trustedRelayerContract, _assetGUID));
        return deployRemoteCall(SyscoinToken.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public List<ErrorSyscoinTokenEventResponse> getErrorSyscoinTokenEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(ERRORSYSCOINTOKEN_EVENT, transactionReceipt);
        ArrayList<ErrorSyscoinTokenEventResponse> responses = new ArrayList<ErrorSyscoinTokenEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            ErrorSyscoinTokenEventResponse typedResponse = new ErrorSyscoinTokenEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.err = (Uint256) eventValues.getNonIndexedValues().get(0);
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<ErrorSyscoinTokenEventResponse> errorSyscoinTokenEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, ErrorSyscoinTokenEventResponse>() {
            @Override
            public ErrorSyscoinTokenEventResponse call(Log log) {
                EventValuesWithLog eventValues = extractEventParametersWithLog(ERRORSYSCOINTOKEN_EVENT, log);
                ErrorSyscoinTokenEventResponse typedResponse = new ErrorSyscoinTokenEventResponse();
                typedResponse.log = log;
                typedResponse.err = (Uint256) eventValues.getNonIndexedValues().get(0);
                return typedResponse;
            }
        });
    }

    public Observable<ErrorSyscoinTokenEventResponse> errorSyscoinTokenEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(ERRORSYSCOINTOKEN_EVENT));
        return errorSyscoinTokenEventObservable(filter);
    }

    public List<NewTokenEventResponse> getNewTokenEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(NEWTOKEN_EVENT, transactionReceipt);
        ArrayList<NewTokenEventResponse> responses = new ArrayList<NewTokenEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            NewTokenEventResponse typedResponse = new NewTokenEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.user = (Address) eventValues.getIndexedValues().get(0);
            typedResponse.value = (Uint256) eventValues.getNonIndexedValues().get(0);
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<NewTokenEventResponse> newTokenEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, NewTokenEventResponse>() {
            @Override
            public NewTokenEventResponse call(Log log) {
                EventValuesWithLog eventValues = extractEventParametersWithLog(NEWTOKEN_EVENT, log);
                NewTokenEventResponse typedResponse = new NewTokenEventResponse();
                typedResponse.log = log;
                typedResponse.user = (Address) eventValues.getIndexedValues().get(0);
                typedResponse.value = (Uint256) eventValues.getNonIndexedValues().get(0);
                return typedResponse;
            }
        });
    }

    public Observable<NewTokenEventResponse> newTokenEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(NEWTOKEN_EVENT));
        return newTokenEventObservable(filter);
    }

    public List<BurnEventResponse> getBurnEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(BURN_EVENT, transactionReceipt);
        ArrayList<BurnEventResponse> responses = new ArrayList<BurnEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            BurnEventResponse typedResponse = new BurnEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.from = (Address) eventValues.getIndexedValues().get(0);
            typedResponse.value = (Uint256) eventValues.getNonIndexedValues().get(0);
            typedResponse.assetGUID = (Uint32) eventValues.getNonIndexedValues().get(1);
            typedResponse.syscoinWitnessProgram = (DynamicBytes) eventValues.getNonIndexedValues().get(2);
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<BurnEventResponse> burnEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, BurnEventResponse>() {
            @Override
            public BurnEventResponse call(Log log) {
                EventValuesWithLog eventValues = extractEventParametersWithLog(BURN_EVENT, log);
                BurnEventResponse typedResponse = new BurnEventResponse();
                typedResponse.log = log;
                typedResponse.from = (Address) eventValues.getIndexedValues().get(0);
                typedResponse.value = (Uint256) eventValues.getNonIndexedValues().get(0);
                typedResponse.assetGUID = (Uint32) eventValues.getNonIndexedValues().get(1);
                typedResponse.syscoinWitnessProgram = (DynamicBytes) eventValues.getNonIndexedValues().get(2);
                return typedResponse;
            }
        });
    }

    public Observable<BurnEventResponse> burnEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(BURN_EVENT));
        return burnEventObservable(filter);
    }

    public List<TransferEventResponse> getTransferEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(TRANSFER_EVENT, transactionReceipt);
        ArrayList<TransferEventResponse> responses = new ArrayList<TransferEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            TransferEventResponse typedResponse = new TransferEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._from = (Address) eventValues.getIndexedValues().get(0);
            typedResponse._to = (Address) eventValues.getIndexedValues().get(1);
            typedResponse._value = (Uint256) eventValues.getNonIndexedValues().get(0);
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<TransferEventResponse> transferEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, TransferEventResponse>() {
            @Override
            public TransferEventResponse call(Log log) {
                EventValuesWithLog eventValues = extractEventParametersWithLog(TRANSFER_EVENT, log);
                TransferEventResponse typedResponse = new TransferEventResponse();
                typedResponse.log = log;
                typedResponse._from = (Address) eventValues.getIndexedValues().get(0);
                typedResponse._to = (Address) eventValues.getIndexedValues().get(1);
                typedResponse._value = (Uint256) eventValues.getNonIndexedValues().get(0);
                return typedResponse;
            }
        });
    }

    public Observable<TransferEventResponse> transferEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(TRANSFER_EVENT));
        return transferEventObservable(filter);
    }

    public List<ApprovalEventResponse> getApprovalEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(APPROVAL_EVENT, transactionReceipt);
        ArrayList<ApprovalEventResponse> responses = new ArrayList<ApprovalEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            ApprovalEventResponse typedResponse = new ApprovalEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._owner = (Address) eventValues.getIndexedValues().get(0);
            typedResponse._spender = (Address) eventValues.getIndexedValues().get(1);
            typedResponse._value = (Uint256) eventValues.getNonIndexedValues().get(0);
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<ApprovalEventResponse> approvalEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, ApprovalEventResponse>() {
            @Override
            public ApprovalEventResponse call(Log log) {
                EventValuesWithLog eventValues = extractEventParametersWithLog(APPROVAL_EVENT, log);
                ApprovalEventResponse typedResponse = new ApprovalEventResponse();
                typedResponse.log = log;
                typedResponse._owner = (Address) eventValues.getIndexedValues().get(0);
                typedResponse._spender = (Address) eventValues.getIndexedValues().get(1);
                typedResponse._value = (Uint256) eventValues.getNonIndexedValues().get(0);
                return typedResponse;
            }
        });
    }

    public Observable<ApprovalEventResponse> approvalEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(APPROVAL_EVENT));
        return approvalEventObservable(filter);
    }

    public RemoteCall<Bool> wasSyscoinTxProcessed(Uint256 txHash) {
        final Function function = new Function(FUNC_WASSYSCOINTXPROCESSED, 
                Arrays.<Type>asList(txHash), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public RemoteCall<TransactionReceipt> processTransaction(Uint256 txHash, Uint256 value, Address destinationAddress, Uint32 _assetGUID, Address _assetContractAddress, Address superblockSubmitterAddress) {
        final Function function = new Function(
                FUNC_PROCESSTRANSACTION, 
                Arrays.<Type>asList(txHash, value, destinationAddress, _assetGUID, _assetContractAddress, superblockSubmitterAddress), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> burn(Uint256 _value, Uint32 _assetGUID, DynamicBytes syscoinWitnessProgram, BigInteger weiValue) {
        final Function function = new Function(
                FUNC_BURN, 
                Arrays.<Type>asList(_value, _assetGUID, syscoinWitnessProgram), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    @Deprecated
    public static SyscoinToken load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new SyscoinToken(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static SyscoinToken load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new SyscoinToken(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static SyscoinToken load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new SyscoinToken(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static SyscoinToken load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new SyscoinToken(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    protected String getStaticDeployedAddress(String networkId) {
        return _addresses.get(networkId);
    }

    public static String getPreviouslyDeployedAddress(String networkId) {
        return _addresses.get(networkId);
    }

    public static class ErrorSyscoinTokenEventResponse {
        public Log log;

        public Uint256 err;
    }

    public static class NewTokenEventResponse {
        public Log log;

        public Address user;

        public Uint256 value;
    }

    public static class BurnEventResponse {
        public Log log;

        public Address from;

        public Uint256 value;

        public Uint32 assetGUID;

        public DynamicBytes syscoinWitnessProgram;
    }

    public static class TransferEventResponse {
        public Log log;

        public Address _from;

        public Address _to;

        public Uint256 _value;
    }

    public static class ApprovalEventResponse {
        public Log log;

        public Address _owner;

        public Address _spender;

        public Uint256 _value;
    }
}
