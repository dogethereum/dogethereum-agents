package org.sysethereum.agents.core.bridge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.sysethereum.agents.contract.SyscoinBattleManager;
import org.sysethereum.agents.contract.SyscoinBattleManagerExtended;
import org.sysethereum.agents.core.eth.EthWrapper;
import org.sysethereum.agents.core.syscoin.Keccak256Hash;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.protocol.core.DefaultBlockParameter;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
public class BattleContractApi {

    private static final Logger logger = LoggerFactory.getLogger("BattleContractApi");

    private final SyscoinBattleManagerExtended main;
    private final SyscoinBattleManagerExtended getter;
    private final SyscoinBattleManagerExtended challenges;
    private final SyscoinBattleManagerExtended challengesGetter;

    public BattleContractApi(
            SyscoinBattleManagerExtended battleManager,
            SyscoinBattleManagerExtended battleManagerGetter,
            SyscoinBattleManagerExtended battleManagerForChallenges,
            SyscoinBattleManagerExtended battleManagerForChallengesGetter
    ) {
        this.main = battleManager;
        this.getter = battleManagerGetter;
        this.challenges = battleManagerForChallenges;
        this.challengesGetter = battleManagerForChallengesGetter;
    }

    public void updateGasPrice(BigInteger gasPriceMinimum) {
        //noinspection deprecation
        main.setGasPrice(gasPriceMinimum);
        //noinspection deprecation
        getter.setGasPrice(gasPriceMinimum);
        //noinspection deprecation
        challenges.setGasPrice(gasPriceMinimum);
        //noinspection deprecation
        challengesGetter.setGasPrice(gasPriceMinimum);
    }

    public boolean getSubmitterHitTimeout(Keccak256Hash sessionId) throws Exception {
        return challengesGetter.getSubmitterHitTimeout(new Bytes32(sessionId.getBytes())).send().getValue();
    }

    public int getNumMerkleHashesBySession(Keccak256Hash sessionId) throws Exception {
        BigInteger ret = getter.getNumMerkleHashesBySession(new Bytes32(sessionId.getBytes())).send().getValue();
        return ret.intValue();
    }

    public EthWrapper.ChallengeState getSessionChallengeState(Keccak256Hash sessionId) throws Exception {
        BigInteger ret = getter.getSessionChallengeState(new Bytes32(sessionId.getBytes())).send().getValue();
        return EthWrapper.ChallengeState.values()[ret.intValue()];
    }

    /**
     * Listens to NewBattle events from SyscoinBattleManager contract within a given block window
     * and parses web3j-generated instances into easier to manage NewBattleEvent objects.
     * @param startBlock First Ethereum block to poll.
     * @param endBlock Last Ethereum block to poll.
     * @return All NewBattle events from SyscoinBattleManager as NewBattleEvent objects.
     * @throws IOException
     */
    public List<EthWrapper.NewBattleEvent> getNewBattleEvents(long startBlock, long endBlock) throws IOException {
        List<EthWrapper.NewBattleEvent> result = new ArrayList<>();
        List<SyscoinBattleManager.NewBattleEventResponse> newBattleEvents =
                challengesGetter.getNewBattleEventResponses(
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock)),
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(endBlock)));

        for (SyscoinBattleManager.NewBattleEventResponse response : newBattleEvents) {
            EthWrapper.NewBattleEvent newBattleEvent = new EthWrapper.NewBattleEvent();
            newBattleEvent.superblockHash = Keccak256Hash.wrap(response.superblockHash.getValue());
            newBattleEvent.sessionId = Keccak256Hash.wrap(response.sessionId.getValue());
            newBattleEvent.submitter = response.submitter.getValue();
            newBattleEvent.challenger = response.challenger.getValue();
            result.add(newBattleEvent);
        }

        return result;
    }
}
