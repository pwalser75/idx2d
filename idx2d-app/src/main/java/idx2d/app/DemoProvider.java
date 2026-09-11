package idx2d.app;

import java.util.List;

/**
 * Service provider interface implemented by demo modules. Implementations are discovered at runtime
 * through {@link java.util.ServiceLoader} (see {@link DemoRegistry}).
 */
public interface DemoProvider {

    List<Demo> demos();
}
