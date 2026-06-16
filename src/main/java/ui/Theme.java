package ui;

import java.awt.Color;

public class Theme {

    public static boolean isDarkMode = false;

    // ── Shared UI Palette ────────────────────────────────────────────────────
    public static Color getBgPanel() {
        return isDarkMode ? new Color(18, 26, 40) : new Color(245, 247, 252);
    }

    public static Color getBgCard() {
        return isDarkMode ? new Color(28, 38, 55) : Color.WHITE;
    }

    public static Color getTxtPrimary() {
        return isDarkMode ? new Color(220, 230, 245) : new Color(30, 45, 70);
    }

    public static Color getTxtSecondary() {
        return isDarkMode ? new Color(150, 165, 190) : new Color(100, 115, 140);
    }

    public static Color getBorderColor() {
        return isDarkMode ? new Color(45, 58, 80) : new Color(215, 220, 235);
    }

    public static Color getTableSelectionBg() {
        return isDarkMode ? new Color(40, 75, 115) : new Color(210, 228, 255);
    }

    public static Color getTableSelectionFg() {
        return isDarkMode ? Color.WHITE : new Color(20, 40, 80);
    }

    public static Color getTableHeaderBg() {
        return isDarkMode ? new Color(28, 38, 55) : new Color(50, 70, 100);
    }

    public static Color getTableHeaderFg() {
        return isDarkMode ? new Color(100, 180, 255) : Color.WHITE;
    }

    public static Color getTableGrid() {
        return isDarkMode ? new Color(35, 48, 70) : new Color(225, 228, 238);
    }

    public static Color getAltRowColor() {
        return isDarkMode ? new Color(24, 34, 50) : new Color(247, 249, 253);
    }

    public static Color getFieldBg() {
        return isDarkMode ? new Color(35, 48, 70) : Color.WHITE;
    }
}
