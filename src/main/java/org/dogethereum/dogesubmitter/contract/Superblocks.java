package org.dogethereum.dogesubmitter.contract;

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
import org.web3j.tuples.generated.Tuple7;
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
public class Superblocks extends Contract {
    private static final String BINARY = "0x608060405234801561001057600080fd5b506040516020806122da8339810180604052810190808051906020019092919050505080600660006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555050612256806100846000396000f300608060405260043610610112576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff168063282c1411146101175780632da8cffd1461019b5780632e400191146101e05780633ce90e8f14610231578063642ed9881461027e57806369ecc3cf146102cb5780636e5b70711461031f578063797af627146103e05780637b34dcd91461043457806387a4d38214610481578063a9a36dcd146104d2578063b1522eb614610529578063b6da2144146105a6578063b8558d12146105e9578063cffd46dc1461068d578063d93a05e7146106e1578063f06d520d14610735578063f6f3238a14610768578063f9b5d7c0146107bb578063fb338e201461083d575b600080fd5b34801561012357600080fd5b506101766004803603810190808035600019169060200190929190803590602001909291908035906020019092919080356000191690602001909291908035600019169060200190929190505050610894565b6040518083815260200182600019166000191681526020019250505060405180910390f35b3480156101a757600080fd5b506101ca6004803603810190808035600019169060200190929190505050610bc6565b6040518082815260200191505060405180910390f35b3480156101ec57600080fd5b5061020f6004803603810190808035600019169060200190929190505050610bed565b604051808263ffffffff1663ffffffff16815260200191505060405180910390f35b34801561023d57600080fd5b506102606004803603810190808035600019169060200190929190505050610c24565b60405180826000191660001916815260200191505060405180910390f35b34801561028a57600080fd5b506102ad6004803603810190808035600019169060200190929190505050610c4b565b60405180826000191660001916815260200191505060405180910390f35b3480156102d757600080fd5b506102fa6004803603810190808035600019169060200190929190505050610c72565b6040518083815260200182600019166000191681526020019250505060405180910390f35b34801561032b57600080fd5b5061034e6004803603810190808035600019169060200190929190505050610eb0565b604051808860001916600019168152602001878152602001868152602001856000191660001916815260200184600019166000191681526020018373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020018260058111156103c657fe5b60ff16815260200197505050505050505060405180910390f35b3480156103ec57600080fd5b5061040f6004803603810190808035600019169060200190929190505050610f42565b6040518083815260200182600019166000191681526020019250505060405180910390f35b34801561044057600080fd5b506104636004803603810190808035600019169060200190929190505050611255565b60405180826000191660001916815260200191505060405180910390f35b34801561048d57600080fd5b506104b0600480360381019080803560001916906020019092919050505061127c565b604051808263ffffffff1663ffffffff16815260200191505060405180910390f35b3480156104de57600080fd5b506104e76112b3565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b34801561053557600080fd5b5061058860048036038101908080356000191690602001909291908035906020019092919080359060200190929190803560001916906020019092919080356000191690602001909291905050506112d9565b60405180826000191660001916815260200191505060405180910390f35b3480156105b257600080fd5b506105e7600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050611334565b005b3480156105f557600080fd5b506106686004803603810190808035600019169060200190929190803590602001909291908035906020019092919080356000191690602001909291908035600019169060200190929190803573ffffffffffffffffffffffffffffffffffffffff1690602001909291905050506113e2565b6040518083815260200182600019166000191681526020019250505060405180910390f35b34801561069957600080fd5b506106bc6004803603810190808035600019169060200190929190505050611890565b6040518083815260200182600019166000191681526020019250505060405180910390f35b3480156106ed57600080fd5b506107106004803603810190808035600019169060200190929190505050611ace565b6040518083815260200182600019166000191681526020019250505060405180910390f35b34801561074157600080fd5b5061074a611cd8565b60405180826000191660001916815260200191505060405180910390f35b34801561077457600080fd5b5061077d611ce2565b6040518082600960200280838360005b838110156107a857808201518184015260208101905061078d565b5050505090500191505060405180910390f35b3480156107c757600080fd5b5061081f60048036038101908080359060200190820180359060200190808060200260200160405190810160405280939291908181526020018383602002808284378201915050505050509192919290505050611da7565b60405180826000191660001916815260200191505060405180910390f35b34801561084957600080fd5b50610852611db9565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b6000806000806000600102600354600019161415156108b257600080fd5b60006004541415156108c357600080fd5b600060010285600019161415156108d957600080fd5b6108e689898989896112d9565b9150600080836000191660001916815260200190815260200160002090506000600581111561091157fe5b8160070160089054906101000a900460ff16600581111561092e57fe5b14151561093a57600080fd5b8160016000600260009054906101000a900463ffffffff1663ffffffff1663ffffffff1681526020019081526020016000208160001916905550888160000181600019169055508781600101819055508681600201819055508581600301816000191690555084816004018160001916905550338160050160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550600260009054906101000a900463ffffffff168160070160006101000a81548163ffffffff021916908363ffffffff16021790555060008160070160046101000a81548163ffffffff021916908363ffffffff16021790555060048160070160086101000a81548160ff02191690836005811115610a6a57fe5b021790555060006001028160060181600019169055506002600081819054906101000a900463ffffffff168092919060010191906101000a81548163ffffffff021916908363ffffffff160217905550507f64951c9008bba9f4663c12662e7a9b6412a7c4757869fdac09285564ae923fa182336040518083600019166000191681526020018273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019250505060405180910390a18160038160001916905550876004819055507ff2dbbf0abb1ab1870a5e4d02746747c91d167c855255440b573ba3b5529dc90182336040518083600019166000191681526020018273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019250505060405180910390a16000829350935050509550959350505050565b60008060008360001916600019168152602001908152602001600020600201549050919050565b6000806000836000191660001916815260200190815260200160002060070160049054906101000a900463ffffffff169050919050565b60008060008360001916600019168152602001908152602001600020600301549050919050565b60008060008360001916600019168152602001908152602001600020600001549050919050565b6000806000600560009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16141515610d2b577fa57c1ba4cf2c89b3558cfeeca4339e04551f0fc1a12cf63f1923c2eed8a5be8b8461c3966040518083600019166000191681526020018281526020019250505060405180910390a161c396600080600102905092509250610eaa565b6000808560001916600019168152602001908152602001600020905060026005811115610d5457fe5b8160070160089054906101000a900460ff166005811115610d7157fe5b14158015610da7575060036005811115610d8757fe5b8160070160089054906101000a900460ff166005811115610da457fe5b14155b15610e09577fa57c1ba4cf2c89b3558cfeeca4339e04551f0fc1a12cf63f1923c2eed8a5be8b8461c3646040518083600019166000191681526020018281526020019250505060405180910390a161c364600080600102905092509250610eaa565b60058160070160086101000a81548160ff02191690836005811115610e2a57fe5b02179055507f64297372062dfcb21d6f7385f68d4656e993be2bb674099e3de73128d4911a9184336040518083600019166000191681526020018273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019250505060405180910390a1600084925092505b50915091565b6000806000806000806000806000808a600019166000191681526020019081526020016000209050806000015481600101548260020154836003015484600401548560050160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff168660070160089054906101000a900460ff16975097509750975097509750975050919395979092949650565b600080600080600560009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16141515610ffc577fa57c1ba4cf2c89b3558cfeeca4339e04551f0fc1a12cf63f1923c2eed8a5be8b8561c3966040518083600019166000191681526020018281526020019250505060405180910390a161c39660008060010290509350935061124e565b600080866000191660001916815260200190815260200160002091506001600581111561102557fe5b8260070160089054906101000a900460ff16600581111561104257fe5b1415801561107857506003600581111561105857fe5b8260070160089054906101000a900460ff16600581111561107557fe5b14155b156110da577fa57c1ba4cf2c89b3558cfeeca4339e04551f0fc1a12cf63f1923c2eed8a5be8b8561c3646040518083600019166000191681526020018281526020019250505060405180910390a161c36460008060010290509350935061124e565b60008083600401546000191660001916815260200190815260200160002090506004600581111561110757fe5b8160070160089054906101000a900460ff16600581111561112457fe5b141515611188577fa57c1ba4cf2c89b3558cfeeca4339e04551f0fc1a12cf63f1923c2eed8a5be8b8561c3826040518083600019166000191681526020018281526020019250505060405180910390a161c38260008060010290509350935061124e565b60048260070160086101000a81548160ff021916908360058111156111a957fe5b0217905550600454826001015411156111d357846003816000191690555081600101546004819055505b7ff2dbbf0abb1ab1870a5e4d02746747c91d167c855255440b573ba3b5529dc90185336040518083600019166000191681526020018273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019250505060405180910390a1600085935093505b5050915091565b60008060008360001916600019168152602001908152602001600020600601549050919050565b6000806000836000191660001916815260200190815260200160002060070160009054906101000a900463ffffffff169050919050565b600560009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b6000858585858560405180866000191660001916815260200185815260200184815260200183600019166000191681526020018260001916600019168152602001955050505050506040518091039020905095945050505050565b6000600560009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16148015611393575060008173ffffffffffffffffffffffffffffffffffffffff1614155b151561139e57600080fd5b80600560006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555050565b6000806000806000600560009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614151561149e577fa57c1ba4cf2c89b3558cfeeca4339e04551f0fc1a12cf63f1923c2eed8a5be8b600061c39660405180836001026000191681526020018281526020019250505060405180910390a161c396600080600102905094509450611882565b60008088600019166000191681526020019081526020016000209250600360058111156114c757fe5b8360070160089054906101000a900460ff1660058111156114e457fe5b1415801561151a5750600460058111156114fa57fe5b8360070160089054906101000a900460ff16600581111561151757fe5b14155b1561157c577fa57c1ba4cf2c89b3558cfeeca4339e04551f0fc1a12cf63f1923c2eed8a5be8b8261c3826040518083600019166000191681526020018281526020019250505060405180910390a161c382600080600102905094509450611882565b6115898b8b8b8b8b6112d9565b915060008083600019166000191681526020019081526020016000209050600060058111156115b457fe5b8160070160089054906101000a900460ff1660058111156115d157fe5b141515611635577fa57c1ba4cf2c89b3558cfeeca4339e04551f0fc1a12cf63f1923c2eed8a5be8b8261c35a6040518083600019166000191681526020018281526020019250505060405180910390a161c35a600080600102905094509450611882565b8160016000600260009054906101000a900463ffffffff1663ffffffff1663ffffffff16815260200190815260200160002081600019169055508a8160000181600019169055508981600101819055508881600201819055508781600301816000191690555086816004018160001916905550858160050160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550600260009054906101000a900463ffffffff168160070160006101000a81548163ffffffff021916908363ffffffff16021790555060018360070160049054906101000a900463ffffffff16018160070160046101000a81548163ffffffff021916908363ffffffff16021790555060018160070160086101000a81548160ff0219169083600581111561177b57fe5b02179055506117c083600601548460070160009054906101000a900463ffffffff1660018660070160049054906101000a900463ffffffff160163ffffffff16611ddf565b8160060181600019169055506002600081819054906101000a900463ffffffff168092919060010191906101000a81548163ffffffff021916908363ffffffff160217905550507f64951c9008bba9f4663c12662e7a9b6412a7c4757869fdac09285564ae923fa182336040518083600019166000191681526020018273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019250505060405180910390a1600082945094505b505050965096945050505050565b6000806000600560009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16141515611949577fa57c1ba4cf2c89b3558cfeeca4339e04551f0fc1a12cf63f1923c2eed8a5be8b8461c3966040518083600019166000191681526020018281526020019250505060405180910390a161c396600080600102905092509250611ac8565b600080856000191660001916815260200190815260200160002090506001600581111561197257fe5b8160070160089054906101000a900460ff16600581111561198f57fe5b141580156119c55750600260058111156119a557fe5b8160070160089054906101000a900460ff1660058111156119c257fe5b14155b15611a27577fa57c1ba4cf2c89b3558cfeeca4339e04551f0fc1a12cf63f1923c2eed8a5be8b8461c3646040518083600019166000191681526020018281526020019250505060405180910390a161c364600080600102905092509250611ac8565b60028160070160086101000a81548160ff02191690836005811115611a4857fe5b02179055507f09cdaca254aa177f759fe7a0968fe696ee9baf7d2a1d4714ed24b83d1f09518e84336040518083600019166000191681526020018273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019250505060405180910390a1600084925092505b50915091565b6000806000600560009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16141515611b87577fa57c1ba4cf2c89b3558cfeeca4339e04551f0fc1a12cf63f1923c2eed8a5be8b8461c3966040518083600019166000191681526020018281526020019250505060405180910390a161c396600080600102905092509250611cd2565b6000808560001916600019168152602001908152602001600020905060026005811115611bb057fe5b8160070160089054906101000a900460ff166005811115611bcd57fe5b141515611c31577fa57c1ba4cf2c89b3558cfeeca4339e04551f0fc1a12cf63f1923c2eed8a5be8b8461c3646040518083600019166000191681526020018281526020019250505060405180910390a161c364600080600102905092509250611cd2565b60038160070160086101000a81548160ff02191690836005811115611c5257fe5b02179055507f87f54f5eb3dd119fe71af0915af693e64a5bfd4acaa19a6c944c47cff8eec9e684336040518083600019166000191681526020018273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019250505060405180910390a1600084925092505b50915091565b6000600354905090565b611cea612206565b611cf2612206565b600080600354836000600981101515611d0757fe5b60200201906000191690816000191681525050611d25600354611255565b9150600890505b6000811115611d9e576001600063ffffffff60010284166001900463ffffffff1663ffffffff168152602001908152602001600020548382600981101515611d7057fe5b60200201906000191690816000191681525050602082600019169060020a9004915080600190039050611d2c565b82935050505090565b6000611db282611e48565b9050919050565b600660009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b600080600060059150611df48660008761218e565b9550600190505b600881108015611e16575060018285811515611e1357fe5b06145b15611e3c57611e2986826004028761218e565b9550600582029150806001019050611dfb565b85925050509392505050565b6000806000806000855193506001841415611e7d57856000815181101515611e6c57fe5b906020019060200201519450612185565b600084111515611e8c57600080fd5b60009050600092505b83831015611ff457836001840110611eb05760018403611eb5565b600183015b9150600280611eda8886815181101515611ecb57fe5b906020019060200201516121d0565b611efa8986815181101515611eeb57fe5b906020019060200201516121d0565b6040518083600019166000191681526020018260001916600019168152602001925050506020604051808303816000865af1158015611f3d573d6000803e3d6000fd5b5050506040513d6020811015611f5257600080fd5b81019080805190602001909291905050506040518082600019166000191681526020019150506020604051808303816000865af1158015611f97573d6000803e3d6000fd5b5050506040513d6020811015611fac57600080fd5b81019080805190602001909291905050508682815181101515611fcb57fe5b906020019060200201906000191690816000191681525050600181019050600283019250611e95565b8093505b60018411156121615760009050600092505b8383101561215957836001840110612025576001840361202a565b600183015b9150600280878581518110151561203d57fe5b90602001906020020151888581518110151561205557fe5b906020019060200201516040518083600019166000191681526020018260001916600019168152602001925050506020604051808303816000865af11580156120a2573d6000803e3d6000fd5b5050506040513d60208110156120b757600080fd5b81019080805190602001909291905050506040518082600019166000191681526020019150506020604051808303816000865af11580156120fc573d6000803e3d6000fd5b5050506040513d602081101561211157600080fd5b8101908080519060200190929190505050868281518110151561213057fe5b90602001906020020190600019169081600019168152505060018101905060028301925061200a565b809350611ff8565b61218286600081518110151561217357fe5b906020019060200201516121d0565b94505b50505050919050565b60008060405185815283601c1a8582015383601d1a6001860182015383601e1a6002860182015383601f1a600386018201538051915050809150509392505050565b60008060405160005b60208110156121f7578481601f031a818301536001810190506121d9565b50805191505080915050919050565b610120604051908101604052806009906020820280388339808201915050905050905600a165627a7a723058205c90c7b463f7b314b01badaa7bbc98d034d30a51240417f7a0cad42328d4f3450029";

    protected static final HashMap<String, String> _addresses;

    static {
        _addresses = new HashMap<>();
    }

    protected Superblocks(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected Superblocks(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
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

    public RemoteCall<String> claimManager() {
        final Function function = new Function("claimManager", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<String> dogeRelay() {
        final Function function = new Function("dogeRelay", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public static RemoteCall<Superblocks> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String _dogeRelay) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_dogeRelay)));
        return deployRemoteCall(Superblocks.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static RemoteCall<Superblocks> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, String _dogeRelay) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_dogeRelay)));
        return deployRemoteCall(Superblocks.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public RemoteCall<TransactionReceipt> setClaimManager(String _claimManager) {
        final Function function = new Function(
                "setClaimManager", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_claimManager)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> initialize(byte[] _blocksMerkleRoot, BigInteger _accumulatedWork, BigInteger _timestamp, byte[] _lastHash, byte[] _parentId) {
        final Function function = new Function(
                "initialize", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_blocksMerkleRoot), 
                new org.web3j.abi.datatypes.generated.Uint256(_accumulatedWork), 
                new org.web3j.abi.datatypes.generated.Uint256(_timestamp), 
                new org.web3j.abi.datatypes.generated.Bytes32(_lastHash), 
                new org.web3j.abi.datatypes.generated.Bytes32(_parentId)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> propose(byte[] _blocksMerkleRoot, BigInteger _accumulatedWork, BigInteger _timestamp, byte[] _lastHash, byte[] _parentId, String submitter) {
        final Function function = new Function(
                "propose", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_blocksMerkleRoot), 
                new org.web3j.abi.datatypes.generated.Uint256(_accumulatedWork), 
                new org.web3j.abi.datatypes.generated.Uint256(_timestamp), 
                new org.web3j.abi.datatypes.generated.Bytes32(_lastHash), 
                new org.web3j.abi.datatypes.generated.Bytes32(_parentId), 
                new org.web3j.abi.datatypes.Address(submitter)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> confirm(byte[] _superblockId) {
        final Function function = new Function(
                "confirm", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_superblockId)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> challenge(byte[] _superblockId) {
        final Function function = new Function(
                "challenge", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_superblockId)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> semiApprove(byte[] _superblockId) {
        final Function function = new Function(
                "semiApprove", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_superblockId)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> invalidate(byte[] _superblockId) {
        final Function function = new Function(
                "invalidate", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_superblockId)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<byte[]> calcSuperblockId(byte[] _blocksMerkleRoot, BigInteger _accumulatedWork, BigInteger _timestamp, byte[] _lastHash, byte[] _parentId) {
        final Function function = new Function("calcSuperblockId", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_blocksMerkleRoot), 
                new org.web3j.abi.datatypes.generated.Uint256(_accumulatedWork), 
                new org.web3j.abi.datatypes.generated.Uint256(_timestamp), 
                new org.web3j.abi.datatypes.generated.Bytes32(_lastHash), 
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

    public RemoteCall<Tuple7<byte[], BigInteger, BigInteger, byte[], byte[], String, BigInteger>> getSuperblock(byte[] superblockId) {
        final Function function = new Function("getSuperblock", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(superblockId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}, new TypeReference<Uint8>() {}));
        return new RemoteCall<Tuple7<byte[], BigInteger, BigInteger, byte[], byte[], String, BigInteger>>(
                new Callable<Tuple7<byte[], BigInteger, BigInteger, byte[], byte[], String, BigInteger>>() {
                    @Override
                    public Tuple7<byte[], BigInteger, BigInteger, byte[], byte[], String, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple7<byte[], BigInteger, BigInteger, byte[], byte[], String, BigInteger>(
                                (byte[]) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue(), 
                                (byte[]) results.get(3).getValue(), 
                                (byte[]) results.get(4).getValue(), 
                                (String) results.get(5).getValue(), 
                                (BigInteger) results.get(6).getValue());
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

    public RemoteCall<byte[]> getSuperblockLastHash(byte[] _superblockId) {
        final Function function = new Function("getSuperblockLastHash", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_superblockId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteCall<byte[]> makeMerkle(List<byte[]> hashes) {
        final Function function = new Function("makeMerkle", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Bytes32>(
                        org.web3j.abi.Utils.typeMap(hashes, org.web3j.abi.datatypes.generated.Bytes32.class))), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
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

    public static Superblocks load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new Superblocks(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static Superblocks load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new Superblocks(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
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
}
