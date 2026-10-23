package GUI.common;

import java.awt.Window;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;

public class AppNavigator {

    public static void switchWindow(Window currentWindow, Window nextWindow) {
        SwingUtilities.invokeLater(() -> {
            if (currentWindow != null) {
                currentWindow.dispose();
            }
            if (nextWindow != null) {
                nextWindow.setLocationRelativeTo(null);
                nextWindow.setVisible(true);
            }
        });
    }

    public static void openDialog(JDialog dialog) {
        SwingUtilities.invokeLater(() -> {
            dialog.setLocationRelativeTo(dialog.getOwner());
            dialog.setVisible(true);
        });
    }
}

