package GUI.panels;

import GUI.common.CurrencyUtil;
import GUI.common.ThemeConfig;
import Model.*;
import Service.IOrderService;
import Service.IProductService;
import Service.IReportService;
import Service.IUserService;
import Service.UserSession;
import Service.impl.OrderServiceImpl;
import Service.impl.ProductServiceImpl;
import Service.impl.ReportServiceImpl;
import Service.impl.UserServiceImpl;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;

public class InvoiceCreationDialog extends JDialog {
    private final IOrderService orderService = new OrderServiceImpl();
    private final IProductService productService = new ProductServiceImpl();
    private final IUserService userService = new UserServiceImpl();
    private final IReportService reportService = new ReportServiceImpl();

    private boolean invoiceCreated = false;
    private final List<ItemGioHang> billItems = new ArrayList<>();
    private List<SanPham> availableProducts = new ArrayList<>();

    // Left components (Product picker)
    private JTextField txtSearchProduct;
    private JTable tblProducts;
    private DefaultTableModel modelProducts;
    private JSpinner spinQuantity;

    // Right components (Bill details)
    private JTable tblBill;
    private DefaultTableModel modelBill;
    private JComboBox<KhachHang> cboCustomer;
    private JSpinner spinDiscount;
    private JLabel lblTotalAmount;

    public InvoiceCreationDialog(Frame parent) {
        super(parent, "Tạo hóa đơn bán hàng tại quầy", true);
        initUI();
        loadCustomers();
        loadProducts();
        updateTotal();
    }

    public boolean isInvoiceCreated() {
        return invoiceCreated;
    }

    private void initUI() {
        setSize(960, 680);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout());
        getContentPane().setBackground(ThemeConfig.BG_MAIN);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ThemeConfig.SURFACE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeConfig.BORDER),
            BorderFactory.createEmptyBorder(16, 24, 16, 24)
        ));

        JLabel lblTitle = new JLabel("Lập hóa đơn bán hàng");
        lblTitle.setFont(ThemeConfig.FONT_TITLE);
        lblTitle.setForeground(ThemeConfig.TEXT_MAIN);
        headerPanel.add(lblTitle, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // Center: 2 panels split horizontally
        JPanel centerPanel = new JPanel(new java.awt.GridLayout(1, 2, 16, 0));
        centerPanel.setBackground(ThemeConfig.BG_MAIN);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        // LEFT: Product picker
        JPanel leftPanel = createProductPickerPanel();
        centerPanel.add(leftPanel);

        // RIGHT: Bill items & Payment
        JPanel rightPanel = createBillSummaryPanel();
        centerPanel.add(rightPanel);

        add(centerPanel, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 16));
        footer.setBackground(ThemeConfig.SURFACE);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ThemeConfig.BORDER));

        JButton btnClose = new JButton("Đóng");
        ThemeConfig.styleSecondaryButton(btnClose);
        btnClose.addActionListener(e -> dispose());

        JButton btnCheckout = new JButton("Thanh toán & Lưu hóa đơn");
        ThemeConfig.stylePrimaryButton(btnCheckout);
        btnCheckout.addActionListener(e -> handleCheckout());

        footer.add(btnClose);
        footer.add(btnCheckout);
        add(footer, BorderLayout.SOUTH);
    }

    private JPanel createProductPickerPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(ThemeConfig.SURFACE);
        panel.setBorder(ThemeConfig.createCardBorder());

        JLabel lblSec = new JLabel("1. Chọn sản phẩm");
        lblSec.setFont(ThemeConfig.FONT_HEADER);
        panel.add(lblSec, BorderLayout.NORTH);

        // Search bar
        JPanel searchRow = new JPanel(new BorderLayout(8, 0));
        searchRow.setBackground(ThemeConfig.SURFACE);
        txtSearchProduct = new JTextField();
        txtSearchProduct.setPreferredSize(new Dimension(140, 34));
        JButton btnSearch = new JButton("Tìm");
        ThemeConfig.styleSecondaryButton(btnSearch);
        btnSearch.addActionListener(e -> filterProductList());

        searchRow.add(txtSearchProduct, BorderLayout.CENTER);
        searchRow.add(btnSearch, BorderLayout.EAST);

        // Table
        String[] cols = {"Mã", "Tên sản phẩm", "Tồn kho", "Đơn giá"};
        modelProducts = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblProducts = new JTable(modelProducts);
        tblProducts.setRowHeight(32);
        tblProducts.getColumnModel().getColumn(0).setPreferredWidth(40);
        tblProducts.getColumnModel().getColumn(1).setPreferredWidth(160);
        tblProducts.getColumnModel().getColumn(2).setPreferredWidth(60);
        tblProducts.getColumnModel().getColumn(3).setPreferredWidth(90);

        JScrollPane scroll = new JScrollPane(tblProducts);

        // Add to bill action
        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        actionRow.setBackground(ThemeConfig.SURFACE);
        actionRow.add(new JLabel("Số lượng:"));
        spinQuantity = new JSpinner(new SpinnerNumberModel(1, 1, 9999, 1));
        spinQuantity.setPreferredSize(new Dimension(70, 32));
        actionRow.add(spinQuantity);

        JButton btnAdd = new JButton("+ Thêm vào đơn");
        ThemeConfig.stylePrimaryButton(btnAdd);
        btnAdd.addActionListener(e -> addProductToBill());
        actionRow.add(btnAdd);

        JPanel centerArea = new JPanel(new BorderLayout(0, 8));
        centerArea.setBackground(ThemeConfig.SURFACE);
        centerArea.add(searchRow, BorderLayout.NORTH);
        centerArea.add(scroll, BorderLayout.CENTER);
        centerArea.add(actionRow, BorderLayout.SOUTH);

        panel.add(centerArea, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createBillSummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(ThemeConfig.SURFACE);
        panel.setBorder(ThemeConfig.createCardBorder());

        JLabel lblSec = new JLabel("2. Chi tiết đơn & Khách hàng");
        lblSec.setFont(ThemeConfig.FONT_HEADER);
        panel.add(lblSec, BorderLayout.NORTH);

        // Bill Table
        String[] cols = {"Tên SP", "SL", "Đơn giá", "Thành tiền"};
        modelBill = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblBill = new JTable(modelBill);
        tblBill.setRowHeight(32);
        JScrollPane scroll = new JScrollPane(tblBill);

        // Remove item button
        JPanel removeRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 2));
        removeRow.setBackground(ThemeConfig.SURFACE);
        JButton btnRemoveItem = new JButton("Xóa dòng chọn");
        ThemeConfig.styleSecondaryButton(btnRemoveItem);
        btnRemoveItem.addActionListener(e -> removeSelectedBillItem());
        removeRow.add(btnRemoveItem);

        // Info Form (Customer & Discount)
        JPanel formInfo = new JPanel(new GridBagLayout());
        formInfo.setBackground(ThemeConfig.SURFACE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        cboCustomer = new JComboBox<>();
        spinDiscount = new JSpinner(new SpinnerNumberModel(0, 0, 100, 5));
        spinDiscount.addChangeListener(e -> updateTotal());

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.35;
        formInfo.add(new JLabel("Khách hàng:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        formInfo.add(cboCustomer, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.35;
        formInfo.add(new JLabel("Chiết khấu (%):"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        formInfo.add(spinDiscount, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.35;
        JLabel lblTotalTitle = new JLabel("Tổng thanh toán:");
        lblTotalTitle.setFont(ThemeConfig.FONT_HEADER);
        formInfo.add(lblTotalTitle, gbc);

        gbc.gridx = 1; gbc.weightx = 0.65;
        lblTotalAmount = new JLabel("0 ₫");
        lblTotalAmount.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 18));
        lblTotalAmount.setForeground(ThemeConfig.PRIMARY);
        formInfo.add(lblTotalAmount, gbc);

        JPanel centerWrap = new JPanel(new BorderLayout(0, 4));
        centerWrap.setBackground(ThemeConfig.SURFACE);
        centerWrap.add(scroll, BorderLayout.CENTER);
        centerWrap.add(removeRow, BorderLayout.SOUTH);

        JPanel bottomWrap = new JPanel(new BorderLayout());
        bottomWrap.setBackground(ThemeConfig.SURFACE);
        bottomWrap.add(formInfo, BorderLayout.CENTER);

        panel.add(centerWrap, BorderLayout.CENTER);
        panel.add(bottomWrap, BorderLayout.SOUTH);
        return panel;
    }

    private void loadCustomers() {
        cboCustomer.addItem(new KhachHang("KH_LE", "Khách hàng vãng lai", "", "", "", null));
        List<KhachHang> list = userService.getAllKhachHang();
        for (KhachHang kh : list) {
            cboCustomer.addItem(kh);
        }
    }

    private void loadProducts() {
        availableProducts = productService.getAllProducts();
        renderProducts(availableProducts);
    }

    private void renderProducts(List<SanPham> list) {
        modelProducts.setRowCount(0);
        for (SanPham sp : list) {
            if (sp.getSoLuong() > 0) {
                modelProducts.addRow(new Object[]{
                    sp.getMaQuanAo(),
                    sp.getTenQuanAo(),
                    sp.getSoLuong(),
                    CurrencyUtil.format(sp.getDonGiaBan())
                });
            }
        }
    }

    private void filterProductList() {
        String kw = txtSearchProduct.getText().trim();
        List<SanPham> filtered = productService.searchAndFilter(kw, null, null, null, null, null, null, null);
        renderProducts(filtered);
    }

    private void addProductToBill() {
        int row = tblProducts.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một sản phẩm từ danh sách!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int maSP = (int) modelProducts.getValueAt(row, 0);
        int qty = (int) spinQuantity.getValue();

        SanPham sp = productService.getProductById(maSP).orElse(null);
        if (sp == null) return;

        if (qty > sp.getSoLuong()) {
            JOptionPane.showMessageDialog(this, "Số lượng trong kho không đủ (chỉ còn: " + sp.getSoLuong() + ")!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Check if already in bill
        boolean found = false;
        for (ItemGioHang item : billItems) {
            if (item.getSanPham().getMaQuanAo() == maSP) {
                if (item.getSoLuongDat() + qty > sp.getSoLuong()) {
                    JOptionPane.showMessageDialog(this, "Tổng số lượng vượt quá tồn kho!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                item.setSoLuongDat(item.getSoLuongDat() + qty);
                found = true;
                break;
            }
        }

        if (!found) {
            ItemGioHang item = new ItemGioHang();
            item.setSanPham(sp);
            item.setSoLuongDat(qty);
            item.setDonGiaBan(sp.getDonGiaBan());
            billItems.add(item);
        }

        renderBill();
        updateTotal();
    }

    private void removeSelectedBillItem() {
        int row = tblBill.getSelectedRow();
        if (row >= 0 && row < billItems.size()) {
            billItems.remove(row);
            renderBill();
            updateTotal();
        }
    }

    private void renderBill() {
        modelBill.setRowCount(0);
        for (ItemGioHang item : billItems) {
            BigDecimal lineTotal = item.getDonGiaBan().multiply(BigDecimal.valueOf(item.getSoLuongDat()));
            modelBill.addRow(new Object[]{
                item.getSanPham().getTenQuanAo(),
                item.getSoLuongDat(),
                CurrencyUtil.format(item.getDonGiaBan()),
                CurrencyUtil.format(lineTotal)
            });
        }
    }

    private void updateTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemGioHang item : billItems) {
            BigDecimal line = item.getDonGiaBan().multiply(BigDecimal.valueOf(item.getSoLuongDat()));
            total = total.add(line);
        }

        int discountPercent = (int) spinDiscount.getValue();
        if (discountPercent > 0) {
            BigDecimal discountFactor = BigDecimal.valueOf(100 - discountPercent).divide(BigDecimal.valueOf(100));
            total = total.multiply(discountFactor);
        }

        lblTotalAmount.setText(CurrencyUtil.format(total));
    }

    private void handleCheckout() {
        if (billItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Hóa đơn chưa có sản phẩm nào!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        KhachHang selectedKH = (KhachHang) cboCustomer.getSelectedItem();
        String maKH = (selectedKH != null && !"KH_LE".equals(selectedKH.getMaKhachHang())) ? selectedKH.getMaKhachHang() : null;

        String maNV = null;
        if (UserSession.getInstance().getCurrentUser() != null) {
            maNV = UserSession.getInstance().getCurrentUser().getMaDinhDanh();
        }

        try {
            HoaDonBan hdb = orderService.lapHoaDonBan(maKH, maNV, billItems);
            invoiceCreated = true;

            int confirmPrint = JOptionPane.showConfirmDialog(this,
                "Thanh toán và lưu hóa đơn #" + hdb.getSoHoaDon() + " thành công!\nBạn có muốn xuất hóa đơn ra file Excel ngay không?",
                "Thành công", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

            if (confirmPrint == JOptionPane.YES_OPTION) {
                reportService.exportHoaDonBan(String.valueOf(hdb.getSoHoaDon()));
            }

            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi thanh toán: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}

