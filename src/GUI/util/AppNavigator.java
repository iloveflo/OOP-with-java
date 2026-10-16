package GUI.util;

import javax.swing.JFrame;
import java.awt.Point;

public class AppNavigator {

    /**
     * Smoothly switches between top-level windows without causing screen flicker.
     * The target window inherits the position of the source window and renders
     * before the source window is disposed.
     */
    public static void switchWindow(JFrame from, JFrame to) {
        if (from != null) {
            Point p = from.getLocation();
            to.setLocation(p);
            to.setVisible(true);
            from.dispose();
        } else {
            to.setLocationRelativeTo(null);
            to.setVisible(true);
        }
    }
}

