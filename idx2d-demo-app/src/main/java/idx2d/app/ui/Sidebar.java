package idx2d.app.ui;

import idx2d.app.Demo;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;

/** Left-hand demo chooser. */
public final class Sidebar extends JPanel {

    private final JList<Demo> list;
    private final DemoListCellRenderer renderer = new DemoListCellRenderer();

    public Sidebar(List<Demo> demos, Consumer<Demo> onSelect) {
        setLayout(new BorderLayout());
        setBackground(Theme.SURFACE);
        setPreferredSize(new Dimension(Theme.SIDEBAR_WIDTH, 0));
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Theme.BORDER));

        add(new BrandPanel(), BorderLayout.NORTH);

        list = new JList<>(demos.toArray(new Demo[0]));
        list.setOpaque(false);
        list.setCellRenderer(renderer);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setFixedCellHeight(68);
        list.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
        installHoverTracking();
        installNavigation();
        list.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Demo selected = list.getSelectedValue();
                if (selected != null) {
                    onSelect.accept(selected);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        add(scroll, BorderLayout.CENTER);

        add(buildFooter(), BorderLayout.SOUTH);
    }

    public void selectFirst() {
        if (list.getModel().getSize() > 0) {
            list.setSelectedIndex(0);
            list.ensureIndexIsVisible(0);
        }
    }

    public void selectRelative(int delta) {
        if (list.getModel().getSize() == 0) return;
        int next = Math.max(0, Math.min(list.getModel().getSize() - 1, list.getSelectedIndex() + delta));
        list.setSelectedIndex(next);
        list.ensureIndexIsVisible(next);
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel();
        footer.setOpaque(false);
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.BORDER_SOFT),
                BorderFactory.createEmptyBorder(14, 20, 18, 20)));

        JLabel count = new JLabel("10 EFFECTS \u00b7 1 CANVAS");
        count.setFont(Theme.mono(Font.PLAIN, 10f));
        count.setForeground(Theme.ACCENT_DIM);
        count.setAlignmentX(LEFT_ALIGNMENT);

        JLabel credit = new JLabel("Original effects \u00a9 2001 Peter Walser");
        credit.setFont(Theme.sans(Font.PLAIN, 10f));
        credit.setForeground(Theme.TEXT_FAINT);
        credit.setAlignmentX(LEFT_ALIGNMENT);

        footer.add(count);
        footer.add(javax.swing.Box.createVerticalStrut(6));
        footer.add(credit);
        return footer;
    }

    private void installNavigation() {
        list.getInputMap(JComponent.WHEN_FOCUSED).put(javax.swing.KeyStroke.getKeyStroke("LEFT"), "demoPrevious");
        list.getInputMap(JComponent.WHEN_FOCUSED).put(javax.swing.KeyStroke.getKeyStroke("RIGHT"), "demoNext");
        list.getActionMap().put("demoPrevious", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                selectRelative(-1);
            }
        });
        list.getActionMap().put("demoNext", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                selectRelative(1);
            }
        });
    }

    private void installHoverTracking() {
        list.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int index = list.locationToIndex(e.getPoint());
                if (index >= 0 && !list.getCellBounds(index, index).contains(e.getPoint())) {
                    index = -1;
                }
                if (renderer.setHoverIndex(index)) {
                    list.repaint();
                }
            }
        });
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                if (renderer.setHoverIndex(-1)) {
                    list.repaint();
                }
            }
        });
    }
}
