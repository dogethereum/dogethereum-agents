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
 * <p>Generated with web3j version 4.5.4.
 */
@SuppressWarnings("rawtypes")
public class SyscoinBattleManager extends Contract {
    private static final String BINARY = "0x608060405234801561001057600080fd5b50612571806100206000396000f3fe608060405234801561001057600080fd5b50600436106100a95760003560e01c8063795ea18e11610071578063795ea18e146101d8578063d1daeede146101f5578063df23ceb214610229578063ec6dbad814610264578063f1afcfa6146102a5578063f871dfe8146102ad576100a9565b806318b011de146100ae5780633678c143146100c8578063455e6166146100f057806351fcf431146100f857806371a8c18a146101a7575b600080fd5b6100b66102ca565b60408051918252519081900360200190f35b6100ee600480360360208110156100de57600080fd5b50356001600160a01b03166102d0565b005b6100b6610327565b6100ee6004803603606081101561010e57600080fd5b8135919081019060408101602082013564010000000081111561013057600080fd5b82018360208201111561014257600080fd5b8035906020019184600183028401116401000000008311171561016457600080fd5b91908080601f0160208091040260200160405190810160405280939291908181526020018383808284376000920191909152509295505091359250610333915050565b6101c4600480360360208110156101bd57600080fd5b5035610500565b604080519115158252519081900360200190f35b6100b6600480360360208110156101ee57600080fd5b5035610520565b6100b66004803603606081101561020b57600080fd5b508035906001600160a01b036020820135811691604001351661057d565b6100ee6004803603608081101561023f57600080fd5b5060ff813516906001600160a01b0360208201351690604081013590606001356106b4565b6102816004803603602081101561027a57600080fd5b503561079d565b6040518082600181111561029157fe5b60ff16815260200191505060405180910390f35b6100b66107b5565b6100b6600480360360208110156102c357600080fd5b50356107bb565b60355481565b60365461010090046001600160a01b03161580156102f657506001600160a01b03811615155b6102ff57600080fd5b603680546001600160a01b0390921661010002610100600160a81b0319909216919091179055565b6729a2241af62c000081565b60008381526033602052604090206002015483906001600160a01b0316331461035b57600080fd5b60008481526033602052604081209060365460ff16600281111561037b57fe5b14156103bc576007810154600210801590610397575082601014155b806103b25750600781015460031480156103b2575082600c14155b156103bc57600080fd5b6103c461235f565b6103d182600101546108fa565b63ffffffff9081166101408c015260a08b019390935250501661010087015260808601526060808601919091526040850191909152602084019190915290825260009061042084848989610a1a565b915091506000821461043b576104368883610d0f565b6104f5565b42600485015561044c848483610dae565b915081156104675761045e8883610d0f565b505050506104fa565b85600c14806104875750600060365460ff16600281111561048457fe5b14155b156104965761045e88836111e0565b600184015460078501546002860154604080518c81526020810194909452838101929092526001600160a01b03166060830152517f0e660e6e65ec52c9dda40ab02165320c09e799891feae8fed08191cb2150b45b9181900360800190a15b505050505b50505050565b60008181526033602052604090206035546004909101540142115b919050565b60008181526033602052604081206001600582015460ff16600181111561054357fe5b148015610557575060355481600401540142115b15610573576105688361c36a610d0f565b61c36a91505061051b565b5061c36e92915050565b60365460009061010090046001600160a01b031633811461059d57600080fd5b60408051602080820188905233828401526001600160a01b0386166060808401919091528351808403909101815260809092018352815191810191909120600081815260339092529190208054156105f457600080fd5b818155600181018790556002810180546001600160a01b038089166001600160a01b03199283161790925560038301805492881692909116919091179055600061064160078301826123c1565b5042600482015560058101805460ff1916600117905560408051888152602081018490526001600160a01b03888116828401528716606082015290517f403956bdc140717d54d4573786b4e9e773ef2e6e325e2c061476eb47711770de916080908290030190a15091505b509392505050565b600054610100900460ff16806106cd57506106cd61127a565b806106db575060005460ff16155b6107165760405162461bcd60e51b815260040180806020018281038252602e81526020018061250f602e913960400191505060405180910390fd5b600054610100900460ff16158015610741576000805460ff1961ff0019909116610100171660011790555b6036805486919060ff1916600183600281111561075a57fe5b0217905550603780546001600160a01b0319166001600160a01b038616179055603483905560358290558015610796576000805461ff00191690555b5050505050565b60009081526033602052604090206005015460ff1690565b60345481565b60006107c56123e5565b600083815260336020908152604091829020825161012081018452815481526001808301549382019390935260028201546001600160a01b039081169482019490945260038201549093166060840152600481015460808401526005810154909160a084019160ff169081111561083857fe5b600181111561084357fe5b81526020016005820160019054906101000a900463ffffffff1663ffffffff1663ffffffff16815260200160068201548152602001600782018054806020026020016040519081016040528092919081815260200182805480156108c657602002820191906000526020600020905b8154815260200190600101908083116108b2575b5050509190925250508151919250506108e357600091505061051b565b505060009081526033602052604090206007015490565b600080600080600080600080600080603760009054906101000a90046001600160a01b03166001600160a01b0316636e5b70718c6040518263ffffffff1660e01b8152600401808281526020019150506101406040518083038186803b15801561096357600080fd5b505afa158015610977573d6000803e3d6000fd5b505050506040513d61014081101561098e57600080fd5b810190808051906020019092919080519060200190929190805190602001909291908051906020019092919080519060200190929190805190602001909291908051906020019092919080519060200190929190805190602001909291908051906020019092919050505099509950995099509950995099509950995099509193959799509193959799565b60006060806001600588015460ff166001811115610a3457fe5b1415610cfe576000809050600080606087604051908082528060200260200182016040528015610a6e578160200160208202803883390190505b509050606088604051908082528060200260200182016040528015610aad57816020015b610a9a61242f565b815260200190600190039081610a925790505b509050600092505b8051831015610b3857610ac88a86611281565b8351849087908110610ad657fe5b6020908102919091010191909152955093508315610afe5750919550929350610d0692505050565b808381518110610b0a57fe5b602002602001015160600151828481518110610b2257fe5b6020908102919091010152600190920191610ab5565b60078c015460031480610b5c5750600060365460ff166002811115610b5957fe5b14155b15610cc957610b6961242f565b81600183510381518110610b7957fe5b60200260200101519050606060006002811115610b9257fe5b60365460ff166002811115610ba357fe5b14610baf576001610bb2565b60045b60ff16604051908082528060200260200182016040528015610bde578160200160208202803883390190505b509050600094505b60078e0154851015610c2e578d6007018581548110610c0157fe5b9060005260206000200154818681518110610c1857fe5b6020908102919091010152600190940193610be6565b610c378461136e565b818681518110610c4357fe5b602002602001018181525050610c588161136e565b8d5114610c745761c37888995099505050505050505050610d06565b816040015163ffffffff168d6040015114610c9e5761c37488995099505050505050505050610d06565b8c60800151826060015114610cc25761c38b88995099505050505050505050610d06565b5050610ced565b8b600701610cd68361136e565b815460018101835560009283526020909220909101555b600097509550610d06945050505050565b61c364925090505b94509492505050565b6000828152603360205260409020600181015460038201546002830154610d4692869290916001600160a01b0391821691166114a6565b600181015460028201546040805186815260208101939093528281018590526001600160a01b039091166060830152517fae4f7410342e27aa0df7167c691dfd96c5d906aff82fbe0279985e0cf48be5e39181900360800190a1610da983611524565b505050565b6000610db861235f565b610dc061242f565b83600185510381518110610dd057fe5b60200260200101519050610de78560a001516108fa565b5050505063ffffffff166101008801526080870152606086015260408501526020840152506007860154600110610e4757816080015184600081518110610e2a57fe5b60200260200101516020015114610e475761c38d925050506111d9565b6000610e598786856101000151611588565b90508015610e6b5792506111d9915050565b8451600c14610ea6576101008381015160058901805464ffffffff00191663ffffffff909216909202179055606082015160068801556111d1565b856040015142611c2f0111610ec25761c37393505050506111d9565b6000610ecd866116d2565b905086606001518114610ee85761c3979450505050506111d9565b83606001518111610f015761c3989450505050506111d9565b80612a30014211610f1a5761c3959450505050506111d9565b600060365460ff166002811115610f2d57fe5b14156111cf57600160345403610f47856101000151611736565b02846020018181510191508181525050600660018861014001510363ffffffff1681610f6f57fe5b0663ffffffff166000141561116e57610f8661242f565b86600288510381518110610f9657fe5b6020908102919091018101516037546101408b01516040805163c0dde98b60e01b815260051990920163ffffffff166004830152519294506001600160a01b0390911692632da8cffd92849263c0dde98b926024808301939192829003018186803b15801561100457600080fd5b505afa158015611018573d6000803e3d6000fd5b505050506040513d602081101561102e57600080fd5b5051604080516001600160e01b031960e085901b1681526004810192909252516024808301926020929190829003018186803b15801561106d57600080fd5b505afa158015611081573d6000803e3d6000fd5b505050506040513d602081101561109757600080fd5b50516040808a018290528201516101008701516000926110be9263ffffffff16039061175d565b90506110d960016110cd6117c3565b0387610100015161175d565b63ffffffff168163ffffffff16108061111557506111066110f86117c9565b60010187610100015161175d565b63ffffffff168163ffffffff16115b1561112a5761c41896505050505050506111d9565b8063ffffffff1689610100015163ffffffff16146111525761c38996505050505050506111d9565b61115b81611736565b6020870180519091019052506111889050565b61117c846101000151611736565b60208501805190910190525b826000015163ffffffff1687610100015163ffffffff16146111b25761c3909450505050506111d9565b86602001518460200151146111cf5761c4049450505050506111d9565b505b600093505050505b9392505050565b600082815260336020526040902060018101546002820154600383015461121792869290916001600160a01b0391821691166114a6565b600181015460038201546040805186815260208101939093528281018590526001600160a01b039091166060830152517f766980202352ff259a9ea889942266c29c1ca6260254f21cd529af44aeb5637c9181900360800190a1610da983611524565b303b155b90565b600061128b61242f565b600061129561242f565b61129f86866117cf565b905060006112b08260000151611824565b60608301519091506112c28888611847565b1561133c576112cf612456565b6112d98989611866565b905082816000015111156112fc57506127a6955091935060009250611367915050565b60006113088383611964565b9050806001146113245796509294506000935061136792505050565b50610140015160009650929450919250611367915050565b8181111561135857506127929450909250600091506113679050565b50600094509092505050605083015b9250925092565b80516000908290600181141561139c578160008151811061138b57fe5b60200260200101519250505061051b565b600081116113e7576040805162461bcd60e51b81526020600482015260136024820152724d7573742070726f766964652068617368657360681b604482015290519081900360640190fd5b600080805b6001841115611485575060009150815b8383101561147d57838360010110611417576001840361141c565b826001015b915061145485848151811061142d57fe5b602002602001015160001c86848151811061144457fe5b602002602001015160001c6119d5565b60001b85828151811061146357fe5b6020908102919091010152600292909201916001016113fc565b8093506113ec565b8460008151811061149257fe5b602002602001015195505050505050919050565b60365460408051633a45007160e11b815260048101879052602481018690526001600160a01b038581166044830152848116606483015291516101009093049091169163748a00e29160848082019260009290919082900301818387803b15801561151057600080fd5b505af11580156104f5573d6000803e3d6000fd5b6000818152603360205260408120818155600181018290556002810180546001600160a01b031990811690915560038201805490911690556004810182905560058101805464ffffffffff191690556006810182905590610da960078301826124b0565b8151600090600019015b801561164e576115a061242f565b8482815181106115ac57fe5b602002602001015190506115be61242f565b8560018403815181106115cd57fe5b602002602001015190508551600c1415806115eb5750600186510383105b1561162757815163ffffffff868116911614158061161557508051825163ffffffff908116911614155b156116275761c38a93505050506111d9565b81602001518160600151146116435761c38c93505050506111d9565b505060001901611592565b5060078401546002116116c85761166361242f565b8360008151811061167057fe5b602002602001015190508263ffffffff168560050160019054906101000a900463ffffffff1663ffffffff16146116ac5761c3929150506111d9565b80602001518560060154146116c65761c3939150506111d9565b505b5060009392505050565b60006116dc6124d1565b60005b600b811015611722578381600101815181106116f757fe5b60200260200101516040015163ffffffff168282600b811061171557fe5b60200201526001016116df565b5061172c81611b3f565b60a0015192915050565b60008061174283611824565b90508060010181198161175157fe5b04600101915050919050565b6000826143808110156117735750614380611782565b61697881111561178257506169785b600061178d84611824565b6154609083020490506001600160ec1b038111156117af57506001600160ec1b035b6117b881611be1565b925050505b92915050565b61438090565b61697890565b6117d761242f565b6117e18383611c72565b63ffffffff1681526117f583836050611c81565b60608201526118048383611ce8565b63ffffffff1660408201526118198383611cf7565b602082015292915050565b62ffffff8116630100000063ffffffff92831604909116600219016101000a0290565b60006101006118568484611d0e565b1663ffffffff1615159392505050565b61186e612456565b606060006050840193506118828585611d4a565b925090506118938585808403611c81565b602080850191909152810193506118ac85856000611d76565b60e085019190915293506118c285856020611e26565b610100840152600493909301926118db85856000611d76565b606085019190915293506118f185856020611e26565b60808401526004939093019261190985856050611c81565b83526024939093019261191c8585611e60565b60c08401526028939093019261193485856020611e26565b61012084015260048401610140840152600061194f83611e68565b60a08701525060408501525091949350505050565b600081610100015160001461197c575061274c6117bd565b8160a00151600114611993575060a08101516117bd565b81604001516119a28484611f31565b146119b0575061277e6117bd565b8160c001516119be83611f46565b146119cc57506127886117bd565b50600192915050565b60006111d96002806119e686611f63565b6119ef86611f63565b60405160200180838152602001828152602001925050506040516020818303038152906040526040518082805190602001908083835b60208310611a445780518252601f199092019160209182019101611a25565b51815160209384036101000a60001901801990921691161790526040519190930194509192505080830381855afa158015611a83573d6000803e3d6000fd5b5050506040513d6020811015611a9857600080fd5b50516040805160208181019390935281518082038401815290820191829052805190928291908401908083835b60208310611ae45780518252601f199092019160209182019101611ac5565b51815160209384036101000a60001901801990921691161790526040519190930194509192505080830381855afa158015611b23573d6000803e3d6000fd5b5050506040513d6020811015611b3857600080fd5b5051611f63565b60005b600b811015611bdd57600181015b600b811015611bd4578281600b8110611b6557fe5b60200201518383600b8110611b7657fe5b60200201511115611bcc5760008383600b8110611b8f57fe5b602002015190508382600b8110611ba257fe5b60200201518484600b8110611bb357fe5b6020020152808483600b8110611bc557fe5b6020020152505b600101611b50565b50600101611b42565b5050565b600080611bfa611bf08461208d565b60070160036120b5565b9050600060038211611c2257611c1b8462ffffff16836003036008026120cb565b9050611c3a565b611c3284600384036008026120b5565b62ffffff1690505b62800000811615611c5f57611c568163ffffffff1660086120b5565b90506001820191505b611c6a8260186120cb565b179392505050565b60006111d98383604801611d0e565b6000611ce06002611c938686866120d2565b6040516020018082815260200191505060405160208183030381529060405260405180828051906020019080838360208310611ae45780518252601f199092019160209182019101611ac5565b949350505050565b60006111d98383604401611d0e565b60248282010151600090600483016117b882611f63565b6000816020840101516040518160031a60008201538160021a60018201538160011a60028201538160001a60038201535160e01c949350505050565b60006060611d5b84846004016120f7565b9092509050611d6a848361217f565b60040194909350915050565b60606000806000611d8787876121d2565b96509150841580611d9757508185115b15611da3575080611da6565b50835b606081604051908082528060200260200182016040528015611dd2578160200160208202803883390190505b50905060005b82811015611e1a57611df2611ded8a8a611e60565b611f63565b828281518110611dfe57fe5b6020908102919091018101919091529790970196600101611dd8565b50979596505050505050565b6000805b600883048110156106ac578060080260020a8582860181518110611e4a57fe5b016020015160f81c029190910190600101611e2a565b016020015190565b6000806000806000809050600063fabe6d6d60e01b905060006001600160e01b031990508751602089018181015b80821015611ecb5784848351161415611ec05785611eb957600482820384030196505b6001860195505b600182019150611e96565b50505060028310611eed57506000955050506003190191506127609050611f2a565b8260011415611f1757611f008885611e60565b965050600319909201935060019250611f2a915050565b5060009550505060031901915061276a90505b9193909250565b60006111d98383608001518460600151612284565b60006117bd611ded83602001518461010001518560e00151612284565b60405160009060ff8316815382601e1a600182015382601d1a600282015382601c1a600382015382601b1a600482015382601a1a60058201538260191a60068201538260181a60078201538260171a60088201538260161a60098201538260151a600a8201538260141a600b8201538260131a600c8201538260121a600d8201538260111a600e8201538260101a600f82015382600f1a601082015382600e1a601182015382600d1a601282015382600c1a601382015382600b1a601482015382600a1a60158201538260091a60168201538260081a60178201538260071a60188201538260061a60198201538260051a601a8201538260041a601b8201538260031a601c8201538260021a601d8201538260011a601e8201538260001a601f8201535192915050565b6000815b80156120af576120a28160016120b5565b9050600182019150612091565b50919050565b60008160020a83816120c357fe5b049392505050565b60020a0290565b60006040516020818486602089010160025afa6120ee57600080fd5b51949350505050565b6000606060008061210886866121d2565b95509150816121385761211b86866121d2565b955091508161212957600080fd5b61213386866121d2565b955091505b8160011461214557600080fd5b60248501945061215586866121d2565b9550905060606121688787848101612304565b9590910160040194859450925050505b9250929050565b600080600061218e85856121d2565b94509150600a821061219f57600080fd5b60005b828110156121c8576008850194506121ba86866121d2565b8101955091506001016121a2565b5092949350505050565b60008060008484815181106121e357fe5b01602001516001949094019360f81c905060fd81101561220a5760ff169150829050612178565b8060ff1660fd14156122305761222285856010611e26565b846002019250925050612178565b8060ff1660fe14156122565761224885856020611e26565b846004019250925050612178565b8060ff1660ff141561227c5761226e85856040611e26565b846008019250925050612178565b509250929050565b600083815b83518110156122fb5760008482815181106122a057fe5b602002602001015190506000600287816122b657fe5b06905060008082600114156122cf5750829050846122da565b826122da5750849050825b6122e482826119d5565b955060028904985060018501945050505050612289565b50949350505050565b6060600083830390506060816040519080825280601f01601f19166020018201604052801561233a576020820181803883390190505b5090508160208201838760208a010160045afa61235657600080fd5b95945050505050565b6040805161018081018252600080825260208201819052918101829052606081018290526080810182905260a0810182905260c0810182905260e081018290526101008101829052610120810182905261014081018290529061016082015290565b815481835581811115610da957600083815260209020610da99181019083016124f0565b604080516101208101825260008082526020820181905291810182905260608101829052608081018290529060a08201908152600060208201819052604082015260609081015290565b60408051608081018252600080825260208201819052918101829052606081019190915290565b60405180610160016040528060008152602001600081526020016000815260200160608152602001600081526020016000815260200160008152602001606081526020016000815260200160008152602001600081525090565b50805460008255906000526020600020908101906124ce91906124f0565b50565b604051806101600160405280600b906020820280388339509192915050565b61127e91905b8082111561250a57600081556001016124f6565b509056fe436f6e747261637420696e7374616e63652068617320616c7265616479206265656e20696e697469616c697a6564a265627a7a7231582009fa39e9f02ac35f9f65378491a2ae03805ad2666e2d189ff23c7362be15d21564736f6c634300050b0032";

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
        _addresses.put("4", "0xA645955D38F0eE6F0863fB9E05CdeB612C3c60fD");
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
