package filters;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by megasoch on 21.09.2016.
 */
public interface Filter {
    boolean doFilter(Path verifiedPath) throws IOException;
}
