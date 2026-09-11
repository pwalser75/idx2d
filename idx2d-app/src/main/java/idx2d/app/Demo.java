package idx2d.app;

import java.util.function.Supplier;

/** A selectable demo: display metadata plus a factory for a fresh, ready-to-run view. */
public record Demo(String name, String description, Supplier<DemoView> factory) {
}
