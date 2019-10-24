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
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicBytes;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
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
 * <p>Generated with web3j version 4.5.0.
 */
public class SyscoinBattleManager extends Contract {
    private static final String BINARY = "0x608060405234801561001057600080fd5b506040516123c33803806123c38339818101604052608081101561003357600080fd5b50805160208201516040830151606090930151600380549394929385919060ff1916600183600281111561006357fe5b0217905550600480546001600160a01b0319166001600160a01b039490941693909317909255600155600255506123248061009f6000396000f3fe608060405234801561001057600080fd5b506004361061009e5760003560e01c8063795ea18e11610066578063795ea18e146101cd578063d1daeede146101ea578063e17732161461021e578063f1afcfa61461023b578063f871dfe8146102435761009e565b806318b011de146100a35780633678c143146100bd578063455e6166146100e557806351fcf431146100ed57806371a8c18a1461019c575b600080fd5b6100ab610260565b60408051918252519081900360200190f35b6100e3600480360360208110156100d357600080fd5b50356001600160a01b0316610266565b005b6100ab6102bd565b6100e36004803603606081101561010357600080fd5b8135919081019060408101602082013564010000000081111561012557600080fd5b82018360208201111561013757600080fd5b8035906020019184600183028401116401000000008311171561015957600080fd5b91908080601f01602080910402602001604051908101604052809392919081815260200183838082843760009201919091525092955050913592506102c8915050565b6101b9600480360360208110156101b257600080fd5b5035610728565b604080519115158252519081900360200190f35b6100ab600480360360208110156101e357600080fd5b5035610748565b6100ab6004803603606081101561020057600080fd5b508035906001600160a01b03602082013581169160400135166107bb565b6101b96004803603602081101561023457600080fd5b50356108e1565b6100ab610901565b6100ab6004803603602081101561025957600080fd5b5035610907565b60025481565b60035461010090046001600160a01b031615801561028c57506001600160a01b03811615155b61029557600080fd5b600380546001600160a01b0390921661010002610100600160a81b0319909216919091179055565b666a94d74f43000081565b600083815260208190526040902060018101546001600160a01b03163381146102f057600080fd5b6005820154600260035460ff16600281111561030857fe5b14610340576002811115801561031f575083601014155b806103365750806003148015610336575083600c14155b1561034057600080fd5b610348612168565b83546004805460408051636e5b707160e01b8152928301849052516001600160a01b0390911691636e5b707191602480830192610120929190829003018186803b15801561039557600080fd5b505afa1580156103a9573d6000803e3d6000fd5b505050506040513d6101208110156103c057600080fd5b50805160208083015160408085015160608087015160808089015160a08a0151610100909a015163ffffffff90811660e08e0152918c019990995290971660c08a01528881019690965287820152868301919091529185528151898152898202810190910190915260009190888015610443578160200160208202803883390190505b50905060608860405190808252806020026020018201604052801561048257816020015b61046f6121b2565b8152602001906001900390816104675790505b5090506000805b82518110156105c55761049c8c866109ee565b8382815181106104a857fe5b602002602001018190525060006104d58483815181106104c457fe5b602002602001015160000151610a43565b90506104e18d87610a66565b15610559576104ee6121d9565b6104f88e88610a89565b90508181600001511115610512576127a6935050506105c5565b600061053886858151811061052357fe5b60200260200101516060015160001c83610b87565b90508060011461054c5793506105c5915050565b506101400151955061058d565b8084838151811061056657fe5b60200260200101516060015160001c1115610586576127929250506105c5565b8560500195505b83828151811061059957fe5b6020026020010151606001518583815181106105b157fe5b602090810291909101015250600101610489565b5080156105f85760028901546105ea908d9087908b906001600160a01b031685610bf8565b505050505050505050610723565b610623898761060686610cda565b8560018751038151811061061657fe5b6020026020010151610f92565b9050801561064e576002890154610649908d9087908b906001600160a01b031685610bf8565b610719565b4260038a015561065f8987846110f8565b905080156106855760028901546105ea908d9087908b906001600160a01b031685610bf8565b89600c14806106a45750600260035460ff1660028111156106a257fe5b145b156106c75760028901546105ea908d9087908b906001600160a01b03168561146e565b60408051868152602081018e905260018901818301526001600160a01b038a16606082015290517f0e660e6e65ec52c9dda40ab02165320c09e799891feae8fed08191cb2150b45b9181900360800190a15b5050505050505050505b505050565b60008181526020819052604090206002546003909101540142115b919050565b600081815260208190526040812060018101546001600160a01b031661076d57600080fd5b6002548160030154014211156107b1578054600182015460028301546107a692869290916001600160a01b03918216911661c36a610bf8565b61c36a915050610743565b5061c36e92915050565b60035460009061010090046001600160a01b03163381146107db57600080fd5b60408051602080820188905233828401526001600160a01b03808716606080850191909152845180850390910181526080909301845282519282019290922060008181529182905292902060018101549091161561083857600080fd5b8681556001810180546001600160a01b038089166001600160a01b03199283161790925560028301805492881692909116919091179055600061087e6005830182612233565b5042600382015560408051888152602081018490526001600160a01b03808916828401528716606082015290517f403956bdc140717d54d4573786b4e9e773ef2e6e325e2c061476eb47711770de9181900360800190a15091505b509392505050565b6000908152602081905260409020600101546001600160a01b0316151590565b60015481565b6000610911612257565b60008381526020818152604091829020825160c0810184528154815260018201546001600160a01b03908116828501526002830154168185015260038201546060820152600482015460808201526005820180548551818602810186019096528086529194929360a086019392908301828280156109ae57602002820191906000526020600020905b81548152602001906001019080831161099a575b5050509190925250505060208101519091506001600160a01b03166109d7576000915050610743565b505060009081526020819052604090206005015490565b6109f66121b2565b610a008383611549565b63ffffffff168152610a1483836050611558565b6060820152610a238383611613565b63ffffffff166040820152610a388383611622565b602082015292915050565b62ffffff8116630100000063ffffffff92831604909116600219016101000a0290565b6000610100610a758484611642565b1663ffffffff166000141590505b92915050565b610a916121d9565b60606000605084019350610aa5858561167e565b92509050610ab68585808403611558565b60208085019190915281019350610acf858560006116aa565b60e08501919091529350610ae58585602061175a565b61010084015260049390930192610afe858560006116aa565b60608501919091529350610b148585602061175a565b608084015260049390930192610b2c85856050611558565b835260249390930192610b3f8585611794565b60c084015260289390930192610b578585602061175a565b610120840152600484016101408401526000610b728361179c565b60a08701525060408501525091949350505050565b6000816101000151600014610b9f575061274c610a83565b8160a00151600114610bb6575060a0810151610a83565b8160400151610bc58484611865565b14610bd3575061277e610a83565b8160c00151610be18361187a565b14610bef5750612788610a83565b50600192915050565b60035460408051633a45007160e11b815260048101889052602481018790526001600160a01b038581166044830152868116606483015291516101009093049091169163748a00e29160848082019260009290919082900301818387803b158015610c6257600080fd5b505af1158015610c76573d6000803e3d6000fd5b505060408051878152602081018990528082018590526001600160a01b038716606082015290517fae4f7410342e27aa0df7167c691dfd96c5d906aff82fbe0279985e0cf48be5e39350908190036080019150a1610cd385611897565b5050505050565b80516000906001811415610d055782600081518110610cf557fe5b6020026020010151915050610743565b60008111610d50576040805162461bcd60e51b81526020600482015260136024820152724d7573742070726f766964652068617368657360681b604482015290519081900360640190fd5b60005b81811015610d9c57610d7a848281518110610d6a57fe5b602002602001015160001c6118e4565b60001b848281518110610d8957fe5b6020908102919091010152600101610d53565b6000805b6001841115610f78575060009150815b83831015610f7057838360010110610dcb5760018403610dd0565b826001015b9150600280878581518110610de157fe5b6020026020010151888581518110610df557fe5b602002602001015160405160200180838152602001828152602001925050506040516020818303038152906040526040518082805190602001908083835b60208310610e525780518252601f199092019160209182019101610e33565b51815160209384036101000a60001901801990921691161790526040519190930194509192505080830381855afa158015610e91573d6000803e3d6000fd5b5050506040513d6020811015610ea657600080fd5b50516040805160208181019390935281518082038401815290820191829052805190928291908401908083835b60208310610ef25780518252601f199092019160209182019101610ed3565b51815160209384036101000a60001901801990921691161790526040519190930194509192505080830381855afa158015610f31573d6000803e3d6000fd5b5050506040513d6020811015610f4657600080fd5b50518651879083908110610f5657fe5b602090810291909101015260029290920191600101610db0565b809350610da0565b610f8886600081518110610d6a57fe5b9695505050505050565b600584015460009060031480610fb85750600260035460ff166002811115610fb657fe5b145b156110d1576060600260035460ff166002811115610fd257fe5b1415610fdf576001610fe2565b60045b60ff1660405190808252806020026020018201604052801561100e578160200160208202803883390190505b50905060005b60058701548110156110595786600501818154811061102f57fe5b906000526020600020015482828151811061104657fe5b6020908102919091010152600101611014565b8482828151811061106657fe5b60200260200101818152505061107b82610cda565b86511461108e5761c376925050506110f0565b836040015163ffffffff168660200151146110af5761c374925050506110f0565b85606001518460600151146110ca5761c38b925050506110f0565b50506110ec565b60058501805460018101825560009182526020909120018390555b5060005b949350505050565b6000611102612168565b61110a6121b2565b8360018551038151811061111a57fe5b60200260200101519050600460009054906101000a90046001600160a01b03166001600160a01b0316636e5b707186608001516040518263ffffffff1660e01b8152600401808281526020019150506101206040518083038186803b15801561118257600080fd5b505afa158015611196573d6000803e3d6000fd5b505050506040513d6101208110156111ad57600080fd5b5060408082015160608084015160809094015163ffffffff1660c0870152850192909252830152600586015460011061120f578160600151846000815181106111f257fe5b6020026020010151602001511461120f5761c38d92505050611467565b600061122087868560c00151611a0e565b90508015611232579250611467915050565b8451600c1461124a576060820151600488015561145f565b600061125586611b0d565b9050866040015181146112705761c397945050505050611467565b836040015181116112895761c398945050505050611467565b600260035460ff16600281111561129c57fe5b1461145d57600660018860e001510363ffffffff16816112b857fe5b0663ffffffff1660001415611434576112cf6121b2565b866002885103815181106112df57fe5b6020908102919091018101516004805460e08c01516040805163c0dde98b60e01b815260051990920163ffffffff169382019390935291519294506001600160a01b031692632da8cffd92849263c0dde98b926024808301939192829003018186803b15801561134e57600080fd5b505afa158015611362573d6000803e3d6000fd5b505050506040513d602081101561137857600080fd5b5051604080516001600160e01b031960e085901b1681526004810192909252516024808301926020929190829003018186803b1580156113b757600080fd5b505afa1580156113cb573d6000803e3d6000fd5b505050506040513d60208110156113e157600080fd5b505160208901819052604082015160c08701516000926114089263ffffffff160390611b71565b90508063ffffffff168960c0015163ffffffff16146114315761c3899650505050505050611467565b50505b826000015163ffffffff168760c0015163ffffffff161461145d5761c390945050505050611467565b505b600093505050505b9392505050565b60035460408051633a45007160e11b815260048101889052602481018790526001600160a01b038681166044830152858116606483015291516101009093049091169163748a00e29160848082019260009290919082900301818387803b1580156114d857600080fd5b505af11580156114ec573d6000803e3d6000fd5b505060408051878152602081018990528082018590526001600160a01b038616606082015290517f766980202352ff259a9ea889942266c29c1ca6260254f21cd529af44aeb5637c9350908190036080019150a1610cd385611897565b60006114678383604801611642565b60006110f0600261156a868686611bd2565b604051602001808281526020019150506040516020818303038152906040526040518082805190602001908083835b602083106115b85780518252601f199092019160209182019101611599565b51815160209384036101000a60001901801990921691161790526040519190930194509192505080830381855afa1580156115f7573d6000803e3d6000fd5b5050506040513d602081101561160c57600080fd5b50516118e4565b60006114678383604401611642565b6024828201015160009060048301611639826118e4565b95945050505050565b6000816020840101516040518160031a60008201538160021a60018201538160011a60028201538160001a60038201535160e01c949350505050565b6000606061168f8484600401611bf7565b909250905061169e8483611c7f565b60040194909350915050565b606060008060006116bb8787611cd2565b965091508415806116cb57508185115b156116d75750806116da565b50835b606081604051908082528060200260200182016040528015611706578160200160208202803883390190505b50905060005b8281101561174e576117266117218a8a611794565b6118e4565b82828151811061173257fe5b602090810291909101810191909152979097019660010161170c565b50979596505050505050565b6000805b600883048110156108d9578060080260020a858286018151811061177e57fe5b016020015160f81c02919091019060010161175e565b016020015190565b6000806000806000809050600063fabe6d6d60e01b905060006001600160e01b031990508751602089018181015b808210156117ff57848483511614156117f457856117ed57600482820384030196505b6001860195505b6001820191506117ca565b505050600283106118215750600095505050600319019150612760905061185e565b826001141561184b576118348885611794565b96505060031990920193506001925061185e915050565b5060009550505060031901915061276a90505b9193909250565b60006114678383608001518460600151611d84565b6000610a8361172183602001518461010001518560e00151611d84565b60008181526020819052604081208181556001810180546001600160a01b03199081169091556002820180549091169055600381018290556004810182905590610723600583018261228e565b60405160009060ff8316815382601e1a600182015382601d1a600282015382601c1a600382015382601b1a600482015382601a1a60058201538260191a60068201538260181a60078201538260171a60088201538260161a60098201538260151a600a8201538260141a600b8201538260131a600c8201538260121a600d8201538260111a600e8201538260101a600f82015382600f1a601082015382600e1a601182015382600d1a601282015382600c1a601382015382600b1a601482015382600a1a60158201538260091a60168201538260081a60178201538260071a60188201538260061a60198201538260051a601a8201538260041a601b8201538260031a601c8201538260021a601d8201538260011a601e8201538260001a601f8201535192915050565b8151600090600019015b8015611abb57611a266121b2565b848281518110611a3257fe5b60200260200101519050611a446121b2565b856001840381518110611a5357fe5b602002602001015190508551600c141580611a715750600186510383105b15611a9457815163ffffffff868116911614611a945761c38a9350505050611467565b8160200151816060015114611ab05761c38c9350505050611467565b505060001901611a18565b506005840154600211611b0357611ad06121b2565b83600081518110611add57fe5b602002602001015190508060200151856004015414611b015761c393915050611467565b505b5060009392505050565b6000611b176122af565b60005b600b811015611b5d57838160010181518110611b3257fe5b60200260200101516040015163ffffffff168282600b8110611b5057fe5b6020020152600101611b1a565b50611b6781611f9e565b60a0015192915050565b600082614380811015611b875750614380611b96565b616978811115611b9657506169785b6000611ba184610a43565b6154606080918290048402040290506001600160f41b03811115611bc957506001600160f41b035b61163981612040565b60006040516020818486602089010160025afa611bee57600080fd5b51949350505050565b60006060600080611c088686611cd2565b9550915081611c3857611c1b8686611cd2565b9550915081611c2957600080fd5b611c338686611cd2565b955091505b81600114611c4557600080fd5b602485019450611c558686611cd2565b955090506060611c6887878481016120d1565b9590910160040194859450925050505b9250929050565b6000806000611c8e8585611cd2565b94509150600a8210611c9f57600080fd5b60005b82811015611cc857600885019450611cba8686611cd2565b810195509150600101611ca2565b5092949350505050565b6000806000848481518110611ce357fe5b01602001516001949094019360f81c905060fd811015611d0a5760ff169150829050611c78565b8060ff1660fd1415611d3057611d228585601061175a565b846002019250925050611c78565b8060ff1660fe1415611d5657611d488585602061175a565b846004019250925050611c78565b8060ff1660ff1415611d7c57611d6e8585604061175a565b846008019250925050611c78565b509250929050565b8051600090815b81811015611dce57611daf848281518110611da257fe5b60200260200101516118e4565b848281518110611dbb57fe5b6020908102919091010152600101611d8b565b50600080611ddb876118e4565b90505b82821015611f8a576000858381518110611df457fe5b6020026020010151905060008060028981611e0b57fe5b0660011415611e1e575081905082611e24565b50829050815b600280838360405160200180838152602001828152602001925050506040516020818303038152906040526040518082805190602001908083835b60208310611e7e5780518252601f199092019160209182019101611e5f565b51815160209384036101000a60001901801990921691161790526040519190930194509192505080830381855afa158015611ebd573d6000803e3d6000fd5b5050506040513d6020811015611ed257600080fd5b50516040805160208181019390935281518082038401815290820191829052805190928291908401908083835b60208310611f1e5780518252601f199092019160209182019101611eff565b51815160209384036101000a60001901801990921691161790526040519190930194509192505080830381855afa158015611f5d573d6000803e3d6000fd5b5050506040513d6020811015611f7257600080fd5b50519350600289049850600185019450505050611dde565b611f93816118e4565b979650505050505050565b60005b600b81101561203c57600181015b600b811015612033578281600b8110611fc457fe5b60200201518383600b8110611fd557fe5b6020020151111561202b5760008383600b8110611fee57fe5b602002015190508382600b811061200157fe5b60200201518484600b811061201257fe5b6020020152808483600b811061202457fe5b6020020152505b600101611faf565b50600101611fa1565b5050565b60008061205961204f84612123565b600701600361214b565b90506000600382116120815761207a8462ffffff1683600303600802612161565b9050612099565b612091846003840360080261214b565b62ffffff1690505b628000008116156120be576120b58163ffffffff16600861214b565b90506001820191505b6120c9826018612161565b179392505050565b6060600083830390506060816040519080825280601f01601f191660200182016040528015612107576020820181803883390190505b5090508160208201838760208a010160045afa61163957600080fd5b6000815b80156121455761213881600161214b565b9050600182019150612127565b50919050565b60008160020a838161215957fe5b049392505050565b60020a0290565b6040805161012081018252600080825260208201819052918101829052606081018290526080810182905260a0810182905260c0810182905260e081018290529061010082015290565b60408051608081018252600080825260208201819052918101829052606081019190915290565b60405180610160016040528060008152602001600081526020016000815260200160608152602001600081526020016000815260200160008152602001606081526020016000815260200160008152602001600081525090565b815481835581811115610723576000838152602090206107239181019083016122ce565b6040805160c0810182526000808252602082018190529181018290526060808201839052608082019290925260a081019190915290565b50805460008255906000526020600020908101906122ac91906122ce565b50565b604051806101600160405280600b906020820280388339509192915050565b6122ec91905b808211156122e857600081556001016122d4565b5090565b9056fea265627a7a7231582058e88a8b219edb097a6b7e148f930046e47151726d239144300136884559a33164736f6c634300050c0032";

    public static final String FUNC_MINPROPOSALDEPOSIT = "minProposalDeposit";

    public static final String FUNC_SUPERBLOCKDURATION = "superblockDuration";

    public static final String FUNC_SUPERBLOCKTIMEOUT = "superblockTimeout";

    public static final String FUNC_SETSYSCOINCLAIMMANAGER = "setSyscoinClaimManager";

    public static final String FUNC_BEGINBATTLESESSION = "beginBattleSession";

    public static final String FUNC_RESPONDBLOCKHEADERS = "respondBlockHeaders";

    public static final String FUNC_TIMEOUT = "timeout";

    public static final String FUNC_GETSUBMITTERHITTIMEOUT = "getSubmitterHitTimeout";

    public static final String FUNC_GETNUMMERKLEHASHESBYSESSION = "getNumMerkleHashesBySession";

    public static final String FUNC_SESSIONEXISTS = "sessionExists";

    public static final Event CHALLENGERCONVICTED_EVENT = new Event("ChallengerConvicted", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Uint256>() {}, new TypeReference<Address>() {}));
    ;

    public static final Event NEWBATTLE_EVENT = new Event("NewBattle", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}, new TypeReference<Address>() {}));
    ;

    public static final Event RESPONDBLOCKHEADERS_EVENT = new Event("RespondBlockHeaders", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Uint256>() {}, new TypeReference<Address>() {}));
    ;

    public static final Event SUBMITTERCONVICTED_EVENT = new Event("SubmitterConvicted", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Uint256>() {}, new TypeReference<Address>() {}));
    ;

    protected static final HashMap<String, String> _addresses;

    static {
        _addresses = new HashMap<String, String>();
        _addresses.put("4", "0xe71E345B19E1cf643f111bEc8eC8D4449C1335b7");
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

    public List<ChallengerConvictedEventResponse> getChallengerConvictedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(CHALLENGERCONVICTED_EVENT, transactionReceipt);
        ArrayList<ChallengerConvictedEventResponse> responses = new ArrayList<ChallengerConvictedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ChallengerConvictedEventResponse typedResponse = new ChallengerConvictedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.superblockHash = (Bytes32) eventValues.getNonIndexedValues().get(0);
            typedResponse.sessionId = (Bytes32) eventValues.getNonIndexedValues().get(1);
            typedResponse.err = (Uint256) eventValues.getNonIndexedValues().get(2);
            typedResponse.challenger = (Address) eventValues.getNonIndexedValues().get(3);
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<ChallengerConvictedEventResponse> challengerConvictedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, ChallengerConvictedEventResponse>() {
            @Override
            public ChallengerConvictedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(CHALLENGERCONVICTED_EVENT, log);
                ChallengerConvictedEventResponse typedResponse = new ChallengerConvictedEventResponse();
                typedResponse.log = log;
                typedResponse.superblockHash = (Bytes32) eventValues.getNonIndexedValues().get(0);
                typedResponse.sessionId = (Bytes32) eventValues.getNonIndexedValues().get(1);
                typedResponse.err = (Uint256) eventValues.getNonIndexedValues().get(2);
                typedResponse.challenger = (Address) eventValues.getNonIndexedValues().get(3);
                return typedResponse;
            }
        });
    }

    public Flowable<ChallengerConvictedEventResponse> challengerConvictedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(CHALLENGERCONVICTED_EVENT));
        return challengerConvictedEventFlowable(filter);
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
        return web3j.ethLogFlowable(filter).map(new Function<Log, NewBattleEventResponse>() {
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

    public List<RespondBlockHeadersEventResponse> getRespondBlockHeadersEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(RESPONDBLOCKHEADERS_EVENT, transactionReceipt);
        ArrayList<RespondBlockHeadersEventResponse> responses = new ArrayList<RespondBlockHeadersEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            RespondBlockHeadersEventResponse typedResponse = new RespondBlockHeadersEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.superblockHash = (Bytes32) eventValues.getNonIndexedValues().get(0);
            typedResponse.sessionId = (Bytes32) eventValues.getNonIndexedValues().get(1);
            typedResponse.merkleHashCount = (Uint256) eventValues.getNonIndexedValues().get(2);
            typedResponse.submitter = (Address) eventValues.getNonIndexedValues().get(3);
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<RespondBlockHeadersEventResponse> respondBlockHeadersEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, RespondBlockHeadersEventResponse>() {
            @Override
            public RespondBlockHeadersEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(RESPONDBLOCKHEADERS_EVENT, log);
                RespondBlockHeadersEventResponse typedResponse = new RespondBlockHeadersEventResponse();
                typedResponse.log = log;
                typedResponse.superblockHash = (Bytes32) eventValues.getNonIndexedValues().get(0);
                typedResponse.sessionId = (Bytes32) eventValues.getNonIndexedValues().get(1);
                typedResponse.merkleHashCount = (Uint256) eventValues.getNonIndexedValues().get(2);
                typedResponse.submitter = (Address) eventValues.getNonIndexedValues().get(3);
                return typedResponse;
            }
        });
    }

    public Flowable<RespondBlockHeadersEventResponse> respondBlockHeadersEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(RESPONDBLOCKHEADERS_EVENT));
        return respondBlockHeadersEventFlowable(filter);
    }

    public List<SubmitterConvictedEventResponse> getSubmitterConvictedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(SUBMITTERCONVICTED_EVENT, transactionReceipt);
        ArrayList<SubmitterConvictedEventResponse> responses = new ArrayList<SubmitterConvictedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            SubmitterConvictedEventResponse typedResponse = new SubmitterConvictedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.superblockHash = (Bytes32) eventValues.getNonIndexedValues().get(0);
            typedResponse.sessionId = (Bytes32) eventValues.getNonIndexedValues().get(1);
            typedResponse.err = (Uint256) eventValues.getNonIndexedValues().get(2);
            typedResponse.submitter = (Address) eventValues.getNonIndexedValues().get(3);
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<SubmitterConvictedEventResponse> submitterConvictedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, SubmitterConvictedEventResponse>() {
            @Override
            public SubmitterConvictedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(SUBMITTERCONVICTED_EVENT, log);
                SubmitterConvictedEventResponse typedResponse = new SubmitterConvictedEventResponse();
                typedResponse.log = log;
                typedResponse.superblockHash = (Bytes32) eventValues.getNonIndexedValues().get(0);
                typedResponse.sessionId = (Bytes32) eventValues.getNonIndexedValues().get(1);
                typedResponse.err = (Uint256) eventValues.getNonIndexedValues().get(2);
                typedResponse.submitter = (Address) eventValues.getNonIndexedValues().get(3);
                return typedResponse;
            }
        });
    }

    public Flowable<SubmitterConvictedEventResponse> submitterConvictedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(SUBMITTERCONVICTED_EVENT));
        return submitterConvictedEventFlowable(filter);
    }

    public RemoteFunctionCall<Uint256> minProposalDeposit() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_MINPROPOSALDEPOSIT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public RemoteFunctionCall<Uint256> superblockDuration() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_SUPERBLOCKDURATION, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public RemoteFunctionCall<Uint256> superblockTimeout() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_SUPERBLOCKTIMEOUT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public RemoteFunctionCall<TransactionReceipt> setSyscoinClaimManager(Address _syscoinClaimManager) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_SETSYSCOINCLAIMMANAGER, 
                Arrays.<Type>asList(_syscoinClaimManager), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> beginBattleSession(Bytes32 superblockHash, Address submitter, Address challenger) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_BEGINBATTLESESSION, 
                Arrays.<Type>asList(superblockHash, submitter, challenger), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> respondBlockHeaders(Bytes32 sessionId, DynamicBytes blockHeaders, Uint256 numHeaders) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_RESPONDBLOCKHEADERS, 
                Arrays.<Type>asList(sessionId, blockHeaders, numHeaders), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> timeout(Bytes32 sessionId) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_TIMEOUT, 
                Arrays.<Type>asList(sessionId), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Bool> getSubmitterHitTimeout(Bytes32 sessionId) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETSUBMITTERHITTIMEOUT, 
                Arrays.<Type>asList(sessionId), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public RemoteFunctionCall<Uint256> getNumMerkleHashesBySession(Bytes32 sessionId) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETNUMMERKLEHASHESBYSESSION, 
                Arrays.<Type>asList(sessionId), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public RemoteFunctionCall<Bool> sessionExists(Bytes32 sessionId) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_SESSIONEXISTS, 
                Arrays.<Type>asList(sessionId), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
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

    public static class ChallengerConvictedEventResponse extends BaseEventResponse {
        public Bytes32 superblockHash;

        public Bytes32 sessionId;

        public Uint256 err;

        public Address challenger;
    }

    public static class NewBattleEventResponse extends BaseEventResponse {
        public Bytes32 superblockHash;

        public Bytes32 sessionId;

        public Address submitter;

        public Address challenger;
    }

    public static class RespondBlockHeadersEventResponse extends BaseEventResponse {
        public Bytes32 superblockHash;

        public Bytes32 sessionId;

        public Uint256 merkleHashCount;

        public Address submitter;
    }

    public static class SubmitterConvictedEventResponse extends BaseEventResponse {
        public Bytes32 superblockHash;

        public Bytes32 sessionId;

        public Uint256 err;

        public Address submitter;
    }
}
