package idx2d.app;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/** Discovers the available demos by loading every {@link DemoProvider} on the classpath. */
public final class DemoRegistry {

    private DemoRegistry() {
    }

    public static List<Demo> load() {
        List<Demo> demos = new ArrayList<>();
        for (DemoProvider provider : ServiceLoader.load(DemoProvider.class)) {
            demos.addAll(provider.demos());
        }
        return List.copyOf(demos);
    }
}
