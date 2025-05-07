package io.github.pheonixhkbxoic.adk;

import java.util.Collection;
import java.util.Locale;
import java.util.UUID;


/**
 * @author PheonixHkbxoic
 * @date 2025/5/2 18:59
 * @desc
 */
public class AdkUtil {

    public static boolean isEmpty(CharSequence c) {
        return c == null || c.isEmpty();
    }

    public static <T> boolean isEmpty(Collection<T> c) {
        return c == null || c.isEmpty();
    }

    public static String uuid4hex() {
        return UUID.randomUUID().toString().replaceAll("-", "").toLowerCase(Locale.ROOT);
    }


}
