package GUI.auth;

import GUI.common.AppNavigator;
import GUI.common.ImageUtil;
import GUI.common.ThemeConfig;
import GUI.main.MainDashboardFrame;
import Service.IAuthService;
import Service.dto.CaptchaDTO;
import Service.impl.AuthServiceImpl;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class LoginFrame extends JFrame {
    private final IAuthService authService = new AuthServiceImpl();
    private CaptchaDTO currentCaptcha;

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JTextField txtCaptcha;
    private JLabel lblCaptchaImage;
    private JLabel lblError;
    private JButton btnLogin;

    public LoginFrame() {
        setTitle("Đăng nhập - Hệ thống Quản lý Cửa hàng Quần áo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(860, 540);
        setMinimumSize(new Dimension(800, 500));
        setLocationRelativeTo(null);
        initUI();
        loadNewCaptcha();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(ThemeConfig.SURFACE);

        // LEFT COLUMN: BRAND HERO BANNER
        JPanel leftBanner = createLeftBanner();
        root.add(leftBanner, BorderLayout.WEST);

        // RIGHT COLUMN: LOGIN FORM
        JPanel rightPanel = createRightForm();
        root.add(rightPanel, BorderLayout.CENTER);

        setContentPane(root);
    }

    private JPanel createLeftBanner() {
        JPanel banner = new JPanel();
        banner.setPreferredSize(new Dimension(340, 540));
        banner.setBackground(ThemeConfig.SIDEBAR_BG);
        banner.setLayout(new BoxLayout(banner, BoxLayout.Y_AXIS));
        banner.setBorder(BorderFactory.createEmptyBorder(60, 36, 60, 36));

        JLabel lblBadge = new JLabel("★ THỜI TRANG CAO CẤP");
        lblBadge.setFont(ThemeConfig.FONT_SMALL_BOLD);
        lblBadge.setForeground(ThemeConfig.PRIMARY_LIGHT);
        lblBadge.setAlignmentX(0.0f);

        JLabel lblBrand = new JLabel("CLOTHING STORE");
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblBrand.setForeground(Color.WHITE);
        lblBrand.setAlignmentX(0.0f);

        JLabel lblSlogan = new JLabel("<html><b>Tôn vinh phong cách<br>Khẳng định cá tính</b></html>");
        lblSlogan.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        lblSlogan.setForeground(new Color(203, 213, 225));
        lblSlogan.setAlignmentX(0.0f);

        JLabel lblDesc = new JLabel("<html>Giải pháp quản lý bán hàng, theo dõi đơn hàng, quản lý kho thông minh và tối ưu vận hành.</html>");
        lblDesc.setFont(ThemeConfig.FONT_SMALL);
        lblDesc.setForeground(new Color(148, 163, 184));
        lblDesc.setAlignmentX(0.0f);

        banner.add(lblBadge);
        banner.add(Box.createVerticalStrut(12));
        banner.add(lblBrand);
        banner.add(Box.createVerticalStrut(24));
        banner.add(lblSlogan);
        banner.add(Box.createVerticalStrut(18));
        banner.add(lblDesc);
        banner.add(Box.createVerticalGlue());

        JLabel lblVersion = new JLabel("Phiên bản 2.0 • 2026");
        lblVersion.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblVersion.setForeground(new Color(100, 116, 139));
        lblVersion.setAlignmentX(0.0f);
        banner.add(lblVersion);

        return banner;
    }

    private JPanel createRightForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(ThemeConfig.SURFACE);
        form.setBorder(BorderFactory.createEmptyBorder(40, 48, 40, 48));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(4, 0, 4, 0);

        // Title
        JLabel lblLoginTitle = new JLabel("Đăng nhập hệ thống");
        lblLoginTitle.setFont(ThemeConfig.FONT_TITLE);
        lblLoginTitle.setForeground(ThemeConfig.TEXT_MAIN);
        gbc.gridy = 0;
        form.add(lblLoginTitle, gbc);

        JLabel lblLoginSub = new JLabel("Vui lòng điền thông tin đăng nhập của bạn");
        lblLoginSub.setFont(ThemeConfig.FONT_REGULAR);
        lblLoginSub.setForeground(ThemeConfig.TEXT_MUTED);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 14, 0);
        form.add(lblLoginSub, gbc);

        // Error message label
        lblError = new JLabel(" ");
        lblError.setFont(ThemeConfig.FONT_SMALL);
        lblError.setForeground(ThemeConfig.DANGER);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 8, 0);
        form.add(lblError, gbc);

        // Username
        JLabel lblUser = new JLabel("Tên đăng nhập");
        lblUser.setFont(ThemeConfig.FONT_BOLD);
        lblUser.setForeground(ThemeConfig.TEXT_MAIN);
        gbc.gridy = 3;
        gbc.insets = new Insets(4, 0, 4, 0);
        form.add(lblUser, gbc);

        txtUsername = new JTextField();
        txtUsername.setPreferredSize(new Dimension(300, 38));
        txtUsername.setFont(ThemeConfig.FONT_REGULAR);
        gbc.gridy = 4;
        form.add(txtUsername, gbc);

        // Password
        JLabel lblPass = new JLabel("Mật khẩu");
        lblPass.setFont(ThemeConfig.FONT_BOLD);
        lblPass.setForeground(ThemeConfig.TEXT_MAIN);
        gbc.gridy = 5;
        gbc.insets = new Insets(8, 0, 4, 0);
        form.add(lblPass, gbc);

        txtPassword = new JPasswordField();
        txtPassword.setPreferredSize(new Dimension(300, 38));
        txtPassword.setFont(ThemeConfig.FONT_REGULAR);
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 4, 0);
        form.add(txtPassword, gbc);

        // Toggle show password
        JCheckBox chkShowPass = new JCheckBox("Hiện mật khẩu");
        chkShowPass.setBackground(ThemeConfig.SURFACE);
        chkShowPass.setFont(ThemeConfig.FONT_SMALL);
        chkShowPass.setForeground(ThemeConfig.TEXT_MUTED);
        chkShowPass.addActionListener(e -> {
            if (chkShowPass.isSelected()) {
                txtPassword.setEchoChar((char) 0);
            } else {
                txtPassword.setEchoChar('•');
            }
        });
        gbc.gridy = 7;
        form.add(chkShowPass, gbc);

        // Captcha Section
        JLabel lblCapTitle = new JLabel("Mã xác thực (CAPTCHA)");
        lblCapTitle.setFont(ThemeConfig.FONT_BOLD);
        lblCapTitle.setForeground(ThemeConfig.TEXT_MAIN);
        gbc.gridy = 8;
        gbc.insets = new Insets(8, 0, 4, 0);
        form.add(lblCapTitle, gbc);

        JPanel captchaRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        captchaRow.setBackground(ThemeConfig.SURFACE);

        lblCaptchaImage = new JLabel();
        lblCaptchaImage.setPreferredSize(new Dimension(130, 38));
        lblCaptchaImage.setBorder(BorderFactory.createLineBorder(ThemeConfig.BORDER, 1));

        JButton btnRefreshCap = new JButton("↻");
        btnRefreshCap.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnRefreshCap.setToolTipText("Đổi mã bảo vệ khác");
        btnRefreshCap.setPreferredSize(new Dimension(42, 38));
        ThemeConfig.styleSecondaryButton(btnRefreshCap);
        btnRefreshCap.addActionListener(e -> loadNewCaptcha());

        txtCaptcha = new JTextField();
        txtCaptcha.setPreferredSize(new Dimension(110, 38));
        txtCaptcha.setFont(ThemeConfig.FONT_REGULAR);

        captchaRow.add(lblCaptchaImage);
        captchaRow.add(btnRefreshCap);
        captchaRow.add(txtCaptcha);

        gbc.gridy = 9;
        gbc.insets = new Insets(0, 0, 16, 0);
        form.add(captchaRow, gbc);

        // Submit Button
        btnLogin = new JButton("Đăng nhập");
        ThemeConfig.stylePrimaryButton(btnLogin);
        btnLogin.setPreferredSize(new Dimension(300, 42));
        btnLogin.addActionListener(e -> handleLogin());
        gbc.gridy = 10;
        gbc.insets = new Insets(4, 0, 12, 0);
        form.add(btnLogin, gbc);

        // Links Row (Forgot password & Register)
        JPanel linksRow = new JPanel(new BorderLayout());
        linksRow.setBackground(ThemeConfig.SURFACE);

        JLabel lblForgot = new JLabel("Quên mật khẩu?");
        lblForgot.setFont(ThemeConfig.FONT_SMALL);
        lblForgot.setForeground(ThemeConfig.PRIMARY);
        lblForgot.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblForgot.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                new ForgotPasswordDialog(LoginFrame.this).setVisible(true);
            }
        });

        JLabel lblRegister = new JLabel("Đăng ký tài khoản");
        lblRegister.setFont(ThemeConfig.FONT_SMALL_BOLD);
        lblRegister.setForeground(ThemeConfig.PRIMARY);
        lblRegister.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblRegister.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                new RegisterDialog(LoginFrame.this).setVisible(true);
            }
        });

        linksRow.add(lblForgot, BorderLayout.WEST);
        linksRow.add(lblRegister, BorderLayout.EAST);

        gbc.gridy = 11;
        gbc.insets = new Insets(8, 0, 0, 0);
        form.add(linksRow, gbc);

        // Key listeners for Enter key
        KeyAdapter enterKeyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleLogin();
                }
            }
        };
        txtUsername.addKeyListener(enterKeyAdapter);
        txtPassword.addKeyListener(enterKeyAdapter);
        txtCaptcha.addKeyListener(enterKeyAdapter);

        return form;
    }

    private void loadNewCaptcha() {
        currentCaptcha = authService.getRandomCaptcha();
        if (currentCaptcha != null) {
            if (currentCaptcha.getLinkAnh() != null && !currentCaptcha.getLinkAnh().isEmpty()) {
                lblCaptchaImage.setIcon(ImageUtil.getScaledImage(currentCaptcha.getLinkAnh(), 130, 38));
            } else {
                lblCaptchaImage.setIcon(ImageUtil.createCaptchaImage(currentCaptcha.getKetQua(), 130, 38));
            }
        }
        txtCaptcha.setText("");
    }

    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        String captcha = txtCaptcha.getText().trim();

        lblError.setText(" ");

        if (username.isEmpty() || password.isEmpty()) {
            lblError.setText("Vui lòng nhập tên đăng nhập và mật khẩu!");
            return;
        }

        if (captcha.isEmpty()) {
            lblError.setText("Vui lòng nhập mã bảo vệ CAPTCHA!");
            return;
        }

        try {
            authService.login(username, password, captcha, currentCaptcha);
            // Login successful
            AppNavigator.switchWindow(this, new MainDashboardFrame());
        } catch (Exception ex) {
            lblError.setText(ex.getMessage());
            loadNewCaptcha();
        }
    }
}

