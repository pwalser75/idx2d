package idx2d.app.ui;

import idx2d.app.Demo;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

/** Renders a sidebar demo entry with hover/selected states, an accent bar and an index badge. */
public final class DemoListCellRenderer extends JPanel implements ListCellRenderer<Demo> {

    private static final int DESCRIPTION_MAX = 44;

    private final JLabel nameLabel = new JLabel();
    private final JLabel descriptionLabel = new JLabel();
    private final JLabel indexLabel = new JLabel();

    private boolean selected;
    private boolean hover;
    private int hoverIndex = -1;

    public DemoListCellRenderer() {
        setOpaque(false);
        setLayout(new BorderLayout(12, 0));
        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 16));

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        nameLabel.setAlignmentX(LEFT_ALIGNMENT);
        descriptionLabel.setAlignmentX(LEFT_ALIGNMENT);
        text.add(nameLabel);
        text.add(descriptionLabel);

        indexLabel.setFont(Theme.mono(java.awt.Font.PLAIN, 11));
        indexLabel.setForeground(Theme.TEXT_FAINT);
        indexLabel.setHorizontalAlignment(JLabel.RIGHT);

        add(text, BorderLayout.CENTER);
        add(indexLabel, BorderLayout.EAST);
    }

    public boolean setHoverIndex(int index) {
        if (hoverIndex == index) return false;
        hoverIndex = index;
        return true;
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Demo> list, Demo value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        this.selected = isSelected;
        this.hover = index == hoverIndex;

        nameLabel.setText(value.name());
        nameLabel.setFont(Theme.sans(selected ? java.awt.Font.BOLD : java.awt.Font.PLAIN, 14.5f));
        nameLabel.setForeground(selected ? Theme.ACCENT : Theme.TEXT);

        descriptionLabel.setText(truncate(value.description()));
        descriptionLabel.setFont(Theme.sans(java.awt.Font.PLAIN, 11f));
        descriptionLabel.setForeground(selected ? Theme.TEXT_MUTED : Theme.TEXT_FAINT);

        indexLabel.setText(String.format("%02d", index + 1));
        return this;
    }

    private static String truncate(String text) {
        if (text.length() <= DESCRIPTION_MAX) return text;
        return text.substring(0, DESCRIPTION_MAX - 1).stripTrailing() + "\u2026";
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int inset = 6;
        int width = getWidth() - inset * 2;
        int height = getHeight() - 6;

        if (selected) {
            g2.setColor(Theme.SURFACE_RAISED);
            g2.fillRoundRect(inset, 3, width, height, 10, 10);
            g2.setColor(Theme.ACCENT);
            g2.fillRoundRect(inset, 10, 3, height - 14, 3, 3);
        } else if (hover) {
            g2.setColor(Theme.SURFACE_HOVER);
            g2.fillRoundRect(inset, 3, width, height, 10, 10);
        }
        g2.dispose();
    }
}
