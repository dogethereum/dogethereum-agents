package org.dogethereum.dogesubmitter.contract;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.web3j.abi.*;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.StaticArray9;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.exceptions.ContractCallException;
import rx.Observable;
import rx.functions.Func1;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 3.2.0.
 */
public class DogeRelay extends Contract {
    private static final String BINARY = "0x6060604052606460055561010060065562010000600755630100000060085564010000000060095565010000000000600a556601000000000000600b55670100000000000000600c5568010000000000000000600d556901000000000000000000600e556a0100000000000000000000600f556b0100000000000000000000006010556c010000000000000000000000006011556d01000000000000000000000000006012556e0100000000000000000000000000006013556f01000000000000000000000000000000601455700100000000000000000000000000000000601555710100000000000000000000000000000000006016557201000000000000000000000000000000000000601755730100000000000000000000000000000000000000601855740100000000000000000000000000000000000000006019557501000000000000000000000000000000000000000000601a55760100000000000000000000000000000000000000000000601b5577010000000000000000000000000000000000000000000000601c557801000000000000000000000000000000000000000000000000601d55790100000000000000000000000000000000000000000000000000601e557a010000000000000000000000000000000000000000000000000000601f557b010000000000000000000000000000000000000000000000000000006020557c01000000000000000000000000000000000000000000000000000000006021557d0100000000000000000000000000000000000000000000000000000000006022557e010000000000000000000000000000000000000000000000000000000000006023557f0100000000000000000000000000000000000000000000000000000000000000602455341561029f57600080fd5b611e7c806102ae6000396000f30060606040526004361061008e576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff1680634934727214610093578063541e9cd7146100bc5780636e2a8e7c14610125578063922407ca1461019f5780639be7076a146101f0578063aa86303614610219578063c32e6af0146102dc578063f9a90d54146103be575b600080fd5b341561009e57600080fd5b6100a661047f565b6040518082815260200191505060405180910390f35b34156100c757600080fd5b61010b600480803590602001909190803567ffffffffffffffff169060200190919080356fffffffffffffffffffffffffffffffff16906020019091905050610489565b604051808215151515815260200191505060405180910390f35b341561013057600080fd5b610189600480803590602001908201803590602001908080601f016020809104026020016040519081016040528093929190818152602001838380828437820191505050505050919080359060200190919050506104ce565b6040518082815260200191505060405180910390f35b34156101aa57600080fd5b6101b2610805565b6040518082600960200280838360005b838110156101dd5780820151818401526020810190506101c2565b5050505090500191505060405180910390f35b34156101fb57600080fd5b6102036108a2565b6040518082815260200191505060405180910390f35b341561022457600080fd5b6102c6600480803590602001908201803590602001908080601f01602080910402602001604051908101604052809392919081815260200183838082843782019150505050505091908035906020019091908035906020019082018035906020019080806020026020016040519081016040528093929190818152602001838360200280828437820191505050505050919080359060200190919050506108be565b6040518082815260200191505060405180910390f35b34156102e757600080fd5b6103a8600480803590602001908201803590602001908080601f016020809104026020016040519081016040528093929190818152602001838380828437820191505050505050919080359060200190919080359060200190820180359060200190808060200260200160405190810160405280939291908181526020018383602002808284378201915050505050509190803590602001909190803573ffffffffffffffffffffffffffffffffffffffff16906020019091905050610950565b6040518082815260200191505060405180910390f35b34156103c957600080fd5b610469600480803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509190803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509190803561ffff16906020019091905050610b08565b6040518082815260200191505060405180910390f35b6000600354905090565b60008060045414151561049f57600090506104c7565b6001600481905550836003819055506104b88484610bea565b6104c28483610c33565b600190505b9392505050565b6000806000806000806000806000806104f26104ed8d60006050610c7c565b610ce9565b98506104fd8c610dcf565b975061050888610dea565b96506000876fffffffffffffffffffffffffffffffff16141561056f577ff2b901c96647f9fa94c3ae5e139812621ac0dbdb2e9d5006404fbe0161db04508961272e604051808381526020018281526020019250505060405180910390a1600099506107f6565b600061057a8a610dea565b6fffffffffffffffffffffffffffffffff161415156105dd577ff2b901c96647f9fa94c3ae5e139812621ac0dbdb2e9d5006404fbe0161db045089612738604051808381526020018281526020019250505060405180910390a1600099506107f6565b6105e68c610e1b565b95506105f186610e4c565b94506105fc88610e90565b60010167ffffffffffffffff16935061061488610ebd565b925061061f84610f0d565b158061064357506000600160009054906101000a900463ffffffff1663ffffffff16145b156106bf578263ffffffff168663ffffffff161415801561066b575060008363ffffffff1614155b156106ba577ff2b901c96647f9fa94c3ae5e139812621ac0dbdb2e9d5006404fbe0161db04508961271a604051808381526020018281526020019250505060405180910390a1600099506107f6565b6106fc565b6106f96106cb89610f25565b63ffffffff166106e56106e060018803610f75565b610f25565b63ffffffff166106f486610e4c565b611019565b91505b61070689896110c6565b6107138c60006050610c7c565b600260008b8152602001908152602001600020600001908051906020019061073c929190611d5a565b50847d0fffff00000000000000000000000000000000000000000000000000000081151561076657fe5b04870190506107758982610c33565b600454816fffffffffffffffffffffffffffffffff161015156107b35788600381905550806fffffffffffffffffffffffffffffffff166004819055505b7ff2b901c96647f9fa94c3ae5e139812621ac0dbdb2e9d5006404fbe0161db04508985604051808381526020018281526020019250505060405180910390a18399505b50505050505050505092915050565b61080d611dda565b600080600060035492508284600060098110151561082757fe5b602002018181525050600091505b600860ff168260ff161015610899576000806108518585611229565b63ffffffff1663ffffffff16815260200190815260200160002054905080846001840160ff1660098110151561088357fe5b6020020181815250508180600101925050610835565b83935050505090565b60006108af600354610e90565b67ffffffffffffffff16905090565b60008060006108cc87610ce9565b9150604087511415610922577f7a2933ac2a256db068a8aec8c8977f9866040648de7e973edfb5387e64a3d66a82614e52604051808381526020018281526020019250505060405180910390a160009250610946565b61092e82878787611262565b9050600181141561094157819250610946565b600092505b5050949350505050565b6000806000610961888888886108be565b9150600082141515610ab5578373ffffffffffffffffffffffffffffffffffffffff16631c0b636789846000604051602001526040518363ffffffff167c01000000000000000000000000000000000000000000000000000000000281526004018080602001838152602001828103825284818151815260200191508051906020019080838360005b83811015610a055780820151818401526020810190506109ea565b50505050905090810190601f168015610a325780820380516001836020036101000a031916815260200191505b509350505050602060405180830381600087803b1515610a5157600080fd5b6102c65a03f11515610a6257600080fd5b5050506040518051905090507f84ed50b3e2027837051c6eaa0464f66293959fe6892b7a62576d1ebcd53931e18282604051808381526020018281526020019250505060405180910390a1809250610afd565b7f84ed50b3e2027837051c6eaa0464f66293959fe6892b7a62576d1ebcd53931e1600061753a604051808381526020018281526020019250505060405180910390a161753a92505b505095945050505050565b6000806000806000806000610b1b611e03565b6000610b25611e03565b610b2d611e03565b600060209a506000995060049850600097508a60ff169650600095505b8c61ffff168661ffff161015610bd857610b658f8b8b610c7c565b9450610b708561137b565b935060048a0199508389019850610b888f8b8b610c7c565b9250610b958e8989610c7c565b9150610ba08261158a565b600190049050610bb083826104ce565b9b50838a0199506004890198508a60ff16880197508a60ff1687019650600186019550610b4a565b50505050505050505050509392505050565b600060026000848152602001908152602001600020600101549050610c1181600084611665565b9050806002600085815260200190815260200160002060010181905550505050565b600060026000848152602001908152602001600020600101549050610c5a816010846116f3565b9050806002600085815260200190815260200160002060010181905550505050565b610c84611e03565b6000610c8e611e03565b84840363ffffffff16915081604051805910610ca75750595b9080825280601f01601f191660200182016040525090508160208201838760208a010160006004600019f11515610cdd57600080fd5b80925050509392505050565b6000610dc8600280846000604051602001526040518082805190602001908083835b602083101515610d305780518252602082019150602081019050602083039250610d0b565b6001836020036101000a03801982511681845116808217855250505050505090500191505060206040518083038160008661646e5a03f11515610d7257600080fd5b50506040518051905060006040516020015260405180826000191660001916815260200191505060206040518083038160008661646e5a03f11515610db657600080fd5b505060405180519050600190046117d9565b9050919050565b60008060248301519050610de2816117d9565b915050919050565b6000601554601554600260008581526020019081526020016000206001015402811515610e1357fe5b049050919050565b60006050820151630100000081601b1a026201000082601a1a02016101008260191a02018160181a01915050919050565b600080600063010000008463ffffffff16811515610e6657fe5b0463ffffffff16915062ffffff841663ffffffff169050600382036101000a810292505050919050565b6000601d546002600084815260200190815260200160002060010154811515610eb557fe5b049050919050565b600080610ede6002600085815260200190815260200160002060000161183a565b905060028101548060081a8160091a600654020181600a1a600754020181600b1a600854020192505050919050565b600080600183811515610f1c57fe5b06149050919050565b600080610f466002600085815260200190815260200160002060000161183a565b905060028101548060041a8160051a60065402018160061a60075402018160071a600854020192505050919050565b60008060006003549150600160080390505b83610f9183610e90565b67ffffffffffffffff16111561100f575b610fab8161186d565b84610fb584610e90565b67ffffffffffffffff1603108015610fd0575060008160ff16115b15610fe057600181039050610fa2565b600080610fed8484611229565b63ffffffff1663ffffffff168152602001908152602001600020549150610f87565b8192505050919050565b600080600084860391506004603c81151561103057fe5b04821015611049576004603c81151561104557fe5b0491505b6004603c0282111561105d576004603c0291505b603c84830281151561106b57fe5b0490507bffffffffffffffffffffffffffffffffffffffffffffffffffffffff8111156110b2577bffffffffffffffffffffffffffffffffffffffffffffffffffffffff90505b6110bb8161187d565b925050509392505050565b60008060008085600080600160009054906101000a900463ffffffff1663ffffffff1663ffffffff1681526020019081526020016000208190555061111d86600160009054906101000a900463ffffffff1661192c565b60018060008282829054906101000a900463ffffffff160192506101000a81548163ffffffff021916908363ffffffff16021790555061116886600161116288610e90565b01610bea565b6000935061117585611980565b9250611183846000856119b1565b9350600191505b600860ff168260ff161015611206576111a28261186d565b90506001816111b088610e90565b67ffffffffffffffff168115156111c357fe5b0614156111df576111d88483600402856119b1565b93506111f9565b6111f684836004026111f18886611229565b6119b1565b93505b818060010192505061118a565b836002600088815260200190815260200160002060020181905550505050505050565b60006021548260ff1660200260020a60026000868152602001908152602001600020600201540281151561125957fe5b04905092915050565b600080600061127084611a13565b156112c0577f7a2933ac2a256db068a8aec8c8977f9866040648de7e973edfb5387e64a3d66a87614e34604051808381526020018281526020019250505060405180910390a1614e349250611371565b6112cb878787611a61565b91506112d684611b06565b9050808214151561132c577f7a2933ac2a256db068a8aec8c8977f9866040648de7e973edfb5387e64a3d66a87614e48604051808381526020018281526020019250505060405180910390a1614e489250611371565b7f7a2933ac2a256db068a8aec8c8977f9866040648de7e973edfb5387e64a3d66a876001604051808381526020018281526020019250505060405180910390a1600192505b5050949350505050565b600081600381518110151561138c57fe5b9060200101517f010000000000000000000000000000000000000000000000000000000000000090047f0100000000000000000000000000000000000000000000000000000000000000027f0100000000000000000000000000000000000000000000000000000000000000900461010083600281518110151561140c57fe5b9060200101517f010000000000000000000000000000000000000000000000000000000000000090047f0100000000000000000000000000000000000000000000000000000000000000027f01000000000000000000000000000000000000000000000000000000000000009004026201000084600181518110151561148e57fe5b9060200101517f010000000000000000000000000000000000000000000000000000000000000090047f0100000000000000000000000000000000000000000000000000000000000000027f0100000000000000000000000000000000000000000000000000000000000000900402630100000085600081518110151561151157fe5b9060200101517f010000000000000000000000000000000000000000000000000000000000000090047f0100000000000000000000000000000000000000000000000000000000000000027f01000000000000000000000000000000000000000000000000000000000000009004020101019050919050565b60008060008090505b602081101561165b576008810260ff7f01000000000000000000000000000000000000000000000000000000000000000285838151811015156115d257fe5b9060200101517f010000000000000000000000000000000000000000000000000000000000000090047f010000000000000000000000000000000000000000000000000000000000000002167effffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916600019169060020a9004821791508080600101915050611593565b8192505050919050565b600061166f611e17565b600060206040519081016040528087815250915061168c82611b60565b90508360181a858201538360191a6001860182015383601a1a6002860182015383601b1a6003860182015383601c1a6004860182015383601d1a6005860182015383601e1a6006860182015383601f1a600786018201538160000151925050509392505050565b60006116fd611e17565b600060206040519081016040528087815250915061171a82611b60565b90508360101a858201538360111a600186018201538360121a600286018201538360131a600386018201538360141a600486018201538360151a600586018201538360161a600686018201538360171a600786018201538360181a600886018201538360191a6009860182015383601a1a600a860182015383601b1a600b860182015383601c1a600c860182015383601d1a600d860182015383601e1a600e860182015383601f1a600f86018201538160000151925050509392505050565b6000806117e4611e17565b60008092506020604051908101604052806000815250915061180582611b60565b90505b60208360ff16101561182b578483601f031a838201538280600101935050611808565b81600001519350505050919050565b60008082905080600102604051808260001916600019168152602001915050604051809103902060019004915050919050565b60008160ff1660050a9050919050565b600080600061189b600761189086611b6a565b0160ff166003611b98565b91506000905060038260ff161115156118ca576118c362ffffff851683600303600802611bb4565b90506118e5565b6118da8460038403600802611b98565b905062ffffff811690505b600062800000821663ffffffff1611156119135761190a8163ffffffff166008611b98565b90506001820191505b6119218260ff166018611bb4565b811792505050919050565b600080600260008581526020019081526020016000206001015491508263ffffffff16905061195d82600883611665565b915081600260008681526020019081526020016000206001018190555050505050565b6000601d54600d546002600085815260200190815260200160002060010154028115156119a957fe5b049050919050565b60006119bb611e17565b60006020604051908101604052808781525091506119d882611b60565b905083601c1a8582015383601d1a6001860182015383601e1a6002860182015383601f1a600386018201538160000151925050509392505050565b60008060006003549150600090505b60068160ff161015611a555781841415611a3f5760019250611a5a565b611a4882611bc7565b9150600181019050611a22565b600092505b5050919050565b6000806000806000806000808a965088519550600094505b85851015611af5578885815181101515611a8f57fe5b90602001906020020151935060028a811515611aa757fe5b0692506001831415611abe57839150869050611acf565b6000831415611ace578691508390505b5b611ad98282611c1e565b965060028a811515611ae757fe5b049950600185019450611a79565b869750505050505050509392505050565b600080600080611b2a6002600087815260200190815260200160002060000161183a565b92506001830154915060028301549050611b5660215482811515611b4a57fe5b046009548402016117d9565b9350505050919050565b6000819050919050565b6000808290505b6000811115611b9257611b85816001611b98565b9050600182019150611b71565b50919050565b60008160ff1660020a83811515611bab57fe5b04905092915050565b60008160ff1660020a8302905092915050565b600080600080611beb6002600087815260200190815260200160002060000161183a565b92508254915060018301549050611c1460215482811515611c0857fe5b046009548402016117d9565b9350505050919050565b6000611c28611e03565b60008060408051805910611c395750595b9080825280601f01601f19166020018201604052509250611c59866117d9565b9150611c64856117d9565b9050816020840152806040840152611d4f600280856000604051602001526040518082805190602001908083835b602083101515611cb75780518252602082019150602081019050602083039250611c92565b6001836020036101000a03801982511681845116808217855250505050505090500191505060206040518083038160008661646e5a03f11515611cf957600080fd5b50506040518051905060006040516020015260405180826000191660001916815260200191505060206040518083038160008661646e5a03f11515611d3d57600080fd5b505060405180519050600190046117d9565b935050505092915050565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f10611d9b57805160ff1916838001178555611dc9565b82800160010185558215611dc9579182015b82811115611dc8578251825591602001919060010190611dad565b5b509050611dd69190611e2b565b5090565b610120604051908101604052806009905b6000815260200190600190039081611deb5790505090565b602060405190810160405280600081525090565b602060405190810160405280600081525090565b611e4d91905b80821115611e49576000816000905550600101611e31565b5090565b905600a165627a7a72305820d2bce1c5357f4dfd624edc63e3999a328fbf44589768d5d594dbeb51de4fe6200029";

    protected static final HashMap<String, String> _addresses;

    static {
        _addresses = new HashMap<>();
    }

    protected DogeRelay(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected DogeRelay(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public List<StoreHeaderEventResponse> getStoreHeaderEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("StoreHeader", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<StoreHeaderEventResponse> responses = new ArrayList<StoreHeaderEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            StoreHeaderEventResponse typedResponse = new StoreHeaderEventResponse();
            typedResponse.blockHash = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.returnCode = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<StoreHeaderEventResponse> storeHeaderEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("StoreHeader", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, StoreHeaderEventResponse>() {
            @Override
            public StoreHeaderEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                StoreHeaderEventResponse typedResponse = new StoreHeaderEventResponse();
                typedResponse.blockHash = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.returnCode = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public List<GetHeaderEventResponse> getGetHeaderEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("GetHeader", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<GetHeaderEventResponse> responses = new ArrayList<GetHeaderEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            GetHeaderEventResponse typedResponse = new GetHeaderEventResponse();
            typedResponse.blockHash = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.returnCode = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<GetHeaderEventResponse> getHeaderEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("GetHeader", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, GetHeaderEventResponse>() {
            @Override
            public GetHeaderEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                GetHeaderEventResponse typedResponse = new GetHeaderEventResponse();
                typedResponse.blockHash = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.returnCode = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public List<VerifyTransactionEventResponse> getVerifyTransactionEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("VerifyTransaction", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<VerifyTransactionEventResponse> responses = new ArrayList<VerifyTransactionEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            VerifyTransactionEventResponse typedResponse = new VerifyTransactionEventResponse();
            typedResponse.txHash = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.returnCode = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<VerifyTransactionEventResponse> verifyTransactionEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("VerifyTransaction", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, VerifyTransactionEventResponse>() {
            @Override
            public VerifyTransactionEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                VerifyTransactionEventResponse typedResponse = new VerifyTransactionEventResponse();
                typedResponse.txHash = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.returnCode = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public List<RelayTransactionEventResponse> getRelayTransactionEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("RelayTransaction", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<RelayTransactionEventResponse> responses = new ArrayList<RelayTransactionEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            RelayTransactionEventResponse typedResponse = new RelayTransactionEventResponse();
            typedResponse.txHash = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.returnCode = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<RelayTransactionEventResponse> relayTransactionEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("RelayTransaction", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, RelayTransactionEventResponse>() {
            @Override
            public RelayTransactionEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                RelayTransactionEventResponse typedResponse = new RelayTransactionEventResponse();
                typedResponse.txHash = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.returnCode = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public RemoteCall<BigInteger> getBestBlockHash() {
        Function function = new Function("getBestBlockHash", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> getBestBlockHash(DefaultBlockParameter defaultBlockParameter) {
        Function function = new Function("getBestBlockHash",
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class, defaultBlockParameter);
    }

    public RemoteCall<TransactionReceipt> setInitialParent(BigInteger blockHash, BigInteger height, BigInteger chainWork) {
        Function function = new Function(
                "setInitialParent", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(blockHash), 
                new org.web3j.abi.datatypes.generated.Uint64(height), 
                new org.web3j.abi.datatypes.generated.Uint128(chainWork)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> storeBlockHeader(byte[] blockHeaderBytes, BigInteger proposedScryptBlockHash) {
        Function function = new Function(
                "storeBlockHeader", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicBytes(blockHeaderBytes), 
                new org.web3j.abi.datatypes.generated.Uint256(proposedScryptBlockHash)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<List> getBlockLocator() {
        Function function = new Function("getBlockLocator", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<StaticArray9<Uint256>>() {}));
        return executeRemoteCallSingleValueReturn(function, List.class);
    }

    public RemoteCall<List> getBlockLocator(DefaultBlockParameter defaultBlockParameter) {
        Function function = new Function("getBlockLocator",
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<StaticArray9<Uint256>>() {}));
        return executeRemoteCallSingleValueReturn(function, List.class, defaultBlockParameter);
    }

    public RemoteCall<BigInteger> getBestBlockHeight() {
        Function function = new Function("getBestBlockHeight", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> getBestBlockHeight(DefaultBlockParameter defaultBlockParameter) {
        Function function = new Function("getBestBlockHeight",
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class, defaultBlockParameter);
    }

    public RemoteCall<TransactionReceipt> verifyTx(byte[] txBytes, BigInteger txIndex, List<BigInteger> siblings, BigInteger txBlockHash) {
        Function function = new Function(
                "verifyTx", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicBytes(txBytes), 
                new org.web3j.abi.datatypes.generated.Uint256(txIndex), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint256>(
                        org.web3j.abi.Utils.typeMap(siblings, org.web3j.abi.datatypes.generated.Uint256.class)), 
                new org.web3j.abi.datatypes.generated.Uint256(txBlockHash)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> relayTx(byte[] txBytes, BigInteger txIndex, List<BigInteger> siblings, BigInteger txBlockHash, String targetContract) {
        Function function = new Function(
                "relayTx", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicBytes(txBytes), 
                new org.web3j.abi.datatypes.generated.Uint256(txIndex), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint256>(
                        org.web3j.abi.Utils.typeMap(siblings, org.web3j.abi.datatypes.generated.Uint256.class)), 
                new org.web3j.abi.datatypes.generated.Uint256(txBlockHash), 
                new org.web3j.abi.datatypes.Address(targetContract)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> bulkStoreHeaders(byte[] headersBytes, byte[] hashesBytes, BigInteger count) {
        Function function = new Function(
                "bulkStoreHeaders", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicBytes(headersBytes), 
                new org.web3j.abi.datatypes.DynamicBytes(hashesBytes), 
                new org.web3j.abi.datatypes.generated.Uint16(count)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public static RemoteCall<DogeRelay> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(DogeRelay.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<DogeRelay> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(DogeRelay.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static DogeRelay load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new DogeRelay(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static DogeRelay load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new DogeRelay(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected String getStaticDeployedAddress(String networkId) {
        return _addresses.get(networkId);
    }

    public static String getPreviouslyDeployedAddress(String networkId) {
        return _addresses.get(networkId);
    }

    public static class StoreHeaderEventResponse {
        public BigInteger blockHash;

        public BigInteger returnCode;
    }

    public static class GetHeaderEventResponse {
        public BigInteger blockHash;

        public BigInteger returnCode;
    }

    public static class VerifyTransactionEventResponse {
        public BigInteger txHash;

        public BigInteger returnCode;
    }

    public static class RelayTransactionEventResponse {
        public BigInteger txHash;

        public BigInteger returnCode;
    }

    protected <T> RemoteCall<T> executeRemoteCallSingleValueReturn(
            Function function, Class<T> returnType, DefaultBlockParameter defaultBlockParameter) {
        return new RemoteCall<>(() -> executeCallSingleValueReturn(function, returnType, defaultBlockParameter));
    }

    @SuppressWarnings("unchecked")
    protected <T extends Type, R> R executeCallSingleValueReturn(
            Function function, Class<R> returnType, DefaultBlockParameter defaultBlockParameter) throws IOException {
        T result = executeCallSingleValueReturn(function, defaultBlockParameter);
        if (result == null) {
            throw new ContractCallException("Empty value (0x) returned from contract");
        }

        Object value = result.getValue();
        if (returnType.isAssignableFrom(value.getClass())) {
            return (R) value;
        } else if (result.getClass().equals(Address.class) && returnType.equals(String.class)) {
            return (R) result.toString();  // cast isn't necessary
        } else {
            throw new ContractCallException(
                    "Unable to convert response: " + value
                            + " to expected type: " + returnType.getSimpleName());
        }
    }

    @SuppressWarnings("unchecked")
    protected <T extends Type> T executeCallSingleValueReturn(
            Function function, DefaultBlockParameter defaultBlockParameter) throws IOException {
        List<Type> values = executeCall(function, defaultBlockParameter);
        if (!values.isEmpty()) {
            return (T) values.get(0);
        } else {
            return null;
        }
    }

    private List<Type> executeCall(
            Function function, DefaultBlockParameter defaultBlockParameter) throws IOException {
        String encodedFunction = FunctionEncoder.encode(function);
        org.web3j.protocol.core.methods.response.EthCall ethCall = web3j.ethCall(
                Transaction.createEthCallTransaction(
                        transactionManager.getFromAddress(), contractAddress, encodedFunction),
                defaultBlockParameter)
                .send();

        String value = ethCall.getValue();
        return FunctionReturnDecoder.decode(value, function.getOutputParameters());
    }
}
