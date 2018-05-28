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
    private static final String BINARY = "0x608060405234801561001057600080fd5b506040516020806124d18339810180604052810190808051906020019092919050505080600660006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055505061244d806100846000396000f300608060405260043610610149576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff168063282c14111461014e5780632da8cffd146101d25780632e400191146102175780633288816a146102685780633ce90e8f1461029b57806355e018ce146102e8578063642ed9881461033557806369ecc3cf146103825780636e5b7071146103d6578063797af627146104975780637b34dcd9146104eb57806387a4d38214610538578063a9a36dcd14610589578063b1522eb6146105e0578063b6da21441461065d578063b8558d12146106a0578063cae0581e14610744578063cffd46dc14610797578063d93a05e7146107eb578063df2223571461083f578063f06d520d1461086a578063f2854e341461089d578063f6f3238a146108e2578063f9b5d7c014610935578063fb338e20146109b7575b600080fd5b34801561015a57600080fd5b506101ad6004803603810190808035600019169060200190929190803590602001909291908035906020019092919080356000191690602001909291908035600019169060200190929190505050610a0e565b6040518083815260200182600019166000191681526020019250505060405180910390f35b3480156101de57600080fd5b506102016004803603810190808035600019169060200190929190505050610d2f565b6040518082815260200191505060405180910390f35b34801561022357600080fd5b506102466004803603810190808035600019169060200190929190505050610d56565b604051808263ffffffff1663ffffffff16815260200191505060405180910390f35b34801561027457600080fd5b5061027d610d8d565b60405180826000191660001916815260200191505060405180910390f35b3480156102a757600080fd5b506102ca6004803603810190808035600019169060200190929190505050610d93565b60405180826000191660001916815260200191505060405180910390f35b3480156102f457600080fd5b506103176004803603810190808035600019169060200190929190505050610dba565b60405180826000191660001916815260200191505060405180910390f35b34801561034157600080fd5b506103646004803603810190808035600019169060200190929190505050610de1565b60405180826000191660001916815260200191505060405180910390f35b34801561038e57600080fd5b506103b16004803603810190808035600019169060200190929190505050610e08565b6040518083815260200182600019166000191681526020019250505060405180910390f35b3480156103e257600080fd5b506104056004803603810190808035600019169060200190929190505050611046565b604051808860001916600019168152602001878152602001868152602001856000191660001916815260200184600019166000191681526020018373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200182600581111561047d57fe5b60ff16815260200197505050505050505060405180910390f35b3480156104a357600080fd5b506104c660048036038101908080356000191690602001909291905050506110d8565b6040518083815260200182600019166000191681526020019250505060405180910390f35b3480156104f757600080fd5b5061051a60048036038101908080356000191690602001909291905050506113eb565b60405180826000191660001916815260200191505060405180910390f35b34801561054457600080fd5b506105676004803603810190808035600019169060200190929190505050611412565b604051808263ffffffff1663ffffffff16815260200191505060405180910390f35b34801561059557600080fd5b5061059e611449565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b3480156105ec57600080fd5b5061063f600480360381019080803560001916906020019092919080359060200190929190803590602001909291908035600019169060200190929190803560001916906020019092919050505061146f565b60405180826000191660001916815260200191505060405180910390f35b34801561066957600080fd5b5061069e600480360381019080803573ffffffffffffffffffffffffffffffffffffffff1690602001909291905050506114ca565b005b3480156106ac57600080fd5b5061071f6004803603810190808035600019169060200190929190803590602001909291908035906020019092919080356000191690602001909291908035600019169060200190929190803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050611578565b6040518083815260200182600019166000191681526020019250505060405180910390f35b34801561075057600080fd5b506107736004803603810190808035600019169060200190929190505050611a26565b6040518082600581111561078357fe5b60ff16815260200191505060405180910390f35b3480156107a357600080fd5b506107c66004803603810190808035600019169060200190929190505050611a5a565b6040518083815260200182600019166000191681526020019250505060405180910390f35b3480156107f757600080fd5b5061081a6004803603810190808035600019169060200190929190505050611c98565b6040518083815260200182600019166000191681526020019250505060405180910390f35b34801561084b57600080fd5b50610854611ea2565b6040518082815260200191505060405180910390f35b34801561087657600080fd5b5061087f611ea8565b60405180826000191660001916815260200191505060405180910390f35b3480156108a957600080fd5b506108cc6004803603810190808035600019169060200190929190505050611eb2565b6040518082815260200191505060405180910390f35b3480156108ee57600080fd5b506108f7611ed9565b6040518082600960200280838360005b83811015610922578082015181840152602081019050610907565b5050505090500191505060405180910390f35b34801561094157600080fd5b5061099960048036038101908080359060200190820180359060200190808060200260200160405190810160405280939291908181526020018383602002808284378201915050505050509192919290505050611f9e565b60405180826000191660001916815260200191505060405180910390f35b3480156109c357600080fd5b506109cc611fb0565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b600080600080600060010260035460001916141515610a2c57600080fd5b60006001028560001916141515610a4257600080fd5b610a4f898989898961146f565b91506000808360001916600019168152602001908152602001600020905060006005811115610a7a57fe5b8160070160089054906101000a900460ff166005811115610a9757fe5b141515610aa357600080fd5b8160016000600260009054906101000a900463ffffffff1663ffffffff1663ffffffff1681526020019081526020016000208160001916905550888160000181600019169055508781600101819055508681600201819055508581600301816000191690555084816004018160001916905550338160050160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550600260009054906101000a900463ffffffff168160070160006101000a81548163ffffffff021916908363ffffffff16021790555060008160070160046101000a81548163ffffffff021916908363ffffffff16021790555060048160070160086101000a81548160ff02191690836005811115610bd357fe5b021790555060006001028160060181600019169055506002600081819054906101000a900463ffffffff168092919060010191906101000a81548163ffffffff021916908363ffffffff160217905550507f64951c9008bba9f4663c12662e7a9b6412a7c4757869fdac09285564ae923fa182336040518083600019166000191681526020018273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019250505060405180910390a18160038160001916905550876004819055507ff2dbbf0abb1ab1870a5e4d02746747c91d167c855255440b573ba3b5529dc90182336040518083600019166000191681526020018273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019250505060405180910390a16000829350935050509550959350505050565b60008060008360001916600019168152602001908152602001600020600201549050919050565b6000806000836000191660001916815260200190815260200160002060070160049054906101000a900463ffffffff169050919050565b60035481565b60008060008360001916600019168152602001908152602001600020600301549050919050565b60008060008360001916600019168152602001908152602001600020600401549050919050565b60008060008360001916600019168152602001908152602001600020600001549050919050565b6000806000600560009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16141515610ec1577fa57c1ba4cf2c89b3558cfeeca4339e04551f0fc1a12cf63f1923c2eed8a5be8b8461c3966040518083600019166000191681526020018281526020019250505060405180910390a161c396600080600102905092509250611040565b6000808560001916600019168152602001908152602001600020905060026005811115610eea57fe5b8160070160089054906101000a900460ff166005811115610f0757fe5b14158015610f3d575060036005811115610f1d57fe5b8160070160089054906101000a900460ff166005811115610f3a57fe5b14155b15610f9f577fa57c1ba4cf2c89b3558cfeeca4339e04551f0fc1a12cf63f1923c2eed8a5be8b8461c3646040518083600019166000191681526020018281526020019250505060405180910390a161c364600080600102905092509250611040565b60058160070160086101000a81548160ff02191690836005811115610fc057fe5b02179055507f64297372062dfcb21d6f7385f68d4656e993be2bb674099e3de73128d4911a9184336040518083600019166000191681526020018273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019250505060405180910390a1600084925092505b50915091565b6000806000806000806000806000808a600019166000191681526020019081526020016000209050806000015481600101548260020154836003015484600401548560050160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff168660070160089054906101000a900460ff16975097509750975097509750975050919395979092949650565b600080600080600560009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16141515611192577fa57c1ba4cf2c89b3558cfeeca4339e04551f0fc1a12cf63f1923c2eed8a5be8b8561c3966040518083600019166000191681526020018281526020019250505060405180910390a161c3966000806001029050935093506113e4565b60008086600019166000191681526020019081526020016000209150600160058111156111bb57fe5b8260070160089054906101000a900460ff1660058111156111d857fe5b1415801561120e5750600360058111156111ee57fe5b8260070160089054906101000a900460ff16600581111561120b57fe5b14155b15611270577fa57c1ba4cf2c89b3558cfeeca4339e04551f0fc1a12cf63f1923c2eed8a5be8b8561c3646040518083600019166000191681526020018281526020019250505060405180910390a161c3646000806001029050935093506113e4565b60008083600401546000191660001916815260200190815260200160002090506004600581111561129d57fe5b8160070160089054906101000a900460ff1660058111156112ba57fe5b14151561131e577fa57c1ba4cf2c89b3558cfeeca4339e04551f0fc1a12cf63f1923c2eed8a5be8b8561c3826040518083600019166000191681526020018281526020019250505060405180910390a161c3826000806001029050935093506113e4565b60048260070160086101000a81548160ff0219169083600581111561133f57fe5b02179055506004548260010154111561136957846003816000191690555081600101546004819055505b7ff2dbbf0abb1ab1870a5e4d02746747c91d167c855255440b573ba3b5529dc90185336040518083600019166000191681526020018273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019250505060405180910390a1600085935093505b5050915091565b60008060008360001916600019168152602001908152602001600020600601549050919050565b6000806000836000191660001916815260200190815260200160002060070160009054906101000a900463ffffffff169050919050565b600560009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b6000858585858560405180866000191660001916815260200185815260200184815260200183600019166000191681526020018260001916600019168152602001955050505050506040518091039020905095945050505050565b6000600560009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16148015611529575060008173ffffffffffffffffffffffffffffffffffffffff1614155b151561153457600080fd5b80600560006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555050565b6000806000806000600560009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16141515611634577fa57c1ba4cf2c89b3558cfeeca4339e04551f0fc1a12cf63f1923c2eed8a5be8b600061c39660405180836001026000191681526020018281526020019250505060405180910390a161c396600080600102905094509450611a18565b600080886000191660001916815260200190815260200160002092506003600581111561165d57fe5b8360070160089054906101000a900460ff16600581111561167a57fe5b141580156116b057506004600581111561169057fe5b8360070160089054906101000a900460ff1660058111156116ad57fe5b14155b15611712577fa57c1ba4cf2c89b3558cfeeca4339e04551f0fc1a12cf63f1923c2eed8a5be8b8261c3826040518083600019166000191681526020018281526020019250505060405180910390a161c382600080600102905094509450611a18565b61171f8b8b8b8b8b61146f565b9150600080836000191660001916815260200190815260200160002090506000600581111561174a57fe5b8160070160089054906101000a900460ff16600581111561176757fe5b1415156117cb577fa57c1ba4cf2c89b3558cfeeca4339e04551f0fc1a12cf63f1923c2eed8a5be8b8261c35a6040518083600019166000191681526020018281526020019250505060405180910390a161c35a600080600102905094509450611a18565b8160016000600260009054906101000a900463ffffffff1663ffffffff1663ffffffff16815260200190815260200160002081600019169055508a8160000181600019169055508981600101819055508881600201819055508781600301816000191690555086816004018160001916905550858160050160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550600260009054906101000a900463ffffffff168160070160006101000a81548163ffffffff021916908363ffffffff16021790555060018360070160049054906101000a900463ffffffff16018160070160046101000a81548163ffffffff021916908363ffffffff16021790555060018160070160086101000a81548160ff0219169083600581111561191157fe5b021790555061195683600601548460070160009054906101000a900463ffffffff1660018660070160049054906101000a900463ffffffff160163ffffffff16611fd6565b8160060181600019169055506002600081819054906101000a900463ffffffff168092919060010191906101000a81548163ffffffff021916908363ffffffff160217905550507f64951c9008bba9f4663c12662e7a9b6412a7c4757869fdac09285564ae923fa182336040518083600019166000191681526020018273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019250505060405180910390a1600082945094505b505050965096945050505050565b6000806000836000191660001916815260200190815260200160002060070160089054906101000a900460ff169050919050565b6000806000600560009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16141515611b13577fa57c1ba4cf2c89b3558cfeeca4339e04551f0fc1a12cf63f1923c2eed8a5be8b8461c3966040518083600019166000191681526020018281526020019250505060405180910390a161c396600080600102905092509250611c92565b6000808560001916600019168152602001908152602001600020905060016005811115611b3c57fe5b8160070160089054906101000a900460ff166005811115611b5957fe5b14158015611b8f575060026005811115611b6f57fe5b8160070160089054906101000a900460ff166005811115611b8c57fe5b14155b15611bf1577fa57c1ba4cf2c89b3558cfeeca4339e04551f0fc1a12cf63f1923c2eed8a5be8b8461c3646040518083600019166000191681526020018281526020019250505060405180910390a161c364600080600102905092509250611c92565b60028160070160086101000a81548160ff02191690836005811115611c1257fe5b02179055507f09cdaca254aa177f759fe7a0968fe696ee9baf7d2a1d4714ed24b83d1f09518e84336040518083600019166000191681526020018273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019250505060405180910390a1600084925092505b50915091565b6000806000600560009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16141515611d51577fa57c1ba4cf2c89b3558cfeeca4339e04551f0fc1a12cf63f1923c2eed8a5be8b8461c3966040518083600019166000191681526020018281526020019250505060405180910390a161c396600080600102905092509250611e9c565b6000808560001916600019168152602001908152602001600020905060026005811115611d7a57fe5b8160070160089054906101000a900460ff166005811115611d9757fe5b141515611dfb577fa57c1ba4cf2c89b3558cfeeca4339e04551f0fc1a12cf63f1923c2eed8a5be8b8461c3646040518083600019166000191681526020018281526020019250505060405180910390a161c364600080600102905092509250611e9c565b60038160070160086101000a81548160ff02191690836005811115611e1c57fe5b02179055507f87f54f5eb3dd119fe71af0915af693e64a5bfd4acaa19a6c944c47cff8eec9e684336040518083600019166000191681526020018273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019250505060405180910390a1600084925092505b50915091565b60045481565b6000600354905090565b60008060008360001916600019168152602001908152602001600020600101549050919050565b611ee16123fd565b611ee96123fd565b600080600354836000600981101515611efe57fe5b60200201906000191690816000191681525050611f1c6003546113eb565b9150600890505b6000811115611f95576001600063ffffffff60010284166001900463ffffffff1663ffffffff168152602001908152602001600020548382600981101515611f6757fe5b60200201906000191690816000191681525050602082600019169060020a9004915080600190039050611f23565b82935050505090565b6000611fa98261203f565b9050919050565b600660009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b600080600060059150611feb86600087612385565b9550600190505b60088110801561200d57506001828581151561200a57fe5b06145b1561203357612020868260040287612385565b9550600582029150806001019050611ff2565b85925050509392505050565b60008060008060008551935060018414156120745785600081518110151561206357fe5b90602001906020020151945061237c565b60008411151561208357600080fd5b60009050600092505b838310156121eb578360018401106120a757600184036120ac565b600183015b91506002806120d188868151811015156120c257fe5b906020019060200201516123c7565b6120f189868151811015156120e257fe5b906020019060200201516123c7565b6040518083600019166000191681526020018260001916600019168152602001925050506020604051808303816000865af1158015612134573d6000803e3d6000fd5b5050506040513d602081101561214957600080fd5b81019080805190602001909291905050506040518082600019166000191681526020019150506020604051808303816000865af115801561218e573d6000803e3d6000fd5b5050506040513d60208110156121a357600080fd5b810190808051906020019092919050505086828151811015156121c257fe5b90602001906020020190600019169081600019168152505060018101905060028301925061208c565b8093505b60018411156123585760009050600092505b838310156123505783600184011061221c5760018403612221565b600183015b9150600280878581518110151561223457fe5b90602001906020020151888581518110151561224c57fe5b906020019060200201516040518083600019166000191681526020018260001916600019168152602001925050506020604051808303816000865af1158015612299573d6000803e3d6000fd5b5050506040513d60208110156122ae57600080fd5b81019080805190602001909291905050506040518082600019166000191681526020019150506020604051808303816000865af11580156122f3573d6000803e3d6000fd5b5050506040513d602081101561230857600080fd5b8101908080519060200190929190505050868281518110151561232757fe5b906020019060200201906000191690816000191681525050600181019050600283019250612201565b8093506121ef565b61237986600081518110151561236a57fe5b906020019060200201516123c7565b94505b50505050919050565b60008060405185815283601c1a8582015383601d1a6001860182015383601e1a6002860182015383601f1a600386018201538051915050809150509392505050565b60008060405160005b60208110156123ee578481601f031a818301536001810190506123d0565b50805191505080915050919050565b610120604051908101604052806009906020820280388339808201915050905050905600a165627a7a723058207969c34a4feca8977beee1911f4d085f5685386ca1b7884a3d6ecae7382c71930029";

    protected static final HashMap<String, String> _addresses;

    static {
        _addresses = new HashMap<>();
        _addresses.put("1527182964555", "0x360c875fe340b817b5684c639cc41c0e89fed3aa");
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
