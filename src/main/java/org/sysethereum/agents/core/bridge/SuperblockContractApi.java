package org.sysethereum.agents.core.bridge;

import org.springframework.stereotype.Service;
import org.sysethereum.agents.contract.SyscoinSuperblocksExtended;
import org.sysethereum.agents.core.syscoin.Keccak256Hash;
import org.web3j.abi.datatypes.generated.Bytes32;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class SuperblockContractApi {

    private final SyscoinSuperblocksExtended main;

    public SuperblockContractApi(
            SyscoinSuperblocksExtended superblocks
    ) {
        this.main = superblocks;
    }

    private BigInteger getStatus(Keccak256Hash superblockId) throws Exception {
        return main.getSuperblockStatus(new Bytes32(superblockId.getBytes())).send().getValue();
    }

    public boolean isApproved(Keccak256Hash superblockId) throws Exception {
        return getStatus(superblockId).equals(Superblock.STATUS_APPROVED);
    }

    public boolean isSemiApproved(Keccak256Hash superblockId) throws Exception {
        return getStatus(superblockId).equals(Superblock.STATUS_SEMI_APPROVED);
    }

    public boolean isNew(Keccak256Hash superblockId) throws Exception {
        return getStatus(superblockId).equals(Superblock.STATUS_NEW);
    }

    public Keccak256Hash getBestSuperblockId() throws Exception {
        return Keccak256Hash.wrap(main.getBestSuperblock().send().getValue());
    }

    public BigInteger getHeight(Keccak256Hash superblockId) throws Exception {
        return main.getSuperblockHeight(new Bytes32(superblockId.getBytes())).send().getValue();
    }

    public BigInteger getChainHeight() throws Exception {
        return main.getChainHeight().send().getValue();
    }

    public void updateGasPrice(BigInteger gasPriceMinimum) {
        //noinspection deprecation
        main.setGasPrice(gasPriceMinimum);
    }

    public static class SuperblockEvent {
        public final Keccak256Hash superblockId;
        public final String who;

        public SuperblockEvent(Keccak256Hash superblockId, String who) {
            this.superblockId = superblockId;
            this.who = who;
        }
    }

    /**
     * Listens to NewSuperblock events from SyscoinSuperblocks contract within a given block window
     * and parses web3j-generated instances into easier to manage SuperblockEvent objects.
     * @param startBlock First Ethereum block to poll.
     * @param endBlock Last Ethereum block to poll.
     * @return All NewSuperblock events from SyscoinSuperblocks as SuperblockEvent objects.
     * @throws IOException
     */
    public List<SuperblockEvent> getNewSuperblocks(long startBlock, long endBlock) throws IOException {

        return main.getNewSuperblockEvents(startBlock, endBlock)
                .stream().map(response -> new SuperblockEvent(
                        Keccak256Hash.wrap(response.superblockHash.getValue()),
                        response.who.getValue()
                ))
                .collect(toList());
    }


    /**
     * Listens to SemiApprovedSuperblock events from SyscoinSuperblocks contract within a given block window
     * and parses web3j-generated instances into easier to manage SuperblockEvent objects.
     * @param startBlock First Ethereum block to poll.
     * @param endBlock Last Ethereum block to poll.
     * @return All SemiApprovedSuperblock events from SyscoinSuperblocks as SuperblockEvent objects.
     * @throws IOException
     */
    public List<SuperblockEvent> getSemiApprovedSuperblocks(long startBlock, long endBlock) throws IOException {

        return main.getSemiApprovedSuperblockEvents(startBlock, endBlock)
                .stream().map(response -> new SuperblockEvent(
                        Keccak256Hash.wrap(response.superblockHash.getValue()),
                        response.who.getValue()
                ))
                .collect(toList());
    }
}
