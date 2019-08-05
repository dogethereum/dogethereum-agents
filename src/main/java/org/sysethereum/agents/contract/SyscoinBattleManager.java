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
    private static final String BINARY = "0x608060405234801561001057600080fd5b50604051611e7a380380611e7a8339818101604052608081101561003357600080fd5b50805160208201516040830151606090930151600380549394929385919060ff1916600183600281111561006357fe5b0217905550600480546001600160a01b0319166001600160a01b03949094169390931790925560015560025550611ddb8061009f6000396000f3fe608060405234801561001057600080fd5b506004361061010b5760003560e01c806357bd24fb116100a2578063a6c07c9611610071578063a6c07c96146103eb578063abbb6bf614610408578063d1daeede14610449578063ec6dbad81461047d578063f1afcfa6146104aa5761010b565b806357bd24fb1461036357806371a8c18a14610380578063795ea18e146103b157806399b32f3a146103ce5761010b565b80633e8a873d116100de5780633e8a873d14610224578063455e61661461024157806352b63e9f146102495780635704a5fa146102f65761010b565b8063089845e1146101105780630f2c63ff146101c157806318b011de146101e45780633678c143146101fe575b600080fd5b6101bf6004803603606081101561012657600080fd5b81359160208101359181019060608101604082013564010000000081111561014d57600080fd5b82018360208201111561015f57600080fd5b8035906020019184602083028401116401000000008311171561018157600080fd5b9190808060200260200160405190810160405280939291908181526020018383602002808284376000920191909152509295506104b2945050505050565b005b6101bf600480360360408110156101d757600080fd5b5080359060200135610597565b6101ec61067a565b60408051918252519081900360200190f35b6101bf6004803603602081101561021457600080fd5b50356001600160a01b0316610680565b6101bf6004803603602081101561023a57600080fd5b50356106d7565b6101ec6107b4565b6101bf6004803603604081101561025f57600080fd5b8135919081019060408101602082013564010000000081111561028157600080fd5b82018360208201111561029357600080fd5b803590602001918460018302840111640100000000831117156102b557600080fd5b91908080601f0160208091040260200160405190810160405280939291908181526020018383808284376000920191909152509295506107c0945050505050565b6103136004803603602081101561030c57600080fd5b503561089e565b60408051602080825283518183015283519192839290830191858101910280838360005b8381101561034f578181015183820152602001610337565b505050509050019250505060405180910390f35b6101ec6004803603602081101561037957600080fd5b5035610902565b61039d6004803603602081101561039657600080fd5b5035610917565b604080519115158252519081900360200190f35b6101ec600480360360208110156103c757600080fd5b503561094a565b61039d600480360360208110156103e457600080fd5b5035610a3c565b6101bf6004803603602081101561040157600080fd5b5035610a6e565b6104256004803603602081101561041e57600080fd5b5035610ae9565b6040518082600281111561043557fe5b60ff16815260200191505060405180910390f35b6101ec6004803603606081101561045f57600080fd5b508035906001600160a01b0360208201358116916040013516610b08565b61049a6004803603602081101561049357600080fd5b5035610c43565b6040518082600781111561043557fe5b6101ec610c5b565b60008281526020819052604090206002015482906001600160a01b031633146104da57600080fd5b6000838152602081905260408120906104f38285610c61565b905080156105295760408051868152602081018390528151600080516020611d87833981519152929181900390910190a161058f565b600782018054600101908190554260048401556005830155600382015460408051888152602081018890526001600160a01b0390921682820152517fa35dfb6661c6bfda214c0a2820d9b4bd1a673e2c303cf1578b5948a6f63b5a669181900360600190a15b505050505050565b60008181526020819052604090206003015481906001600160a01b031633146105bf57600080fd5b6000828152602081905260408120906105d782610e0a565b9050801561060d5760408051858152602081018390528151600080516020611d87833981519152929181900390910190a1610673565b600782018054600101908190554260048401556006830155600282015460408051878152602081018790526001600160a01b0390921682820152517f0817bf136ff95abb2d41d10a9fa5ff6652ff71c13e1b46717fae65db16423c9b9181900360600190a15b5050505050565b60025481565b60035461010090046001600160a01b03161580156106a657506001600160a01b03811615155b6106af57600080fd5b600380546001600160a01b0390921661010002610100600160a81b0319909216919091179055565b60008181526020819052604090206003015481906001600160a01b031633146106ff57600080fd5b60008281526020819052604081209061071782610e5f565b9050801561074d5760408051858152602081018390528151600080516020611d87833981519152929181900390910190a16107ae565b6007820180546001019081905542600484015560068301556002820154604080518681526001600160a01b03909216602083015280517fa0fbaa47643ff31cddc88d15082b689c8e90d1d48c12a4828762add3eb31be229281900390910190a15b50505050565b6729a2241af62c000081565b60008281526020819052604090206002015482906001600160a01b031633146107e857600080fd5b6000838152602081905260408120906108018285610ed0565b905080156108375760408051868152602081018390528151600080516020611d87833981519152929181900390910190a1610673565b6007820180546001019081905542600484015560058301556003820154604080518781526001600160a01b03909216602083015280517fbe7699071671fa6d8a4089a86a6ad506b9b9bf927b07a9c2ff44b0204de48b599281900390910190a15050505050565b600081815260208181526040918290206008018054835181840281018401909452808452606093928301828280156108f557602002820191906000526020600020905b8154815260200190600101908083116108e1575b505050505090505b919050565b60009081526020819052604090206001015490565b600081815260208190526040812060058101546006820154118015610943575060025481600401540142115b9392505050565b60008181526020819052604081206007600c82015460ff16600781111561096d57fe5b1480610992575080600501548160060154118015610992575060025481600401540142115b156109c257600281015460018201546109b89185916001600160a01b0390911690610faf565b60009150506108fd565b806006015481600501541180156109e0575060025481600401540142115b15610a085760038101546001808301546109b89286926001600160a01b03909116919061103d565b6040805184815261c36e60208201528151600080516020611d87833981519152929181900390910190a15061c36e92915050565b600081815260208190526040812060068101546005820154118015610943575060025460049091015401421192915050565b600081815260208190526040812090610a8782846110c8565b90508060011415610aba5760038201546001830154610ab59185916001600160a01b0390911690600061103d565b610ae4565b8060021415610ae45760028201546001830154610ae49185916001600160a01b0390911690610faf565b505050565b6000908152602081905260409020600a0154600160601b900460ff1690565b60035460009061010090046001600160a01b0316338114610b2857600080fd5b60408051602080820188905233828401526001600160a01b0386166060808401919091528351808403909101815260809092018352815191810191909120600081815291829052919020805415610b7e57600080fd5b81815560018082018890556002820180546001600160a01b03808a166001600160a01b03199283161790925560038401805492891692909116919091179055426004830155600060068301556005820181905560078201819055600c8201805460ff19168280021790555060408051888152602081018490526001600160a01b03808916828401528716606082015290517f403956bdc140717d54d4573786b4e9e773ef2e6e325e2c061476eb47711770de9181900360800190a15095945050505050565b6000908152602081905260409020600c015460ff1690565b60015481565b600882015460009015610c7357600080fd5b6002600c84015460ff166007811115610c8857fe5b1415610dff57600080610c9e85600101546111a5565b505050505093505050915083600185510381518110610cb957fe5b60200260200101518114610cd35761c3e692505050610e04565b600260035460ff166002811115610ce657fe5b14158015610cf75750600154845114155b15610d085761c3fa92505050610e04565b6040516303e6d75f60e61b815260206004820181815286516024840152865173__SyscoinMessageLibrary_________________9363f9b5d7c093899392839260440191808601910280838360005b83811015610d6f578181015183820152602001610d57565b505050509050019250505060206040518083038186803b158015610d9257600080fd5b505af4158015610da6573d6000803e3d6000fd5b505050506040513d6020811015610dbc57600080fd5b50518214610dd05761c37892505050610e04565b8351610de59060088701906020870190611cfd565b50505050600c8201805460ff191660031790556000610e04565b5061c3645b92915050565b60006001600c83015460ff166007811115610e2157fe5b1415610e5657600c8201805460ff191660021790556003820154336001600160a01b0390911614610e4e57fe5b5060006108fd565b5061c364919050565b60006003600c83015460ff166007811115610e7657fe5b1415610e56576000600a830154600160601b900460ff166002811115610e9857fe5b14610ea257600080fd5b50600c8101805460ff19166004179055600a81018054600160601b60ff60601b1990911617905560006108fd565b60006004600c84015460ff166007811115610ee757fe5b1415610dff576000610efc83600060506112b8565b60088501805491925082916000198101908110610f1557fe5b906000526020600020015414610f305761c3e6915050610e04565b60098401600180820154600160601b900460ff166002811115610f4f57fe5b14610f605761c36992505050610e04565b6000610f6d82848761137b565b90508015610f7f579250610e04915050565b50600c8501805460ff191660051790556001018054600160611b60ff60601b199091161790555060009050610e04565b6000838152602081905260408120600381015460028201549192610fe692879286926001600160a01b0391821692909116906114eb565b610fef8461157c565b60408051838152602081018690526001600160a01b0385168183015290517faab6a8f22c7ab5131c1cdc1c0000e123efd38efadfef092cef78be507d16542e9181900360600190a150505050565b60008481526020819052604090206002810154600382015461107091879186916001600160a01b039081169116866114eb565b6110798561157c565b60408051848152602081018790526001600160a01b0386168183015290517fda9b5fdafb0f67d811425d3f095917a63329d60d3db3aa456b5247a8f3b88b119181900360600190a15050505050565b60006005600c84015460ff1660078111156110df57fe5b14156111795760006110f084611613565b9050801561112b5760408051848152602081018390528151600080516020611d87833981519152929181900390910190a16002915050610e04565b61113484611770565b9050801561116f5760408051848152602081018390528151600080516020611d87833981519152929181900390910190a16002915050610e04565b6001915050610e04565b6007600c84015460ff16600781111561118e57fe5b141561119c57506002610e04565b50600092915050565b6000806000806000806000806000600460009054906101000a90046001600160a01b03166001600160a01b0316636e5b70718b6040518263ffffffff1660e01b8152600401808281526020019150506101206040518083038186803b15801561120d57600080fd5b505afa158015611221573d6000803e3d6000fd5b505050506040513d61012081101561123857600080fd5b810190808051906020019092919080519060200190929190805190602001909291908051906020019092919080519060200190929190805190602001909291908051906020019092919080519060200190929190805190602001909291905050509850985098509850985098509850985098509193959799909294969850565b600061137360026112ca868686611bf3565b604051602001808281526020019150506040516020818303038152906040526040518082805190602001908083835b602083106113185780518252601f1990920191602091820191016112f9565b51815160209384036101000a60001901801990921691161790526040519190930194509192505080830381855afa158015611357573d6000803e3d6000fd5b5050506040513d602081101561136c57600080fd5b5051611c18565b949350505050565b60008073__SyscoinMessageLibrary_________________6376837a138460008760001c6040518463ffffffff1660e01b81526004018080602001848152602001838152602001828103825285818151815260200191508051906020019080838360005b838110156113f75781810151838201526020016113df565b50505050905090810190601f1680156114245780820380516001836020036101000a031916815260200191505b5094505050505060206040518083038186803b15801561144357600080fd5b505af4158015611457573d6000803e3d6000fd5b505050506040513d602081101561146d57600080fd5b50519050801561147e579050610943565b61148783611c43565b60018601805467ffffffffffffffff191663ffffffff929092169190911790556114b083611c50565b8560010160086101000a81548163ffffffff021916908363ffffffff1602179055506114db83611c5d565b8555505050600290910155600090565b600354604080516308088ce560e11b815260048101889052602481018790526001600160a01b0386811660448301528581166064830152841515608483015291516101009093049091169163101119ca9160a48082019260009290919082900301818387803b15801561155d57600080fd5b505af1158015611571573d6000803e3d6000fd5b505050505050505050565b6000818152602081905260408120818155600181018290556002810180546001600160a01b0319908116909155600382018054909116905560048101829055600581018290556006810182905560078101829055906115de6008830182611d48565b50600060098201819055600a820180546cffffffffffffffffffffffffff19169055600b820155600c01805460ff1916905550565b6008810154600090611628575061c3e66108fd565b60008060008061163b86600101546111a5565b50505060088c018054949a509097509195506000945090925050600019810190811061166357fe5b60009182526020909120015460088801549091506009880190600210156116c75760088801805460009190600119810190811061169c57fe5b90600052602060002001549050808260000154146116c55761c40e9750505050505050506108fd565b505b8282146116de5761c3e696505050505050506108fd565b600181015467ffffffffffffffff1686146117035761c37396505050505050506108fd565b60026001820154600160601b900460ff16600281111561171f57fe5b146117345761c3f096505050505050506108fd565b61173d846111a5565b50949b50505050898911159350611762925050505761c37396505050505050506108fd565b506000979650505050505050565b600080600080600154905060008060008088600101549050611791816111a5565b969e50919c509198505063ffffffff90931695505060098c0192505050876117c55761c3c8985050505050505050506108fd565b600181015463ffffffff858116600160401b90920416146117f25761c42c985050505050505050506108fd565b6117fb876111a5565b50959b5091995060029550611811945050505050565b60035460ff16600281111561182257fe5b14156118305760088a015495505b8488116118495761c404985050505050505050506108fd565b600060035460ff16600281111561185c57fe5b1415611be3576006600119840106611b0257600181015463ffffffff858116600160401b90920416141561189c5761c422985050505050505050506108fd565b600073__SyscoinMessageLibrary_________________63b199c898600173__SyscoinMessageLibrary_________________635193ab7a6040518163ffffffff1660e01b815260040160206040518083038186803b1580156118fe57600080fd5b505af4158015611912573d6000803e3d6000fd5b505050506040513d602081101561192857600080fd5b5051604080516001600160e01b031960e086901b16815292909103600790810b900b600483015263ffffffff89166024830152516044808301926020929190829003018186803b15801561197b57600080fd5b505af415801561198f573d6000803e3d6000fd5b505050506040513d60208110156119a557600080fd5b50516040805163c9fdef7b60e01b8152905191925060009173__SyscoinMessageLibrary_________________9163b199c89891839163c9fdef7b916004808301926020929190829003018186803b158015611a0057600080fd5b505af4158015611a14573d6000803e3d6000fd5b505050506040513d6020811015611a2a57600080fd5b5051604080516001600160e01b031960e085901b1681526001909201600790810b900b600483015263ffffffff8a166024830152516044808301926020929190829003018186803b158015611a7e57600080fd5b505af4158015611a92573d6000803e3d6000fd5b505050506040513d6020811015611aa857600080fd5b5051600184015490915063ffffffff808416600160401b909204161080611ae25750600183015463ffffffff808316600160401b90920416115b15611afb5761c4189a50505050505050505050506108fd565b5050611b2f565b600181015463ffffffff858116600160401b9092041614611b2f5761c3d2985050505050505050506108fd565b600181015460408051636b814acd60e11b8152600160401b90920463ffffffff16600483015251600091889173__SyscoinMessageLibrary_________________9163d702959a916024808301926020929190829003018186803b158015611b9657600080fd5b505af4158015611baa573d6000803e3d6000fd5b505050506040513d6020811015611bc057600080fd5b50510286019050888114611be15761c3c899505050505050505050506108fd565b505b5060009998505050505050505050565b60006040516020818486602089010160025afa611c0f57600080fd5b51949350505050565b600060405160005b6020811015611c3b578381601f031a81830153600101611c20565b505192915050565b6000610e04826044611c6e565b6000610e04826048611c6e565b602481015160009061094381611c18565b6000828260030181518110611c7f57fe5b602001015160f81c60f81b60f81c60ff16630100000002838360020181518110611ca557fe5b602001015160f81c60f81b60f81c60ff166201000002848460010181518110611cca57fe5b602001015160f81c60f81b60f81c60ff1661010002858581518110611ceb57fe5b016020015160f81c0101019392505050565b828054828255906000526020600020908101928215611d38579160200282015b82811115611d38578251825591602001919060010190611d1d565b50611d44929150611d69565b5090565b5080546000825590600052602060002090810190611d669190611d69565b50565b611d8391905b80821115611d445760008155600101611d6f565b9056fe80235326defb5d335564dd77860b0a010e19446427d3d78d155cabd064ca9c2aa265627a7a723058200eebf17f11b42ba1675a8e1ffcaec9cb3420b3e75fb03afd52edbb3434ea266b64736f6c634300050a0032";

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
        _addresses.put("4", "0x35C13F26BBA16dd0d458f72550ae9F548A6B7871");
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
