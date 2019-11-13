package org.sysethereum.agents.util;

import com.google.common.util.concurrent.AtomicDouble;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@Slf4j(topic = "RandomizationCounter")
public class RandomizationCounter {

    private final AtomicDouble value = new AtomicDouble();
    private final Random random;

    @Autowired
    public RandomizationCounter() {
        this(new Random());
    }

    /**
     * For testing
     */
    public RandomizationCounter(Random random) {
        this.random = random;
        updateRandomValue();
    }

    /**
     * @return A random value between 0.1 to 1.0 (both inclusive)
     */
    public double getValue() {
        return value.get();
    }

    public double updateRandomValue() {
        int rndNumber = random.nextInt(91) + 10; // a random number between 10 (inclusive) and 100 (inclusive)
        double result = rndNumber / 100.0f;
        value.set(result);

        return result;
    }

}
