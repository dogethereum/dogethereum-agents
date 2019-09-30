package org.sysethereum.agents.core.bridge;

import org.springframework.stereotype.Service;
import org.sysethereum.agents.contract.SyscoinSuperblocks;
import org.sysethereum.agents.contract.SyscoinSuperblocksExtended;
import org.sysethereum.agents.core.syscoin.Keccak256Hash;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.protocol.core.DefaultBlockParameter;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
public class SuperblockContractApi {

    private final SyscoinSuperblocksExtended main;
    private final SyscoinSuperblocksExtended getter;

    public SuperblockContractApi(
            SyscoinSuperblocksExtended superblocks,
            SyscoinSuperblocksExtended superblocksGetter
    ) {
        this.main = superblocks;
        this.getter = superblocksGetter;
    }

    private BigInteger getStatus(Keccak256Hash superblockId) throws Exception {
        return getter.getSuperblockStatus(new Bytes32(superblockId.getBytes())).send().getValue();
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
        return Keccak256Hash.wrap(getter.getBestSuperblock().send().getValue());
    }

    public BigInteger getHeight(Keccak256Hash superblockId) throws Exception {
        return getter.getSuperblockHeight(new Bytes32(superblockId.getBytes())).send().getValue();
    }

    public BigInteger getChainHeight() throws Exception {
        return getter.getChainHeight().send().getValue();
    }

    public void updateGasPrice(BigInteger gasPriceMinimum) {
        //noinspection deprecation
        main.setGasPrice(gasPriceMinimum);
        //noinspection deprecation
        getter.setGasPrice(gasPriceMinimum);
    }

    public static class SuperblockEvent {
        public Keccak256Hash superblockId;
        public String who;
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
        List<SuperblockEvent> result = new ArrayList<>();
        List<SyscoinSuperblocks.NewSuperblockEventResponse> newSuperblockEvents =
                getter.getNewSuperblockEvents(
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock)),
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(endBlock)));

        for (SyscoinSuperblocks.NewSuperblockEventResponse response : newSuperblockEvents) {
            SuperblockEvent event = new SuperblockEvent();
            event.superblockId = Keccak256Hash.wrap(response.superblockHash.getValue());
            event.who = response.who.getValue();
            result.add(event);
        }

        return result;
    }

    /**
     * Listens to ApprovedSuperblock events from SyscoinSuperblocks contract within a given block window
     * and parses web3j-generated instances into easier to manage SuperblockEvent objects.
     * @param startBlock First Ethereum block to poll.
     * @param endBlock Last Ethereum block to poll.
     * @return All ApprovedSuperblock events from SyscoinSuperblocks as SuperblockEvent objects.
     * @throws IOException
     */
    public List<SuperblockEvent> getApprovedSuperblocks(long startBlock, long endBlock)
            throws IOException {
        List<SuperblockEvent> result = new ArrayList<>();
        List<SyscoinSuperblocks.ApprovedSuperblockEventResponse> approvedSuperblockEvents =
                getter.getApprovedSuperblockEvents(
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock)),
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(endBlock)));

        for (SyscoinSuperblocks.ApprovedSuperblockEventResponse response : approvedSuperblockEvents) {
            SuperblockEvent event = new SuperblockEvent();
            event.superblockId = Keccak256Hash.wrap(response.superblockHash.getValue());
            event.who = response.who.getValue();
            result.add(event);
        }

        return result;
    }

    /**
     * Listens to SemiApprovedSuperblock events from SyscoinSuperblocks contract within a given block window
     * and parses web3j-generated instances into easier to manage SuperblockEvent objects.
     * @param startBlock First Ethereum block to poll.
     * @param endBlock Last Ethereum block to poll.
     * @return All SemiApprovedSuperblock events from SyscoinSuperblocks as SuperblockEvent objects.
     * @throws IOException
     */
    public List<SuperblockEvent> getSemiApprovedSuperblocks(long startBlock, long endBlock)
            throws IOException {
        List<SuperblockEvent> result = new ArrayList<>();
        List<SyscoinSuperblocks.SemiApprovedSuperblockEventResponse> semiApprovedSuperblockEvents =
                getter.getSemiApprovedSuperblockEvents(
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock)),
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(endBlock)));

        for (SyscoinSuperblocks.SemiApprovedSuperblockEventResponse response : semiApprovedSuperblockEvents) {
            SuperblockEvent event = new SuperblockEvent();
            event.superblockId = Keccak256Hash.wrap(response.superblockHash.getValue());
            event.who = response.who.getValue();
            result.add(event);
        }

        return result;
    }

    /**
     * Listens to InvalidSuperblock events from SyscoinSuperblocks contract within a given block window
     * and parses web3j-generated instances into easier to manage SuperblockEvent objects.
     * @param startBlock First Ethereum block to poll.
     * @param endBlock Last Ethereum block to poll.
     * @return All InvalidSuperblock events from SyscoinSuperblocks as SuperblockEvent objects.
     * @throws IOException
     */
    public List<SuperblockEvent> getInvalidSuperblocks(long startBlock, long endBlock)
            throws IOException {
        List<SuperblockEvent> result = new ArrayList<>();
        List<SyscoinSuperblocks.InvalidSuperblockEventResponse> invalidSuperblockEvents =
                getter.getInvalidSuperblockEvents(
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock)),
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(endBlock)));

        for (SyscoinSuperblocks.InvalidSuperblockEventResponse response : invalidSuperblockEvents) {
            SuperblockEvent event = new SuperblockEvent();
            event.superblockId = Keccak256Hash.wrap(response.superblockHash.getValue());
            event.who = response.who.getValue();
            result.add(event);
        }

        return result;
    }
}
