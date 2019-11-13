package org.sysethereum.agents.core.bridge;

import com.google.common.io.BaseEncoding;
import org.bitcoinj.core.Sha256Hash;
import org.junit.jupiter.api.Test;
import org.libdohj.params.SyscoinRegTestParams;
import org.sysethereum.agents.core.syscoin.Keccak256Hash;
import org.sysethereum.agents.service.rest.MerkleRootComputer;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class SuperblockSerializationHelperTest {

    public static final BaseEncoding BASE_ENCODING = BaseEncoding.base16().lowerCase();

    private static final String EXPECTED_RESULT = "a6a9566cbc61a9f6b28583025061403117fba5e9ef9d4f362521dfc02a26" +
            "70d2327a1a5d00000000000000000000000000000000000000000000000000000000327a1a5d0000000000000000000000" +
            "00000000000000000000000000000000006ffca8ce7e7f295c2adfbaf0d4895525299bb566d16ef22903c3fd4355cb7ca0" +
            "000000000000000000000000000000000000000000000000000000000000000000000000000000000300000068adb3be86" +
            "1f0eec10b62fea72e5d92ea5c6a5084b6642953445fec716502add1ec0859bcde1d91ce0d62faf47fe666525b5070d616e" +
            "b9660d44f2929f0f53426ffca8ce7e7f295c2adfbaf0d4895525299bb566d16ef22903c3fd4355cb7ca0";

    private static final String SYS_HASH_1 = "dd2a5016c7fe45349542664b08a5c6a52ed9e572ea2fb610ec0e1f86beb3ad68";
    private static final String SYS_HASH_2 = "42530f9f92f2440d66b96e610d07b5256566fe47af2fd6e01cd9e1cd9b85c01e";
    private static final String SYS_HASH_3 = "a07ccb5543fdc30329f26ed166b59b29255589d4f0badf2a5c297f7ecea8fc6f";

    @Test
    void serializeForStorage() {

        var hashes = new ArrayList<Sha256Hash>();
        hashes.add(Sha256Hash.wrap(SYS_HASH_1));
        hashes.add(Sha256Hash.wrap(SYS_HASH_2));
        hashes.add(Sha256Hash.wrap(SYS_HASH_3));

        SuperblockData data = new SuperblockData(
                MerkleRootComputer.computeMerkleRoot(SyscoinRegTestParams.get(), hashes),
                hashes,
                1562016306,
                1562016306,
                0,
                Keccak256Hash.wrap(new byte[32]), // initialised with 0s
                0
        );

        var underTest = new SuperblockSerializationHelper();

        String result = BASE_ENCODING.encode(underTest.serializeForStorage(data).toByteArray());
        assertEquals(EXPECTED_RESULT, result);
    }

    @Test
    void fromBytes() {
        var underTest = new SuperblockSerializationHelper();

        byte[] bytes = BASE_ENCODING.decode(EXPECTED_RESULT);

        SuperblockData data = underTest.fromBytes(bytes);
        assertEquals(0, data.superblockHeight);
        assertEquals(1562016306, data.lastSyscoinBlockTime);
        assertEquals(0, data.lastSyscoinBlockBits);
        assertEquals(Keccak256Hash.ZERO_HASH, data.parentId);
        assertEquals(3, data.syscoinBlockHashes.size());
        assertEquals(SYS_HASH_1, data.syscoinBlockHashes.get(0).toString());
        assertEquals(SYS_HASH_2, data.syscoinBlockHashes.get(1).toString());
        assertEquals(SYS_HASH_3, data.syscoinBlockHashes.get(2).toString());
    }
}