package itmo.lab8.basic.utils.generators;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * IdGenerator class. Used to generate unique ids.
 *
 * @author kxrxh
 */
public final class IdGenerator {
    /**
     * Unique id. Starts from 1
     */
    private static final AtomicLong longIdCounter = new AtomicLong(ThreadLocalRandom.current().nextLong() & Long.MAX_VALUE);
    /**
     * Unique id. Starts from 1
     */
    private static final AtomicInteger intIdCounter = new AtomicInteger(ThreadLocalRandom.current().nextInt() & Integer.MAX_VALUE);

    /**
     * Generate unique id of type long
     *
     * @return unique id
     */
    public static Long generateLongId() {
        return longIdCounter.incrementAndGet();
    }

    /**
     * Generate unique id of type int
     *
     * @return unique id
     */
    public static Integer generateIntId() {
        return intIdCounter.incrementAndGet();
    }
}
