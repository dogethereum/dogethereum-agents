package org.dogethereum.agents.contract;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple8;
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
 * <p>Generated with web3j version 4.8.2.
 */
@SuppressWarnings("rawtypes")
public class SuperblockClaims extends Contract {
    public static final String BINARY = "Bin file was not provided";

    public static final String FUNC_BATTLEREWARD = "battleReward";

    public static final String FUNC_BONDDEPOSIT = "bondDeposit";

    public static final String FUNC_CHALLENGECOST = "challengeCost";

    public static final String FUNC_CHALLENGESUPERBLOCK = "challengeSuperblock";

    public static final String FUNC_CHECKCLAIMFINISHED = "checkClaimFinished";

    public static final String FUNC_CLAIMS = "claims";

    public static final String FUNC_CONFIRMCLAIM = "confirmClaim";

    public static final String FUNC_DEPOSITS = "deposits";

    public static final String FUNC_GETBONDEDDEPOSIT = "getBondedDeposit";

    public static final String FUNC_GETCLAIMCHALLENGETIMEOUT = "getClaimChallengeTimeout";

    public static final String FUNC_GETCLAIMCHALLENGERS = "getClaimChallengers";

    public static final String FUNC_GETCLAIMDECIDED = "getClaimDecided";

    public static final String FUNC_GETCLAIMEXISTS = "getClaimExists";

    public static final String FUNC_GETCLAIMINVALID = "getClaimInvalid";

    public static final String FUNC_GETCLAIMREMAININGCHALLENGERS = "getClaimRemainingChallengers";

    public static final String FUNC_GETCLAIMSUBMITTER = "getClaimSubmitter";

    public static final String FUNC_GETCLAIMVERIFICATIONONGOING = "getClaimVerificationOngoing";

    public static final String FUNC_GETDEPOSIT = "getDeposit";

    public static final String FUNC_GETINBATTLEANDSEMIAPPROVABLE = "getInBattleAndSemiApprovable";

    public static final String FUNC_GETNEWSUPERBLOCKEVENTTIMESTAMP = "getNewSuperblockEventTimestamp";

    public static final String FUNC_GETSESSION = "getSession";

    public static final String FUNC_INITIALIZE = "initialize";

    public static final String FUNC_MAKEDEPOSIT = "makeDeposit";

    public static final String FUNC_MINCHALLENGEDEPOSIT = "minChallengeDeposit";

    public static final String FUNC_MINPROPOSALDEPOSIT = "minProposalDeposit";

    public static final String FUNC_MINREWARD = "minReward";

    public static final String FUNC_PROPOSESUPERBLOCK = "proposeSuperblock";

    public static final String FUNC_QUERYBLOCKHEADERCOST = "queryBlockHeaderCost";

    public static final String FUNC_QUERYMERKLEROOTHASHESCOST = "queryMerkleRootHashesCost";

    public static final String FUNC_REJECTCLAIM = "rejectClaim";

    public static final String FUNC_REQUESTSCRYPTCOST = "requestScryptCost";

    public static final String FUNC_RESPONDBLOCKHEADERCOST = "respondBlockHeaderCost";

    public static final String FUNC_RESPONDMERKLEROOTHASHESCOST = "respondMerkleRootHashesCost";

    public static final String FUNC_SESSIONDECIDED = "sessionDecided";

    public static final String FUNC_SUPERBLOCKCONFIRMATIONS = "superblockConfirmations";

    public static final String FUNC_SUPERBLOCKCOST = "superblockCost";

    public static final String FUNC_SUPERBLOCKDELAY = "superblockDelay";

    public static final String FUNC_SUPERBLOCKTIMEOUT = "superblockTimeout";

    public static final String FUNC_TRUSTEDDOGEBATTLEMANAGER = "trustedDogeBattleManager";

    public static final String FUNC_TRUSTEDSUPERBLOCKS = "trustedSuperblocks";

    public static final String FUNC_VERIFYSUPERBLOCKCOST = "verifySuperblockCost";

    public static final String FUNC_WITHDRAWDEPOSIT = "withdrawDeposit";

    public static final Event DEPOSITBONDED_EVENT = new Event("DepositBonded", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event DEPOSITMADE_EVENT = new Event("DepositMade", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event DEPOSITUNBONDED_EVENT = new Event("DepositUnbonded", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event DEPOSITWITHDRAWN_EVENT = new Event("DepositWithdrawn", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event SUPERBLOCKBATTLEDECIDED_EVENT = new Event("SuperblockBattleDecided", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}, new TypeReference<Address>() {}));
    ;

    public static final Event SUPERBLOCKCLAIMCHALLENGED_EVENT = new Event("SuperblockClaimChallenged", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}));
    ;

    public static final Event SUPERBLOCKCLAIMCREATED_EVENT = new Event("SuperblockClaimCreated", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}));
    ;

    public static final Event SUPERBLOCKCLAIMFAILED_EVENT = new Event("SuperblockClaimFailed", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}));
    ;

    public static final Event SUPERBLOCKCLAIMPENDING_EVENT = new Event("SuperblockClaimPending", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}));
    ;

    public static final Event SUPERBLOCKCLAIMSUCCESSFUL_EVENT = new Event("SuperblockClaimSuccessful", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}));
    ;

    public static final Event VERIFICATIONGAMESTARTED_EVENT = new Event("VerificationGameStarted", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}, new TypeReference<Address>() {}, new TypeReference<Bytes32>() {}));
    ;

    @Deprecated
    protected SuperblockClaims(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected SuperblockClaims(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected SuperblockClaims(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected SuperblockClaims(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public List<DepositBondedEventResponse> getDepositBondedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(DEPOSITBONDED_EVENT, transactionReceipt);
        ArrayList<DepositBondedEventResponse> responses = new ArrayList<DepositBondedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            DepositBondedEventResponse typedResponse = new DepositBondedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.superblockHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.account = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<DepositBondedEventResponse> depositBondedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, DepositBondedEventResponse>() {
            @Override
            public DepositBondedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(DEPOSITBONDED_EVENT, log);
                DepositBondedEventResponse typedResponse = new DepositBondedEventResponse();
                typedResponse.log = log;
                typedResponse.superblockHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.account = (String) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<DepositBondedEventResponse> depositBondedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(DEPOSITBONDED_EVENT));
        return depositBondedEventFlowable(filter);
    }

    public List<DepositMadeEventResponse> getDepositMadeEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(DEPOSITMADE_EVENT, transactionReceipt);
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

    public Flowable<DepositMadeEventResponse> depositMadeEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, DepositMadeEventResponse>() {
            @Override
            public DepositMadeEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(DEPOSITMADE_EVENT, log);
                DepositMadeEventResponse typedResponse = new DepositMadeEventResponse();
                typedResponse.log = log;
                typedResponse.who = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<DepositMadeEventResponse> depositMadeEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(DEPOSITMADE_EVENT));
        return depositMadeEventFlowable(filter);
    }

    public List<DepositUnbondedEventResponse> getDepositUnbondedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(DEPOSITUNBONDED_EVENT, transactionReceipt);
        ArrayList<DepositUnbondedEventResponse> responses = new ArrayList<DepositUnbondedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            DepositUnbondedEventResponse typedResponse = new DepositUnbondedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.superblockHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.account = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<DepositUnbondedEventResponse> depositUnbondedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, DepositUnbondedEventResponse>() {
            @Override
            public DepositUnbondedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(DEPOSITUNBONDED_EVENT, log);
                DepositUnbondedEventResponse typedResponse = new DepositUnbondedEventResponse();
                typedResponse.log = log;
                typedResponse.superblockHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.account = (String) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<DepositUnbondedEventResponse> depositUnbondedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(DEPOSITUNBONDED_EVENT));
        return depositUnbondedEventFlowable(filter);
    }

    public List<DepositWithdrawnEventResponse> getDepositWithdrawnEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(DEPOSITWITHDRAWN_EVENT, transactionReceipt);
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

    public Flowable<DepositWithdrawnEventResponse> depositWithdrawnEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, DepositWithdrawnEventResponse>() {
            @Override
            public DepositWithdrawnEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(DEPOSITWITHDRAWN_EVENT, log);
                DepositWithdrawnEventResponse typedResponse = new DepositWithdrawnEventResponse();
                typedResponse.log = log;
                typedResponse.who = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<DepositWithdrawnEventResponse> depositWithdrawnEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(DEPOSITWITHDRAWN_EVENT));
        return depositWithdrawnEventFlowable(filter);
    }

    public List<SuperblockBattleDecidedEventResponse> getSuperblockBattleDecidedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(SUPERBLOCKBATTLEDECIDED_EVENT, transactionReceipt);
        ArrayList<SuperblockBattleDecidedEventResponse> responses = new ArrayList<SuperblockBattleDecidedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            SuperblockBattleDecidedEventResponse typedResponse = new SuperblockBattleDecidedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.sessionId = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.winner = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.loser = (String) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<SuperblockBattleDecidedEventResponse> superblockBattleDecidedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, SuperblockBattleDecidedEventResponse>() {
            @Override
            public SuperblockBattleDecidedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(SUPERBLOCKBATTLEDECIDED_EVENT, log);
                SuperblockBattleDecidedEventResponse typedResponse = new SuperblockBattleDecidedEventResponse();
                typedResponse.log = log;
                typedResponse.sessionId = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.winner = (String) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.loser = (String) eventValues.getNonIndexedValues().get(2).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<SuperblockBattleDecidedEventResponse> superblockBattleDecidedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(SUPERBLOCKBATTLEDECIDED_EVENT));
        return superblockBattleDecidedEventFlowable(filter);
    }

    public List<SuperblockClaimChallengedEventResponse> getSuperblockClaimChallengedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(SUPERBLOCKCLAIMCHALLENGED_EVENT, transactionReceipt);
        ArrayList<SuperblockClaimChallengedEventResponse> responses = new ArrayList<SuperblockClaimChallengedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            SuperblockClaimChallengedEventResponse typedResponse = new SuperblockClaimChallengedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.superblockHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.challenger = (String) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<SuperblockClaimChallengedEventResponse> superblockClaimChallengedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, SuperblockClaimChallengedEventResponse>() {
            @Override
            public SuperblockClaimChallengedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(SUPERBLOCKCLAIMCHALLENGED_EVENT, log);
                SuperblockClaimChallengedEventResponse typedResponse = new SuperblockClaimChallengedEventResponse();
                typedResponse.log = log;
                typedResponse.superblockHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.challenger = (String) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<SuperblockClaimChallengedEventResponse> superblockClaimChallengedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(SUPERBLOCKCLAIMCHALLENGED_EVENT));
        return superblockClaimChallengedEventFlowable(filter);
    }

    public List<SuperblockClaimCreatedEventResponse> getSuperblockClaimCreatedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(SUPERBLOCKCLAIMCREATED_EVENT, transactionReceipt);
        ArrayList<SuperblockClaimCreatedEventResponse> responses = new ArrayList<SuperblockClaimCreatedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            SuperblockClaimCreatedEventResponse typedResponse = new SuperblockClaimCreatedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.superblockHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.submitter = (String) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<SuperblockClaimCreatedEventResponse> superblockClaimCreatedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, SuperblockClaimCreatedEventResponse>() {
            @Override
            public SuperblockClaimCreatedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(SUPERBLOCKCLAIMCREATED_EVENT, log);
                SuperblockClaimCreatedEventResponse typedResponse = new SuperblockClaimCreatedEventResponse();
                typedResponse.log = log;
                typedResponse.superblockHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.submitter = (String) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<SuperblockClaimCreatedEventResponse> superblockClaimCreatedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(SUPERBLOCKCLAIMCREATED_EVENT));
        return superblockClaimCreatedEventFlowable(filter);
    }

    public List<SuperblockClaimFailedEventResponse> getSuperblockClaimFailedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(SUPERBLOCKCLAIMFAILED_EVENT, transactionReceipt);
        ArrayList<SuperblockClaimFailedEventResponse> responses = new ArrayList<SuperblockClaimFailedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            SuperblockClaimFailedEventResponse typedResponse = new SuperblockClaimFailedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.superblockHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.submitter = (String) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<SuperblockClaimFailedEventResponse> superblockClaimFailedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, SuperblockClaimFailedEventResponse>() {
            @Override
            public SuperblockClaimFailedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(SUPERBLOCKCLAIMFAILED_EVENT, log);
                SuperblockClaimFailedEventResponse typedResponse = new SuperblockClaimFailedEventResponse();
                typedResponse.log = log;
                typedResponse.superblockHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.submitter = (String) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<SuperblockClaimFailedEventResponse> superblockClaimFailedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(SUPERBLOCKCLAIMFAILED_EVENT));
        return superblockClaimFailedEventFlowable(filter);
    }

    public List<SuperblockClaimPendingEventResponse> getSuperblockClaimPendingEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(SUPERBLOCKCLAIMPENDING_EVENT, transactionReceipt);
        ArrayList<SuperblockClaimPendingEventResponse> responses = new ArrayList<SuperblockClaimPendingEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            SuperblockClaimPendingEventResponse typedResponse = new SuperblockClaimPendingEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.superblockHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.submitter = (String) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<SuperblockClaimPendingEventResponse> superblockClaimPendingEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, SuperblockClaimPendingEventResponse>() {
            @Override
            public SuperblockClaimPendingEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(SUPERBLOCKCLAIMPENDING_EVENT, log);
                SuperblockClaimPendingEventResponse typedResponse = new SuperblockClaimPendingEventResponse();
                typedResponse.log = log;
                typedResponse.superblockHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.submitter = (String) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<SuperblockClaimPendingEventResponse> superblockClaimPendingEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(SUPERBLOCKCLAIMPENDING_EVENT));
        return superblockClaimPendingEventFlowable(filter);
    }

    public List<SuperblockClaimSuccessfulEventResponse> getSuperblockClaimSuccessfulEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(SUPERBLOCKCLAIMSUCCESSFUL_EVENT, transactionReceipt);
        ArrayList<SuperblockClaimSuccessfulEventResponse> responses = new ArrayList<SuperblockClaimSuccessfulEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            SuperblockClaimSuccessfulEventResponse typedResponse = new SuperblockClaimSuccessfulEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.superblockHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.submitter = (String) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<SuperblockClaimSuccessfulEventResponse> superblockClaimSuccessfulEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, SuperblockClaimSuccessfulEventResponse>() {
            @Override
            public SuperblockClaimSuccessfulEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(SUPERBLOCKCLAIMSUCCESSFUL_EVENT, log);
                SuperblockClaimSuccessfulEventResponse typedResponse = new SuperblockClaimSuccessfulEventResponse();
                typedResponse.log = log;
                typedResponse.superblockHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.submitter = (String) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<SuperblockClaimSuccessfulEventResponse> superblockClaimSuccessfulEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(SUPERBLOCKCLAIMSUCCESSFUL_EVENT));
        return superblockClaimSuccessfulEventFlowable(filter);
    }

    public List<VerificationGameStartedEventResponse> getVerificationGameStartedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(VERIFICATIONGAMESTARTED_EVENT, transactionReceipt);
        ArrayList<VerificationGameStartedEventResponse> responses = new ArrayList<VerificationGameStartedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            VerificationGameStartedEventResponse typedResponse = new VerificationGameStartedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.superblockHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.submitter = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.challenger = (String) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.sessionId = (byte[]) eventValues.getNonIndexedValues().get(3).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<VerificationGameStartedEventResponse> verificationGameStartedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, VerificationGameStartedEventResponse>() {
            @Override
            public VerificationGameStartedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(VERIFICATIONGAMESTARTED_EVENT, log);
                VerificationGameStartedEventResponse typedResponse = new VerificationGameStartedEventResponse();
                typedResponse.log = log;
                typedResponse.superblockHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.submitter = (String) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.challenger = (String) eventValues.getNonIndexedValues().get(2).getValue();
                typedResponse.sessionId = (byte[]) eventValues.getNonIndexedValues().get(3).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<VerificationGameStartedEventResponse> verificationGameStartedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(VERIFICATIONGAMESTARTED_EVENT));
        return verificationGameStartedEventFlowable(filter);
    }

    public RemoteFunctionCall<BigInteger> battleReward() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_BATTLEREWARD, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> bondDeposit(byte[] superblockHash, String account, BigInteger amount) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_BONDDEPOSIT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(superblockHash), 
                new org.web3j.abi.datatypes.Address(account), 
                new org.web3j.abi.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> challengeCost() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_CHALLENGECOST, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> challengeSuperblock(byte[] superblockHash) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_CHALLENGESUPERBLOCK, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(superblockHash)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> checkClaimFinished(byte[] superblockHash) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_CHECKCLAIMFINISHED, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(superblockHash)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Tuple8<byte[], String, BigInteger, BigInteger, BigInteger, Boolean, Boolean, Boolean>> claims(byte[] param0) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_CLAIMS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Bool>() {}, new TypeReference<Bool>() {}, new TypeReference<Bool>() {}));
        return new RemoteFunctionCall<Tuple8<byte[], String, BigInteger, BigInteger, BigInteger, Boolean, Boolean, Boolean>>(function,
                new Callable<Tuple8<byte[], String, BigInteger, BigInteger, BigInteger, Boolean, Boolean, Boolean>>() {
                    @Override
                    public Tuple8<byte[], String, BigInteger, BigInteger, BigInteger, Boolean, Boolean, Boolean> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple8<byte[], String, BigInteger, BigInteger, BigInteger, Boolean, Boolean, Boolean>(
                                (byte[]) results.get(0).getValue(), 
                                (String) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue(), 
                                (BigInteger) results.get(3).getValue(), 
                                (BigInteger) results.get(4).getValue(), 
                                (Boolean) results.get(5).getValue(), 
                                (Boolean) results.get(6).getValue(), 
                                (Boolean) results.get(7).getValue());
                    }
                });
    }

    public RemoteFunctionCall<TransactionReceipt> confirmClaim(byte[] superblockHash, byte[] descendantId) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_CONFIRMCLAIM, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(superblockHash), 
                new org.web3j.abi.datatypes.generated.Bytes32(descendantId)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> deposits(String param0) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_DEPOSITS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> getBondedDeposit(byte[] superblockHash, String account) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETBONDEDDEPOSIT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(superblockHash), 
                new org.web3j.abi.datatypes.Address(account)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> getClaimChallengeTimeout(byte[] superblockHash) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETCLAIMCHALLENGETIMEOUT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(superblockHash)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<List> getClaimChallengers(byte[] superblockHash) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETCLAIMCHALLENGERS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(superblockHash)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Address>>() {}));
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

    public RemoteFunctionCall<Boolean> getClaimDecided(byte[] superblockHash) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETCLAIMDECIDED, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(superblockHash)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<Boolean> getClaimExists(byte[] superblockHash) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETCLAIMEXISTS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(superblockHash)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<Boolean> getClaimInvalid(byte[] superblockHash) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETCLAIMINVALID, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(superblockHash)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<BigInteger> getClaimRemainingChallengers(byte[] superblockHash) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETCLAIMREMAININGCHALLENGERS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(superblockHash)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<String> getClaimSubmitter(byte[] superblockHash) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETCLAIMSUBMITTER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(superblockHash)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<Boolean> getClaimVerificationOngoing(byte[] superblockHash) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETCLAIMVERIFICATIONONGOING, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(superblockHash)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<BigInteger> getDeposit(String account) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETDEPOSIT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(account)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<Boolean> getInBattleAndSemiApprovable(byte[] superblockHash) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETINBATTLEANDSEMIAPPROVABLE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(superblockHash)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<BigInteger> getNewSuperblockEventTimestamp(byte[] superblockHash) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETNEWSUPERBLOCKEVENTTIMESTAMP, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(superblockHash)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<byte[]> getSession(byte[] superblockHash, String challenger) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETSESSION, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(superblockHash), 
                new org.web3j.abi.datatypes.Address(challenger)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteFunctionCall<TransactionReceipt> initialize(String superblocks, String battleManager, BigInteger initSuperblockDelay, BigInteger initSuperblockTimeout, BigInteger initSuperblockConfirmations, BigInteger initBattleReward) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_INITIALIZE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(superblocks), 
                new org.web3j.abi.datatypes.Address(battleManager), 
                new org.web3j.abi.datatypes.generated.Uint256(initSuperblockDelay), 
                new org.web3j.abi.datatypes.generated.Uint256(initSuperblockTimeout), 
                new org.web3j.abi.datatypes.generated.Uint256(initSuperblockConfirmations), 
                new org.web3j.abi.datatypes.generated.Uint256(initBattleReward)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> makeDeposit(BigInteger weiValue) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_MAKEDEPOSIT, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
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

    public RemoteFunctionCall<TransactionReceipt> proposeSuperblock(byte[] blocksMerkleRoot, BigInteger accumulatedWork, BigInteger timestamp, BigInteger prevTimestamp, byte[] lastHash, BigInteger lastBits, byte[] parentHash) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_PROPOSESUPERBLOCK, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(blocksMerkleRoot), 
                new org.web3j.abi.datatypes.generated.Uint256(accumulatedWork), 
                new org.web3j.abi.datatypes.generated.Uint256(timestamp), 
                new org.web3j.abi.datatypes.generated.Uint256(prevTimestamp), 
                new org.web3j.abi.datatypes.generated.Bytes32(lastHash), 
                new org.web3j.abi.datatypes.generated.Uint32(lastBits), 
                new org.web3j.abi.datatypes.generated.Bytes32(parentHash)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
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

    public RemoteFunctionCall<TransactionReceipt> rejectClaim(byte[] superblockHash) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_REJECTCLAIM, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(superblockHash)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
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

    public RemoteFunctionCall<TransactionReceipt> sessionDecided(byte[] sessionId, byte[] superblockHash, String winner, String loser) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_SESSIONDECIDED, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(sessionId), 
                new org.web3j.abi.datatypes.generated.Bytes32(superblockHash), 
                new org.web3j.abi.datatypes.Address(winner), 
                new org.web3j.abi.datatypes.Address(loser)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> superblockConfirmations() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_SUPERBLOCKCONFIRMATIONS, 
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

    public RemoteFunctionCall<BigInteger> superblockDelay() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_SUPERBLOCKDELAY, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> superblockTimeout() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_SUPERBLOCKTIMEOUT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<String> trustedDogeBattleManager() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_TRUSTEDDOGEBATTLEMANAGER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> trustedSuperblocks() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_TRUSTEDSUPERBLOCKS, 
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

    public RemoteFunctionCall<TransactionReceipt> withdrawDeposit(BigInteger amount) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_WITHDRAWDEPOSIT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static SuperblockClaims load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new SuperblockClaims(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static SuperblockClaims load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new SuperblockClaims(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static SuperblockClaims load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new SuperblockClaims(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static SuperblockClaims load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new SuperblockClaims(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static class DepositBondedEventResponse extends BaseEventResponse {
        public byte[] superblockHash;

        public String account;

        public BigInteger amount;
    }

    public static class DepositMadeEventResponse extends BaseEventResponse {
        public String who;

        public BigInteger amount;
    }

    public static class DepositUnbondedEventResponse extends BaseEventResponse {
        public byte[] superblockHash;

        public String account;

        public BigInteger amount;
    }

    public static class DepositWithdrawnEventResponse extends BaseEventResponse {
        public String who;

        public BigInteger amount;
    }

    public static class SuperblockBattleDecidedEventResponse extends BaseEventResponse {
        public byte[] sessionId;

        public String winner;

        public String loser;
    }

    public static class SuperblockClaimChallengedEventResponse extends BaseEventResponse {
        public byte[] superblockHash;

        public String challenger;
    }

    public static class SuperblockClaimCreatedEventResponse extends BaseEventResponse {
        public byte[] superblockHash;

        public String submitter;
    }

    public static class SuperblockClaimFailedEventResponse extends BaseEventResponse {
        public byte[] superblockHash;

        public String submitter;
    }

    public static class SuperblockClaimPendingEventResponse extends BaseEventResponse {
        public byte[] superblockHash;

        public String submitter;
    }

    public static class SuperblockClaimSuccessfulEventResponse extends BaseEventResponse {
        public byte[] superblockHash;

        public String submitter;
    }

    public static class VerificationGameStartedEventResponse extends BaseEventResponse {
        public byte[] superblockHash;

        public String submitter;

        public String challenger;

        public byte[] sessionId;
    }
}
