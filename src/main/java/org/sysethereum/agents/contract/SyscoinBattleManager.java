package org.sysethereum.agents.contract;

import io.reactivex.Flowable;
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
import org.web3j.abi.datatypes.generated.Uint32;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple11;
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
 * <p>Generated with web3j version 4.2.0.
 */
public class SyscoinBattleManager extends Contract {
    private static final String BINARY = "0x608060405234801561001057600080fd5b5060405160808061257683398101604090815281516020830151918301516060909301516003805492949285919060ff1916600183600281111561005057fe5b021790555060048054600160a060020a031916600160a060020a039490941693909317909255600155600255506124ea8061008c6000396000f30060806040526004361061010d5763ffffffff60e060020a6000350416626622a48114610112578063089845e1146101325780630f2c63ff1461019057806318b011de146101ab5780632c1ae450146101d25780633678c14314610232578063455e6166146102535780634955d085146102685780635704a5fa1461027d57806361bd8d66146102e55780636ca640a1146102fa57806371a8c18a1461030f578063795ea18e1461033b5780637dbd28321461035357806390b6f699146103ee57806399b32f3a14610403578063a6c07c961461041b578063ba16d60014610433578063d035c40314610448578063d1daeede1461045d578063eda1970b14610487578063f1afcfa61461049c575b600080fd5b34801561011e57600080fd5b506101306004356024356044356104b1565b005b34801561013e57600080fd5b5060408051602060046044358181013583810280860185019096528085526101309583359560248035963696956064959394920192918291850190849080828437509497506105a09650505050505050565b34801561019c57600080fd5b506101306004356024356106df565b3480156101b757600080fd5b506101c06107c5565b60408051918252519081900360200190f35b3480156101de57600080fd5b50604080516020600460443581810135601f81018490048402850184019095528484526101309482359460248035953695946064949201919081908401838280828437509497506107cb9650505050505050565b34801561023e57600080fd5b50610130600160a060020a03600435166108c0565b34801561025f57600080fd5b506101c0610926565b34801561027457600080fd5b506101c0610932565b34801561028957600080fd5b50610295600435610938565b60408051602080825283518183015283519192839290830191858101910280838360005b838110156102d15781810151838201526020016102b9565b505050509050019250505060405180910390f35b3480156102f157600080fd5b506101c061099c565b34801561030657600080fd5b506101c06109a3565b34801561031b57600080fd5b506103276004356109af565b604080519115158252519081900360200190f35b34801561034757600080fd5b506101c06004356109e4565b34801561035f57600080fd5b5061036b600435610ad3565b604080518c8152602081018c9052600160a060020a03808c169282019290925290891660608201526080810188905260a0810187905260c0810186905260e081018590526101008101849052610120810183905261014081018260088111156103d057fe5b60ff1681526020019b50505050505050505050505060405180910390f35b3480156103fa57600080fd5b506101c0610b3b565b34801561040f57600080fd5b50610327600435610b42565b34801561042757600080fd5b50610130600435610b74565b34801561043f57600080fd5b506101c0610bed565b34801561045457600080fd5b506101c0610bf9565b34801561046957600080fd5b506101c0600435600160a060020a0360243581169060443516610c00565b34801561049357600080fd5b506101c0610dd3565b3480156104a857600080fd5b506101c0610dd9565b60008281526020819052604081206003015481908490600160a060020a031633146104db57600080fd5b600085815260208190526040902092506104f58385610ddf565b9150811561052b576040805186815260208101849052815160008051602061249f833981519152929181900390910190a1610598565b60078301805460010190819055426004850155600684015560028301546040805188815260208101889052600160a060020a039092168282015260608201869052517fc32d73f54fbafb3a4f05d1f05fa0d120659da2f8494eee5a94442fcda572f4159181900360800190a15b505050505050565b60008281526020819052604081206002015481908490600160a060020a031633146105ca57600080fd5b600085815260208190526040902092506105e48385610f2e565b9150811561061a576040805186815260208101849052815160008051602061249f833981519152929181900390910190a1610598565b6007830180546001019081905542600485015560058401556003830154604080518881526020808201899052600160a060020a0390931691810182905260806060820181815288519183019190915287517fbca3431b78418303a430f83db37413835aa6c34f2bc94b5cb93f3c1a56384e69948b948b9490938b939192909160a0840191808601910280838360005b838110156106c15781810151838201526020016106a9565b505050509050019550505050505060405180910390a1505050505050565b60008181526020819052604081206003015481908390600160a060020a0316331461070957600080fd5b60008481526020819052604090209250610722836110fe565b91508115610758576040805185815260208101849052815160008051602061249f833981519152929181900390910190a16107be565b60078301805460010190819055426004850155600684015560028301546040805187815260208101879052600160a060020a0390921682820152517f0817bf136ff95abb2d41d10a9fa5ff6652ff71c13e1b46717fae65db16423c9b9181900360600190a15b5050505050565b60025481565b600082815260208190526040812060020154819081908590600160a060020a031633146107f757600080fd5b60008681526020819052604090209350610811848661118c565b9093509150821561084a576040805187815260208101859052815160008051602061249f833981519152929181900390910190a16108b7565b60078401805460010190819055426004860155600585015560038401546040805189815260208101899052600160a060020a039092168282015260608201849052517feefa5143430a5f9c1deb10d06937531503b1a37147bc45d33654ef02a473eabd9181900360800190a15b50505050505050565b6003546101009004600160a060020a03161580156108e65750600160a060020a03811615155b15156108f157600080fd5b60038054600160a060020a039092166101000274ffffffffffffffffffffffffffffffffffffffff0019909216919091179055565b670de0b6b3a76484d081565b619c4081565b6000818152602081815260409182902060080180548351818402810184019094528084526060939283018282801561099057602002820191906000526020600020905b8154815260019091019060200180831161097b575b50505050509050919050565b6206b6c081565b670de0b6b3a76ab6c081565b6000818152602081905260408120600581015460068201541180156109db575060025481600401540142115b91505b50919050565b60008181526020819052604081206008600c82015460ff166008811115610a0757fe5b1480610a2c575080600501548160060154118015610a2c575060025481600401540142115b15610a5b5760028101546001820154610a52918591600160a060020a0390911690611311565b600091506109de565b80600601548160050154118015610a79575060025481600401540142115b15610a9f5760038101546001820154610a52918591600160a060020a039091169061139a565b6040805184815261c36e6020820152815160008051602061249f833981519152929181900390910190a15061c36e92915050565b6000602081905290815260409020805460018201546002830154600384015460048501546005860154600687015460078801546009890154600a8a0154600c909a015498999798600160a060020a039788169897909616969495939492939192909160ff168b565b6205c49081565b6000818152602081905260408120600681015460058201541180156109db575060025460049091015401421192915050565b600081815260208190526040812090610b8d8284611423565b90508060011415610bbe5760038201546001830154610bb9918591600160a060020a039091169061139a565b610be8565b8060021415610be85760028201546001830154610be8918591600160a060020a0390911690611311565b505050565b670de0b6b3a764000081565b62035b6081565b600354600090819081906101009004600160a060020a0316338114610c2457600080fd5b6040805160208082018a90523382840152825180830384018152606090920192839052815191929182918401908083835b60208310610c745780518252601f199092019160209182019101610c55565b6001836020036101000a03801982511681845116808217855250505050505090500191505060405180910390209250600080846000191660001916815260200190815260200160002091508282600001816000191690555086826001018160001916905550858260020160006101000a815481600160a060020a030219169083600160a060020a03160217905550848260030160006101000a815481600160a060020a030219169083600160a060020a03160217905550428260040181905550600082600601819055506001826005018190555060018260070181905550600182600c0160006101000a81548160ff02191690836008811115610d7357fe5b02179055506040805188815260208101859052600160a060020a03808916828401528716606082015290517f403956bdc140717d54d4573786b4e9e773ef2e6e325e2c061476eb47711770de9181900360800190a1509095945050505050565b6184d081565b60015481565b600080610dee33619c406114fe565b1515610dfe5761c38c9150610f27565b6009840154158015610e2257506003600c85015460ff166008811115610e2057fe5b145b80610e4e575060008460090154118015610e4e57506005600c85015460ff166008811115610e4c57fe5b145b15610f21576008840154600985015410610e6757600080fd5b600080848152600b860160205260409020600101546c01000000000000000000000000900460ff166002811115610e9a57fe5b14610ea457600080fd5b610eb5846001015433619c406115a2565b90508015610ec557809150610f27565b60098401805460019081019091556000848152600b86016020526040812090910180546c010000000000000000000000006cff00000000000000000000000019909116179055600c8501805460ff191660041790559150610f27565b61c36491505b5092915050565b600080600080610f413362035b606114fe565b1515610f515761c38c93506110f5565b600886015415610f6057600080fd5b6002600c87015460ff166008811115610f7557fe5b14156110ef57610f888660010154611653565b505050505095505050509250846001865103815181101515610fa657fe5b602090810290910101518214610fc05761c3e693506110f5565b6040517ff9b5d7c000000000000000000000000000000000000000000000000000000000815260206004820181815287516024840152875173__SyscoinMessageLibrary_________________9363f9b5d7c0938a9392839260440191808601910280838360005b83811015611040578181015183820152602001611028565b505050509050019250505060206040518083038186803b15801561106357600080fd5b505af4158015611077573d6000803e3d6000fd5b505050506040513d602081101561108d57600080fd5b5051831461109f5761c37893506110f5565b6110b186600101543362035b606115a2565b905080156110c1578093506110f5565b84516110d69060088801906020880190612413565b50600c8601805460ff19166003179055600093506110f5565b61c36493505b50505092915050565b60008061110e336205c4906114fe565b151561111e5761c38c91506109de565b6001600c84015460ff16600881111561113357fe5b141561118257600c8301805460ff19166002179055600383015433600160a060020a039091161461116057fe5b6111728360010154336205c4906115a2565b90508015610a52578091506109de565b5061c36492915050565b60008060008060006111a033619c406114fe565b15156111b45761c38c945060009350611307565b6004600c88015460ff1660088111156111c957fe5b14156112fd576111dc8660006050611784565b6000818152600b89016020526040902090935091506001808301546c01000000000000000000000000900460ff16600281111561121557fe5b146112285761c369945060009350611307565b611236876001015487611845565b151561124a5761c373945060009350611307565b6112558284886118a7565b905080156112695793506000925083611307565b600182810180546cff00000000000000000000000019166c020000000000000000000000001790558701546112a19033619c406115a2565b905080156112b55793506000925083611307565b600a8701805460010190819055600888015414156112e157600c8701805460ff191660061790556112f1565b600c8701805460ff191660051790555b60008394509450611307565b61c3649450600093505b5050509250929050565b6000838152602081905260409020600381015460028201546113439186918591600160a060020a039081169116611a22565b61134c84611ac3565b6040805183815260208101869052600160a060020a0385168183015290517faab6a8f22c7ab5131c1cdc1c0000e123efd38efadfef092cef78be507d16542e9181900360600190a150505050565b6000838152602081905260409020600281015460038201546113cc9186918591600160a060020a039081169116611a22565b6113d584611ac3565b6040805183815260208101869052600160a060020a0385168183015290517fda9b5fdafb0f67d811425d3f095917a63329d60d3db3aa456b5247a8f3b88b119181900360600190a150505050565b6000806006600c85015460ff16600881111561143b57fe5b14156114d05761144a84611b4f565b90508015611484576040805184815260208101839052815160008051602061249f833981519152929181900390910190a160029150610f27565b61148d84611c06565b905080156114c7576040805184815260208101839052815160008051602061249f833981519152929181900390910190a160029150610f27565b60019150610f27565b6008600c85015460ff1660088111156114e557fe5b14156114f45760029150610f27565b5060009392505050565b600354604080517fe1254fba000000000000000000000000000000000000000000000000000000008152600160a060020a03858116600483015291516000938593610100909104169163e1254fba91602480830192602092919082900301818887803b15801561156d57600080fd5b505af1158015611581573d6000803e3d6000fd5b505050506040513d602081101561159757600080fd5b505110159392505050565b600354604080517fc2569e5a00000000000000000000000000000000000000000000000000000000815260048101869052600160a060020a03858116602483015260448201859052915160009361010090049092169163c2569e5a9160648082019260209290919082900301818787803b15801561161f57600080fd5b505af1158015611633573d6000803e3d6000fd5b505050506040513d602081101561164957600080fd5b5051949350505050565b60048054604080517f6e5b707100000000000000000000000000000000000000000000000000000000815292830184905251600092839283928392839283928392839283928392600160a060020a0390921691636e5b70719160248083019261014092919082900301818787803b1580156116cd57600080fd5b505af11580156116e1573d6000803e3d6000fd5b505050506040513d6101408110156116f857600080fd5b810190808051906020019092919080519060200190929190805190602001909291908051906020019092919080519060200190929190805190602001909291908051906020019092919080519060200190929190805190602001909291908051906020019092919050505099509950995099509950995099509950995099509193959799509193959799565b600061183d6002611796868686611cea565b6040805160208082019390935281518082038401815290820191829052805190928291908401908083835b602083106117e05780518252601f1990920191602091820191016117c1565b51815160209384036101000a600019018019909216911617905260405191909301945091925050808303816000865af1158015611821573d6000803e3d6000fd5b5050506040513d602081101561183657600080fd5b5051611d11565b949350505050565b600080600061185384611d3c565b63ffffffff16915061186485611653565b5095985050505085871180159550935061189e925050505750600180548281151561188b57fe5b04036001548381151561189a57fe5b0410155b95945050505050565b60008073__SyscoinMessageLibrary_________________6376837a1384600087600190046040518463ffffffff1660e060020a0281526004018080602001848152602001838152602001828103825285818151815260200191508051906020019080838360005b8381101561192757818101518382015260200161190f565b50505050905090810190601f1680156119545780820380516001836020036101000a031916815260200191505b5094505050505060206040518083038186803b15801561197357600080fd5b505af4158015611987573d6000803e3d6000fd5b505050506040513d602081101561199d57600080fd5b5051905080156119af57809150611a1a565b6119b883611d3c565b60018601805467ffffffffffffffff191663ffffffff929092169190911790556119e183611d4f565b8560010160086101000a81548163ffffffff021916908363ffffffff160217905550611a0c83611d5c565b855560028501849055600091505b509392505050565b600354604080517f748a00e20000000000000000000000000000000000000000000000000000000081526004810187905260248101869052600160a060020a038581166044830152848116606483015291516101009093049091169163748a00e29160848082019260009290919082900301818387803b158015611aa557600080fd5b505af1158015611ab9573d6000803e3d6000fd5b5050505050505050565b60008181526020819052604081208181556001810182905560028101805473ffffffffffffffffffffffffffffffffffffffff1990811690915560038201805490911690556004810182905560058101829055600681018290556007810182905590611b326008830182612460565b50600060098201819055600a820155600c01805460ff1916905550565b60008060008060008060008760080180549050111515611b735761c3e69550611bfc565b611b808760010154611653565b50505060088e018054959c50939a50909850965090935050600019810191508110611ba757fe5b6000918252602080832090910154808352600b8a0190915260409091206001015490915067ffffffffffffffff168514611be55761c3739550611bfc565b84841115611bf75761c3739550611bfc565b600095505b5050505050919050565b600080600080600080600080600080611c228b60010154611653565b9091929394959697985090919293949596509091929394509091509050809a50819c50829850839950849d505050505050611c5c88611653565b90919293949596979850909192939495509091929350909192509091509050809a50819b50829650839550505050508a60080180549050870163ffffffff168663ffffffff16141515611cb7578563ffffffff169950611cdc565b611cc78b88848c89888a8f611d6d565b90508015611cd757809950611cdc565b600099505b505050505050505050919050565b60006040516020818486602089010160025afa1515611d0857600080fd5b51949350505050565b600060405160005b6020811015611d34578381601f031a81830153600101611d19565b505192915050565b6000611d49826044612358565b92915050565b6000611d49826048612358565b60248101516000906109db81611d11565b600080808080805b60088e015463ffffffff861610156122d75760088e01805463ffffffff8716908110611d9d57fe5b906000526020600020015493508d600b016000856000191660001916815260200190815260200160002060010160089054906101000a900463ffffffff1692507f06dcccda64f812dfaf9409154ccccaf2df1dc6f0896f5352f0918f9e5630e49d8e600801805490508f600b016000876000191660001916815260200190815260200160002060010160009054906101000a900467ffffffffffffffff168e8e878d604051808781526020018667ffffffffffffffff1681526020018581526020018481526020018363ffffffff1663ffffffff1681526020018263ffffffff1663ffffffff168152602001965050505050505060405180910390a16000848152600b8f0160205260409020548714611eba5761c3829550612347565b600260035460ff166002811115611ecd57fe5b146121c757600160035460ff166002811115611ee557fe5b148015611f1457506000848152600b8f016020526040902060010154607867ffffffffffffffff909116829003115b15611f2557631e0fffff91506121ad565b73__SyscoinMessageLibrary_________________63f28843856040518163ffffffff1660e060020a02815260040160206040518083038186803b158015611f6c57600080fd5b505af4158015611f80573d6000803e3d6000fd5b505050506040513d6020811015611f9657600080fd5b505160070b63ffffffff60018f88010116811515611fb057fe5b0760070b15611fc1578791506121ad565b63ffffffff85161515611fd5575087611ff5565b506000868152600b8e01602052604090206001015467ffffffffffffffff165b60408051828152602081018c905263ffffffff8a168183015290517f7d9402ed31d50a8f66cea8ff52b6c876c754375d6f7582b8d13b19896b718fb59181900360600190a1604080517fb199c8980000000000000000000000000000000000000000000000000000000081528b8303600790810b900b600482015263ffffffff8a166024820152905173__SyscoinMessageLibrary_________________9163b199c898916044808301926020929190829003018186803b1580156120b957600080fd5b505af41580156120cd573d6000803e3d6000fd5b505050506040513d60208110156120e357600080fd5b810190808051906020019092919050505091507f7d9402ed31d50a8f66cea8ff52b6c876c754375d6f7582b8d13b19896b718fb58e600b016000866000191660001916815260200190815260200160002060010160009054906101000a900467ffffffffffffffff168b84604051808467ffffffffffffffff1681526020018381526020018263ffffffff1663ffffffff168152602001935050505060405180910390a16000848152600b8f01602052604090206001015467ffffffffffffffff16995090965086905b63ffffffff838116908316146121c75761c3d29550612347565b604080517f304ef8ac00000000000000000000000000000000000000000000000000000000815263ffffffff85166004820152905173__SyscoinMessageLibrary_________________9163304ef8ac916024808301926020929190829003018186803b15801561223757600080fd5b505af415801561224b573d6000803e3d6000fd5b505050506040513d602081101561226157600080fd5b5051604080519d909101808e5263ffffffff851660208f01528151909d7f30b21f63b8aa3d605dc52326f71cb4c71e865f206e6144053487336d1b05eb4192908290030190a1506000838152600b8e01602052604090206001908101549396509390930192859267ffffffffffffffff16611d75565b604080518d8152602081018d905281517f617b69e69782999494b87f331d8718910de5f772605da3a749f6fd5315c3662c929181900390910190a1600260035460ff16600281111561232557fe5b1415801561233357508a8c14155b156123425761c3c89550612347565b600095505b505050505098975050505050505050565b6000828260030181518110151561236b57fe5b90602001015160f860020a900460f860020a0260f860020a9004630100000002838360020181518110151561239c57fe5b90602001015160f860020a900460f860020a0260f860020a9004620100000284846001018151811015156123cc57fe5b90602001015160f860020a900460f860020a0260f860020a90046101000285858151811015156123f857fe5b016020015160f860020a908190048102040101019392505050565b828054828255906000526020600020908101928215612450579160200282015b828111156124505782518255602090920191600190910190612433565b5061245c929150612481565b5090565b508054600082559060005260206000209081019061247e9190612481565b50565b61249b91905b8082111561245c5760008155600101612487565b90560080235326defb5d335564dd77860b0a010e19446427d3d78d155cabd064ca9c2aa165627a7a72305820eda92c4f4de797cc3a050be7fb3b49246aa76fde75a2cd1d21cc71cd34219d280029";

    public static final String FUNC_SUPERBLOCKTIMEOUT = "superblockTimeout";

    public static final String FUNC_MINPROPOSALDEPOSIT = "minProposalDeposit";

    public static final String FUNC_RESPONDBLOCKHEADERCOST = "respondBlockHeaderCost";

    public static final String FUNC_SUPERBLOCKCOST = "superblockCost";

    public static final String FUNC_MINCHALLENGEDEPOSIT = "minChallengeDeposit";

    public static final String FUNC_SESSIONS = "sessions";

    public static final String FUNC_RESPONDMERKLEROOTHASHESCOST = "respondMerkleRootHashesCost";

    public static final String FUNC_MINREWARD = "minReward";

    public static final String FUNC_VERIFYSUPERBLOCKCOST = "verifySuperblockCost";

    public static final String FUNC_CHALLENGECOST = "challengeCost";

    public static final String FUNC_SUPERBLOCKDURATION = "superblockDuration";

    public static final String FUNC_SETSYSCOINCLAIMMANAGER = "setSyscoinClaimManager";

    public static final String FUNC_BEGINBATTLESESSION = "beginBattleSession";

    public static final String FUNC_QUERYMERKLEROOTHASHES = "queryMerkleRootHashes";

    public static final String FUNC_RESPONDMERKLEROOTHASHES = "respondMerkleRootHashes";

    public static final String FUNC_QUERYBLOCKHEADER = "queryBlockHeader";

    public static final String FUNC_RESPONDBLOCKHEADER = "respondBlockHeader";

    public static final String FUNC_VERIFYSUPERBLOCK = "verifySuperblock";

    public static final String FUNC_TIMEOUT = "timeout";

    public static final String FUNC_GETCHALLENGERHITTIMEOUT = "getChallengerHitTimeout";

    public static final String FUNC_GETSUBMITTERHITTIMEOUT = "getSubmitterHitTimeout";

    public static final String FUNC_GETSYSCOINBLOCKHASHES = "getSyscoinBlockHashes";

    public static final Event NEWBATTLE_EVENT = new Event("NewBattle", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}, new TypeReference<Address>() {}));
    ;

    public static final Event CHALLENGERCONVICTED_EVENT = new Event("ChallengerConvicted", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}));
    ;

    public static final Event SUBMITTERCONVICTED_EVENT = new Event("SubmitterConvicted", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}));
    ;

    public static final Event QUERYMERKLEROOTHASHES_EVENT = new Event("QueryMerkleRootHashes", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}));
    ;

    public static final Event RESPONDMERKLEROOTHASHES_EVENT = new Event("RespondMerkleRootHashes", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}, new TypeReference<DynamicArray<Bytes32>>() {}));
    ;

    public static final Event QUERYBLOCKHEADER_EVENT = new Event("QueryBlockHeader", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}, new TypeReference<Bytes32>() {}));
    ;

    public static final Event RESPONDBLOCKHEADER_EVENT = new Event("RespondBlockHeader", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}, new TypeReference<Bytes32>() {}));
    ;

    public static final Event DIFFICULTY_EVENT = new Event("Difficulty", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint32>() {}, new TypeReference<Uint32>() {}));
    ;

    public static final Event DIFFICULTYEND_EVENT = new Event("DifficultyEnd", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event DIFFICULTYSTEP_EVENT = new Event("DifficultyStep", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint32>() {}));
    ;

    public static final Event DIFFICULTYADJUSTMENT_EVENT = new Event("DifficultyAdjustment", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint32>() {}));
    ;

    public static final Event ERRORBATTLE_EVENT = new Event("ErrorBattle", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Uint256>() {}));
    ;

    protected static final HashMap<String, String> _addresses;

    static {
        _addresses = new HashMap<String, String>();
        _addresses.put("4", "0x3cddc160c2e9492f1d6b6d9ee88ea7553586ff0f");
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

    public RemoteCall<Uint256> superblockTimeout() {
        final Function function = new Function(FUNC_SUPERBLOCKTIMEOUT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public RemoteCall<Uint256> minProposalDeposit() {
        final Function function = new Function(FUNC_MINPROPOSALDEPOSIT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public RemoteCall<Uint256> respondBlockHeaderCost() {
        final Function function = new Function(FUNC_RESPONDBLOCKHEADERCOST, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public RemoteCall<Uint256> superblockCost() {
        final Function function = new Function(FUNC_SUPERBLOCKCOST, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public RemoteCall<Uint256> minChallengeDeposit() {
        final Function function = new Function(FUNC_MINCHALLENGEDEPOSIT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public RemoteCall<Tuple11<Bytes32, Bytes32, Address, Address, Uint256, Uint256, Uint256, Uint256, Uint256, Uint256, Uint8>> sessions(Bytes32 param0) {
        final Function function = new Function(FUNC_SESSIONS, 
                Arrays.<Type>asList(param0), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}, new TypeReference<Address>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint8>() {}));
        return new RemoteCall<Tuple11<Bytes32, Bytes32, Address, Address, Uint256, Uint256, Uint256, Uint256, Uint256, Uint256, Uint8>>(
                new Callable<Tuple11<Bytes32, Bytes32, Address, Address, Uint256, Uint256, Uint256, Uint256, Uint256, Uint256, Uint8>>() {
                    @Override
                    public Tuple11<Bytes32, Bytes32, Address, Address, Uint256, Uint256, Uint256, Uint256, Uint256, Uint256, Uint8> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple11<Bytes32, Bytes32, Address, Address, Uint256, Uint256, Uint256, Uint256, Uint256, Uint256, Uint8>(
                                (Bytes32) results.get(0), 
                                (Bytes32) results.get(1), 
                                (Address) results.get(2), 
                                (Address) results.get(3), 
                                (Uint256) results.get(4), 
                                (Uint256) results.get(5), 
                                (Uint256) results.get(6), 
                                (Uint256) results.get(7), 
                                (Uint256) results.get(8), 
                                (Uint256) results.get(9), 
                                (Uint8) results.get(10));
                    }
                });
    }

    public RemoteCall<Uint256> respondMerkleRootHashesCost() {
        final Function function = new Function(FUNC_RESPONDMERKLEROOTHASHESCOST, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public RemoteCall<Uint256> minReward() {
        final Function function = new Function(FUNC_MINREWARD, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public RemoteCall<Uint256> verifySuperblockCost() {
        final Function function = new Function(FUNC_VERIFYSUPERBLOCKCOST, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public RemoteCall<Uint256> challengeCost() {
        final Function function = new Function(FUNC_CHALLENGECOST, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public RemoteCall<Uint256> superblockDuration() {
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
            typedResponse.challenger = (Address) eventValues.getNonIndexedValues().get(2);
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
                typedResponse.challenger = (Address) eventValues.getNonIndexedValues().get(2);
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
            typedResponse.submitter = (Address) eventValues.getNonIndexedValues().get(2);
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
                typedResponse.submitter = (Address) eventValues.getNonIndexedValues().get(2);
                return typedResponse;
            }
        });
    }

    public Flowable<SubmitterConvictedEventResponse> submitterConvictedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(SUBMITTERCONVICTED_EVENT));
        return submitterConvictedEventFlowable(filter);
    }

    public List<QueryMerkleRootHashesEventResponse> getQueryMerkleRootHashesEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(QUERYMERKLEROOTHASHES_EVENT, transactionReceipt);
        ArrayList<QueryMerkleRootHashesEventResponse> responses = new ArrayList<QueryMerkleRootHashesEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            QueryMerkleRootHashesEventResponse typedResponse = new QueryMerkleRootHashesEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.superblockHash = (Bytes32) eventValues.getNonIndexedValues().get(0);
            typedResponse.sessionId = (Bytes32) eventValues.getNonIndexedValues().get(1);
            typedResponse.submitter = (Address) eventValues.getNonIndexedValues().get(2);
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<QueryMerkleRootHashesEventResponse> queryMerkleRootHashesEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, QueryMerkleRootHashesEventResponse>() {
            @Override
            public QueryMerkleRootHashesEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(QUERYMERKLEROOTHASHES_EVENT, log);
                QueryMerkleRootHashesEventResponse typedResponse = new QueryMerkleRootHashesEventResponse();
                typedResponse.log = log;
                typedResponse.superblockHash = (Bytes32) eventValues.getNonIndexedValues().get(0);
                typedResponse.sessionId = (Bytes32) eventValues.getNonIndexedValues().get(1);
                typedResponse.submitter = (Address) eventValues.getNonIndexedValues().get(2);
                return typedResponse;
            }
        });
    }

    public Flowable<QueryMerkleRootHashesEventResponse> queryMerkleRootHashesEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(QUERYMERKLEROOTHASHES_EVENT));
        return queryMerkleRootHashesEventFlowable(filter);
    }

    public List<RespondMerkleRootHashesEventResponse> getRespondMerkleRootHashesEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(RESPONDMERKLEROOTHASHES_EVENT, transactionReceipt);
        ArrayList<RespondMerkleRootHashesEventResponse> responses = new ArrayList<RespondMerkleRootHashesEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            RespondMerkleRootHashesEventResponse typedResponse = new RespondMerkleRootHashesEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.superblockHash = (Bytes32) eventValues.getNonIndexedValues().get(0);
            typedResponse.sessionId = (Bytes32) eventValues.getNonIndexedValues().get(1);
            typedResponse.challenger = (Address) eventValues.getNonIndexedValues().get(2);
            typedResponse.blockHashes = (DynamicArray<Bytes32>) eventValues.getNonIndexedValues().get(3);
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<RespondMerkleRootHashesEventResponse> respondMerkleRootHashesEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, RespondMerkleRootHashesEventResponse>() {
            @Override
            public RespondMerkleRootHashesEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(RESPONDMERKLEROOTHASHES_EVENT, log);
                RespondMerkleRootHashesEventResponse typedResponse = new RespondMerkleRootHashesEventResponse();
                typedResponse.log = log;
                typedResponse.superblockHash = (Bytes32) eventValues.getNonIndexedValues().get(0);
                typedResponse.sessionId = (Bytes32) eventValues.getNonIndexedValues().get(1);
                typedResponse.challenger = (Address) eventValues.getNonIndexedValues().get(2);
                typedResponse.blockHashes = (DynamicArray<Bytes32>) eventValues.getNonIndexedValues().get(3);
                return typedResponse;
            }
        });
    }

    public Flowable<RespondMerkleRootHashesEventResponse> respondMerkleRootHashesEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(RESPONDMERKLEROOTHASHES_EVENT));
        return respondMerkleRootHashesEventFlowable(filter);
    }

    public List<QueryBlockHeaderEventResponse> getQueryBlockHeaderEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(QUERYBLOCKHEADER_EVENT, transactionReceipt);
        ArrayList<QueryBlockHeaderEventResponse> responses = new ArrayList<QueryBlockHeaderEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            QueryBlockHeaderEventResponse typedResponse = new QueryBlockHeaderEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.superblockHash = (Bytes32) eventValues.getNonIndexedValues().get(0);
            typedResponse.sessionId = (Bytes32) eventValues.getNonIndexedValues().get(1);
            typedResponse.submitter = (Address) eventValues.getNonIndexedValues().get(2);
            typedResponse.blockSha256Hash = (Bytes32) eventValues.getNonIndexedValues().get(3);
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<QueryBlockHeaderEventResponse> queryBlockHeaderEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, QueryBlockHeaderEventResponse>() {
            @Override
            public QueryBlockHeaderEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(QUERYBLOCKHEADER_EVENT, log);
                QueryBlockHeaderEventResponse typedResponse = new QueryBlockHeaderEventResponse();
                typedResponse.log = log;
                typedResponse.superblockHash = (Bytes32) eventValues.getNonIndexedValues().get(0);
                typedResponse.sessionId = (Bytes32) eventValues.getNonIndexedValues().get(1);
                typedResponse.submitter = (Address) eventValues.getNonIndexedValues().get(2);
                typedResponse.blockSha256Hash = (Bytes32) eventValues.getNonIndexedValues().get(3);
                return typedResponse;
            }
        });
    }

    public Flowable<QueryBlockHeaderEventResponse> queryBlockHeaderEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(QUERYBLOCKHEADER_EVENT));
        return queryBlockHeaderEventFlowable(filter);
    }

    public List<RespondBlockHeaderEventResponse> getRespondBlockHeaderEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(RESPONDBLOCKHEADER_EVENT, transactionReceipt);
        ArrayList<RespondBlockHeaderEventResponse> responses = new ArrayList<RespondBlockHeaderEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            RespondBlockHeaderEventResponse typedResponse = new RespondBlockHeaderEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.superblockHash = (Bytes32) eventValues.getNonIndexedValues().get(0);
            typedResponse.sessionId = (Bytes32) eventValues.getNonIndexedValues().get(1);
            typedResponse.challenger = (Address) eventValues.getNonIndexedValues().get(2);
            typedResponse.blockSha256Hash = (Bytes32) eventValues.getNonIndexedValues().get(3);
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<RespondBlockHeaderEventResponse> respondBlockHeaderEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, RespondBlockHeaderEventResponse>() {
            @Override
            public RespondBlockHeaderEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(RESPONDBLOCKHEADER_EVENT, log);
                RespondBlockHeaderEventResponse typedResponse = new RespondBlockHeaderEventResponse();
                typedResponse.log = log;
                typedResponse.superblockHash = (Bytes32) eventValues.getNonIndexedValues().get(0);
                typedResponse.sessionId = (Bytes32) eventValues.getNonIndexedValues().get(1);
                typedResponse.challenger = (Address) eventValues.getNonIndexedValues().get(2);
                typedResponse.blockSha256Hash = (Bytes32) eventValues.getNonIndexedValues().get(3);
                return typedResponse;
            }
        });
    }

    public Flowable<RespondBlockHeaderEventResponse> respondBlockHeaderEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(RESPONDBLOCKHEADER_EVENT));
        return respondBlockHeaderEventFlowable(filter);
    }

    public List<DifficultyEventResponse> getDifficultyEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(DIFFICULTY_EVENT, transactionReceipt);
        ArrayList<DifficultyEventResponse> responses = new ArrayList<DifficultyEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            DifficultyEventResponse typedResponse = new DifficultyEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.hashes = (Uint256) eventValues.getNonIndexedValues().get(0);
            typedResponse.timestamp = (Uint256) eventValues.getNonIndexedValues().get(1);
            typedResponse.work = (Uint256) eventValues.getNonIndexedValues().get(2);
            typedResponse.accWork = (Uint256) eventValues.getNonIndexedValues().get(3);
            typedResponse.newBits = (Uint32) eventValues.getNonIndexedValues().get(4);
            typedResponse.prevBits = (Uint32) eventValues.getNonIndexedValues().get(5);
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<DifficultyEventResponse> difficultyEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, DifficultyEventResponse>() {
            @Override
            public DifficultyEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(DIFFICULTY_EVENT, log);
                DifficultyEventResponse typedResponse = new DifficultyEventResponse();
                typedResponse.log = log;
                typedResponse.hashes = (Uint256) eventValues.getNonIndexedValues().get(0);
                typedResponse.timestamp = (Uint256) eventValues.getNonIndexedValues().get(1);
                typedResponse.work = (Uint256) eventValues.getNonIndexedValues().get(2);
                typedResponse.accWork = (Uint256) eventValues.getNonIndexedValues().get(3);
                typedResponse.newBits = (Uint32) eventValues.getNonIndexedValues().get(4);
                typedResponse.prevBits = (Uint32) eventValues.getNonIndexedValues().get(5);
                return typedResponse;
            }
        });
    }

    public Flowable<DifficultyEventResponse> difficultyEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(DIFFICULTY_EVENT));
        return difficultyEventFlowable(filter);
    }

    public List<DifficultyEndEventResponse> getDifficultyEndEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(DIFFICULTYEND_EVENT, transactionReceipt);
        ArrayList<DifficultyEndEventResponse> responses = new ArrayList<DifficultyEndEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            DifficultyEndEventResponse typedResponse = new DifficultyEndEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.work = (Uint256) eventValues.getNonIndexedValues().get(0);
            typedResponse.accWork = (Uint256) eventValues.getNonIndexedValues().get(1);
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<DifficultyEndEventResponse> difficultyEndEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, DifficultyEndEventResponse>() {
            @Override
            public DifficultyEndEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(DIFFICULTYEND_EVENT, log);
                DifficultyEndEventResponse typedResponse = new DifficultyEndEventResponse();
                typedResponse.log = log;
                typedResponse.work = (Uint256) eventValues.getNonIndexedValues().get(0);
                typedResponse.accWork = (Uint256) eventValues.getNonIndexedValues().get(1);
                return typedResponse;
            }
        });
    }

    public Flowable<DifficultyEndEventResponse> difficultyEndEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(DIFFICULTYEND_EVENT));
        return difficultyEndEventFlowable(filter);
    }

    public List<DifficultyStepEventResponse> getDifficultyStepEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(DIFFICULTYSTEP_EVENT, transactionReceipt);
        ArrayList<DifficultyStepEventResponse> responses = new ArrayList<DifficultyStepEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            DifficultyStepEventResponse typedResponse = new DifficultyStepEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.work = (Uint256) eventValues.getNonIndexedValues().get(0);
            typedResponse.bits = (Uint32) eventValues.getNonIndexedValues().get(1);
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<DifficultyStepEventResponse> difficultyStepEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, DifficultyStepEventResponse>() {
            @Override
            public DifficultyStepEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(DIFFICULTYSTEP_EVENT, log);
                DifficultyStepEventResponse typedResponse = new DifficultyStepEventResponse();
                typedResponse.log = log;
                typedResponse.work = (Uint256) eventValues.getNonIndexedValues().get(0);
                typedResponse.bits = (Uint32) eventValues.getNonIndexedValues().get(1);
                return typedResponse;
            }
        });
    }

    public Flowable<DifficultyStepEventResponse> difficultyStepEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(DIFFICULTYSTEP_EVENT));
        return difficultyStepEventFlowable(filter);
    }

    public List<DifficultyAdjustmentEventResponse> getDifficultyAdjustmentEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(DIFFICULTYADJUSTMENT_EVENT, transactionReceipt);
        ArrayList<DifficultyAdjustmentEventResponse> responses = new ArrayList<DifficultyAdjustmentEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            DifficultyAdjustmentEventResponse typedResponse = new DifficultyAdjustmentEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.parentTimestamp = (Uint256) eventValues.getNonIndexedValues().get(0);
            typedResponse.prevTimestamp = (Uint256) eventValues.getNonIndexedValues().get(1);
            typedResponse.bits = (Uint32) eventValues.getNonIndexedValues().get(2);
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<DifficultyAdjustmentEventResponse> difficultyAdjustmentEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, DifficultyAdjustmentEventResponse>() {
            @Override
            public DifficultyAdjustmentEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(DIFFICULTYADJUSTMENT_EVENT, log);
                DifficultyAdjustmentEventResponse typedResponse = new DifficultyAdjustmentEventResponse();
                typedResponse.log = log;
                typedResponse.parentTimestamp = (Uint256) eventValues.getNonIndexedValues().get(0);
                typedResponse.prevTimestamp = (Uint256) eventValues.getNonIndexedValues().get(1);
                typedResponse.bits = (Uint32) eventValues.getNonIndexedValues().get(2);
                return typedResponse;
            }
        });
    }

    public Flowable<DifficultyAdjustmentEventResponse> difficultyAdjustmentEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(DIFFICULTYADJUSTMENT_EVENT));
        return difficultyAdjustmentEventFlowable(filter);
    }

    public List<ErrorBattleEventResponse> getErrorBattleEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(ERRORBATTLE_EVENT, transactionReceipt);
        ArrayList<ErrorBattleEventResponse> responses = new ArrayList<ErrorBattleEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ErrorBattleEventResponse typedResponse = new ErrorBattleEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.sessionId = (Bytes32) eventValues.getNonIndexedValues().get(0);
            typedResponse.err = (Uint256) eventValues.getNonIndexedValues().get(1);
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<ErrorBattleEventResponse> errorBattleEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, ErrorBattleEventResponse>() {
            @Override
            public ErrorBattleEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(ERRORBATTLE_EVENT, log);
                ErrorBattleEventResponse typedResponse = new ErrorBattleEventResponse();
                typedResponse.log = log;
                typedResponse.sessionId = (Bytes32) eventValues.getNonIndexedValues().get(0);
                typedResponse.err = (Uint256) eventValues.getNonIndexedValues().get(1);
                return typedResponse;
            }
        });
    }

    public Flowable<ErrorBattleEventResponse> errorBattleEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(ERRORBATTLE_EVENT));
        return errorBattleEventFlowable(filter);
    }

    public RemoteCall<TransactionReceipt> setSyscoinClaimManager(Address _syscoinClaimManager) {
        final Function function = new Function(
                FUNC_SETSYSCOINCLAIMMANAGER, 
                Arrays.<Type>asList(_syscoinClaimManager), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> beginBattleSession(Bytes32 superblockHash, Address submitter, Address challenger) {
        final Function function = new Function(
                FUNC_BEGINBATTLESESSION, 
                Arrays.<Type>asList(superblockHash, submitter, challenger), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> queryMerkleRootHashes(Bytes32 superblockHash, Bytes32 sessionId) {
        final Function function = new Function(
                FUNC_QUERYMERKLEROOTHASHES, 
                Arrays.<Type>asList(superblockHash, sessionId), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> respondMerkleRootHashes(Bytes32 superblockHash, Bytes32 sessionId, DynamicArray<Bytes32> blockHashes) {
        final Function function = new Function(
                FUNC_RESPONDMERKLEROOTHASHES, 
                Arrays.<Type>asList(superblockHash, sessionId, blockHashes), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> queryBlockHeader(Bytes32 superblockHash, Bytes32 sessionId, Bytes32 blockHash) {
        final Function function = new Function(
                FUNC_QUERYBLOCKHEADER, 
                Arrays.<Type>asList(superblockHash, sessionId, blockHash), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> respondBlockHeader(Bytes32 superblockHash, Bytes32 sessionId, DynamicBytes blockHeader) {
        final Function function = new Function(
                FUNC_RESPONDBLOCKHEADER, 
                Arrays.<Type>asList(superblockHash, sessionId, blockHeader), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> verifySuperblock(Bytes32 sessionId) {
        final Function function = new Function(
                FUNC_VERIFYSUPERBLOCK, 
                Arrays.<Type>asList(sessionId), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> timeout(Bytes32 sessionId) {
        final Function function = new Function(
                FUNC_TIMEOUT, 
                Arrays.<Type>asList(sessionId), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Bool> getChallengerHitTimeout(Bytes32 sessionId) {
        final Function function = new Function(FUNC_GETCHALLENGERHITTIMEOUT, 
                Arrays.<Type>asList(sessionId), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public RemoteCall<Bool> getSubmitterHitTimeout(Bytes32 sessionId) {
        final Function function = new Function(FUNC_GETSUBMITTERHITTIMEOUT, 
                Arrays.<Type>asList(sessionId), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public RemoteCall<DynamicArray<Bytes32>> getSyscoinBlockHashes(Bytes32 sessionId) {
        final Function function = new Function(FUNC_GETSYSCOINBLOCKHASHES, 
                Arrays.<Type>asList(sessionId), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Bytes32>>() {}));
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

    public static RemoteCall<SyscoinBattleManager> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider, Uint8 _network, Address _superblocks, Uint256 _superblockDuration, Uint256 _superblockTimeout) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(_network, _superblocks, _superblockDuration, _superblockTimeout));
        return deployRemoteCall(SyscoinBattleManager.class, web3j, credentials, contractGasProvider, BINARY, encodedConstructor);
    }

    public static RemoteCall<SyscoinBattleManager> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider, Uint8 _network, Address _superblocks, Uint256 _superblockDuration, Uint256 _superblockTimeout) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(_network, _superblocks, _superblockDuration, _superblockTimeout));
        return deployRemoteCall(SyscoinBattleManager.class, web3j, transactionManager, contractGasProvider, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<SyscoinBattleManager> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, Uint8 _network, Address _superblocks, Uint256 _superblockDuration, Uint256 _superblockTimeout) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(_network, _superblocks, _superblockDuration, _superblockTimeout));
        return deployRemoteCall(SyscoinBattleManager.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<SyscoinBattleManager> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, Uint8 _network, Address _superblocks, Uint256 _superblockDuration, Uint256 _superblockTimeout) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(_network, _superblocks, _superblockDuration, _superblockTimeout));
        return deployRemoteCall(SyscoinBattleManager.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    protected String getStaticDeployedAddress(String networkId) {
        return _addresses.get(networkId);
    }

    public static String getPreviouslyDeployedAddress(String networkId) {
        return _addresses.get(networkId);
    }

    public static class NewBattleEventResponse {
        public Log log;

        public Bytes32 superblockHash;

        public Bytes32 sessionId;

        public Address submitter;

        public Address challenger;
    }

    public static class ChallengerConvictedEventResponse {
        public Log log;

        public Bytes32 superblockHash;

        public Bytes32 sessionId;

        public Address challenger;
    }

    public static class SubmitterConvictedEventResponse {
        public Log log;

        public Bytes32 superblockHash;

        public Bytes32 sessionId;

        public Address submitter;
    }

    public static class QueryMerkleRootHashesEventResponse {
        public Log log;

        public Bytes32 superblockHash;

        public Bytes32 sessionId;

        public Address submitter;
    }

    public static class RespondMerkleRootHashesEventResponse {
        public Log log;

        public Bytes32 superblockHash;

        public Bytes32 sessionId;

        public Address challenger;

        public DynamicArray<Bytes32> blockHashes;
    }

    public static class QueryBlockHeaderEventResponse {
        public Log log;

        public Bytes32 superblockHash;

        public Bytes32 sessionId;

        public Address submitter;

        public Bytes32 blockSha256Hash;
    }

    public static class RespondBlockHeaderEventResponse {
        public Log log;

        public Bytes32 superblockHash;

        public Bytes32 sessionId;

        public Address challenger;

        public Bytes32 blockSha256Hash;
    }

    public static class DifficultyEventResponse {
        public Log log;

        public Uint256 hashes;

        public Uint256 timestamp;

        public Uint256 work;

        public Uint256 accWork;

        public Uint32 newBits;

        public Uint32 prevBits;
    }

    public static class DifficultyEndEventResponse {
        public Log log;

        public Uint256 work;

        public Uint256 accWork;
    }

    public static class DifficultyStepEventResponse {
        public Log log;

        public Uint256 work;

        public Uint32 bits;
    }

    public static class DifficultyAdjustmentEventResponse {
        public Log log;

        public Uint256 parentTimestamp;

        public Uint256 prevTimestamp;

        public Uint32 bits;
    }

    public static class ErrorBattleEventResponse {
        public Log log;

        public Bytes32 sessionId;

        public Uint256 err;
    }
}
