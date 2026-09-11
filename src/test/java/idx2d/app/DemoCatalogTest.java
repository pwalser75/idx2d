package idx2d.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DemoCatalogTest {

    @Test
    void catalogHasTenDemos() {
        assertEquals(10, DemoCatalog.all().size());
    }

    @Test
    void everyDemoCanBeConstructedHeadlessly() {
        for (Demo demo : DemoCatalog.all()) {
            DemoView view = demo.factory().get();
            assertNotNull(view, demo.name());
            assertTrue(view.getPreferredSize().width > 0, demo.name());
            assertTrue(view.getPreferredSize().height > 0, demo.name());
        }
    }
}
