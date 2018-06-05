package org.dogethereum.dogesubmitter.contract;

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
    private static final String BINARY = "0x608060405234801561001057600080fd5b506124cc806100206000396000f300608060405260043610610154576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff168063282c1411146101595780632da8cffd146101dd5780632e400191146102225780633288816a146102735780633ce90e8f146102a657806348aefc32146102f357806355e018ce1461033c578063642ed9881461038957806369ecc3cf146103d65780636e5b70711461042a578063797af627146104eb5780637b34dcd91461053f57806387a4d3821461058c578063a9a36dcd146105dd578063b1522eb614610634578063b6da2144146106b1578063b8558d12146106f4578063cae0581e14610798578063cffd46dc146107eb578063d93a05e71461083f578063df22235714610893578063f06d520d146108be578063f2854e34146108f1578063f6f3238a14610936578063f9b5d7c014610989578063fb338e2014610a0b575b600080fd5b34801561016557600080fd5b506101b86004803603810190808035600019169060200190929190803590602001909291908035906020019092919080356000191690602001909291908035600019169060200190929190505050610a62565b6040518083815260200182600019166000191681526020019250505060405180910390f35b3480156101e957600080fd5b5061020c6004803603810190808035600019169060200190929190505050610d83565b6040518082815260200191505060405180910390f35b34801561022e57600080fd5b506102516004803603810190808035600019169060200190929190505050610daa565b604051808263ffffffff1663ffffffff16815260200191505060405180910390f35b34801561027f57600080fd5b50610288610de1565b60405180826000191660001916815260200191505060405180910390f35b3480156102b257600080fd5b506102d56004803603810190808035600019169060200190929190505050610de7565b60405180826000191660001916815260200191505060405180910390f35b3480156102ff57600080fd5b506103226004803603810190808035600019169060200190929190505050610e0e565b604051808215151515815260200191505060405180910390f35b34801561034857600080fd5b5061036b6004803603810190808035600019169060200190929190505050610e39565b60405180826000191660001916815260200191505060405180910390f35b34801561039557600080fd5b506103b86004803603810190808035600019169060200190929190505050610e60565b60405180826000191660001916815260200191505060405180910390f35b3480156103e257600080fd5b506104056004803603810190808035600019169060200190929190505050610e87565b6040518083815260200182600019166000191681526020019250505060405180910390f35b34801561043657600080fd5b5061045960048036038101908080356000191690602001909291905050506110c5565b604051808860001916600019168152602001878152602001868152602001856000191660001916815260200184600019166000191681526020018373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020018260058111156104d157fe5b60ff16815260200197505050505050505060405180910390f35b3480156104f757600080fd5b5061051a6004803603810190808035600019169060200190929190505050611157565b6040518083815260200182600019166000191681526020019250505060405180910390f35b34801561054b57600080fd5b5061056e600480360381019080803560001916906020019092919050505061146a565b60405180826000191660001916815260200191505060405180910390f35b34801561059857600080fd5b506105bb6004803603810190808035600019169060200190929190505050611491565b604051808263ffffffff1663ffffffff16815260200191505060405180910390f35b3480156105e957600080fd5b506105f26114c8565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b34801561064057600080fd5b5061069360048036038101908080356000191690602001909291908035906020019092919080359060200190929190803560001916906020019092919080356000191690602001909291905050506114ee565b60405180826000191660001916815260200191505060405180910390f35b3480156106bd57600080fd5b506106f2600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050611549565b005b34801561070057600080fd5b506107736004803603810190808035600019169060200190929190803590602001909291908035906020019092919080356000191690602001909291908035600019169060200190929190803573ffffffffffffffffffffffffffffffffffffffff1690602001909291905050506115f7565b6040518083815260200182600019166000191681526020019250505060405180910390f35b3480156107a457600080fd5b506107c76004803603810190808035600019169060200190929190505050611aa5565b604051808260058111156107d757fe5b60ff16815260200191505060405180910390f35b3480156107f757600080fd5b5061081a6004803603810190808035600019169060200190929190505050611ad9565b6040518083815260200182600019166000191681526020019250505060405180910390f35b34801561084b57600080fd5b5061086e6004803603810190808035600019169060200190929190505050611d17565b6040518083815260200182600019166000191681526020019250505060405180910390f35b34801561089f57600080fd5b506108a8611f21565b6040518082815260200191505060405180910390f35b3480156108ca57600080fd5b506108d3611f27565b60405180826000191660001916815260200191505060405180910390f35b3480156108fd57600080fd5b506109206004803603810190808035600019169060200190929190505050611f31565b6040518082815260200191505060405180910390f35b34801561094257600080fd5b5061094b611f58565b6040518082600960200280838360005b8381101561097657808201518184015260208101905061095b565b5050505090500191505060405180910390f35b34801561099557600080fd5b506109ed6004803603810190808035906020019082018035906020019080806020026020016040519081016040528093929190818152602001838360200280828437820191505050505050919291929050505061201d565b60405180826000191660001916815260200191505060405180910390f35b348015610a1757600080fd5b50610a2061202f565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b600080600080600060010260035460001916141515610a8057600080fd5b60006001028560001916141515610a9657600080fd5b610aa389898989896114ee565b91506000808360001916600019168152602001908152602001600020905060006005811115610ace57fe5b8160070160089054906101000a900460ff166005811115610aeb57fe5b141515610af757600080fd5b8160016000600260009054906101000a900463ffffffff1663ffffffff1663ffffffff1681526020019081526020016000208160001916905550888160000181600019169055508781600101819055508681600201819055508581600301816000191690555084816004018160001916905550338160050160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550600260009054906101000a900463ffffffff168160070160006101000a81548163ffffffff021916908363ffffffff16021790555060008160070160046101000a81548163ffffffff021916908363ffffffff16021790555060048160070160086101000a81548160ff02191690836005811115610c2757fe5b021790555060006001028160060181600019169055506002600081819054906101000a900463ffffffff168092919060010191906101000a81548163ffffffff021916908363ffffffff160217905550507f64951c9008bba9f4663c12662e7a9b6412a7c4757869fdac09285564ae923fa182336040518083600019166000191681526020018273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019250505060405180910390a18160038160001916905550876004819055507ff2dbbf0abb1ab1870a5e4d02746747c91d167c855255440b573ba3b5529dc90182336040518083600019166000191681526020018273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019250505060405180910390a16000829350935050509550959350505050565b60008060008360001916600019168152602001908152602001600020600201549050919050565b6000806000836000191660001916815260200190815260200160002060070160049054906101000a900463ffffffff169050919050565b60035481565b60008060008360001916600019168152602001908152602001600020600301549050919050565b600060046005811115610e1d57fe5b610e2683611aa5565b6005811115610e3157fe5b149050919050565b60008060008360001916600019168152602001908152602001600020600401549050919050565b60008060008360001916600019168152602001908152602001600020600001549050919050565b6000806000600560009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16141515610f40577fa57c1ba4cf2c89b3558cfeeca4339e04551f0fc1a12cf63f1923c2eed8a5be8b8461c3966040518083600019166000191681526020018281526020019250505060405180910390a161c3966000806001029050925092506110bf565b6000808560001916600019168152602001908152602001600020905060026005811115610f6957fe5b8160070160089054906101000a900460ff166005811115610f8657fe5b14158015610fbc575060036005811115610f9c57fe5b8160070160089054906101000a900460ff166005811115610fb957fe5b14155b1561101e577fa57c1ba4cf2c89b3558cfeeca4339e04551f0fc1a12cf63f1923c2eed8a5be8b8461c3646040518083600019166000191681526020018281526020019250505060405180910390a161c3646000806001029050925092506110bf565b60058160070160086101000a81548160ff0219169083600581111561103f57fe5b02179055507f64297372062dfcb21d6f7385f68d4656e993be2bb674099e3de73128d4911a9184336040518083600019166000191681526020018273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019250505060405180910390a1600084925092505b50915091565b6000806000806000806000806000808a600019166000191681526020019081526020016000209050806000015481600101548260020154836003015484600401548560050160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff168660070160089054906101000a900460ff16975097509750975097509750975050919395979092949650565b600080600080600560009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16141515611211577fa57c1ba4cf2c89b3558cfeeca4339e04551f0fc1a12cf63f1923c2eed8a5be8b8561c3966040518083600019166000191681526020018281526020019250505060405180910390a161c396600080600102905093509350611463565b600080866000191660001916815260200190815260200160002091506001600581111561123a57fe5b8260070160089054906101000a900460ff16600581111561125757fe5b1415801561128d57506003600581111561126d57fe5b8260070160089054906101000a900460ff16600581111561128a57fe5b14155b156112ef577fa57c1ba4cf2c89b3558cfeeca4339e04551f0fc1a12cf63f1923c2eed8a5be8b8561c3646040518083600019166000191681526020018281526020019250505060405180910390a161c364600080600102905093509350611463565b60008083600401546000191660001916815260200190815260200160002090506004600581111561131c57fe5b8160070160089054906101000a900460ff16600581111561133957fe5b14151561139d577fa57c1ba4cf2c89b3558cfeeca4339e04551f0fc1a12cf63f1923c2eed8a5be8b8561c3826040518083600019166000191681526020018281526020019250505060405180910390a161c382600080600102905093509350611463565b60048260070160086101000a81548160ff021916908360058111156113be57fe5b0217905550600454826001015411156113e857846003816000191690555081600101546004819055505b7ff2dbbf0abb1ab1870a5e4d02746747c91d167c855255440b573ba3b5529dc90185336040518083600019166000191681526020018273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019250505060405180910390a1600085935093505b5050915091565b60008060008360001916600019168152602001908152602001600020600601549050919050565b6000806000836000191660001916815260200190815260200160002060070160009054906101000a900463ffffffff169050919050565b600560009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b6000858585858560405180866000191660001916815260200185815260200184815260200183600019166000191681526020018260001916600019168152602001955050505050506040518091039020905095945050505050565b6000600560009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff161480156115a8575060008173ffffffffffffffffffffffffffffffffffffffff1614155b15156115b357600080fd5b80600560006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555050565b6000806000806000600560009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff161415156116b3577fa57c1ba4cf2c89b3558cfeeca4339e04551f0fc1a12cf63f1923c2eed8a5be8b600061c39660405180836001026000191681526020018281526020019250505060405180910390a161c396600080600102905094509450611a97565b60008088600019166000191681526020019081526020016000209250600360058111156116dc57fe5b8360070160089054906101000a900460ff1660058111156116f957fe5b1415801561172f57506004600581111561170f57fe5b8360070160089054906101000a900460ff16600581111561172c57fe5b14155b15611791577fa57c1ba4cf2c89b3558cfeeca4339e04551f0fc1a12cf63f1923c2eed8a5be8b8261c3826040518083600019166000191681526020018281526020019250505060405180910390a161c382600080600102905094509450611a97565b61179e8b8b8b8b8b6114ee565b915060008083600019166000191681526020019081526020016000209050600060058111156117c957fe5b8160070160089054906101000a900460ff1660058111156117e657fe5b14151561184a577fa57c1ba4cf2c89b3558cfeeca4339e04551f0fc1a12cf63f1923c2eed8a5be8b8261c35a6040518083600019166000191681526020018281526020019250505060405180910390a161c35a600080600102905094509450611a97565b8160016000600260009054906101000a900463ffffffff1663ffffffff1663ffffffff16815260200190815260200160002081600019169055508a8160000181600019169055508981600101819055508881600201819055508781600301816000191690555086816004018160001916905550858160050160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550600260009054906101000a900463ffffffff168160070160006101000a81548163ffffffff021916908363ffffffff16021790555060018360070160049054906101000a900463ffffffff16018160070160046101000a81548163ffffffff021916908363ffffffff16021790555060018160070160086101000a81548160ff0219169083600581111561199057fe5b02179055506119d583600601548460070160009054906101000a900463ffffffff1660018660070160049054906101000a900463ffffffff160163ffffffff16612055565b8160060181600019169055506002600081819054906101000a900463ffffffff168092919060010191906101000a81548163ffffffff021916908363ffffffff160217905550507f64951c9008bba9f4663c12662e7a9b6412a7c4757869fdac09285564ae923fa182336040518083600019166000191681526020018273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019250505060405180910390a1600082945094505b505050965096945050505050565b6000806000836000191660001916815260200190815260200160002060070160089054906101000a900460ff169050919050565b6000806000600560009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16141515611b92577fa57c1ba4cf2c89b3558cfeeca4339e04551f0fc1a12cf63f1923c2eed8a5be8b8461c3966040518083600019166000191681526020018281526020019250505060405180910390a161c396600080600102905092509250611d11565b6000808560001916600019168152602001908152602001600020905060016005811115611bbb57fe5b8160070160089054906101000a900460ff166005811115611bd857fe5b14158015611c0e575060026005811115611bee57fe5b8160070160089054906101000a900460ff166005811115611c0b57fe5b14155b15611c70577fa57c1ba4cf2c89b3558cfeeca4339e04551f0fc1a12cf63f1923c2eed8a5be8b8461c3646040518083600019166000191681526020018281526020019250505060405180910390a161c364600080600102905092509250611d11565b60028160070160086101000a81548160ff02191690836005811115611c9157fe5b02179055507f09cdaca254aa177f759fe7a0968fe696ee9baf7d2a1d4714ed24b83d1f09518e84336040518083600019166000191681526020018273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019250505060405180910390a1600084925092505b50915091565b6000806000600560009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16141515611dd0577fa57c1ba4cf2c89b3558cfeeca4339e04551f0fc1a12cf63f1923c2eed8a5be8b8461c3966040518083600019166000191681526020018281526020019250505060405180910390a161c396600080600102905092509250611f1b565b6000808560001916600019168152602001908152602001600020905060026005811115611df957fe5b8160070160089054906101000a900460ff166005811115611e1657fe5b141515611e7a577fa57c1ba4cf2c89b3558cfeeca4339e04551f0fc1a12cf63f1923c2eed8a5be8b8461c3646040518083600019166000191681526020018281526020019250505060405180910390a161c364600080600102905092509250611f1b565b60038160070160086101000a81548160ff02191690836005811115611e9b57fe5b02179055507f87f54f5eb3dd119fe71af0915af693e64a5bfd4acaa19a6c944c47cff8eec9e684336040518083600019166000191681526020018273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019250505060405180910390a1600084925092505b50915091565b60045481565b6000600354905090565b60008060008360001916600019168152602001908152602001600020600101549050919050565b611f6061247c565b611f6861247c565b600080600354836000600981101515611f7d57fe5b60200201906000191690816000191681525050611f9b60035461146a565b9150600890505b6000811115612014576001600063ffffffff60010284166001900463ffffffff1663ffffffff168152602001908152602001600020548382600981101515611fe657fe5b60200201906000191690816000191681525050602082600019169060020a9004915080600190039050611fa2565b82935050505090565b6000612028826120be565b9050919050565b600660009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b60008060006005915061206a86600087612404565b9550600190505b60088110801561208c57506001828581151561208957fe5b06145b156120b25761209f868260040287612404565b9550600582029150806001019050612071565b85925050509392505050565b60008060008060008551935060018414156120f3578560008151811015156120e257fe5b9060200190602002015194506123fb565b60008411151561210257600080fd5b60009050600092505b8383101561226a57836001840110612126576001840361212b565b600183015b9150600280612150888681518110151561214157fe5b90602001906020020151612446565b612170898681518110151561216157fe5b90602001906020020151612446565b6040518083600019166000191681526020018260001916600019168152602001925050506020604051808303816000865af11580156121b3573d6000803e3d6000fd5b5050506040513d60208110156121c857600080fd5b81019080805190602001909291905050506040518082600019166000191681526020019150506020604051808303816000865af115801561220d573d6000803e3d6000fd5b5050506040513d602081101561222257600080fd5b8101908080519060200190929190505050868281518110151561224157fe5b90602001906020020190600019169081600019168152505060018101905060028301925061210b565b8093505b60018411156123d75760009050600092505b838310156123cf5783600184011061229b57600184036122a0565b600183015b915060028087858151811015156122b357fe5b9060200190602002015188858151811015156122cb57fe5b906020019060200201516040518083600019166000191681526020018260001916600019168152602001925050506020604051808303816000865af1158015612318573d6000803e3d6000fd5b5050506040513d602081101561232d57600080fd5b81019080805190602001909291905050506040518082600019166000191681526020019150506020604051808303816000865af1158015612372573d6000803e3d6000fd5b5050506040513d602081101561238757600080fd5b810190808051906020019092919050505086828151811015156123a657fe5b906020019060200201906000191690816000191681525050600181019050600283019250612280565b80935061226e565b6123f88660008151811015156123e957fe5b90602001906020020151612446565b94505b50505050919050565b60008060405185815283601c1a8582015383601d1a6001860182015383601e1a6002860182015383601f1a600386018201538051915050809150509392505050565b60008060405160005b602081101561246d578481601f031a8183015360018101905061244f565b50805191505080915050919050565b610120604051908101604052806009906020820280388339808201915050905050905600a165627a7a72305820552eec152148cadd44d34ab75cfcce21bc3bb92a531339aad49ca8b38b450f040029";

    protected static final HashMap<String, String> _addresses;

    static {
        _addresses = new HashMap<>();
        _addresses.put("1528132973147", "0x32726cf78a056df4b5943b6e079f287a5b834cd5");
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

    public static RemoteCall<Superblocks> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Superblocks.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<Superblocks> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Superblocks.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
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

    public RemoteCall<Boolean> isApproved(byte[] _superblockId) {
        final Function function = new Function("isApproved", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_superblockId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
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
