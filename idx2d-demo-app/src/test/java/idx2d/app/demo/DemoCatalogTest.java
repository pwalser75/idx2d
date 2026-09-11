package idx2d.app.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import idx2d.app.Demo;
import idx2d.app.DemoRegistry;
import idx2d.app.DemoView;
import java.util.List;
import org.junit.jupiter.api.Test;

class DemoCatalogTest {

    @Test
    void serviceLoaderDiscoversTenDemos() {
        assertEquals(10, DemoRegistry.load().size());
    }

    @Test
    void everyDemoCanBeConstructedHeadlessly() {
        List<Demo> demos = DemoRegistry.load();
        for (Demo demo : demos) {
            DemoView view = demo.factory().get();
            assertNotNull(view, demo.name());
            assertTrue(view.getPreferredSize().width > 0, demo.name());
            assertTrue(view.getPreferredSize().height > 0, demo.name());
        }
    }
}
