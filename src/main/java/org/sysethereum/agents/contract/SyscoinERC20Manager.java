package org.sysethereum.agents.contract;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
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
import org.web3j.tuples.generated.Tuple6;
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
    private static final String BINARY = "0x608060405234801561001057600080fd5b506118b4806100206000396000f3fe6080604052600436106100915760003560e01c8063a71d75ca11610059578063a71d75ca146102c2578063af56f158146102ec578063cf496b1014610351578063f7daeb8514610382578063fe2e9718146103be57610091565b8063085e7092146100965780631b7289201461013d5780632f3489c7146101625780635f959b69146101925780636cde8d6f14610283575b600080fd5b3480156100a257600080fd5b506100c6600480360360208110156100b957600080fd5b503563ffffffff16610400565b60405180878152602001868152602001856001600160a01b03166001600160a01b03168152602001846001600160a01b03166001600160a01b031681526020018363ffffffff1663ffffffff16815260200182600481111561012457fe5b60ff168152602001965050505050505060405180910390f35b6101606004803603602081101561015357600080fd5b503563ffffffff16610454565b005b34801561016e57600080fd5b506101606004803603602081101561018557600080fd5b503563ffffffff16610628565b34801561019e57600080fd5b5061026f600480360360a08110156101b557600080fd5b81359163ffffffff602082013516916001600160a01b036040830135169160ff6060820135169181019060a0810160808201356401000000008111156101fa57600080fd5b82018360208201111561020c57600080fd5b8035906020019184600183028401116401000000008311171561022e57600080fd5b91908080601f016020809104026020016040519081016040528093929190818152602001838380828437600092019190915250929550610898945050505050565b604080519115158252519081900360200190f35b34801561028f57600080fd5b50610160600480360360408110156102a657600080fd5b50803563ffffffff1690602001356001600160a01b0316610cc5565b3480156102ce57600080fd5b5061026f600480360360208110156102e557600080fd5b5035610e48565b3480156102f857600080fd5b50610160600480360360e081101561030f57600080fd5b5080359060208101359060408101356001600160a01b039081169160608101358216916080820135169060a081013563ffffffff169060c0013560ff16610e5b565b34801561035d57600080fd5b506103666111c0565b604080516001600160a01b039092168252519081900360200190f35b34801561038e57600080fd5b50610160600480360360408110156103a557600080fd5b50803560ff1690602001356001600160a01b03166111cf565b3480156103ca57600080fd5b506103ee600480360360208110156103e157600080fd5b503563ffffffff166112b9565b60408051918252519081900360200190f35b63ffffffff9081166000908152603760205260409020805460018201546002830154600390930154919490936001600160a01b0393841693831692600160a01b810490911691600160c01b90910460ff1690565b63ffffffff8116600090815260376020526040902060016003820154600160c01b900460ff16600481111561048557fe5b146104c15760405162461bcd60e51b81526004018080602001828103825260528152602001806115fe6052913960600191505060405180910390fd5b60038101546001600160a01b0316331461050c5760405162461bcd60e51b815260040180806020018281038252605281526020018061174b6052913960600191505060405180910390fd5b600060395460ff16600281111561051f57fe5b1461052c57618ca0610531565b620dd7c05b81544203116105715760405162461bcd60e51b81526004018080602001828103825260548152602001806116f76054913960600191505060405180910390fd5b6729a2241af62c00003410156105b85760405162461bcd60e51b81526004018080602001828103825260468152602001806116b16046913960600191505060405180910390fd5b63ffffffff821660008181526038602090815260409182902034905542845560038401805460ff60c01b1916600160c11b17905581513381529081019290925280517f1bd938c0559acc36703807b71652dec64b2eed0d54f1716803e65cedc4f55a129281900390910190a15050565b63ffffffff8116600090815260376020526040902060026003820154600160c01b900460ff16600481111561065957fe5b146106955760405162461bcd60e51b815260040180806020018281038252604c815260200180611834604c913960600191505060405180910390fd5b610e1081600001544203116106db5760405162461bcd60e51b81526004018080602001828103825260488152602001806117ec6048913960600191505060405180910390fd5b60028101546001820154600383015463ffffffff600160a01b90910481166000908152603460205260409020546001600160a01b03909316926107209290916112cb16565b600383018054600160a01b900463ffffffff1660009081526034602090815260408083209490945591546001860154845163a9059cbb60e01b81526001600160a01b039283166004820152602481019190915293519085169363a9059cbb936044808301949193928390030190829087803b15801561079e57600080fd5b505af11580156107b2573d6000803e3d6000fd5b505050506040513d60208110156107c857600080fd5b5050600382015463ffffffff84166000908152603860205260408082205490516001600160a01b0390931692839282156108fc02929190818181858888f1935050505015801561081c573d6000803e3d6000fd5b5063ffffffff841660008181526038602090815260408083209290925560038601805460ff60c01b1916600160c21b179081905582516001600160a01b0390911681529081019290925280517f558dcc0f85e822d51fb0c98b95ab299d76c136c9d1a34b9cb2e3ede1689cdcfe9281900390910190a150505050565b600083866000826001600160a01b031663313ce5676040518163ffffffff1660e01b815260040160206040518083038186803b1580156108d757600080fd5b505afa1580156108eb573d6000803e3d6000fd5b505050506040513d602081101561090157600080fd5b505160ff16905061091c600a82810a9063ffffffff61131416565b82101561095a5760405162461bcd60e51b815260040180806020018281038252602c8152602001806115d2602c913960400191505060405180910390fd5b60008551116109b0576040805162461bcd60e51b815260206004820152601d60248201527f737973636f696e416464726573732063616e6e6f74206265207a65726f000000604482015290519081900360640190fd5b60008863ffffffff1611610a0b576040805162461bcd60e51b815260206004820152601860248201527f41737365742047554944206d757374206e6f7420626520300000000000000000604482015290519081900360640190fd5b6000879050806001600160a01b031663313ce5676040518163ffffffff1660e01b815260040160206040518083038186803b158015610a4957600080fd5b505afa158015610a5d573d6000803e3d6000fd5b505050506040513d6020811015610a7357600080fd5b505160ff888116911614610ab85760405162461bcd60e51b81526004018080602001828103825260318152602001806115a16031913960400191505060405180910390fd5b604080516323b872dd60e01b8152336004820152306024820152604481018c905290516001600160a01b038316916323b872dd9160648083019260209291908290030181600087803b158015610b0d57600080fd5b505af1158015610b21573d6000803e3d6000fd5b505050506040513d6020811015610b3757600080fd5b505063ffffffff808a16600090815260346020526040902054610b5c918c9061135616565b63ffffffff808b166000818152603460209081526040918290209490945560368054808516600190810190951663ffffffff19909116179055805160c0810182524281529384018e90526001600160a01b038c1690840152336060840152608083015260a082015260365463ffffffff90811660009081526037602090815260409182902084518155908401516001820155908301516002820180546001600160a01b03199081166001600160a01b0393841617909155606085015160038401805460808801519316919093161763ffffffff60a01b1916600160a01b91909416029290921780835560a084015191929060ff60c01b1916600160c01b836004811115610c6557fe5b02179055505060365460408051338152602081018e905263ffffffff90921682820152517faabab1db49e504b5156edf3f99042aeecb9607a08f392589571cd49743aaba8d92509081900360600190a15060019998505050505050505050565b6033546001600160a01b03163314610d0e5760405162461bcd60e51b815260040180806020018281038252602181526020018061179d6021913960400191505060405180910390fd5b63ffffffff8216600090815260376020526040902060026003820154600160c01b900460ff166004811115610d3f57fe5b14610d7b5760405162461bcd60e51b81526004018080602001828103825260618152602001806116506061913960800191505060405180910390fd5b63ffffffff83166000908152603860205260408082205490516001600160a01b0385169282156108fc02929190818181858888f19350505050158015610dc5573d6000803e3d6000fd5b5063ffffffff83166000908152603860205260408120556003818101805460ff60c01b1916600160c01b8302179055506003810154604080516001600160a01b03909216825263ffffffff8516602083015280517f960e217c57581c52cdc4e321eb617416d051a348a2ecf62bb8023a3558e80e859281900390910190a1505050565b6000610e53826113b0565b90505b919050565b6033546001600160a01b03163314610ea45760405162461bcd60e51b815260040180806020018281038252602181526020018061179d6021913960400191505060405180910390fd5b60008390506000816001600160a01b031663313ce5676040518163ffffffff1660e01b815260040160206040518083038186803b158015610ee457600080fd5b505afa158015610ef8573d6000803e3d6000fd5b505050506040513d6020811015610f0e57600080fd5b5051905060ff8084169082161115610f325782810360ff16600a0a88029750610f55565b8260ff168160ff161015610f555780830360ff16600a0a8881610f5157fe5b0497505b610f5f81896113c5565b610f6889611466565b610fb0576040805162461bcd60e51b8152602060048201526014602482015273151608185b1c9958591e481c1c9bd8d95cdcd95960621b604482015290519081900360640190fd5b63ffffffff808516600090815260346020526040902054610fd3918a906112cb16565b63ffffffff808616600090815260346020526040812092909255610ffd908a906127109061131416565b905060006110118a8363ffffffff6112cb16565b9050836001600160a01b031663a9059cbb89846040518363ffffffff1660e01b815260040180836001600160a01b03166001600160a01b0316815260200182815260200192505050602060405180830381600087803b15801561107357600080fd5b505af1158015611087573d6000803e3d6000fd5b505050506040513d602081101561109d57600080fd5b5050604080516001600160a01b038a1681526020810184905281517f378dbe173f6ed6e11630b29573f719ec4cefc9b49f430deed915911c5f78a080929181900390910190a1836001600160a01b031663a9059cbb8a836040518363ffffffff1660e01b815260040180836001600160a01b03166001600160a01b0316815260200182815260200192505050602060405180830381600087803b15801561114357600080fd5b505af1158015611157573d6000803e3d6000fd5b505050506040513d602081101561116d57600080fd5b5050604080516001600160a01b038b1681526020810183905281517fb925ba840e2f36bcb317f8179bd8b5ed01aba4a22abf5f169162c0894dea87ab929181900390910190a15050505050505050505050565b6033546001600160a01b031681565b600054610100900460ff16806111e857506111e861149e565b806111f6575060005460ff16155b6112315760405162461bcd60e51b815260040180806020018281038252602e8152602001806117be602e913960400191505060405180910390fd5b600054610100900460ff1615801561125c576000805460ff1961ff0019909116610100171660011790555b6039805484919060ff1916600183600281111561127557fe5b0217905550603380546001600160a01b0319166001600160a01b0384161790556036805463ffffffff1916905580156112b4576000805461ff00191690555b505050565b60346020526000908152604090205481565b600061130d83836040518060400160405280601e81526020017f536166654d6174683a207375627472616374696f6e206f766572666c6f7700008152506114a4565b9392505050565b600061130d83836040518060400160405280601a81526020017f536166654d6174683a206469766973696f6e206279207a65726f00000000000081525061153b565b60008282018381101561130d576040805162461bcd60e51b815260206004820152601b60248201527f536166654d6174683a206164646974696f6e206f766572666c6f770000000000604482015290519081900360640190fd5b60009081526035602052604090205460ff1690565b60ff821681611414576040805162461bcd60e51b815260206004820152601660248201527556616c7565206d75737420626520706f73697469766560501b604482015290519081900360640190fd5b611428600a82810a9063ffffffff61131416565b8210156112b45760405162461bcd60e51b815260040180806020018281038252602c8152602001806115d2602c913960400191505060405180910390fd5b6000611471826113b0565b1561147e57506000610e56565b506000908152603560205260409020805460ff1916600190811790915590565b303b1590565b600081848411156115335760405162461bcd60e51b81526004018080602001828103825283818151815260200191508051906020019080838360005b838110156114f85781810151838201526020016114e0565b50505050905090810190601f1680156115255780820380516001836020036101000a031916815260200191505b509250505060405180910390fd5b505050900390565b6000818361158a5760405162461bcd60e51b81526020600482018181528351602484015283519092839260449091019190850190808383600083156114f85781810151838201526020016114e0565b50600083858161159657fe5b049594505050505056fe446563696d616c732077657265206e6f742070726f766964656420776974682074686520636f72726563742076616c756556616c7565206d75737420626520626967676572206f7220657175616c204d494e5f4c4f434b5f56414c554523537973636f696e45524332304d616e616765722063616e63656c5472616e736665725265717565737428293a20537461747573206f6620627269646765207472616e73666572206d757374206265204f6b23537973636f696e45524332304d616e616765722063616e63656c5472616e736665725375636365737328293a20537461747573206d7573742062652043616e63656c52657175657374656420746f204661696c20746865207472616e7366657223537973636f696e45524332304d616e616765722063616e63656c5472616e736665725265717565737428293a2043616e63656c206465706f73697420696e636f727265637423537973636f696e45524332304d616e616765722063616e63656c5472616e736665725265717565737428293a205472616e73666572206d757374206265206174206c6561737420312e35207765656b206f6c6423537973636f696e45524332304d616e616765722063616e63656c5472616e736665725265717565737428293a204f6e6c79206d73672e73656e64657220697320616c6c6f77656420746f2063616e63656c43616c6c206d7573742062652066726f6d20747275737465642072656c61796572436f6e747261637420696e7374616e63652068617320616c7265616479206265656e20696e697469616c697a656423537973636f696e45524332304d616e616765722063616e63656c5472616e736665725375636365737328293a203120686f75722074696d656f757420697320726571756972656423537973636f696e45524332304d616e616765722063616e63656c5472616e736665725375636365737328293a20537461747573206d7573742062652043616e63656c526571756573746564a265627a7a7231582039d8ba6ff440b8c0478cb56ee1793ec4d068cb68662e7097427199716cea64bc64736f6c634300050d0032";

    public static final String FUNC_ASSETBALANCES = "assetBalances";

    public static final String FUNC_TRUSTEDRELAYERCONTRACT = "trustedRelayerContract";

    public static final String FUNC_INIT = "init";

    public static final String FUNC_WASSYSCOINTXPROCESSED = "wasSyscoinTxProcessed";

    public static final String FUNC_PROCESSTRANSACTION = "processTransaction";

    public static final String FUNC_CANCELTRANSFERREQUEST = "cancelTransferRequest";

    public static final String FUNC_CANCELTRANSFERSUCCESS = "cancelTransferSuccess";

    public static final String FUNC_PROCESSCANCELTRANSFERFAIL = "processCancelTransferFail";

    public static final String FUNC_FREEZEBURNERC20 = "freezeBurnERC20";

    public static final String FUNC_GETBRIDGETRANSFER = "getBridgeTransfer";

    public static final Event CANCELTRANSFERFAILED_EVENT = new Event("CancelTransferFailed", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint32>() {}));
    ;

    public static final Event CANCELTRANSFERREQUEST_EVENT = new Event("CancelTransferRequest", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint32>() {}));
    ;

    public static final Event CANCELTRANSFERSUCCEEDED_EVENT = new Event("CancelTransferSucceeded", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint32>() {}));
    ;

    public static final Event TOKENFREEZE_EVENT = new Event("TokenFreeze", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint32>() {}));
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
        _addresses.put("4", "0x443d9a14fb6ba2A45465bEC3767186f404Ccea25");
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

    public List<CancelTransferFailedEventResponse> getCancelTransferFailedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(CANCELTRANSFERFAILED_EVENT, transactionReceipt);
        ArrayList<CancelTransferFailedEventResponse> responses = new ArrayList<CancelTransferFailedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            CancelTransferFailedEventResponse typedResponse = new CancelTransferFailedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.canceller = (Address) eventValues.getNonIndexedValues().get(0);
            typedResponse.bridgetransferid = (Uint32) eventValues.getNonIndexedValues().get(1);
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<CancelTransferFailedEventResponse> cancelTransferFailedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, CancelTransferFailedEventResponse>() {
            @Override
            public CancelTransferFailedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(CANCELTRANSFERFAILED_EVENT, log);
                CancelTransferFailedEventResponse typedResponse = new CancelTransferFailedEventResponse();
                typedResponse.log = log;
                typedResponse.canceller = (Address) eventValues.getNonIndexedValues().get(0);
                typedResponse.bridgetransferid = (Uint32) eventValues.getNonIndexedValues().get(1);
                return typedResponse;
            }
        });
    }

    public Flowable<CancelTransferFailedEventResponse> cancelTransferFailedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(CANCELTRANSFERFAILED_EVENT));
        return cancelTransferFailedEventFlowable(filter);
    }

    public List<CancelTransferRequestEventResponse> getCancelTransferRequestEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(CANCELTRANSFERREQUEST_EVENT, transactionReceipt);
        ArrayList<CancelTransferRequestEventResponse> responses = new ArrayList<CancelTransferRequestEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            CancelTransferRequestEventResponse typedResponse = new CancelTransferRequestEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.canceller = (Address) eventValues.getNonIndexedValues().get(0);
            typedResponse.bridgetransferid = (Uint32) eventValues.getNonIndexedValues().get(1);
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<CancelTransferRequestEventResponse> cancelTransferRequestEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, CancelTransferRequestEventResponse>() {
            @Override
            public CancelTransferRequestEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(CANCELTRANSFERREQUEST_EVENT, log);
                CancelTransferRequestEventResponse typedResponse = new CancelTransferRequestEventResponse();
                typedResponse.log = log;
                typedResponse.canceller = (Address) eventValues.getNonIndexedValues().get(0);
                typedResponse.bridgetransferid = (Uint32) eventValues.getNonIndexedValues().get(1);
                return typedResponse;
            }
        });
    }

    public Flowable<CancelTransferRequestEventResponse> cancelTransferRequestEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(CANCELTRANSFERREQUEST_EVENT));
        return cancelTransferRequestEventFlowable(filter);
    }

    public List<CancelTransferSucceededEventResponse> getCancelTransferSucceededEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(CANCELTRANSFERSUCCEEDED_EVENT, transactionReceipt);
        ArrayList<CancelTransferSucceededEventResponse> responses = new ArrayList<CancelTransferSucceededEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            CancelTransferSucceededEventResponse typedResponse = new CancelTransferSucceededEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.canceller = (Address) eventValues.getNonIndexedValues().get(0);
            typedResponse.bridgetransferid = (Uint32) eventValues.getNonIndexedValues().get(1);
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<CancelTransferSucceededEventResponse> cancelTransferSucceededEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, CancelTransferSucceededEventResponse>() {
            @Override
            public CancelTransferSucceededEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(CANCELTRANSFERSUCCEEDED_EVENT, log);
                CancelTransferSucceededEventResponse typedResponse = new CancelTransferSucceededEventResponse();
                typedResponse.log = log;
                typedResponse.canceller = (Address) eventValues.getNonIndexedValues().get(0);
                typedResponse.bridgetransferid = (Uint32) eventValues.getNonIndexedValues().get(1);
                return typedResponse;
            }
        });
    }

    public Flowable<CancelTransferSucceededEventResponse> cancelTransferSucceededEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(CANCELTRANSFERSUCCEEDED_EVENT));
        return cancelTransferSucceededEventFlowable(filter);
    }

    public List<TokenFreezeEventResponse> getTokenFreezeEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(TOKENFREEZE_EVENT, transactionReceipt);
        ArrayList<TokenFreezeEventResponse> responses = new ArrayList<TokenFreezeEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            TokenFreezeEventResponse typedResponse = new TokenFreezeEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.freezer = (Address) eventValues.getNonIndexedValues().get(0);
            typedResponse.value = (Uint256) eventValues.getNonIndexedValues().get(1);
            typedResponse.bridgetransferid = (Uint32) eventValues.getNonIndexedValues().get(2);
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
                typedResponse.bridgetransferid = (Uint32) eventValues.getNonIndexedValues().get(2);
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

    public RemoteFunctionCall<TransactionReceipt> init(Uint8 _network, Address _trustedRelayerContract) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_INIT, 
                Arrays.<Type>asList(_network, _trustedRelayerContract), 
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

    public RemoteFunctionCall<TransactionReceipt> cancelTransferRequest(Uint32 bridgeTransferId, BigInteger weiValue) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_CANCELTRANSFERREQUEST, 
                Arrays.<Type>asList(bridgeTransferId), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteFunctionCall<TransactionReceipt> cancelTransferSuccess(Uint32 bridgeTransferId) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_CANCELTRANSFERSUCCESS, 
                Arrays.<Type>asList(bridgeTransferId), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> processCancelTransferFail(Uint32 bridgeTransferId, Address challengerAddress) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_PROCESSCANCELTRANSFERFAIL, 
                Arrays.<Type>asList(bridgeTransferId, challengerAddress), 
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

    public RemoteFunctionCall<Tuple6<Uint256, Uint256, Address, Address, Uint32, Uint8>> getBridgeTransfer(Uint32 bridgeTransferId) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETBRIDGETRANSFER, 
                Arrays.<Type>asList(bridgeTransferId), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Address>() {}, new TypeReference<Address>() {}, new TypeReference<Uint32>() {}, new TypeReference<Uint8>() {}));
        return new RemoteFunctionCall<Tuple6<Uint256, Uint256, Address, Address, Uint32, Uint8>>(function,
                new Callable<Tuple6<Uint256, Uint256, Address, Address, Uint32, Uint8>>() {
                    @Override
                    public Tuple6<Uint256, Uint256, Address, Address, Uint32, Uint8> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple6<Uint256, Uint256, Address, Address, Uint32, Uint8>(
                                (Uint256) results.get(0), 
                                (Uint256) results.get(1), 
                                (Address) results.get(2), 
                                (Address) results.get(3), 
                                (Uint32) results.get(4), 
                                (Uint8) results.get(5));
                    }
                });
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

    public static class CancelTransferFailedEventResponse extends BaseEventResponse {
        public Address canceller;

        public Uint32 bridgetransferid;
    }

    public static class CancelTransferRequestEventResponse extends BaseEventResponse {
        public Address canceller;

        public Uint32 bridgetransferid;
    }

    public static class CancelTransferSucceededEventResponse extends BaseEventResponse {
        public Address canceller;

        public Uint32 bridgetransferid;
    }

    public static class TokenFreezeEventResponse extends BaseEventResponse {
        public Address freezer;

        public Uint256 value;

        public Uint32 bridgetransferid;
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
