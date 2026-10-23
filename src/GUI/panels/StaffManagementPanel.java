package GUI.panels;

import DAO.DanhMucDAOImpl;
import DAO.IDanhMucDAO;
import GUI.common.ThemeConfig;
import GUI.common.ValidationUtil;
import Model.CongViec;
import Model.NhanVien;
import Service.IUserService;
import Service.impl.UserServiceImpl;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class StaffManagementPanel extends JPanel {
    private final IUserService userService = new UserServiceImpl();
    private final IDanhMucDAO danhMucDAO = new DanhMucDAOImpl();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    private List<NhanVien> staffList = new ArrayList<>();
    private JTable tblStaff;
    private DefaultTableModel modelStaff;
    private JTextField txtSearch;
    private JButton btnAdd;
    private JButton btnEdit;
    private JButton btnDelete;

    public StaffManagementPanel() {
        setLayout(new BorderLayout(0, 12));
        setBackground(ThemeConfig.BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        initToolbar();
        initTable();
        loadStaff();
    }

    private void initToolbar() {
        JPanel toolbarCard = new JPanel(new BorderLayout(12, 10));
        toolbarCard.setBackground(ThemeConfig.SURFACE);
        toolbarCard.setBorder(ThemeConfig.createCardBorder());

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        searchPanel.setBackground(ThemeConfig.SURFACE);

        JLabel lblSearch = new JLabel("Tìm theo tên / SĐT:");
        lblSearch.setFont(ThemeConfig.FONT_BOLD);
        txtSearch = new JTextField(14);
        txtSearch.setPreferredSize(new Dimension(160, 36));

        JButton btnSearch = new JButton("Tìm");
        ThemeConfig.stylePrimaryButton(btnSearch);
        btnSearch.addActionListener(e -> filterStaff());

        JButton btnRefresh = new JButton("Làm mới");
        ThemeConfig.styleSecondaryButton(btnRefresh);
        btnRefresh.addActionListener(e -> loadStaff());

        searchPanel.add(lblSearch);
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnRefresh);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        actionPanel.setBackground(ThemeConfig.SURFACE);

        btnAdd = new JButton("+ Thêm nhân viên");
        ThemeConfig.stylePrimaryButton(btnAdd);
        btnAdd.addActionListener(e -> openStaffDialog(null));
        actionPanel.add(btnAdd);

        btnEdit = new JButton("Sửa");
        ThemeConfig.styleSecondaryButton(btnEdit);
        btnEdit.setEnabled(false);
        btnEdit.addActionListener(e -> {
            int row = tblStaff.getSelectedRow();
            if (row >= 0 && row < staffList.size()) {
                openStaffDialog(staffList.get(row));
            }
        });
        actionPanel.add(btnEdit);

        btnDelete = new JButton("Xóa");
        ThemeConfig.styleDangerButton(btnDelete);
        btnDelete.setEnabled(false);
        btnDelete.addActionListener(e -> deleteSelectedStaff());
        actionPanel.add(btnDelete);

        toolbarCard.add(searchPanel, BorderLayout.WEST);
        toolbarCard.add(actionPanel, BorderLayout.EAST);
        add(toolbarCard, BorderLayout.NORTH);
    }

    private void initTable() {
        String[] cols = {"Mã NV", "Họ tên", "Giới tính", "Ngày sinh", "Số ĐT", "Email", "Địa chỉ", "Chức vụ"};
        modelStaff = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tblStaff = new JTable(modelStaff);
        tblStaff.setRowHeight(38);
        tblStaff.setShowGrid(true);
        tblStaff.setGridColor(ThemeConfig.BORDER);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tblStaff.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tblStaff.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        tblStaff.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        tblStaff.getSelectionModel().addListSelectionListener(e -> {
            boolean selected = tblStaff.getSelectedRow() != -1;
            btnEdit.setEnabled(selected);
            btnDelete.setEnabled(selected);
        });

        JScrollPane scroll = new JScrollPane(tblStaff);
        scroll.setBorder(BorderFactory.createLineBorder(ThemeConfig.BORDER, 1));
        add(scroll, BorderLayout.CENTER);
    }

    public void loadStaff() {
        txtSearch.setText("");
        staffList = userService.getAllNhanVien();
        renderTable(staffList);
    }

    private void renderTable(List<NhanVien> list) {
        modelStaff.setRowCount(0);
        for (NhanVien nv : list) {
            String ns = nv.getNgaySinh() != null ? dateFormat.format(nv.getNgaySinh()) : "-";
            String cv = nv.getCongViec() != null ? nv.getCongViec().getTenCV() : "-";

            modelStaff.addRow(new Object[]{
                nv.getMaNhanVien(),
                nv.getHoTen(),
                nv.getGioiTinh() != null ? nv.getGioiTinh() : "-",
                ns,
                nv.getSoDienThoai(),
                nv.getEmail(),
                nv.getDiaChi(),
                cv
            });
        }
    }

    private void filterStaff() {
        String kw = txtSearch.getText().trim().toLowerCase();
        if (kw.isEmpty()) {
            renderTable(staffList);
            return;
        }

        List<NhanVien> filtered = new ArrayList<>();
        for (NhanVien nv : staffList) {
            String name = nv.getHoTen() != null ? nv.getHoTen().toLowerCase() : "";
            String phone = nv.getSoDienThoai() != null ? nv.getSoDienThoai() : "";
            if (name.contains(kw) || phone.contains(kw)) {
                filtered.add(nv);
            }
        }
        renderTable(filtered);
    }

    private void openStaffDialog(NhanVien existing) {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parent, existing == null ? "Thêm nhân viên mới" : "Chỉnh sửa nhân viên", true);
        dialog.setSize(480, 560);
        dialog.setLocationRelativeTo(parent);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(ThemeConfig.BG_MAIN);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(ThemeConfig.SURFACE);
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 10, 24));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 4, 6, 4);

        JTextField txtMaNV = new JTextField();
        JTextField txtHoTen = new JTextField();
        JComboBox<String> cboGender = new JComboBox<>(new String[]{"Nam", "Nữ", "Khác"});
        JTextField txtPhone = new JTextField();
        JTextField txtEmail = new JTextField();
        JTextField txtAddress = new JTextField();
        JComboBox<CongViec> cboJob = new JComboBox<>();
        JTextField txtUsername = new JTextField();
        JPasswordField txtPassword = new JPasswordField();

        danhMucDAO.getAllCongViec().forEach(cboJob::addItem);

        int row = 0;
        if (existing == null) {
            addFormField(form, gbc, row++, "Mã nhân viên (*):", txtMaNV);
            addFormField(form, gbc, row++, "Tên đăng nhập (*):", txtUsername);
            addFormField(form, gbc, row++, "Mật khẩu ban đầu (*):", txtPassword);
        } else {
            txtMaNV.setText(existing.getMaNhanVien());
            txtMaNV.setEditable(false);
            txtHoTen.setText(existing.getHoTen());
            cboGender.setSelectedItem(existing.getGioiTinh());
            txtPhone.setText(existing.getSoDienThoai());
            txtEmail.setText(existing.getEmail());
            txtAddress.setText(existing.getDiaChi());
            if (existing.getCongViec() != null) {
                for (int i = 0; i < cboJob.getItemCount(); i++) {
                    if (cboJob.getItemAt(i).getMaCV() == existing.getCongViec().getMaCV()) {
                        cboJob.setSelectedIndex(i);
                        break;
                    }
                }
            }
            addFormField(form, gbc, row++, "Mã nhân viên:", txtMaNV);
        }

        addFormField(form, gbc, row++, "Họ và tên (*):", txtHoTen);
        addFormField(form, gbc, row++, "Giới tính:", cboGender);
        addFormField(form, gbc, row++, "Số điện thoại (*):", txtPhone);
        addFormField(form, gbc, row++, "Email (*):", txtEmail);
        addFormField(form, gbc, row++, "Địa chỉ:", txtAddress);
        addFormField(form, gbc, row++, "Vị trí công việc:", cboJob);

        dialog.add(form, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 16));
        footer.setBackground(ThemeConfig.SURFACE);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ThemeConfig.BORDER));

        JButton btnCancel = new JButton("Hủy");
        ThemeConfig.styleSecondaryButton(btnCancel);
        btnCancel.addActionListener(e -> dialog.dispose());

        JButton btnSave = new JButton("Lưu");
        ThemeConfig.stylePrimaryButton(btnSave);
        btnSave.addActionListener(e -> {
            String ma = txtMaNV.getText().trim();
            String name = txtHoTen.getText().trim();
            String phone = txtPhone.getText().trim();
            String email = txtEmail.getText().trim();
            String addr = txtAddress.getText().trim();
            String gender = (String) cboGender.getSelectedItem();
            CongViec cv = (CongViec) cboJob.getSelectedItem();

            if (ma.isEmpty() || name.isEmpty() || phone.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập đầy đủ các trường bắt buộc (*)", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!ValidationUtil.isValidPhone(phone)) {
                JOptionPane.showMessageDialog(dialog, "Số điện thoại không hợp lệ!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!ValidationUtil.isValidEmail(email)) {
                JOptionPane.showMessageDialog(dialog, "Email không hợp lệ!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                if (existing == null) {
                    String user = txtUsername.getText().trim();
                    String pass = new String(txtPassword.getPassword());
                    if (user.isEmpty() || pass.isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, "Vui lòng nhập tên đăng nhập và mật khẩu!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    NhanVien nv = new NhanVien(ma, name, gender, new Date(), addr, phone, email, cv, ma);
                    userService.saveNhanVien(nv, user, pass);
                } else {
                    existing.setHoTen(name);
                    existing.setGioiTinh(gender);
                    existing.setSoDienThoai(phone);
                    existing.setEmail(email);
                    existing.setDiaChi(addr);
                    existing.setCongViec(cv);
                    userService.updateNhanVien(existing);
                }
                JOptionPane.showMessageDialog(dialog, "Thao tác thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadStaff();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        footer.add(btnCancel);
        footer.add(btnSave);
        dialog.add(footer, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String label, javax.swing.JComponent comp) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35;
        JLabel lbl = new JLabel(label);
        lbl.setFont(ThemeConfig.FONT_REGULAR);
        panel.add(lbl, gbc);

        gbc.gridx = 1; gbc.weightx = 0.65;
        comp.setPreferredSize(new Dimension(comp.getPreferredSize().width, 34));
        panel.add(comp, gbc);
    }

    private void deleteSelectedStaff() {
        int row = tblStaff.getSelectedRow();
        if (row < 0 || row >= staffList.size()) return;

        NhanVien nv = staffList.get(row);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc muốn xóa nhân viên \"" + nv.getHoTen() + "\" (Mã: " + nv.getMaNhanVien() + ")?",
            "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = userService.deleteNhanVien(nv.getMaNhanVien());
            if (ok) {
                JOptionPane.showMessageDialog(this, "Đã xóa nhân viên thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadStaff();
            } else {
                JOptionPane.showMessageDialog(this, "Không thể xóa nhân viên (có thể nhân viên đã lập hóa đơn trong hệ thống)!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

