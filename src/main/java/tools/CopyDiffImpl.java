package tools;

import exceptions.PathInitializationException;
import filters.Filter;
import org.apache.log4j.Logger;
import utils.BinaryComparator;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class CopyDiffImpl implements CopyDiff {
    final static Logger log = Logger.getLogger(CopyDiffImpl.class);

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
        log.info("Copy diff process started!");
        try {
            checkInitialization();
            fillPaths(source, source, sourceFilesPaths);
            log.info("Source paths of files has been founded");
            for (Path path : sourceFilesPaths) {
                Path sourcePath = Paths.get(source.toString(), path.toString());
                Path targetPath = Paths.get(target.toString(), path.toString());
                Path diffPath = Paths.get(diff.toString(), path.toString());
                if (doFilter(sourcePath)) {
                    log.info("FILTER PASSED: " + sourcePath.toString());
                    if (Files.notExists(targetPath) || Files.isDirectory(targetPath) || !BinaryComparator.compareFiles(sourcePath, targetPath)) {
                        copyFile(sourcePath, diffPath);
                    } else {
                        log.info("File " + sourcePath.toString() + " NOT COPIED");
                    }
                } else {
                    log.info("FILTER NOT PASSED: " + sourcePath.toString());
                }
            }
        } catch (PathInitializationException e) {
            log.error("Path initialization failed(source/target/diff == null or not folder)");
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

    //Returns true if file acceptable
    private boolean doFilter(Path filteredPath) throws IOException {
        boolean verified = true;
        for (Filter f : filters) {
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
        log.info("File: " + sourcePath.toString() + " COPIED IN " + diffParent.toString());
    }

    private void checkInitialization() throws PathInitializationException {
        if (source == null ||
            target == null ||
            diff == null ||
            !Files.isDirectory(source) ||
            !Files.isDirectory(target) ||
            !Files.isDirectory(diff)) {
            throw new PathInitializationException();
        }
    }
}
