package tools;

import exceptions.PathInitializationException;
import filters.Filter;
import utils.KMP;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;
import java.util.*;

public class CopyDiffImpl implements CopyDiff {
    private Path source;
    private Path target;
    private Path diff;

    private Set<Path> sourceFilesPaths = new TreeSet<>();
    private List<Filter> filters = new ArrayList<>();


    @Override
    public void setSource(Path source) {
        this.source = source;
    }

    @Override
    public void setTarget(Path target) {
        this.target = target;
    }

    @Override
    public void setDiff(Path diff) {
        this.diff = diff;
    }

    @Override
    public void doCopyDiff() throws IOException {
        try {
            if (incorrectInitialization()) {
                throw new PathInitializationException("One or more folder paths(source, target, diff) not correctly initialized");
            }
            fillPaths(source, source, sourceFilesPaths);
            for (Path path : sourceFilesPaths) {
                Path sourcePath = Paths.get(source.toString(), path.toString());
                Path targetPath = Paths.get(target.toString(), path.toString());
                Path diffPath = Paths.get(diff.toString(), path.toString());
                if (doFilter(sourcePath)) {
                    if (Files.notExists(targetPath) || Files.isDirectory(targetPath)) {
                        copyFile(sourcePath, diffPath);
                    } else if (!compareFiles(sourcePath, targetPath)) {
                        copyFile(sourcePath, diffPath);
                    }
                }
            }
        } catch (PathInitializationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addFilter(Filter filter) {
        this.filters.add(filter);
    }

    @Override
    public void addFilters(Collection<Filter> filters) {
        this.filters.addAll(filters);
    }

    private boolean compareFiles(Path sourcePath, Path targetPath) throws IOException {
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

    private boolean binaryCompare(Path sourcePath, Path targetPath) throws IOException {
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

    //Returns true if file acceptable
    private boolean doFilter(Path filteredPath) throws IOException {
        boolean verified = true;
        for (Filter f: filters) {
            verified = f.doFilter(filteredPath) && verified;
        }
        return verified;
    }


    private void fillPaths(Path path, Path relative, Collection filled) throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            for (Path entry : stream) {
                if (Files.isDirectory(entry)) {
                    fillPaths(entry, relative, filled);
                } else {
                    filled.add(relative.relativize(entry));
                }
            }
        }
    }

    private void copyFile(Path sourcePath, Path diffPath) throws IOException {
        Path diffParent = diffPath.getParent();
        if (diffParent != null) {
            Files.createDirectories(diffParent);
        }
        Files.copy(sourcePath, diffPath, StandardCopyOption.REPLACE_EXISTING);
    }

    private boolean incorrectInitialization() {
        return source == null ||
                target == null ||
                diff == null ||
                !Files.isDirectory(source) ||
                !Files.isDirectory(target) ||
                !Files.isDirectory(diff);
    }
}
