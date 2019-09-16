package org.sysethereum.agents.contract;

import io.reactivex.Flowable;
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
    private static final String BINARY = "0x608060405234801561001057600080fd5b50612253806100206000396000f3fe608060405234801561001057600080fd5b50600436106100a95760003560e01c806371a8c18a1161007157806371a8c18a146101c4578063795ea18e146101f5578063d1daeede14610212578063df23ceb214610246578063ec6dbad814610281578063f1afcfa6146102c2576100a9565b806318b011de146100ae5780633678c143146100c8578063455e6166146100f057806351fcf431146100f857806357bd24fb146101a7575b600080fd5b6100b66102ca565b60408051918252519081900360200190f35b6100ee600480360360208110156100de57600080fd5b50356001600160a01b03166102d0565b005b6100b6610327565b6100ee6004803603606081101561010e57600080fd5b8135919081019060408101602082013564010000000081111561013057600080fd5b82018360208201111561014257600080fd5b8035906020019184600183028401116401000000008311171561016457600080fd5b91908080601f0160208091040260200160405190810160405280939291908181526020018383808284376000920191909152509295505091359250610332915050565b6100b6600480360360208110156101bd57600080fd5b503561045c565b6101e1600480360360208110156101da57600080fd5b5035610474565b604080519115158252519081900360200190f35b6100b66004803603602081101561020b57600080fd5b5035610491565b6100b66004803603606081101561022857600080fd5b508035906001600160a01b03602082013581169160400135166104ee565b6100ee6004803603608081101561025c57600080fd5b5060ff813516906001600160a01b036020820135169060408101359060600135610625565b61029e6004803603602081101561029757600080fd5b503561070e565b604051808260018111156102ae57fe5b60ff16815260200191505060405180910390f35b6100b6610726565b60355481565b60365461010090046001600160a01b03161580156102f657506001600160a01b03811615155b6102ff57600080fd5b603680546001600160a01b0390921661010002610100600160a81b0319909216919091179055565b666a94d74f43000081565b60008381526033602052604090206002015483906001600160a01b0316331461035a57600080fd5b6000848152603360205260409020600781015460021080159061037e575082601014155b80610399575060078101546003148015610399575082600c14155b156103a357600080fd5b6103ab6120b2565b6103b8826001015461072c565b63ffffffff9081166101208b015260808a019390935250501660e0860152606080860191909152604085019190915260208401919091529082526000906104018484898961083f565b915091506000821461041c576104178883610aea565b610452565b42600485015561042d848483610b7c565b9150811561043f5761043f8883610aea565b85600c1415610452576104528883610f98565b5050505050505050565b6000818152603360205260409020600101545b919050565b600090815260336020526040902060355460049091015401421190565b60008181526033602052604081206001600582015460ff1660018111156104b457fe5b1480156104c8575060355481600401540142115b156104e4576104d98361c36a610aea565b61c36a91505061046f565b5061c36e92915050565b60365460009061010090046001600160a01b031633811461050e57600080fd5b60408051602080820188905233828401526001600160a01b03861660608084019190915283518084039091018152608090920183528151918101919091206000818152603390925291902080541561056557600080fd5b818155600181018790556002810180546001600160a01b038089166001600160a01b0319928316179092556003830180549288169290911691909117905560006105b2600783018261210c565b5042600482015560058101805460ff1916600117905560408051888152602081018490526001600160a01b03888116828401528716606082015290517f403956bdc140717d54d4573786b4e9e773ef2e6e325e2c061476eb47711770de916080908290030190a15091505b509392505050565b600054610100900460ff168061063e575061063e611025565b8061064c575060005460ff16155b6106875760405162461bcd60e51b815260040180806020018281038252602e8152602001806121f1602e913960400191505060405180910390fd5b600054610100900460ff161580156106b2576000805460ff1961ff0019909116610100171660011790555b6036805486919060ff191660018360028111156106cb57fe5b0217905550603780546001600160a01b0319166001600160a01b038616179055603483905560358290558015610707576000805461ff00191690555b5050505050565b60009081526033602052604090206005015460ff1690565b60345481565b6000806000806000806000806000603760009054906101000a90046001600160a01b03166001600160a01b0316636e5b70718b6040518263ffffffff1660e01b8152600401808281526020019150506101206040518083038186803b15801561079457600080fd5b505afa1580156107a8573d6000803e3d6000fd5b505050506040513d6101208110156107bf57600080fd5b810190808051906020019092919080519060200190929190805190602001909291908051906020019092919080519060200190929190805190602001909291908051906020019092919080519060200190929190805190602001909291905050509850985098509850985098509850985098509193959799909294969850565b60006060806001600588015460ff16600181111561085957fe5b1415610ad9576000809050600080606087604051908082528060200260200182016040528015610893578160200160208202803883390190505b5090506060886040519080825280602002602001820160405280156108d257816020015b6108bf612130565b8152602001906001900390816108b75790505b509050600092505b805183101561095d576108ed8a8661102c565b83518490879081106108fb57fe5b60209081029190910101919091529550935083156109235750919550929350610ae192505050565b80838151811061092f57fe5b60200260200101516060015182848151811061094757fe5b60209081029190910101526001909201916108da565b60078c015460031415610aa457610972612130565b8160018351038151811061098257fe5b60200260200101519050606060046040519080825280602002602001820160405280156109b9578160200160208202803883390190505b509050600094505b60078e0154851015610a09578d60070185815481106109dc57fe5b90600052602060002001548186815181106109f357fe5b60209081029190910101526001909401936109c1565b610a1284611119565b818681518110610a1e57fe5b602002602001018181525050610a3381611119565b8d5114610a4f5761c37888995099505050505050505050610ae1565b816040015163ffffffff168d6040015114610a795761c37488995099505050505050505050610ae1565b8c60600151826060015114610a9d5761c38b88995099505050505050505050610ae1565b5050610ac8565b8b600701610ab183611119565b815460018101835560009283526020909220909101555b600097509550610ae1945050505050565b61c364925090505b94509492505050565b6000828152603360205260409020600181015460038201546002830154610b2192869290916001600160a01b039182169116611251565b600281015460408051858152602081018590526001600160a01b0390921682820152517fcc11926aca009e381b48e432fbfb8e3f192d5d8be733dc474fa78831bbfdf0449181900360600190a1610b77836112cf565b505050565b6000610b866120b2565b610b8e612130565b83600185510381518110610b9e57fe5b60200260200101519050610bb5856080015161072c565b5050505063ffffffff1660e0870152606086015260408501526020840152506007860154600110610c7557816060015184600081518110610bf257fe5b60200260200101516020015114610c0f5761c38d92505050610f91565b83600081518110610c1c57fe5b60200260200101516040015163ffffffff1682604001511180610c645750611c2084600081518110610c4a57fe5b6020026020010151604001510363ffffffff168260400151105b15610c755761c38e92505050610f91565b6000610c8687868560e00151611337565b90508015610c98579250610f91915050565b8451600c14610cf85760e0830151600588018054604085015163ffffffff908116650100000000000268ffffffff000000000019919094166101000264ffffffff0019909216919091171691909117905560608201516006880155610f89565b600160345403610d0b8460e00151611528565b02836020018181510191508181525050600660018761012001510363ffffffff1681610d3357fe5b0663ffffffff1660001415610f2c57610d4a612130565b85600287510381518110610d5a57fe5b6020908102919091018101516037546101208a01516040805163c0dde98b60e01b815260051990920163ffffffff166004830152519294506001600160a01b0390911692632da8cffd92849263c0dde98b926024808301939192829003018186803b158015610dc857600080fd5b505afa158015610ddc573d6000803e3d6000fd5b505050506040513d6020811015610df257600080fd5b5051604080516001600160e01b031960e085901b1681526004810192909252516024808301926020929190829003018186803b158015610e3157600080fd5b505afa158015610e45573d6000803e3d6000fd5b505050506040513d6020811015610e5b57600080fd5b5051604080890182905282015160e0860151600092610e819263ffffffff16039061154f565b9050610e9b6001610e906115b7565b038660e0015161154f565b63ffffffff168163ffffffff161080610ed65750610ec7610eba6115bd565b6001018660e0015161154f565b63ffffffff168163ffffffff16115b15610eea5761c41895505050505050610f91565b8063ffffffff168860e0015163ffffffff1614610f105761c38995505050505050610f91565b610f1981611528565b602086018051909101905250610f459050565b610f398360e00151611528565b60208401805190910190525b816000015163ffffffff168660e0015163ffffffff1614610f6d5761c3909350505050610f91565b8560200151836020015114610f895761c4049350505050610f91565b600093505050505b9392505050565b6000828152603360205260409020600181015460028201546003830154610fcf92869290916001600160a01b039182169116611251565b600381015460408051858152602081018590526001600160a01b0390921682820152517fffa243eaeafd66e0a938ee0d270bbefd594e08a551dc29a9a44e39c719cfc79f9181900360600190a1610b77836112cf565b303b155b90565b6000611036612130565b6000611040612130565b61104a86866115c4565b9050600061105b8260000151611619565b606083015190915061106d888861163c565b156110e75761107a612157565b611084898961165b565b905082816000015111156110a757506127a6955091935060009250611112915050565b60006110b38383611759565b9050806001146110cf5796509294506000935061111292505050565b50610140015160009650929450919250611112915050565b8181111561110357506127929450909250600091506111129050565b50600094509092505050605083015b9250925092565b805160009082906001811415611147578160008151811061113657fe5b60200260200101519250505061046f565b60008111611192576040805162461bcd60e51b81526020600482015260136024820152724d7573742070726f766964652068617368657360681b604482015290519081900360640190fd5b600080805b6001841115611230575060009150815b83831015611228578383600101106111c257600184036111c7565b826001015b91506111ff8584815181106111d857fe5b602002602001015160001c8684815181106111ef57fe5b602002602001015160001c6117ca565b60001b85828151811061120e57fe5b6020908102919091010152600292909201916001016111a7565b809350611197565b8460008151811061123d57fe5b602002602001015195505050505050919050565b60365460408051633a45007160e11b815260048101879052602481018690526001600160a01b038581166044830152848116606483015291516101009093049091169163748a00e29160848082019260009290919082900301818387803b1580156112bb57600080fd5b505af1158015610452573d6000803e3d6000fd5b6000818152603360205260408120818155600181018290556002810180546001600160a01b031990811690915560038201805490911690556004810182905560058101805468ffffffffffffffffff191690556006810182905590610b7760078301826121b1565b8151600090600019015b80156114485761134f612130565b84828151811061135b57fe5b6020026020010151905061136d612130565b85600184038151811061137c57fe5b602002602001015190508551600c14158061139a5750600186510383105b156113d657815163ffffffff86811691161415806113c457508051825163ffffffff908116911614155b156113d65761c38a9350505050610f91565b81602001518160600151146113f25761c38c9350505050610f91565b816040015163ffffffff16816040015163ffffffff16118061142b5750611c2082604001510363ffffffff16816040015163ffffffff16105b1561143d5761c38f9350505050610f91565b505060001901611341565b50600784015460021161151e5761145d612130565b8360008151811061146a57fe5b602002602001015190508263ffffffff168560050160019054906101000a900463ffffffff1663ffffffff16146114a65761c392915050610f91565b80602001518560060154146114c05761c393915050610f91565b6040810151600586015463ffffffff91821665010000000000909104909116118061150c575060408101516005860154611c1f1990910163ffffffff9081166501000000000090920416105b1561151c5761c394915050610f91565b505b5060009392505050565b60008061153483611619565b90508060010181198161154357fe5b04600101915050919050565b6000826115188110156115655750611518611576565b620151808111156115765750620151805b600061158184611619565b6154609083020490506001600160ec1b038111156115a357506001600160ec1b035b6115ac81611934565b925050505b92915050565b61151890565b6201518090565b6115cc612130565b6115d683836119c5565b63ffffffff1681526115ea838360506119d4565b60608201526115f98383611a3b565b63ffffffff16604082015261160e8383611a4a565b602082015292915050565b62ffffff8116630100000063ffffffff92831604909116600219016101000a0290565b600061010061164b8484611a61565b1663ffffffff1615159392505050565b611663612157565b606060006050840193506116778585611a9d565b9250905061168885858084036119d4565b602080850191909152810193506116a185856000611ac9565b60e085019190915293506116b785856020611b79565b610100840152600493909301926116d085856000611ac9565b606085019190915293506116e685856020611b79565b6080840152600493909301926116fe858560506119d4565b8352602493909301926117118585611bb3565b60c08401526028939093019261172985856020611b79565b61012084015260048401610140840152600061174483611bbb565b60a08701525060408501525091949350505050565b6000816101000151600014611771575061274c6115b1565b8160a00151600114611788575060a08101516115b1565b81604001516117978484611c84565b146117a5575061277e6115b1565b8160c001516117b383611c99565b146117c157506127886115b1565b50600192915050565b6000610f916002806117db86611cb6565b6117e486611cb6565b60405160200180838152602001828152602001925050506040516020818303038152906040526040518082805190602001908083835b602083106118395780518252601f19909201916020918201910161181a565b51815160209384036101000a60001901801990921691161790526040519190930194509192505080830381855afa158015611878573d6000803e3d6000fd5b5050506040513d602081101561188d57600080fd5b50516040805160208181019390935281518082038401815290820191829052805190928291908401908083835b602083106118d95780518252601f1990920191602091820191016118ba565b51815160209384036101000a60001901801990921691161790526040519190930194509192505080830381855afa158015611918573d6000803e3d6000fd5b5050506040513d602081101561192d57600080fd5b5051611cb6565b60008061194d61194384611de0565b6007016003611e08565b90506000600382116119755761196e8462ffffff1683600303600802611e1e565b905061198d565b6119858460038403600802611e08565b62ffffff1690505b628000008116156119b2576119a98163ffffffff166008611e08565b90506001820191505b6119bd826018611e1e565b179392505050565b6000610f918383604801611a61565b6000611a3360026119e6868686611e25565b60405160200180828152602001915050604051602081830303815290604052604051808280519060200190808383602083106118d95780518252601f1990920191602091820191016118ba565b949350505050565b6000610f918383604401611a61565b60248282010151600090600483016115ac82611cb6565b6000816020840101516040518160031a60008201538160021a60018201538160011a60028201538160001a60038201535160e01c949350505050565b60006060611aae8484600401611e4a565b9092509050611abd8483611ed2565b60040194909350915050565b60606000806000611ada8787611f25565b96509150841580611aea57508185115b15611af6575080611af9565b50835b606081604051908082528060200260200182016040528015611b25578160200160208202803883390190505b50905060005b82811015611b6d57611b45611b408a8a611bb3565b611cb6565b828281518110611b5157fe5b6020908102919091018101919091529790970196600101611b2b565b50979596505050505050565b6000805b6008830481101561061d578060080260020a8582860181518110611b9d57fe5b016020015160f81c029190910190600101611b7d565b016020015190565b6000806000806000809050600063fabe6d6d60e01b905060006001600160e01b031990508751602089018181015b80821015611c1e5784848351161415611c135785611c0c57600482820384030196505b6001860195505b600182019150611be9565b50505060028310611c4057506000955050506003190191506127609050611c7d565b8260011415611c6a57611c538885611bb3565b965050600319909201935060019250611c7d915050565b5060009550505060031901915061276a90505b9193909250565b6000610f918383608001518460600151611fd7565b60006115b1611b4083602001518461010001518560e00151611fd7565b60405160009060ff8316815382601e1a600182015382601d1a600282015382601c1a600382015382601b1a600482015382601a1a60058201538260191a60068201538260181a60078201538260171a60088201538260161a60098201538260151a600a8201538260141a600b8201538260131a600c8201538260121a600d8201538260111a600e8201538260101a600f82015382600f1a601082015382600e1a601182015382600d1a601282015382600c1a601382015382600b1a601482015382600a1a60158201538260091a60168201538260081a60178201538260071a60188201538260061a60198201538260051a601a8201538260041a601b8201538260031a601c8201538260021a601d8201538260011a601e8201538260001a601f8201535192915050565b6000815b8015611e0257611df5816001611e08565b9050600182019150611de4565b50919050565b60008160020a8381611e1657fe5b049392505050565b60020a0290565b60006040516020818486602089010160025afa611e4157600080fd5b51949350505050565b60006060600080611e5b8686611f25565b9550915081611e8b57611e6e8686611f25565b9550915081611e7c57600080fd5b611e868686611f25565b955091505b81600114611e9857600080fd5b602485019450611ea88686611f25565b955090506060611ebb8787848101612057565b9590910160040194859450925050505b9250929050565b6000806000611ee18585611f25565b94509150600a8210611ef257600080fd5b60005b82811015611f1b57600885019450611f0d8686611f25565b810195509150600101611ef5565b5092949350505050565b6000806000848481518110611f3657fe5b01602001516001949094019360f81c905060fd811015611f5d5760ff169150829050611ecb565b8060ff1660fd1415611f8357611f7585856010611b79565b846002019250925050611ecb565b8060ff1660fe1415611fa957611f9b85856020611b79565b846004019250925050611ecb565b8060ff1660ff1415611fcf57611fc185856040611b79565b846008019250925050611ecb565b509250929050565b600083815b835181101561204e576000848281518110611ff357fe5b6020026020010151905060006002878161200957fe5b069050600080826001141561202257508290508461202d565b8261202d5750849050825b61203782826117ca565b955060028904985060018501945050505050611fdc565b50949350505050565b6060600083830390506060816040519080825280601f01601f19166020018201604052801561208d576020820181803883390190505b5090508160208201838760208a010160045afa6120a957600080fd5b95945050505050565b6040805161016081018252600080825260208201819052918101829052606081018290526080810182905260a0810182905260c0810182905260e08101829052610100810182905261012081018290529061014082015290565b815481835581811115610b7757600083815260209020610b779181019083016121d2565b60408051608081018252600080825260208201819052918101829052606081019190915290565b60405180610160016040528060008152602001600081526020016000815260200160608152602001600081526020016000815260200160008152602001606081526020016000815260200160008152602001600081525090565b50805460008255906000526020600020908101906121cf91906121d2565b50565b61102991905b808211156121ec57600081556001016121d8565b509056fe436f6e747261637420696e7374616e63652068617320616c7265616479206265656e20696e697469616c697a6564a265627a7a723158208728fdd851adfa9e09fe5e81f6b48c4354d37a49987032f63496b6dbc9ea632464736f6c634300050b0032";

    public static final String FUNC_SUPERBLOCKTIMEOUT = "superblockTimeout";

    public static final String FUNC_MINPROPOSALDEPOSIT = "minProposalDeposit";

    public static final String FUNC_SUPERBLOCKDURATION = "superblockDuration";

    public static final String FUNC_INIT = "init";

    public static final String FUNC_SETSYSCOINCLAIMMANAGER = "setSyscoinClaimManager";

    public static final String FUNC_BEGINBATTLESESSION = "beginBattleSession";

    public static final String FUNC_RESPONDBLOCKHEADERS = "respondBlockHeaders";

    public static final String FUNC_TIMEOUT = "timeout";

    public static final String FUNC_GETSUBMITTERHITTIMEOUT = "getSubmitterHitTimeout";

    public static final String FUNC_GETSUPERBLOCKBYSESSION = "getSuperblockBySession";

    public static final String FUNC_GETSESSIONCHALLENGESTATE = "getSessionChallengeState";

    public static final Event NEWBATTLE_EVENT = new Event("NewBattle", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}, new TypeReference<Address>() {}));
    ;

    public static final Event CHALLENGERCONVICTED_EVENT = new Event("ChallengerConvicted", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Uint256>() {}, new TypeReference<Address>() {}));
    ;

    public static final Event SUBMITTERCONVICTED_EVENT = new Event("SubmitterConvicted", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Uint256>() {}, new TypeReference<Address>() {}));
    ;

    protected static final HashMap<String, String> _addresses;

    static {
        _addresses = new HashMap<String, String>();
        _addresses.put("4", "0xD7FA62006631Ab61ab82Be658b2572Aabd42e5c8");
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
            typedResponse.sessionId = (Bytes32) eventValues.getNonIndexedValues().get(0);
            typedResponse.err = (Uint256) eventValues.getNonIndexedValues().get(1);
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
                typedResponse.sessionId = (Bytes32) eventValues.getNonIndexedValues().get(0);
                typedResponse.err = (Uint256) eventValues.getNonIndexedValues().get(1);
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
            typedResponse.sessionId = (Bytes32) eventValues.getNonIndexedValues().get(0);
            typedResponse.err = (Uint256) eventValues.getNonIndexedValues().get(1);
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
                typedResponse.sessionId = (Bytes32) eventValues.getNonIndexedValues().get(0);
                typedResponse.err = (Uint256) eventValues.getNonIndexedValues().get(1);
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

    public RemoteCall<TransactionReceipt> init(Uint8 _network, Address _superblocks, Uint256 _superblockDuration, Uint256 _superblockTimeout) {
        final Function function = new Function(
                FUNC_INIT, 
                Arrays.<Type>asList(_network, _superblocks, _superblockDuration, _superblockTimeout), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
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

    public RemoteCall<TransactionReceipt> respondBlockHeaders(Bytes32 sessionId, DynamicBytes blockHeaders, Uint256 numHeaders) {
        final Function function = new Function(
                FUNC_RESPONDBLOCKHEADERS, 
                Arrays.<Type>asList(sessionId, blockHeaders, numHeaders), 
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

    public static RemoteCall<SyscoinBattleManager> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(SyscoinBattleManager.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<SyscoinBattleManager> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(SyscoinBattleManager.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<SyscoinBattleManager> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(SyscoinBattleManager.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<SyscoinBattleManager> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(SyscoinBattleManager.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
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

        public Bytes32 sessionId;

        public Uint256 err;

        public Address challenger;
    }

    public static class SubmitterConvictedEventResponse {
        public Log log;

        public Bytes32 sessionId;

        public Uint256 err;

        public Address submitter;
    }
}
