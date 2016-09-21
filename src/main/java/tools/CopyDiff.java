package tools;

import filters.Filter;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;

public interface CopyDiff {
    void setSource(Path path);
    void setTarget(Path path);
    void setDiff(Path path);
    void doCopyDiff() throws IOException;
    void addFilter(Filter filter);
    void addFilters(Collection<Filter> filters);
}
