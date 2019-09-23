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
    private static final String BINARY = "0x608060405234801561001057600080fd5b506123b0806100206000396000f3fe608060405234801561001057600080fd5b50600436106100a95760003560e01c8063795ea18e11610071578063795ea18e146101d8578063d1daeede146101f5578063df23ceb214610229578063ec6dbad814610264578063f1afcfa6146102a5578063f871dfe8146102ad576100a9565b806318b011de146100ae5780633678c143146100c8578063455e6166146100f057806351fcf431146100f857806371a8c18a146101a7575b600080fd5b6100b66102ca565b60408051918252519081900360200190f35b6100ee600480360360208110156100de57600080fd5b50356001600160a01b03166102d0565b005b6100b6610327565b6100ee6004803603606081101561010e57600080fd5b8135919081019060408101602082013564010000000081111561013057600080fd5b82018360208201111561014257600080fd5b8035906020019184600183028401116401000000008311171561016457600080fd5b91908080601f0160208091040260200160405190810160405280939291908181526020018383808284376000920191909152509295505091359250610333915050565b6101c4600480360360208110156101bd57600080fd5b50356104fa565b604080519115158252519081900360200190f35b6100b6600480360360208110156101ee57600080fd5b503561051a565b6100b66004803603606081101561020b57600080fd5b508035906001600160a01b0360208201358116916040013516610577565b6100ee6004803603608081101561023f57600080fd5b5060ff813516906001600160a01b0360208201351690604081013590606001356106ae565b6102816004803603602081101561027a57600080fd5b5035610797565b6040518082600181111561029157fe5b60ff16815260200191505060405180910390f35b6100b66107af565b6100b6600480360360208110156102c357600080fd5b50356107b5565b60355481565b60365461010090046001600160a01b03161580156102f657506001600160a01b03811615155b6102ff57600080fd5b603680546001600160a01b0390921661010002610100600160a81b0319909216919091179055565b6729a2241af62c000081565b60008381526033602052604090206002015483906001600160a01b0316331461035b57600080fd5b60008481526033602052604081209060365460ff16600281111561037b57fe5b14156103bc576007810154600210801590610397575082601014155b806103b25750600781015460031480156103b2575082600c14155b156103bc57600080fd5b6103c46121c5565b6103d182600101546108f4565b63ffffffff9081166101208b015260808a019390935250501660e08601526060808601919091526040850191909152602084019190915290825260009061041a84848989610a07565b9150915060008214610435576104308883610cfc565b6104ef565b426004850155610446848483610d9b565b91508115610461576104588883610cfc565b505050506104f4565b85600c14806104815750600060365460ff16600281111561047e57fe5b14155b15610490576104588883611149565b600184015460078501546002860154604080518c81526020810194909452838101929092526001600160a01b03166060830152517f0e660e6e65ec52c9dda40ab02165320c09e799891feae8fed08191cb2150b45b9181900360800190a15b505050505b50505050565b60008181526033602052604090206035546004909101540142115b919050565b60008181526033602052604081206001600582015460ff16600181111561053d57fe5b148015610551575060355481600401540142115b1561056d576105628361c36a610cfc565b61c36a915050610515565b5061c36e92915050565b60365460009061010090046001600160a01b031633811461059757600080fd5b60408051602080820188905233828401526001600160a01b0386166060808401919091528351808403909101815260809092018352815191810191909120600081815260339092529190208054156105ee57600080fd5b818155600181018790556002810180546001600160a01b038089166001600160a01b03199283161790925560038301805492881692909116919091179055600061063b600783018261221f565b5042600482015560058101805460ff1916600117905560408051888152602081018490526001600160a01b03888116828401528716606082015290517f403956bdc140717d54d4573786b4e9e773ef2e6e325e2c061476eb47711770de916080908290030190a15091505b509392505050565b600054610100900460ff16806106c757506106c76111e3565b806106d5575060005460ff16155b6107105760405162461bcd60e51b815260040180806020018281038252602e81526020018061234e602e913960400191505060405180910390fd5b600054610100900460ff1615801561073b576000805460ff1961ff0019909116610100171660011790555b6036805486919060ff1916600183600281111561075457fe5b0217905550603780546001600160a01b0319166001600160a01b038616179055603483905560358290558015610790576000805461ff00191690555b5050505050565b60009081526033602052604090206005015460ff1690565b60345481565b60006107bf612243565b600083815260336020908152604091829020825161012081018452815481526001808301549382019390935260028201546001600160a01b039081169482019490945260038201549093166060840152600481015460808401526005810154909160a084019160ff169081111561083257fe5b600181111561083d57fe5b81526020016005820160019054906101000a900463ffffffff1663ffffffff1663ffffffff16815260200160068201548152602001600782018054806020026020016040519081016040528092919081815260200182805480156108c057602002820191906000526020600020905b8154815260200190600101908083116108ac575b5050509190925250508151919250506108dd576000915050610515565b505060009081526033602052604090206007015490565b6000806000806000806000806000603760009054906101000a90046001600160a01b03166001600160a01b0316636e5b70718b6040518263ffffffff1660e01b8152600401808281526020019150506101206040518083038186803b15801561095c57600080fd5b505afa158015610970573d6000803e3d6000fd5b505050506040513d61012081101561098757600080fd5b810190808051906020019092919080519060200190929190805190602001909291908051906020019092919080519060200190929190805190602001909291908051906020019092919080519060200190929190805190602001909291905050509850985098509850985098509850985098509193959799909294969850565b60006060806001600588015460ff166001811115610a2157fe5b1415610ceb576000809050600080606087604051908082528060200260200182016040528015610a5b578160200160208202803883390190505b509050606088604051908082528060200260200182016040528015610a9a57816020015b610a8761228d565b815260200190600190039081610a7f5790505b509050600092505b8051831015610b2557610ab58a866111ea565b8351849087908110610ac357fe5b6020908102919091010191909152955093508315610aeb5750919550929350610cf392505050565b808381518110610af757fe5b602002602001015160600151828481518110610b0f57fe5b6020908102919091010152600190920191610aa2565b60078c015460031480610b495750600060365460ff166002811115610b4657fe5b14155b15610cb657610b5661228d565b81600183510381518110610b6657fe5b60200260200101519050606060006002811115610b7f57fe5b60365460ff166002811115610b9057fe5b14610b9c576001610b9f565b60045b60ff16604051908082528060200260200182016040528015610bcb578160200160208202803883390190505b509050600094505b60078e0154851015610c1b578d6007018581548110610bee57fe5b9060005260206000200154818681518110610c0557fe5b6020908102919091010152600190940193610bd3565b610c24846112d7565b818681518110610c3057fe5b602002602001018181525050610c45816112d7565b8d5114610c615761c37888995099505050505050505050610cf3565b816040015163ffffffff168d6040015114610c8b5761c37488995099505050505050505050610cf3565b8c60600151826060015114610caf5761c38b88995099505050505050505050610cf3565b5050610cda565b8b600701610cc3836112d7565b815460018101835560009283526020909220909101555b600097509550610cf3945050505050565b61c364925090505b94509492505050565b6000828152603360205260409020600181015460038201546002830154610d3392869290916001600160a01b03918216911661140f565b600181015460028201546040805186815260208101939093528281018590526001600160a01b039091166060830152517fae4f7410342e27aa0df7167c691dfd96c5d906aff82fbe0279985e0cf48be5e39181900360800190a1610d968361148d565b505050565b6000610da56121c5565b610dad61228d565b83600185510381518110610dbd57fe5b60200260200101519050610dd485608001516108f4565b5050505063ffffffff1660e0870152606086015260408501526020840152506007860154600110610e2e57816060015184600081518110610e1157fe5b60200260200101516020015114610e2e5761c38d92505050611142565b6000610e3f87868560e001516114f1565b90508015610e51579250611142915050565b8451600c14610e905760e083015160058801805463ffffffff9092166101000264ffffffff00199092169190911790556060820151600688015561113a565b600060365460ff166002811115610ea357fe5b141561113a57600160345403610ebc8460e0015161163b565b02836020018181510191508181525050600660018761012001510363ffffffff1681610ee457fe5b0663ffffffff16600014156110dd57610efb61228d565b85600287510381518110610f0b57fe5b6020908102919091018101516037546101208a01516040805163c0dde98b60e01b815260051990920163ffffffff166004830152519294506001600160a01b0390911692632da8cffd92849263c0dde98b926024808301939192829003018186803b158015610f7957600080fd5b505afa158015610f8d573d6000803e3d6000fd5b505050506040513d6020811015610fa357600080fd5b5051604080516001600160e01b031960e085901b1681526004810192909252516024808301926020929190829003018186803b158015610fe257600080fd5b505afa158015610ff6573d6000803e3d6000fd5b505050506040513d602081101561100c57600080fd5b5051604080890182905282015160e08601516000926110329263ffffffff160390611662565b905061104c60016110416116ca565b038660e00151611662565b63ffffffff168163ffffffff161080611087575061107861106b6116d0565b6001018660e00151611662565b63ffffffff168163ffffffff16115b1561109b5761c41895505050505050611142565b8063ffffffff168860e0015163ffffffff16146110c15761c38995505050505050611142565b6110ca8161163b565b6020860180519091019052506110f69050565b6110ea8360e0015161163b565b60208401805190910190525b816000015163ffffffff168660e0015163ffffffff161461111e5761c3909350505050611142565b856020015183602001511461113a5761c4049350505050611142565b600093505050505b9392505050565b600082815260336020526040902060018101546002820154600383015461118092869290916001600160a01b03918216911661140f565b600181015460038201546040805186815260208101939093528281018590526001600160a01b039091166060830152517f766980202352ff259a9ea889942266c29c1ca6260254f21cd529af44aeb5637c9181900360800190a1610d968361148d565b303b155b90565b60006111f461228d565b60006111fe61228d565b61120886866116d7565b90506000611219826000015161172c565b606083015190915061122b888861174f565b156112a5576112386122b4565b611242898961176e565b9050828160000151111561126557506127a69550919350600092506112d0915050565b6000611271838361186c565b90508060011461128d579650929450600093506112d092505050565b506101400151600096509294509192506112d0915050565b818111156112c157506127929450909250600091506112d09050565b50600094509092505050605083015b9250925092565b80516000908290600181141561130557816000815181106112f457fe5b602002602001015192505050610515565b60008111611350576040805162461bcd60e51b81526020600482015260136024820152724d7573742070726f766964652068617368657360681b604482015290519081900360640190fd5b600080805b60018411156113ee575060009150815b838310156113e6578383600101106113805760018403611385565b826001015b91506113bd85848151811061139657fe5b602002602001015160001c8684815181106113ad57fe5b602002602001015160001c6118dd565b60001b8582815181106113cc57fe5b602090810291909101015260029290920191600101611365565b809350611355565b846000815181106113fb57fe5b602002602001015195505050505050919050565b60365460408051633a45007160e11b815260048101879052602481018690526001600160a01b038581166044830152848116606483015291516101009093049091169163748a00e29160848082019260009290919082900301818387803b15801561147957600080fd5b505af11580156104ef573d6000803e3d6000fd5b6000818152603360205260408120818155600181018290556002810180546001600160a01b031990811690915560038201805490911690556004810182905560058101805464ffffffffff191690556006810182905590610d96600783018261230e565b8151600090600019015b80156115b75761150961228d565b84828151811061151557fe5b6020026020010151905061152761228d565b85600184038151811061153657fe5b602002602001015190508551600c1415806115545750600186510383105b1561159057815163ffffffff868116911614158061157e57508051825163ffffffff908116911614155b156115905761c38a9350505050611142565b81602001518160600151146115ac5761c38c9350505050611142565b5050600019016114fb565b506007840154600211611631576115cc61228d565b836000815181106115d957fe5b602002602001015190508263ffffffff168560050160019054906101000a900463ffffffff1663ffffffff16146116155761c392915050611142565b806020015185600601541461162f5761c393915050611142565b505b5060009392505050565b6000806116478361172c565b90508060010181198161165657fe5b04600101915050919050565b6000826115188110156116785750611518611689565b620151808111156116895750620151805b60006116948461172c565b6154609083020490506001600160ec1b038111156116b657506001600160ec1b035b6116bf81611a47565b925050505b92915050565b61151890565b6201518090565b6116df61228d565b6116e98383611ad8565b63ffffffff1681526116fd83836050611ae7565b606082015261170c8383611b4e565b63ffffffff1660408201526117218383611b5d565b602082015292915050565b62ffffff8116630100000063ffffffff92831604909116600219016101000a0290565b600061010061175e8484611b74565b1663ffffffff1615159392505050565b6117766122b4565b6060600060508401935061178a8585611bb0565b9250905061179b8585808403611ae7565b602080850191909152810193506117b485856000611bdc565b60e085019190915293506117ca85856020611c8c565b610100840152600493909301926117e385856000611bdc565b606085019190915293506117f985856020611c8c565b60808401526004939093019261181185856050611ae7565b8352602493909301926118248585611cc6565b60c08401526028939093019261183c85856020611c8c565b61012084015260048401610140840152600061185783611cce565b60a08701525060408501525091949350505050565b6000816101000151600014611884575061274c6116c4565b8160a0015160011461189b575060a08101516116c4565b81604001516118aa8484611d97565b146118b8575061277e6116c4565b8160c001516118c683611dac565b146118d457506127886116c4565b50600192915050565b60006111426002806118ee86611dc9565b6118f786611dc9565b60405160200180838152602001828152602001925050506040516020818303038152906040526040518082805190602001908083835b6020831061194c5780518252601f19909201916020918201910161192d565b51815160209384036101000a60001901801990921691161790526040519190930194509192505080830381855afa15801561198b573d6000803e3d6000fd5b5050506040513d60208110156119a057600080fd5b50516040805160208181019390935281518082038401815290820191829052805190928291908401908083835b602083106119ec5780518252601f1990920191602091820191016119cd565b51815160209384036101000a60001901801990921691161790526040519190930194509192505080830381855afa158015611a2b573d6000803e3d6000fd5b5050506040513d6020811015611a4057600080fd5b5051611dc9565b600080611a60611a5684611ef3565b6007016003611f1b565b9050600060038211611a8857611a818462ffffff1683600303600802611f31565b9050611aa0565b611a988460038403600802611f1b565b62ffffff1690505b62800000811615611ac557611abc8163ffffffff166008611f1b565b90506001820191505b611ad0826018611f31565b179392505050565b60006111428383604801611b74565b6000611b466002611af9868686611f38565b60405160200180828152602001915050604051602081830303815290604052604051808280519060200190808383602083106119ec5780518252601f1990920191602091820191016119cd565b949350505050565b60006111428383604401611b74565b60248282010151600090600483016116bf82611dc9565b6000816020840101516040518160031a60008201538160021a60018201538160011a60028201538160001a60038201535160e01c949350505050565b60006060611bc18484600401611f5d565b9092509050611bd08483611fe5565b60040194909350915050565b60606000806000611bed8787612038565b96509150841580611bfd57508185115b15611c09575080611c0c565b50835b606081604051908082528060200260200182016040528015611c38578160200160208202803883390190505b50905060005b82811015611c8057611c58611c538a8a611cc6565b611dc9565b828281518110611c6457fe5b6020908102919091018101919091529790970196600101611c3e565b50979596505050505050565b6000805b600883048110156106a6578060080260020a8582860181518110611cb057fe5b016020015160f81c029190910190600101611c90565b016020015190565b6000806000806000809050600063fabe6d6d60e01b905060006001600160e01b031990508751602089018181015b80821015611d315784848351161415611d265785611d1f57600482820384030196505b6001860195505b600182019150611cfc565b50505060028310611d5357506000955050506003190191506127609050611d90565b8260011415611d7d57611d668885611cc6565b965050600319909201935060019250611d90915050565b5060009550505060031901915061276a90505b9193909250565b600061114283836080015184606001516120ea565b60006116c4611c5383602001518461010001518560e001516120ea565b60405160009060ff8316815382601e1a600182015382601d1a600282015382601c1a600382015382601b1a600482015382601a1a60058201538260191a60068201538260181a60078201538260171a60088201538260161a60098201538260151a600a8201538260141a600b8201538260131a600c8201538260121a600d8201538260111a600e8201538260101a600f82015382600f1a601082015382600e1a601182015382600d1a601282015382600c1a601382015382600b1a601482015382600a1a60158201538260091a60168201538260081a60178201538260071a60188201538260061a60198201538260051a601a8201538260041a601b8201538260031a601c8201538260021a601d8201538260011a601e8201538260001a601f8201535192915050565b6000815b8015611f1557611f08816001611f1b565b9050600182019150611ef7565b50919050565b60008160020a8381611f2957fe5b049392505050565b60020a0290565b60006040516020818486602089010160025afa611f5457600080fd5b51949350505050565b60006060600080611f6e8686612038565b9550915081611f9e57611f818686612038565b9550915081611f8f57600080fd5b611f998686612038565b955091505b81600114611fab57600080fd5b602485019450611fbb8686612038565b955090506060611fce878784810161216a565b9590910160040194859450925050505b9250929050565b6000806000611ff48585612038565b94509150600a821061200557600080fd5b60005b8281101561202e576008850194506120208686612038565b810195509150600101612008565b5092949350505050565b600080600084848151811061204957fe5b01602001516001949094019360f81c905060fd8110156120705760ff169150829050611fde565b8060ff1660fd14156120965761208885856010611c8c565b846002019250925050611fde565b8060ff1660fe14156120bc576120ae85856020611c8c565b846004019250925050611fde565b8060ff1660ff14156120e2576120d485856040611c8c565b846008019250925050611fde565b509250929050565b600083815b835181101561216157600084828151811061210657fe5b6020026020010151905060006002878161211c57fe5b0690506000808260011415612135575082905084612140565b826121405750849050825b61214a82826118dd565b9550600289049850600185019450505050506120ef565b50949350505050565b6060600083830390506060816040519080825280601f01601f1916602001820160405280156121a0576020820181803883390190505b5090508160208201838760208a010160045afa6121bc57600080fd5b95945050505050565b6040805161016081018252600080825260208201819052918101829052606081018290526080810182905260a0810182905260c0810182905260e08101829052610100810182905261012081018290529061014082015290565b815481835581811115610d9657600083815260209020610d9691810190830161232f565b604080516101208101825260008082526020820181905291810182905260608101829052608081018290529060a08201908152600060208201819052604082015260609081015290565b60408051608081018252600080825260208201819052918101829052606081019190915290565b60405180610160016040528060008152602001600081526020016000815260200160608152602001600081526020016000815260200160008152602001606081526020016000815260200160008152602001600081525090565b508054600082559060005260206000209081019061232c919061232f565b50565b6111e791905b808211156123495760008155600101612335565b509056fe436f6e747261637420696e7374616e63652068617320616c7265616479206265656e20696e697469616c697a6564a265627a7a72315820624d0f600cdeb9ed9583e07b261fd172820dc28aaa651729c20f358bedc9415064736f6c634300050b0032";

    public static final String FUNC_SUPERBLOCKTIMEOUT = "superblockTimeout";

    public static final String FUNC_MINPROPOSALDEPOSIT = "minProposalDeposit";

    public static final String FUNC_SUPERBLOCKDURATION = "superblockDuration";

    public static final String FUNC_INIT = "init";

    public static final String FUNC_SETSYSCOINCLAIMMANAGER = "setSyscoinClaimManager";

    public static final String FUNC_BEGINBATTLESESSION = "beginBattleSession";

    public static final String FUNC_RESPONDBLOCKHEADERS = "respondBlockHeaders";

    public static final String FUNC_TIMEOUT = "timeout";

    public static final String FUNC_GETSUBMITTERHITTIMEOUT = "getSubmitterHitTimeout";

    public static final String FUNC_GETNUMMERKLEHASHESBYSESSION = "getNumMerkleHashesBySession";

    public static final String FUNC_GETSESSIONCHALLENGESTATE = "getSessionChallengeState";

    public static final Event NEWBATTLE_EVENT = new Event("NewBattle", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}, new TypeReference<Address>() {}));
    ;

    public static final Event CHALLENGERCONVICTED_EVENT = new Event("ChallengerConvicted", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Uint256>() {}, new TypeReference<Address>() {}));
    ;

    public static final Event SUBMITTERCONVICTED_EVENT = new Event("SubmitterConvicted", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Uint256>() {}, new TypeReference<Address>() {}));
    ;

    public static final Event RESPONDBLOCKHEADERS_EVENT = new Event("RespondBlockHeaders", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Uint256>() {}, new TypeReference<Address>() {}));
    ;

    protected static final HashMap<String, String> _addresses;

    static {
        _addresses = new HashMap<String, String>();
        _addresses.put("4", "0xc33C03e8e80F402145EB05450C1B27AC93327383");
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

    public RemoteFunctionCall<Uint256> superblockTimeout() {
        final Function function = new Function(FUNC_SUPERBLOCKTIMEOUT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public RemoteFunctionCall<Uint256> minProposalDeposit() {
        final Function function = new Function(FUNC_MINPROPOSALDEPOSIT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public RemoteFunctionCall<Uint256> superblockDuration() {
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
            typedResponse.err = (Uint256) eventValues.getNonIndexedValues().get(2);
            typedResponse.challenger = (Address) eventValues.getNonIndexedValues().get(3);
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
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, SubmitterConvictedEventResponse>() {
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
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, RespondBlockHeadersEventResponse>() {
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

    public RemoteFunctionCall<TransactionReceipt> init(Uint8 _network, Address _superblocks, Uint256 _superblockDuration, Uint256 _superblockTimeout) {
        final Function function = new Function(
                FUNC_INIT, 
                Arrays.<Type>asList(_network, _superblocks, _superblockDuration, _superblockTimeout), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> setSyscoinClaimManager(Address _syscoinClaimManager) {
        final Function function = new Function(
                FUNC_SETSYSCOINCLAIMMANAGER, 
                Arrays.<Type>asList(_syscoinClaimManager), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> beginBattleSession(Bytes32 superblockHash, Address submitter, Address challenger) {
        final Function function = new Function(
                FUNC_BEGINBATTLESESSION, 
                Arrays.<Type>asList(superblockHash, submitter, challenger), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> respondBlockHeaders(Bytes32 sessionId, DynamicBytes blockHeaders, Uint256 numHeaders) {
        final Function function = new Function(
                FUNC_RESPONDBLOCKHEADERS, 
                Arrays.<Type>asList(sessionId, blockHeaders, numHeaders), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> timeout(Bytes32 sessionId) {
        final Function function = new Function(
                FUNC_TIMEOUT, 
                Arrays.<Type>asList(sessionId), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Bool> getSubmitterHitTimeout(Bytes32 sessionId) {
        final Function function = new Function(FUNC_GETSUBMITTERHITTIMEOUT, 
                Arrays.<Type>asList(sessionId), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public RemoteFunctionCall<Uint256> getNumMerkleHashesBySession(Bytes32 sessionId) {
        final Function function = new Function(FUNC_GETNUMMERKLEHASHESBYSESSION, 
                Arrays.<Type>asList(sessionId), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public RemoteFunctionCall<Uint8> getSessionChallengeState(Bytes32 sessionId) {
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

    public static class NewBattleEventResponse extends BaseEventResponse {
        public Bytes32 superblockHash;

        public Bytes32 sessionId;

        public Address submitter;

        public Address challenger;
    }

    public static class ChallengerConvictedEventResponse extends BaseEventResponse {
        public Bytes32 superblockHash;

        public Bytes32 sessionId;

        public Uint256 err;

        public Address challenger;
    }

    public static class SubmitterConvictedEventResponse extends BaseEventResponse {
        public Bytes32 superblockHash;

        public Bytes32 sessionId;

        public Uint256 err;

        public Address submitter;
    }

    public static class RespondBlockHeadersEventResponse extends BaseEventResponse {
        public Bytes32 superblockHash;

        public Bytes32 sessionId;

        public Uint256 merkleHashCount;

        public Address submitter;
    }
}
