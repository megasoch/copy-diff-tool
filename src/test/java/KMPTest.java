import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import utils.KMP;

/**
 * Created by megasoch on 12.09.2016.
 */
public class KMPTest extends Assert {
    private static byte[] data;
    private static byte[] emptyData;
    private static byte[] dataWithoutPattern;
    private static byte[] pattern;
    private static byte[] emptyPattern;

    @BeforeClass
    public static void initialize() {
        data = new byte[]{0, 1, 0, 0, 1, 1, 1, 0, 0, 0};
        emptyData = new byte[0];
        dataWithoutPattern = new byte[]{1, 0, 0, 0, 0, 1};
        pattern = new byte[]{0, 1, 1, 1};
        emptyPattern = new byte[0];
    }

    @Test
    public void filledDataFilledPatternContainsPattern() {
        assertTrue(KMP.containsSequence(data, pattern));
    }

    @Test
    public void filledDataFilledPatternWithoutPattern() {
        assertFalse(KMP.containsSequence(data, dataWithoutPattern));
    }

    @Test
    public void emptyDataFilledPattern() {
        assertFalse(KMP.containsSequence(emptyData, pattern));
    }

    @Test
    public void filledDataEmptyPattern() {
        assertTrue(KMP.containsSequence(data, emptyPattern));
    }

    @Test
    public void emptyDataEmptyPattern() {
        assertTrue(KMP.containsSequence(emptyData, emptyPattern));
    }
}
