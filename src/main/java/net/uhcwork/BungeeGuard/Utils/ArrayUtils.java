package net.uhcwork.BungeeGuard.Utils;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;

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

    public static Number[] quartiles(int[] values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("The data array either is null or does not contain any data.");
        }
        Arrays.sort(values);

        int n = Math.round(values.length * 25 / 100);

        return new Number[]{values[n], values[2 * n], values[3 * n]};

    }

    public static double sum(double[] values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("The data array either is null or does not contain any data.");
        }
        double sum = 0;
        for (double value : values)
            sum = sum + value;
        return sum;
    }

    public static double average(double[] values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("The data array either is null or does not contain any data.");
        }
        return sum(values) / values.length;
    }

    public static double average(int[] values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("The data array either is null or does not contain any data.");
        }
        return sum(Doubles.toArray(Ints.asList(values))) / values.length;
    }

    public static int[] toPrimitive(Integer[] values) {
        int[] result = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            result[i] = values[i];
        }
        return result;
    }
}
