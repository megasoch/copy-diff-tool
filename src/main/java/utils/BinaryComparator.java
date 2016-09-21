package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Arrays;

/**
 * Created by megasoch on 21.09.2016.
 */
public class BinaryComparator {

    public static boolean compareFiles(Path sourcePath, Path targetPath) throws IOException {
        FileTime sourceTime = Files.getLastModifiedTime(sourcePath);
        FileTime targetTime = Files.getLastModifiedTime(targetPath);
        if (Files.size(sourcePath) == Files.size(targetPath)) {
            if (sourceTime.equals(targetTime)) {
                return true;
            }
            return binaryCompare(sourcePath, targetPath);
        }
        return false;
    }

    private static boolean binaryCompare(Path sourcePath, Path targetPath) throws IOException {
        final int BLOCK_SIZE = 128;
        InputStream sourceStream = new FileInputStream(sourcePath.toFile());
        InputStream targetStream = new FileInputStream(targetPath.toFile());
        byte[] sourceBuffer = new byte[BLOCK_SIZE];
        byte[] targetBuffer = new byte[BLOCK_SIZE];
        while (true) {
            int sourceByteCount = sourceStream.read(sourceBuffer, 0, BLOCK_SIZE);
            targetStream.read(targetBuffer, 0, BLOCK_SIZE);
            if (sourceByteCount < 0) {
                return true;
            }
            if (!Arrays.equals(sourceBuffer, targetBuffer)) {
                return false;
            }
        }
    }
}
