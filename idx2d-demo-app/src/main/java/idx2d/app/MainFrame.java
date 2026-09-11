package idx2d.app;

import idx2d.app.ui.ContentPanel;
import idx2d.app.ui.Sidebar;
import idx2d.app.ui.Theme;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

/** Main application window: demo chooser on the left, running demo on the right. */
public final class MainFrame extends JFrame {

    private final List<Demo> demos = DemoRegistry.load();
    private final Sidebar sidebar;
    private final ContentPanel content = new ContentPanel();

    private DemoView current;
    private Demo currentDemo;

    public MainFrame() {
        super("idx2d \u2014 Effects Lab");
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setMinimumSize(new Dimension(980, 660));

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BACKGROUND);
        sidebar = new Sidebar(demos, this::select);
        root.add(sidebar, BorderLayout.WEST);
        root.add(content, BorderLayout.CENTER);
        setContentPane(root);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                shutdown();
            }
        });
        installShortcuts();

        setSize(1220, 820);
        setLocationRelativeTo(null);
        sidebar.selectFirst();
    }

    private void select(Demo demo) {
        if (current != null && currentDemo == demo) {
            return;
        }
        if (current != null) {
            current.stop();
            current = null;
        }
        DemoView view = demo.factory().get();
        view.setStatusSink(content::setStatus);
        content.showDemo(demo, view);
        current = view;
        currentDemo = demo;
        view.start();
    }

    private void installShortcuts() {
        bind(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), -1);
        bind(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), 1);
    }

    private void bind(KeyStroke stroke, int delta) {
        JComponent root = getRootPane();
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, "move" + delta);
        root.getActionMap().put("move" + delta, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sidebar.selectRelative(delta);
            }
        });
    }

    private void shutdown() {
        if (current != null) {
            current.stop();
        }
        dispose();
        System.exit(0);
    }
}
