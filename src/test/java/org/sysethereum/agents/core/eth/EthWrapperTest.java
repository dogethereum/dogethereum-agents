package org.sysethereum.agents.core.eth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.web3j.abi.datatypes.Address;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class EthWrapperTest {

    @Test
    void test_AddressComparison() {
        Address address = new Address("01391a7a726fcfeb6206e310082fa5942ae7ab95");
        assertEquals("0x01391a7a726fcfeb6206e310082fa5942ae7ab95", address.getValue());
    }
}