package org.sysethereum.agents.core.bridge;

import com.google.common.io.BaseEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.sysethereum.agents.contract.SyscoinSuperblocksExtended;
import org.sysethereum.agents.core.syscoin.BlockSPVProof;
import org.sysethereum.agents.core.eth.SuperblockSPVProof;
import org.sysethereum.agents.core.syscoin.Keccak256Hash;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.DynamicBytes;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.util.stream.Collectors.toList;

@Service
public class SuperblockContractApi {
    private static final Logger logger = LoggerFactory.getLogger("SuperblockContractApi");
    private final SyscoinSuperblocksExtended superblocks;
    private final SyscoinSuperblocksExtended superblocksForChallenges;

    public SuperblockContractApi(
            SyscoinSuperblocksExtended superblocks,
            SyscoinSuperblocksExtended superblocksForChallenges
    ) {
        this.superblocks = superblocks;
        this.superblocksForChallenges = superblocksForChallenges;
    }

    private BigInteger getStatus(Keccak256Hash superblockId) throws Exception {
        return superblocks.getSuperblockStatus(new Bytes32(superblockId.getBytes())).send().getValue();
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
        return Keccak256Hash.wrap(superblocks.getBestSuperblock().send().getValue());
    }

    public BigInteger getHeight(Keccak256Hash superblockId) throws Exception {
        return superblocks.getSuperblockHeight(new Bytes32(superblockId.getBytes())).send().getValue();
    }

    public BigInteger getChainHeight() throws Exception {
        return superblocks.getChainHeight().send().getValue();
    }

    public void updateGasPrice(BigInteger gasPriceMinimum) {
        //noinspection deprecation
        superblocks.setGasPrice(gasPriceMinimum);
        superblocksForChallenges.setGasPrice(gasPriceMinimum);
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

        return superblocks.getNewSuperblockEvents(startBlock, endBlock)
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

        return superblocks.getSemiApprovedSuperblockEvents(startBlock, endBlock)
                .stream().map(response -> new SuperblockEvent(
                        Keccak256Hash.wrap(response.superblockHash.getValue()),
                        response.who.getValue()
                ))
                .collect(toList());
    }

    public void challengeCancelTransfer(BlockSPVProof blockSPVProof, SuperblockSPVProof superblockSPVProof){

        List<Uint256> txSiblings = new ArrayList<>();
        for(int i =0;i<blockSPVProof.siblings.size();i++){
            txSiblings.add(i, new Uint256(new BigInteger(blockSPVProof.siblings.get(i), 16)));
        }
        List<Uint256> blockSiblings = new ArrayList<>();
        for(int i =0;i<superblockSPVProof.merklePath.size();i++){
            blockSiblings.add(i, new Uint256(new BigInteger(superblockSPVProof.merklePath.get(i), 16)));
        }
        CompletableFuture<TransactionReceipt> futureReceipt = superblocksForChallenges.challengeCancelTransfer(new DynamicBytes(BaseEncoding.base16().lowerCase().decode(blockSPVProof.transaction.toLowerCase())), new Uint256(blockSPVProof.index),new DynamicArray<Uint256>(txSiblings),
                new DynamicBytes(BaseEncoding.base16().lowerCase().decode(blockSPVProof.header.toLowerCase())), new Uint256(superblockSPVProof.index), new DynamicArray<Uint256>(blockSiblings), new Bytes32(BaseEncoding.base16().decode(superblockSPVProof.superBlock))).sendAsync();

        futureReceipt.thenAcceptAsync((TransactionReceipt receipt) ->
                logger.info("challengeCancelTransfer receipt {}", receipt.toString()));
    }

}
