package GUI.panels;

import GUI.common.ThemeConfig;
import GUI.common.ValidationUtil;
import Model.KhachHang;
import Service.IUserService;
import Service.impl.UserServiceImpl;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class CustomerManagementPanel extends JPanel {
    private final IUserService userService = new UserServiceImpl();

    private List<KhachHang> customerList = new ArrayList<>();
    private JTable tblCustomers;
    private DefaultTableModel modelCustomers;
    private JTextField txtSearch;
    private JButton btnEdit;
    private JButton btnDelete;

    public CustomerManagementPanel() {
        setLayout(new BorderLayout(0, 12));
        setBackground(ThemeConfig.BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        initToolbar();
        initTable();
        loadCustomers();
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
        btnSearch.addActionListener(e -> filterCustomers());

        JButton btnRefresh = new JButton("Làm mới");
        ThemeConfig.styleSecondaryButton(btnRefresh);
        btnRefresh.addActionListener(e -> loadCustomers());

        searchPanel.add(lblSearch);
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnRefresh);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        actionPanel.setBackground(ThemeConfig.SURFACE);

        btnEdit = new JButton("Sửa thông tin");
        ThemeConfig.styleSecondaryButton(btnEdit);
        btnEdit.setEnabled(false);
        btnEdit.addActionListener(e -> {
            int row = tblCustomers.getSelectedRow();
            if (row >= 0 && row < customerList.size()) {
                openEditDialog(customerList.get(row));
            }
        });
        actionPanel.add(btnEdit);

        btnDelete = new JButton("Xóa");
        ThemeConfig.styleDangerButton(btnDelete);
        btnDelete.setEnabled(false);
        btnDelete.addActionListener(e -> deleteSelectedCustomer());
        actionPanel.add(btnDelete);

        toolbarCard.add(searchPanel, BorderLayout.WEST);
        toolbarCard.add(actionPanel, BorderLayout.EAST);
        add(toolbarCard, BorderLayout.NORTH);
    }

    private void initTable() {
        String[] cols = {"Mã KH", "Họ và tên", "Số điện thoại", "Email", "Địa chỉ"};
        modelCustomers = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tblCustomers = new JTable(modelCustomers);
        tblCustomers.setRowHeight(38);
        tblCustomers.setShowGrid(true);
        tblCustomers.setGridColor(ThemeConfig.BORDER);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tblCustomers.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);

        tblCustomers.getSelectionModel().addListSelectionListener(e -> {
            boolean selected = tblCustomers.getSelectedRow() != -1;
            btnEdit.setEnabled(selected);
            btnDelete.setEnabled(selected);
        });

        JScrollPane scroll = new JScrollPane(tblCustomers);
        scroll.setBorder(BorderFactory.createLineBorder(ThemeConfig.BORDER, 1));
        add(scroll, BorderLayout.CENTER);
    }

    public void loadCustomers() {
        txtSearch.setText("");
        customerList = userService.getAllKhachHang();
        renderTable(customerList);
    }

    private void renderTable(List<KhachHang> list) {
        modelCustomers.setRowCount(0);
        for (KhachHang kh : list) {
            modelCustomers.addRow(new Object[]{
                kh.getMaKhachHang(),
                kh.getHoTen(),
                kh.getSoDienThoai(),
                kh.getEmail(),
                kh.getDiaChi()
            });
        }
    }

    private void filterCustomers() {
        String kw = txtSearch.getText().trim().toLowerCase();
        if (kw.isEmpty()) {
            renderTable(customerList);
            return;
        }

        List<KhachHang> filtered = new ArrayList<>();
        for (KhachHang kh : customerList) {
            String name = kh.getHoTen() != null ? kh.getHoTen().toLowerCase() : "";
            String phone = kh.getSoDienThoai() != null ? kh.getSoDienThoai() : "";
            if (name.contains(kw) || phone.contains(kw)) {
                filtered.add(kh);
            }
        }
        renderTable(filtered);
    }

    private void openEditDialog(KhachHang kh) {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parent, "Chỉnh sửa khách hàng #" + kh.getMaKhachHang(), true);
        dialog.setSize(440, 400);
        dialog.setLocationRelativeTo(parent);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(ThemeConfig.SURFACE);
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 10, 24));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 4, 6, 4);

        JTextField txtName = new JTextField(kh.getHoTen());
        JTextField txtPhone = new JTextField(kh.getSoDienThoai());
        JTextField txtEmail = new JTextField(kh.getEmail());
        JTextField txtAddress = new JTextField(kh.getDiaChi());

        addFormField(form, gbc, 0, "Họ và tên (*):", txtName);
        addFormField(form, gbc, 1, "Số điện thoại (*):", txtPhone);
        addFormField(form, gbc, 2, "Email (*):", txtEmail);
        addFormField(form, gbc, 3, "Địa chỉ:", txtAddress);

        dialog.add(form, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 16));
        footer.setBackground(ThemeConfig.SURFACE);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ThemeConfig.BORDER));

        JButton btnCancel = new JButton("Hủy");
        ThemeConfig.styleSecondaryButton(btnCancel);
        btnCancel.addActionListener(e -> dialog.dispose());

        JButton btnSave = new JButton("Lưu thay đổi");
        ThemeConfig.stylePrimaryButton(btnSave);
        btnSave.addActionListener(e -> {
            String name = txtName.getText().trim();
            String phone = txtPhone.getText().trim();
            String email = txtEmail.getText().trim();
            String addr = txtAddress.getText().trim();

            if (name.isEmpty() || phone.isEmpty() || email.isEmpty()) {
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

            kh.setHoTen(name);
            kh.setSoDienThoai(phone);
            kh.setEmail(email);
            kh.setDiaChi(addr);

            boolean ok = userService.updateKhachHang(kh);
            if (ok) {
                JOptionPane.showMessageDialog(dialog, "Cập nhật thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadCustomers();
            } else {
                JOptionPane.showMessageDialog(dialog, "Lỗi cập nhật khách hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
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

    private void deleteSelectedCustomer() {
        int row = tblCustomers.getSelectedRow();
        if (row < 0 || row >= customerList.size()) return;

        KhachHang kh = customerList.get(row);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc muốn xóa khách hàng \"" + kh.getHoTen() + "\" (Mã: " + kh.getMaKhachHang() + ")?",
            "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = userService.deleteKhachHang(kh.getMaKhachHang());
            if (ok) {
                JOptionPane.showMessageDialog(this, "Đã xóa khách hàng thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadCustomers();
            } else {
                JOptionPane.showMessageDialog(this, "Không thể xóa khách hàng (có thể khách hàng đã có lịch sử đơn hàng)!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

