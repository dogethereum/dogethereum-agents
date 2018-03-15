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
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
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
public class DogeRelay extends Contract {
    private static final String BINARY = "0x6060604052341561000f57600080fd5b60405160208061285f8339810160405280805190602001909190505080600560006101000a81548160ff0219169083600281111561004957fe5b0217905550506128018061005e6000396000f3006060604052600436106100af576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff1680631defb765146100b457806333176a3f1461010957806349347272146101a25780635292a590146101cb578063541e9cd71461020657806391fdf3c11461026f578063922407ca1461034b5780639be7076a1461039c578063aa863036146103c5578063c32e6af014610488578063e1bafb911461056a575b600080fd5b34156100bf57600080fd5b6100c76105a3565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b341561011457600080fd5b61018c600480803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509190803590602001909190803573ffffffffffffffffffffffffffffffffffffffff169060200190919050506105c9565b6040518082815260200191505060405180910390f35b34156101ad57600080fd5b6101b56105e3565b6040518082815260200191505060405180910390f35b34156101d657600080fd5b6101f06004808035600019169060200190919050506105ed565b6040518082815260200191505060405180910390f35b341561021157600080fd5b610255600480803590602001909190803567ffffffffffffffff169060200190919080356fffffffffffffffffffffffffffffffff16906020019091905050610cdf565b604051808215151515815260200191505060405180910390f35b341561027a57600080fd5b610335600480803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509190803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509190803590602001909190803573ffffffffffffffffffffffffffffffffffffffff16906020019091905050610d24565b6040518082815260200191505060405180910390f35b341561035657600080fd5b61035e610d96565b6040518082600960200280838360005b8381101561038957808201518184015260208101905061036e565b5050505090500191505060405180910390f35b34156103a757600080fd5b6103af610e24565b6040518082815260200191505060405180910390f35b34156103d057600080fd5b610472600480803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509190803590602001909190803590602001908201803590602001908080602002602001604051908101604052809392919081815260200183836020028082843782019150505050505091908035906020019091905050610e40565b6040518082815260200191505060405180910390f35b341561049357600080fd5b610554600480803590602001908201803590602001908080601f016020809104026020016040519081016040528093929190818152602001838380828437820191505050505050919080359060200190919080359060200190820180359060200190808060200260200160405190810160405280939291908181526020018383602002808284378201915050505050509190803590602001909190803573ffffffffffffffffffffffffffffffffffffffff16906020019091905050610ed7565b6040518082815260200191505060405180910390f35b341561057557600080fd5b6105a1600480803573ffffffffffffffffffffffffffffffffffffffff169060200190919050506110a5565b005b600860009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b60006105da84600086518686611153565b90509392505050565b6000600354905090565b600080600080600080600080600080600860009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff161415156106a9577f6080b2cdc7fc4777ed1e1066af5de1df45ae9186adbdb7aec21fde27a3e17e4c60006001026127426040518083600019166000191681526020018281526020019250505060405180910390a160009950610cd1565b600660008c600190048152602001908152602001600020985088600001600101549750886000016002015496506106df876116fc565b95506000866fffffffffffffffffffffffffffffffff161415610751577f6080b2cdc7fc4777ed1e1066af5de1df45ae9186adbdb7aec21fde27a3e17e4c8860010261272e6040518083600019166000191681526020018281526020019250505060405180910390a160009950610cd1565b600061075c896116fc565b6fffffffffffffffffffffffffffffffff161415156107ca577f6080b2cdc7fc4777ed1e1066af5de1df45ae9186adbdb7aec21fde27a3e17e4c886001026127386040518083600019166000191681526020018281526020019250505060405180910390a160009950610cd1565b8860000160000160089054906101000a900463ffffffff1694506107ed8761173a565b60010167ffffffffffffffff1693506108058761177e565b925060028081111561081357fe5b600560009054906101000a900460ff16600281111561082e57fe5b141515610a27576000600160009054906101000a900463ffffffff1663ffffffff1614156108d8578263ffffffff168563ffffffff1614158015610879575060008363ffffffff1614155b156108d3577f6080b2cdc7fc4777ed1e1066af5de1df45ae9186adbdb7aec21fde27a3e17e4c8860010261271a6040518083600019166000191681526020018281526020019250505060405180910390a160009950610cd1565b610a26565b60018060009054906101000a900463ffffffff1663ffffffff1614156108fd57610a25565b61092d61091161090c896117b1565b6117d4565b63ffffffff16610920896117d4565b63ffffffff160384611807565b91506001600281111561093c57fe5b600560009054906101000a900460ff16600281111561095757fe5b14801561098b5750607861096a886117d4565b8a60000160000160049054906101000a900463ffffffff160363ffffffff16115b801561099a57506202673c8410155b156109a757631e0fffff91505b8163ffffffff168563ffffffff16141580156109ca575060008263ffffffff1614155b15610a24577f6080b2cdc7fc4777ed1e1066af5de1df45ae9186adbdb7aec21fde27a3e17e4c886001026127246040518083600019166000191681526020018281526020019250505060405180910390a160009950610cd1565b5b5b5b88600260008a815260200190815260200160002060008201816000016000820160009054906101000a900463ffffffff168160000160006101000a81548163ffffffff021916908363ffffffff1602179055506000820160049054906101000a900463ffffffff168160000160046101000a81548163ffffffff021916908363ffffffff1602179055506000820160089054906101000a900463ffffffff168160000160086101000a81548163ffffffff021916908363ffffffff16021790555060008201600c9054906101000a900463ffffffff1681600001600c6101000a81548163ffffffff021916908363ffffffff16021790555060018201548160010155600282015481600201556003820154816003015550506004820154816004015560058201548160050155905050610b608888611909565b600660008c6001900481526020019081526020016000206000808201600080820160006101000a81549063ffffffff02191690556000820160046101000a81549063ffffffff02191690556000820160086101000a81549063ffffffff021916905560008201600c6101000a81549063ffffffff02191690556001820160009055600282016000905560038201600090555050600482016000905560058201600090555050610c0e85611a66565b7d0fffff000000000000000000000000000000000000000000000000000000811515610c3657fe5b0486019050610c458882611aaa565b600454816fffffffffffffffffffffffffffffffff16101515610c835787600381905550806fffffffffffffffffffffffffffffffff166004819055505b7f6080b2cdc7fc4777ed1e1066af5de1df45ae9186adbdb7aec21fde27a3e17e4c88600102856040518083600019166000191681526020018281526020019250505060405180910390a18399505b505050505050505050919050565b600080600454141515610cf55760009050610d1d565b600160048190555083600381905550610d0e8484611af3565b610d188483611aaa565b600190505b9392505050565b600080600080600080935060009250600091505b86821015610d8a57610d4a8985611b3c565b63ffffffff169050600484019350610d72898583610d688c88611d51565b600190048a611153565b94508084019350602083019250600182019150610d38565b50505050949350505050565b610d9e612742565b600080600354915081836000600981101515610db657fe5b602002018181525050600090505b6008811015610e1c57600080610dda8484611d67565b63ffffffff1663ffffffff168152602001908152602001600020548360018301600981101515610e0657fe5b6020020181815250508080600101915050610dc4565b829250505090565b6000610e3160035461173a565b67ffffffffffffffff16905090565b600080610e4c86611db8565b9050604086511415610ead577f65bd72698b9ffcfb3c7cb4c7414e13225cabd57fb690e183ae8c01c8ec268ebd81600102614e526040518083600019166000191681526020018281526020019250505060405180910390a160009150610ece565b6001610ebb82878787611e9e565b1415610ec957809150610ece565b600091505b50949350505050565b6000806000610ee888888888610e40565b9150600082141515611047578373ffffffffffffffffffffffffffffffffffffffff16631c0b636789846000604051602001526040518363ffffffff167c01000000000000000000000000000000000000000000000000000000000281526004018080602001838152602001828103825284818151815260200191508051906020019080838360005b83811015610f8c578082015181840152602081019050610f71565b50505050905090810190601f168015610fb95780820380516001836020036101000a031916815260200191505b509350505050602060405180830381600087803b1515610fd857600080fd5b6102c65a03f11515610fe957600080fd5b5050506040518051905090507f4e64138cc499eb1adf9edff9ef69bd45c56ac4bfd307540952e4c9d51eab55c182600102826040518083600019166000191681526020018281526020019250505060405180910390a180925061109a565b7f4e64138cc499eb1adf9edff9ef69bd45c56ac4bfd307540952e4c9d51eab55c1600060010261753a6040518083600019166000191681526020018281526020019250505060405180910390a161753a92505b505095945050505050565b6000600860009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16148015611104575060008173ffffffffffffffffffffffffffffffffffffffff1614155b151561110f57600080fd5b80600860006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555050565b60008060508510156111b5577f6080b2cdc7fc4777ed1e1066af5de1df45ae9186adbdb7aec21fde27a3e17e4c60006001026127426040518083600019166000191681526020018281526020019250505060405180910390a1600091506116f2565b6007600081546001019190508190555060066000600754815260200190815260200160002090506111e68787611fcd565b8160000160008201518160000160006101000a81548163ffffffff021916908363ffffffff16021790555060208201518160000160046101000a81548163ffffffff021916908363ffffffff16021790555060408201518160000160086101000a81548163ffffffff021916908363ffffffff160217905550606082015181600001600c6101000a81548163ffffffff021916908363ffffffff1602179055506080820151816001015560a0820151816002015560c082015181600301559050506112c78160000160000160089054906101000a900463ffffffff16611a66565b6112d0856120cb565b1115611332577f6080b2cdc7fc4777ed1e1066af5de1df45ae9186adbdb7aec21fde27a3e17e4c816000016001015460010261276a6040518083600019166000191681526020018281526020019250505060405180910390a1600091506116f2565b60007f01000000000000000000000000000000000000000000000000000000000000000260017f010000000000000000000000000000000000000000000000000000000000000002886001890181518110151561138b57fe5b9060200101517f010000000000000000000000000000000000000000000000000000000000000090047f010000000000000000000000000000000000000000000000000000000000000002167effffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff191614151561157b57600860009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16634754efd0611450896050898b0103898b016120fc565b86600102866007546001026040518563ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808060200185600019166000191681526020018473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020018360001916600019168152602001828103825286818151815260200191508051906020019080838360005b838110156115145780820151818401526020810190506114f9565b50505050905090810190601f1680156115415780820380516001836020036101000a031916815260200191505b5095505050505050600060405180830381600087803b151561156257600080fd5b6102c65a03f1151561157357600080fd5b5050506116ed565b600860009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16634754efd06115c689600060506120fc565b86600102866007546001026040518563ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808060200185600019166000191681526020018473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020018360001916600019168152602001828103825286818151815260200191508051906020019080838360005b8381101561168a57808201518184015260208101905061166f565b50505050905090810190601f1680156116b75780820380516001836020036101000a031916815260200191505b5095505050505050600060405180830381600087803b15156116d857600080fd5b6102c65a03f115156116e957600080fd5b5050505b600191505b5095945050505050565b60007001000000000000000000000000000000008060026000858152602001908152602001600020600401540281151561173257fe5b049050919050565b60007801000000000000000000000000000000000000000000000000600260008481526020019081526020016000206004015481151561177657fe5b049050919050565b60006002600083815260200190815260200160002060000160000160089054906101000a900463ffffffff169050919050565b600060026000838152602001908152602001600020600001600201549050919050565b60006002600083815260200190815260200160002060000160000160049054906101000a900463ffffffff169050919050565b600080600080600080603c9450879350600860070b85850360070b81151561182b57fe5b0585019350600460070b8560070b81151561184257fe5b0585039250600260070b8560070b81151561185957fe5b05850191508260070b8460070b121561187457829350611887565b8160070b8460070b1315611886578193505b5b61189087611a66565b90508360070b810290508460070b818115156118a857fe5b0490507d0fffffffffffffffffffffffffffffffffffffffffffffffffffffffffff8111156118f3577d0fffffffffffffffffffffffffffffffffffffffffffffffffffffffffff90505b6118fc81612163565b9550505050505092915050565b60008060008085600080600160009054906101000a900463ffffffff1663ffffffff1663ffffffff1681526020019081526020016000208190555061196086600160009054906101000a900463ffffffff16612209565b60018060008282829054906101000a900463ffffffff160192506101000a81548163ffffffff021916908363ffffffff1602179055506119ab8660016119a58861173a565b01611af3565b600093506119b88561225d565b92506119c6846000856122ac565b93506119d18661173a565b67ffffffffffffffff169150600190505b6008811015611a435760016119f6826122e9565b83811515611a0057fe5b061415611a1c57611a158482600402856122ac565b9350611a36565b611a338482600402611a2e8885611d67565b6122ac565b93505b80806001019150506119e2565b836002600088815260200190815260200160002060050181905550505050505050565b600080600063010000008463ffffffff16811515611a8057fe5b0463ffffffff16915062ffffff841663ffffffff169050600382036101000a810292505050919050565b600060026000848152602001908152602001600020600401549050611ad1816010846122f6565b9050806002600085815260200190815260200160002060040181905550505050565b600060026000848152602001908152602001600020600401549050611b1a816000846123b7565b9050806002600085815260200190815260200160002060040181905550505050565b60008260038301815181101515611b4f57fe5b9060200101517f010000000000000000000000000000000000000000000000000000000000000090047f0100000000000000000000000000000000000000000000000000000000000000027f010000000000000000000000000000000000000000000000000000000000000090046101008460028501815181101515611bd157fe5b9060200101517f010000000000000000000000000000000000000000000000000000000000000090047f0100000000000000000000000000000000000000000000000000000000000000027f0100000000000000000000000000000000000000000000000000000000000000900402620100008560018601815181101515611c5557fe5b9060200101517f010000000000000000000000000000000000000000000000000000000000000090047f0100000000000000000000000000000000000000000000000000000000000000027f010000000000000000000000000000000000000000000000000000000000000090040263010000008686815181101515611cd757fe5b9060200101517f010000000000000000000000000000000000000000000000000000000000000090047f0100000000000000000000000000000000000000000000000000000000000000027f0100000000000000000000000000000000000000000000000000000000000000900402010101905092915050565b6000808260208501015190508091505092915050565b60007c01000000000000000000000000000000000000000000000000000000008260200260020a600260008681526020019081526020016000206005015402811515611daf57fe5b04905092915050565b6000611e97600280846000604051602001526040518082805190602001908083835b602083101515611dff5780518252602082019150602081019050602083039250611dda565b6001836020036101000a03801982511681845116808217855250505050505090500191505060206040518083038160008661646e5a03f11515611e4157600080fd5b50506040518051905060006040516020015260405180826000191660001916815260200191505060206040518083038160008661646e5a03f11515611e8557600080fd5b505060405180519050600190046120cb565b9050919050565b6000611ea982612420565b15611f04577f65bd72698b9ffcfb3c7cb4c7414e13225cabd57fb690e183ae8c01c8ec268ebd85600102614e346040518083600019166000191681526020018281526020019250505060405180910390a1614e349050611fc5565b611f0d8261246b565b611f1886868661248e565b141515611f75577f65bd72698b9ffcfb3c7cb4c7414e13225cabd57fb690e183ae8c01c8ec268ebd85600102614e486040518083600019166000191681526020018281526020019250505060405180910390a1614e489050611fc5565b7f65bd72698b9ffcfb3c7cb4c7414e13225cabd57fb690e183ae8c01c8ec268ebd8560010260016040518083600019166000191681526020018281526020019250505060405180910390a1600190505b949350505050565b611fd561276b565b611fdf838361252e565b816000019063ffffffff16908163ffffffff16815250506120008383612562565b816020019063ffffffff16908163ffffffff16815250506120218383612596565b816040019063ffffffff16908163ffffffff16815250506120966002612049858560506125ca565b60006040516020015260405180826000191660001916815260200191505060206040518083038160008661646e5a03f1151561208457600080fd5b505060405180519050600190046120cb565b8160800181815250506120a983836125fd565b8160a00181815250506120bc838361261b565b8160c001818152505092915050565b600060405160005b60208110156120f1578381601f031a818301536001810190506120d3565b508051915050919050565b6121046127c1565b600061210e6127c1565b8484039150816040518059106121215750595b9080825280601f01601f191660200182016040525090508160208201838760208a010160006004600019f1151561215757600080fd5b80925050509392505050565b600080600061217e600761217686612639565b016003612667565b9150600090506003821115156121aa576121a362ffffff851683600303600802612680565b90506121c5565b6121ba8460038403600802612667565b905062ffffff811690505b600062800000821663ffffffff1611156121f3576121ea8163ffffffff166008612667565b90506001820191505b6121fe826018612680565b811792505050919050565b600080600260008581526020019081526020016000206004015491508263ffffffff16905061223a826008836123b7565b915081600260008681526020019081526020016000206004018190555050505050565b60007801000000000000000000000000000000000000000000000000680100000000000000006002600085815260200190815260200160002060040154028115156122a457fe5b049050919050565b600060405184815282601c1a8482015382601d1a6001850182015382601e1a6002850182015382601f1a6003850182015380519150509392505050565b60008160050a9050919050565b60006040518481528260101a848201538260111a600185018201538260121a600285018201538260131a600385018201538260141a600485018201538260151a600585018201538260161a600685018201538260171a600785018201538260181a600885018201538260191a6009850182015382601a1a600a850182015382601b1a600b850182015382601c1a600c850182015382601d1a600d850182015382601e1a600e850182015382601f1a600f850182015380519150509392505050565b60006040518481528260181a848201538260191a6001850182015382601a1a6002850182015382601b1a6003850182015382601c1a6004850182015382601d1a6005850182015382601e1a6006850182015382601f1a6007850182015380519150509392505050565b60008060006003549150600090505b600681101561245f57818414156124495760019250612464565b612452826117b1565b915060018101905061242f565b600092505b5050919050565b600060026000838152602001908152602001600020600001600301549050919050565b6000806000806000806000899550600094505b875185101561251e5787858151811015156124b857fe5b9060200190602002015193506002898115156124d057fe5b06925060018314156124e7578391508590506124f8565b60008314156124f7578591508390505b5b6125028282612690565b955060028981151561251057fe5b0498506001850194506124a1565b8596505050505050509392505050565b600081600484010151630100000081601b1a026201000082601a1a02016101008260191a02018160181a0191505092915050565b600081604c84010151630100000081601b1a026201000082601a1a02016101008260191a02018160181a0191505092915050565b600081605084010151630100000081601b1a026201000082601a1a02016101008260191a02018160181a0191505092915050565b60006040516020810160405260208184866020890101600060025af115156125f157600080fd5b80519150509392505050565b600080826024850101519050612612816120cb565b91505092915050565b600080826044850101519050612630816120cb565b91505092915050565b6000808290505b600081111561266157612654816001612667565b9050600182019150612640565b50919050565b60008160020a8381151561267757fe5b04905092915050565b60008160020a8302905092915050565b600061273a6002806126a1866120cb565b6126aa866120cb565b600060405160200152604051808381526020018281526020019250505060206040518083038160008661646e5a03f115156126e457600080fd5b50506040518051905060006040516020015260405180826000191660001916815260200191505060206040518083038160008661646e5a03f1151561272857600080fd5b505060405180519050600190046120cb565b905092915050565b610120604051908101604052806009905b60008152602001906001900390816127535790505090565b60e060405190810160405280600063ffffffff168152602001600063ffffffff168152602001600063ffffffff168152602001600063ffffffff1681526020016000815260200160008152602001600081525090565b6020604051908101604052806000815250905600a165627a7a72305820ae1d48dd15f6bb49de5813c13e97f784dc1dc06f209cae377d3820b9bfaf80740029";

    protected static final HashMap<String, String> _addresses;

    static {
        _addresses = new HashMap<>();
        _addresses.put("1521062677956", "0xdc3a2d97432696796e76ea48e3f84d1a7c61862f");
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
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Uint256>() {}));
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(event, transactionReceipt);
        ArrayList<StoreHeaderEventResponse> responses = new ArrayList<StoreHeaderEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            StoreHeaderEventResponse typedResponse = new StoreHeaderEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.blockHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.returnCode = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<StoreHeaderEventResponse> storeHeaderEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("StoreHeader", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, StoreHeaderEventResponse>() {
            @Override
            public StoreHeaderEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);
                StoreHeaderEventResponse typedResponse = new StoreHeaderEventResponse();
                typedResponse.log = log;
                typedResponse.blockHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.returnCode = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public List<GetHeaderEventResponse> getGetHeaderEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("GetHeader", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Uint256>() {}));
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(event, transactionReceipt);
        ArrayList<GetHeaderEventResponse> responses = new ArrayList<GetHeaderEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            GetHeaderEventResponse typedResponse = new GetHeaderEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.blockHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.returnCode = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<GetHeaderEventResponse> getHeaderEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("GetHeader", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, GetHeaderEventResponse>() {
            @Override
            public GetHeaderEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);
                GetHeaderEventResponse typedResponse = new GetHeaderEventResponse();
                typedResponse.log = log;
                typedResponse.blockHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.returnCode = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
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

    public RemoteCall<String> scryptChecker() {
        final Function function = new Function("scryptChecker", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public static RemoteCall<DogeRelay> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, BigInteger _network) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint8(_network)));
        return deployRemoteCall(DogeRelay.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static RemoteCall<DogeRelay> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, BigInteger _network) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint8(_network)));
        return deployRemoteCall(DogeRelay.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public RemoteCall<TransactionReceipt> setScryptChecker(String _scryptChecker) {
        final Function function = new Function(
                "setScryptChecker", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_scryptChecker)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> setInitialParent(BigInteger _blockHash, BigInteger _height, BigInteger _chainWork) {
        final Function function = new Function(
                "setInitialParent", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_blockHash), 
                new org.web3j.abi.datatypes.generated.Uint64(_height), 
                new org.web3j.abi.datatypes.generated.Uint128(_chainWork)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> storeBlockHeader(byte[] _blockHeaderBytes, BigInteger _proposedScryptBlockHash, String _truebitClaimantAddress) {
        final Function function = new Function(
                "storeBlockHeader", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicBytes(_blockHeaderBytes), 
                new org.web3j.abi.datatypes.generated.Uint256(_proposedScryptBlockHash), 
                new org.web3j.abi.datatypes.Address(_truebitClaimantAddress)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> scryptVerified(byte[] _proposalId) {
        final Function function = new Function(
                "scryptVerified", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_proposalId)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> bulkStoreHeaders(byte[] _headersBytes, byte[] _hashesBytes, BigInteger count, String truebitClaimantAddress) {
        final Function function = new Function(
                "bulkStoreHeaders", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicBytes(_headersBytes), 
                new org.web3j.abi.datatypes.DynamicBytes(_hashesBytes), 
                new org.web3j.abi.datatypes.generated.Uint256(count), 
                new org.web3j.abi.datatypes.Address(truebitClaimantAddress)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> verifyTx(byte[] _txBytes, BigInteger _txIndex, List<BigInteger> _siblings, BigInteger _txBlockHash) {
        final Function function = new Function(
                "verifyTx", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicBytes(_txBytes), 
                new org.web3j.abi.datatypes.generated.Uint256(_txIndex), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint256>(
                        org.web3j.abi.Utils.typeMap(_siblings, org.web3j.abi.datatypes.generated.Uint256.class)), 
                new org.web3j.abi.datatypes.generated.Uint256(_txBlockHash)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> relayTx(byte[] _txBytes, BigInteger _txIndex, List<BigInteger> _siblings, BigInteger _txBlockHash, String _targetContract) {
        final Function function = new Function(
                "relayTx", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicBytes(_txBytes), 
                new org.web3j.abi.datatypes.generated.Uint256(_txIndex), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint256>(
                        org.web3j.abi.Utils.typeMap(_siblings, org.web3j.abi.datatypes.generated.Uint256.class)), 
                new org.web3j.abi.datatypes.generated.Uint256(_txBlockHash), 
                new org.web3j.abi.datatypes.Address(_targetContract)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<List> getBlockLocator() {
        final Function function = new Function("getBlockLocator", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<StaticArray9<Uint256>>() {}));
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

    public RemoteCall<BigInteger> getBestBlockHeight() {
        final Function function = new Function("getBestBlockHeight", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> getBestBlockHash() {
        final Function function = new Function("getBestBlockHash", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
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
        public Log log;

        public byte[] blockHash;

        public BigInteger returnCode;
    }

    public static class GetHeaderEventResponse {
        public Log log;

        public byte[] blockHash;

        public BigInteger returnCode;
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
