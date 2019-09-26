package org.sysethereum.agents.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RandomizationCounterTest {

    @Test
    void updateRandomValue() {

        Random random = mock(Random.class);
        // First call of `random.nextInt(91)` will return 90, the second call will return 10
        when(random.nextInt(91)).thenReturn(90, 10);

        var underTest = new RandomizationCounter(random);

        double r1 = underTest.getValue();
        assertEquals(1.0f, r1);

        double r2 = underTest.updateRandomValue();
        assertEquals(0.2f, r2);
    }
}