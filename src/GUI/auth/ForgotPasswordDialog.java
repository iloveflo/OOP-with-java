package GUI.auth;

import GUI.common.ThemeConfig;
import Service.IOTPService;
import Service.impl.OTPServiceImpl;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class ForgotPasswordDialog extends JDialog {
    private final IOTPService otpService = new OTPServiceImpl();
    private String sentOTP = null;

    private JTextField txtUsername;
    private JButton btnSendOTP;
    private JTextField txtOTP;
    private JPasswordField txtNewPassword;
    private JPasswordField txtConfirmPassword;
    private JButton btnReset;

    public ForgotPasswordDialog(Frame parent) {
        super(parent, "Khôi phục mật khẩu", true);
        initUI();
    }

    private void initUI() {
        setSize(450, 480);
        setResizable(false);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout());
        getContentPane().setBackground(ThemeConfig.BG_MAIN);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ThemeConfig.SURFACE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeConfig.BORDER),
            BorderFactory.createEmptyBorder(20, 24, 16, 24)
        ));

        JLabel lblTitle = new JLabel("Quên mật khẩu?");
        lblTitle.setFont(ThemeConfig.FONT_TITLE);
        lblTitle.setForeground(ThemeConfig.TEXT_MAIN);

        JLabel lblSubtitle = new JLabel("Nhận mã OTP qua email đã đăng ký để đặt lại mật khẩu");
        lblSubtitle.setFont(ThemeConfig.FONT_SMALL);
        lblSubtitle.setForeground(ThemeConfig.TEXT_MUTED);

        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblSubtitle, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(ThemeConfig.BG_MAIN);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 24, 10, 24));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 4, 8, 4);

        // Username row
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        JLabel lblUser = new JLabel("Tên đăng nhập:");
        lblUser.setFont(ThemeConfig.FONT_REGULAR);
        formPanel.add(lblUser, gbc);

        gbc.gridx = 1; gbc.weightx = 0.5;
        txtUsername = new JTextField();
        txtUsername.setPreferredSize(new Dimension(160, 36));
        formPanel.add(txtUsername, gbc);

        gbc.gridx = 2; gbc.weightx = 0.2;
        btnSendOTP = new JButton("Gửi OTP");
        ThemeConfig.stylePrimaryButton(btnSendOTP);
        btnSendOTP.setPreferredSize(new Dimension(100, 36));
        btnSendOTP.addActionListener(e -> handleSendOTP());
        formPanel.add(btnSendOTP, gbc);

        // OTP row
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        JLabel lblOTP = new JLabel("Mã OTP (6 số):");
        lblOTP.setFont(ThemeConfig.FONT_REGULAR);
        formPanel.add(lblOTP, gbc);

        gbc.gridx = 1; gbc.gridwidth = 2; gbc.weightx = 0.7;
        txtOTP = new JTextField();
        txtOTP.setPreferredSize(new Dimension(200, 36));
        txtOTP.setEnabled(false);
        formPanel.add(txtOTP, gbc);
        gbc.gridwidth = 1;

        // New Password
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        JLabel lblNewPass = new JLabel("Mật khẩu mới:");
        lblNewPass.setFont(ThemeConfig.FONT_REGULAR);
        formPanel.add(lblNewPass, gbc);

        gbc.gridx = 1; gbc.gridwidth = 2; gbc.weightx = 0.7;
        txtNewPassword = new JPasswordField();
        txtNewPassword.setPreferredSize(new Dimension(200, 36));
        txtNewPassword.setEnabled(false);
        formPanel.add(txtNewPassword, gbc);
        gbc.gridwidth = 1;

        // Confirm Password
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3;
        JLabel lblConfirmPass = new JLabel("Xác nhận MK:");
        lblConfirmPass.setFont(ThemeConfig.FONT_REGULAR);
        formPanel.add(lblConfirmPass, gbc);

        gbc.gridx = 1; gbc.gridwidth = 2; gbc.weightx = 0.7;
        txtConfirmPassword = new JPasswordField();
        txtConfirmPassword.setPreferredSize(new Dimension(200, 36));
        txtConfirmPassword.setEnabled(false);
        formPanel.add(txtConfirmPassword, gbc);
        gbc.gridwidth = 1;

        add(formPanel, BorderLayout.CENTER);

        // Footer
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 16));
        bottomPanel.setBackground(ThemeConfig.SURFACE);
        bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ThemeConfig.BORDER));

        JButton btnCancel = new JButton("Đóng");
        ThemeConfig.styleSecondaryButton(btnCancel);
        btnCancel.addActionListener(e -> dispose());

        btnReset = new JButton("Đặt lại mật khẩu");
        ThemeConfig.stylePrimaryButton(btnReset);
        btnReset.setEnabled(false);
        btnReset.addActionListener(e -> handleResetPassword());

        bottomPanel.add(btnCancel);
        bottomPanel.add(btnReset);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void handleSendOTP() {
        String username = txtUsername.getText().trim();
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên đăng nhập!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        btnSendOTP.setEnabled(false);
        btnSendOTP.setText("Đang gửi...");

        new Thread(() -> {
            String otp = otpService.generateAndSendOTP(username);
            javax.swing.SwingUtilities.invokeLater(() -> {
                btnSendOTP.setEnabled(true);
                btnSendOTP.setText("Gửi lại");

                if (otp != null) {
                    sentOTP = otp;
                    txtOTP.setEnabled(true);
                    txtNewPassword.setEnabled(true);
                    txtConfirmPassword.setEnabled(true);
                    btnReset.setEnabled(true);
                    txtUsername.setEditable(false);
                    JOptionPane.showMessageDialog(this, "Mã OTP đã được gửi tới email liên kết với tài khoản!\nVui lòng kiểm tra hòm thư.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy email cho tài khoản hoặc không thể gửi email.\nVui lòng kiểm tra lại tên đăng nhập.", "Thất bại", JOptionPane.ERROR_MESSAGE);
                }
            });
        }).start();
    }

    private void handleResetPassword() {
        String otpInput = txtOTP.getText().trim();
        String newPass = new String(txtNewPassword.getPassword());
        String confirmPass = new String(txtConfirmPassword.getPassword());
        String username = txtUsername.getText().trim();

        if (otpInput.isEmpty() || newPass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã OTP và mật khẩu mới!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!newPass.equals(confirmPass)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean success = otpService.verifyOTPAndChangePassword(otpInput, sentOTP, username, newPass);
        if (success) {
            JOptionPane.showMessageDialog(this, "Đặt lại mật khẩu thành công! Bạn có thể dùng mật khẩu mới để đăng nhập.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Mã OTP không chính xác hoặc có lỗi xảy ra!", "Thất bại", JOptionPane.ERROR_MESSAGE);
        }
    }
}

