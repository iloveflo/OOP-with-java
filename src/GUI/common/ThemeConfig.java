package GUI.common;

import com.formdev.flatlaf.FlatLightLaf;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.UIManager;
import javax.swing.border.Border;

public class ThemeConfig {
    // Color Palette
    public static final Color PRIMARY = new Color(37, 99, 235);        // #2563EB
    public static final Color PRIMARY_DARK = new Color(29, 78, 216);   // #1D4ED8
    public static final Color PRIMARY_LIGHT = new Color(239, 246, 255); // #EFF6FF
    public static final Color SECONDARY = new Color(100, 116, 139);    // #64748B
    public static final Color SUCCESS = new Color(16, 185, 129);       // #10B981
    public static final Color WARNING = new Color(245, 158, 11);       // #F59E0B
    public static final Color DANGER = new Color(239, 68, 68);         // #EF4444
    public static final Color BG_MAIN = new Color(248, 250, 252);      // #F8FAFC
    public static final Color SURFACE = Color.WHITE;
    public static final Color BORDER = new Color(226, 232, 240);       // #E2E8F0
    public static final Color TEXT_MAIN = new Color(15, 23, 42);       // #0F172A
    public static final Color TEXT_MUTED = new Color(100, 116, 139);   // #64748B
    public static final Color SIDEBAR_BG = new Color(15, 23, 42);      // #0F172A
    public static final Color SIDEBAR_HOVER = new Color(30, 41, 59);   // #1E293B
    public static final Color SIDEBAR_ACTIVE = new Color(37, 99, 235); // #2563EB

    // Typography
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 17);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_SMALL_BOLD = new Font("Segoe UI", Font.BOLD, 12);

    public static void setup() {
        FlatLightLaf.setup();

        UIManager.put("Button.arc", 8);
        UIManager.put("Component.arc", 8);
        UIManager.put("TextComponent.arc", 8);
        UIManager.put("ScrollBar.thumbArc", 8);
        UIManager.put("ScrollBar.width", 10);
        UIManager.put("Table.rowHeight", 38);
        UIManager.put("Table.showHorizontalLines", true);
        UIManager.put("Table.showVerticalLines", false);
        UIManager.put("Table.gridColor", BORDER);
        UIManager.put("Table.intercellSpacing", new Dimension(0, 1));
        UIManager.put("TableHeader.height", 40);
        UIManager.put("TableHeader.font", FONT_HEADER);
        UIManager.put("defaultFont", FONT_REGULAR);
    }

    public static Border createCardBorder() {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            BorderFactory.createEmptyBorder(16, 16, 16, 16)
        );
    }

    public static void stylePrimaryButton(JButton btn) {
        btn.setFont(FONT_BOLD);
        btn.setBackground(PRIMARY);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    }

    public static void styleSecondaryButton(JButton btn) {
        btn.setFont(FONT_REGULAR);
        btn.setBackground(Color.WHITE);
        btn.setForeground(TEXT_MAIN);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            BorderFactory.createEmptyBorder(9, 19, 9, 19)
        ));
    }

    public static void styleDangerButton(JButton btn) {
        btn.setFont(FONT_BOLD);
        btn.setBackground(DANGER);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    }
}

