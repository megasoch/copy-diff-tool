package tools;

import java.io.IOException;
import java.nio.file.Path;

public interface CopyDiff {
    void setSource(Path path);
    void setTarget(Path path);
    void setDiff(Path path);
    void doCopyDiff() throws IOException;
    void loadVirusSequence(byte[] virusSequence);
}
