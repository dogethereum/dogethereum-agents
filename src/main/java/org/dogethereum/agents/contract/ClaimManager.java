package org.dogethereum.agents.contract;

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
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple4;
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
public class ClaimManager extends Contract {
    private static final String BINARY = "0x6080604052600180556001600255600560035534801561001e57600080fd5b506040516020806122be833981016040525160048054600160a060020a031916600160a060020a039092169190911790556122608061005e6000396000f3006080604052600436106101195763ffffffff60e060020a6000350416630b7aa418811461012457806311470582146101555780632b2299281461016d57806333289a461461019457806340732c89146101ac57806341b3d185146101b457806350a1676e146101c957806354a452fb146101e15780635aef24471461020d5780635d57e82e1461031e5780635e45fa7a1461038c57806366ffaaab146103bb578063887daa2a146103e15780638b6dcfc21461040557806392c824d81461042957806392f2df2114610441578063bc38036414610465578063cb01f1b2146104cd578063ce3c5dcb146104e5578063e1254fba146104fd578063e32250de1461051e578063f350351f14610536578063fc7e286d1461054e575b61012161056f565b50005b34801561013057600080fd5b50610139610590565b60408051600160a060020a039092168252519081900360200190f35b34801561016157600080fd5b5061013960043561059f565b34801561017957600080fd5b506101826105df565b60408051918252519081900360200190f35b3480156101a057600080fd5b506101826004356105e5565b61018261056f565b3480156101c057600080fd5b50610182610691565b3480156101d557600080fd5b50610182600435610697565b3480156101ed57600080fd5b506101f96004356106ac565b604080519115158252519081900360200190f35b34801561021957600080fd5b5061022560043561095e565b60408051600160a060020a038616815260608101839052608060208083018281528751928401929092528651929391929184019160a085019188019080838360005b8381101561027f578181015183820152602001610267565b50505050905090810190601f1680156102ac5780820380516001836020036101000a031916815260200191505b50838103825285518152855160209182019187019080838360005b838110156102df5781810151838201526020016102c7565b50505050905090810190601f16801561030c5780820380516001836020036101000a031916815260200191505b50965050505050505060405180910390f35b34801561032a57600080fd5b506040805160206004803580820135601f810184900484028501840190955284845261018294369492936024939284019190819084018382808284375094975050843595505050506020820135600160a060020a031691604001359050610abb565b34801561039857600080fd5b506103b9600435602435600160a060020a0360443581169060643516610b67565b005b6103b9602460048035828101929101359035604435600160a060020a0360643516610d33565b3480156103ed57600080fd5b50610182600435600160a060020a03602435166111ce565b34801561041157600080fd5b50610182600435600160a060020a036024351661128d565b34801561043557600080fd5b506103b96004356113df565b34801561044d57600080fd5b50610182600435600160a060020a036024351661157b565b34801561047157600080fd5b5061047d6004356115a6565b60408051602080825283518183015283519192839290830191858101910280838360005b838110156104b95781810151838201526020016104a1565b505050509050019250505060405180910390f35b3480156104d957600080fd5b50610139600435611615565b3480156104f157600080fd5b506101f9600435611644565b34801561050957600080fd5b50610182600160a060020a036004351661165c565b34801561052a57600080fd5b506103b9600435611677565b34801561054257600080fd5b506103b9600435611a12565b34801561055a57600080fd5b50610182600160a060020a0360043516611ef5565b600061057b3334611f07565b50336000908152602081905260409020545b90565b600454600160a060020a031681565b600081815260056020526040812060078101546004909101805490919081106105c457fe5b600091825260209091200154600160a060020a031692915050565b60035481565b3360009081526020819052604081205482111561060157600080fd5b33600081815260208190526040808220805486900390555184156108fc0291859190818181858888f19350505050158015610640573d6000803e3d6000fd5b50604080513381526020810184905281517f4482101800a5c2e900f4156e57e05e19ffd7b366cde579553d723fd3abb2180e929181900390910190a150503360009081526020819052604090205490565b60025481565b60009081526005602052604090206003015490565b600081815260056020908152604080832081516101a0810183528154600160a060020a03168152600180830180548551600261010094831615949094026000190190911692909204601f81018790048702830187019095528482529294869485948594859485946108e79492938b9385830193928301828280156107715780601f1061074657610100808354040283529160200191610771565b820191906000526020600020905b81548152906001019060200180831161075457829003601f168201915b5050509183525050600282810180546040805160206001841615610100026000190190931694909404601f810183900483028501830190915280845293810193908301828280156108035780601f106107d857610100808354040283529160200191610803565b820191906000526020600020905b8154815290600101906020018083116107e657829003601f168201915b50505050508152602001600382015481526020016004820180548060200260200160405190810160405280929190818152602001828054801561086f57602002820191906000526020600020905b8154600160a060020a03168152600190910190602001808311610851575b50505091835250506006820154602082015260078201546040820152600882015460ff90811615156060830152600a8301548082161515608084015261010090819004909116151560a0830152600b83015460c0830152600c83015460e0830152600d90920154600160a060020a0316910152611f7c565b9450600354610903876003015443611f8b90919063ffffffff16565b600b880154600889015460078a015460068b01549490931197504391909111955060ff161593501490508480156109375750835b80156109405750825b80156109495750815b80156109525750805b98975050505050505050565b60008181526005602090815260408083208054600c8201546001808401805486516002610100948316159490940260001901909116839004601f810189900489028201890190975286815260609788978a9796600160a060020a03169593948701939291859190830182828015610a165780601f106109eb57610100808354040283529160200191610a16565b820191906000526020600020905b8154815290600101906020018083116109f957829003601f168201915b5050855460408051602060026001851615610100026000190190941693909304601f810184900484028201840190925281815295985087945092508401905082828015610aa45780601f10610a7957610100808354040283529160200191610aa4565b820191906000526020600020905b815481529060010190602001808311610a8757829003601f168201915b505050505091509450945094509450509193509193565b604080516c01000000000000000000000000600160a060020a03851602602080830191909152603482018690526054808301859052835180840390910181526074909201928390528151600093918291908401908083835b60208310610b325780518252601f199092019160209182019101610b13565b5181516020939093036101000a6000190180199091169216919091179052604051920182900390912098975050505050505050565b6004546000908190600160a060020a0316338114610b8457600080fd5b60008681526005602090815260409182902082516101a0810184528154600160a060020a03168152600180830180548651600261010094831615949094026000190190911692909204601f8101869004860283018601909652858252929750610c1794919388938582019390918301828280156107715780601f1061074657610100808354040283529160200191610771565b1515610c2257600080fd5b60088301805460ff19169055600160a060020a038085166000908152600985016020526040808220549288168252902054909250610c66908363ffffffff611f9d16565b600160a060020a03808716600090815260098601602052604080822093909355868216808252928120558454161415610cb95760068301546007840155600a8301805461ff001916610100179055610ce1565b8254600160a060020a0386811691161415610cdc57610cd786611677565b610ce1565b600080fd5b60408051888152600160a060020a03808816602083015286168183015290517f27d6edc6ce9ab48630ff9c08892a548870cd6985dbe8d4cb1a21c158a5fba7b99181900360600190a150505050505050565b6040805160208082528183019092526060916000918291829190808201610400803883395050506020810188905293503292503415610d7657610d768334611f07565b600254600160a060020a0384166000908152602081905260409020541015610d9d57600080fd5b604080516c01000000000000000000000000600160a060020a03861602602080830191909152603482018a905260548083018a905283518084039091018152607490920192839052815191929182918401908083835b60208310610e125780518252601f199092019160209182019101610df3565b518151600019602094850361010090810a820192831692199390931691909117909252604080519690940186900386206000818152600585528590206101a0880186528054600160a060020a03168852600181810180548851601f6002948316159097029097011691909104938401869004860285018601909652828452909a50610ec9985095965086830194509092918301828280156107715780601f1061074657610100808354040283529160200191610771565b15610ed357600080fd5b506000818152600560205260409020805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a038416178155610f16600182018a8a61212e565b508351610f2c90600283019060208701906121ac565b50600060068201819055600782015560088101805460ff19169055436003820155600a8101805461ffff19169055600c8101869055600d8101805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a03878116919091179091558154600254610fa29285921690611fb0565b50805460408051848152600160a060020a03909216602083018190526080918301828152600180860180546002600019938216156101000293909301168290049486018590527f3dd94f4964946edbcf822a77f4abd83b2a91b03f889cece28612831d85c243d39588959193928801929091606083019060a08401908690801561106d5780601f106110425761010080835404028352916020019161106d565b820191906000526020600020905b81548152906001019060200180831161105057829003601f168201915b50508381038252845460026000196101006001841615020190911604808252602090910190859080156110e15780601f106110b6576101008083540402835291602001916110e1565b820191906000526020600020905b8154815290600101906020018083116110c457829003601f168201915b5050965050505050505060405180910390a180600d0160009054906101000a9004600160a060020a0316600160a060020a0316636c8bb73982600c0154898c8c336040518663ffffffff1660e060020a02815260040180866000191660001916815260200185600019166000191681526020018060200183600160a060020a0316600160a060020a03168152602001828103825285858281815260200192508082843782019150509650505050505050600060405180830381600087803b1580156111ab57600080fd5b505af11580156111bf573d6000803e3d6000fd5b50505050505050505050505050565b600082815260056020908152604080832081516101a0810183528154600160a060020a03168152600180830180548551600261010094831615949094026000190190911692909204601f8101879004870283018701909552848252929461125e949293869385830193928301828280156107715780601f1061074657610100808354040283529160200191610771565b151561126957600080fd5b600160a060020a038316600090815260098201602052604090205491505092915050565b600082815260056020908152604080832081516101a0810183528154600160a060020a03168152600180830180548551600261010094831615949094026000190190911692909204601f81018790048702830187019095528482529294869461131f948793858401939092908301828280156107715780601f1061074657610100808354040283529160200191610771565b151561132a57600080fd5b600a82015460ff16151560011461134057600080fd5b50600160a060020a03831660009081526009820160209081526040808320805490849055918390529091205461137c908263ffffffff611f9d16565b600160a060020a0385166000818152602081815260409182902093909355805188815292830191909152818101839052517f44c8a24da6f48938ce6ee551c058529a48695baf9b754bf3184fc5f065878b109181900360600190a1949350505050565b60008181526005602090815260409182902082516101a0810184528154600160a060020a03168152600180830180548651600261010094831615949094026000190190911692909204601f8101869004860283018601909652858252929461147194929386938581019392908301828280156107715780601f1061074657610100808354040283529160200191610771565b151561147c57600080fd5b600a81015460ff161561148e57600080fd5b336000908152600582016020526040902054156114aa57600080fd5b6002543360009081526020819052604090205410156114c857600080fd5b6114d58233600254611fb0565b506003546114ea90439063ffffffff611f9d16565b600b820155600481018054600181810183556000928352602090922001805473ffffffffffffffffffffffffffffffffffffffff19163317905560068201546115389163ffffffff611f9d16565b60068201556040805183815233602082015281517f829b4d84d4743d197a9b0fad05abef9f517535e06d50e0b85b1e77089b8afa9d929181900390910190a15050565b6000828152600560208181526040808420600160a060020a0386168552909201905290205492915050565b60008181526005602090815260409182902060040180548351818402810184019094528084526060939283018282801561160957602002820191906000526020600020905b8154600160a060020a031681526001909101906020018083116115eb575b50505050509050919050565b600154600090821061162657600080fd5b6000828152600560205260408120600401805490919081106105c457fe5b60009081526005602052604090206008015460ff1690565b600160a060020a031660009081526020819052604090205490565b600081815260056020908152604080832081516101a0810183528154600160a060020a03168152600180830180548551600261010094831615949094026000190190911692909204601f8101879004870283018701909552848252929594611708949293879385830193928301828280156107715780601f1061074657610100808354040283529160200191610771565b151561171357600080fd5b600a82015460ff161561172557600080fd5b600882015460ff161561173757600080fd5b816007015482600601541115611a0d576004805460078401549184018054600160a060020a03909216926383ce5da59287929190811061177357fe5b600091825260209091200154855460405163ffffffff851660e060020a02815260048101848152600160a060020a0393841660248301819052939092166044820181905261080160a4830181905260c0606484019081526001808c01805460026101009382161593909302600019011682900460c4870181905294969095918d0194909291608481019160e490910190879080156118525780601f1061182757610100808354040283529160200191611852565b820191906000526020600020905b81548152906001019060200180831161183557829003601f168201915b50508381038252855460026000196101006001841615020190911604808252602090910190869080156118c65780601f1061189b576101008083540402835291602001916118c6565b820191906000526020600020905b8154815290600101906020018083116118a957829003601f168201915b505098505050505050505050602060405180830381600087803b1580156118ec57600080fd5b505af1158015611900573d6000803e3d6000fd5b505050506040513d602081101561191657600080fd5b505160078301546004840180549293508392600586019260009291811061193957fe5b6000918252602080832090910154600160a060020a039081168452908301939093526040909101902091909155825460078401546004850180547f2b294b3eef63ed64ae6063c231cf082926ced732f1acc4ac1a7c052314b0edf294889416929081106119a257fe5b6000918252602091829020015460408051948552600160a060020a0393841692850192909252919091168282015260608201849052519081900360800190a160088201805460ff191660019081179091556007830154611a079163ffffffff611f9d16565b60078301555b505050565b60008181526005602090815260409182902082516101a0810184528154600160a060020a03168152600180830180548651600261010094831615949094026000190190911692909204601f81018690048602830186019096528582529294611aa494929386938581019392908301828280156107715780601f1061074657610100808354040283529160200191610771565b1515611aaf57600080fd5b600881015460ff1615611ac157600080fd5b600354611adb826003015443611f8b90919063ffffffff16565b11611ae557600080fd5b600b8101544311611af557600080fd5b6007810154600682015414611b0957600080fd5b600a8101805460ff191660011790819055610100900460ff161515611d1957600d810154600c820154604080517f5292a590000000000000000000000000000000000000000000000000000000008152600481019290925251600160a060020a0390921691635292a5909160248082019260009290919082900301818387803b158015611b9557600080fd5b505af1158015611ba9573d6000803e3d6000fd5b50508254611bc39250849150600160a060020a031661128d565b50805460408051848152600160a060020a03909216602083018190526080918301828152600180860180546002600019938216156101000293909301168290049486018590527f4549d9b7bb648611ec44e49083bbeeba3960dc743ea4f12e3998860c46da0b2e9588959193928801929091606083019060a084019086908015611c8e5780601f10611c6357610100808354040283529160200191611c8e565b820191906000526020600020905b815481529060010190602001808311611c7157829003601f168201915b5050838103825284546002600019610100600184161502019091160480825260209091019085908015611d025780601f10611cd757610100808354040283529160200191611d02565b820191906000526020600020905b815481529060010190602001808311611ce557829003601f168201915b5050965050505050505060405180910390a1611ef1565b600d810154600c820154604080517fdb1ee903000000000000000000000000000000000000000000000000000000008152600481019290925251600160a060020a039092169163db1ee9039160248082019260009290919082900301818387803b158015611d8657600080fd5b505af1158015611d9a573d6000803e3d6000fd5b5050825460408051868152600160a060020a03909216602083018190526080918301828152600180880180546002600019938216156101000293909301168290049486018590527f80cfc76da9b35fee5f480583330e1c8db05b6a2d6d27cc60cccae080c81076f8975089965092949293908801929190606083019060a084019086908015611e6a5780601f10611e3f57610100808354040283529160200191611e6a565b820191906000526020600020905b815481529060010190602001808311611e4d57829003601f168201915b5050838103825284546002600019610100600184161502019091160480825260209091019085908015611ede5780601f10611eb357610100808354040283529160200191611ede565b820191906000526020600020905b815481529060010190602001808311611ec157829003601f168201915b5050965050505050505060405180910390a15b5050565b60006020819052908152604090205481565b600160a060020a0382166000908152602081905260409020805482019081905530311015611f3457600080fd5b60408051600160a060020a03841681526020810183905281517fd15c9547ea5c06670c0010ce19bc32d54682a4b3801ece7f3ab0c3f17106b4bb929181900390910190a15050565b51600160a060020a0316151590565b600082821115611f9757fe5b50900390565b81810182811015611faa57fe5b92915050565b600083815260056020908152604080832081516101a0810183528154600160a060020a03168152600180830180548551600261010094831615949094026000190190911692909204601f81018790048702830187019095528482529294612040949293869385830193928301828280156107715780601f1061074657610100808354040283529160200191610771565b151561204b57600080fd5b600160a060020a03841660009081526020819052604090205483111561207057600080fd5b600160a060020a03841660009081526020818152604080832080548790039055600984019091529020546120aa908463ffffffff611f9d16565b600160a060020a038516600081815260098401602090815260409182902093909355805188815292830191909152818101859052517f5552f672574aa03647978f5573eebe6545f711fc59f43558a820e8d251c346619181900360600190a1600160a060020a03841660009081526009820160205260409020549150509392505050565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061216f5782800160ff1982351617855561219c565b8280016001018555821561219c579182015b8281111561219c578235825591602001919060010190612181565b506121a892915061221a565b5090565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106121ed57805160ff191683800117855561219c565b8280016001018555821561219c579182015b8281111561219c5782518255916020019190600101906121ff565b61058d91905b808211156121a857600081556001016122205600a165627a7a72305820259ce1b759c386d16d68e0a8284051618b7e3461ce1d50d2883631e51d4bc4ce0029";

    protected static final HashMap<String, String> _addresses;

    static {
        _addresses = new HashMap<>();
    }

    protected ClaimManager(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected ClaimManager(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public List<DepositBondedEventResponse> getDepositBondedEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("DepositBonded", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(event, transactionReceipt);
        ArrayList<DepositBondedEventResponse> responses = new ArrayList<DepositBondedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            DepositBondedEventResponse typedResponse = new DepositBondedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.claimID = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.account = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<DepositBondedEventResponse> depositBondedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("DepositBonded", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, DepositBondedEventResponse>() {
            @Override
            public DepositBondedEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);
                DepositBondedEventResponse typedResponse = new DepositBondedEventResponse();
                typedResponse.log = log;
                typedResponse.claimID = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.account = (String) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
                return typedResponse;
            }
        });
    }

    public List<DepositUnbondedEventResponse> getDepositUnbondedEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("DepositUnbonded", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(event, transactionReceipt);
        ArrayList<DepositUnbondedEventResponse> responses = new ArrayList<DepositUnbondedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            DepositUnbondedEventResponse typedResponse = new DepositUnbondedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.claimID = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.account = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<DepositUnbondedEventResponse> depositUnbondedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("DepositUnbonded", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, DepositUnbondedEventResponse>() {
            @Override
            public DepositUnbondedEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);
                DepositUnbondedEventResponse typedResponse = new DepositUnbondedEventResponse();
                typedResponse.log = log;
                typedResponse.claimID = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.account = (String) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
                return typedResponse;
            }
        });
    }

    public List<ClaimCreatedEventResponse> getClaimCreatedEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("ClaimCreated", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Address>() {}, new TypeReference<DynamicBytes>() {}, new TypeReference<DynamicBytes>() {}));
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(event, transactionReceipt);
        ArrayList<ClaimCreatedEventResponse> responses = new ArrayList<ClaimCreatedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ClaimCreatedEventResponse typedResponse = new ClaimCreatedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.claimID = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.claimant = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.plaintext = (byte[]) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.blockHash = (byte[]) eventValues.getNonIndexedValues().get(3).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<ClaimCreatedEventResponse> claimCreatedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("ClaimCreated", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Address>() {}, new TypeReference<DynamicBytes>() {}, new TypeReference<DynamicBytes>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, ClaimCreatedEventResponse>() {
            @Override
            public ClaimCreatedEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);
                ClaimCreatedEventResponse typedResponse = new ClaimCreatedEventResponse();
                typedResponse.log = log;
                typedResponse.claimID = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.claimant = (String) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.plaintext = (byte[]) eventValues.getNonIndexedValues().get(2).getValue();
                typedResponse.blockHash = (byte[]) eventValues.getNonIndexedValues().get(3).getValue();
                return typedResponse;
            }
        });
    }

    public List<ClaimChallengedEventResponse> getClaimChallengedEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("ClaimChallenged", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Address>() {}));
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(event, transactionReceipt);
        ArrayList<ClaimChallengedEventResponse> responses = new ArrayList<ClaimChallengedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ClaimChallengedEventResponse typedResponse = new ClaimChallengedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.claimID = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.challenger = (String) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<ClaimChallengedEventResponse> claimChallengedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("ClaimChallenged", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Address>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, ClaimChallengedEventResponse>() {
            @Override
            public ClaimChallengedEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);
                ClaimChallengedEventResponse typedResponse = new ClaimChallengedEventResponse();
                typedResponse.log = log;
                typedResponse.claimID = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.challenger = (String) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public List<SessionDecidedEventResponse> getSessionDecidedEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("SessionDecided", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Address>() {}, new TypeReference<Address>() {}));
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(event, transactionReceipt);
        ArrayList<SessionDecidedEventResponse> responses = new ArrayList<SessionDecidedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            SessionDecidedEventResponse typedResponse = new SessionDecidedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.sessionId = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.winner = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.loser = (String) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<SessionDecidedEventResponse> sessionDecidedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("SessionDecided", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Address>() {}, new TypeReference<Address>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, SessionDecidedEventResponse>() {
            @Override
            public SessionDecidedEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);
                SessionDecidedEventResponse typedResponse = new SessionDecidedEventResponse();
                typedResponse.log = log;
                typedResponse.sessionId = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.winner = (String) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.loser = (String) eventValues.getNonIndexedValues().get(2).getValue();
                return typedResponse;
            }
        });
    }

    public List<ClaimSuccessfulEventResponse> getClaimSuccessfulEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("ClaimSuccessful", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Address>() {}, new TypeReference<DynamicBytes>() {}, new TypeReference<DynamicBytes>() {}));
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(event, transactionReceipt);
        ArrayList<ClaimSuccessfulEventResponse> responses = new ArrayList<ClaimSuccessfulEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ClaimSuccessfulEventResponse typedResponse = new ClaimSuccessfulEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.claimID = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.claimant = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.plaintext = (byte[]) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.blockHash = (byte[]) eventValues.getNonIndexedValues().get(3).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<ClaimSuccessfulEventResponse> claimSuccessfulEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("ClaimSuccessful", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Address>() {}, new TypeReference<DynamicBytes>() {}, new TypeReference<DynamicBytes>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, ClaimSuccessfulEventResponse>() {
            @Override
            public ClaimSuccessfulEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);
                ClaimSuccessfulEventResponse typedResponse = new ClaimSuccessfulEventResponse();
                typedResponse.log = log;
                typedResponse.claimID = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.claimant = (String) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.plaintext = (byte[]) eventValues.getNonIndexedValues().get(2).getValue();
                typedResponse.blockHash = (byte[]) eventValues.getNonIndexedValues().get(3).getValue();
                return typedResponse;
            }
        });
    }

    public List<ClaimFailedEventResponse> getClaimFailedEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("ClaimFailed", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Address>() {}, new TypeReference<DynamicBytes>() {}, new TypeReference<DynamicBytes>() {}));
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(event, transactionReceipt);
        ArrayList<ClaimFailedEventResponse> responses = new ArrayList<ClaimFailedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ClaimFailedEventResponse typedResponse = new ClaimFailedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.claimID = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.claimant = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.plaintext = (byte[]) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.blockHash = (byte[]) eventValues.getNonIndexedValues().get(3).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<ClaimFailedEventResponse> claimFailedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("ClaimFailed", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Address>() {}, new TypeReference<DynamicBytes>() {}, new TypeReference<DynamicBytes>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, ClaimFailedEventResponse>() {
            @Override
            public ClaimFailedEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);
                ClaimFailedEventResponse typedResponse = new ClaimFailedEventResponse();
                typedResponse.log = log;
                typedResponse.claimID = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.claimant = (String) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.plaintext = (byte[]) eventValues.getNonIndexedValues().get(2).getValue();
                typedResponse.blockHash = (byte[]) eventValues.getNonIndexedValues().get(3).getValue();
                return typedResponse;
            }
        });
    }

    public List<VerificationGameStartedEventResponse> getVerificationGameStartedEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("VerificationGameStarted", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Address>() {}, new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(event, transactionReceipt);
        ArrayList<VerificationGameStartedEventResponse> responses = new ArrayList<VerificationGameStartedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            VerificationGameStartedEventResponse typedResponse = new VerificationGameStartedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.claimID = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.claimant = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.challenger = (String) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.sessionId = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<VerificationGameStartedEventResponse> verificationGameStartedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("VerificationGameStarted", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Address>() {}, new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, VerificationGameStartedEventResponse>() {
            @Override
            public VerificationGameStartedEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);
                VerificationGameStartedEventResponse typedResponse = new VerificationGameStartedEventResponse();
                typedResponse.log = log;
                typedResponse.claimID = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.claimant = (String) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.challenger = (String) eventValues.getNonIndexedValues().get(2).getValue();
                typedResponse.sessionId = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
                return typedResponse;
            }
        });
    }

    public List<ClaimVerificationGamesEndedEventResponse> getClaimVerificationGamesEndedEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("ClaimVerificationGamesEnded", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(event, transactionReceipt);
        ArrayList<ClaimVerificationGamesEndedEventResponse> responses = new ArrayList<ClaimVerificationGamesEndedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ClaimVerificationGamesEndedEventResponse typedResponse = new ClaimVerificationGamesEndedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.claimID = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<ClaimVerificationGamesEndedEventResponse> claimVerificationGamesEndedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("ClaimVerificationGamesEnded", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, ClaimVerificationGamesEndedEventResponse>() {
            @Override
            public ClaimVerificationGamesEndedEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);
                ClaimVerificationGamesEndedEventResponse typedResponse = new ClaimVerificationGamesEndedEventResponse();
                typedResponse.log = log;
                typedResponse.claimID = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public List<DepositMadeEventResponse> getDepositMadeEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("DepositMade", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(event, transactionReceipt);
        ArrayList<DepositMadeEventResponse> responses = new ArrayList<DepositMadeEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            DepositMadeEventResponse typedResponse = new DepositMadeEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.who = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<DepositMadeEventResponse> depositMadeEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("DepositMade", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, DepositMadeEventResponse>() {
            @Override
            public DepositMadeEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);
                DepositMadeEventResponse typedResponse = new DepositMadeEventResponse();
                typedResponse.log = log;
                typedResponse.who = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public List<DepositWithdrawnEventResponse> getDepositWithdrawnEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("DepositWithdrawn", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(event, transactionReceipt);
        ArrayList<DepositWithdrawnEventResponse> responses = new ArrayList<DepositWithdrawnEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            DepositWithdrawnEventResponse typedResponse = new DepositWithdrawnEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.who = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<DepositWithdrawnEventResponse> depositWithdrawnEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("DepositWithdrawn", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, DepositWithdrawnEventResponse>() {
            @Override
            public DepositWithdrawnEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);
                DepositWithdrawnEventResponse typedResponse = new DepositWithdrawnEventResponse();
                typedResponse.log = log;
                typedResponse.who = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }


    public RemoteCall<BigInteger> defaultChallengeTimeout() {
        final Function function = new Function("defaultChallengeTimeout", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> withdrawDeposit(BigInteger amount) {
        final Function function = new Function(
                "withdrawDeposit", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> makeDeposit(BigInteger weiValue) {
        final Function function = new Function(
                "makeDeposit", 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteCall<BigInteger> minDeposit() {
        final Function function = new Function("minDeposit", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> getDeposit(String who) {
        final Function function = new Function("getDeposit", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(who)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> deposits(String param0) {
        final Function function = new Function("deposits", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> getBondedDeposit(BigInteger claimID, String account) {
        final Function function = new Function("getBondedDeposit", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(claimID), 
                new org.web3j.abi.datatypes.Address(account)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> unbondDeposit(BigInteger claimID, String account) {
        final Function function = new Function(
                "unbondDeposit", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(claimID), 
                new org.web3j.abi.datatypes.Address(account)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> calcId(byte[] param0, byte[] _hash, String claimant, byte[] _proposalId) {
        final Function function = new Function("calcId", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicBytes(param0), 
                new org.web3j.abi.datatypes.generated.Bytes32(_hash), 
                new org.web3j.abi.datatypes.Address(claimant), 
                new org.web3j.abi.datatypes.generated.Bytes32(_proposalId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }


    public RemoteCall<TransactionReceipt> challengeClaim(BigInteger claimID) {
        final Function function = new Function(
                "challengeClaim", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(claimID)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> runNextVerificationGame(BigInteger claimID) {
        final Function function = new Function(
                "runNextVerificationGame", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(claimID)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> sessionDecided(BigInteger sessionId, BigInteger claimID, String winner, String loser) {
        final Function function = new Function(
                "sessionDecided", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(sessionId), 
                new org.web3j.abi.datatypes.generated.Uint256(claimID), 
                new org.web3j.abi.datatypes.Address(winner), 
                new org.web3j.abi.datatypes.Address(loser)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> checkClaimSuccessful(BigInteger claimID) {
        final Function function = new Function(
                "checkClaimSuccessful", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(claimID)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> firstChallenger(BigInteger claimID) {
        final Function function = new Function("firstChallenger", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(claimID)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<BigInteger> createdAt(BigInteger claimID) {
        final Function function = new Function("createdAt", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(claimID)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> getSession(BigInteger claimID, String challenger) {
        final Function function = new Function("getSession", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(claimID), 
                new org.web3j.abi.datatypes.Address(challenger)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<List> getChallengers(BigInteger claimID) {
        final Function function = new Function("getChallengers", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(claimID)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Address>>() {}));
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

    public RemoteCall<String> getCurrentChallenger(BigInteger claimID) {
        final Function function = new Function("getCurrentChallenger", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(claimID)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<Boolean> getVerificationOngoing(BigInteger claimID) {
        final Function function = new Function("getVerificationOngoing", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(claimID)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<Tuple4<String, byte[], byte[], byte[]>> getClaim(BigInteger claimID) {
        final Function function = new Function("getClaim", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(claimID)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<DynamicBytes>() {}, new TypeReference<DynamicBytes>() {}, new TypeReference<Bytes32>() {}));
        return new RemoteCall<Tuple4<String, byte[], byte[], byte[]>>(
                new Callable<Tuple4<String, byte[], byte[], byte[]>>() {
                    @Override
                    public Tuple4<String, byte[], byte[], byte[]> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple4<String, byte[], byte[], byte[]>(
                                (String) results.get(0).getValue(), 
                                (byte[]) results.get(1).getValue(), 
                                (byte[]) results.get(2).getValue(), 
                                (byte[]) results.get(3).getValue());
                    }
                });
    }

    public RemoteCall<Boolean> getClaimReady(BigInteger claimID) {
        final Function function = new Function("getClaimReady", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(claimID)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public static ClaimManager load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new ClaimManager(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static ClaimManager load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new ClaimManager(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected String getStaticDeployedAddress(String networkId) {
        return _addresses.get(networkId);
    }

    public static String getPreviouslyDeployedAddress(String networkId) {
        return _addresses.get(networkId);
    }

    public static class DepositBondedEventResponse {
        public Log log;

        public BigInteger claimID;

        public String account;

        public BigInteger amount;
    }

    public static class DepositUnbondedEventResponse {
        public Log log;

        public BigInteger claimID;

        public String account;

        public BigInteger amount;
    }

    public static class ClaimCreatedEventResponse {
        public Log log;

        public BigInteger claimID;

        public String claimant;

        public byte[] plaintext;

        public byte[] blockHash;
    }

    public static class ClaimChallengedEventResponse {
        public Log log;

        public BigInteger claimID;

        public String challenger;
    }

    public static class SessionDecidedEventResponse {
        public Log log;

        public BigInteger sessionId;

        public String winner;

        public String loser;
    }

    public static class ClaimSuccessfulEventResponse {
        public Log log;

        public BigInteger claimID;

        public String claimant;

        public byte[] plaintext;

        public byte[] blockHash;
    }

    public static class ClaimFailedEventResponse {
        public Log log;

        public BigInteger claimID;

        public String claimant;

        public byte[] plaintext;

        public byte[] blockHash;
    }

    public static class VerificationGameStartedEventResponse {
        public Log log;

        public BigInteger claimID;

        public String claimant;

        public String challenger;

        public BigInteger sessionId;
    }

    public static class ClaimVerificationGamesEndedEventResponse {
        public Log log;

        public BigInteger claimID;
    }

    public static class DepositMadeEventResponse {
        public Log log;

        public String who;

        public BigInteger amount;
    }

    public static class DepositWithdrawnEventResponse {
        public Log log;

        public String who;

        public BigInteger amount;
    }
}
