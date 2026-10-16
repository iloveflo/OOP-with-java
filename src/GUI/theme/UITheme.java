package GUI.theme;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class UITheme {

    // 2026 Enterprise Color Palette (Slate & Royal Indigo)
    public static final Color BG_MAIN = new Color(248, 250, 252);         // #F8FAFC
    public static final Color CARD_BG = new Color(255, 255, 255);         // #FFFFFF
    public static final Color SIDEBAR_BG = new Color(15, 23, 42);          // #0F172A
    public static final Color SIDEBAR_HOVER = new Color(30, 41, 59);       // #1E293B
    public static final Color SIDEBAR_ACTIVE = new Color(37, 99, 235);     // #2563EB
    public static final Color SIDEBAR_TEXT = new Color(226, 232, 240);     // #E2E8F0
    public static final Color SIDEBAR_MUTED = new Color(148, 163, 184);    // #94A3B8

    public static final Color PRIMARY = new Color(37, 99, 235);           // #2563EB
    public static final Color PRIMARY_HOVER = new Color(29, 78, 216);     // #1D4ED8
    public static final Color SUCCESS = new Color(16, 185, 129);          // #10B981
    public static final Color SUCCESS_HOVER = new Color(5, 150, 105);      // #059669
    public static final Color DANGER = new Color(239, 68, 68);            // #EF4444
    public static final Color DANGER_HOVER = new Color(220, 38, 38);       // #DC2626
    public static final Color WARNING = new Color(245, 158, 11);          // #F59E0B
    public static final Color SECONDARY = new Color(241, 245, 249);       // #F1F5F9
    public static final Color SECONDARY_HOVER = new Color(226, 232, 240); // #E2E8F0

    public static final Color TEXT_PRIMARY = new Color(15, 23, 42);       // #0F172A
    public static final Color TEXT_SECONDARY = new Color(71, 85, 105);    // #475569
    public static final Color TEXT_MUTED = new Color(148, 163, 184);      // #94A3B8
    public static final Color BORDER_COLOR = new Color(226, 232, 240);    // #E2E8F0
    public static final Color TABLE_ZEBRA = new Color(248, 250, 252);     // #F8FAFC
    public static final Color TABLE_SELECTION = new Color(224, 231, 255); // #E0E7FF

    // Typography
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);

    public static void setupGlobalLaf() {
        try {
            FlatLightLaf.setup();

            // Configure FlatLaf 2026 Enterprise Global Tokens
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8);
            UIManager.put("TextComponent.arc", 8);
            UIManager.put("ProgressBar.arc", 8);
            UIManager.put("ScrollBar.thumbArc", 999);
            UIManager.put("ScrollBar.width", 10);

            UIManager.put("Component.focusWidth", 1);
            UIManager.put("Component.innerFocusWidth", 0);
            UIManager.put("Component.borderColor", BORDER_COLOR);

            UIManager.put("Table.rowHeight", 38);
            UIManager.put("Table.showHorizontalLines", true);
            UIManager.put("Table.showVerticalLines", false);
            UIManager.put("Table.gridColor", new Color(241, 245, 249));
            UIManager.put("Table.selectionBackground", TABLE_SELECTION);
            UIManager.put("Table.selectionForeground", TEXT_PRIMARY);
            UIManager.put("Table.font", FONT_BODY);

            UIManager.put("TableHeader.height", 42);
            UIManager.put("TableHeader.background", new Color(241, 245, 249));
            UIManager.put("TableHeader.foreground", TEXT_PRIMARY);
            UIManager.put("TableHeader.font", FONT_BOLD);
            UIManager.put("TableHeader.separatorColor", new Color(226, 232, 240));

            UIManager.put("Panel.background", BG_MAIN);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void styleTable(JTable table) {
        table.setRowHeight(38);
        table.setFont(FONT_BODY);
        table.setShowGrid(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(241, 245, 249));
        table.setSelectionBackground(TABLE_SELECTION);
        table.setSelectionForeground(TEXT_PRIMARY);

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_BOLD);
        header.setBackground(new Color(241, 245, 249));
        header.setForeground(TEXT_PRIMARY);
        header.setPreferredSize(new Dimension(header.getWidth(), 42));

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean isSel, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, isSel, hasFocus, row, col);
                setBorder(new EmptyBorder(0, 12, 0, 12));
                if (!isSel) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : TABLE_ZEBRA);
                    c.setForeground(TEXT_PRIMARY);
                } else {
                    c.setBackground(TABLE_SELECTION);
                    c.setForeground(PRIMARY);
                }
                return c;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
    }

    public static void styleButton(JButton btn, Color bg, Color hoverBg, Color fg) {
        btn.setFont(FONT_BOLD);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        btn.putClientProperty(FlatClientProperties.STYLE, "arc: 8; borderWidth: 0;");

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled()) btn.setBackground(hoverBg);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (btn.isEnabled()) btn.setBackground(bg);
            }
        });
    }

    public static void stylePrimaryButton(JButton btn) {
        styleButton(btn, PRIMARY, PRIMARY_HOVER, Color.WHITE);
    }

    public static void styleSuccessButton(JButton btn) {
        styleButton(btn, SUCCESS, SUCCESS_HOVER, Color.WHITE);
    }

    public static void styleDangerButton(JButton btn) {
        styleButton(btn, DANGER, DANGER_HOVER, Color.WHITE);
    }

    public static void styleSecondaryButton(JButton btn) {
        styleButton(btn, SECONDARY, SECONDARY_HOVER, TEXT_PRIMARY);
    }

    public static void styleSidebarButton(JButton btn) {
        btn.setFont(FONT_SUBTITLE);
        btn.setBackground(SIDEBAR_BG);
        btn.setForeground(SIDEBAR_TEXT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        btn.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        btn.putClientProperty(FlatClientProperties.STYLE, "arc: 8;");

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled() && !Boolean.TRUE.equals(btn.getClientProperty("sidebar.active"))) {
                    btn.setBackground(SIDEBAR_HOVER);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (btn.isEnabled() && !Boolean.TRUE.equals(btn.getClientProperty("sidebar.active"))) {
                    btn.setBackground(SIDEBAR_BG);
                }
            }
        });
    }

    public static void setActiveSidebarButton(JButton activeBtn, JButton... allBtns) {
        for (JButton btn : allBtns) {
            if (btn == null) continue;
            if (btn == activeBtn) {
                btn.putClientProperty("sidebar.active", Boolean.TRUE);
                btn.setBackground(PRIMARY);
                btn.setForeground(Color.WHITE);
                btn.setFont(FONT_BOLD);
            } else {
                btn.putClientProperty("sidebar.active", Boolean.FALSE);
                btn.setBackground(SIDEBAR_BG);
                btn.setForeground(SIDEBAR_TEXT);
                btn.setFont(FONT_SUBTITLE);
            }
        }
    }

    public static JPanel createCard() {
        JPanel card = new JPanel();
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(16, 16, 16, 16)
        ));
        return card;
    }

    public static void styleContainer(Container container) {
        if (container == null) return;
        container.setBackground(BG_MAIN);
        for (Component c : container.getComponents()) {
            if (c instanceof JLabel) {
                JLabel lbl = (JLabel) c;
                if (lbl.getFont() != null && lbl.getFont().getSize() >= 20) {
                    lbl.setFont(FONT_TITLE);
                    lbl.setForeground(TEXT_PRIMARY);
                } else {
                    lbl.setFont(FONT_BOLD);
                    lbl.setForeground(TEXT_SECONDARY);
                }
            } else if (c instanceof JTable) {
                styleTable((JTable) c);
            } else if (c instanceof JScrollPane) {
                JScrollPane sp = (JScrollPane) c;
                sp.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
                if (sp.getViewport() != null && sp.getViewport().getView() instanceof JTable) {
                    styleTable((JTable) sp.getViewport().getView());
                }
            } else if (c instanceof JPanel) {
                c.setBackground(BG_MAIN);
                styleContainer((Container) c);
            }
        }
    }
}
