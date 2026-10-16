import GUI.Start;
import GUI.theme.UITheme;

public class Main {
    public static void main(String[] args) {
        UITheme.setupGlobalLaf();
        // Thiết lập giao diện chạy trên luồng sự kiện của Swing
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Start().setVisible(true);
            }
        });
    }
}
