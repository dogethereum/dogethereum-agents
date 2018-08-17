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
    private static final String BINARY = "0x608060405234801561001057600080fd5b50611f51806100206000396000f3006080604052600436106101665763ffffffff60e060020a600035041663193e6f1a811461016b57806327426f75146101ad5780632da8cffd146101ea5780632e400191146102025780633288816a146102335780633a47290a146102485780633ce90e8f1461027857806348aefc321461029057806355e018ce146102bc5780635b572812146102d4578063642ed988146102e95780636e5b707114610301578063742057861461038a5780637b34dcd9146103ae57806387a4d382146103c65780638e4d8e99146103de57806395b45ee7146103f657806397dde2091461041a578063a9a36dcd14610559578063b1b595281461058a578063b6da2144146105c6578063c11818a1146105e9578063c1f67ab3146106c2578063cae0581e146106e6578063df22235714610722578063f06d520d14610737578063f2854e341461074c578063f32007e914610764578063f6f3238a14610779578063f9b5d7c0146107c7575b600080fd5b34801561017757600080fd5b5061019b60043560243560443560643560843563ffffffff60a4351660c43561081c565b60408051918252519081900360200190f35b3480156101b957600080fd5b506101d1600435600160a060020a03602435166108d7565b6040805192835260208301919091528051918290030190f35b3480156101f657600080fd5b5061019b600435610a0a565b34801561020e57600080fd5b5061021a600435610a1f565b6040805163ffffffff9092168252519081900360200190f35b34801561023f57600080fd5b5061019b610a46565b34801561025457600080fd5b506101d160043560243560443560643560843563ffffffff60a4351660c435610a4c565b34801561028457600080fd5b5061019b600435610c23565b34801561029c57600080fd5b506102a8600435610c38565b604080519115158252519081900360200190f35b3480156102c857600080fd5b5061019b600435610c57565b3480156102e057600080fd5b5061019b610c6c565b3480156102f557600080fd5b5061019b600435610c96565b34801561030d57600080fd5b50610319600435610ca8565b604080518a8152602081018a9052908101889052606081018790526080810186905263ffffffff851660a082015260c08101849052600160a060020a03831660e0820152610100810182600581111561036e57fe5b60ff168152602001995050505050505050505060405180910390f35b34801561039657600080fd5b506101d1600435600160a060020a0360243516610d09565b3480156103ba57600080fd5b5061019b600435610e5e565b3480156103d257600080fd5b5061021a600435610e73565b3480156103ea57600080fd5b5061019b600435610e96565b34801561040257600080fd5b506101d1600435600160a060020a0360243516610eab565b34801561042657600080fd5b506040805160206004803580820135601f810184900484028501840190955284845261019b9436949293602493928401919081908401838280828437505060408051818801358901803560208181028481018201909552818452989b6bffffffffffffffffffffffff198b35169b8a8c01359b919a9099506060909101975092955090820193509182918501908490808284375050604080516020601f89358b018035918201839004830284018301909452808352979a99988101979196509182019450925082915084018382808284375050604080516020808901358a01803580830284810184018652818552999c8b359c909b909a9501985092965081019450909250829190850190849080828437509497505084359550505050602090910135600160a060020a03169050611093565b34801561056557600080fd5b5061056e6112f4565b60408051600160a060020a039092168252519081900360200190f35b34801561059657600080fd5b506101d160043560243560443560643560843563ffffffff60a4351660c435600160a060020a0360e43516611303565b3480156105d257600080fd5b506105e7600160a060020a0360043516611603565b005b3480156105f557600080fd5b506040805160206004803580820135601f810184900484028501840190955284845261019b94369492936024939284019190819084018382808284375050604080516020808901358a01803580830284810184018652818552999c8b359c909b909a95019850929650810194509092508291908501908490808284375050604080516020601f89358b018035918201839004830284018301909452808352979a999881019791965091820194509250829150840183828082843750949750509335945061165e9350505050565b3480156106ce57600080fd5b506101d1600435600160a060020a03602435166116d5565b3480156106f257600080fd5b506106fe60043561182a565b6040518082600581111561070e57fe5b60ff16815260200191505060405180910390f35b34801561072e57600080fd5b5061019b611849565b34801561074357600080fd5b5061019b61184f565b34801561075857600080fd5b5061019b600435611855565b34801561077057600080fd5b5061021a61186a565b34801561078557600080fd5b5061078e611876565b604051808261012080838360005b838110156107b457818101518382015260200161079c565b5050505090500191505060405180910390f35b3480156107d357600080fd5b506040805160206004803580820135838102808601850190965280855261019b953695939460249493850192918291850190849080828437509497506118ec9650505050505050565b6040805160208082018a9052818301899052606082018890526080820187905260a0820186905260e060020a63ffffffff86160260c083015260c48083018590528351808403909101815260e4909201928390528151600093918291908401908083835b6020831061089f5780518252601f199092019160209182019101610880565b5181516020939093036101000a600019018019909116921691909117905260405192018290039091209b9a5050505050505050505050565b60055460009081908190600160a060020a03163314610928576040805186815261c39660208201528151600080516020611ee6833981519152929181900390910190a161c396925060009150610a02565b50600084815260208190526040902060026008820154606060020a900460ff16600581111561095357fe5b14610990576040805186815261c36460208201528151600080516020611ee6833981519152929181900390910190a161c364925060009150610a02565b6008810180546cff00000000000000000000000019166c0300000000000000000000000017905560408051868152600160a060020a038616602082015281517f87f54f5eb3dd119fe71af0915af693e64a5bfd4acaa19a6c944c47cff8eec9e6929181900390910190a1600085925092505b509250929050565b60009081526020819052604090206002015490565b60009081526020819052604090206008015468010000000000000000900463ffffffff1690565b60035481565b60035460009081908190819015610a6257600080fd5b8415610a6d57600080fd5b610a7c8b8b8b8b8b8b8b61081c565b60008181526020819052604081209193509091506008820154606060020a900460ff166005811115610aaa57fe5b14610ab457600080fd5b6002805463ffffffff90811660009081526001602081905260409091208590558d845583018c90558282018b9055600383018a905560048084018a9055600584018890556006840180543373ffffffffffffffffffffffffffffffffffffffff19909116179055915460088401805467ffffffff00000000191691831664010000000002919091176bffffffff00000000ffffffff1916918916919091178082556cff0000000000000000000000001916606060020a830217905550600060078201556002805463ffffffff8082166001011663ffffffff199091161790556040805183815233602082015281517f64951c9008bba9f4663c12662e7a9b6412a7c4757869fdac09285564ae923fa1929181900390910190a1600382905560048a90556040805183815233602082015281517ff2dbbf0abb1ab1870a5e4d02746747c91d167c855255440b573ba3b5529dc901929181900390910190a15060009a909950975050505050505050565b60009081526020819052604090206004015490565b60006004610c458361182a565b6005811115610c5057fe5b1492915050565b60009081526020819052604090206005015490565b60035460009081526020819052604090206008015468010000000000000000900463ffffffff1690565b60009081526020819052604090205490565b600090815260208190526040902080546001820154600283015460038401546004850154600886015460058701546006909701549597949693959294919363ffffffff8216939092600160a060020a0390911691606060020a900460ff1690565b60055460009081908190600160a060020a03163314610d5a576040805186815261c39660208201528151600080516020611ee6833981519152929181900390910190a161c396925060009150610a02565b50600084815260208190526040902060026008820154606060020a900460ff166005811115610d8557fe5b14158015610dad575060036008820154606060020a900460ff166005811115610daa57fe5b14155b15610dea576040805186815261c36460208201528151600080516020611ee6833981519152929181900390910190a161c364925060009150610a02565b6008810180546cff00000000000000000000000019166c0500000000000000000000000017905560408051868152600160a060020a038616602082015281517f64297372062dfcb21d6f7385f68d4656e993be2bb674099e3de73128d4911a91929181900390910190a15060009492505050565b60009081526020819052604090206007015490565b600090815260208190526040902060080154640100000000900463ffffffff1690565b60009081526020819052604090206003015490565b600554600090819081908190600160a060020a03163314610efe576040805187815261c39660208201528151600080516020611ee6833981519152929181900390910190a161c39693506000925061108a565b6000868152602081905260409020915060016008830154606060020a900460ff166005811115610f2a57fe5b14158015610f52575060036008830154606060020a900460ff166005811115610f4f57fe5b14155b15610f8f576040805187815261c36460208201528151600080516020611ee6833981519152929181900390910190a161c36493506000925061108a565b506005810154600090815260208190526040902060046008820154606060020a900460ff166005811115610fbf57fe5b14610ffc576040805187815261c38260208201528151600080516020611ee6833981519152929181900390910190a161c38293506000925061108a565b6008820180546cff00000000000000000000000019166c040000000000000000000000001790556004546001830154111561103f57600386905560018201546004555b60408051878152600160a060020a038716602082015281517ff2dbbf0abb1ab1870a5e4d02746747c91d167c855255440b573ba3b5529dc901929181900390910190a1600086935093505b50509250929050565b6000806000806110a2896119b0565b92506110ad86610c96565b6110b8848a8a611b53565b146110f757600080516020611f068339815191526110d58e6119b0565b60408051918252614e4860208301528051918290030190a1614e4893506112e4565b6111048d8c8c8c8a61165e565b915081156112a15784600160a060020a031663c3d5e1868e848f6000808c6000191660001916815260200190815260200160002060060160009054906101000a9004600160a060020a03166040518563ffffffff1660e060020a0281526004018080602001858152602001846bffffffffffffffffffffffff19166bffffffffffffffffffffffff1916815260200183600160a060020a0316600160a060020a03168152602001828103825286818151815260200191508051906020019080838360005b838110156111e05781810151838201526020016111c8565b50505050905090810190601f16801561120d5780820380516001836020036101000a031916815260200191505b5095505050505050602060405180830381600087803b15801561122f57600080fd5b505af1158015611243573d6000803e3d6000fd5b505050506040513d602081101561125957600080fd5b5051604080518481526020810183905281519293507f4e64138cc499eb1adf9edff9ef69bd45c56ac4bfd307540952e4c9d51eab55c1929081900390910190a18093506112e4565b604080516000815261753a602082015281517f4e64138cc499eb1adf9edff9ef69bd45c56ac4bfd307540952e4c9d51eab55c1929181900390910190a161753a93505b5050509998505050505050505050565b600554600160a060020a031681565b6005546000908190819081908190600160a060020a0316331461135957604080516000815261c39660208201528151600080516020611ee6833981519152929181900390910190a161c3969450600093506115f3565b6000878152602081905260409020925060036008840154606060020a900460ff16600581111561138557fe5b141580156113ad575060046008840154606060020a900460ff1660058111156113aa57fe5b14155b156113ea576040805183815261c38260208201528151600080516020611ee6833981519152929181900390910190a161c3829450600093506115f3565b6113f98d8d8d8d8d8d8d61081c565b60008181526020819052604081209193509091506008820154606060020a900460ff16600581111561142757fe5b14611464576040805183815261c35a60208201528151600080516020611ee6833981519152929181900390910190a161c35a9450600093506115f3565b6002805463ffffffff90811660009081526001602081905260409091208590558f84558381018f90558383018e9055600384018d9055600484018c9055600584018a905560068401805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a038b1617905591546008808501805467ffffffff0000000019169284166401000000000292909217808355908701546bffffffff00000000000000001990911668010000000000000000918290048416850184169091021763ffffffff1916918b16919091178082556cff0000000000000000000000001916606060020a83021790555060078301546008840154611587919063ffffffff640100000000820481169160016801000000000000000090910482160116611bd0565b60078201556002805463ffffffff8082166001011663ffffffff1990911617905560408051838152600160a060020a038816602082015281517f64951c9008bba9f4663c12662e7a9b6412a7c4757869fdac09285564ae923fa1929181900390910190a1600082945094505b5050509850989650505050505050565b600554600160a060020a03161580156116245750600160a060020a03811615155b151561162f57600080fd5b6005805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a0392909216919091179055565b60008061166a876119b0565b90508651604014156116a95760408051828152614e5c60208201528151600080516020611f06833981519152929181900390910190a1600091506116cb565b6116b68187878787611c30565b600114156116c6578091506116cb565b600091505b5095945050505050565b60055460009081908190600160a060020a03163314611726576040805186815261c39660208201528151600080516020611ee6833981519152929181900390910190a161c396925060009150610a02565b50600084815260208190526040902060016008820154606060020a900460ff16600581111561175157fe5b14158015611779575060026008820154606060020a900460ff16600581111561177657fe5b14155b156117b6576040805186815261c36460208201528151600080516020611ee6833981519152929181900390910190a161c364925060009150610a02565b6008810180546cff00000000000000000000000019166c0200000000000000000000000017905560408051868152600160a060020a038616602082015281517f09cdaca254aa177f759fe7a0968fe696ee9baf7d2a1d4714ed24b83d1f09518e929181900390910190a15060009492505050565b600090815260208190526040902060080154606060020a900460ff1690565b60045481565b60035490565b60009081526020819052604090206001015490565b60025463ffffffff1690565b61187e611ec5565b611886611ec5565b600354808252600090819061189a90610e5e565b9150600890505b60008111156118e45763ffffffff82166000908152600160205260409020548382600981106118cc57fe5b602002015264010000000090910490600019016118a1565b509092915050565b600073__DogeTx________________________________63f9b5d7c0836040518263ffffffff1660e060020a0281526004018080602001828103825283818151815260200191508051906020019060200280838360005b8381101561195b578181015183820152602001611943565b505050509050019250505060206040518083038186803b15801561197e57600080fd5b505af4158015611992573d6000803e3d6000fd5b505050506040513d60208110156119a857600080fd5b505192915050565b6000611b4d600280846040516020018082805190602001908083835b602083106119eb5780518252601f1990920191602091820191016119cc565b6001836020036101000a0380198251168184511680821785525050505050509050019150506040516020818303038152906040526040518082805190602001908083835b60208310611a4e5780518252601f199092019160209182019101611a2f565b51815160209384036101000a600019018019909216911617905260405191909301945091925050808303816000865af1158015611a8f573d6000803e3d6000fd5b5050506040513d6020811015611aa457600080fd5b50516040805160208181019390935281518082038401815290820191829052805190928291908401908083835b60208310611af05780518252601f199092019160209182019101611ad1565b51815160209384036101000a600019018019909216911617905260405191909301945091925050808303816000865af1158015611b31573d6000803e3d6000fd5b5050506040513d6020811015611b4657600080fd5b5051611df1565b92915050565b60008381808080805b8751851015611bc2578785815181101515611b7357fe5b6020908102909101015193506002890692508260011415611b98575082905084611ba5565b821515611ba55750849050825b611baf8282611e14565b9550600289049850600185019450611b5c565b509398975050505050505050565b6000600581611be0868287611e89565b9550600190505b600881108015611c0257508184811515611bfd57fe5b066001145b15611c2657611c15868260040287611e89565b955060059190910290600101611be7565b5093949350505050565b600080611c3c83610c38565b1515611c765760408051888152614e3e60208201528151600080516020611f06833981519152929181900390910190a1614e3e91506116cb565b604080517fd2db98720000000000000000000000000000000000000000000000000000000081526000602482018190526004820192835286516044830152865173__DogeTx________________________________9363d2db9872938993928291606401906020860190808383885b83811015611cfd578181015183820152602001611ce5565b50505050905090810190601f168015611d2a5780820380516001836020036101000a031916815260200191505b50935050505060206040518083038186803b158015611d4857600080fd5b505af4158015611d5c573d6000803e3d6000fd5b505050506040513d6020811015611d7257600080fd5b5051905080611d82888888611b53565b14611dbb5760408051888152614e5260208201528151600080516020611f06833981519152929181900390910190a1614e5291506116cb565b60408051888152600160208201528151600080516020611f06833981519152929181900390910190a15060019695505050505050565b600060405160005b60208110156119a8578381601f031a81830153600101611df9565b6000611e82600280611e2586611df1565b611e2e86611df1565b604051602001808381526020018281526020019250505060405160208183030381529060405260405180828051906020019080838360208310611a4e5780518252601f199092019160209182019101611a2f565b9392505050565b60008060405185815283601c1a8582015383601d1a6001860182015383601e1a6002860182015383601f1a600386018201535195945050505050565b6101206040519081016040528060099060208202803883395091929150505600a57c1ba4cf2c89b3558cfeeca4339e04551f0fc1a12cf63f1923c2eed8a5be8b65bd72698b9ffcfb3c7cb4c7414e13225cabd57fb690e183ae8c01c8ec268ebda165627a7a7230582075ef3d751eaca8b682d971c4a0149189540a3aabb6bf0d270f49466b487fd26a0029";

    protected static final HashMap<String, String> _addresses;

    static {
        _addresses = new HashMap<>();
        _addresses.put("32001", "0x59445ebf9c6ce16dd8517c7711348de129cbd0da");
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
            typedResponse.superblockId = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
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
                typedResponse.superblockId = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
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
            typedResponse.superblockId = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
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
                typedResponse.superblockId = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
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
            typedResponse.superblockId = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
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
                typedResponse.superblockId = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
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
            typedResponse.superblockId = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
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
                typedResponse.superblockId = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
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
            typedResponse.superblockId = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
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
                typedResponse.superblockId = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
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
            typedResponse.superblockId = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
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
                typedResponse.superblockId = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
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

    public RemoteCall<byte[]> bestSuperblock() {
        final Function function = new Function("bestSuperblock", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteCall<String> claimManager() {
        final Function function = new Function("claimManager", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<BigInteger> bestSuperblockAccumulatedWork() {
        final Function function = new Function("bestSuperblockAccumulatedWork", 
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

    public RemoteCall<TransactionReceipt> confirm(byte[] _superblockId, String validator) {
        final Function function = new Function(
                "confirm", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_superblockId), 
                new org.web3j.abi.datatypes.Address(validator)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> challenge(byte[] _superblockId, String challenger) {
        final Function function = new Function(
                "challenge", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_superblockId), 
                new org.web3j.abi.datatypes.Address(challenger)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> semiApprove(byte[] _superblockId, String validator) {
        final Function function = new Function(
                "semiApprove", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_superblockId), 
                new org.web3j.abi.datatypes.Address(validator)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> invalidate(byte[] _superblockId, String validator) {
        final Function function = new Function(
                "invalidate", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_superblockId), 
                new org.web3j.abi.datatypes.Address(validator)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> relayTx(byte[] _txBytes, byte[] _operatorPublicKeyHash, BigInteger _txIndex, List<BigInteger> _txSiblings, byte[] _dogeBlockHeader, BigInteger _dogeBlockIndex, List<BigInteger> _dogeBlockSiblings, byte[] _superblockId, String _targetContract) {
        final Function function = new Function(
                "relayTx", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicBytes(_txBytes), 
                new org.web3j.abi.datatypes.generated.Bytes20(_operatorPublicKeyHash), 
                new org.web3j.abi.datatypes.generated.Uint256(_txIndex), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint256>(
                        org.web3j.abi.Utils.typeMap(_txSiblings, org.web3j.abi.datatypes.generated.Uint256.class)), 
                new org.web3j.abi.datatypes.DynamicBytes(_dogeBlockHeader), 
                new org.web3j.abi.datatypes.generated.Uint256(_dogeBlockIndex), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint256>(
                        org.web3j.abi.Utils.typeMap(_dogeBlockSiblings, org.web3j.abi.datatypes.generated.Uint256.class)), 
                new org.web3j.abi.datatypes.generated.Bytes32(_superblockId), 
                new org.web3j.abi.datatypes.Address(_targetContract)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> verifyTx(byte[] _txBytes, BigInteger _txIndex, List<BigInteger> _siblings, byte[] _txBlockHeaderBytes, byte[] _txSuperblockId) {
        final Function function = new Function(
                "verifyTx", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicBytes(_txBytes), 
                new org.web3j.abi.datatypes.generated.Uint256(_txIndex), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint256>(
                        org.web3j.abi.Utils.typeMap(_siblings, org.web3j.abi.datatypes.generated.Uint256.class)), 
                new org.web3j.abi.datatypes.DynamicBytes(_txBlockHeaderBytes), 
                new org.web3j.abi.datatypes.generated.Bytes32(_txSuperblockId)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<byte[]> calcSuperblockId(byte[] _blocksMerkleRoot, BigInteger _accumulatedWork, BigInteger _timestamp, BigInteger _prevTimestamp, byte[] _lastHash, BigInteger _lastBits, byte[] _parentId) {
        final Function function = new Function("calcSuperblockId", 
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

    public RemoteCall<Tuple9<byte[], BigInteger, BigInteger, BigInteger, byte[], BigInteger, byte[], String, BigInteger>> getSuperblock(byte[] superblockId) {
        final Function function = new Function("getSuperblock", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(superblockId)), 
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

    public RemoteCall<BigInteger> getSuperblockHeight(byte[] superblockId) {
        final Function function = new Function("getSuperblockHeight", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(superblockId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint32>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> getSuperblockIndex(byte[] superblockId) {
        final Function function = new Function("getSuperblockIndex", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(superblockId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint32>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<byte[]> getSuperblockAncestors(byte[] superblockId) {
        final Function function = new Function("getSuperblockAncestors", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(superblockId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteCall<byte[]> getSuperblockMerkleRoot(byte[] _superblockId) {
        final Function function = new Function("getSuperblockMerkleRoot", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_superblockId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteCall<BigInteger> getSuperblockTimestamp(byte[] _superblockId) {
        final Function function = new Function("getSuperblockTimestamp", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_superblockId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> getSuperblockPrevTimestamp(byte[] _superblockId) {
        final Function function = new Function("getSuperblockPrevTimestamp", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_superblockId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<byte[]> getSuperblockLastHash(byte[] _superblockId) {
        final Function function = new Function("getSuperblockLastHash", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_superblockId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteCall<byte[]> getSuperblockParentId(byte[] _superblockId) {
        final Function function = new Function("getSuperblockParentId", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_superblockId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteCall<BigInteger> getSuperblockAccumulatedWork(byte[] _superblockId) {
        final Function function = new Function("getSuperblockAccumulatedWork", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_superblockId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> getSuperblockStatus(byte[] _superblockId) {
        final Function function = new Function("getSuperblockStatus", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_superblockId)), 
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

    public RemoteCall<Boolean> isApproved(byte[] _superblockId) {
        final Function function = new Function("isApproved", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_superblockId)), 
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

        public byte[] superblockId;

        public String who;
    }

    public static class ApprovedSuperblockEventResponse {
        public Log log;

        public byte[] superblockId;

        public String who;
    }

    public static class ChallengeSuperblockEventResponse {
        public Log log;

        public byte[] superblockId;

        public String who;
    }

    public static class SemiApprovedSuperblockEventResponse {
        public Log log;

        public byte[] superblockId;

        public String who;
    }

    public static class InvalidSuperblockEventResponse {
        public Log log;

        public byte[] superblockId;

        public String who;
    }

    public static class ErrorSuperblockEventResponse {
        public Log log;

        public byte[] superblockId;

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
