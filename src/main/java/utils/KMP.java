package utils;

public class KMP {

    public static boolean containsSequence(byte[] data, byte[] pattern) {
        if (data.length == 0 && pattern.length != 0) {
            return  false;
        } else if (pattern.length == 0) {
            return true;
        }

        if (data.length != 0 && pattern.length == 0) {
            return true;
        }


        int[] failure = computeFailure(pattern);

        int j = 0;

        for (int i = 0; i < data.length; i++) {
            while (j > 0 && pattern[j] != data[i]) {
                j = failure[j - 1];
            }
            if (pattern[j] == data[i]) {
                j++;
            }
            if (j == pattern.length) {
                return true;
            }
        }
        return false;
    }

    private static int[] computeFailure(byte[] pattern) {
        int[] failure = new int[pattern.length];

        int j = 0;
        for (int i = 1; i < pattern.length; i++) {
            while (j > 0 && pattern[j] != pattern[i]) {
                j = failure[j - 1];
            }
            if (pattern[j] == pattern[i]) {
                j++;
            }
            failure[i] = j;
        }
        return failure;
    }
}
