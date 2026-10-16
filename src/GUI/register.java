package GUI;

import javax.swing.*;
import java.awt.*;

public final class register extends javax.swing.JFrame {

    private JTextField txtFullName;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPassword;
    private JTextField txtEmail;
    private JTextField txtPhone;
    private JTextField txtAddress;
    private JTextField txtAccountCode;
    private JTextField txtCaptcha;
    private JButton btnReloadCaptcha;
    private JButton btnRegister;
    private JButton btnBack;
    private JCheckBox chkShowPassword;
    private JCheckBox chkShowConfirmPassword;
    private JLabel lblTitle;
    private JLabel lblCaptchaImage;

    private final Service.IUserService userService = new Service.impl.UserServiceImpl();
    private final Service.IAuthService authService = new Service.impl.AuthServiceImpl();
    private Service.dto.CaptchaDTO currentCaptcha;

    public register() {
        initComponents();
        loadCaptcha();
        btnReloadCaptcha.addActionListener(e -> loadCaptcha());
        btnRegister.addActionListener(e -> handleRegister());
        getRootPane().setDefaultButton(btnRegister);
        btnBack.addActionListener(e -> {
            GUI.util.AppNavigator.switchWindow(this, new Start());
        });
        chkShowPassword.addActionListener(e -> {
            txtPassword.setEchoChar(chkShowPassword.isSelected() ? (char) 0 : '\u2022');
        });
        chkShowConfirmPassword.addActionListener(e -> {
            txtConfirmPassword.setEchoChar(chkShowConfirmPassword.isSelected() ? (char) 0 : '\u2022');
        });
    }

    private void loadCaptcha() {
        currentCaptcha = authService.getRandomCaptcha();
        if (currentCaptcha != null && currentCaptcha.getLinkAnh() != null) {
            Image img = new ImageIcon(currentCaptcha.getLinkAnh()).getImage().getScaledInstance(150, 50, Image.SCALE_SMOOTH);
            lblCaptchaImage.setIcon(new ImageIcon(img));
        }
    }

    private void handleRegister() {
        try {
            String fullName = txtFullName.getText().trim();
            String username = txtUsername.getText().trim();
            String pass = String.valueOf(txtPassword.getPassword());
            String confirmPass = String.valueOf(txtConfirmPassword.getPassword());
            String email = txtEmail.getText().trim();
            String phone = txtPhone.getText().trim();
            String address = txtAddress.getText().trim();
            String captcha = txtCaptcha.getText().trim();

            if (fullName.isEmpty() || username.isEmpty() || pass.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!pass.equals(confirmPass)) {
                JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (currentCaptcha == null || !currentCaptcha.validate(captcha)) {
                JOptionPane.showMessageDialog(this, "Mã bảo vệ không chính xác!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                loadCaptcha();
                return;
            }

            userService.registerKhachHang(username, pass, fullName, phone, address, email);
            JOptionPane.showMessageDialog(this, "Đăng ký tài khoản thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            GUI.util.AppNavigator.switchWindow(this, new Start());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi đăng ký", JOptionPane.ERROR_MESSAGE);
            loadCaptcha();
        }
    }

    private void initComponents() {
        JPanel panelMain = new JPanel();
        panelMain.setLayout(null);

        lblTitle = new JLabel("Đăng ký");
        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 24));
        lblTitle.setBounds(220, 10, 150, 30);
        panelMain.add(lblTitle);

        btnBack = new JButton("Quay lại");
        btnBack.setBounds(10, 10, 90, 30);
        panelMain.add(btnBack);

        JLabel lblFullName = new JLabel("Tên đầy đủ:");
        lblFullName.setBounds(50, 60, 100, 25);
        panelMain.add(lblFullName);

        txtFullName = new JTextField();
        txtFullName.setBounds(180, 60, 250, 25);
        panelMain.add(txtFullName);

        JLabel lblUsername = new JLabel("Tên đăng nhập:");
        lblUsername.setBounds(50, 100, 100, 25);
        panelMain.add(lblUsername);

        txtUsername = new JTextField();
        txtUsername.setBounds(180, 100, 250, 25);
        panelMain.add(txtUsername);

        JLabel lblPassword = new JLabel("Mật khẩu:");
        lblPassword.setBounds(50, 140, 100, 25);
        panelMain.add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(180, 140, 250, 25);
        panelMain.add(txtPassword);

        chkShowPassword = new JCheckBox("Hiển thị mật khẩu");
        chkShowPassword.setBounds(440, 140, 150, 25);
        panelMain.add(chkShowPassword);

        JLabel lblConfirmPassword = new JLabel("Nhập lại mật khẩu:");
        lblConfirmPassword.setBounds(50, 180, 130, 25);
        panelMain.add(lblConfirmPassword);

        txtConfirmPassword = new JPasswordField();
        txtConfirmPassword.setBounds(180, 180, 250, 25);
        panelMain.add(txtConfirmPassword);

        chkShowConfirmPassword = new JCheckBox("Hiển thị mật khẩu");
        chkShowConfirmPassword.setBounds(440, 180, 150, 25);
        panelMain.add(chkShowConfirmPassword);

        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setBounds(50, 220, 100, 25);
        panelMain.add(lblEmail);

        txtEmail = new JTextField();
        txtEmail.setBounds(180, 220, 250, 25);
        panelMain.add(txtEmail);

        JLabel lblPhone = new JLabel("Số điện thoại:");
        lblPhone.setBounds(50, 260, 100, 25);
        panelMain.add(lblPhone);

        txtPhone = new JTextField();
        txtPhone.setBounds(180, 260, 250, 25);
        panelMain.add(txtPhone);

        JLabel lblAddress = new JLabel("Địa chỉ:");
        lblAddress.setBounds(50, 300, 100, 25);
        panelMain.add(lblAddress);

        txtAddress = new JTextField();
        txtAddress.setBounds(180, 300, 250, 25);
        panelMain.add(txtAddress);

        JLabel lblAccountCode = new JLabel("Tạo mã tài khoản:");
        lblAccountCode.setBounds(50, 340, 100, 25);
        panelMain.add(lblAccountCode);

        txtAccountCode = new JTextField();
        txtAccountCode.setBounds(180, 340, 250, 25);
        panelMain.add(txtAccountCode);

        JLabel lblCaptcha = new JLabel("Captcha:");
        lblCaptcha.setBounds(50, 380, 100, 25);
        panelMain.add(lblCaptcha);

        txtCaptcha = new JTextField();
        txtCaptcha.setBounds(180, 380, 150, 25);
        panelMain.add(txtCaptcha);

        // Ảnh captcha
        lblCaptchaImage = new JLabel();
        lblCaptchaImage.setBounds(340, 370, 120, 50);
        lblCaptchaImage.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panelMain.add(lblCaptchaImage);

        btnReloadCaptcha = new JButton("Đổi captcha");
        btnReloadCaptcha.setBounds(470, 380, 100, 25);
        panelMain.add(btnReloadCaptcha);

        btnRegister = new JButton("Đăng ký");
        btnRegister.setBounds(200, 440, 120, 40);
        panelMain.add(btnRegister);
        panelMain.setBackground(GUI.theme.UITheme.BG_MAIN);
        lblTitle.setFont(GUI.theme.UITheme.FONT_TITLE);
        lblTitle.setForeground(GUI.theme.UITheme.TEXT_PRIMARY);
        lblCaptchaImage.setBorder(BorderFactory.createLineBorder(GUI.theme.UITheme.BORDER_COLOR, 1, true));
        GUI.theme.UITheme.stylePrimaryButton(btnRegister);
        GUI.theme.UITheme.styleSecondaryButton(btnReloadCaptcha);
        GUI.theme.UITheme.styleSecondaryButton(btnBack);

        this.add(panelMain);
        this.setSize(600, 550);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Đăng ký tài khoản");
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new register().setVisible(true));
    }
}
