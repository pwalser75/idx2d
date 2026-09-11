package idx2d.app.ui;

import idx2d.app.Demo;
import idx2d.app.DemoView;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;

/** Right-hand pane: demo header, centered canvas and status bar. */
public final class ContentPanel extends JPanel {

    private final JLabel titleLabel = new JLabel();
    private final JLabel descriptionLabel = new JLabel();
    private final JPanel canvasHost = new JPanel(new GridBagLayout());
    private final JLabel statusLabel = new JLabel(" ");
    private final JLabel metaLabel = new JLabel(" ");

    public ContentPanel() {
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND);
        add(buildHeader(), BorderLayout.NORTH);
        canvasHost.setBackground(Theme.BACKGROUND);
        add(canvasHost, BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);
    }

    public void showDemo(Demo demo, DemoView view) {
        titleLabel.setText(demo.name());
        descriptionLabel.setText(demo.description());
        statusLabel.setText("Running");
        metaLabel.setText(view.getPreferredSize().width + " \u00d7 " + view.getPreferredSize().height);

        JPanel frame = new JPanel(new BorderLayout());
        frame.setBackground(Theme.BACKGROUND);
        frame.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        frame.add(view, BorderLayout.CENTER);

        canvasHost.removeAll();
        canvasHost.add(frame);
        canvasHost.revalidate();
        canvasHost.repaint();
    }

    public void setStatus(String text) {
        statusLabel.setText(text);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER),
                BorderFactory.createEmptyBorder(22, 30, 18, 30)));

        titleLabel.setFont(Theme.sans(Font.BOLD, 26f));
        titleLabel.setForeground(Theme.TEXT);
        titleLabel.setAlignmentX(LEFT_ALIGNMENT);

        descriptionLabel.setFont(Theme.sans(Font.PLAIN, 13f));
        descriptionLabel.setForeground(Theme.TEXT_MUTED);
        descriptionLabel.setAlignmentX(LEFT_ALIGNMENT);

        header.add(titleLabel);
        header.add(Box.createVerticalStrut(7));
        header.add(descriptionLabel);
        return header;
    }

    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(Theme.SURFACE);
        bar.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.BORDER),
                BorderFactory.createEmptyBorder(8, 30, 8, 30)));

        statusLabel.setFont(Theme.mono(Font.PLAIN, 11f));
        statusLabel.setForeground(Theme.TEXT_MUTED);

        metaLabel.setFont(Theme.mono(Font.PLAIN, 11f));
        metaLabel.setForeground(Theme.TEXT_FAINT);

        bar.add(statusLabel, BorderLayout.WEST);
        bar.add(metaLabel, BorderLayout.EAST);
        return bar;
    }
}
