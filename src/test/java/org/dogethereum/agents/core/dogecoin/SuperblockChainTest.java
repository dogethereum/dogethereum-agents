package org.sysethereum.agents.core.syscoin;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SuperblockChainTest {
    SuperblockChain sb;
    SimpleDateFormat format;

    @Before
    public void init() throws Exception {
        sb = new SuperblockChain();
        format = new SimpleDateFormat("HH:mm:ss.SSS");
    }

    @Test
    public void testStartTime1() throws Exception {
        sb.SUPERBLOCK_DURATION = 120;
        Date firstBlockTimestamp = format.parse("22:31:08.123");
        Date expectedStartTime = format.parse("22:30:00.000");
        Date startTime = sb.getStartTime(firstBlockTimestamp);
        assertEquals(expectedStartTime, startTime);
    }

    @Test
    public void testStartTime2() throws Exception {
        sb.SUPERBLOCK_DURATION = 120;
        Date firstBlockTimestamp = format.parse("22:30:00.000");
        Date expectedStartTime = format.parse("22:30:00.000");
        Date startTime = sb.getStartTime(firstBlockTimestamp);
        assertEquals(expectedStartTime, startTime);
    }

    @Test
    public void testStartTime3() throws Exception {
        sb.SUPERBLOCK_DURATION = 120;
        Date firstBlockTimestamp = format.parse("22:31:59.999");
        Date expectedStartTime = format.parse("22:30:00.000");
        Date startTime = sb.getStartTime(firstBlockTimestamp);
        assertEquals(expectedStartTime, startTime);
    }

    @Test
    public void testStartTime4() throws Exception {
        sb.SUPERBLOCK_DURATION = 120;
        Date firstBlockTimestamp = format.parse("22:00:00.000");
        Date expectedStartTime = format.parse("22:00:00.000");
        Date startTime = sb.getStartTime(firstBlockTimestamp);
        assertEquals(expectedStartTime, startTime);
    }

    @Test
    public void testStartTime5() throws Exception {
        sb.SUPERBLOCK_DURATION = 3600;
        Date firstBlockTimestamp = format.parse("22:31:17.123");
        Date expectedStartTime = format.parse("22:00:00.000");
        Date startTime = sb.getStartTime(firstBlockTimestamp);
        assertEquals(expectedStartTime, startTime);
    }

    @Test
    public void testStartTime6() throws Exception {
        sb.SUPERBLOCK_DURATION = 7200;
        Date firstBlockTimestamp = format.parse("22:31:17.123");
        Date expectedStartTime = format.parse("22:00:00.000");
        Date startTime = sb.getStartTime(firstBlockTimestamp);
        assertEquals(expectedStartTime, startTime);
    }

    @Test
    public void testStartTime7() throws Exception {
        sb.SUPERBLOCK_DURATION = 7200;
        Date firstBlockTimestamp = format.parse("23:31:17.123");
        Date expectedStartTime = format.parse("22:00:00.000");
        Date startTime = sb.getStartTime(firstBlockTimestamp);
        assertEquals(expectedStartTime, startTime);
    }

    @Test
    public void testEndTime() throws Exception {
        sb.SUPERBLOCK_DURATION = 120;
        Date startTime = format.parse("22:30:00.000");
        Date expectedEndTime = format.parse("22:32:00.000");
        Date endTime = sb.getEndTime(startTime);
        assertEquals(expectedEndTime, endTime);
    }

}
