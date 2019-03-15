package org.dogethereum.agents.contract;

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
import org.web3j.abi.datatypes.Function;
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
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple9;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import rx.Observable;
import rx.functions.Func1;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 3.3.1.
 */
public class DogeSuperblocks extends Contract {
    private static final String BINARY = "0x608060405234801561001057600080fd5b506122e8806100206000396000f3006080604052600436106101ea5763ffffffff60e060020a60003504166306ef472181146101ef578063155ee8941461021657806327426f75146102475780632da8cffd146102845780632e4001911461029c5780633288816a146102cd5780633a47290a146102e25780633ce90e8f1461031257806341827da71461032a578063455e61661461033f57806348aefc32146103545780634955d0851461038057806355e018ce146103955780635b572812146103ad57806361bd8d66146103c2578063642ed988146103d757806367e26419146103ef5780636ca640a11461041f5780636e5b70711461043457806374205786146104bd5780637b34dcd9146104e157806387a4d382146104f95780638e4d8e991461051157806390b6f69914610529578063945fd0c51461053e57806395b45ee71461055357806397dde20914610577578063b1b59528146106b6578063b6da2144146106f2578063ba16d60014610715578063c0dde98b1461072a578063c11818a114610742578063c1f67ab31461081b578063cae0581e1461083f578063d035c4031461087b578063df22235714610890578063eda1970b146108a5578063f06d520d146108ba578063f2854e34146108cf578063f32007e9146108e7578063f6f3238a146108fc578063f9b5d7c01461094a575b600080fd5b3480156101fb57600080fd5b5061020461099f565b60408051918252519081900360200190f35b34801561022257600080fd5b5061022b6109a6565b60408051600160a060020a039092168252519081900360200190f35b34801561025357600080fd5b5061026b600435600160a060020a03602435166109b5565b6040805192835260208301919091528051918290030190f35b34801561029057600080fd5b50610204600435610b10565b3480156102a857600080fd5b506102b4600435610b25565b6040805163ffffffff9092168252519081900360200190f35b3480156102d957600080fd5b50610204610b4c565b3480156102ee57600080fd5b5061026b60043560243560443560643560843563ffffffff60a4351660c435610b52565b34801561031e57600080fd5b50610204600435610d40565b34801561033657600080fd5b50610204610d55565b34801561034b57600080fd5b50610204610d5c565b34801561036057600080fd5b5061036c600435610d63565b604080519115158252519081900360200190f35b34801561038c57600080fd5b50610204610d82565b3480156103a157600080fd5b50610204600435610d88565b3480156103b957600080fd5b50610204610d9d565b3480156103ce57600080fd5b50610204610dc7565b3480156103e357600080fd5b50610204600435610dce565b3480156103fb57600080fd5b5061020460043560243560443560643560843563ffffffff60a4351660c435610de0565b34801561042b57600080fd5b50610204610e9b565b34801561044057600080fd5b5061044c600435610ea2565b604080518a8152602081018a9052908101889052606081018790526080810186905263ffffffff851660a082015260c08101849052600160a060020a03831660e082015261010081018260058111156104a157fe5b60ff168152602001995050505050505050505060405180910390f35b3480156104c957600080fd5b5061026b600435600160a060020a0360243516610f03565b3480156104ed57600080fd5b50610204600435611058565b34801561050557600080fd5b506102b460043561106d565b34801561051d57600080fd5b50610204600435611090565b34801561053557600080fd5b506102046110a5565b34801561054a57600080fd5b506102046110ac565b34801561055f57600080fd5b5061026b600435600160a060020a03602435166110b3565b34801561058357600080fd5b506040805160206004803580820135601f81018490048402850184019095528484526102049436949293602493928401919081908401838280828437505060408051818801358901803560208181028481018201909552818452989b6bffffffffffffffffffffffff198b35169b8a8c01359b919a9099506060909101975092955090820193509182918501908490808284375050604080516020601f89358b018035918201839004830284018301909452808352979a99988101979196509182019450925082915084018382808284375050604080516020808901358a01803580830284810184018652818552999c8b359c909b909a9501985092965081019450909250829190850190849080828437509497505084359550505050602090910135600160a060020a0316905061129b565b3480156106c257600080fd5b5061026b60043560243560443560643560843563ffffffff60a4351660c435600160a060020a0360e435166114fc565b3480156106fe57600080fd5b50610713600160a060020a03600435166117f6565b005b34801561072157600080fd5b50610204611851565b34801561073657600080fd5b50610204600435611858565b34801561074e57600080fd5b506040805160206004803580820135601f810184900484028501840190955284845261020494369492936024939284019190819084018382808284375050604080516020808901358a01803580830284810184018652818552999c8b359c909b909a95019850929650810194509092508291908501908490808284375050604080516020601f89358b018035918201839004830284018301909452808352979a99988101979196509182019450925082915084018382808284375094975050933594506118c59350505050565b34801561082757600080fd5b5061026b600435600160a060020a036024351661193c565b34801561084b57600080fd5b50610857600435611a91565b6040518082600581111561086757fe5b60ff16815260200191505060405180910390f35b34801561088757600080fd5b50610204611ab0565b34801561089c57600080fd5b50610204611ab7565b3480156108b157600080fd5b50610204611abd565b3480156108c657600080fd5b50610204611ac3565b3480156108db57600080fd5b50610204600435611ac9565b3480156108f357600080fd5b506102b4611ade565b34801561090857600080fd5b50610911611aea565b604051808261012080838360005b8381101561093757818101518382015260200161091f565b5050505090500191505060405180910390f35b34801561095657600080fd5b506040805160206004803580820135838102808601850190965280855261020495369593946024949385019291829185019084908082843750949750611b609650505050505050565b620157c081565b600554600160a060020a031681565b60055460009081908190600160a060020a03163314610a06576040805186815261c3966020820152815160008051602061227d833981519152929181900390910190a161c396925060009150610b08565b50600084815260208190526040902060026008820154606060020a900460ff166005811115610a3157fe5b14158015610a59575060016008820154606060020a900460ff166005811115610a5657fe5b14155b15610a96576040805186815261c3646020820152815160008051602061227d833981519152929181900390910190a161c364925060009150610b08565b6008810180546cff00000000000000000000000019166c0300000000000000000000000017905560408051868152600160a060020a038616602082015281517f87f54f5eb3dd119fe71af0915af693e64a5bfd4acaa19a6c944c47cff8eec9e6929181900390910190a1600085925092505b509250929050565b60009081526020819052604090206002015490565b60009081526020819052604090206008015468010000000000000000900463ffffffff1690565b60035481565b60035460009081908190819015610b6857600080fd5b8415610b7357600080fd5b610b828b8b8b8b8b8b8b610de0565b60008181526020819052604081209193509091506008820154606060020a900460ff166005811115610bb057fe5b14610bba57600080fd5b6002805463ffffffff90811660009081526001602081905260409091208590558d845583018c90558282018b9055600383018a905560048084018a9055600584018890556006840180543373ffffffffffffffffffffffffffffffffffffffff1990911617905591546008840180546801000000000000000067ffffffff000000001990911692841664010000000002929092176bffffffff000000000000000019169190911763ffffffff1916918916919091178082556cff0000000000000000000000001916606060020a830217905550600060078201556002805463ffffffff8082166001011663ffffffff199091161790556040805183815233602082015281517f64951c9008bba9f4663c12662e7a9b6412a7c4757869fdac09285564ae923fa1929181900390910190a1600382905560048a90556040805183815233602082015281517ff2dbbf0abb1ab1870a5e4d02746747c91d167c855255440b573ba3b5529dc901929181900390910190a15060009a909950975050505050505050565b60009081526020819052604090206004015490565b62018e7081565b62069f5081565b60006004610d7083611a91565b6005811115610d7b57fe5b1492915050565b619c4081565b60009081526020819052604090206005015490565b60035460009081526020819052604090206008015468010000000000000000900463ffffffff1690565b6206b6c081565b60009081526020819052604090205490565b6040805160208082018a9052818301899052606082018890526080820187905260a0820186905260e060020a63ffffffff86160260c083015260c48083018590528351808403909101815260e4909201928390528151600093918291908401908083835b60208310610e635780518252601f199092019160209182019101610e44565b5181516020939093036101000a600019018019909116921691909117905260405192018290039091209b9a5050505050505050505050565b620cd14081565b600090815260208190526040902080546001820154600283015460038401546004850154600886015460058701546006909701549597949693959294919363ffffffff8216939092600160a060020a0390911691606060020a900460ff1690565b60055460009081908190600160a060020a03163314610f54576040805186815261c3966020820152815160008051602061227d833981519152929181900390910190a161c396925060009150610b08565b50600084815260208190526040902060026008820154606060020a900460ff166005811115610f7f57fe5b14158015610fa7575060036008820154606060020a900460ff166005811115610fa457fe5b14155b15610fe4576040805186815261c3646020820152815160008051602061227d833981519152929181900390910190a161c364925060009150610b08565b6008810180546cff00000000000000000000000019166c0500000000000000000000000017905560408051868152600160a060020a038616602082015281517f64297372062dfcb21d6f7385f68d4656e993be2bb674099e3de73128d4911a91929181900390910190a15060009492505050565b60009081526020819052604090206007015490565b600090815260208190526040902060080154640100000000900463ffffffff1690565b60009081526020819052604090206003015490565b6205c49081565b6201388081565b600554600090819081908190600160a060020a03163314611106576040805187815261c3966020820152815160008051602061227d833981519152929181900390910190a161c396935060009250611292565b6000868152602081905260409020915060016008830154606060020a900460ff16600581111561113257fe5b1415801561115a575060036008830154606060020a900460ff16600581111561115757fe5b14155b15611197576040805187815261c3646020820152815160008051602061227d833981519152929181900390910190a161c364935060009250611292565b506005810154600090815260208190526040902060046008820154606060020a900460ff1660058111156111c757fe5b14611204576040805187815261c3826020820152815160008051602061227d833981519152929181900390910190a161c382935060009250611292565b6008820180546cff00000000000000000000000019166c040000000000000000000000001790556004546001830154111561124757600386905560018201546004555b60408051878152600160a060020a038716602082015281517ff2dbbf0abb1ab1870a5e4d02746747c91d167c855255440b573ba3b5529dc901929181900390910190a1600086935093505b50509250929050565b6000806000806112aa89611c24565b92506112b586610dce565b6112c0848a8a611dc7565b146112ff5760008051602061229d8339815191526112dd8e611c24565b60408051918252614e4860208301528051918290030190a1614e4893506114ec565b61130c8d8c8c8c8a6118c5565b915081156114a95784600160a060020a031663c3d5e1868e848f6000808c6000191660001916815260200190815260200160002060060160009054906101000a9004600160a060020a03166040518563ffffffff1660e060020a0281526004018080602001858152602001846bffffffffffffffffffffffff19166bffffffffffffffffffffffff1916815260200183600160a060020a0316600160a060020a03168152602001828103825286818151815260200191508051906020019080838360005b838110156113e85781810151838201526020016113d0565b50505050905090810190601f1680156114155780820380516001836020036101000a031916815260200191505b5095505050505050602060405180830381600087803b15801561143757600080fd5b505af115801561144b573d6000803e3d6000fd5b505050506040513d602081101561146157600080fd5b5051604080518481526020810183905281519293507f4e64138cc499eb1adf9edff9ef69bd45c56ac4bfd307540952e4c9d51eab55c1929081900390910190a18093506114ec565b604080516000815261753a602082015281517f4e64138cc499eb1adf9edff9ef69bd45c56ac4bfd307540952e4c9d51eab55c1929181900390910190a161753a93505b5050509998505050505050505050565b6005546000908190819081908190600160a060020a0316331461155257604080516000815261c3966020820152815160008051602061227d833981519152929181900390910190a161c3969450600093506117e6565b6000878152602081905260409020925060036008840154606060020a900460ff16600581111561157e57fe5b141580156115a6575060046008840154606060020a900460ff1660058111156115a357fe5b14155b156115e3576040805183815261c3826020820152815160008051602061227d833981519152929181900390910190a161c3829450600093506117e6565b6115f28d8d8d8d8d8d8d610de0565b60008181526020819052604081209193509091506008820154606060020a900460ff16600581111561162057fe5b1461165d576040805183815261c35a6020820152815160008051602061227d833981519152929181900390910190a161c35a9450600093506117e6565b6002805463ffffffff90811660009081526001602081905260409091208590558f84558381018f90558383018e9055600384018d9055600484018c9055600584018a905560068401805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a038b1617905591546008808501805467ffffffff0000000019169284166401000000000292909217808355908701546bffffffff00000000000000001990911668010000000000000000918290048416850184169091021763ffffffff1916918b16919091178082556cff0000000000000000000000001916606060020a8302179055506007830154600884015461177a919063ffffffff640100000000820481169168010000000000000000900416611e44565b60078201556002805463ffffffff8082166001011663ffffffff1990911617905560408051838152600160a060020a038816602082015281517f64951c9008bba9f4663c12662e7a9b6412a7c4757869fdac09285564ae923fa1929181900390910190a1600082945094505b5050509850989650505050505050565b600554600160a060020a03161580156118175750600160a060020a03811615155b151561182257600080fd5b6005805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a0392909216919091179055565b62061a8081565b60035460009060075b8361186b83610b25565b63ffffffff1611156118be575b61188181611ea4565b8461188b84610b25565b63ffffffff160310801561189f5750600081115b156118ad5760001901611878565b6118b78282611eaa565b9150611861565b5092915050565b6000806118d187611c24565b90508651604014156119105760408051828152614e5c6020820152815160008051602061229d833981519152929181900390910190a160009150611932565b61191d8187878787611f7e565b6001141561192d57809150611932565b600091505b5095945050505050565b60055460009081908190600160a060020a0316331461198d576040805186815261c3966020820152815160008051602061227d833981519152929181900390910190a161c396925060009150610b08565b50600084815260208190526040902060016008820154606060020a900460ff1660058111156119b857fe5b141580156119e0575060026008820154606060020a900460ff1660058111156119dd57fe5b14155b15611a1d576040805186815261c3646020820152815160008051602061227d833981519152929181900390910190a161c364925060009150610b08565b6008810180546cff00000000000000000000000019166c0200000000000000000000000017905560408051868152600160a060020a038616602082015281517f09cdaca254aa177f759fe7a0968fe696ee9baf7d2a1d4714ed24b83d1f09518e929181900390910190a15060009492505050565b600090815260208190526040902060080154606060020a900460ff1690565b62035b6081565b60045481565b6184d081565b60035490565b60009081526020819052604090206001015490565b60025463ffffffff1690565b611af261225c565b611afa61225c565b6003548082526000908190611b0e90611058565b9150600890505b6000811115611b585763ffffffff8216600090815260016020526040902054838260098110611b4057fe5b60200201526401000000009091049060001901611b15565b509092915050565b600073__DogeMessageLibrary____________________63f9b5d7c0836040518263ffffffff1660e060020a0281526004018080602001828103825283818151815260200191508051906020019060200280838360005b83811015611bcf578181015183820152602001611bb7565b505050509050019250505060206040518083038186803b158015611bf257600080fd5b505af4158015611c06573d6000803e3d6000fd5b505050506040513d6020811015611c1c57600080fd5b505192915050565b6000611dc1600280846040516020018082805190602001908083835b60208310611c5f5780518252601f199092019160209182019101611c40565b6001836020036101000a0380198251168184511680821785525050505050509050019150506040516020818303038152906040526040518082805190602001908083835b60208310611cc25780518252601f199092019160209182019101611ca3565b51815160209384036101000a600019018019909216911617905260405191909301945091925050808303816000865af1158015611d03573d6000803e3d6000fd5b5050506040513d6020811015611d1857600080fd5b50516040805160208181019390935281518082038401815290820191829052805190928291908401908083835b60208310611d645780518252601f199092019160209182019101611d45565b51815160209384036101000a600019018019909216911617905260405191909301945091925050808303816000865af1158015611da5573d6000803e3d6000fd5b5050506040513d6020811015611dba57600080fd5b5051612150565b92915050565b60008381808080805b8751851015611e36578785815181101515611de757fe5b6020908102909101015193506002890692508260011415611e0c575082905084611e19565b821515611e195750849050825b611e238282612173565b9550600289049850600185019450611dd0565b509398975050505050505050565b6000600581611e548682876121e8565b9550600190505b600881108015611e7657508184811515611e7157fe5b066001145b15611e9a57611e898682600402876121e8565b955060059190910290600101611e5b565b5093949350505050565b60050a90565b600082815260208181526040822060070154908290829060036004870201908110611ed157fe5b60f860020a91901a810204826002600487020160208110611eee57fe5b1a60f860020a0260f860020a9004610100028386600402600101602081101515611f1457fe5b1a60f860020a0260f860020a900462010000028487600402600001602081101515611f3b57fe5b1a60f860020a0260f860020a90046301000000020101019050600160008263ffffffff1663ffffffff168152602001908152602001600020549250505092915050565b600080611f8a83610d63565b1580611f9c5750611f9a83612224565b155b15611fd55760408051888152614e3e6020820152815160008051602061229d833981519152929181900390910190a1614e3e9150611932565b604080517fd2db98720000000000000000000000000000000000000000000000000000000081526000602482018190526004820192835286516044830152865173__DogeMessageLibrary____________________9363d2db9872938993928291606401906020860190808383885b8381101561205c578181015183820152602001612044565b50505050905090810190601f1680156120895780820380516001836020036101000a031916815260200191505b50935050505060206040518083038186803b1580156120a757600080fd5b505af41580156120bb573d6000803e3d6000fd5b505050506040513d60208110156120d157600080fd5b50519050806120e1888888611dc7565b1461211a5760408051888152614e526020820152815160008051602061229d833981519152929181900390910190a1614e529150611932565b6040805188815260016020820152815160008051602061229d833981519152929181900390910190a15060019695505050505050565b600060405160005b6020811015611c1c578381601f031a81830153600101612158565b60006121e160028061218486612150565b61218d86612150565b604051602001808381526020018281526020019250505060405160208183030381529060405260405180828051906020019080838360208310611cc25780518252601f199092019160209182019101611ca3565b9392505050565b60008060405185815283601c1a8582015383601d1a6001860182015383601e1a6002860182015383601f1a600386018201535195945050505050565b60008061223083610b25565b63ffffffff1690508015156122485760009150612256565b8261225282611858565b1491505b50919050565b6101206040519081016040528060099060208202803883395091929150505600a57c1ba4cf2c89b3558cfeeca4339e04551f0fc1a12cf63f1923c2eed8a5be8b65bd72698b9ffcfb3c7cb4c7414e13225cabd57fb690e183ae8c01c8ec268ebda165627a7a7230582055a668d617a2cc38ebde1eae7117be0e37d7bd682583941bf038cb4905edcc670029";

    protected static final HashMap<String, String> _addresses;

    static {
        _addresses = new HashMap<>();
        _addresses.put("32001", "0x58d3f24e281a9f7b018b6f1079e5e9e244d6911c");
    }

    protected DogeSuperblocks(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected DogeSuperblocks(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public List<NewSuperblockEventResponse> getNewSuperblockEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("NewSuperblock", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}));
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(event, transactionReceipt);
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

    public Observable<NewSuperblockEventResponse> newSuperblockEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("NewSuperblock", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, NewSuperblockEventResponse>() {
            @Override
            public NewSuperblockEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);
                NewSuperblockEventResponse typedResponse = new NewSuperblockEventResponse();
                typedResponse.log = log;
                typedResponse.superblockHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.who = (String) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public List<ApprovedSuperblockEventResponse> getApprovedSuperblockEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("ApprovedSuperblock", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}));
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(event, transactionReceipt);
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

    public Observable<ApprovedSuperblockEventResponse> approvedSuperblockEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("ApprovedSuperblock", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, ApprovedSuperblockEventResponse>() {
            @Override
            public ApprovedSuperblockEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);
                ApprovedSuperblockEventResponse typedResponse = new ApprovedSuperblockEventResponse();
                typedResponse.log = log;
                typedResponse.superblockHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.who = (String) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public List<ChallengeSuperblockEventResponse> getChallengeSuperblockEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("ChallengeSuperblock", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}));
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(event, transactionReceipt);
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

    public Observable<ChallengeSuperblockEventResponse> challengeSuperblockEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("ChallengeSuperblock", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, ChallengeSuperblockEventResponse>() {
            @Override
            public ChallengeSuperblockEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);
                ChallengeSuperblockEventResponse typedResponse = new ChallengeSuperblockEventResponse();
                typedResponse.log = log;
                typedResponse.superblockHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.who = (String) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public List<SemiApprovedSuperblockEventResponse> getSemiApprovedSuperblockEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("SemiApprovedSuperblock", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}));
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(event, transactionReceipt);
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

    public Observable<SemiApprovedSuperblockEventResponse> semiApprovedSuperblockEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("SemiApprovedSuperblock", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, SemiApprovedSuperblockEventResponse>() {
            @Override
            public SemiApprovedSuperblockEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);
                SemiApprovedSuperblockEventResponse typedResponse = new SemiApprovedSuperblockEventResponse();
                typedResponse.log = log;
                typedResponse.superblockHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.who = (String) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public List<InvalidSuperblockEventResponse> getInvalidSuperblockEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("InvalidSuperblock", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}));
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(event, transactionReceipt);
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

    public Observable<InvalidSuperblockEventResponse> invalidSuperblockEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("InvalidSuperblock", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, InvalidSuperblockEventResponse>() {
            @Override
            public InvalidSuperblockEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);
                InvalidSuperblockEventResponse typedResponse = new InvalidSuperblockEventResponse();
                typedResponse.log = log;
                typedResponse.superblockHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.who = (String) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public List<ErrorSuperblockEventResponse> getErrorSuperblockEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("ErrorSuperblock", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Uint256>() {}));
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(event, transactionReceipt);
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

    public Observable<ErrorSuperblockEventResponse> errorSuperblockEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("ErrorSuperblock", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, ErrorSuperblockEventResponse>() {
            @Override
            public ErrorSuperblockEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);
                ErrorSuperblockEventResponse typedResponse = new ErrorSuperblockEventResponse();
                typedResponse.log = log;
                typedResponse.superblockHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.err = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public List<VerifyTransactionEventResponse> getVerifyTransactionEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("VerifyTransaction", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Uint256>() {}));
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(event, transactionReceipt);
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

    public Observable<VerifyTransactionEventResponse> verifyTransactionEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("VerifyTransaction", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, VerifyTransactionEventResponse>() {
            @Override
            public VerifyTransactionEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);
                VerifyTransactionEventResponse typedResponse = new VerifyTransactionEventResponse();
                typedResponse.log = log;
                typedResponse.txHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.returnCode = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public List<RelayTransactionEventResponse> getRelayTransactionEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("RelayTransaction", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Uint256>() {}));
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(event, transactionReceipt);
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

    public Observable<RelayTransactionEventResponse> relayTransactionEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("RelayTransaction", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, RelayTransactionEventResponse>() {
            @Override
            public RelayTransactionEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);
                RelayTransactionEventResponse typedResponse = new RelayTransactionEventResponse();
                typedResponse.log = log;
                typedResponse.txHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.returnCode = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public RemoteCall<BigInteger> queryMerkleRootHashesCost() {
        final Function function = new Function("queryMerkleRootHashesCost", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<String> trustedClaimManager() {
        final Function function = new Function("trustedClaimManager", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<byte[]> bestSuperblock() {
        final Function function = new Function("bestSuperblock", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteCall<BigInteger> queryBlockHeaderCost() {
        final Function function = new Function("queryBlockHeaderCost", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> minProposalDeposit() {
        final Function function = new Function("minProposalDeposit", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> respondBlockHeaderCost() {
        final Function function = new Function("respondBlockHeaderCost", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> superblockCost() {
        final Function function = new Function("superblockCost", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> minChallengeDeposit() {
        final Function function = new Function("minChallengeDeposit", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> respondMerkleRootHashesCost() {
        final Function function = new Function("respondMerkleRootHashesCost", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }


    public RemoteCall<BigInteger> minReward() {
        final Function function = new Function("minReward", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> verifySuperblockCost() {
        final Function function = new Function("verifySuperblockCost", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> bestSuperblockAccumulatedWork() {
        final Function function = new Function("bestSuperblockAccumulatedWork", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> challengeCost() {
        final Function function = new Function("challengeCost", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public static RemoteCall<DogeSuperblocks> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(DogeSuperblocks.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<DogeSuperblocks> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(DogeSuperblocks.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public RemoteCall<TransactionReceipt> setClaimManager(String _claimManager) {
        final Function function = new Function(
                "setClaimManager", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_claimManager)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> initialize(byte[] _blocksMerkleRoot, BigInteger _accumulatedWork, BigInteger _timestamp, BigInteger _prevTimestamp, byte[] _lastHash, BigInteger _lastBits, byte[] _parentId) {
        final Function function = new Function(
                "initialize", 
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

    public RemoteCall<TransactionReceipt> propose(byte[] _blocksMerkleRoot, BigInteger _accumulatedWork, BigInteger _timestamp, BigInteger _prevTimestamp, byte[] _lastHash, BigInteger _lastBits, byte[] _parentId, String submitter) {
        final Function function = new Function(
                "propose", 
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

    public RemoteCall<TransactionReceipt> confirm(byte[] _superblockHash, String _validator) {
        final Function function = new Function(
                "confirm", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_superblockHash), 
                new org.web3j.abi.datatypes.Address(_validator)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> challenge(byte[] _superblockHash, String _challenger) {
        final Function function = new Function(
                "challenge", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_superblockHash), 
                new org.web3j.abi.datatypes.Address(_challenger)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> semiApprove(byte[] _superblockHash, String _validator) {
        final Function function = new Function(
                "semiApprove", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_superblockHash), 
                new org.web3j.abi.datatypes.Address(_validator)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> invalidate(byte[] _superblockHash, String _validator) {
        final Function function = new Function(
                "invalidate", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_superblockHash), 
                new org.web3j.abi.datatypes.Address(_validator)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> relayTx(byte[] _txBytes, BigInteger _txIndex, List<BigInteger> _txSiblings, byte[] _dogeBlockHeader, BigInteger _dogeBlockIndex, List<BigInteger> _dogeBlockSiblings, byte[] _superblockHash, String _untrustedTargetContract) {
        final Function function = new Function(
                "relayTx", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicBytes(_txBytes),
                new org.web3j.abi.datatypes.generated.Uint256(_txIndex), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint256>(
                        org.web3j.abi.Utils.typeMap(_txSiblings, org.web3j.abi.datatypes.generated.Uint256.class)), 
                new org.web3j.abi.datatypes.DynamicBytes(_dogeBlockHeader), 
                new org.web3j.abi.datatypes.generated.Uint256(_dogeBlockIndex), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint256>(
                        org.web3j.abi.Utils.typeMap(_dogeBlockSiblings, org.web3j.abi.datatypes.generated.Uint256.class)), 
                new org.web3j.abi.datatypes.generated.Bytes32(_superblockHash), 
                new org.web3j.abi.datatypes.Address(_untrustedTargetContract)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> verifyTx(byte[] _txBytes, BigInteger _txIndex, List<BigInteger> _siblings, byte[] _txBlockHeaderBytes, byte[] _txsuperblockHash) {
        final Function function = new Function(
                "verifyTx", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicBytes(_txBytes), 
                new org.web3j.abi.datatypes.generated.Uint256(_txIndex), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint256>(
                        org.web3j.abi.Utils.typeMap(_siblings, org.web3j.abi.datatypes.generated.Uint256.class)), 
                new org.web3j.abi.datatypes.DynamicBytes(_txBlockHeaderBytes), 
                new org.web3j.abi.datatypes.generated.Bytes32(_txsuperblockHash)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<byte[]> calcSuperblockHash(byte[] _blocksMerkleRoot, BigInteger _accumulatedWork, BigInteger _timestamp, BigInteger _prevTimestamp, byte[] _lastHash, BigInteger _lastBits, byte[] _parentId) {
        final Function function = new Function("calcSuperblockHash", 
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

    public RemoteCall<byte[]> getBestSuperblock() {
        final Function function = new Function("getBestSuperblock", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteCall<Tuple9<byte[], BigInteger, BigInteger, BigInteger, byte[], BigInteger, byte[], String, BigInteger>> getSuperblock(byte[] superblockHash) {
        final Function function = new Function("getSuperblock", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(superblockHash)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Uint32>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}, new TypeReference<Uint8>() {}));
        return new RemoteCall<Tuple9<byte[], BigInteger, BigInteger, BigInteger, byte[], BigInteger, byte[], String, BigInteger>>(
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

    public RemoteCall<BigInteger> getSuperblockHeight(byte[] superblockHash) {
        final Function function = new Function("getSuperblockHeight", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(superblockHash)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint32>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> getSuperblockIndex(byte[] superblockHash) {
        final Function function = new Function("getSuperblockIndex", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(superblockHash)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint32>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<byte[]> getSuperblockAncestors(byte[] superblockHash) {
        final Function function = new Function("getSuperblockAncestors", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(superblockHash)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteCall<byte[]> getSuperblockMerkleRoot(byte[] _superblockHash) {
        final Function function = new Function("getSuperblockMerkleRoot", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_superblockHash)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteCall<BigInteger> getSuperblockTimestamp(byte[] _superblockHash) {
        final Function function = new Function("getSuperblockTimestamp", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_superblockHash)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> getSuperblockPrevTimestamp(byte[] _superblockHash) {
        final Function function = new Function("getSuperblockPrevTimestamp", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_superblockHash)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<byte[]> getSuperblockLastHash(byte[] _superblockHash) {
        final Function function = new Function("getSuperblockLastHash", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_superblockHash)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteCall<byte[]> getSuperblockParentId(byte[] _superblockHash) {
        final Function function = new Function("getSuperblockParentId", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_superblockHash)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteCall<BigInteger> getSuperblockAccumulatedWork(byte[] _superblockHash) {
        final Function function = new Function("getSuperblockAccumulatedWork", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_superblockHash)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> getSuperblockStatus(byte[] _superblockHash) {
        final Function function = new Function("getSuperblockStatus", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_superblockHash)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> getIndexNextSuperblock() {
        final Function function = new Function("getIndexNextSuperblock", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint32>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<byte[]> makeMerkle(List<byte[]> hashes) {
        final Function function = new Function("makeMerkle", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Bytes32>(
                        org.web3j.abi.Utils.typeMap(hashes, org.web3j.abi.datatypes.generated.Bytes32.class))), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteCall<Boolean> isApproved(byte[] _superblockHash) {
        final Function function = new Function("isApproved", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_superblockHash)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<BigInteger> getChainHeight() {
        final Function function = new Function("getChainHeight", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<List> getSuperblockLocator() {
        final Function function = new Function("getSuperblockLocator", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<StaticArray9<Bytes32>>() {}));
        return new RemoteCall<List>(
                new Callable<List>() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public List call() throws Exception {
                        List<Type> result = (List<Type>) executeCallSingleValueReturn(function, List.class);
                        return convertToNative(result);
                    }
                });
    }

    public RemoteCall<byte[]> getSuperblockAt(BigInteger _height) {
        final Function function = new Function("getSuperblockAt", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_height)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public static DogeSuperblocks load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new DogeSuperblocks(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static DogeSuperblocks load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new DogeSuperblocks(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected String getStaticDeployedAddress(String networkId) {
        return _addresses.get(networkId);
    }

    public static String getPreviouslyDeployedAddress(String networkId) {
        return _addresses.get(networkId);
    }

    public static class NewSuperblockEventResponse {
        public Log log;

        public byte[] superblockHash;

        public String who;
    }

    public static class ApprovedSuperblockEventResponse {
        public Log log;

        public byte[] superblockHash;

        public String who;
    }

    public static class ChallengeSuperblockEventResponse {
        public Log log;

        public byte[] superblockHash;

        public String who;
    }

    public static class SemiApprovedSuperblockEventResponse {
        public Log log;

        public byte[] superblockHash;

        public String who;
    }

    public static class InvalidSuperblockEventResponse {
        public Log log;

        public byte[] superblockHash;

        public String who;
    }

    public static class ErrorSuperblockEventResponse {
        public Log log;

        public byte[] superblockHash;

        public BigInteger err;
    }

    public static class VerifyTransactionEventResponse {
        public Log log;

        public byte[] txHash;

        public BigInteger returnCode;
    }

    public static class RelayTransactionEventResponse {
        public Log log;

        public byte[] txHash;

        public BigInteger returnCode;
    }
}
