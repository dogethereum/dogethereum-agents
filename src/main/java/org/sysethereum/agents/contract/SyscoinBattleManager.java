package org.sysethereum.agents.contract;

import io.reactivex.Flowable;
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
    private static final String BINARY = "0x608060405234801561001057600080fd5b506040516121dd3803806121dd8339818101604052608081101561003357600080fd5b50805160208201516040830151606090930151600380549394929385919060ff1916600183600281111561006357fe5b0217905550600480546001600160a01b0319166001600160a01b0394909416939093179092556001556002555061213e8061009f6000396000f3fe608060405234801561001057600080fd5b50600436106101165760003560e01c8063795ea18e116100a2578063d1daeede11610071578063d1daeede146104be578063eab5247d146104f2578063ec6dbad81461050f578063f1afcfa61461053c578063fa06d8901461054457610116565b8063795ea18e1461042657806399b32f3a14610443578063a6c07c9614610460578063abbb6bf61461047d57610116565b8063232355b8116100e9578063232355b81461033d5780633678c143146103aa578063455e6166146103d057806357bd24fb146103d857806371a8c18a146103f557610116565b8063089845e11461011b5780630f2c63ff146101cc57806313fef72c146101ef57806318b011de14610323575b600080fd5b6101ca6004803603606081101561013157600080fd5b81359160208101359181019060608101604082013564010000000081111561015857600080fd5b82018360208201111561016a57600080fd5b8035906020019184602083028401116401000000008311171561018c57600080fd5b919080806020026020016040519081016040528093929190818152602001838360200280828437600092019190915250929550610567945050505050565b005b6101ca600480360360408110156101e257600080fd5b508035906020013561064c565b6101ca6004803603606081101561020557600080fd5b8135919081019060408101602082013564010000000081111561022757600080fd5b82018360208201111561023957600080fd5b8035906020019184600183028401116401000000008311171561025b57600080fd5b91908080601f01602080910402602001604051908101604052809392919081815260200183838082843760009201919091525092959493602081019350359150506401000000008111156102ae57600080fd5b8201836020820111156102c057600080fd5b803590602001918460018302840111640100000000831117156102e257600080fd5b91908080601f01602080910402602001604051908101604052809392919081815260200183838082843760009201919091525092955061072f945050505050565b61032b61080f565b60408051918252519081900360200190f35b61035a6004803603602081101561035357600080fd5b5035610815565b60408051602080825283518183015283519192839290830191858101910280838360005b8381101561039657818101518382015260200161037e565b505050509050019250505060405180910390f35b6101ca600480360360208110156103c057600080fd5b50356001600160a01b0316610879565b61032b6108d0565b61032b600480360360208110156103ee57600080fd5b50356108dc565b6104126004803603602081101561040b57600080fd5b50356108f1565b604080519115158252519081900360200190f35b61032b6004803603602081101561043c57600080fd5b5035610924565b6104126004803603602081101561045957600080fd5b5035610a16565b6101ca6004803603602081101561047657600080fd5b5035610a48565b61049a6004803603602081101561049357600080fd5b5035610ac3565b604051808260028111156104aa57fe5b60ff16815260200191505060405180910390f35b61032b600480360360608110156104d457600080fd5b508035906001600160a01b0360208201358116916040013516610ae2565b61032b6004803603602081101561050857600080fd5b5035610c1d565b61052c6004803603602081101561052557600080fd5b5035610c32565b604051808260078111156104aa57fe5b61032b610c4a565b6101ca6004803603604081101561055a57600080fd5b5080359060200135610c50565b60008281526020819052604090206002015482906001600160a01b0316331461058f57600080fd5b6000838152602081905260408120906105a88285610d2e565b905080156105de57604080518681526020810183905281516000805160206120ea833981519152929181900390910190a1610644565b600782018054600101908190554260048401556005830155600382015460408051888152602081018890526001600160a01b0390921682820152517fa35dfb6661c6bfda214c0a2820d9b4bd1a673e2c303cf1578b5948a6f63b5a669181900360600190a15b505050505050565b60008181526020819052604090206003015481906001600160a01b0316331461067457600080fd5b60008281526020819052604081209061068c82610ed7565b905080156106c257604080518581526020810183905281516000805160206120ea833981519152929181900390910190a1610728565b600782018054600101908190554260048401556006830155600282015460408051878152602081018790526001600160a01b0390921682820152517f0817bf136ff95abb2d41d10a9fa5ff6652ff71c13e1b46717fae65db16423c9b9181900360600190a15b5050505050565b60008381526020819052604090206002015483906001600160a01b0316331461075757600080fd5b600084815260208190526040812090610771828686610f2c565b905080156107a757604080518781526020810183905281516000805160206120ea833981519152929181900390910190a1610644565b6007820180546001019081905542600484015560058301556003820154604080518881526001600160a01b03909216602083015280517fbe7699071671fa6d8a4089a86a6ad506b9b9bf927b07a9c2ff44b0204de48b599281900390910190a1505050505050565b60025481565b6000818152602081815260409182902060090180548351818402810184019094528084526060939283018282801561086c57602002820191906000526020600020905b815481526020019060010190808311610858575b505050505090505b919050565b60035461010090046001600160a01b031615801561089f57506001600160a01b03811615155b6108a857600080fd5b600380546001600160a01b0390921661010002610100600160a81b0319909216919091179055565b6729a2241af62c000081565b60009081526020819052604090206001015490565b60008181526020819052604081206005810154600682015411801561091d575060025481600401540142115b9392505050565b60008181526020819052604081206007600d82015460ff16600781111561094757fe5b148061096c57508060050154816006015411801561096c575060025481600401540142115b1561099c57600281015460018201546109929185916001600160a01b03909116906111f4565b6000915050610874565b806006015481600501541180156109ba575060025481600401540142115b156109e25760038101546001808301546109929286926001600160a01b039091169190611282565b6040805184815261c36e602082015281516000805160206120ea833981519152929181900390910190a15061c36e92915050565b60008181526020819052604081206006810154600582015411801561091d575060025460049091015401421192915050565b600081815260208190526040812090610a61828461130d565b90508060011415610a945760038201546001830154610a8f9185916001600160a01b03909116906000611282565b610abe565b8060021415610abe5760028201546001830154610abe9185916001600160a01b03909116906111f4565b505050565b6000908152602081905260409020600b0154600160601b900460ff1690565b60035460009061010090046001600160a01b0316338114610b0257600080fd5b60408051602080820188905233828401526001600160a01b0386166060808401919091528351808403909101815260809092018352815191810191909120600081815291829052919020805415610b5857600080fd5b81815560018082018890556002820180546001600160a01b03808a166001600160a01b03199283161790925560038401805492891692909116919091179055426004830155600060068301556005820181905560078201819055600d8201805460ff19168280021790555060408051888152602081018490526001600160a01b03808916828401528716606082015290517f403956bdc140717d54d4573786b4e9e773ef2e6e325e2c061476eb47711770de9181900360800190a15095945050505050565b60009081526020819052604090206008015490565b6000908152602081905260409020600d015460ff1690565b60015481565b60008281526020819052604090206003015482906001600160a01b03163314610c7857600080fd5b600083815260208190526040812090610c9182856113ea565b90508015610cc757604080518681526020810183905281516000805160206120ea833981519152929181900390910190a1610728565b6007820180546001019081905542600484015560068301556002820154604080518781526001600160a01b03909216602083015280517fa0fbaa47643ff31cddc88d15082b689c8e90d1d48c12a4828762add3eb31be229281900390910190a15050505050565b600982015460009015610d4057600080fd5b6002600d84015460ff166007811115610d5557fe5b1415610ecc57600080610d6b8560010154611477565b505050505093505050915083600185510381518110610d8657fe5b60200260200101518114610da05761c3e692505050610ed1565b600260035460ff166002811115610db357fe5b14158015610dc45750600154845114155b15610dd55761c3fa92505050610ed1565b6040516303e6d75f60e61b815260206004820181815286516024840152865173__SyscoinMessageLibrary_________________9363f9b5d7c093899392839260440191808601910280838360005b83811015610e3c578181015183820152602001610e24565b505050509050019250505060206040518083038186803b158015610e5f57600080fd5b505af4158015610e73573d6000803e3d6000fd5b505050506040513d6020811015610e8957600080fd5b50518214610e9d5761c37892505050610ed1565b8351610eb29060098701906020870190612060565b50505050600d8201805460ff191660031790556000610ed1565b5061c3645b92915050565b60006001600d83015460ff166007811115610eee57fe5b1415610f2357600d8201805460ff191660021790556003820154336001600160a01b0390911614610f1b57fe5b506000610874565b5061c364919050565b60006004600d85015460ff166007811115610f4357fe5b14156111e9576000610f58846000605061158a565b60098601805491925060001982019183919083908110610f7457fe5b906000526020600020015414610f905761c3e69250505061091d565b600a8601600180820154600160601b900460ff166002811115610faf57fe5b14610fc15761c369935050505061091d565b6000610fce82858961164d565b90508015610fe157935061091d92505050565b82886008015414158015610ff457508551155b156110075761c44094505050505061091d565b6000865111801561101c575060008860080154115b156111b7576000611030876000605061158a565b60001b905080896009018a600801548154811061104957fe5b9060005260206000200154146110685761c4369550505050505061091d565b73__SyscoinMessageLibrary_________________6376837a138860008460001c6040518463ffffffff1660e01b81526004018080602001848152602001838152602001828103825285818151815260200191508051906020019080838360005b838110156110e15781810151838201526020016110c9565b50505050905090810190601f16801561110e5780820380516001836020036101000a031916815260200191505b5094505050505060206040518083038186803b15801561112d57600080fd5b505af4158015611141573d6000803e3d6000fd5b505050506040513d602081101561115757600080fd5b50519150811561116d5750935061091d92505050565b6000611178886117bd565b60001b90508960090160018b60080154038154811061119357fe5b906000526020600020015481146111b45761c44a965050505050505061091d565b50505b50600d8701805460ff191660051790556001018054600160611b60ff60601b19909116179055506000915061091d9050565b5061c3649392505050565b600083815260208190526040812060038101546002820154919261122b92879286926001600160a01b0391821692909116906117ce565b6112348461185f565b60408051838152602081018690526001600160a01b0385168183015290517faab6a8f22c7ab5131c1cdc1c0000e123efd38efadfef092cef78be507d16542e9181900360600190a150505050565b6000848152602081905260409020600281015460038201546112b591879186916001600160a01b039081169116866117ce565b6112be8561185f565b60408051848152602081018790526001600160a01b0386168183015290517fda9b5fdafb0f67d811425d3f095917a63329d60d3db3aa456b5247a8f3b88b119181900360600190a15050505050565b60006005600d84015460ff16600781111561132457fe5b14156113be576000611335846118fd565b9050801561137057604080518481526020810183905281516000805160206120ea833981519152929181900390910190a16002915050610ed1565b61137984611a63565b905080156113b457604080518481526020810183905281516000805160206120ea833981519152929181900390910190a16002915050610ed1565b6001915050610ed1565b6007600d84015460ff1660078111156113d357fe5b14156113e157506002610ed1565b50600092915050565b60006003600d84015460ff16600781111561140157fe5b1415610ecc576000600b840154600160601b900460ff16600281111561142357fe5b1461142d57600080fd5b600d8301805460ff19166004179055600b83018054600160601b60ff60601b199091161790556009830154821115611468575061c454610ed1565b50600882018190556000610ed1565b6000806000806000806000806000600460009054906101000a90046001600160a01b03166001600160a01b0316636e5b70718b6040518263ffffffff1660e01b8152600401808281526020019150506101206040518083038186803b1580156114df57600080fd5b505afa1580156114f3573d6000803e3d6000fd5b505050506040513d61012081101561150a57600080fd5b810190808051906020019092919080519060200190929190805190602001909291908051906020019092919080519060200190929190805190602001909291908051906020019092919080519060200190929190805190602001909291905050509850985098509850985098509850985098509193959799909294969850565b6000611645600261159c868686611ebf565b604051602001808281526020019150506040516020818303038152906040526040518082805190602001908083835b602083106115ea5780518252601f1990920191602091820191016115cb565b51815160209384036101000a60001901801990921691161790526040519190930194509192505080830381855afa158015611629573d6000803e3d6000fd5b5050506040513d602081101561163e57600080fd5b5051611ee4565b949350505050565b60008073__SyscoinMessageLibrary_________________6376837a138460008760001c6040518463ffffffff1660e01b81526004018080602001848152602001838152602001828103825285818151815260200191508051906020019080838360005b838110156116c95781810151838201526020016116b1565b50505050905090810190601f1680156116f65780820380516001836020036101000a031916815260200191505b5094505050505060206040518083038186803b15801561171557600080fd5b505af4158015611729573d6000803e3d6000fd5b505050506040513d602081101561173f57600080fd5b50519050801561175057905061091d565b6117598361200e565b60018601805467ffffffffffffffff191663ffffffff929092169190911790556117828361201b565b8560010160086101000a81548163ffffffff021916908363ffffffff1602179055506117ad836117bd565b8555505050600290910155600090565b602481015160009061091d81611ee4565b600354604080516308088ce560e11b815260048101889052602481018790526001600160a01b0386811660448301528581166064830152841515608483015291516101009093049091169163101119ca9160a48082019260009290919082900301818387803b15801561184057600080fd5b505af1158015611854573d6000803e3d6000fd5b505050505050505050565b6000818152602081905260408120818155600181018290556002810180546001600160a01b031990811690915560038201805490911690556004810182905560058101829055600681018290556007810182905560088101829055906118c860098301826120ab565b506000600a8201819055600b820180546cffffffffffffffffffffffffff19169055600c820155600d01805460ff1916905550565b6009810154600090611912575061c3e6610874565b6000806000806119258660010154611477565b50505060098c018054949a509097509195506000945090925050600019810190811061194d57fe5b6000918252602090912001549050600a8701600260035460ff16600281111561197257fe5b146119ba5760098801805460009190600119810190811061198f57fe5b90600052602060002001549050808260000154146119b85761c40e975050505050505050610874565b505b8282146119d15761c3e69650505050505050610874565b600181015467ffffffffffffffff1686146119f65761c3739650505050505050610874565b60026001820154600160601b900460ff166002811115611a1257fe5b14611a275761c3f09650505050505050610874565b611a3084611477565b50949b50505050898911159350611a55925050505761c3739650505050505050610874565b506000979650505050505050565b600080600080600080600087600101549050611a7e81611477565b969d50919b509198505063ffffffff909316955050600a8b019250505086611ab15761c3c8975050505050505050610874565b600181015463ffffffff858116600160401b9092041614611add5761c42c975050505050505050610874565b611ae686611477565b50959b509199505050888b119350611b0d925050505761c404975050505050505050610874565b600060035460ff166002811115611b2057fe5b1415611eb0576006600119840106611dc457600181015463ffffffff858116600160401b909204161415611b5f5761c422975050505050505050610874565b600073__SyscoinMessageLibrary_________________63b199c898600173__SyscoinMessageLibrary_________________635193ab7a6040518163ffffffff1660e01b815260040160206040518083038186803b158015611bc157600080fd5b505af4158015611bd5573d6000803e3d6000fd5b505050506040513d6020811015611beb57600080fd5b5051604080516001600160e01b031960e086901b16815292909103600790810b900b600483015263ffffffff89166024830152516044808301926020929190829003018186803b158015611c3e57600080fd5b505af4158015611c52573d6000803e3d6000fd5b505050506040513d6020811015611c6857600080fd5b50516040805163c9fdef7b60e01b8152905191925060009173__SyscoinMessageLibrary_________________9163b199c89891839163c9fdef7b916004808301926020929190829003018186803b158015611cc357600080fd5b505af4158015611cd7573d6000803e3d6000fd5b505050506040513d6020811015611ced57600080fd5b5051604080516001600160e01b031960e085901b1681526001909201600790810b900b600483015263ffffffff8a166024830152516044808301926020929190829003018186803b158015611d4157600080fd5b505af4158015611d55573d6000803e3d6000fd5b505050506040513d6020811015611d6b57600080fd5b5051600184015490915063ffffffff808416600160401b909204161080611da55750600183015463ffffffff808316600160401b90920416115b15611dbd5761c4189950505050505050505050610874565b5050611df0565b600181015463ffffffff858116600160401b9092041614611df05761c3d2975050505050505050610874565b600060015473__SyscoinMessageLibrary_________________63d702959a8460010160089054906101000a900463ffffffff166040518263ffffffff1660e01b8152600401808263ffffffff1663ffffffff16815260200191505060206040518083038186803b158015611e6457600080fd5b505af4158015611e78573d6000803e3d6000fd5b505050506040513d6020811015611e8e57600080fd5b50510286019050878114611eae5761c3c898505050505050505050610874565b505b50600098975050505050505050565b60006040516020818486602089010160025afa611edb57600080fd5b51949350505050565b60405160009060ff8316815382601e1a600182015382601d1a600282015382601c1a600382015382601b1a600482015382601a1a60058201538260191a60068201538260181a60078201538260171a60088201538260161a60098201538260151a600a8201538260141a600b8201538260131a600c8201538260121a600d8201538260111a600e8201538260101a600f82015382600f1a601082015382600e1a601182015382600d1a601282015382600c1a601382015382600b1a601482015382600a1a60158201538260091a60168201538260081a60178201538260071a60188201538260061a60198201538260051a601a8201538260041a601b8201538260031a601c8201538260021a601d8201538260011a601e8201538260001a601f8201535192915050565b6000610ed1826044612024565b6000610ed18260485b6000816020840101516040518160031a60008201538160021a60018201538160011a60028201538160001a60038201535160e01c949350505050565b82805482825590600052602060002090810192821561209b579160200282015b8281111561209b578251825591602001919060010190612080565b506120a79291506120cc565b5090565b50805460008255906000526020600020908101906120c991906120cc565b50565b6120e691905b808211156120a757600081556001016120d2565b9056fe80235326defb5d335564dd77860b0a010e19446427d3d78d155cabd064ca9c2aa265627a7a72305820e555f7c29b0318f66b674333ebd8fe64fddbd5f750cec9a6fa6a199c90a69f0464736f6c634300050a0032";

    public static final String FUNC_SUPERBLOCKTIMEOUT = "superblockTimeout";

    public static final String FUNC_MINPROPOSALDEPOSIT = "minProposalDeposit";

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

    public static final String FUNC_GETSUPERBLOCKBYSESSION = "getSuperblockBySession";

    public static final String FUNC_GETBLOCKHASHESBYSESSION = "getBlockHashesBySession";

    public static final String FUNC_GETINVALIDATEDBLOCKINDEXBYSESSION = "getInvalidatedBlockIndexBySession";

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

    public RemoteCall<TransactionReceipt> queryLastBlockHeader(Bytes32 sessionId, Uint256 blockIndexInvalidated) {
        final Function function = new Function(
                FUNC_QUERYLASTBLOCKHEADER, 
                Arrays.<Type>asList(sessionId, blockIndexInvalidated), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> respondLastBlockHeader(Bytes32 sessionId, DynamicBytes blockLastHeader, DynamicBytes blockInterimHeader) {
        final Function function = new Function(
                FUNC_RESPONDLASTBLOCKHEADER, 
                Arrays.<Type>asList(sessionId, blockLastHeader, blockInterimHeader), 
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

    public RemoteCall<Bytes32> getSuperblockBySession(Bytes32 sessionId) {
        final Function function = new Function(FUNC_GETSUPERBLOCKBYSESSION, 
                Arrays.<Type>asList(sessionId), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public RemoteCall<DynamicArray<Bytes32>> getBlockHashesBySession(Bytes32 sessionId) {
        final Function function = new Function(FUNC_GETBLOCKHASHESBYSESSION, 
                Arrays.<Type>asList(sessionId), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Bytes32>>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public RemoteCall<Uint256> getInvalidatedBlockIndexBySession(Bytes32 sessionId) {
        final Function function = new Function(FUNC_GETINVALIDATEDBLOCKINDEXBYSESSION, 
                Arrays.<Type>asList(sessionId), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
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
