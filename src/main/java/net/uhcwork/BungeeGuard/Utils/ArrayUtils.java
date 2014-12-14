package net.uhcwork.BungeeGuard.Utils;

import java.util.Arrays;
import java.util.Random;

public class ArrayUtils {
    private static final Random random = new Random();

    public static <T> T[] concat(T[] first, T[]... rest) {
        int totalLength = first.length;
        for (T[] array : rest) {
            totalLength += array.length;
        }
        T[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (T[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

    public static <T> T rand(T[] array) {
        if (array.length == 0)
            return null;
        return array[random.nextInt(array.length)];
    }
}
