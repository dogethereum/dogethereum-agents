package org.sysethereum.agents.core.bridge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.sysethereum.agents.contract.SyscoinERC20Manager;
import org.sysethereum.agents.contract.SyscoinERC20ManagerExtended;
import org.sysethereum.agents.core.bridge.battle.NewCancelTransferRequestEvent;
import org.web3j.protocol.core.DefaultBlockParameter;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
public class ERC20ManagerContractApi {

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger("ERC20ManagerContractApi");

    private final SyscoinERC20ManagerExtended challenges;

    public ERC20ManagerContractApi(
            SyscoinERC20ManagerExtended erc20ManagerForChallenges
    ) {
        this.challenges = erc20ManagerForChallenges;
    }

    /**
     * Listens to CancelTransferRequest events from SyscoinERC20Manager contract within a given block window
     * and parses web3j-generated instances into easier to manage NewCancelTransferRequest objects.
     *
     * @param startBlock First Ethereum block to poll.
     * @param endBlock   Last Ethereum block to poll.
     * @return All CancelTransferRequest events from SyscoinERC20Manager as NewCancelTransferRequest objects.
     * @throws IOException
     */
    public List<NewCancelTransferRequestEvent> getNewCancelTransferEvents(long startBlock, long endBlock) throws IOException {
        List<NewCancelTransferRequestEvent> result = new ArrayList<>();
        List<SyscoinERC20Manager.CancelTransferRequestEventResponse> newCancelTransferEvents =
                challenges.getCancelTransferRequestEvents(
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock)),
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(endBlock)));

        for (SyscoinERC20Manager.CancelTransferRequestEventResponse response : newCancelTransferEvents) {
            NewCancelTransferRequestEvent newCancelTransferRequestEvent = new NewCancelTransferRequestEvent();
            newCancelTransferRequestEvent.canceller = response.canceller.getValue();
            newCancelTransferRequestEvent.bridgeTransferId = response.bridgetransferid.getValue().intValue();
            result.add(newCancelTransferRequestEvent);
        }
        return result;
    }
}
