package org.dogethereum.agents.contract;

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
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.DynamicBytes;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
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
    public static final String BINARY = "0x6080604052600180556001600255600560035534801561001e57600080fd5b50604051611eae380380611eae8339818101604052602081101561004157600080fd5b5051600480546001600160a01b0319166001600160a01b03909216919091179055611e3d806100716000396000f3fe6080604052600436106101445760003560e01c806366ffaaab116100b6578063cb01f1b21161006f578063cb01f1b214610658578063ce3c5dcb14610682578063e1254fba146106ac578063e32250de146106df578063f350351f14610709578063fc7e286d1461073357610154565b806366ffaaab14610484578063887daa2a146105095780638b6dcfc21461054257806392c824d81461057b57806392f2df21146105a5578063bc380364146105de57610154565b806341b3d1851161010857806341b3d1851461020d57806350a1676e1461022257806354a452fb1461024c5780635aef24471461028a5780635d57e82e146103a95780635e45fa7a1461043b57610154565b80630b7aa41814610159578063114705821461018a5780632b229928146101b457806333289a46146101db57806340732c891461020557610154565b3661015457610151610766565b50005b600080fd5b34801561016557600080fd5b5061016e610786565b604080516001600160a01b039092168252519081900360200190f35b34801561019657600080fd5b5061016e600480360360208110156101ad57600080fd5b5035610795565b3480156101c057600080fd5b506101c96107d5565b60408051918252519081900360200190f35b3480156101e757600080fd5b506101c9600480360360208110156101fe57600080fd5b50356107db565b6101c9610766565b34801561021957600080fd5b506101c9610887565b34801561022e57600080fd5b506101c96004803603602081101561024557600080fd5b503561088d565b34801561025857600080fd5b506102766004803603602081101561026f57600080fd5b50356108a2565b604080519115158252519081900360200190f35b34801561029657600080fd5b506102b4600480360360208110156102ad57600080fd5b5035610931565b60405180856001600160a01b031681526020018060200180602001848152602001838103835286818151815260200191508051906020019080838360005b8381101561030a5781810151838201526020016102f2565b50505050905090810190601f1680156103375780820380516001836020036101000a031916815260200191505b50838103825285518152855160209182019187019080838360005b8381101561036a578181015183820152602001610352565b50505050905090810190601f1680156103975780820380516001836020036101000a031916815260200191505b50965050505050505060405180910390f35b3480156103b557600080fd5b506101c9600480360360808110156103cc57600080fd5b8101906020810181356401000000008111156103e757600080fd5b8201836020820111156103f957600080fd5b8035906020019184600183028401116401000000008311171561041b57600080fd5b91935091508035906001600160a01b036020820135169060400135610a8e565b34801561044757600080fd5b506104826004803603608081101561045e57600080fd5b508035906020810135906001600160a01b0360408201358116916060013516610ade565b005b6104826004803603608081101561049a57600080fd5b8101906020810181356401000000008111156104b557600080fd5b8201836020820111156104c757600080fd5b803590602001918460018302840111640100000000831117156104e957600080fd5b9193509150803590602081013590604001356001600160a01b0316610c14565b34801561051557600080fd5b506101c96004803603604081101561052c57600080fd5b50803590602001356001600160a01b0316610fb3565b34801561054e57600080fd5b506101c96004803603604081101561056557600080fd5b50803590602001356001600160a01b0316610ff7565b34801561058757600080fd5b506104826004803603602081101561059e57600080fd5b50356110c5565b3480156105b157600080fd5b506101c9600480360360408110156105c857600080fd5b50803590602001356001600160a01b03166111cb565b3480156105ea57600080fd5b506106086004803603602081101561060157600080fd5b50356111f6565b60408051602080825283518183015283519192839290830191858101910280838360005b8381101561064457818101518382015260200161062c565b505050509050019250505060405180910390f35b34801561066457600080fd5b5061016e6004803603602081101561067b57600080fd5b5035611265565b34801561068e57600080fd5b50610276600480360360208110156106a557600080fd5b5035611291565b3480156106b857600080fd5b506101c9600480360360208110156106cf57600080fd5b50356001600160a01b03166112a9565b3480156106eb57600080fd5b506104826004803603602081101561070257600080fd5b50356112c4565b34801561071557600080fd5b506104826004803603602081101561072c57600080fd5b50356115ed565b34801561073f57600080fd5b506101c96004803603602081101561075657600080fd5b50356001600160a01b0316611a1e565b60006107723334611a30565b503360009081526020819052604090205490565b6004546001600160a01b031681565b600081815260056020526040812060078101546004909101805490919081106107ba57fe5b6000918252602090912001546001600160a01b031692915050565b60035481565b336000908152602081905260408120548211156107f757600080fd5b33600081815260208190526040808220805486900390555184156108fc0291859190818181858888f19350505050158015610836573d6000803e3d6000fd5b50604080513381526020810184905281517f4482101800a5c2e900f4156e57e05e19ffd7b366cde579553d723fd3abb2180e929181900390910190a150503360009081526020819052604090205490565b60025481565b60009081526005602052604090206003015490565b6000818152600560205260408120816108ba82611aa4565b905060006003546108d8846003015443611ab390919063ffffffff16565b600b85015460088601546007870154600688015494909311945043919091119260ff90911615911484801561090a5750835b80156109135750825b801561091c5750815b80156109255750805b98975050505050505050565b60008181526005602090815260408083208054600c8201546001808401805486516002610100948316159490940260001901909116839004601f810189900489028201890190975286815260609788978a97966001600160a01b031695939487019392918591908301828280156109e95780601f106109be576101008083540402835291602001916109e9565b820191906000526020600020905b8154815290600101906020018083116109cc57829003601f168201915b5050855460408051602060026001851615610100026000190190941693909304601f810184900484028201840190925281815295985087945092508401905082828015610a775780601f10610a4c57610100808354040283529160200191610a77565b820191906000526020600020905b815481529060010190602001808311610a5a57829003601f168201915b505050505091509450945094509450509193509193565b604080516bffffffffffffffffffffffff19606085901b16602080830191909152603482018690526054808301859052835180840390910181526074909201909252805191012095945050505050565b6004546001600160a01b0316338114610af657600080fd5b6000848152600560205260409020610b0d81611aa4565b610b1657600080fd5b60088101805460ff191690556001600160a01b038084166000908152600983016020526040808220549287168252902054610b519082611afc565b6001600160a01b03808716600090815260098501602052604080822093909355868216808252928120558354161415610ba45760068201546007830155600a8201805461ff001916610100179055610bc2565b81546001600160a01b038681169116141561015457610bc2866112c4565b604080518881526001600160a01b03808816602083015286168183015290517f27d6edc6ce9ab48630ff9c08892a548870cd6985dbe8d4cb1a21c158a5fba7b99181900360600190a150505050505050565b604080516020808252818301909252600091602082018180368337505050602081018590529050323415610c4c57610c4c8134611a30565b6002546001600160a01b0382166000908152602081905260409020541015610c7357600080fd5b604080516bffffffffffffffffffffffff19606084901b16602080830191909152603482018890526054808301889052835180840390910181526074909201835281519181019190912060008181526005909252919020610cd390611aa4565b15610cdd57600080fd5b600081815260056020526040902080546001600160a01b0319166001600160a01b038416178155610d12600182018a8a611cea565b508351610d289060028301906020870190611d76565b50600060068201819055600782015560088101805460ff19169055436003820155600a8101805461ffff19169055600c8101869055600d810180546001600160a01b0319166001600160a01b03878116919091179091558154600254610d919285921690611b56565b508054604080518481526001600160a01b03909216602083018190526080918301828152600180860180546002600019938216156101000293909301168290049486018590527f3dd94f4964946edbcf822a77f4abd83b2a91b03f889cece28612831d85c243d39588959193928801929091606083019060a084019086908015610e5c5780601f10610e3157610100808354040283529160200191610e5c565b820191906000526020600020905b815481529060010190602001808311610e3f57829003601f168201915b5050838103825284546002600019610100600184161502019091160480825260209091019085908015610ed05780601f10610ea557610100808354040283529160200191610ed0565b820191906000526020600020905b815481529060010190602001808311610eb357829003601f168201915b5050965050505050505060405180910390a180600d0160009054906101000a90046001600160a01b03166001600160a01b0316636c8bb73982600c0154898c8c336040518663ffffffff1660e01b81526004018086815260200185815260200180602001836001600160a01b031681526020018281038252858582818152602001925080828437600081840152601f19601f8201169050808301925050509650505050505050600060405180830381600087803b158015610f9057600080fd5b505af1158015610fa4573d6000803e3d6000fd5b50505050505050505050505050565b6000828152600560205260408120610fca81611aa4565b610fd357600080fd5b6001600160a01b038316600090815260099091016020526040902054905092915050565b600082815260056020526040812061100e81611aa4565b61101757600080fd5b600a81015460ff16151560011461102d57600080fd5b6001600160a01b0383166000908152600982016020908152604080832080549084905591839052909120546110629082611afc565b6001600160a01b0385166000818152602081815260409182902093909355805188815292830191909152818101839052517f44c8a24da6f48938ce6ee551c058529a48695baf9b754bf3184fc5f065878b109181900360600190a1949350505050565b60008181526005602052604090206110dc81611aa4565b6110e557600080fd5b600a81015460ff16156110f757600080fd5b3360009081526005820160205260409020541561111357600080fd5b60025433600090815260208190526040902054101561113157600080fd5b61113e8233600254611b56565b5060035461114d904390611afc565b600b82015560048101805460018181018355600092835260209092200180546001600160a01b03191633179055600682015461118891611afc565b60068201556040805183815233602082015281517f829b4d84d4743d197a9b0fad05abef9f517535e06d50e0b85b1e77089b8afa9d929181900390910190a15050565b60008281526005602081815260408084206001600160a01b0386168552909201905290205492915050565b60008181526005602090815260409182902060040180548351818402810184019094528084526060939283018282801561125957602002820191906000526020600020905b81546001600160a01b0316815260019091019060200180831161123b575b50505050509050919050565b6000600154821061127557600080fd5b600082815260056020526040812060040180549091906107ba57fe5b60009081526005602052604090206008015460ff1690565b6001600160a01b031660009081526020819052604090205490565b60008181526005602052604090206112db81611aa4565b6112e457600080fd5b600a81015460ff16156112f657600080fd5b600881015460ff161561130857600080fd5b8060070154816006015411156115e9576000600460009054906101000a90046001600160a01b03166001600160a01b03166383ce5da5848460040185600701548154811061135257fe5b600091825260209091200154855460405160e085901b6001600160e01b0319168152600481018481526001600160a01b0393841660248301819052939092166044820181905261080160a4830181905260c0606484019081526001808c01805460026101009382161593909302600019011682900460c4870181905294969095918d0194909291608481019160e490910190879080156114335780601f1061140857610100808354040283529160200191611433565b820191906000526020600020905b81548152906001019060200180831161141657829003601f168201915b50508381038252855460026000196101006001841615020190911604808252602090910190869080156114a75780601f1061147c576101008083540402835291602001916114a7565b820191906000526020600020905b81548152906001019060200180831161148a57829003601f168201915b505098505050505050505050602060405180830381600087803b1580156114cd57600080fd5b505af11580156114e1573d6000803e3d6000fd5b505050506040513d60208110156114f757600080fd5b505160078301546004840180549293508392600586019260009291811061151a57fe5b60009182526020808320909101546001600160a01b039081168452908301939093526040909101902091909155825460078401546004850180547f2b294b3eef63ed64ae6063c231cf082926ced732f1acc4ac1a7c052314b0edf2948894169290811061158357fe5b60009182526020918290200154604080519485526001600160a01b0393841692850192909252919091168282015260608201849052519081900360800190a160088201805460ff1916600190811790915560078301546115e291611afc565b6007830155505b5050565b600081815260056020526040902061160481611aa4565b61160d57600080fd5b600881015460ff161561161f57600080fd5b600354611639826003015443611ab390919063ffffffff16565b1161164357600080fd5b80600b0154431161165357600080fd5b806007015481600601541461166757600080fd5b600a8101805460ff191660011790819055610100900460ff1661185c57600d810154600c820154604080516305292a5960e41b81526004810192909252516001600160a01b0390921691635292a5909160248082019260009290919082900301818387803b1580156116d857600080fd5b505af11580156116ec573d6000803e3d6000fd5b5050825461170692508491506001600160a01b0316610ff7565b508054604080518481526001600160a01b03909216602083018190526080918301828152600180860180546002600019938216156101000293909301168290049486018590527f4549d9b7bb648611ec44e49083bbeeba3960dc743ea4f12e3998860c46da0b2e9588959193928801929091606083019060a0840190869080156117d15780601f106117a6576101008083540402835291602001916117d1565b820191906000526020600020905b8154815290600101906020018083116117b457829003601f168201915b50508381038252845460026000196101006001841615020190911604808252602090910190859080156118455780601f1061181a57610100808354040283529160200191611845565b820191906000526020600020905b81548152906001019060200180831161182857829003601f168201915b5050965050505050505060405180910390a16115e9565b600d810154600c8201546040805163db1ee90360e01b81526004810192909252516001600160a01b039092169163db1ee9039160248082019260009290919082900301818387803b1580156118b057600080fd5b505af11580156118c4573d6000803e3d6000fd5b50508254604080518681526001600160a01b03909216602083018190526080918301828152600180880180546002600019938216156101000293909301168290049486018590527f80cfc76da9b35fee5f480583330e1c8db05b6a2d6d27cc60cccae080c81076f8975089965092949293908801929190606083019060a0840190869080156119945780601f1061196957610100808354040283529160200191611994565b820191906000526020600020905b81548152906001019060200180831161197757829003601f168201915b5050838103825284546002600019610100600184161502019091160480825260209091019085908015611a085780601f106119dd57610100808354040283529160200191611a08565b820191906000526020600020905b8154815290600101906020018083116119eb57829003601f168201915b5050965050505050505060405180910390a15050565b60006020819052908152604090205481565b6001600160a01b03821660009081526020819052604090208054820190819055471015611a5c57600080fd5b604080516001600160a01b03841681526020810183905281517fd15c9547ea5c06670c0010ce19bc32d54682a4b3801ece7f3ab0c3f17106b4bb929181900390910190a15050565b546001600160a01b0316151590565b6000611af583836040518060400160405280601e81526020017f536166654d6174683a207375627472616374696f6e206f766572666c6f770000815250611c53565b9392505050565b600082820183811015611af5576040805162461bcd60e51b815260206004820152601b60248201527f536166654d6174683a206164646974696f6e206f766572666c6f770000000000604482015290519081900360640190fd5b6000838152600560205260408120611b6d81611aa4565b611b7657600080fd5b6001600160a01b038416600090815260208190526040902054831115611b9b57600080fd5b6001600160a01b0384166000908152602081815260408083208054879003905560098401909152902054611bcf9084611afc565b6001600160a01b038516600081815260098401602090815260409182902093909355805188815292830191909152818101859052517f5552f672574aa03647978f5573eebe6545f711fc59f43558a820e8d251c346619181900360600190a16001600160a01b03841660009081526009909101602052604090205490509392505050565b60008184841115611ce25760405162461bcd60e51b81526004018080602001828103825283818151815260200191508051906020019080838360005b83811015611ca7578181015183820152602001611c8f565b50505050905090810190601f168015611cd45780820380516001836020036101000a031916815260200191505b509250505060405180910390fd5b505050900390565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282611d205760008555611d66565b82601f10611d395782800160ff19823516178555611d66565b82800160010185558215611d66579182015b82811115611d66578235825591602001919060010190611d4b565b50611d72929150611df2565b5090565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282611dac5760008555611d66565b82601f10611dc557805160ff1916838001178555611d66565b82800160010185558215611d66579182015b82811115611d66578251825591602001919060010190611dd7565b5b80821115611d725760008155600101611df356fea2646970667358221220038526d726fb1c9250a77ef114687d152ff5479065f47ca9f7add7312fc5a01b64736f6c63430007060033";

    public static final String FUNC_DEFAULTCHALLENGETIMEOUT = "defaultChallengeTimeout";

    public static final String FUNC_DEPOSITS = "deposits";

    public static final String FUNC_GETDEPOSIT = "getDeposit";

    public static final String FUNC_MAKEDEPOSIT = "makeDeposit";

    public static final String FUNC_MINDEPOSIT = "minDeposit";

    public static final String FUNC_SCRYPTVERIFIER = "scryptVerifier";

    public static final String FUNC_WITHDRAWDEPOSIT = "withdrawDeposit";

    public static final String FUNC_GETBONDEDDEPOSIT = "getBondedDeposit";

    public static final String FUNC_UNBONDDEPOSIT = "unbondDeposit";

    public static final String FUNC_CALCID = "calcId";

    public static final String FUNC_CHECKSCRYPT = "checkScrypt";

    public static final String FUNC_CHALLENGECLAIM = "challengeClaim";

    public static final String FUNC_RUNNEXTVERIFICATIONGAME = "runNextVerificationGame";

    public static final String FUNC_SESSIONDECIDED = "sessionDecided";

    public static final String FUNC_CHECKCLAIMSUCCESSFUL = "checkClaimSuccessful";

    public static final String FUNC_FIRSTCHALLENGER = "firstChallenger";

    public static final String FUNC_CREATEDAT = "createdAt";

    public static final String FUNC_GETSESSION = "getSession";

    public static final String FUNC_GETCHALLENGERS = "getChallengers";

    public static final String FUNC_GETCURRENTCHALLENGER = "getCurrentChallenger";

    public static final String FUNC_GETVERIFICATIONONGOING = "getVerificationOngoing";

    public static final String FUNC_GETCLAIM = "getClaim";

    public static final String FUNC_GETCLAIMREADY = "getClaimReady";

    public static final Event CLAIMCHALLENGED_EVENT = new Event("ClaimChallenged", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Address>() {}));
    ;

    public static final Event CLAIMCREATED_EVENT = new Event("ClaimCreated", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Address>() {}, new TypeReference<DynamicBytes>() {}, new TypeReference<DynamicBytes>() {}));
    ;

    public static final Event CLAIMFAILED_EVENT = new Event("ClaimFailed", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Address>() {}, new TypeReference<DynamicBytes>() {}, new TypeReference<DynamicBytes>() {}));
    ;

    public static final Event CLAIMSUCCESSFUL_EVENT = new Event("ClaimSuccessful", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Address>() {}, new TypeReference<DynamicBytes>() {}, new TypeReference<DynamicBytes>() {}));
    ;

    public static final Event CLAIMVERIFICATIONGAMESENDED_EVENT = new Event("ClaimVerificationGamesEnded", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    public static final Event DEPOSITBONDED_EVENT = new Event("DepositBonded", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event DEPOSITMADE_EVENT = new Event("DepositMade", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event DEPOSITUNBONDED_EVENT = new Event("DepositUnbonded", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event DEPOSITWITHDRAWN_EVENT = new Event("DepositWithdrawn", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event SESSIONDECIDED_EVENT = new Event("SessionDecided", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Address>() {}, new TypeReference<Address>() {}));
    ;

    public static final Event VERIFICATIONGAMESTARTED_EVENT = new Event("VerificationGameStarted", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Address>() {}, new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
    ;

    protected static final HashMap<String, String> _addresses;

    static {
        _addresses = new HashMap<String, String>();
    }

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

    public List<ClaimChallengedEventResponse> getClaimChallengedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(CLAIMCHALLENGED_EVENT, transactionReceipt);
        ArrayList<ClaimChallengedEventResponse> responses = new ArrayList<ClaimChallengedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ClaimChallengedEventResponse typedResponse = new ClaimChallengedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.claimID = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.challenger = (String) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<ClaimChallengedEventResponse> claimChallengedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, ClaimChallengedEventResponse>() {
            @Override
            public ClaimChallengedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(CLAIMCHALLENGED_EVENT, log);
                ClaimChallengedEventResponse typedResponse = new ClaimChallengedEventResponse();
                typedResponse.log = log;
                typedResponse.claimID = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.challenger = (String) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<ClaimChallengedEventResponse> claimChallengedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(CLAIMCHALLENGED_EVENT));
        return claimChallengedEventFlowable(filter);
    }

    public List<ClaimCreatedEventResponse> getClaimCreatedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(CLAIMCREATED_EVENT, transactionReceipt);
        ArrayList<ClaimCreatedEventResponse> responses = new ArrayList<ClaimCreatedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ClaimCreatedEventResponse typedResponse = new ClaimCreatedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.claimID = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.claimant = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.plaintext = (byte[]) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.blockHash = (byte[]) eventValues.getNonIndexedValues().get(3).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<ClaimCreatedEventResponse> claimCreatedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, ClaimCreatedEventResponse>() {
            @Override
            public ClaimCreatedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(CLAIMCREATED_EVENT, log);
                ClaimCreatedEventResponse typedResponse = new ClaimCreatedEventResponse();
                typedResponse.log = log;
                typedResponse.claimID = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.claimant = (String) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.plaintext = (byte[]) eventValues.getNonIndexedValues().get(2).getValue();
                typedResponse.blockHash = (byte[]) eventValues.getNonIndexedValues().get(3).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<ClaimCreatedEventResponse> claimCreatedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(CLAIMCREATED_EVENT));
        return claimCreatedEventFlowable(filter);
    }

    public List<ClaimFailedEventResponse> getClaimFailedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(CLAIMFAILED_EVENT, transactionReceipt);
        ArrayList<ClaimFailedEventResponse> responses = new ArrayList<ClaimFailedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ClaimFailedEventResponse typedResponse = new ClaimFailedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.claimID = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.claimant = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.plaintext = (byte[]) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.blockHash = (byte[]) eventValues.getNonIndexedValues().get(3).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<ClaimFailedEventResponse> claimFailedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, ClaimFailedEventResponse>() {
            @Override
            public ClaimFailedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(CLAIMFAILED_EVENT, log);
                ClaimFailedEventResponse typedResponse = new ClaimFailedEventResponse();
                typedResponse.log = log;
                typedResponse.claimID = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.claimant = (String) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.plaintext = (byte[]) eventValues.getNonIndexedValues().get(2).getValue();
                typedResponse.blockHash = (byte[]) eventValues.getNonIndexedValues().get(3).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<ClaimFailedEventResponse> claimFailedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(CLAIMFAILED_EVENT));
        return claimFailedEventFlowable(filter);
    }

    public List<ClaimSuccessfulEventResponse> getClaimSuccessfulEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(CLAIMSUCCESSFUL_EVENT, transactionReceipt);
        ArrayList<ClaimSuccessfulEventResponse> responses = new ArrayList<ClaimSuccessfulEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ClaimSuccessfulEventResponse typedResponse = new ClaimSuccessfulEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.claimID = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.claimant = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.plaintext = (byte[]) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.blockHash = (byte[]) eventValues.getNonIndexedValues().get(3).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<ClaimSuccessfulEventResponse> claimSuccessfulEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, ClaimSuccessfulEventResponse>() {
            @Override
            public ClaimSuccessfulEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(CLAIMSUCCESSFUL_EVENT, log);
                ClaimSuccessfulEventResponse typedResponse = new ClaimSuccessfulEventResponse();
                typedResponse.log = log;
                typedResponse.claimID = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.claimant = (String) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.plaintext = (byte[]) eventValues.getNonIndexedValues().get(2).getValue();
                typedResponse.blockHash = (byte[]) eventValues.getNonIndexedValues().get(3).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<ClaimSuccessfulEventResponse> claimSuccessfulEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(CLAIMSUCCESSFUL_EVENT));
        return claimSuccessfulEventFlowable(filter);
    }

    public List<ClaimVerificationGamesEndedEventResponse> getClaimVerificationGamesEndedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(CLAIMVERIFICATIONGAMESENDED_EVENT, transactionReceipt);
        ArrayList<ClaimVerificationGamesEndedEventResponse> responses = new ArrayList<ClaimVerificationGamesEndedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ClaimVerificationGamesEndedEventResponse typedResponse = new ClaimVerificationGamesEndedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.claimID = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<ClaimVerificationGamesEndedEventResponse> claimVerificationGamesEndedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, ClaimVerificationGamesEndedEventResponse>() {
            @Override
            public ClaimVerificationGamesEndedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(CLAIMVERIFICATIONGAMESENDED_EVENT, log);
                ClaimVerificationGamesEndedEventResponse typedResponse = new ClaimVerificationGamesEndedEventResponse();
                typedResponse.log = log;
                typedResponse.claimID = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<ClaimVerificationGamesEndedEventResponse> claimVerificationGamesEndedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(CLAIMVERIFICATIONGAMESENDED_EVENT));
        return claimVerificationGamesEndedEventFlowable(filter);
    }

    public List<DepositBondedEventResponse> getDepositBondedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(DEPOSITBONDED_EVENT, transactionReceipt);
        ArrayList<DepositBondedEventResponse> responses = new ArrayList<DepositBondedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            DepositBondedEventResponse typedResponse = new DepositBondedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.claimID = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.account = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<DepositBondedEventResponse> depositBondedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, DepositBondedEventResponse>() {
            @Override
            public DepositBondedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(DEPOSITBONDED_EVENT, log);
                DepositBondedEventResponse typedResponse = new DepositBondedEventResponse();
                typedResponse.log = log;
                typedResponse.claimID = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.account = (String) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<DepositBondedEventResponse> depositBondedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(DEPOSITBONDED_EVENT));
        return depositBondedEventFlowable(filter);
    }

    public List<DepositMadeEventResponse> getDepositMadeEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(DEPOSITMADE_EVENT, transactionReceipt);
        ArrayList<DepositMadeEventResponse> responses = new ArrayList<DepositMadeEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            DepositMadeEventResponse typedResponse = new DepositMadeEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.who = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<DepositMadeEventResponse> depositMadeEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, DepositMadeEventResponse>() {
            @Override
            public DepositMadeEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(DEPOSITMADE_EVENT, log);
                DepositMadeEventResponse typedResponse = new DepositMadeEventResponse();
                typedResponse.log = log;
                typedResponse.who = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<DepositMadeEventResponse> depositMadeEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(DEPOSITMADE_EVENT));
        return depositMadeEventFlowable(filter);
    }

    public List<DepositUnbondedEventResponse> getDepositUnbondedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(DEPOSITUNBONDED_EVENT, transactionReceipt);
        ArrayList<DepositUnbondedEventResponse> responses = new ArrayList<DepositUnbondedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            DepositUnbondedEventResponse typedResponse = new DepositUnbondedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.claimID = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.account = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<DepositUnbondedEventResponse> depositUnbondedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, DepositUnbondedEventResponse>() {
            @Override
            public DepositUnbondedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(DEPOSITUNBONDED_EVENT, log);
                DepositUnbondedEventResponse typedResponse = new DepositUnbondedEventResponse();
                typedResponse.log = log;
                typedResponse.claimID = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.account = (String) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<DepositUnbondedEventResponse> depositUnbondedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(DEPOSITUNBONDED_EVENT));
        return depositUnbondedEventFlowable(filter);
    }

    public List<DepositWithdrawnEventResponse> getDepositWithdrawnEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(DEPOSITWITHDRAWN_EVENT, transactionReceipt);
        ArrayList<DepositWithdrawnEventResponse> responses = new ArrayList<DepositWithdrawnEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            DepositWithdrawnEventResponse typedResponse = new DepositWithdrawnEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.who = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<DepositWithdrawnEventResponse> depositWithdrawnEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, DepositWithdrawnEventResponse>() {
            @Override
            public DepositWithdrawnEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(DEPOSITWITHDRAWN_EVENT, log);
                DepositWithdrawnEventResponse typedResponse = new DepositWithdrawnEventResponse();
                typedResponse.log = log;
                typedResponse.who = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<DepositWithdrawnEventResponse> depositWithdrawnEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(DEPOSITWITHDRAWN_EVENT));
        return depositWithdrawnEventFlowable(filter);
    }

    public List<SessionDecidedEventResponse> getSessionDecidedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(SESSIONDECIDED_EVENT, transactionReceipt);
        ArrayList<SessionDecidedEventResponse> responses = new ArrayList<SessionDecidedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            SessionDecidedEventResponse typedResponse = new SessionDecidedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.sessionId = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.winner = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.loser = (String) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<SessionDecidedEventResponse> sessionDecidedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, SessionDecidedEventResponse>() {
            @Override
            public SessionDecidedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(SESSIONDECIDED_EVENT, log);
                SessionDecidedEventResponse typedResponse = new SessionDecidedEventResponse();
                typedResponse.log = log;
                typedResponse.sessionId = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.winner = (String) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.loser = (String) eventValues.getNonIndexedValues().get(2).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<SessionDecidedEventResponse> sessionDecidedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(SESSIONDECIDED_EVENT));
        return sessionDecidedEventFlowable(filter);
    }

    public List<VerificationGameStartedEventResponse> getVerificationGameStartedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(VERIFICATIONGAMESTARTED_EVENT, transactionReceipt);
        ArrayList<VerificationGameStartedEventResponse> responses = new ArrayList<VerificationGameStartedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            VerificationGameStartedEventResponse typedResponse = new VerificationGameStartedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.claimID = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.claimant = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.challenger = (String) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.sessionId = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<VerificationGameStartedEventResponse> verificationGameStartedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, VerificationGameStartedEventResponse>() {
            @Override
            public VerificationGameStartedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(VERIFICATIONGAMESTARTED_EVENT, log);
                VerificationGameStartedEventResponse typedResponse = new VerificationGameStartedEventResponse();
                typedResponse.log = log;
                typedResponse.claimID = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.claimant = (String) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.challenger = (String) eventValues.getNonIndexedValues().get(2).getValue();
                typedResponse.sessionId = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<VerificationGameStartedEventResponse> verificationGameStartedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(VERIFICATIONGAMESTARTED_EVENT));
        return verificationGameStartedEventFlowable(filter);
    }

    public RemoteFunctionCall<BigInteger> defaultChallengeTimeout() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_DEFAULTCHALLENGETIMEOUT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> deposits(String param0) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_DEPOSITS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> getDeposit(String who) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETDEPOSIT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(who)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> makeDeposit() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_MAKEDEPOSIT, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> minDeposit() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_MINDEPOSIT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<String> scryptVerifier() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_SCRYPTVERIFIER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> withdrawDeposit(BigInteger amount) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_WITHDRAWDEPOSIT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> getBondedDeposit(BigInteger claimID, String account) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETBONDEDDEPOSIT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(claimID), 
                new org.web3j.abi.datatypes.Address(account)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> unbondDeposit(BigInteger claimID, String account) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_UNBONDDEPOSIT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(claimID), 
                new org.web3j.abi.datatypes.Address(account)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> calcId(byte[] param0, byte[] _hash, String claimant, byte[] _proposalId) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_CALCID, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicBytes(param0), 
                new org.web3j.abi.datatypes.generated.Bytes32(_hash), 
                new org.web3j.abi.datatypes.Address(claimant), 
                new org.web3j.abi.datatypes.generated.Bytes32(_proposalId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> checkScrypt(byte[] _data, byte[] _hash, byte[] _proposalId, String _scryptDependent) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_CHECKSCRYPT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicBytes(_data), 
                new org.web3j.abi.datatypes.generated.Bytes32(_hash), 
                new org.web3j.abi.datatypes.generated.Bytes32(_proposalId), 
                new org.web3j.abi.datatypes.Address(_scryptDependent)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> challengeClaim(BigInteger claimID) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_CHALLENGECLAIM, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(claimID)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> runNextVerificationGame(BigInteger claimID) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_RUNNEXTVERIFICATIONGAME, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(claimID)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> sessionDecided(BigInteger sessionId, BigInteger claimID, String winner, String loser) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_SESSIONDECIDED, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(sessionId), 
                new org.web3j.abi.datatypes.generated.Uint256(claimID), 
                new org.web3j.abi.datatypes.Address(winner), 
                new org.web3j.abi.datatypes.Address(loser)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> checkClaimSuccessful(BigInteger claimID) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_CHECKCLAIMSUCCESSFUL, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(claimID)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> firstChallenger(BigInteger claimID) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_FIRSTCHALLENGER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(claimID)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<BigInteger> createdAt(BigInteger claimID) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_CREATEDAT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(claimID)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> getSession(BigInteger claimID, String challenger) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETSESSION, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(claimID), 
                new org.web3j.abi.datatypes.Address(challenger)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<List> getChallengers(BigInteger claimID) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETCHALLENGERS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(claimID)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Address>>() {}));
        return new RemoteFunctionCall<List>(function,
                new Callable<List>() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public List call() throws Exception {
                        List<Type> result = (List<Type>) executeCallSingleValueReturn(function, List.class);
                        return convertToNative(result);
                    }
                });
    }

    public RemoteFunctionCall<String> getCurrentChallenger(BigInteger claimID) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETCURRENTCHALLENGER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(claimID)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<Boolean> getVerificationOngoing(BigInteger claimID) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETVERIFICATIONONGOING, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(claimID)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<Tuple4<String, byte[], byte[], byte[]>> getClaim(BigInteger claimID) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETCLAIM, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(claimID)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<DynamicBytes>() {}, new TypeReference<DynamicBytes>() {}, new TypeReference<Bytes32>() {}));
        return new RemoteFunctionCall<Tuple4<String, byte[], byte[], byte[]>>(function,
                new Callable<Tuple4<String, byte[], byte[], byte[]>>() {
                    @Override
                    public Tuple4<String, byte[], byte[], byte[]> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple4<String, byte[], byte[], byte[]>(
                                (String) results.get(0).getValue(), 
                                (byte[]) results.get(1).getValue(), 
                                (byte[]) results.get(2).getValue(), 
                                (byte[]) results.get(3).getValue());
                    }
                });
    }

    public RemoteFunctionCall<Boolean> getClaimReady(BigInteger claimID) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETCLAIMREADY, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(claimID)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
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

    public static RemoteCall<ClaimManager> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider, String _scryptVerifier) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_scryptVerifier)));
        return deployRemoteCall(ClaimManager.class, web3j, credentials, contractGasProvider, BINARY, encodedConstructor);
    }

    public static RemoteCall<ClaimManager> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider, String _scryptVerifier) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_scryptVerifier)));
        return deployRemoteCall(ClaimManager.class, web3j, transactionManager, contractGasProvider, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<ClaimManager> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String _scryptVerifier) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_scryptVerifier)));
        return deployRemoteCall(ClaimManager.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<ClaimManager> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, String _scryptVerifier) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_scryptVerifier)));
        return deployRemoteCall(ClaimManager.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    protected String getStaticDeployedAddress(String networkId) {
        return _addresses.get(networkId);
    }

    public static String getPreviouslyDeployedAddress(String networkId) {
        return _addresses.get(networkId);
    }

    public static class ClaimChallengedEventResponse extends BaseEventResponse {
        public BigInteger claimID;

        public String challenger;
    }

    public static class ClaimCreatedEventResponse extends BaseEventResponse {
        public BigInteger claimID;

        public String claimant;

        public byte[] plaintext;

        public byte[] blockHash;
    }

    public static class ClaimFailedEventResponse extends BaseEventResponse {
        public BigInteger claimID;

        public String claimant;

        public byte[] plaintext;

        public byte[] blockHash;
    }

    public static class ClaimSuccessfulEventResponse extends BaseEventResponse {
        public BigInteger claimID;

        public String claimant;

        public byte[] plaintext;

        public byte[] blockHash;
    }

    public static class ClaimVerificationGamesEndedEventResponse extends BaseEventResponse {
        public BigInteger claimID;
    }

    public static class DepositBondedEventResponse extends BaseEventResponse {
        public BigInteger claimID;

        public String account;

        public BigInteger amount;
    }

    public static class DepositMadeEventResponse extends BaseEventResponse {
        public String who;

        public BigInteger amount;
    }

    public static class DepositUnbondedEventResponse extends BaseEventResponse {
        public BigInteger claimID;

        public String account;

        public BigInteger amount;
    }

    public static class DepositWithdrawnEventResponse extends BaseEventResponse {
        public String who;

        public BigInteger amount;
    }

    public static class SessionDecidedEventResponse extends BaseEventResponse {
        public BigInteger sessionId;

        public String winner;

        public String loser;
    }

    public static class VerificationGameStartedEventResponse extends BaseEventResponse {
        public BigInteger claimID;

        public String claimant;

        public String challenger;

        public BigInteger sessionId;
    }
}
