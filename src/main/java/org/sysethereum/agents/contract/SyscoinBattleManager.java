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
    private static final String BINARY = "0x608060405234801561001057600080fd5b506040516122b03803806122b08339818101604052608081101561003357600080fd5b50805160208201516040830151606090930151600380549394929385919060ff1916600183600281111561006357fe5b0217905550600480546001600160a01b0319166001600160a01b039490941693909317909255600155600255506122118061009f6000396000f3fe608060405234801561001057600080fd5b50600436106101735760003560e01c806371a8c18a116100de578063abbb6bf611610097578063d1daeede11610071578063d1daeede1461057e578063ec6dbad8146105b2578063eda1970b146105df578063f1afcfa6146105e757610173565b8063abbb6bf61461052d578063ba16d6001461056e578063d035c4031461057657610173565b806371a8c18a14610400578063795ea18e146104315780637dbd28321461044e57806390b6f699146104eb57806399b32f3a146104f3578063a6c07c961461051057610173565b8063455e616611610130578063455e6166146102b157806352b63e9f146102b95780635704a5fa1461036657806357bd24fb146103d357806361bd8d66146103f05780636ca640a1146103f857610173565b8063089845e1146101785780630f2c63ff146102295780631797e5e91461024c57806318b011de146102665780633678c1431461026e5780633e8a873d14610294575b600080fd5b6102276004803603606081101561018e57600080fd5b8135916020810135918101906060810160408201356401000000008111156101b557600080fd5b8201836020820111156101c757600080fd5b803590602001918460208302840111640100000000831117156101e957600080fd5b9190808060200260200160405190810160405280939291908181526020018383602002808284376000920191909152509295506105ef945050505050565b005b6102276004803603604081101561023f57600080fd5b50803590602001356106d4565b6102546107b7565b60408051918252519081900360200190f35b6102546107bd565b6102276004803603602081101561028457600080fd5b50356001600160a01b03166107c3565b610227600480360360208110156102aa57600080fd5b503561081a565b6102546108f7565b610227600480360360408110156102cf57600080fd5b813591908101906040810160208201356401000000008111156102f157600080fd5b82018360208201111561030357600080fd5b8035906020019184600183028401116401000000008311171561032557600080fd5b91908080601f016020809104026020016040519081016040528093929190818152602001838380828437600092019190915250929550610903945050505050565b6103836004803603602081101561037c57600080fd5b50356109e1565b60408051602080825283518183015283519192839290830191858101910280838360005b838110156103bf5781810151838201526020016103a7565b505050509050019250505060405180910390f35b610254600480360360208110156103e957600080fd5b5035610a45565b610254610a5a565b610254610a61565b61041d6004803603602081101561041657600080fd5b5035610a6d565b604080519115158252519081900360200190f35b6102546004803603602081101561044757600080fd5b5035610aa0565b61046b6004803603602081101561046457600080fd5b5035610b90565b604051808a8152602001898152602001886001600160a01b03166001600160a01b03168152602001876001600160a01b03166001600160a01b031681526020018681526020018581526020018481526020018381526020018260078111156104cf57fe5b60ff168152602001995050505050505050505060405180910390f35b610254610bea565b61041d6004803603602081101561050957600080fd5b5035610bf1565b6102276004803603602081101561052657600080fd5b5035610c23565b61054a6004803603602081101561054357600080fd5b5035610c9c565b6040518082600281111561055a57fe5b60ff16815260200191505060405180910390f35b610254610ccd565b610254610cd9565b6102546004803603606081101561059457600080fd5b508035906001600160a01b0360208201358116916040013516610ce0565b6105cf600480360360208110156105c857600080fd5b5035610e1b565b6040518082600781111561055a57fe5b610254610e33565b610254610e39565b60008281526020819052604090206002015482906001600160a01b0316331461061757600080fd5b6000838152602081905260408120906106308285610e3f565b9050801561066657604080518681526020810183905281516000805160206121bd833981519152929181900390910190a16106cc565b600782018054600101908190554260048401556005830155600382015460408051888152602081018890526001600160a01b0390921682820152517fa35dfb6661c6bfda214c0a2820d9b4bd1a673e2c303cf1578b5948a6f63b5a669181900360600190a15b505050505050565b60008181526020819052604090206003015481906001600160a01b031633146106fc57600080fd5b60008281526020819052604081209061071482611031565b9050801561074a57604080518581526020810183905281516000805160206121bd833981519152929181900390910190a16107b0565b600782018054600101908190554260048401556006830155600282015460408051878152602081018790526001600160a01b0390921682820152517f0817bf136ff95abb2d41d10a9fa5ff6652ff71c13e1b46717fae65db16423c9b9181900360600190a15b5050505050565b619c4081565b60025481565b60035461010090046001600160a01b03161580156107e957506001600160a01b03811615155b6107f257600080fd5b600380546001600160a01b0390921661010002610100600160a81b0319909216919091179055565b60008181526020819052604090206003015481906001600160a01b0316331461084257600080fd5b60008281526020819052604081209061085a826110bb565b9050801561089057604080518581526020810183905281516000805160206121bd833981519152929181900390910190a16108f1565b6007820180546001019081905542600484015560068301556002820154604080518681526001600160a01b03909216602083015280517fa0fbaa47643ff31cddc88d15082b689c8e90d1d48c12a4828762add3eb31be229281900390910190a15b50505050565b670de0b6b3a76484d081565b60008281526020819052604090206002015482906001600160a01b0316331461092b57600080fd5b600083815260208190526040812090610944828561118e565b9050801561097a57604080518681526020810183905281516000805160206121bd833981519152929181900390910190a16107b0565b6007820180546001019081905542600484015560058301556003820154604080518781526001600160a01b03909216602083015280517fbe7699071671fa6d8a4089a86a6ad506b9b9bf927b07a9c2ff44b0204de48b599281900390910190a15050505050565b60008181526020818152604091829020600801805483518184028101840190945280845260609392830182828015610a3857602002820191906000526020600020905b815481526020019060010190808311610a24575b505050505090505b919050565b60009081526020819052604090206001015490565b6206b6c081565b670de0b6b3a76ab6c081565b600081815260208190526040812060058101546006820154118015610a99575060025481600401540142115b9392505050565b60008181526020819052604081206007600a82015460ff166007811115610ac357fe5b1480610ae8575080600501548160060154118015610ae8575060025481600401540142115b15610b185760028101546001820154610b0e9185916001600160a01b03909116906112b6565b6000915050610a40565b80600601548160050154118015610b36575060025481600401540142115b15610b5c5760038101546001820154610b0e9185916001600160a01b039091169061133f565b6040805184815261c36e602082015281516000805160206121bd833981519152929181900390910190a15061c36e92915050565b600060208190529081526040902080546001820154600283015460038401546004850154600586015460068701546007880154600a90980154969795966001600160a01b0395861696959094169492939192909160ff1689565b6205c49081565b600081815260208190526040812060068101546005820154118015610a99575060025460049091015401421192915050565b600081815260208190526040812090610c3c82846113c8565b90508060011415610c6d5760038201546001830154610c689185916001600160a01b039091169061133f565b610c97565b8060021415610c975760028201546001830154610c979185916001600160a01b03909116906112b6565b505050565b600090815260208181526040808320600180820154855260099091019092529091200154600160601b900460ff1690565b670de0b6b3a764000081565b62035b6081565b60035460009061010090046001600160a01b0316338114610d0057600080fd5b60408051602080820188905233828401526001600160a01b0386166060808401919091528351808403909101815260809092018352815191810191909120600081815291829052919020805415610d5657600080fd5b81815560018082018890556002820180546001600160a01b03808a166001600160a01b03199283161790925560038401805492891692909116919091179055426004830155600060068301556005820181905560078201819055600a8201805460ff19168280021790555060408051888152602081018490526001600160a01b03808916828401528716606082015290517f403956bdc140717d54d4573786b4e9e773ef2e6e325e2c061476eb47711770de9181900360800190a15095945050505050565b6000908152602081905260409020600a015460ff1690565b6184d081565b60015481565b6000610e4e3362035b606114a5565b610e5b575061c38c61102b565b600883015415610e6a57600080fd5b6002600a84015460ff166007811115610e7f57fe5b141561102657600080610e95856001015461152f565b505050505093505050915083600185510381518110610eb057fe5b60200260200101518114610eca5761c3e69250505061102b565b600260035460ff166002811115610edd57fe5b14158015610eee5750600154845114155b15610eff5761c3fa9250505061102b565b6040516303e6d75f60e61b815260206004820181815286516024840152865173__SyscoinMessageLibrary_________________9363f9b5d7c093899392839260440191808601910280838360005b83811015610f66578181015183820152602001610f4e565b505050509050019250505060206040518083038186803b158015610f8957600080fd5b505af4158015610f9d573d6000803e3d6000fd5b505050506040513d6020811015610fb357600080fd5b50518214610fc75761c3789250505061102b565b6000610fdb86600101543362035b60611642565b90508015610fed57925061102b915050565b84516110029060088801906020880190612133565b50600a860180546003919060ff19166001835b02179055506000935050505061102b565b5061c3645b92915050565b6000611040336205c4906114a5565b61104d575061c38c610a40565b6001600a83015460ff16600781111561106257fe5b14156110b257600a8201805460ff191660021790556003820154336001600160a01b039091161461108f57fe5b60006110a38360010154336205c490611642565b90508015610b0e579050610a40565b5061c364919050565b60006110c933619c406114a5565b6110d6575061c38c610a40565b6003600a83015460ff1660078111156110eb57fe5b14156110b2576000600183810154600090815260098501602052604090200154600160601b900460ff16600281111561112057fe5b1461112a57600080fd5b600061113d836001015433619c40611642565b9050801561114c579050610a40565b5050600a8101805460ff19166004179055600180820154600090815260098301602052604081209091018054600160601b60ff60601b19909116179055610a40565b600061119c33619c406114a5565b6111a9575061c38c61102b565b6004600a84015460ff1660078111156111be57fe5b14156110265760006111d383600060506116da565b600885018054919250829160001981019081106111ec57fe5b9060005260206000200154146112075761c3e691505061102b565b60018085015460009081526009860160205260409020906001820154600160601b900460ff16600281111561123857fe5b146112495761c3699250505061102b565b600061125682848761179d565b9050801561126857925061102b915050565b611279866001015433619c40611642565b9050801561128b57925061102b915050565b600a8601805460ff191660051790556001820180546002919060ff60601b1916600160601b83611015565b6000838152602081905260409020600381015460028201546112e891869185916001600160a01b03908116911661190d565b6112f184611995565b60408051838152602081018690526001600160a01b0385168183015290517faab6a8f22c7ab5131c1cdc1c0000e123efd38efadfef092cef78be507d16542e9181900360600190a150505050565b60008381526020819052604090206002810154600382015461137191869185916001600160a01b03908116911661190d565b61137a84611995565b60408051838152602081018690526001600160a01b0385168183015290517fda9b5fdafb0f67d811425d3f095917a63329d60d3db3aa456b5247a8f3b88b119181900360600190a150505050565b60006005600a84015460ff1660078111156113df57fe5b14156114795760006113f084611a06565b9050801561142b57604080518481526020810183905281516000805160206121bd833981519152929181900390910190a1600291505061102b565b61143484611b74565b9050801561146f57604080518481526020810183905281516000805160206121bd833981519152929181900390910190a1600291505061102b565b600191505061102b565b6007600a84015460ff16600781111561148e57fe5b141561149c5750600261102b565b50600092915050565b60035460408051637092a7dd60e11b81526001600160a01b03858116600483015291516000938593610100909104169163e1254fba916024808301926020929190829003018186803b1580156114fa57600080fd5b505afa15801561150e573d6000803e3d6000fd5b505050506040513d602081101561152457600080fd5b505110159392505050565b6000806000806000806000806000600460009054906101000a90046001600160a01b03166001600160a01b0316636e5b70718b6040518263ffffffff1660e01b8152600401808281526020019150506101206040518083038186803b15801561159757600080fd5b505afa1580156115ab573d6000803e3d6000fd5b505050506040513d6101208110156115c257600080fd5b810190808051906020019092919080519060200190929190805190602001909291908051906020019092919080519060200190929190805190602001909291908051906020019092919080519060200190929190805190602001909291905050509850985098509850985098509850985098509193959799909294969850565b6003546040805163612b4f2d60e11b8152600481018690526001600160a01b03858116602483015260448201859052915160009361010090049092169163c2569e5a9160648082019260209290919082900301818787803b1580156116a657600080fd5b505af11580156116ba573d6000803e3d6000fd5b505050506040513d60208110156116d057600080fd5b5051949350505050565b600061179560026116ec868686612029565b604051602001808281526020019150506040516020818303038152906040526040518082805190602001908083835b6020831061173a5780518252601f19909201916020918201910161171b565b51815160209384036101000a60001901801990921691161790526040519190930194509192505080830381855afa158015611779573d6000803e3d6000fd5b5050506040513d602081101561178e57600080fd5b505161204e565b949350505050565b60008073__SyscoinMessageLibrary_________________6376837a138460008760001c6040518463ffffffff1660e01b81526004018080602001848152602001838152602001828103825285818151815260200191508051906020019080838360005b83811015611819578181015183820152602001611801565b50505050905090810190601f1680156118465780820380516001836020036101000a031916815260200191505b5094505050505060206040518083038186803b15801561186557600080fd5b505af4158015611879573d6000803e3d6000fd5b505050506040513d602081101561188f57600080fd5b5051905080156118a0579050610a99565b6118a983612079565b60018601805467ffffffffffffffff191663ffffffff929092169190911790556118d283612086565b8560010160086101000a81548163ffffffff021916908363ffffffff1602179055506118fd83612093565b8555505050600290910155600090565b60035460408051633a45007160e11b815260048101879052602481018690526001600160a01b038581166044830152848116606483015291516101009093049091169163748a00e29160848082019260009290919082900301818387803b15801561197757600080fd5b505af115801561198b573d6000803e3d6000fd5b5050505050505050565b6000818152602081905260408120818155600181018290556002810180546001600160a01b0319908116909155600382018054909116905560048101829055600581018290556006810182905560078101829055906119f7600883018261217e565b50600a01805460ff1916905550565b6008810154600090611a1b575061c3e6610a40565b600080600080611a2e866001015461152f565b50505060088c018054949a5090975091955060009450909250506000198101908110611a5657fe5b600091825260208083209091015460018a0154835260098a01909152604090912060088901549192509060021015611acb57600888018054600091906001198101908110611aa057fe5b9060005260206000200154905080826000015414611ac95761c40e975050505050505050610a40565b505b828214611ae25761c3e69650505050505050610a40565b600181015467ffffffffffffffff168614611b075761c3739650505050505050610a40565b60026001820154600160601b900460ff166002811115611b2357fe5b14611b385761c3f09650505050505050610a40565b611b418461152f565b50949b50505050898911159350611b66925050505761c3739650505050505050610a40565b506000979650505050505050565b600080600080600154905060008060008088600101549050611b958161152f565b9091929394959697509091929394955090919293945090915090508063ffffffff169050809550819950829650839a50505050506000896009016000838152602001908152602001600020905060008811611bfc5761c3c898505050505050505050610a40565b600181015463ffffffff858116600160401b9092041614611c295761c42c98505050505050505050610a40565b611c328761152f565b50959b5091995060029550611c48945050505050565b60035460ff166002811115611c5957fe5b1415611c675760088a015495505b848811611c805761c40498505050505050505050610a40565b600260035460ff166002811115611c9357fe5b14612019576006600019840106611f3857600181015463ffffffff858116600160401b909204161415611cd25761c42298505050505050505050610a40565b600073__SyscoinMessageLibrary_________________63b199c898600173__SyscoinMessageLibrary_________________635193ab7a6040518163ffffffff1660e01b815260040160206040518083038186803b158015611d3457600080fd5b505af4158015611d48573d6000803e3d6000fd5b505050506040513d6020811015611d5e57600080fd5b5051604080516001600160e01b031960e086901b16815292909103600790810b900b600483015263ffffffff89166024830152516044808301926020929190829003018186803b158015611db157600080fd5b505af4158015611dc5573d6000803e3d6000fd5b505050506040513d6020811015611ddb57600080fd5b50516040805163c9fdef7b60e01b8152905191925060009173__SyscoinMessageLibrary_________________9163b199c89891839163c9fdef7b916004808301926020929190829003018186803b158015611e3657600080fd5b505af4158015611e4a573d6000803e3d6000fd5b505050506040513d6020811015611e6057600080fd5b5051604080516001600160e01b031960e085901b1681526001909201600790810b900b600483015263ffffffff8a166024830152516044808301926020929190829003018186803b158015611eb457600080fd5b505af4158015611ec8573d6000803e3d6000fd5b505050506040513d6020811015611ede57600080fd5b5051600184015490915063ffffffff808416600160401b909204161080611f185750600183015463ffffffff808316600160401b90920416115b15611f315761c4189a5050505050505050505050610a40565b5050611f65565b600181015463ffffffff858116600160401b9092041614611f655761c3d298505050505050505050610a40565b600181015460408051630c13be2b60e21b8152600160401b90920463ffffffff16600483015251600091889173__SyscoinMessageLibrary_________________9163304ef8ac916024808301926020929190829003018186803b158015611fcc57600080fd5b505af4158015611fe0573d6000803e3d6000fd5b505050506040513d6020811015611ff657600080fd5b505102860190508881146120175761c3c89950505050505050505050610a40565b505b5060009998505050505050505050565b60006040516020818486602089010160025afa61204557600080fd5b51949350505050565b600060405160005b6020811015612071578381601f031a81830153600101612056565b505192915050565b600061102b8260446120a4565b600061102b8260486120a4565b6024810151600090610a998161204e565b60008282600301815181106120b557fe5b602001015160f81c60f81b60f81c60ff166301000000028383600201815181106120db57fe5b602001015160f81c60f81b60f81c60ff16620100000284846001018151811061210057fe5b602001015160f81c60f81b60f81c60ff166101000285858151811061212157fe5b016020015160f81c0101019392505050565b82805482825590600052602060002090810192821561216e579160200282015b8281111561216e578251825591602001919060010190612153565b5061217a92915061219f565b5090565b508054600082559060005260206000209081019061219c919061219f565b50565b6121b991905b8082111561217a57600081556001016121a5565b9056fe80235326defb5d335564dd77860b0a010e19446427d3d78d155cabd064ca9c2aa265627a7a723058201aeb114e35f5273b60c2889027f20b4772ac8a6ef0271d716c7cb45b9341304764736f6c634300050a0032";

    public static final String FUNC_RESPONDLASTBLOCKHEADERCOST = "respondLastBlockHeaderCost";

    public static final String FUNC_SUPERBLOCKTIMEOUT = "superblockTimeout";

    public static final String FUNC_MINPROPOSALDEPOSIT = "minProposalDeposit";

    public static final String FUNC_SUPERBLOCKCOST = "superblockCost";

    public static final String FUNC_MINCHALLENGEDEPOSIT = "minChallengeDeposit";

    public static final String FUNC_SESSIONS = "sessions";

    public static final String FUNC_RESPONDMERKLEROOTHASHESCOST = "respondMerkleRootHashesCost";

    public static final String FUNC_MINREWARD = "minReward";

    public static final String FUNC_VERIFYSUPERBLOCKCOST = "verifySuperblockCost";

    public static final String FUNC_CHALLENGECOST = "challengeCost";

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
        _addresses.put("4", "0xd67db58e35b84596574EC5943371f0cE5ecD5Fb8");
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

    public RemoteCall<Uint256> respondLastBlockHeaderCost() {
        final Function function = new Function(FUNC_RESPONDLASTBLOCKHEADERCOST, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function);
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

    public RemoteCall<Uint256> superblockCost() {
        final Function function = new Function(FUNC_SUPERBLOCKCOST, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public RemoteCall<Uint256> minChallengeDeposit() {
        final Function function = new Function(FUNC_MINCHALLENGEDEPOSIT, 
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

    public RemoteCall<Uint256> respondMerkleRootHashesCost() {
        final Function function = new Function(FUNC_RESPONDMERKLEROOTHASHESCOST, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public RemoteCall<Uint256> minReward() {
        final Function function = new Function(FUNC_MINREWARD, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public RemoteCall<Uint256> verifySuperblockCost() {
        final Function function = new Function(FUNC_VERIFYSUPERBLOCKCOST, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public RemoteCall<Uint256> challengeCost() {
        final Function function = new Function(FUNC_CHALLENGECOST, 
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
