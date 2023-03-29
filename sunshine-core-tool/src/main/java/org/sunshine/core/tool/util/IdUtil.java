package org.sunshine.core.tool.util;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Teamo
 * @since 2023/2/7
 */
public class IdUtil {

    public static UUID fastUUID() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return new UUID(random.nextLong(), random.nextLong());
    }
}
