package org.sysethereum.agents.contract;

import io.reactivex.Flowable;
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
 * <p>Generated with web3j version 4.3.0.
 */
public class SyscoinBattleManager extends Contract {
    private static final String BINARY = "0x608060405234801561001057600080fd5b50604051611fcf380380611fcf8339818101604052608081101561003357600080fd5b50805160208201516040830151606090930151600380549394929385919060ff1916600183600281111561006357fe5b0217905550600480546001600160a01b0319166001600160a01b03949094169390931790925560015560025550611f308061009f6000396000f3fe608060405234801561001057600080fd5b50600436106101165760003560e01c806371a8c18a116100a2578063a6c07c9611610071578063a6c07c9614610493578063abbb6bf6146104b0578063d1daeede146104f1578063ec6dbad814610525578063f1afcfa61461055257610116565b806371a8c18a1461038b578063795ea18e146103bc5780637dbd2832146103d957806399b32f3a1461047657610116565b80633e8a873d116100e95780633e8a873d1461022f578063455e61661461024c57806352b63e9f146102545780635704a5fa1461030157806357bd24fb1461036e57610116565b8063089845e11461011b5780630f2c63ff146101cc57806318b011de146101ef5780633678c14314610209575b600080fd5b6101ca6004803603606081101561013157600080fd5b81359160208101359181019060608101604082013564010000000081111561015857600080fd5b82018360208201111561016a57600080fd5b8035906020019184602083028401116401000000008311171561018c57600080fd5b91908080602002602001604051908101604052809392919081815260200183836020028082843760009201919091525092955061055a945050505050565b005b6101ca600480360360408110156101e257600080fd5b508035906020013561063f565b6101f7610722565b60408051918252519081900360200190f35b6101ca6004803603602081101561021f57600080fd5b50356001600160a01b0316610728565b6101ca6004803603602081101561024557600080fd5b503561077f565b6101f761085c565b6101ca6004803603604081101561026a57600080fd5b8135919081019060408101602082013564010000000081111561028c57600080fd5b82018360208201111561029e57600080fd5b803590602001918460018302840111640100000000831117156102c057600080fd5b91908080601f016020809104026020016040519081016040528093929190818152602001838380828437600092019190915250929550610868945050505050565b61031e6004803603602081101561031757600080fd5b5035610946565b60408051602080825283518183015283519192839290830191858101910280838360005b8381101561035a578181015183820152602001610342565b505050509050019250505060405180910390f35b6101f76004803603602081101561038457600080fd5b50356109aa565b6103a8600480360360208110156103a157600080fd5b50356109bf565b604080519115158252519081900360200190f35b6101f7600480360360208110156103d257600080fd5b50356109f2565b6103f6600480360360208110156103ef57600080fd5b5035610ae2565b604051808a8152602001898152602001886001600160a01b03166001600160a01b03168152602001876001600160a01b03166001600160a01b0316815260200186815260200185815260200184815260200183815260200182600781111561045a57fe5b60ff168152602001995050505050505050505060405180910390f35b6103a86004803603602081101561048c57600080fd5b5035610b3c565b6101ca600480360360208110156104a957600080fd5b5035610b6e565b6104cd600480360360208110156104c657600080fd5b5035610be7565b604051808260028111156104dd57fe5b60ff16815260200191505060405180910390f35b6101f76004803603606081101561050757600080fd5b508035906001600160a01b0360208201358116916040013516610c18565b6105426004803603602081101561053b57600080fd5b5035610d53565b604051808260078111156104dd57fe5b6101f7610d6b565b60008281526020819052604090206002015482906001600160a01b0316331461058257600080fd5b60008381526020819052604081209061059b8285610d71565b905080156105d15760408051868152602081018390528151600080516020611edc833981519152929181900390910190a1610637565b600782018054600101908190554260048401556005830155600382015460408051888152602081018890526001600160a01b0390921682820152517fa35dfb6661c6bfda214c0a2820d9b4bd1a673e2c303cf1578b5948a6f63b5a669181900360600190a15b505050505050565b60008181526020819052604090206003015481906001600160a01b0316331461066757600080fd5b60008281526020819052604081209061067f82610f1a565b905080156106b55760408051858152602081018390528151600080516020611edc833981519152929181900390910190a161071b565b600782018054600101908190554260048401556006830155600282015460408051878152602081018790526001600160a01b0390921682820152517f0817bf136ff95abb2d41d10a9fa5ff6652ff71c13e1b46717fae65db16423c9b9181900360600190a15b5050505050565b60025481565b60035461010090046001600160a01b031615801561074e57506001600160a01b03811615155b61075757600080fd5b600380546001600160a01b0390921661010002610100600160a81b0319909216919091179055565b60008181526020819052604090206003015481906001600160a01b031633146107a757600080fd5b6000828152602081905260408120906107bf82610f6f565b905080156107f55760408051858152602081018390528151600080516020611edc833981519152929181900390910190a1610856565b6007820180546001019081905542600484015560068301556002820154604080518681526001600160a01b03909216602083015280517fa0fbaa47643ff31cddc88d15082b689c8e90d1d48c12a4828762add3eb31be229281900390910190a15b50505050565b670de0b6b3a764000081565b60008281526020819052604090206002015482906001600160a01b0316331461089057600080fd5b6000838152602081905260408120906108a98285611006565b905080156108df5760408051868152602081018390528151600080516020611edc833981519152929181900390910190a161071b565b6007820180546001019081905542600484015560058301556003820154604080518781526001600160a01b03909216602083015280517fbe7699071671fa6d8a4089a86a6ad506b9b9bf927b07a9c2ff44b0204de48b599281900390910190a15050505050565b6000818152602081815260409182902060080180548351818402810184019094528084526060939283018282801561099d57602002820191906000526020600020905b815481526020019060010190808311610989575b505050505090505b919050565b60009081526020819052604090206001015490565b6000818152602081905260408120600581015460068201541180156109eb575060025481600401540142115b9392505050565b60008181526020819052604081206007600a82015460ff166007811115610a1557fe5b1480610a3a575080600501548160060154118015610a3a575060025481600401540142115b15610a6a5760028101546001820154610a609185916001600160a01b03909116906110f7565b60009150506109a5565b80600601548160050154118015610a88575060025481600401540142115b15610aae5760038101546001820154610a609185916001600160a01b0390911690611180565b6040805184815261c36e60208201528151600080516020611edc833981519152929181900390910190a15061c36e92915050565b600060208190529081526040902080546001820154600283015460038401546004850154600586015460068701546007880154600a90980154969795966001600160a01b0395861696959094169492939192909160ff1689565b6000818152602081905260408120600681015460058201541180156109eb575060025460049091015401421192915050565b600081815260208190526040812090610b878284611209565b90508060011415610bb85760038201546001830154610bb39185916001600160a01b0390911690611180565b610be2565b8060021415610be25760028201546001830154610be29185916001600160a01b03909116906110f7565b505050565b600090815260208181526040808320600180820154855260099091019092529091200154600160601b900460ff1690565b60035460009061010090046001600160a01b0316338114610c3857600080fd5b60408051602080820188905233828401526001600160a01b0386166060808401919091528351808403909101815260809092018352815191810191909120600081815291829052919020805415610c8e57600080fd5b81815560018082018890556002820180546001600160a01b03808a166001600160a01b03199283161790925560038401805492891692909116919091179055426004830155600060068301556005820181905560078201819055600a8201805460ff19168280021790555060408051888152602081018490526001600160a01b03808916828401528716606082015290517f403956bdc140717d54d4573786b4e9e773ef2e6e325e2c061476eb47711770de9181900360800190a15095945050505050565b6000908152602081905260409020600a015460ff1690565b60015481565b600882015460009015610d8357600080fd5b6002600a84015460ff166007811115610d9857fe5b1415610f0f57600080610dae85600101546112e6565b505050505093505050915083600185510381518110610dc957fe5b60200260200101518114610de35761c3e692505050610f14565b600260035460ff166002811115610df657fe5b14158015610e075750600154845114155b15610e185761c3fa92505050610f14565b6040516303e6d75f60e61b815260206004820181815286516024840152865173__SyscoinMessageLibrary_________________9363f9b5d7c093899392839260440191808601910280838360005b83811015610e7f578181015183820152602001610e67565b505050509050019250505060206040518083038186803b158015610ea257600080fd5b505af4158015610eb6573d6000803e3d6000fd5b505050506040513d6020811015610ecc57600080fd5b50518214610ee05761c37892505050610f14565b8351610ef59060088701906020870190611e52565b50505050600a8201805460ff191660031790556000610f14565b5061c3645b92915050565b60006001600a83015460ff166007811115610f3157fe5b1415610f6657600a8201805460ff191660021790556003820154336001600160a01b0390911614610f5e57fe5b5060006109a5565b5061c364919050565b60006003600a83015460ff166007811115610f8657fe5b1415610f66576000600183810154600090815260098501602052604090200154600160601b900460ff166002811115610fbb57fe5b14610fc557600080fd5b50600a8101805460ff19166004179055600180820154600090815260098301602052604081209091018054600160601b60ff60601b199091161790556109a5565b60006004600a84015460ff16600781111561101d57fe5b1415610f0f57600061103283600060506113f9565b6008850180549192508291600019810190811061104b57fe5b9060005260206000200154146110665761c3e6915050610f14565b60018085015460009081526009860160205260409020906001820154600160601b900460ff16600281111561109757fe5b146110a85761c36992505050610f14565b60006110b58284876114bc565b905080156110c7579250610f14915050565b50600a8501805460ff191660051790556001018054600160611b60ff60601b199091161790555060009050610f14565b60008381526020819052604090206003810154600282015461112991869185916001600160a01b03908116911661162c565b611132846116b4565b60408051838152602081018690526001600160a01b0385168183015290517faab6a8f22c7ab5131c1cdc1c0000e123efd38efadfef092cef78be507d16542e9181900360600190a150505050565b6000838152602081905260409020600281015460038201546111b291869185916001600160a01b03908116911661162c565b6111bb846116b4565b60408051838152602081018690526001600160a01b0385168183015290517fda9b5fdafb0f67d811425d3f095917a63329d60d3db3aa456b5247a8f3b88b119181900360600190a150505050565b60006005600a84015460ff16600781111561122057fe5b14156112ba57600061123184611725565b9050801561126c5760408051848152602081018390528151600080516020611edc833981519152929181900390910190a16002915050610f14565b61127584611893565b905080156112b05760408051848152602081018390528151600080516020611edc833981519152929181900390910190a16002915050610f14565b6001915050610f14565b6007600a84015460ff1660078111156112cf57fe5b14156112dd57506002610f14565b50600092915050565b6000806000806000806000806000600460009054906101000a90046001600160a01b03166001600160a01b0316636e5b70718b6040518263ffffffff1660e01b8152600401808281526020019150506101206040518083038186803b15801561134e57600080fd5b505afa158015611362573d6000803e3d6000fd5b505050506040513d61012081101561137957600080fd5b810190808051906020019092919080519060200190929190805190602001909291908051906020019092919080519060200190929190805190602001909291908051906020019092919080519060200190929190805190602001909291905050509850985098509850985098509850985098509193959799909294969850565b60006114b4600261140b868686611d48565b604051602001808281526020019150506040516020818303038152906040526040518082805190602001908083835b602083106114595780518252601f19909201916020918201910161143a565b51815160209384036101000a60001901801990921691161790526040519190930194509192505080830381855afa158015611498573d6000803e3d6000fd5b5050506040513d60208110156114ad57600080fd5b5051611d6d565b949350505050565b60008073__SyscoinMessageLibrary_________________6376837a138460008760001c6040518463ffffffff1660e01b81526004018080602001848152602001838152602001828103825285818151815260200191508051906020019080838360005b83811015611538578181015183820152602001611520565b50505050905090810190601f1680156115655780820380516001836020036101000a031916815260200191505b5094505050505060206040518083038186803b15801561158457600080fd5b505af4158015611598573d6000803e3d6000fd5b505050506040513d60208110156115ae57600080fd5b5051905080156115bf5790506109eb565b6115c883611d98565b60018601805467ffffffffffffffff191663ffffffff929092169190911790556115f183611da5565b8560010160086101000a81548163ffffffff021916908363ffffffff16021790555061161c83611db2565b8555505050600290910155600090565b60035460408051633a45007160e11b815260048101879052602481018690526001600160a01b038581166044830152848116606483015291516101009093049091169163748a00e29160848082019260009290919082900301818387803b15801561169657600080fd5b505af11580156116aa573d6000803e3d6000fd5b5050505050505050565b6000818152602081905260408120818155600181018290556002810180546001600160a01b0319908116909155600382018054909116905560048101829055600581018290556006810182905560078101829055906117166008830182611e9d565b50600a01805460ff1916905550565b600881015460009061173a575061c3e66109a5565b60008060008061174d86600101546112e6565b50505060088c018054949a509097509195506000945090925050600019810190811061177557fe5b600091825260208083209091015460018a0154835260098a019091526040909120600889015491925090600210156117ea576008880180546000919060011981019081106117bf57fe5b90600052602060002001549050808260000154146117e85761c40e9750505050505050506109a5565b505b8282146118015761c3e696505050505050506109a5565b600181015467ffffffffffffffff1686146118265761c37396505050505050506109a5565b60026001820154600160601b900460ff16600281111561184257fe5b146118575761c3f096505050505050506109a5565b611860846112e6565b50949b50505050898911159350611885925050505761c37396505050505050506109a5565b506000979650505050505050565b6000806000806001549050600080600080886001015490506118b4816112e6565b9091929394959697509091929394955090919293945090915090508063ffffffff169050809550819950829650839a5050505050600089600901600083815260200190815260200160002090506000881161191b5761c3c8985050505050505050506109a5565b600181015463ffffffff858116600160401b90920416146119485761c42c985050505050505050506109a5565b611951876112e6565b50959b5091995060029550611967945050505050565b60035460ff16600281111561197857fe5b14156119865760088a015495505b84881161199f5761c404985050505050505050506109a5565b600260035460ff1660028111156119b257fe5b14611d38576006600019840106611c5757600181015463ffffffff858116600160401b9092041614156119f15761c422985050505050505050506109a5565b600073__SyscoinMessageLibrary_________________63b199c898600173__SyscoinMessageLibrary_________________635193ab7a6040518163ffffffff1660e01b815260040160206040518083038186803b158015611a5357600080fd5b505af4158015611a67573d6000803e3d6000fd5b505050506040513d6020811015611a7d57600080fd5b5051604080516001600160e01b031960e086901b16815292909103600790810b900b600483015263ffffffff89166024830152516044808301926020929190829003018186803b158015611ad057600080fd5b505af4158015611ae4573d6000803e3d6000fd5b505050506040513d6020811015611afa57600080fd5b50516040805163c9fdef7b60e01b8152905191925060009173__SyscoinMessageLibrary_________________9163b199c89891839163c9fdef7b916004808301926020929190829003018186803b158015611b5557600080fd5b505af4158015611b69573d6000803e3d6000fd5b505050506040513d6020811015611b7f57600080fd5b5051604080516001600160e01b031960e085901b1681526001909201600790810b900b600483015263ffffffff8a166024830152516044808301926020929190829003018186803b158015611bd357600080fd5b505af4158015611be7573d6000803e3d6000fd5b505050506040513d6020811015611bfd57600080fd5b5051600184015490915063ffffffff808416600160401b909204161080611c375750600183015463ffffffff808316600160401b90920416115b15611c505761c4189a50505050505050505050506109a5565b5050611c84565b600181015463ffffffff858116600160401b9092041614611c845761c3d2985050505050505050506109a5565b600181015460408051636b814acd60e11b8152600160401b90920463ffffffff16600483015251600091889173__SyscoinMessageLibrary_________________9163d702959a916024808301926020929190829003018186803b158015611ceb57600080fd5b505af4158015611cff573d6000803e3d6000fd5b505050506040513d6020811015611d1557600080fd5b50510286019050888114611d365761c3c899505050505050505050506109a5565b505b5060009998505050505050505050565b60006040516020818486602089010160025afa611d6457600080fd5b51949350505050565b600060405160005b6020811015611d90578381601f031a81830153600101611d75565b505192915050565b6000610f14826044611dc3565b6000610f14826048611dc3565b60248101516000906109eb81611d6d565b6000828260030181518110611dd457fe5b602001015160f81c60f81b60f81c60ff16630100000002838360020181518110611dfa57fe5b602001015160f81c60f81b60f81c60ff166201000002848460010181518110611e1f57fe5b602001015160f81c60f81b60f81c60ff1661010002858581518110611e4057fe5b016020015160f81c0101019392505050565b828054828255906000526020600020908101928215611e8d579160200282015b82811115611e8d578251825591602001919060010190611e72565b50611e99929150611ebe565b5090565b5080546000825590600052602060002090810190611ebb9190611ebe565b50565b611ed891905b80821115611e995760008155600101611ec4565b9056fe80235326defb5d335564dd77860b0a010e19446427d3d78d155cabd064ca9c2aa265627a7a7230582068e5030985fbb4eec0672510416b3ecd2ba99b9701a01c3003e15b492c41841564736f6c634300050a0032";

    public static final String FUNC_SUPERBLOCKTIMEOUT = "superblockTimeout";

    public static final String FUNC_MINPROPOSALDEPOSIT = "minProposalDeposit";

    public static final String FUNC_SESSIONS = "sessions";

    public static final String FUNC_SUPERBLOCKDURATION = "superblockDuration";

    public static final String FUNC_SETSYSCOINCLAIMMANAGER = "setSyscoinClaimManager";

    public static final String FUNC_BEGINBATTLESESSION = "beginBattleSession";

    public static final String FUNC_QUERYMERKLEROOTHASHES = "queryMerkleRootHashes";

    public static final String FUNC_RESPONDMERKLEROOTHASHES = "respondMerkleRootHashes";

    public static final String FUNC_QUERYLASTBLOCKHEADER = "queryLastBlockHeader";

    public static final String FUNC_RESPONDLASTBLOCKHEADER = "respondLastBlockHeader";

    public static final String FUNC_VERIFYSUPERBLOCK = "verifySuperblock";

    public static final String FUNC_TIMEOUT = "timeout";

    public static final String FUNC_GETCHALLENGERHITTIMEOUT = "getChallengerHitTimeout";

    public static final String FUNC_GETSUBMITTERHITTIMEOUT = "getSubmitterHitTimeout";

    public static final String FUNC_GETSYSCOINBLOCKHASHES = "getSyscoinBlockHashes";

    public static final String FUNC_GETSUPERBLOCKBYSESSION = "getSuperblockBySession";

    public static final String FUNC_GETSESSIONSTATUS = "getSessionStatus";

    public static final String FUNC_GETSESSIONCHALLENGESTATE = "getSessionChallengeState";

    public static final Event NEWBATTLE_EVENT = new Event("NewBattle", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}, new TypeReference<Address>() {}));
    ;

    public static final Event CHALLENGERCONVICTED_EVENT = new Event("ChallengerConvicted", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}));
    ;

    public static final Event SUBMITTERCONVICTED_EVENT = new Event("SubmitterConvicted", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}));
    ;

    public static final Event QUERYMERKLEROOTHASHES_EVENT = new Event("QueryMerkleRootHashes", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}));
    ;

    public static final Event RESPONDMERKLEROOTHASHES_EVENT = new Event("RespondMerkleRootHashes", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}));
    ;

    public static final Event QUERYLASTBLOCKHEADER_EVENT = new Event("QueryLastBlockHeader", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}));
    ;

    public static final Event RESPONDLASTBLOCKHEADER_EVENT = new Event("RespondLastBlockHeader", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}));
    ;

    public static final Event ERRORBATTLE_EVENT = new Event("ErrorBattle", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Uint256>() {}));
    ;

    protected static final HashMap<String, String> _addresses;

    static {
        _addresses = new HashMap<String, String>();
        _addresses.put("4", "0xd276d4fF8110a0653E39bFb7701B8D0435154b38");
    }

    @Deprecated
    protected SyscoinBattleManager(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected SyscoinBattleManager(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected SyscoinBattleManager(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected SyscoinBattleManager(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteCall<Uint256> superblockTimeout() {
        final Function function = new Function(FUNC_SUPERBLOCKTIMEOUT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public RemoteCall<Uint256> minProposalDeposit() {
        final Function function = new Function(FUNC_MINPROPOSALDEPOSIT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public RemoteCall<Tuple9<Bytes32, Bytes32, Address, Address, Uint256, Uint256, Uint256, Uint256, Uint8>> sessions(Bytes32 param0) {
        final Function function = new Function(FUNC_SESSIONS, 
                Arrays.<Type>asList(param0), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}, new TypeReference<Address>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint8>() {}));
        return new RemoteCall<Tuple9<Bytes32, Bytes32, Address, Address, Uint256, Uint256, Uint256, Uint256, Uint8>>(
                new Callable<Tuple9<Bytes32, Bytes32, Address, Address, Uint256, Uint256, Uint256, Uint256, Uint8>>() {
                    @Override
                    public Tuple9<Bytes32, Bytes32, Address, Address, Uint256, Uint256, Uint256, Uint256, Uint8> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple9<Bytes32, Bytes32, Address, Address, Uint256, Uint256, Uint256, Uint256, Uint8>(
                                (Bytes32) results.get(0), 
                                (Bytes32) results.get(1), 
                                (Address) results.get(2), 
                                (Address) results.get(3), 
                                (Uint256) results.get(4), 
                                (Uint256) results.get(5), 
                                (Uint256) results.get(6), 
                                (Uint256) results.get(7), 
                                (Uint8) results.get(8));
                    }
                });
    }

    public RemoteCall<Uint256> superblockDuration() {
        final Function function = new Function(FUNC_SUPERBLOCKDURATION, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public List<NewBattleEventResponse> getNewBattleEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(NEWBATTLE_EVENT, transactionReceipt);
        ArrayList<NewBattleEventResponse> responses = new ArrayList<NewBattleEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            NewBattleEventResponse typedResponse = new NewBattleEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.superblockHash = (Bytes32) eventValues.getNonIndexedValues().get(0);
            typedResponse.sessionId = (Bytes32) eventValues.getNonIndexedValues().get(1);
            typedResponse.submitter = (Address) eventValues.getNonIndexedValues().get(2);
            typedResponse.challenger = (Address) eventValues.getNonIndexedValues().get(3);
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<NewBattleEventResponse> newBattleEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, NewBattleEventResponse>() {
            @Override
            public NewBattleEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(NEWBATTLE_EVENT, log);
                NewBattleEventResponse typedResponse = new NewBattleEventResponse();
                typedResponse.log = log;
                typedResponse.superblockHash = (Bytes32) eventValues.getNonIndexedValues().get(0);
                typedResponse.sessionId = (Bytes32) eventValues.getNonIndexedValues().get(1);
                typedResponse.submitter = (Address) eventValues.getNonIndexedValues().get(2);
                typedResponse.challenger = (Address) eventValues.getNonIndexedValues().get(3);
                return typedResponse;
            }
        });
    }

    public Flowable<NewBattleEventResponse> newBattleEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(NEWBATTLE_EVENT));
        return newBattleEventFlowable(filter);
    }

    public List<ChallengerConvictedEventResponse> getChallengerConvictedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(CHALLENGERCONVICTED_EVENT, transactionReceipt);
        ArrayList<ChallengerConvictedEventResponse> responses = new ArrayList<ChallengerConvictedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ChallengerConvictedEventResponse typedResponse = new ChallengerConvictedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.superblockHash = (Bytes32) eventValues.getNonIndexedValues().get(0);
            typedResponse.sessionId = (Bytes32) eventValues.getNonIndexedValues().get(1);
            typedResponse.challenger = (Address) eventValues.getNonIndexedValues().get(2);
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<ChallengerConvictedEventResponse> challengerConvictedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, ChallengerConvictedEventResponse>() {
            @Override
            public ChallengerConvictedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(CHALLENGERCONVICTED_EVENT, log);
                ChallengerConvictedEventResponse typedResponse = new ChallengerConvictedEventResponse();
                typedResponse.log = log;
                typedResponse.superblockHash = (Bytes32) eventValues.getNonIndexedValues().get(0);
                typedResponse.sessionId = (Bytes32) eventValues.getNonIndexedValues().get(1);
                typedResponse.challenger = (Address) eventValues.getNonIndexedValues().get(2);
                return typedResponse;
            }
        });
    }

    public Flowable<ChallengerConvictedEventResponse> challengerConvictedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(CHALLENGERCONVICTED_EVENT));
        return challengerConvictedEventFlowable(filter);
    }

    public List<SubmitterConvictedEventResponse> getSubmitterConvictedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(SUBMITTERCONVICTED_EVENT, transactionReceipt);
        ArrayList<SubmitterConvictedEventResponse> responses = new ArrayList<SubmitterConvictedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            SubmitterConvictedEventResponse typedResponse = new SubmitterConvictedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.superblockHash = (Bytes32) eventValues.getNonIndexedValues().get(0);
            typedResponse.sessionId = (Bytes32) eventValues.getNonIndexedValues().get(1);
            typedResponse.submitter = (Address) eventValues.getNonIndexedValues().get(2);
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<SubmitterConvictedEventResponse> submitterConvictedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, SubmitterConvictedEventResponse>() {
            @Override
            public SubmitterConvictedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(SUBMITTERCONVICTED_EVENT, log);
                SubmitterConvictedEventResponse typedResponse = new SubmitterConvictedEventResponse();
                typedResponse.log = log;
                typedResponse.superblockHash = (Bytes32) eventValues.getNonIndexedValues().get(0);
                typedResponse.sessionId = (Bytes32) eventValues.getNonIndexedValues().get(1);
                typedResponse.submitter = (Address) eventValues.getNonIndexedValues().get(2);
                return typedResponse;
            }
        });
    }

    public Flowable<SubmitterConvictedEventResponse> submitterConvictedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(SUBMITTERCONVICTED_EVENT));
        return submitterConvictedEventFlowable(filter);
    }

    public List<QueryMerkleRootHashesEventResponse> getQueryMerkleRootHashesEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(QUERYMERKLEROOTHASHES_EVENT, transactionReceipt);
        ArrayList<QueryMerkleRootHashesEventResponse> responses = new ArrayList<QueryMerkleRootHashesEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            QueryMerkleRootHashesEventResponse typedResponse = new QueryMerkleRootHashesEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.superblockHash = (Bytes32) eventValues.getNonIndexedValues().get(0);
            typedResponse.sessionId = (Bytes32) eventValues.getNonIndexedValues().get(1);
            typedResponse.submitter = (Address) eventValues.getNonIndexedValues().get(2);
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<QueryMerkleRootHashesEventResponse> queryMerkleRootHashesEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, QueryMerkleRootHashesEventResponse>() {
            @Override
            public QueryMerkleRootHashesEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(QUERYMERKLEROOTHASHES_EVENT, log);
                QueryMerkleRootHashesEventResponse typedResponse = new QueryMerkleRootHashesEventResponse();
                typedResponse.log = log;
                typedResponse.superblockHash = (Bytes32) eventValues.getNonIndexedValues().get(0);
                typedResponse.sessionId = (Bytes32) eventValues.getNonIndexedValues().get(1);
                typedResponse.submitter = (Address) eventValues.getNonIndexedValues().get(2);
                return typedResponse;
            }
        });
    }

    public Flowable<QueryMerkleRootHashesEventResponse> queryMerkleRootHashesEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(QUERYMERKLEROOTHASHES_EVENT));
        return queryMerkleRootHashesEventFlowable(filter);
    }

    public List<RespondMerkleRootHashesEventResponse> getRespondMerkleRootHashesEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(RESPONDMERKLEROOTHASHES_EVENT, transactionReceipt);
        ArrayList<RespondMerkleRootHashesEventResponse> responses = new ArrayList<RespondMerkleRootHashesEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            RespondMerkleRootHashesEventResponse typedResponse = new RespondMerkleRootHashesEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.superblockHash = (Bytes32) eventValues.getNonIndexedValues().get(0);
            typedResponse.sessionId = (Bytes32) eventValues.getNonIndexedValues().get(1);
            typedResponse.challenger = (Address) eventValues.getNonIndexedValues().get(2);
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<RespondMerkleRootHashesEventResponse> respondMerkleRootHashesEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, RespondMerkleRootHashesEventResponse>() {
            @Override
            public RespondMerkleRootHashesEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(RESPONDMERKLEROOTHASHES_EVENT, log);
                RespondMerkleRootHashesEventResponse typedResponse = new RespondMerkleRootHashesEventResponse();
                typedResponse.log = log;
                typedResponse.superblockHash = (Bytes32) eventValues.getNonIndexedValues().get(0);
                typedResponse.sessionId = (Bytes32) eventValues.getNonIndexedValues().get(1);
                typedResponse.challenger = (Address) eventValues.getNonIndexedValues().get(2);
                return typedResponse;
            }
        });
    }

    public Flowable<RespondMerkleRootHashesEventResponse> respondMerkleRootHashesEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(RESPONDMERKLEROOTHASHES_EVENT));
        return respondMerkleRootHashesEventFlowable(filter);
    }

    public List<QueryLastBlockHeaderEventResponse> getQueryLastBlockHeaderEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(QUERYLASTBLOCKHEADER_EVENT, transactionReceipt);
        ArrayList<QueryLastBlockHeaderEventResponse> responses = new ArrayList<QueryLastBlockHeaderEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            QueryLastBlockHeaderEventResponse typedResponse = new QueryLastBlockHeaderEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.sessionId = (Bytes32) eventValues.getNonIndexedValues().get(0);
            typedResponse.submitter = (Address) eventValues.getNonIndexedValues().get(1);
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<QueryLastBlockHeaderEventResponse> queryLastBlockHeaderEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, QueryLastBlockHeaderEventResponse>() {
            @Override
            public QueryLastBlockHeaderEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(QUERYLASTBLOCKHEADER_EVENT, log);
                QueryLastBlockHeaderEventResponse typedResponse = new QueryLastBlockHeaderEventResponse();
                typedResponse.log = log;
                typedResponse.sessionId = (Bytes32) eventValues.getNonIndexedValues().get(0);
                typedResponse.submitter = (Address) eventValues.getNonIndexedValues().get(1);
                return typedResponse;
            }
        });
    }

    public Flowable<QueryLastBlockHeaderEventResponse> queryLastBlockHeaderEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(QUERYLASTBLOCKHEADER_EVENT));
        return queryLastBlockHeaderEventFlowable(filter);
    }

    public List<RespondLastBlockHeaderEventResponse> getRespondLastBlockHeaderEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(RESPONDLASTBLOCKHEADER_EVENT, transactionReceipt);
        ArrayList<RespondLastBlockHeaderEventResponse> responses = new ArrayList<RespondLastBlockHeaderEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            RespondLastBlockHeaderEventResponse typedResponse = new RespondLastBlockHeaderEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.sessionId = (Bytes32) eventValues.getNonIndexedValues().get(0);
            typedResponse.challenger = (Address) eventValues.getNonIndexedValues().get(1);
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<RespondLastBlockHeaderEventResponse> respondLastBlockHeaderEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, RespondLastBlockHeaderEventResponse>() {
            @Override
            public RespondLastBlockHeaderEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(RESPONDLASTBLOCKHEADER_EVENT, log);
                RespondLastBlockHeaderEventResponse typedResponse = new RespondLastBlockHeaderEventResponse();
                typedResponse.log = log;
                typedResponse.sessionId = (Bytes32) eventValues.getNonIndexedValues().get(0);
                typedResponse.challenger = (Address) eventValues.getNonIndexedValues().get(1);
                return typedResponse;
            }
        });
    }

    public Flowable<RespondLastBlockHeaderEventResponse> respondLastBlockHeaderEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(RESPONDLASTBLOCKHEADER_EVENT));
        return respondLastBlockHeaderEventFlowable(filter);
    }

    public List<ErrorBattleEventResponse> getErrorBattleEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(ERRORBATTLE_EVENT, transactionReceipt);
        ArrayList<ErrorBattleEventResponse> responses = new ArrayList<ErrorBattleEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ErrorBattleEventResponse typedResponse = new ErrorBattleEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.sessionId = (Bytes32) eventValues.getNonIndexedValues().get(0);
            typedResponse.err = (Uint256) eventValues.getNonIndexedValues().get(1);
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<ErrorBattleEventResponse> errorBattleEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, ErrorBattleEventResponse>() {
            @Override
            public ErrorBattleEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(ERRORBATTLE_EVENT, log);
                ErrorBattleEventResponse typedResponse = new ErrorBattleEventResponse();
                typedResponse.log = log;
                typedResponse.sessionId = (Bytes32) eventValues.getNonIndexedValues().get(0);
                typedResponse.err = (Uint256) eventValues.getNonIndexedValues().get(1);
                return typedResponse;
            }
        });
    }

    public Flowable<ErrorBattleEventResponse> errorBattleEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(ERRORBATTLE_EVENT));
        return errorBattleEventFlowable(filter);
    }

    public RemoteCall<TransactionReceipt> setSyscoinClaimManager(Address _syscoinClaimManager) {
        final Function function = new Function(
                FUNC_SETSYSCOINCLAIMMANAGER, 
                Arrays.<Type>asList(_syscoinClaimManager), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> beginBattleSession(Bytes32 superblockHash, Address submitter, Address challenger) {
        final Function function = new Function(
                FUNC_BEGINBATTLESESSION, 
                Arrays.<Type>asList(superblockHash, submitter, challenger), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> queryMerkleRootHashes(Bytes32 superblockHash, Bytes32 sessionId) {
        final Function function = new Function(
                FUNC_QUERYMERKLEROOTHASHES, 
                Arrays.<Type>asList(superblockHash, sessionId), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> respondMerkleRootHashes(Bytes32 superblockHash, Bytes32 sessionId, DynamicArray<Bytes32> blockHashes) {
        final Function function = new Function(
                FUNC_RESPONDMERKLEROOTHASHES, 
                Arrays.<Type>asList(superblockHash, sessionId, blockHashes), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> queryLastBlockHeader(Bytes32 sessionId) {
        final Function function = new Function(
                FUNC_QUERYLASTBLOCKHEADER, 
                Arrays.<Type>asList(sessionId), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> respondLastBlockHeader(Bytes32 sessionId, DynamicBytes blockHeader) {
        final Function function = new Function(
                FUNC_RESPONDLASTBLOCKHEADER, 
                Arrays.<Type>asList(sessionId, blockHeader), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> verifySuperblock(Bytes32 sessionId) {
        final Function function = new Function(
                FUNC_VERIFYSUPERBLOCK, 
                Arrays.<Type>asList(sessionId), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> timeout(Bytes32 sessionId) {
        final Function function = new Function(
                FUNC_TIMEOUT, 
                Arrays.<Type>asList(sessionId), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Bool> getChallengerHitTimeout(Bytes32 sessionId) {
        final Function function = new Function(FUNC_GETCHALLENGERHITTIMEOUT, 
                Arrays.<Type>asList(sessionId), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public RemoteCall<Bool> getSubmitterHitTimeout(Bytes32 sessionId) {
        final Function function = new Function(FUNC_GETSUBMITTERHITTIMEOUT, 
                Arrays.<Type>asList(sessionId), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public RemoteCall<DynamicArray<Bytes32>> getSyscoinBlockHashes(Bytes32 sessionId) {
        final Function function = new Function(FUNC_GETSYSCOINBLOCKHASHES, 
                Arrays.<Type>asList(sessionId), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Bytes32>>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public RemoteCall<Bytes32> getSuperblockBySession(Bytes32 sessionId) {
        final Function function = new Function(FUNC_GETSUPERBLOCKBYSESSION, 
                Arrays.<Type>asList(sessionId), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public RemoteCall<Uint8> getSessionStatus(Bytes32 sessionId) {
        final Function function = new Function(FUNC_GETSESSIONSTATUS, 
                Arrays.<Type>asList(sessionId), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public RemoteCall<Uint8> getSessionChallengeState(Bytes32 sessionId) {
        final Function function = new Function(FUNC_GETSESSIONCHALLENGESTATE, 
                Arrays.<Type>asList(sessionId), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    @Deprecated
    public static SyscoinBattleManager load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new SyscoinBattleManager(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static SyscoinBattleManager load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new SyscoinBattleManager(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static SyscoinBattleManager load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new SyscoinBattleManager(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static SyscoinBattleManager load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new SyscoinBattleManager(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<SyscoinBattleManager> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider, Uint8 _network, Address _superblocks, Uint256 _superblockDuration, Uint256 _superblockTimeout) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(_network, _superblocks, _superblockDuration, _superblockTimeout));
        return deployRemoteCall(SyscoinBattleManager.class, web3j, credentials, contractGasProvider, BINARY, encodedConstructor);
    }

    public static RemoteCall<SyscoinBattleManager> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider, Uint8 _network, Address _superblocks, Uint256 _superblockDuration, Uint256 _superblockTimeout) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(_network, _superblocks, _superblockDuration, _superblockTimeout));
        return deployRemoteCall(SyscoinBattleManager.class, web3j, transactionManager, contractGasProvider, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<SyscoinBattleManager> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, Uint8 _network, Address _superblocks, Uint256 _superblockDuration, Uint256 _superblockTimeout) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(_network, _superblocks, _superblockDuration, _superblockTimeout));
        return deployRemoteCall(SyscoinBattleManager.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<SyscoinBattleManager> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, Uint8 _network, Address _superblocks, Uint256 _superblockDuration, Uint256 _superblockTimeout) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(_network, _superblocks, _superblockDuration, _superblockTimeout));
        return deployRemoteCall(SyscoinBattleManager.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    protected String getStaticDeployedAddress(String networkId) {
        return _addresses.get(networkId);
    }

    public static String getPreviouslyDeployedAddress(String networkId) {
        return _addresses.get(networkId);
    }

    public static class NewBattleEventResponse {
        public Log log;

        public Bytes32 superblockHash;

        public Bytes32 sessionId;

        public Address submitter;

        public Address challenger;
    }

    public static class ChallengerConvictedEventResponse {
        public Log log;

        public Bytes32 superblockHash;

        public Bytes32 sessionId;

        public Address challenger;
    }

    public static class SubmitterConvictedEventResponse {
        public Log log;

        public Bytes32 superblockHash;

        public Bytes32 sessionId;

        public Address submitter;
    }

    public static class QueryMerkleRootHashesEventResponse {
        public Log log;

        public Bytes32 superblockHash;

        public Bytes32 sessionId;

        public Address submitter;
    }

    public static class RespondMerkleRootHashesEventResponse {
        public Log log;

        public Bytes32 superblockHash;

        public Bytes32 sessionId;

        public Address challenger;
    }

    public static class QueryLastBlockHeaderEventResponse {
        public Log log;

        public Bytes32 sessionId;

        public Address submitter;
    }

    public static class RespondLastBlockHeaderEventResponse {
        public Log log;

        public Bytes32 sessionId;

        public Address challenger;
    }

    public static class ErrorBattleEventResponse {
        public Log log;

        public Bytes32 sessionId;

        public Uint256 err;
    }
}
