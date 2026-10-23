package GUI.panels;

import GUI.common.ThemeConfig;
import GUI.common.ValidationUtil;
import Model.KhachHang;
import Model.LoaiTaiKhoan;
import Model.Nguoi;
import Model.NhanVien;
import Model.TaiKhoan;
import Service.IUserService;
import Service.UserSession;
import Service.impl.UserServiceImpl;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Optional;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class ProfilePanel extends JPanel {
    private final IUserService userService = new UserServiceImpl();

    // Profile fields
    private JTextField txtUsername;
    private JTextField txtFullName;
    private JTextField txtPhone;
    private JTextField txtEmail;
    private JTextField txtAddress;
    private JButton btnEditToggle;
    private JButton btnSaveProfile;
    private boolean isEditing = false;

    // Password fields
    private JPasswordField txtOldPass;
    private JPasswordField txtNewPass;
    private JPasswordField txtConfirmPass;

    public ProfilePanel() {
        setLayout(new BorderLayout());
        setBackground(ThemeConfig.BG_MAIN);
        initUI();
        loadProfileData();
    }

    private void initUI() {
        JPanel container = new JPanel(new GridBagLayout());
        container.setBackground(ThemeConfig.BG_MAIN);

        // Center card panel
        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(620, 680));
        card.setMaximumSize(new Dimension(620, 720));
        card.setBackground(ThemeConfig.SURFACE);
        card.setBorder(ThemeConfig.createCardBorder());
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        // Title
        JLabel lblTitle = new JLabel("Hồ sơ tài khoản");
        lblTitle.setFont(ThemeConfig.FONT_TITLE);
        lblTitle.setForeground(ThemeConfig.TEXT_MAIN);
        lblTitle.setAlignmentX(0.0f);

        JLabel lblSub = new JLabel("Quản lý thông tin cá nhân và bảo mật tài khoản");
        lblSub.setFont(ThemeConfig.FONT_SMALL);
        lblSub.setForeground(ThemeConfig.TEXT_MUTED);
        lblSub.setAlignmentX(0.0f);

        card.add(lblTitle);
        card.add(Box.createVerticalStrut(4));
        card.add(lblSub);
        card.add(Box.createVerticalStrut(18));

        // Profile Form
        JPanel formProfile = new JPanel(new GridBagLayout());
        formProfile.setBackground(ThemeConfig.SURFACE);
        formProfile.setAlignmentX(0.0f);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 4, 6, 4);

        txtUsername = new JTextField();
        txtUsername.setEditable(false);
        txtFullName = new JTextField();
        txtFullName.setEditable(false);
        txtPhone = new JTextField();
        txtPhone.setEditable(false);
        txtEmail = new JTextField();
        txtEmail.setEditable(false);
        txtAddress = new JTextField();
        txtAddress.setEditable(false);

        addFormField(formProfile, gbc, 0, "Tên đăng nhập:", txtUsername);
        addFormField(formProfile, gbc, 1, "Họ và tên:", txtFullName);
        addFormField(formProfile, gbc, 2, "Số điện thoại:", txtPhone);
        addFormField(formProfile, gbc, 3, "Email:", txtEmail);
        addFormField(formProfile, gbc, 4, "Địa chỉ:", txtAddress);

        card.add(formProfile);
        card.add(Box.createVerticalStrut(12));

        // Profile Action buttons
        JPanel btnProfilePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnProfilePanel.setBackground(ThemeConfig.SURFACE);
        btnProfilePanel.setAlignmentX(0.0f);

        btnEditToggle = new JButton("Chỉnh sửa");
        ThemeConfig.styleSecondaryButton(btnEditToggle);
        btnEditToggle.addActionListener(e -> toggleEditMode());

        btnSaveProfile = new JButton("Lưu thay đổi");
        ThemeConfig.stylePrimaryButton(btnSaveProfile);
        btnSaveProfile.setEnabled(false);
        btnSaveProfile.addActionListener(e -> saveProfile());

        btnProfilePanel.add(btnEditToggle);
        btnProfilePanel.add(btnSaveProfile);
        card.add(btnProfilePanel);

        // Divider
        card.add(Box.createVerticalStrut(20));
        JPanel divider = new JPanel();
        divider.setMaximumSize(new Dimension(620, 1));
        divider.setBackground(ThemeConfig.BORDER);
        divider.setAlignmentX(0.0f);
        card.add(divider);
        card.add(Box.createVerticalStrut(18));

        // Password change section
        JLabel lblPassTitle = new JLabel("Đổi mật khẩu");
        lblPassTitle.setFont(ThemeConfig.FONT_HEADER);
        lblPassTitle.setForeground(ThemeConfig.TEXT_MAIN);
        lblPassTitle.setAlignmentX(0.0f);
        card.add(lblPassTitle);
        card.add(Box.createVerticalStrut(10));

        JPanel formPass = new JPanel(new GridBagLayout());
        formPass.setBackground(ThemeConfig.SURFACE);
        formPass.setAlignmentX(0.0f);

        txtOldPass = new JPasswordField();
        txtNewPass = new JPasswordField();
        txtConfirmPass = new JPasswordField();

        addFormField(formPass, gbc, 0, "Mật khẩu hiện tại:", txtOldPass);
        addFormField(formPass, gbc, 1, "Mật khẩu mới:", txtNewPass);
        addFormField(formPass, gbc, 2, "Xác nhận MK mới:", txtConfirmPass);

        card.add(formPass);
        card.add(Box.createVerticalStrut(12));

        JPanel btnPassPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPassPanel.setBackground(ThemeConfig.SURFACE);
        btnPassPanel.setAlignmentX(0.0f);

        JButton btnChangePass = new JButton("Cập nhật mật khẩu");
        ThemeConfig.stylePrimaryButton(btnChangePass);
        btnChangePass.addActionListener(e -> changePassword());

        btnPassPanel.add(btnChangePass);
        card.add(btnPassPanel);

        container.add(card);
        JScrollPane scroll = new JScrollPane(container);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
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
        field.setPreferredSize(new Dimension(280, 36));
        panel.add(field, gbc);
    }

    private void loadProfileData() {
        TaiKhoan currentAcc = UserSession.getInstance().getCurrentAccount();
        if (currentAcc == null) return;

        txtUsername.setText(currentAcc.getTenDangNhap());

        Nguoi currentUser = UserSession.getInstance().getCurrentUser();
        if (currentUser != null) {
            txtFullName.setText(currentUser.getHoTen());
            txtPhone.setText(currentUser.getSoDienThoai());
            txtEmail.setText(currentUser.getEmail());
            txtAddress.setText(currentUser.getDiaChi());
        } else {
            // If admin or not loaded in session, query from DAO
            if (currentAcc.getLoaiTaiKhoan() == LoaiTaiKhoan.NHAN_VIEN || currentAcc.getLoaiTaiKhoan() == LoaiTaiKhoan.ADMIN) {
                Optional<NhanVien> opt = userService.getNhanVienByMaTaiKhoan(currentAcc.getMaTaiKhoan());
                opt.ifPresent(nv -> {
                    txtFullName.setText(nv.getHoTen());
                    txtPhone.setText(nv.getSoDienThoai());
                    txtEmail.setText(nv.getEmail());
                    txtAddress.setText(nv.getDiaChi());
                });
            } else if (currentAcc.getLoaiTaiKhoan() == LoaiTaiKhoan.KHACH_HANG) {
                Optional<KhachHang> opt = userService.getKhachHangByMaTaiKhoan(currentAcc.getMaTaiKhoan());
                opt.ifPresent(kh -> {
                    txtFullName.setText(kh.getHoTen());
                    txtPhone.setText(kh.getSoDienThoai());
                    txtEmail.setText(kh.getEmail());
                    txtAddress.setText(kh.getDiaChi());
                });
            }
        }
    }

    private void toggleEditMode() {
        isEditing = !isEditing;
        txtFullName.setEditable(isEditing);
        txtPhone.setEditable(isEditing);
        txtEmail.setEditable(isEditing);
        txtAddress.setEditable(isEditing);

        btnSaveProfile.setEnabled(isEditing);
        btnEditToggle.setText(isEditing ? "Hủy sửa" : "Chỉnh sửa");

        if (!isEditing) {
            loadProfileData();
        }
    }

    private void saveProfile() {
        String fullName = txtFullName.getText().trim();
        String phone = txtPhone.getText().trim();
        String email = txtEmail.getText().trim();
        String address = txtAddress.getText().trim();

        if (fullName.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Họ tên, số điện thoại và email không được để trống!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!ValidationUtil.isValidPhone(phone)) {
            JOptionPane.showMessageDialog(this, "Số điện thoại không hợp lệ (10-11 số)!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!ValidationUtil.isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Email không hợp lệ!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        TaiKhoan currentAcc = UserSession.getInstance().getCurrentAccount();
        if (currentAcc == null) return;

        boolean success = false;
        if (currentAcc.getLoaiTaiKhoan() == LoaiTaiKhoan.KHACH_HANG) {
            Optional<KhachHang> opt = userService.getKhachHangByMaTaiKhoan(currentAcc.getMaTaiKhoan());
            if (opt.isPresent()) {
                KhachHang kh = opt.get();
                kh.setHoTen(fullName);
                kh.setSoDienThoai(phone);
                kh.setEmail(email);
                kh.setDiaChi(address);
                success = userService.updateKhachHangProfile(kh);
            }
        } else {
            Optional<NhanVien> opt = userService.getNhanVienByMaTaiKhoan(currentAcc.getMaTaiKhoan());
            if (opt.isPresent()) {
                NhanVien nv = opt.get();
                nv.setHoTen(fullName);
                nv.setSoDienThoai(phone);
                nv.setEmail(email);
                nv.setDiaChi(address);
                success = userService.updateNhanVienProfile(nv);
            }
        }

        if (success) {
            JOptionPane.showMessageDialog(this, "Cập nhật thông tin thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            toggleEditMode();
            loadProfileData();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật thất bại. Vui lòng thử lại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void changePassword() {
        String oldPass = new String(txtOldPass.getPassword());
        String newPass = new String(txtNewPass.getPassword());
        String confirmPass = new String(txtConfirmPass.getPassword());

        if (oldPass.isEmpty() || newPass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mật khẩu cũ và mật khẩu mới!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!newPass.equals(confirmPass)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        TaiKhoan currentAcc = UserSession.getInstance().getCurrentAccount();
        if (currentAcc == null) return;

        try {
            userService.changePassword(currentAcc.getTenDangNhap(), oldPass, newPass);
            JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            txtOldPass.setText("");
            txtNewPass.setText("");
            txtConfirmPass.setText("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}

