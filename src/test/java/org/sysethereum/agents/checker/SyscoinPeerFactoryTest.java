package org.sysethereum.agents.checker;

import org.bitcoinj.core.PeerAddress;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sysethereum.agents.constants.LocalAgentConstantsFactory;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class SyscoinPeerFactoryTest {

    @Test
    void buildSyscoinPeerAddresses() throws UnknownHostException {
        SyscoinPeerFactory underTest = new SyscoinPeerFactory(new LocalAgentConstantsFactory().create());

        List<String> list = new ArrayList<>();
        list.add("127.0.0.1");

        List<PeerAddress> result = underTest.buildSyscoinPeerAddresses(8000, list);

        assertEquals(1, result.size());
        assertEquals(8000, result.get(0).getPort());
        assertEquals("[127.0.0.1]:8000", result.get(0).toString());
    }
}