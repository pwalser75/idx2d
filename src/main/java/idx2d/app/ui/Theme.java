package idx2d.app.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/** Central dark "tech" theme: palette, typography and spacing used across the application. */
public final class Theme {

    private Theme() {
    }

    // ---- Palette ---------------------------------------------------------------------------------
    public static final Color BACKGROUND = new Color(0x0A0E13);
    public static final Color SURFACE = new Color(0x10161F);
    public static final Color SURFACE_RAISED = new Color(0x151D28);
    public static final Color SURFACE_HOVER = new Color(0x1A2430);
    public static final Color BORDER = new Color(0x1E2A38);
    public static final Color BORDER_SOFT = new Color(0x16202B);

    public static final Color ACCENT = new Color(0x2DD4BF);
    public static final Color ACCENT_DIM = new Color(0x1F9E92);
    public static final Color ACCENT_GLOW = new Color(45, 212, 191, 38);

    public static final Color TEXT = new Color(0xE7EEF5);
    public static final Color TEXT_MUTED = new Color(0x93A1B2);
    public static final Color TEXT_FAINT = new Color(0x5B6A7B);

    // ---- Spacing ---------------------------------------------------------------------------------
    public static final int GAP_XS = 4;
    public static final int GAP_SM = 8;
    public static final int GAP_MD = 16;
    public static final int GAP_LG = 24;
    public static final int GAP_XL = 36;

    public static final int SIDEBAR_WIDTH = 304;
    public static final int CORNER_RADIUS = 12;

    private static final String SANS = resolve(
            "Inter", "SF Pro Display", "SF Pro Text", "Segoe UI Variable", "Segoe UI",
            "Roboto", "Ubuntu", "Noto Sans", "DejaVu Sans", "Helvetica Neue", "SansSerif");

    private static final String MONO = resolve(
            "JetBrains Mono", "SF Mono", "Cascadia Code", "Cascadia Mono", "Fira Code",
            "Consolas", "DejaVu Sans Mono", "Liberation Mono", "Monospaced");

    public static Font sans(int style, float size) {
        return new Font(SANS, style, 12).deriveFont(style, size);
    }

    public static Font mono(int style, float size) {
        return new Font(MONO, style, 12).deriveFont(style, size);
    }

    private static String resolve(String... preferred) {
        try {
            Set<String> available = new HashSet<>(Arrays.asList(
                    GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
            for (String candidate : preferred) {
                if (available.contains(candidate)) {
                    return candidate;
                }
            }
        } catch (RuntimeException ignored) {
            // fall through to the logical font
        }
        return preferred[preferred.length - 1];
    }
}
