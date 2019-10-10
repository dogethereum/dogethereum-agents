package org.sysethereum.agents.core.bridge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.sysethereum.agents.contract.SyscoinBattleManagerExtended;
import org.sysethereum.agents.core.eth.EthWrapper;
import org.sysethereum.agents.core.syscoin.Keccak256Hash;
import org.web3j.abi.datatypes.generated.Bytes32;

import java.math.BigInteger;

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
}
