package GUI.panels;

import GUI.common.CurrencyUtil;
import GUI.common.ImageUtil;
import GUI.common.ThemeConfig;
import Model.ItemGioHang;
import Service.IOrderService;
import Service.UserSession;
import Service.impl.OrderServiceImpl;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class CartPanel extends JPanel {
    private final IOrderService orderService = new OrderServiceImpl();
    private List<ItemGioHang> cartItems = new ArrayList<>();

    private JTable tblCart;
    private DefaultTableModel modelCart;
    private JLabel lblTotalAmount;
    private JButton btnOrder;
    private JButton btnRemove;
    private JButton btnIncrease;
    private JButton btnDecrease;

    public CartPanel() {
        setLayout(new BorderLayout(0, 12));
        setBackground(ThemeConfig.BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        initHeader();
        initTable();
        initBottomSummary();
        loadCart();
    }

    private void initHeader() {
        JPanel headerCard = new JPanel(new BorderLayout(12, 10));
        headerCard.setBackground(ThemeConfig.SURFACE);
        headerCard.setBorder(ThemeConfig.createCardBorder());

        JLabel lblTitle = new JLabel("Giỏ hàng của bạn");
        lblTitle.setFont(ThemeConfig.FONT_TITLE);
        lblTitle.setForeground(ThemeConfig.TEXT_MAIN);

        JButton btnRefresh = new JButton("Làm mới giỏ");
        ThemeConfig.styleSecondaryButton(btnRefresh);
        btnRefresh.addActionListener(e -> loadCart());

        headerCard.add(lblTitle, BorderLayout.WEST);
        headerCard.add(btnRefresh, BorderLayout.EAST);
        add(headerCard, BorderLayout.NORTH);
    }

    private void initTable() {
        String[] cols = {"Mã SP", "Ảnh", "Tên sản phẩm", "Đơn giá", "Số lượng", "Thành tiền"};
        modelCart = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
            @Override
            public Class<?> getColumnClass(int col) {
                return col == 1 ? javax.swing.Icon.class : Object.class;
            }
        };

        tblCart = new JTable(modelCart);
        tblCart.setRowHeight(44);
        tblCart.setShowGrid(true);
        tblCart.setGridColor(ThemeConfig.BORDER);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        tblCart.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblCart.getColumnModel().getColumn(0).setCellRenderer(center);
        tblCart.getColumnModel().getColumn(1).setPreferredWidth(60);
        tblCart.getColumnModel().getColumn(4).setPreferredWidth(70);
        tblCart.getColumnModel().getColumn(4).setCellRenderer(center);

        tblCart.getSelectionModel().addListSelectionListener(e -> {
            boolean sel = tblCart.getSelectedRow() != -1;
            btnRemove.setEnabled(sel);
            btnIncrease.setEnabled(sel);
            btnDecrease.setEnabled(sel);
        });

        JScrollPane scroll = new JScrollPane(tblCart);
        scroll.setBorder(BorderFactory.createLineBorder(ThemeConfig.BORDER, 1));
        add(scroll, BorderLayout.CENTER);
    }

    private void initBottomSummary() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(ThemeConfig.SURFACE);
        bottomPanel.setBorder(ThemeConfig.createCardBorder());

        // Left quantity buttons
        JPanel qtyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        qtyPanel.setBackground(ThemeConfig.SURFACE);

        btnIncrease = new JButton("+1");
        ThemeConfig.styleSecondaryButton(btnIncrease);
        btnIncrease.setEnabled(false);
        btnIncrease.addActionListener(e -> changeQuantity(1));

        btnDecrease = new JButton("-1");
        ThemeConfig.styleSecondaryButton(btnDecrease);
        btnDecrease.setEnabled(false);
        btnDecrease.addActionListener(e -> changeQuantity(-1));

        btnRemove = new JButton("Xóa khỏi giỏ");
        ThemeConfig.styleDangerButton(btnRemove);
        btnRemove.setEnabled(false);
        btnRemove.addActionListener(e -> removeItem());

        qtyPanel.add(btnIncrease);
        qtyPanel.add(btnDecrease);
        qtyPanel.add(btnRemove);

        // Right checkout summary
        JPanel checkoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 4));
        checkoutPanel.setBackground(ThemeConfig.SURFACE);

        JLabel lblTotalTitle = new JLabel("Tổng thành tiền:");
        lblTotalTitle.setFont(ThemeConfig.FONT_HEADER);

        lblTotalAmount = new JLabel("0 ₫");
        lblTotalAmount.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 20));
        lblTotalAmount.setForeground(ThemeConfig.PRIMARY);

        btnOrder = new JButton("Đặt hàng ngay");
        ThemeConfig.stylePrimaryButton(btnOrder);
        btnOrder.setPreferredSize(new Dimension(160, 40));
        btnOrder.addActionListener(e -> handleCheckoutOrder());

        checkoutPanel.add(lblTotalTitle);
        checkoutPanel.add(lblTotalAmount);
        checkoutPanel.add(btnOrder);

        bottomPanel.add(qtyPanel, BorderLayout.WEST);
        bottomPanel.add(checkoutPanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void loadCart() {
        String maKH = UserSession.getInstance().getMaTaiKhoan();
        if (maKH == null || maKH.isEmpty()) {
            return;
        }

        cartItems = orderService.getGioHang(maKH, 0); // 0 = Chưa thanh toán
        renderCart();
    }

    private void renderCart() {
        modelCart.setRowCount(0);
        BigDecimal total = BigDecimal.ZERO;

        for (ItemGioHang item : cartItems) {
            BigDecimal lineTotal = item.getDonGiaBan().multiply(BigDecimal.valueOf(item.getSoLuongDat()));
            total = total.add(lineTotal);

            javax.swing.ImageIcon thumb = ImageUtil.getScaledImage(item.getSanPham().getAnh(), 40, 40);

            modelCart.addRow(new Object[]{
                item.getSanPham().getMaQuanAo(),
                thumb,
                item.getSanPham().getTenQuanAo(),
                CurrencyUtil.format(item.getDonGiaBan()),
                item.getSoLuongDat(),
                CurrencyUtil.format(lineTotal)
            });
        }

        lblTotalAmount.setText(CurrencyUtil.format(total));
        btnOrder.setEnabled(!cartItems.isEmpty());
    }

    private void changeQuantity(int delta) {
        int row = tblCart.getSelectedRow();
        if (row < 0 || row >= cartItems.size()) return;

        ItemGioHang item = cartItems.get(row);
        int newQty = item.getSoLuongDat() + delta;
        if (newQty <= 0) {
            removeItem();
            return;
        }

        try {
            orderService.capNhatSoLuong(UserSession.getInstance().getMaTaiKhoan(), item.getSanPham().getMaQuanAo(), newQty);
            loadCart();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeItem() {
        int row = tblCart.getSelectedRow();
        if (row < 0 || row >= cartItems.size()) return;

        ItemGioHang item = cartItems.get(row);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc muốn xóa \"" + item.getSanPham().getTenQuanAo() + "\" khỏi giỏ hàng?",
            "Xác nhận", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                orderService.xoaKhoiGioHang(UserSession.getInstance().getMaTaiKhoan(), item.getSanPham().getMaQuanAo());
                loadCart();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleCheckoutOrder() {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Giỏ hàng đang trống!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn đặt " + cartItems.size() + " mặt hàng với tổng thanh toán là " + lblTotalAmount.getText() + "?",
            "Xác nhận đặt hàng", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                orderService.datHang(UserSession.getInstance().getMaTaiKhoan());
                JOptionPane.showMessageDialog(this, "Đặt hàng thành công! Đơn hàng của bạn đang được xử lý.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadCart();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi đặt hàng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

