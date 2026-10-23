import GUI.auth.LoginFrame;
import GUI.common.ThemeConfig;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        // Khởi tạo FlatLaf Look and Feel ngay dòng đầu tiên
        FlatLightLaf.setup();
        UIManager.put("Button.arc", 8);
        UIManager.put("Component.arc", 8);
        UIManager.put("TextComponent.arc", 8);

        // Thiết lập cấu hình giao diện & typography
        ThemeConfig.setup();

        // Chạy giao diện trên Event Dispatch Thread của Swing
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
