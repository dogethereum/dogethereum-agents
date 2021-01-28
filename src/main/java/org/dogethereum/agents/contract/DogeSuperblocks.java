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
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.StaticArray9;
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
 * <p>Generated with web3j version 1.4.0.
 */
@SuppressWarnings("rawtypes")
public class DogeSuperblocks extends Contract {
    public static final String BINARY = "0x608060405234801561001057600080fd5b50612399806100206000396000f3fe608060405234801561001057600080fd5b506004361061025e5760003560e01c806387a4d38211610146578063c11818a1116100c3578063eda1970b11610087578063eda1970b14610afd578063f06d520d14610b05578063f2854e3414610b0d578063f32007e914610b2a578063f6f3238a14610b32578063f9b5d7c014610b735761025e565b8063c11818a1146108ce578063c1f67ab314610a83578063cae0581e14610aaf578063d035c40314610aed578063df22235714610af55761025e565b806397dde2091161010a57806397dde209146105cb578063b1b595281461082a578063b6da214414610881578063ba16d600146108a9578063c0dde98b146108b15761025e565b806387a4d382146105555780638e4d8e991461057257806390b6f6991461058f578063945fd0c51461059757806395b45ee71461059f5761025e565b806348aefc32116101df578063642ed988116101a3578063642ed9881461041b57806367e26419146104385780636ca640a11461047f5780636e5b707114610487578063742057861461050c5780637b34dcd9146105385761025e565b806348aefc32146103b55780634955d085146103e657806355e018ce146103ee5780635b5728121461040b57806361bd8d66146104135761025e565b80633288816a116102265780633288816a146103395780633a47290a146103415780633ce90e8f1461038857806341827da7146103a5578063455e6166146103ad5761025e565b806306ef472114610263578063155ee8941461027d57806327426f75146102a15780632da8cffd146102e65780632e40019114610303575b600080fd5b61026b610be1565b60408051918252519081900360200190f35b610285610be8565b604080516001600160a01b039092168252519081900360200190f35b6102cd600480360360408110156102b757600080fd5b50803590602001356001600160a01b0316610bf7565b6040805192835260208301919091528051918290030190f35b61026b600480360360208110156102fc57600080fd5b5035610d3d565b6103206004803603602081101561031957600080fd5b5035610d55565b6040805163ffffffff9092168252519081900360200190f35b61026b610d77565b6102cd600480360360e081101561035757600080fd5b5080359060208101359060408101359060608101359060808101359063ffffffff60a0820135169060c00135610d7d565b61026b6004803603602081101561039e57600080fd5b5035610f45565b61026b610f5a565b61026b610f61565b6103d2600480360360208110156103cb57600080fd5b5035610f68565b604080519115158252519081900360200190f35b61026b610f87565b61026b6004803603602081101561040457600080fd5b5035610f8d565b61026b610fa2565b61026b610fc7565b61026b6004803603602081101561043157600080fd5b5035610fce565b61026b600480360360e081101561044e57600080fd5b5080359060208101359060408101359060608101359060808101359063ffffffff60a0820135169060c00135610fe0565b61026b611040565b6104a46004803603602081101561049d57600080fd5b5035611047565b604051808a81526020018981526020018881526020018781526020018681526020018563ffffffff168152602001848152602001836001600160a01b031681526020018260058111156104f357fe5b8152602001995050505050505050505060405180910390f35b6102cd6004803603604081101561052257600080fd5b50803590602001356001600160a01b03166110a8565b61026b6004803603602081101561054e57600080fd5b50356111e8565b6103206004803603602081101561056b57600080fd5b50356111fd565b61026b6004803603602081101561058857600080fd5b503561121f565b61026b611234565b61026b61123b565b6102cd600480360360408110156105b557600080fd5b50803590602001356001600160a01b0316611242565b61026b60048036036101208110156105e257600080fd5b810190602081018135600160201b8111156105fc57600080fd5b82018360208201111561060e57600080fd5b803590602001918460018302840111600160201b8311171561062f57600080fd5b91908080601f01602080910402602001604051908101604052809392919081815260200183838082843760009201919091525092956bffffffffffffffffffffffff1985351695602086013595919450925060608101915060400135600160201b81111561069c57600080fd5b8201836020820111156106ae57600080fd5b803590602001918460208302840111600160201b831117156106cf57600080fd5b9190808060200260200160405190810160405280939291908181526020018383602002808284376000920191909152509295949360208101935035915050600160201b81111561071e57600080fd5b82018360208201111561073057600080fd5b803590602001918460018302840111600160201b8311171561075157600080fd5b91908080601f01602080910402602001604051908101604052809392919081815260200183838082843760009201919091525092958435959094909350604081019250602001359050600160201b8111156107ab57600080fd5b8201836020820111156107bd57600080fd5b803590602001918460208302840111600160201b831117156107de57600080fd5b91908080602002602001604051908101604052809392919081815260200183836020028082843760009201919091525092955050823593505050602001356001600160a01b031661140e565b6102cd600480360361010081101561084157600080fd5b5080359060208101359060408101359060608101359060808101359060a081013563ffffffff169060c08101359060e001356001600160a01b031661164f565b6108a76004803603602081101561089757600080fd5b50356001600160a01b031661191f565b005b61026b61196b565b61026b600480360360208110156108c757600080fd5b5035611972565b61026b600480360360a08110156108e457600080fd5b810190602081018135600160201b8111156108fe57600080fd5b82018360208201111561091057600080fd5b803590602001918460018302840111600160201b8311171561093157600080fd5b91908080601f01602080910402602001604051908101604052809392919081815260200183838082843760009201919091525092958435959094909350604081019250602001359050600160201b81111561098b57600080fd5b82018360208201111561099d57600080fd5b803590602001918460208302840111600160201b831117156109be57600080fd5b9190808060200260200160405190810160405280939291908181526020018383602002808284376000920191909152509295949360208101935035915050600160201b811115610a0d57600080fd5b820183602082011115610a1f57600080fd5b803590602001918460018302840111600160201b83111715610a4057600080fd5b91908080601f01602080910402602001604051908101604052809392919081815260200183838082843760009201919091525092955050913592506119df915050565b6102cd60048036036040811015610a9957600080fd5b50803590602001356001600160a01b0316611a56565b610acc60048036036020811015610ac557600080fd5b5035611b96565b60405180826005811115610adc57fe5b815260200191505060405180910390f35b61026b611bb5565b61026b611bbc565b61026b611bc2565b61026b611bc8565b61026b60048036036020811015610b2357600080fd5b5035611bce565b610320611be3565b610b3a611bef565b604051808261012080838360005b83811015610b60578181015183820152602001610b48565b5050505090500191505060405180910390f35b61026b60048036036020811015610b8957600080fd5b810190602081018135600160201b811115610ba357600080fd5b820183602082011115610bb557600080fd5b803590602001918460208302840111600160201b83111715610bd657600080fd5b509092509050611c61565b620157c081565b6005546001600160a01b031681565b60055460009081906001600160a01b03163314610c45576040805185815261c39660208201528151600080516020612324833981519152929181900390910190a15061c39690506000610d36565b600084815260208190526040902060026008820154600160601b900460ff166005811115610c6f57fe5b14158015610c97575060016008820154600160601b900460ff166005811115610c9457fe5b14155b15610cd5576040805186815261c36460208201528151600080516020612324833981519152929181900390910190a15061c364915060009050610d36565b60088101805460ff60601b1916600360601b179055604080518681526001600160a01b038616602082015281517f87f54f5eb3dd119fe71af0915af693e64a5bfd4acaa19a6c944c47cff8eec9e6929181900390910190a160008592509250505b9250929050565b6000818152602081905260409020600201545b919050565b600090815260208190526040902060080154600160401b900463ffffffff1690565b60035481565b600354600090819015610d8f57600080fd5b8215610d9a57600080fd5b6000610dab8a8a8a8a8a8a8a610fe0565b60008181526020819052604081209192506008820154600160601b900460ff166005811115610dd657fe5b14610de057600080fd5b6002805463ffffffff90811660009081526001602081905260409091208590558d845583018c90558282018b9055600383018a905560048084018a905560058401889055600684018054336001600160a01b03199091161790559154600884018054600160401b67ffffffff0000000019909116928416600160201b029290921763ffffffff60401b19169190911763ffffffff19169189169190911780825560ff60601b1916600160601b830217905550600060078201556002805463ffffffff8082166001011663ffffffff199091161790556040805183815233602082015281517f64951c9008bba9f4663c12662e7a9b6412a7c4757869fdac09285564ae923fa1929181900390910190a1600382905560048a90556040805183815233602082015281517ff2dbbf0abb1ab1870a5e4d02746747c91d167c855255440b573ba3b5529dc901929181900390910190a15060009a909950975050505050505050565b60009081526020819052604090206004015490565b62018e7081565b62069f5081565b60006004610f7583611b96565b6005811115610f8057fe5b1492915050565b619c4081565b60009081526020819052604090206005015490565b600354600090815260208190526040902060080154600160401b900463ffffffff1690565b6206b6c081565b60009081526020819052604090205490565b60408051602080820199909952808201979097526060870195909552608086019390935260a085019190915260e01b6001600160e01b03191660c084015260c4808401919091528151808403909101815260e49092019052805191012090565b620cd14081565b600090815260208190526040902080546001820154600283015460038401546004850154600886015460058701546006909701549597949693959294919363ffffffff82169390926001600160a01b0390911691600160601b900460ff1690565b60055460009081906001600160a01b031633146110f6576040805185815261c39660208201528151600080516020612324833981519152929181900390910190a15061c39690506000610d36565b600084815260208190526040902060026008820154600160601b900460ff16600581111561112057fe5b14158015611148575060036008820154600160601b900460ff16600581111561114557fe5b14155b15611186576040805186815261c36460208201528151600080516020612324833981519152929181900390910190a15061c364915060009050610d36565b60088101805460ff60601b1916600560601b179055604080518681526001600160a01b038616602082015281517f64297372062dfcb21d6f7385f68d4656e993be2bb674099e3de73128d4911a91929181900390910190a15060009492505050565b60009081526020819052604090206007015490565b600090815260208190526040902060080154600160201b900463ffffffff1690565b60009081526020819052604090206003015490565b6205c49081565b6201388081565b60055460009081906001600160a01b03163314611290576040805185815261c39660208201528151600080516020612324833981519152929181900390910190a15061c39690506000610d36565b600084815260208190526040902060016008820154600160601b900460ff1660058111156112ba57fe5b141580156112e2575060036008820154600160601b900460ff1660058111156112df57fe5b14155b15611320576040805186815261c36460208201528151600080516020612324833981519152929181900390910190a15061c364915060009050610d36565b6005810154600090815260208190526040902060046008820154600160601b900460ff16600581111561134f57fe5b1461138f576040805187815261c38260208201528151600080516020612324833981519152929181900390910190a15061c382925060009150610d369050565b60088201805460ff60601b1916600160621b179055600454600183015411156113c057600386905560018201546004555b604080518781526001600160a01b038716602082015281517ff2dbbf0abb1ab1870a5e4d02746747c91d167c855255440b573ba3b5529dc901929181900390910190a1506000959350505050565b60008061141a87611d13565b905061142584610fce565b611430828888611eb2565b146114705760008051602061234483398151915261144d8c611d13565b60408051918252614e4860208301528051918290030190a1614e48915050611642565b600061147f8c8b8b8b896119df565b905080156115fd576000846001600160a01b031663c3d5e1868e848f6000808c815260200190815260200160002060060160009054906101000a90046001600160a01b03166040518563ffffffff1660e01b81526004018080602001858152602001846bffffffffffffffffffffffff19168152602001836001600160a01b03168152602001828103825286818151815260200191508051906020019080838360005b8381101561153a578181015183820152602001611522565b50505050905090810190601f1680156115675780820380516001836020036101000a031916815260200191505b5095505050505050602060405180830381600087803b15801561158957600080fd5b505af115801561159d573d6000803e3d6000fd5b505050506040513d60208110156115b357600080fd5b5051604080518481526020810183905281519293507f4e64138cc499eb1adf9edff9ef69bd45c56ac4bfd307540952e4c9d51eab55c1929081900390910190a19250611642915050565b604080516000815261753a602082015281517f4e64138cc499eb1adf9edff9ef69bd45c56ac4bfd307540952e4c9d51eab55c1929181900390910190a161753a925050505b9998505050505050505050565b60055460009081906001600160a01b0316331461169e57604080516000815261c39660208201528151600080516020612324833981519152929181900390910190a15061c39690506000611912565b60006116af8b8b8b8b8b8b8b610fe0565b600086815260208190526040902090915060036008820154600160601b900460ff1660058111156116dc57fe5b14158015611704575060046008820154600160601b900460ff16600581111561170157fe5b14155b15611744576040805183815261c38260208201528151600080516020612324833981519152929181900390910190a15061c3829250600091506119129050565b6000828152602081905260408120906008820154600160601b900460ff16600581111561176d57fe5b146117ae576040805184815261c35a60208201528151600080516020612324833981519152929181900390910190a15061c35a935060009250611912915050565b6002805463ffffffff90811660009081526001602081905260409091208690558f84558381018f90558383018e9055600384018d9055600484018c9055600584018a90556006840180546001600160a01b0319166001600160a01b038b1617905591546008808501805467ffffffff000000001916928416600160201b02929092178083559086015463ffffffff60401b19909116600160401b918290048416850184169091021763ffffffff1916918b169190911780825560ff60601b1916600160601b830217905550600782015460088301546118a4919063ffffffff600160201b8204811691600160401b900416611f32565b60078201556002805463ffffffff8082166001011663ffffffff19909116179055604080518481526001600160a01b038816602082015281517f64951c9008bba9f4663c12662e7a9b6412a7c4757869fdac09285564ae923fa1929181900390910190a15060009350909150505b9850989650505050505050565b6005546001600160a01b031615801561194057506001600160a01b03811615155b61194957600080fd5b600580546001600160a01b0319166001600160a01b0392909216919091179055565b62061a8081565b60035460009060075b8361198583610d55565b63ffffffff1611156119d8575b61199b81611f8d565b846119a584610d55565b63ffffffff16031080156119b95750600081115b156119c75760001901611992565b6119d18282611f93565b915061197b565b5092915050565b6000806119eb87611d13565b9050865160401415611a2b5760408051828152614e5c60208201528151600080516020612344833981519152929181900390910190a16000915050611a4d565b611a388187878787612038565b60011415611a47579050611a4d565b60009150505b95945050505050565b60055460009081906001600160a01b03163314611aa4576040805185815261c39660208201528151600080516020612324833981519152929181900390910190a15061c39690506000610d36565b600084815260208190526040902060016008820154600160601b900460ff166005811115611ace57fe5b14158015611af6575060026008820154600160601b900460ff166005811115611af357fe5b14155b15611b34576040805186815261c36460208201528151600080516020612324833981519152929181900390910190a15061c364915060009050610d36565b60088101805460ff60601b1916600160611b179055604080518681526001600160a01b038616602082015281517f09cdaca254aa177f759fe7a0968fe696ee9baf7d2a1d4714ed24b83d1f09518e929181900390910190a15060009492505050565b600090815260208190526040902060080154600160601b900460ff1690565b62035b6081565b60045481565b6184d081565b60035490565b60009081526020819052604090206001015490565b60025463ffffffff1690565b611bf7612304565b611bff612304565b600354808252600090611c11906111e8565b905060085b8015611c595763ffffffff8216600090815260016020526040902054838260098110611c3e57fe5b602090810291909101919091529190911c9060001901611c16565b509091505090565b600073__DogeMessageLibrary____________________63f9b5d7c084846040518363ffffffff1660e01b815260040180806020018281038252848482818152602001925060200280828437600083820152604051601f909101601f191690920195506020945090925050508083038186803b158015611ce057600080fd5b505af4158015611cf4573d6000803e3d6000fd5b505050506040513d6020811015611d0a57600080fd5b50519392505050565b6000611eac600280846040516020018082805190602001908083835b60208310611d4e5780518252601f199092019160209182019101611d2f565b6001836020036101000a0380198251168184511680821785525050505050509050019150506040516020818303038152906040526040518082805190602001908083835b60208310611db15780518252601f199092019160209182019101611d92565b51815160209384036101000a60001901801990921691161790526040519190930194509192505080830381855afa158015611df0573d6000803e3d6000fd5b5050506040513d6020811015611e0557600080fd5b50516040805160208181019390935281518082038401815290820191829052805190928291908401908083835b60208310611e515780518252601f199092019160209182019101611e32565b51815160209384036101000a60001901801990921691161790526040519190930194509192505080830381855afa158015611e90573d6000803e3d6000fd5b5050506040513d6020811015611ea557600080fd5b50516121f3565b92915050565b600083815b8351811015611f29576000848281518110611ece57fe5b60200260200101519050600060028781611ee457fe5b0690506000808260011415611efd575082905084611f08565b82611f085750849050825b611f12828261221e565b955060028904985060018501945050505050611eb7565b50949350505050565b60006005611f41858386612293565b945060015b600881108015611f5f5750818481611f5a57fe5b066001145b15611f8357611f72868260040287612293565b955060059190910290600101611f46565b5093949350505050565b60050a90565b600082815260208181526040822060070154908290829060036004870201908110611fba57fe5b1a826002600487020160208110611fcd57fe5b1a61010002836004870260010160208110611fe457fe5b1a6201000002846004880260208110611ff957fe5b1a60f81b60f81c60ff166301000000020101019050600160008263ffffffff1663ffffffff168152602001908152602001600020549250505092915050565b600061204382610f68565b15806120555750612053826122cf565b155b1561208d5760408051878152614e3e60208201528151600080516020612344833981519152929181900390910190a150614e3e611a4d565b600073__DogeMessageLibrary____________________63d2db98728560006040518363ffffffff1660e01b81526004018080602001838152602001828103825284818151815260200191508051906020019080838360005b838110156120fe5781810151838201526020016120e6565b50505050905090810190601f16801561212b5780820380516001836020036101000a031916815260200191505b50935050505060206040518083038186803b15801561214957600080fd5b505af415801561215d573d6000803e3d6000fd5b505050506040513d602081101561217357600080fd5b5051905080612183888888611eb2565b146121bd5760408051888152614e5260208201528151600080516020612344833981519152929181900390910190a1614e52915050611a4d565b60408051888152600160208201528151600080516020612344833981519152929181900390910190a15060019695505050505050565b600060405160005b6020811015612216578381601f031a818301536001016121fb565b505192915050565b600061228c60028061222f866121f3565b612238866121f3565b604051602001808381526020018281526020019250505060405160208183030381529060405260405180828051906020019080838360208310611db15780518252601f199092019160209182019101611d92565b9392505050565b60008060405185815283601c1a8582015383601d1a6001860182015383601e1a6002860182015383601f1a600386018201535195945050505050565b6000806122db83610d55565b63ffffffff169050806122f2576000915050610d50565b826122fc82611972565b149392505050565b604051806101200160405280600990602082028036833750919291505056fea57c1ba4cf2c89b3558cfeeca4339e04551f0fc1a12cf63f1923c2eed8a5be8b65bd72698b9ffcfb3c7cb4c7414e13225cabd57fb690e183ae8c01c8ec268ebda2646970667358221220c7cedad72d96c055c0f2ea6317afc440f33492ff9a8b724933cc1b4613f229af64736f6c63430007060033";

    public static final String FUNC_BESTSUPERBLOCK = "bestSuperblock";

    public static final String FUNC_BESTSUPERBLOCKACCUMULATEDWORK = "bestSuperblockAccumulatedWork";

    public static final String FUNC_CHALLENGECOST = "challengeCost";

    public static final String FUNC_MINCHALLENGEDEPOSIT = "minChallengeDeposit";

    public static final String FUNC_MINPROPOSALDEPOSIT = "minProposalDeposit";

    public static final String FUNC_MINREWARD = "minReward";

    public static final String FUNC_QUERYBLOCKHEADERCOST = "queryBlockHeaderCost";

    public static final String FUNC_QUERYMERKLEROOTHASHESCOST = "queryMerkleRootHashesCost";

    public static final String FUNC_REQUESTSCRYPTCOST = "requestScryptCost";

    public static final String FUNC_RESPONDBLOCKHEADERCOST = "respondBlockHeaderCost";

    public static final String FUNC_RESPONDMERKLEROOTHASHESCOST = "respondMerkleRootHashesCost";

    public static final String FUNC_SUPERBLOCKCOST = "superblockCost";

    public static final String FUNC_TRUSTEDCLAIMMANAGER = "trustedClaimManager";

    public static final String FUNC_VERIFYSUPERBLOCKCOST = "verifySuperblockCost";

    public static final String FUNC_SETCLAIMMANAGER = "setClaimManager";

    public static final String FUNC_INITIALIZE = "initialize";

    public static final String FUNC_PROPOSE = "propose";

    public static final String FUNC_CONFIRM = "confirm";

    public static final String FUNC_CHALLENGE = "challenge";

    public static final String FUNC_SEMIAPPROVE = "semiApprove";

    public static final String FUNC_INVALIDATE = "invalidate";

    public static final String FUNC_RELAYTX = "relayTx";

    public static final String FUNC_VERIFYTX = "verifyTx";

    public static final String FUNC_CALCSUPERBLOCKHASH = "calcSuperblockHash";

    public static final String FUNC_GETBESTSUPERBLOCK = "getBestSuperblock";

    public static final String FUNC_GETSUPERBLOCK = "getSuperblock";

    public static final String FUNC_GETSUPERBLOCKHEIGHT = "getSuperblockHeight";

    public static final String FUNC_GETSUPERBLOCKINDEX = "getSuperblockIndex";

    public static final String FUNC_GETSUPERBLOCKANCESTORS = "getSuperblockAncestors";

    public static final String FUNC_GETSUPERBLOCKMERKLEROOT = "getSuperblockMerkleRoot";

    public static final String FUNC_GETSUPERBLOCKTIMESTAMP = "getSuperblockTimestamp";

    public static final String FUNC_GETSUPERBLOCKPREVTIMESTAMP = "getSuperblockPrevTimestamp";

    public static final String FUNC_GETSUPERBLOCKLASTHASH = "getSuperblockLastHash";

    public static final String FUNC_GETSUPERBLOCKPARENTID = "getSuperblockParentId";

    public static final String FUNC_GETSUPERBLOCKACCUMULATEDWORK = "getSuperblockAccumulatedWork";

    public static final String FUNC_GETSUPERBLOCKSTATUS = "getSuperblockStatus";

    public static final String FUNC_GETINDEXNEXTSUPERBLOCK = "getIndexNextSuperblock";

    public static final String FUNC_MAKEMERKLE = "makeMerkle";

    public static final String FUNC_ISAPPROVED = "isApproved";

    public static final String FUNC_GETCHAINHEIGHT = "getChainHeight";

    public static final String FUNC_GETSUPERBLOCKLOCATOR = "getSuperblockLocator";

    public static final String FUNC_GETSUPERBLOCKAT = "getSuperblockAt";

    public static final Event APPROVEDSUPERBLOCK_EVENT = new Event("ApprovedSuperblock", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}));
    ;

    public static final Event CHALLENGESUPERBLOCK_EVENT = new Event("ChallengeSuperblock", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}));
    ;

    public static final Event ERRORSUPERBLOCK_EVENT = new Event("ErrorSuperblock", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event INVALIDSUPERBLOCK_EVENT = new Event("InvalidSuperblock", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}));
    ;

    public static final Event NEWSUPERBLOCK_EVENT = new Event("NewSuperblock", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}));
    ;

    public static final Event RELAYTRANSACTION_EVENT = new Event("RelayTransaction", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event SEMIAPPROVEDSUPERBLOCK_EVENT = new Event("SemiApprovedSuperblock", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}));
    ;

    public static final Event VERIFYTRANSACTION_EVENT = new Event("VerifyTransaction", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Uint256>() {}));
    ;

    protected static final HashMap<String, String> _addresses;

    static {
        _addresses = new HashMap<String, String>();
        _addresses.put("1611781691726", "0x701100f19326cc304e3673ba05a4757a031116FA");
        _addresses.put("1611783520013", "0x180CE539B011b2EF08D11d0e6a3148a3C70b91DE");
    }

    @Deprecated
    protected DogeSuperblocks(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected DogeSuperblocks(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected DogeSuperblocks(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected DogeSuperblocks(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public List<ApprovedSuperblockEventResponse> getApprovedSuperblockEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(APPROVEDSUPERBLOCK_EVENT, transactionReceipt);
        ArrayList<ApprovedSuperblockEventResponse> responses = new ArrayList<ApprovedSuperblockEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ApprovedSuperblockEventResponse typedResponse = new ApprovedSuperblockEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.superblockHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.who = (String) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<ApprovedSuperblockEventResponse> approvedSuperblockEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, ApprovedSuperblockEventResponse>() {
            @Override
            public ApprovedSuperblockEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(APPROVEDSUPERBLOCK_EVENT, log);
                ApprovedSuperblockEventResponse typedResponse = new ApprovedSuperblockEventResponse();
                typedResponse.log = log;
                typedResponse.superblockHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.who = (String) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<ApprovedSuperblockEventResponse> approvedSuperblockEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(APPROVEDSUPERBLOCK_EVENT));
        return approvedSuperblockEventFlowable(filter);
    }

    public List<ChallengeSuperblockEventResponse> getChallengeSuperblockEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(CHALLENGESUPERBLOCK_EVENT, transactionReceipt);
        ArrayList<ChallengeSuperblockEventResponse> responses = new ArrayList<ChallengeSuperblockEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ChallengeSuperblockEventResponse typedResponse = new ChallengeSuperblockEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.superblockHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.who = (String) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<ChallengeSuperblockEventResponse> challengeSuperblockEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, ChallengeSuperblockEventResponse>() {
            @Override
            public ChallengeSuperblockEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(CHALLENGESUPERBLOCK_EVENT, log);
                ChallengeSuperblockEventResponse typedResponse = new ChallengeSuperblockEventResponse();
                typedResponse.log = log;
                typedResponse.superblockHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.who = (String) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<ChallengeSuperblockEventResponse> challengeSuperblockEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(CHALLENGESUPERBLOCK_EVENT));
        return challengeSuperblockEventFlowable(filter);
    }

    public List<ErrorSuperblockEventResponse> getErrorSuperblockEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(ERRORSUPERBLOCK_EVENT, transactionReceipt);
        ArrayList<ErrorSuperblockEventResponse> responses = new ArrayList<ErrorSuperblockEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ErrorSuperblockEventResponse typedResponse = new ErrorSuperblockEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.superblockHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.err = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<ErrorSuperblockEventResponse> errorSuperblockEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, ErrorSuperblockEventResponse>() {
            @Override
            public ErrorSuperblockEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(ERRORSUPERBLOCK_EVENT, log);
                ErrorSuperblockEventResponse typedResponse = new ErrorSuperblockEventResponse();
                typedResponse.log = log;
                typedResponse.superblockHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.err = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<ErrorSuperblockEventResponse> errorSuperblockEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(ERRORSUPERBLOCK_EVENT));
        return errorSuperblockEventFlowable(filter);
    }

    public List<InvalidSuperblockEventResponse> getInvalidSuperblockEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(INVALIDSUPERBLOCK_EVENT, transactionReceipt);
        ArrayList<InvalidSuperblockEventResponse> responses = new ArrayList<InvalidSuperblockEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            InvalidSuperblockEventResponse typedResponse = new InvalidSuperblockEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.superblockHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.who = (String) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<InvalidSuperblockEventResponse> invalidSuperblockEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, InvalidSuperblockEventResponse>() {
            @Override
            public InvalidSuperblockEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(INVALIDSUPERBLOCK_EVENT, log);
                InvalidSuperblockEventResponse typedResponse = new InvalidSuperblockEventResponse();
                typedResponse.log = log;
                typedResponse.superblockHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.who = (String) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<InvalidSuperblockEventResponse> invalidSuperblockEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(INVALIDSUPERBLOCK_EVENT));
        return invalidSuperblockEventFlowable(filter);
    }

    public List<NewSuperblockEventResponse> getNewSuperblockEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(NEWSUPERBLOCK_EVENT, transactionReceipt);
        ArrayList<NewSuperblockEventResponse> responses = new ArrayList<NewSuperblockEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            NewSuperblockEventResponse typedResponse = new NewSuperblockEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.superblockHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.who = (String) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<NewSuperblockEventResponse> newSuperblockEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, NewSuperblockEventResponse>() {
            @Override
            public NewSuperblockEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(NEWSUPERBLOCK_EVENT, log);
                NewSuperblockEventResponse typedResponse = new NewSuperblockEventResponse();
                typedResponse.log = log;
                typedResponse.superblockHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.who = (String) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<NewSuperblockEventResponse> newSuperblockEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(NEWSUPERBLOCK_EVENT));
        return newSuperblockEventFlowable(filter);
    }

    public List<RelayTransactionEventResponse> getRelayTransactionEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(RELAYTRANSACTION_EVENT, transactionReceipt);
        ArrayList<RelayTransactionEventResponse> responses = new ArrayList<RelayTransactionEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            RelayTransactionEventResponse typedResponse = new RelayTransactionEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.txHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.returnCode = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<RelayTransactionEventResponse> relayTransactionEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, RelayTransactionEventResponse>() {
            @Override
            public RelayTransactionEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(RELAYTRANSACTION_EVENT, log);
                RelayTransactionEventResponse typedResponse = new RelayTransactionEventResponse();
                typedResponse.log = log;
                typedResponse.txHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.returnCode = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<RelayTransactionEventResponse> relayTransactionEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(RELAYTRANSACTION_EVENT));
        return relayTransactionEventFlowable(filter);
    }

    public List<SemiApprovedSuperblockEventResponse> getSemiApprovedSuperblockEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(SEMIAPPROVEDSUPERBLOCK_EVENT, transactionReceipt);
        ArrayList<SemiApprovedSuperblockEventResponse> responses = new ArrayList<SemiApprovedSuperblockEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            SemiApprovedSuperblockEventResponse typedResponse = new SemiApprovedSuperblockEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.superblockHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.who = (String) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<SemiApprovedSuperblockEventResponse> semiApprovedSuperblockEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, SemiApprovedSuperblockEventResponse>() {
            @Override
            public SemiApprovedSuperblockEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(SEMIAPPROVEDSUPERBLOCK_EVENT, log);
                SemiApprovedSuperblockEventResponse typedResponse = new SemiApprovedSuperblockEventResponse();
                typedResponse.log = log;
                typedResponse.superblockHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.who = (String) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<SemiApprovedSuperblockEventResponse> semiApprovedSuperblockEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(SEMIAPPROVEDSUPERBLOCK_EVENT));
        return semiApprovedSuperblockEventFlowable(filter);
    }

    public List<VerifyTransactionEventResponse> getVerifyTransactionEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(VERIFYTRANSACTION_EVENT, transactionReceipt);
        ArrayList<VerifyTransactionEventResponse> responses = new ArrayList<VerifyTransactionEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            VerifyTransactionEventResponse typedResponse = new VerifyTransactionEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.txHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.returnCode = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<VerifyTransactionEventResponse> verifyTransactionEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, VerifyTransactionEventResponse>() {
            @Override
            public VerifyTransactionEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(VERIFYTRANSACTION_EVENT, log);
                VerifyTransactionEventResponse typedResponse = new VerifyTransactionEventResponse();
                typedResponse.log = log;
                typedResponse.txHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.returnCode = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<VerifyTransactionEventResponse> verifyTransactionEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(VERIFYTRANSACTION_EVENT));
        return verifyTransactionEventFlowable(filter);
    }

    public RemoteFunctionCall<byte[]> bestSuperblock() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_BESTSUPERBLOCK, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteFunctionCall<BigInteger> bestSuperblockAccumulatedWork() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_BESTSUPERBLOCKACCUMULATEDWORK, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> challengeCost() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_CHALLENGECOST, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> minChallengeDeposit() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_MINCHALLENGEDEPOSIT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> minProposalDeposit() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_MINPROPOSALDEPOSIT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> minReward() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_MINREWARD, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> queryBlockHeaderCost() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_QUERYBLOCKHEADERCOST, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> queryMerkleRootHashesCost() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_QUERYMERKLEROOTHASHESCOST, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> requestScryptCost() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_REQUESTSCRYPTCOST, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> respondBlockHeaderCost() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_RESPONDBLOCKHEADERCOST, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> respondMerkleRootHashesCost() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_RESPONDMERKLEROOTHASHESCOST, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> superblockCost() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_SUPERBLOCKCOST, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<String> trustedClaimManager() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_TRUSTEDCLAIMMANAGER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<BigInteger> verifySuperblockCost() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_VERIFYSUPERBLOCKCOST, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> setClaimManager(String _claimManager) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_SETCLAIMMANAGER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_claimManager)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> initialize(byte[] _blocksMerkleRoot, BigInteger _accumulatedWork, BigInteger _timestamp, BigInteger _prevTimestamp, byte[] _lastHash, BigInteger _lastBits, byte[] _parentId) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_INITIALIZE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_blocksMerkleRoot), 
                new org.web3j.abi.datatypes.generated.Uint256(_accumulatedWork), 
                new org.web3j.abi.datatypes.generated.Uint256(_timestamp), 
                new org.web3j.abi.datatypes.generated.Uint256(_prevTimestamp), 
                new org.web3j.abi.datatypes.generated.Bytes32(_lastHash), 
                new org.web3j.abi.datatypes.generated.Uint32(_lastBits), 
                new org.web3j.abi.datatypes.generated.Bytes32(_parentId)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> propose(byte[] _blocksMerkleRoot, BigInteger _accumulatedWork, BigInteger _timestamp, BigInteger _prevTimestamp, byte[] _lastHash, BigInteger _lastBits, byte[] _parentId, String submitter) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_PROPOSE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_blocksMerkleRoot), 
                new org.web3j.abi.datatypes.generated.Uint256(_accumulatedWork), 
                new org.web3j.abi.datatypes.generated.Uint256(_timestamp), 
                new org.web3j.abi.datatypes.generated.Uint256(_prevTimestamp), 
                new org.web3j.abi.datatypes.generated.Bytes32(_lastHash), 
                new org.web3j.abi.datatypes.generated.Uint32(_lastBits), 
                new org.web3j.abi.datatypes.generated.Bytes32(_parentId), 
                new org.web3j.abi.datatypes.Address(submitter)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> confirm(byte[] _superblockHash, String _validator) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_CONFIRM, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_superblockHash), 
                new org.web3j.abi.datatypes.Address(_validator)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> challenge(byte[] _superblockHash, String _challenger) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_CHALLENGE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_superblockHash), 
                new org.web3j.abi.datatypes.Address(_challenger)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> semiApprove(byte[] _superblockHash, String _validator) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_SEMIAPPROVE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_superblockHash), 
                new org.web3j.abi.datatypes.Address(_validator)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> invalidate(byte[] _superblockHash, String _validator) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_INVALIDATE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_superblockHash), 
                new org.web3j.abi.datatypes.Address(_validator)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> relayTx(byte[] _txBytes, byte[] _operatorPublicKeyHash, BigInteger _txIndex, List<BigInteger> _txSiblings, byte[] _dogeBlockHeader, BigInteger _dogeBlockIndex, List<BigInteger> _dogeBlockSiblings, byte[] _superblockHash, String _untrustedTargetContract) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_RELAYTX, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicBytes(_txBytes), 
                new org.web3j.abi.datatypes.generated.Bytes20(_operatorPublicKeyHash), 
                new org.web3j.abi.datatypes.generated.Uint256(_txIndex), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint256>(
                        org.web3j.abi.datatypes.generated.Uint256.class,
                        org.web3j.abi.Utils.typeMap(_txSiblings, org.web3j.abi.datatypes.generated.Uint256.class)), 
                new org.web3j.abi.datatypes.DynamicBytes(_dogeBlockHeader), 
                new org.web3j.abi.datatypes.generated.Uint256(_dogeBlockIndex), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint256>(
                        org.web3j.abi.datatypes.generated.Uint256.class,
                        org.web3j.abi.Utils.typeMap(_dogeBlockSiblings, org.web3j.abi.datatypes.generated.Uint256.class)), 
                new org.web3j.abi.datatypes.generated.Bytes32(_superblockHash), 
                new org.web3j.abi.datatypes.Address(_untrustedTargetContract)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> verifyTx(byte[] _txBytes, BigInteger _txIndex, List<BigInteger> _siblings, byte[] _txBlockHeaderBytes, byte[] _txsuperblockHash) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_VERIFYTX, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicBytes(_txBytes), 
                new org.web3j.abi.datatypes.generated.Uint256(_txIndex), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint256>(
                        org.web3j.abi.datatypes.generated.Uint256.class,
                        org.web3j.abi.Utils.typeMap(_siblings, org.web3j.abi.datatypes.generated.Uint256.class)), 
                new org.web3j.abi.datatypes.DynamicBytes(_txBlockHeaderBytes), 
                new org.web3j.abi.datatypes.generated.Bytes32(_txsuperblockHash)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<byte[]> calcSuperblockHash(byte[] _blocksMerkleRoot, BigInteger _accumulatedWork, BigInteger _timestamp, BigInteger _prevTimestamp, byte[] _lastHash, BigInteger _lastBits, byte[] _parentId) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_CALCSUPERBLOCKHASH, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_blocksMerkleRoot), 
                new org.web3j.abi.datatypes.generated.Uint256(_accumulatedWork), 
                new org.web3j.abi.datatypes.generated.Uint256(_timestamp), 
                new org.web3j.abi.datatypes.generated.Uint256(_prevTimestamp), 
                new org.web3j.abi.datatypes.generated.Bytes32(_lastHash), 
                new org.web3j.abi.datatypes.generated.Uint32(_lastBits), 
                new org.web3j.abi.datatypes.generated.Bytes32(_parentId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteFunctionCall<byte[]> getBestSuperblock() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETBESTSUPERBLOCK, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteFunctionCall<Tuple9<byte[], BigInteger, BigInteger, BigInteger, byte[], BigInteger, byte[], String, BigInteger>> getSuperblock(byte[] superblockHash) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETSUPERBLOCK, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(superblockHash)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Uint32>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}, new TypeReference<Uint8>() {}));
        return new RemoteFunctionCall<Tuple9<byte[], BigInteger, BigInteger, BigInteger, byte[], BigInteger, byte[], String, BigInteger>>(function,
                new Callable<Tuple9<byte[], BigInteger, BigInteger, BigInteger, byte[], BigInteger, byte[], String, BigInteger>>() {
                    @Override
                    public Tuple9<byte[], BigInteger, BigInteger, BigInteger, byte[], BigInteger, byte[], String, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple9<byte[], BigInteger, BigInteger, BigInteger, byte[], BigInteger, byte[], String, BigInteger>(
                                (byte[]) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue(), 
                                (BigInteger) results.get(3).getValue(), 
                                (byte[]) results.get(4).getValue(), 
                                (BigInteger) results.get(5).getValue(), 
                                (byte[]) results.get(6).getValue(), 
                                (String) results.get(7).getValue(), 
                                (BigInteger) results.get(8).getValue());
                    }
                });
    }

    public RemoteFunctionCall<BigInteger> getSuperblockHeight(byte[] superblockHash) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETSUPERBLOCKHEIGHT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(superblockHash)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint32>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> getSuperblockIndex(byte[] superblockHash) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETSUPERBLOCKINDEX, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(superblockHash)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint32>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<byte[]> getSuperblockAncestors(byte[] superblockHash) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETSUPERBLOCKANCESTORS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(superblockHash)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteFunctionCall<byte[]> getSuperblockMerkleRoot(byte[] _superblockHash) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETSUPERBLOCKMERKLEROOT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_superblockHash)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteFunctionCall<BigInteger> getSuperblockTimestamp(byte[] _superblockHash) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETSUPERBLOCKTIMESTAMP, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_superblockHash)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> getSuperblockPrevTimestamp(byte[] _superblockHash) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETSUPERBLOCKPREVTIMESTAMP, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_superblockHash)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<byte[]> getSuperblockLastHash(byte[] _superblockHash) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETSUPERBLOCKLASTHASH, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_superblockHash)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteFunctionCall<byte[]> getSuperblockParentId(byte[] _superblockHash) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETSUPERBLOCKPARENTID, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_superblockHash)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteFunctionCall<BigInteger> getSuperblockAccumulatedWork(byte[] _superblockHash) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETSUPERBLOCKACCUMULATEDWORK, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_superblockHash)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> getSuperblockStatus(byte[] _superblockHash) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETSUPERBLOCKSTATUS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_superblockHash)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> getIndexNextSuperblock() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETINDEXNEXTSUPERBLOCK, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint32>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<byte[]> makeMerkle(List<byte[]> hashes) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_MAKEMERKLE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Bytes32>(
                        org.web3j.abi.datatypes.generated.Bytes32.class,
                        org.web3j.abi.Utils.typeMap(hashes, org.web3j.abi.datatypes.generated.Bytes32.class))), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteFunctionCall<Boolean> isApproved(byte[] _superblockHash) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_ISAPPROVED, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_superblockHash)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<BigInteger> getChainHeight() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETCHAINHEIGHT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<List> getSuperblockLocator() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETSUPERBLOCKLOCATOR, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<StaticArray9<Bytes32>>() {}));
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

    public RemoteFunctionCall<byte[]> getSuperblockAt(BigInteger _height) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETSUPERBLOCKAT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_height)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    @Deprecated
    public static DogeSuperblocks load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new DogeSuperblocks(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static DogeSuperblocks load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new DogeSuperblocks(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static DogeSuperblocks load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new DogeSuperblocks(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static DogeSuperblocks load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new DogeSuperblocks(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<DogeSuperblocks> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(DogeSuperblocks.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    public static RemoteCall<DogeSuperblocks> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(DogeSuperblocks.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<DogeSuperblocks> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(DogeSuperblocks.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<DogeSuperblocks> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(DogeSuperblocks.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    protected String getStaticDeployedAddress(String networkId) {
        return _addresses.get(networkId);
    }

    public static String getPreviouslyDeployedAddress(String networkId) {
        return _addresses.get(networkId);
    }

    public static class ApprovedSuperblockEventResponse extends BaseEventResponse {
        public byte[] superblockHash;

        public String who;
    }

    public static class ChallengeSuperblockEventResponse extends BaseEventResponse {
        public byte[] superblockHash;

        public String who;
    }

    public static class ErrorSuperblockEventResponse extends BaseEventResponse {
        public byte[] superblockHash;

        public BigInteger err;
    }

    public static class InvalidSuperblockEventResponse extends BaseEventResponse {
        public byte[] superblockHash;

        public String who;
    }

    public static class NewSuperblockEventResponse extends BaseEventResponse {
        public byte[] superblockHash;

        public String who;
    }

    public static class RelayTransactionEventResponse extends BaseEventResponse {
        public byte[] txHash;

        public BigInteger returnCode;
    }

    public static class SemiApprovedSuperblockEventResponse extends BaseEventResponse {
        public byte[] superblockHash;

        public String who;
    }

    public static class VerifyTransactionEventResponse extends BaseEventResponse {
        public byte[] txHash;

        public BigInteger returnCode;
    }
}
