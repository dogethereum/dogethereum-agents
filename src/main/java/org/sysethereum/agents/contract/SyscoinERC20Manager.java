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
    private static final String BINARY = "0x608060405234801561001057600080fd5b50611857806100206000396000f3fe6080604052600436106100865760003560e01c8063a71d75ca11610059578063a71d75ca14610210578063af56f1581461023a578063cf496b101461029f578063f7daeb85146102d0578063fe2e97181461030c57610086565b80631b7289201461008b5780632f3489c7146100b05780635f959b69146100e05780636cde8d6f146101d1575b600080fd5b6100ae600480360360208110156100a157600080fd5b503563ffffffff1661034e565b005b3480156100bc57600080fd5b506100ae600480360360208110156100d357600080fd5b503563ffffffff16610597565b3480156100ec57600080fd5b506101bd600480360360a081101561010357600080fd5b81359163ffffffff602082013516916001600160a01b036040830135169160ff6060820135169181019060a08101608082013564010000000081111561014857600080fd5b82018360208201111561015a57600080fd5b8035906020019184600183028401116401000000008311171561017c57600080fd5b91908080601f016020809104026020016040519081016040528093929190818152602001838380828437600092019190915250929550610807945050505050565b604080519115158252519081900360200190f35b3480156101dd57600080fd5b506100ae600480360360408110156101f457600080fd5b50803563ffffffff1690602001356001600160a01b0316610c34565b34801561021c57600080fd5b506101bd6004803603602081101561023357600080fd5b5035610db7565b34801561024657600080fd5b506100ae600480360360e081101561025d57600080fd5b5080359060208101359060408101356001600160a01b039081169160608101358216916080820135169060a081013563ffffffff169060c0013560ff16610dca565b3480156102ab57600080fd5b506102b461112f565b604080516001600160a01b039092168252519081900360200190f35b3480156102dc57600080fd5b506100ae600480360360408110156102f357600080fd5b50803560ff1690602001356001600160a01b031661113e565b34801561031857600080fd5b5061033c6004803603602081101561032f57600080fd5b503563ffffffff16611228565b60408051918252519081900360200190f35b61035661150f565b63ffffffff828116600090815260376020908152604091829020825160c0810184528154815260018201549281019290925260028101546001600160a01b039081169383019390935260038101549283166060830152600160a01b83049093166080820152919060a0830190600160c01b900460ff1660048111156103d757fe5b60048111156103e257fe5b905250905060018160a0015160048111156103f957fe5b146104355760405162461bcd60e51b81526004018080602001828103825260528152602001806115a06052913960600191505060405180910390fd5b80606001516001600160a01b0316336001600160a01b0316146104895760405162461bcd60e51b81526004018080602001828103825260528152602001806116ed6052913960600191505060405180910390fd5b600060395460ff16600281111561049c57fe5b146104a957612a306104ae565b620dd7c05b81514203116104ee5760405162461bcd60e51b81526004018080602001828103825260548152602001806116996054913960600191505060405180910390fd5b6729a2241af62c00003410156105355760405162461bcd60e51b81526004018080602001828103825260468152602001806116536046913960600191505060405180910390fd5b63ffffffff8216600081815260386020908152604091829020349055428452600260a085015281513381529081019290925280517f1bd938c0559acc36703807b71652dec64b2eed0d54f1716803e65cedc4f55a129281900390910190a15050565b63ffffffff8116600090815260376020526040902060026003820154600160c01b900460ff1660048111156105c857fe5b146106045760405162461bcd60e51b815260040180806020018281038252604c8152602001806117d7604c913960600191505060405180910390fd5b610e10816000015442031161064a5760405162461bcd60e51b815260040180806020018281038252604981526020018061178e6049913960600191505060405180910390fd5b60028101546001820154600383015463ffffffff600160a01b90910481166000908152603460205260409020546001600160a01b039093169261068f92909161123a16565b600383018054600160a01b900463ffffffff1660009081526034602090815260408083209490945591546001860154845163a9059cbb60e01b81526001600160a01b039283166004820152602481019190915293519085169363a9059cbb936044808301949193928390030190829087803b15801561070d57600080fd5b505af1158015610721573d6000803e3d6000fd5b505050506040513d602081101561073757600080fd5b5050600382015463ffffffff84166000908152603860205260408082205490516001600160a01b0390931692839282156108fc02929190818181858888f1935050505015801561078b573d6000803e3d6000fd5b5063ffffffff841660008181526038602090815260408083209290925560038601805460ff60c01b1916600160c21b179081905582516001600160a01b0390911681529081019290925280517f558dcc0f85e822d51fb0c98b95ab299d76c136c9d1a34b9cb2e3ede1689cdcfe9281900390910190a150505050565b600083866000826001600160a01b031663313ce5676040518163ffffffff1660e01b815260040160206040518083038186803b15801561084657600080fd5b505afa15801561085a573d6000803e3d6000fd5b505050506040513d602081101561087057600080fd5b505160ff16905061088b600a82810a9063ffffffff61128316565b8210156108c95760405162461bcd60e51b815260040180806020018281038252602c815260200180611574602c913960400191505060405180910390fd5b600085511161091f576040805162461bcd60e51b815260206004820152601d60248201527f737973636f696e416464726573732063616e6e6f74206265207a65726f000000604482015290519081900360640190fd5b60008863ffffffff161161097a576040805162461bcd60e51b815260206004820152601860248201527f41737365742047554944206d757374206e6f7420626520300000000000000000604482015290519081900360640190fd5b6000879050806001600160a01b031663313ce5676040518163ffffffff1660e01b815260040160206040518083038186803b1580156109b857600080fd5b505afa1580156109cc573d6000803e3d6000fd5b505050506040513d60208110156109e257600080fd5b505160ff888116911614610a275760405162461bcd60e51b81526004018080602001828103825260318152602001806115436031913960400191505060405180910390fd5b604080516323b872dd60e01b8152336004820152306024820152604481018c905290516001600160a01b038316916323b872dd9160648083019260209291908290030181600087803b158015610a7c57600080fd5b505af1158015610a90573d6000803e3d6000fd5b505050506040513d6020811015610aa657600080fd5b505063ffffffff808a16600090815260346020526040902054610acb918c906112c516565b63ffffffff808b166000818152603460209081526040918290209490945560368054808516600190810190951663ffffffff19909116179055805160c0810182524281529384018e90526001600160a01b038c1690840152336060840152608083015260a082015260365463ffffffff90811660009081526037602090815260409182902084518155908401516001820155908301516002820180546001600160a01b03199081166001600160a01b0393841617909155606085015160038401805460808801519316919093161763ffffffff60a01b1916600160a01b91909416029290921780835560a084015191929060ff60c01b1916600160c01b836004811115610bd457fe5b02179055505060365460408051338152602081018e905263ffffffff90921682820152517faabab1db49e504b5156edf3f99042aeecb9607a08f392589571cd49743aaba8d92509081900360600190a15060019998505050505050505050565b6033546001600160a01b03163314610c7d5760405162461bcd60e51b815260040180806020018281038252602181526020018061173f6021913960400191505060405180910390fd5b63ffffffff8216600090815260376020526040902060026003820154600160c01b900460ff166004811115610cae57fe5b14610cea5760405162461bcd60e51b81526004018080602001828103825260618152602001806115f26061913960800191505060405180910390fd5b63ffffffff83166000908152603860205260408082205490516001600160a01b0385169282156108fc02929190818181858888f19350505050158015610d34573d6000803e3d6000fd5b5063ffffffff83166000908152603860205260408120556003818101805460ff60c01b1916600160c01b8302179055506003810154604080516001600160a01b03909216825263ffffffff8516602083015280517f960e217c57581c52cdc4e321eb617416d051a348a2ecf62bb8023a3558e80e859281900390910190a1505050565b6000610dc28261131f565b90505b919050565b6033546001600160a01b03163314610e135760405162461bcd60e51b815260040180806020018281038252602181526020018061173f6021913960400191505060405180910390fd5b60008390506000816001600160a01b031663313ce5676040518163ffffffff1660e01b815260040160206040518083038186803b158015610e5357600080fd5b505afa158015610e67573d6000803e3d6000fd5b505050506040513d6020811015610e7d57600080fd5b5051905060ff8084169082161115610ea15782810360ff16600a0a88029750610ec4565b8260ff168160ff161015610ec45780830360ff16600a0a8881610ec057fe5b0497505b610ece8189611334565b610ed7896113d5565b610f1f576040805162461bcd60e51b8152602060048201526014602482015273151608185b1c9958591e481c1c9bd8d95cdcd95960621b604482015290519081900360640190fd5b63ffffffff808516600090815260346020526040902054610f42918a9061123a16565b63ffffffff808616600090815260346020526040812092909255610f6c908a906127109061128316565b90506000610f808a8363ffffffff61123a16565b9050836001600160a01b031663a9059cbb89846040518363ffffffff1660e01b815260040180836001600160a01b03166001600160a01b0316815260200182815260200192505050602060405180830381600087803b158015610fe257600080fd5b505af1158015610ff6573d6000803e3d6000fd5b505050506040513d602081101561100c57600080fd5b5050604080516001600160a01b038a1681526020810184905281517f378dbe173f6ed6e11630b29573f719ec4cefc9b49f430deed915911c5f78a080929181900390910190a1836001600160a01b031663a9059cbb8a836040518363ffffffff1660e01b815260040180836001600160a01b03166001600160a01b0316815260200182815260200192505050602060405180830381600087803b1580156110b257600080fd5b505af11580156110c6573d6000803e3d6000fd5b505050506040513d60208110156110dc57600080fd5b5050604080516001600160a01b038b1681526020810183905281517fb925ba840e2f36bcb317f8179bd8b5ed01aba4a22abf5f169162c0894dea87ab929181900390910190a15050505050505050505050565b6033546001600160a01b031681565b600054610100900460ff1680611157575061115761140d565b80611165575060005460ff16155b6111a05760405162461bcd60e51b815260040180806020018281038252602e815260200180611760602e913960400191505060405180910390fd5b600054610100900460ff161580156111cb576000805460ff1961ff0019909116610100171660011790555b6039805484919060ff191660018360028111156111e457fe5b0217905550603380546001600160a01b0319166001600160a01b0384161790556036805463ffffffff191690558015611223576000805461ff00191690555b505050565b60346020526000908152604090205481565b600061127c83836040518060400160405280601e81526020017f536166654d6174683a207375627472616374696f6e206f766572666c6f770000815250611413565b9392505050565b600061127c83836040518060400160405280601a81526020017f536166654d6174683a206469766973696f6e206279207a65726f0000000000008152506114aa565b60008282018381101561127c576040805162461bcd60e51b815260206004820152601b60248201527f536166654d6174683a206164646974696f6e206f766572666c6f770000000000604482015290519081900360640190fd5b60009081526035602052604090205460ff1690565b60ff821681611383576040805162461bcd60e51b815260206004820152601660248201527556616c7565206d75737420626520706f73697469766560501b604482015290519081900360640190fd5b611397600a82810a9063ffffffff61128316565b8210156112235760405162461bcd60e51b815260040180806020018281038252602c815260200180611574602c913960400191505060405180910390fd5b60006113e08261131f565b156113ed57506000610dc5565b506000908152603560205260409020805460ff1916600190811790915590565b303b1590565b600081848411156114a25760405162461bcd60e51b81526004018080602001828103825283818151815260200191508051906020019080838360005b8381101561146757818101518382015260200161144f565b50505050905090810190601f1680156114945780820380516001836020036101000a031916815260200191505b509250505060405180910390fd5b505050900390565b600081836114f95760405162461bcd60e51b815260206004820181815283516024840152835190928392604490910191908501908083836000831561146757818101518382015260200161144f565b50600083858161150557fe5b0495945050505050565b6040805160c08101825260008082526020820181905291810182905260608101829052608081018290529060a08201529056fe446563696d616c732077657265206e6f742070726f766964656420776974682074686520636f72726563742076616c756556616c7565206d75737420626520626967676572206f7220657175616c204d494e5f4c4f434b5f56414c554523537973636f696e45524332304d616e616765722063616e63656c5472616e736665725265717565737428293a20537461747573206f6620627269646765207472616e73666572206d757374206265204f6b23537973636f696e45524332304d616e616765722063616e63656c5472616e736665725375636365737328293a20537461747573206d7573742062652043616e63656c52657175657374656420746f204661696c20746865207472616e7366657223537973636f696e45524332304d616e616765722063616e63656c5472616e736665725375636365737328293a2043616e63656c206465706f73697420696e636f727265637423537973636f696e45524332304d616e616765722063616e63656c5472616e736665725265717565737428293a205472616e73666572206d757374206265206174206c6561737420312e35207765656b206f6c6423537973636f696e45524332304d616e616765722063616e63656c5472616e736665725265717565737428293a204f6e6c79206d73672e73656e64657220697320616c6c6f77656420746f2063616e63656c43616c6c206d7573742062652066726f6d20747275737465642072656c61796572436f6e747261637420696e7374616e63652068617320616c7265616479206265656e20696e697469616c697a656423537973636f696e45524332304d616e616765722063616e63656c5472616e736665725375636365737328293a203120686f7572732074696d656f757420697320726571756972656423537973636f696e45524332304d616e616765722063616e63656c5472616e736665725375636365737328293a20537461747573206d7573742062652043616e63656c526571756573746564a265627a7a72315820eab72084030a1d9720416c53a27c3ca74d0a04ae082cba9fac1c10bdfa3660a164736f6c634300050d0032";

    public static final String FUNC_ASSETBALANCES = "assetBalances";

    public static final String FUNC_TRUSTEDRELAYERCONTRACT = "trustedRelayerContract";

    public static final String FUNC_INIT = "init";

    public static final String FUNC_WASSYSCOINTXPROCESSED = "wasSyscoinTxProcessed";

    public static final String FUNC_PROCESSTRANSACTION = "processTransaction";

    public static final String FUNC_CANCELTRANSFERREQUEST = "cancelTransferRequest";

    public static final String FUNC_CANCELTRANSFERSUCCESS = "cancelTransferSuccess";

    public static final String FUNC_PROCESSCANCELTRANSFERFAIL = "processCancelTransferFail";

    public static final String FUNC_FREEZEBURNERC20 = "freezeBurnERC20";

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
        _addresses.put("4", "0x4Cafd654dA402B4f14aCbE7cf2E8F0a43Cd68e19");
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
