package org.sysethereum.agents.core.bridge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.sysethereum.agents.contract.SyscoinBattleManager;
import org.sysethereum.agents.contract.SyscoinBattleManagerExtended;
import org.sysethereum.agents.core.bridge.battle.NewBattleEvent;
import org.sysethereum.agents.core.syscoin.Keccak256Hash;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.protocol.core.DefaultBlockParameter;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
public class BattleContractApi {

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger("BattleContractApi");

    private final SyscoinBattleManagerExtended main;
    private final SyscoinBattleManagerExtended challenges;

    public BattleContractApi(
            SyscoinBattleManagerExtended battleManager,
            SyscoinBattleManagerExtended battleManagerForChallenges
    ) {
        this.main = battleManager;
        this.challenges = battleManagerForChallenges;
    }
    public SyscoinBattleManagerExtended getBattleManagerForChallenges(){
        return challenges;
    }
    public void updateGasPrice(BigInteger gasPriceMinimum) {
        //noinspection deprecation
        main.setGasPrice(gasPriceMinimum);
        //noinspection deprecation
        challenges.setGasPrice(gasPriceMinimum);
    }

    public boolean getSubmitterHitTimeout(Keccak256Hash superblockHash) throws Exception {
        return challenges.getSubmitterHitTimeout(new Bytes32(superblockHash.getBytes())).send().getValue();
    }

    public int getNumMerkleHashesBySession(Keccak256Hash superblockHash) throws Exception {
        BigInteger ret = main.getNumMerkleHashesBySession(new Bytes32(superblockHash.getBytes())).send().getValue();
        return ret.intValue();
    }

    public boolean sessionExists(Keccak256Hash superblockHash) throws Exception {
        return main.sessionExists(new Bytes32(superblockHash.getBytes())).send().getValue();
    }

    /**
     * Listens to NewBattle events from SyscoinBattleManager contract within a given block window
     * and parses web3j-generated instances into easier to manage NewBattleEvent objects.
     *
     * @param startBlock First Ethereum block to poll.
     * @param endBlock   Last Ethereum block to poll.
     * @return All NewBattle events from SyscoinBattleManager as NewBattleEvent objects.
     * @throws IOException
     */
    public List<NewBattleEvent> getNewBattleEvents(long startBlock, long endBlock) throws IOException {
        List<NewBattleEvent> result = new ArrayList<>();
        List<SyscoinBattleManager.NewBattleEventResponse> newBattleEvents =
                challenges.getNewBattleEventResponses(
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock)),
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(endBlock)));

        for (SyscoinBattleManager.NewBattleEventResponse response : newBattleEvents) {
            NewBattleEvent newBattleEvent = new NewBattleEvent();
            newBattleEvent.superblockHash = Keccak256Hash.wrap(response.superblockHash.getValue());
            newBattleEvent.submitter = response.submitter.getValue();
            newBattleEvent.challenger = response.challenger.getValue();
            result.add(newBattleEvent);
        }

        return result;
    }
}
