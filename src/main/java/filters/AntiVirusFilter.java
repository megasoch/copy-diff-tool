package filters;

import utils.KMP;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by megasoch on 21.09.2016.
 */
public class AntiVirusFilter implements Filter {

    private byte[] virusSequence = new byte[0];

    @Override
    public boolean doFilter(Path verifiedPath) throws IOException {
        return !hasVirus(verifiedPath);
    }

    public void loadVirusSequence(byte[] virusSequence) {
        this.virusSequence = virusSequence;
    }

    private boolean hasVirus(Path verifiedPath) throws IOException {
        byte[] verifiedBytes = Files.readAllBytes(verifiedPath);
        return virusSequence.length != 0 && KMP.containsSequence(verifiedBytes, virusSequence);
    }
}
