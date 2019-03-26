package org.sysethereum.agents.core.syscoin;

import org.bitcoinj.core.Block;
import org.bitcoinj.core.UnsafeByteArrayOutputStream;
import org.bitcoinj.core.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;

/**
 * Parsing library for superblocks.
 * @author Catalina Juarros
 */
public class SuperblockUtils {

    /* ---- SUPERBLOCK STATUS CODES ---- */

    public static final BigInteger STATUS_UNINITIALIZED = BigInteger.valueOf(0);
    public static final BigInteger STATUS_NEW = BigInteger.valueOf(1);
    public static final BigInteger STATUS_IN_BATTLE = BigInteger.valueOf(2);
    public static final BigInteger STATUS_SEMI_APPROVED = BigInteger.valueOf(3);
    public static final BigInteger STATUS_APPROVED = BigInteger.valueOf(4);
    public static final BigInteger STATUS_INVALID = BigInteger.valueOf(5);

    public static byte[] toBytes32(BigInteger n) throws IOException {
        byte[] hex = n.toByteArray();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        for (int i = hex.length; i < 32; i++) outputStream.write(0); // pad with 0s
        outputStream.write(hex);
        return outputStream.toByteArray();
    }

    public static byte[] toBytes32(long n) throws IOException {
        byte[] hex = longToBytes(n);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        for (int i = 8; i < 32; i++) outputStream.write(0); // pad with 0s
        outputStream.write(hex);
        return outputStream.toByteArray();
    }

    public static byte[] toBytes32(int n) throws IOException {
        byte[] hex = intToBytes(n);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        for (int i = 4; i < 32; i++) outputStream.write(0); // pad with 0s
        outputStream.write(hex);
        return outputStream.toByteArray();
    }

    public static byte[] toUint32(long l) {
        byte[] result = new byte[4];
        for (int i = 3; i >= 0; i--) {
            result[i] = (byte)(l & 0xFF);
            l >>= 8;
        }
        return result;
    }

    public static byte[] longToBytes(long l) {
        byte[] result = new byte[8];
        for (int i = 7; i >= 0; i--) {
            result[i] = (byte)(l & 0xFF);
            l >>= 8;
        }
        return result;
    }

    public static byte[] intToBytes(int j) {
        byte[] result = new byte[4];
        for (int i = 3; i >= 0; i--) {
            result[i] = (byte)(j & 0xFF);
            j >>= 8;
        }
        return result;
    }

    public static byte[] readBytes(byte[] payload, int offset, int length) {
        byte[] b = new byte[length];
        System.arraycopy(payload, offset, b, 0, length);
        return b;
    }

    // this is missing in java.Utils for some utterly incomprehensible reason
    public static void uint32ToByteStreamBE(long val, OutputStream stream) throws IOException {
        stream.write((int) (0xFF & (val >> 24)));
        stream.write((int) (0xFF & (val >> 16)));
        stream.write((int) (0xFF & (val >> 8)));
        stream.write((int) (0xFF & val));
    }

    public static long readPaddedUint32(byte[] bytes, int offset) {
        int realOffset = offset + 28; // read last 4 bytes
        return (bytes[realOffset] & 0xffl) |
                ((bytes[realOffset + 1] & 0xffl) << 8) |
                ((bytes[realOffset + 2] & 0xffl) << 16) |
                ((bytes[realOffset + 3] & 0xffl) << 24);
    }

    /**
     * Get a timestamp from exactly n seconds before system time.
     * Useful for knowing when to stop building superblocks.
     * @param n Superblock timeout.
     * @return Timestamp from n seconds ago.
     */
    public static Date getNSecondsAgo(int n) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, -n);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * Copied from bitcoinj's Block.writeHeader().
     * @param block Syscoin block.
     * @return Serialized block header.
     */
    public static byte[] serializeBlockHeader(Block block) throws IOException {
        ByteArrayOutputStream stream = new UnsafeByteArrayOutputStream(80);

        Utils.uint32ToByteStreamLE(block.getVersion(), stream);
        stream.write(block.getPrevBlockHash().getReversedBytes());
        stream.write(block.getMerkleRoot().getReversedBytes());
        Utils.uint32ToByteStreamLE(block.getTimeSeconds(), stream);
        Utils.uint32ToByteStreamLE(block.getDifficultyTarget(), stream);
        Utils.uint32ToByteStreamLE(block.getNonce(), stream);

        return stream.toByteArray();
    }
}
