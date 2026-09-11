package idx2d.app.ui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

/** The sidebar masthead: accent logo tile plus product wordmark. */
public final class BrandPanel extends JPanel {

    public BrandPanel() {
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(24, 20, 18, 20));
        setPreferredSize(new Dimension(Theme.SIDEBAR_WIDTH, 106));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int x = 20;
        int y = 22;

        g2.setColor(Theme.ACCENT);
        g2.fillRoundRect(x, y, 42, 42, 13, 13);
        g2.setColor(new java.awt.Color(0x06231F));
        g2.setFont(Theme.mono(Font.BOLD, 19f));
        g2.drawString("i2", x + 9, y + 29);

        g2.setColor(Theme.TEXT);
        g2.setFont(Theme.sans(Font.BOLD, 22f));
        g2.drawString("idx2d", x + 56, y + 20);

        g2.setColor(Theme.ACCENT);
        g2.setFont(Theme.mono(Font.PLAIN, 10f));
        g2.drawString("REAL-TIME EFFECTS", x + 57, y + 36);

        g2.setColor(Theme.TEXT_FAINT);
        g2.setFont(Theme.sans(Font.PLAIN, 11f));
        g2.drawString("Java 17 \u00b7 Swing edition", x + 57, y + 52);

        g2.dispose();
    }
}
