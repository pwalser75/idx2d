package idx2d.app;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/** Application entry point. */
public final class Idx2dApp {

    private Idx2dApp() {
    }

    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ignored) {
                // keep the default look and feel
            }
            new MainFrame().setVisible(true);
        });
    }
}
