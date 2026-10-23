package GUI.auth;

import GUI.common.ThemeConfig;
import GUI.common.ValidationUtil;
import Service.IUserService;
import Service.impl.UserServiceImpl;
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

public class RegisterDialog extends JDialog {
    private final IUserService userService = new UserServiceImpl();

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPassword;
    private JTextField txtFullName;
    private JTextField txtPhone;
    private JTextField txtAddress;
    private JTextField txtEmail;

    public RegisterDialog(Frame parent) {
        super(parent, "Đăng ký tài khoản Khách hàng", true);
        initUI();
    }

    private void initUI() {
        setSize(460, 560);
        setResizable(false);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout());
        getContentPane().setBackground(ThemeConfig.BG_MAIN);

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ThemeConfig.SURFACE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeConfig.BORDER),
            BorderFactory.createEmptyBorder(20, 24, 16, 24)
        ));

        JLabel lblTitle = new JLabel("Tạo tài khoản mới");
        lblTitle.setFont(ThemeConfig.FONT_TITLE);
        lblTitle.setForeground(ThemeConfig.TEXT_MAIN);

        JLabel lblSubtitle = new JLabel("Điền thông tin để đăng ký làm thành viên");
        lblSubtitle.setFont(ThemeConfig.FONT_SMALL);
        lblSubtitle.setForeground(ThemeConfig.TEXT_MUTED);

        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblSubtitle, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(ThemeConfig.BG_MAIN);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 24, 10, 24));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 4, 6, 4);

        txtUsername = new JTextField();
        txtPassword = new JPasswordField();
        txtConfirmPassword = new JPasswordField();
        txtFullName = new JTextField();
        txtPhone = new JTextField();
        txtAddress = new JTextField();
        txtEmail = new JTextField();

        addFormField(formPanel, gbc, 0, "Tên đăng nhập (*):", txtUsername);
        addFormField(formPanel, gbc, 1, "Mật khẩu (*):", txtPassword);
        addFormField(formPanel, gbc, 2, "Xác nhận mật khẩu (*):", txtConfirmPassword);
        addFormField(formPanel, gbc, 3, "Họ và tên (*):", txtFullName);
        addFormField(formPanel, gbc, 4, "Số điện thoại (*):", txtPhone);
        addFormField(formPanel, gbc, 5, "Email (*):", txtEmail);
        addFormField(formPanel, gbc, 6, "Địa chỉ:", txtAddress);

        add(formPanel, BorderLayout.CENTER);

        // Bottom Action Panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 16));
        bottomPanel.setBackground(ThemeConfig.SURFACE);
        bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ThemeConfig.BORDER));

        JButton btnCancel = new JButton("Hủy bỏ");
        ThemeConfig.styleSecondaryButton(btnCancel);
        btnCancel.addActionListener(e -> dispose());

        JButton btnSubmit = new JButton("Đăng ký");
        ThemeConfig.stylePrimaryButton(btnSubmit);
        btnSubmit.addActionListener(e -> handleRegister());

        bottomPanel.add(btnCancel);
        bottomPanel.add(btnSubmit);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String label, JTextField field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.35;
        JLabel lbl = new JLabel(label);
        lbl.setFont(ThemeConfig.FONT_REGULAR);
        lbl.setForeground(ThemeConfig.TEXT_MAIN);
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        field.setPreferredSize(new Dimension(220, 36));
        panel.add(field, gbc);
    }

    private void handleRegister() {
        String username = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword());
        String confirmPass = new String(txtConfirmPassword.getPassword());
        String fullName = txtFullName.getText().trim();
        String phone = txtPhone.getText().trim();
        String email = txtEmail.getText().trim();
        String address = txtAddress.getText().trim();

        if (username.isEmpty() || pass.isEmpty() || fullName.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ các trường bắt buộc (*)", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!pass.equals(confirmPass)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không trùng khớp!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!ValidationUtil.isValidPhone(phone)) {
            JOptionPane.showMessageDialog(this, "Số điện thoại không hợp lệ (cần 10 số bắt đầu bằng 0)!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!ValidationUtil.isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Email không hợp lệ!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            userService.registerKhachHang(username, pass, fullName, phone, address, email);
            JOptionPane.showMessageDialog(this, "Đăng ký tài khoản thành công! Bạn có thể đăng nhập ngay bây giờ.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi đăng ký: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}

