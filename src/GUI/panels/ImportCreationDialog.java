package GUI.panels;

import DAO.DanhMucDAOImpl;
import DAO.IDanhMucDAO;
import GUI.common.CurrencyUtil;
import GUI.common.ThemeConfig;
import GUI.common.ValidationUtil;
import Model.*;
import Service.IImportService;
import Service.IProductService;
import Service.UserSession;
import Service.impl.ImportServiceImpl;
import Service.impl.ProductServiceImpl;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

public class ImportCreationDialog extends JDialog {
    private final IImportService importService = new ImportServiceImpl();
    private final IProductService productService = new ProductServiceImpl();
    private final IDanhMucDAO danhMucDAO = new DanhMucDAOImpl();

    private boolean saved = false;

    private JComboBox<NhaCungCap> cboNCC;
    private JComboBox<SanPham> cboProduct;
    private JTextField txtQuantity;
    private JTextField txtImportPrice;
    private JSpinner spinDiscount;

    public ImportCreationDialog(Frame parent) {
        super(parent, "Lập phiếu nhập hàng", true);
        initUI();
        loadData();
    }

    public boolean isSaved() {
        return saved;
    }

    private void initUI() {
        setSize(520, 440);
        setResizable(false);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout());
        getContentPane().setBackground(ThemeConfig.BG_MAIN);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ThemeConfig.SURFACE);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeConfig.BORDER),
            BorderFactory.createEmptyBorder(18, 24, 18, 24)
        ));

        JLabel lblTitle = new JLabel("Tạo phiếu nhập kho");
        lblTitle.setFont(ThemeConfig.FONT_TITLE);
        lblTitle.setForeground(ThemeConfig.TEXT_MAIN);
        header.add(lblTitle, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(ThemeConfig.SURFACE);
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 10, 24));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 4, 8, 4);

        cboNCC = new JComboBox<>();
        cboProduct = new JComboBox<>();
        txtQuantity = new JTextField();
        txtImportPrice = new JTextField();
        spinDiscount = new JSpinner(new SpinnerNumberModel(0, 0, 100, 5));

        cboProduct.addActionListener(e -> {
            SanPham selected = (SanPham) cboProduct.getSelectedItem();
            if (selected != null && selected.getDonGiaNhap() != null) {
                txtImportPrice.setText(selected.getDonGiaNhap().toPlainString());
            }
        });

        addRow(form, gbc, 0, "Nhà cung cấp (*):", cboNCC);
        addRow(form, gbc, 1, "Sản phẩm (*):", cboProduct);
        addRow(form, gbc, 2, "Số lượng nhập (*):", txtQuantity);
        addRow(form, gbc, 3, "Đơn giá nhập (₫):", txtImportPrice);
        addRow(form, gbc, 4, "Chiết khấu (%):", spinDiscount);

        add(form, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 16));
        footer.setBackground(ThemeConfig.SURFACE);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ThemeConfig.BORDER));

        JButton btnCancel = new JButton("Hủy bỏ");
        ThemeConfig.styleSecondaryButton(btnCancel);
        btnCancel.addActionListener(e -> dispose());

        JButton btnSubmit = new JButton("Lưu phiếu nhập");
        ThemeConfig.stylePrimaryButton(btnSubmit);
        btnSubmit.addActionListener(e -> handleSave());

        footer.add(btnCancel);
        footer.add(btnSubmit);
        add(footer, BorderLayout.SOUTH);
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, int row, String label, javax.swing.JComponent comp) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35;
        JLabel lbl = new JLabel(label);
        lbl.setFont(ThemeConfig.FONT_REGULAR);
        lbl.setForeground(ThemeConfig.TEXT_MAIN);
        panel.add(lbl, gbc);

        gbc.gridx = 1; gbc.weightx = 0.65;
        comp.setPreferredSize(new Dimension(comp.getPreferredSize().width, 36));
        panel.add(comp, gbc);
    }

    private void loadData() {
        List<NhaCungCap> nccList = danhMucDAO.getAllNhaCungCap();
        nccList.forEach(cboNCC::addItem);

        List<SanPham> spList = productService.getAllProducts();
        spList.forEach(cboProduct::addItem);
    }

    private void handleSave() {
        NhaCungCap ncc = (NhaCungCap) cboNCC.getSelectedItem();
        SanPham sp = (SanPham) cboProduct.getSelectedItem();
        String qtyStr = txtQuantity.getText().trim();
        String priceStr = txtImportPrice.getText().trim();

        if (ncc == null || sp == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhà cung cấp và sản phẩm!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!ValidationUtil.isPositiveInt(qtyStr)) {
            JOptionPane.showMessageDialog(this, "Số lượng nhập phải là số nguyên > 0!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!ValidationUtil.isPositiveBigDecimal(priceStr)) {
            JOptionPane.showMessageDialog(this, "Đơn giá nhập không hợp lệ!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int qty = Integer.parseInt(qtyStr);
        BigDecimal price = CurrencyUtil.parse(priceStr);
        double discount = ((Number) spinDiscount.getValue()).doubleValue();

        String maNV = null;
        if (UserSession.getInstance().getCurrentUser() != null) {
            maNV = UserSession.getInstance().getCurrentUser().getMaDinhDanh();
        }

        try {
            HoaDonNhap hdn = importService.lapHoaDonNhap(maNV, ncc.getMaNCC(), sp.getMaQuanAo(), qty, price, discount);
            saved = true;
            JOptionPane.showMessageDialog(this, "Đã lập phiếu nhập kho #" + hdn.getSoHoaDon() + " thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi lập phiếu nhập: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}

