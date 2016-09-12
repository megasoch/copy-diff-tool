import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import tools.CopyDiff;
import tools.CopyDiffImpl;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class CopyDiffTest extends Assert {
    private static CopyDiff copyDiff;
    private static Path fileWithoutVirus;
    private static Path fileWithVirus;
    private static Path source;
    private static Path target;
    private static Path diff;

    private static Path diffVirus;
    private static Path diffClean;
    private static Path diffEqual;
    private static Path diffAsFolderName;

    private static void createFileWithVirus() throws IOException {
        byte[] virus = new byte[]{0, 1, 0, 0, 1, 1, 1, 1, 1};
        Files.write(fileWithVirus, virus);
    }

    private static void createFileWithoutVirus() throws IOException {
        byte[] virus = new byte[]{0, 1, 0, 0, 0, 1, 1, 0, 1};
        Files.write(fileWithoutVirus, virus);
    }

    private static void cleanDiff() throws IOException {
        Files.walkFileTree(diff, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
        Files.createDirectory(diff);
    }

    @BeforeClass
    public static void initialize() throws IOException {
        copyDiff = new CopyDiffImpl();
        source = Paths.get("src/main/resources/source");
        target = Paths.get("src/main/resources/target");
        diff = Paths.get("src/main/resources/diff");
        cleanDiff();
        copyDiff.setSource(source);
        copyDiff.setTarget(target);
        copyDiff.setDiff(diff);

        copyDiff.loadVirusSequence(new byte[]{0, 1, 1, 1});

        fileWithVirus = Paths.get("src/main/resources/source/fileWithVirus");
        fileWithoutVirus = Paths.get("src/main/resources/source/fileWithoutVirus");
        createFileWithVirus();
        createFileWithoutVirus();

        copyDiff.doCopyDiff();

        diffVirus = Paths.get("src/main/resources/diff/fileWithVirus");
        diffClean = Paths.get("src/main/resources/diff/fileWithoutVirus");
        diffEqual = Paths.get("src/main/resources/diff/equalFile");
        diffAsFolderName = Paths.get("src/main/resources/diff/folder1/simpleFile");
    }

    @Test
    public void virusNotCopied() {
        assertFalse(Files.exists(diffVirus));
    }

    @Test
    public void cleanCopied() {
        assertTrue(Files.exists(diffClean));
    }

    @Test
    public void equalNotCopied() {
        assertFalse(Files.exists(diffEqual));
    }

    @Test
    public void fileWithFolderNameCopied() {
        assertTrue(Files.exists(diffAsFolderName));
    }
}
