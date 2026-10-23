package GUI.panels;

import GUI.common.CurrencyUtil;
import GUI.common.ThemeConfig;
import Model.HoaDonBan;
import Model.SanPham;
import Service.IOrderService;
import Service.IProductService;
import Service.impl.OrderServiceImpl;
import Service.impl.ProductServiceImpl;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class DashboardOverviewPanel extends JPanel {
    private final IProductService productService = new ProductServiceImpl();
    private final IOrderService orderService = new OrderServiceImpl();

    // Metric labels
    private JLabel lblTotalRevenue;
    private JLabel lblTotalOrders;
    private JLabel lblLowStockCount;
    private JLabel lblTotalProducts;

    // Tables
    private JTable tblTopSelling;
    private DefaultTableModel modelTopSelling;
    private JTable tblLowStock;
    private DefaultTableModel modelLowStock;

    public DashboardOverviewPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(ThemeConfig.BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        initHeader();
        initMetrics();
        initTables();
        loadDashboardData();
    }

    private void initHeader() {
        JPanel headerCard = new JPanel(new BorderLayout(12, 10));
        headerCard.setBackground(ThemeConfig.SURFACE);
        headerCard.setBorder(ThemeConfig.createCardBorder());

        JLabel lblTitle = new JLabel("Tổng quan tình hình kinh doanh");
        lblTitle.setFont(ThemeConfig.FONT_TITLE);
        lblTitle.setForeground(ThemeConfig.TEXT_MAIN);

        JButton btnRefresh = new JButton("Làm mới số liệu");
        ThemeConfig.styleSecondaryButton(btnRefresh);
        btnRefresh.addActionListener(e -> loadDashboardData());

        headerCard.add(lblTitle, BorderLayout.WEST);
        headerCard.add(btnRefresh, BorderLayout.EAST);
        add(headerCard, BorderLayout.NORTH);
    }

    private void initMetrics() {
        JPanel metricsPanel = new JPanel(new GridLayout(1, 4, 16, 0));
        metricsPanel.setBackground(ThemeConfig.BG_MAIN);

        lblTotalRevenue = new JLabel("0 ₫");
        lblTotalOrders = new JLabel("0");
        lblLowStockCount = new JLabel("0");
        lblTotalProducts = new JLabel("0");

        metricsPanel.add(createMetricCard("DOANH THU", lblTotalRevenue, ThemeConfig.PRIMARY));
        metricsPanel.add(createMetricCard("TỔNG ĐƠN HÀNG", lblTotalOrders, ThemeConfig.SUCCESS));
        metricsPanel.add(createMetricCard("HÀNG SẮP HẾT", lblLowStockCount, ThemeConfig.DANGER));
        metricsPanel.add(createMetricCard("TỔNG MẶT HÀNG", lblTotalProducts, ThemeConfig.TEXT_MAIN));

        add(metricsPanel, BorderLayout.CENTER);
    }

    private JPanel createMetricCard(String title, JLabel valueLabel, Color accentColor) {
        JPanel card = new JPanel();
        card.setBackground(ThemeConfig.SURFACE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 0, 0, accentColor),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeConfig.BORDER, 1),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
            )
        ));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(ThemeConfig.FONT_SMALL_BOLD);
        lblTitle.setForeground(ThemeConfig.TEXT_MUTED);

        valueLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 22));
        valueLabel.setForeground(accentColor);

        card.add(lblTitle);
        card.add(Box.createVerticalStrut(8));
        card.add(valueLabel);
        return card;
    }

    private void initTables() {
        JPanel tablesPanel = new JPanel(new GridLayout(1, 2, 16, 0));
        tablesPanel.setPreferredSize(new Dimension(800, 340));
        tablesPanel.setBackground(ThemeConfig.BG_MAIN);

        // LEFT: Top Selling
        JPanel left = new JPanel(new BorderLayout(0, 8));
        left.setBackground(ThemeConfig.SURFACE);
        left.setBorder(ThemeConfig.createCardBorder());

        JLabel lblTop = new JLabel("Top 10 sản phẩm bán chạy nhất");
        lblTop.setFont(ThemeConfig.FONT_HEADER);
        left.add(lblTop, BorderLayout.NORTH);

        String[] colsTop = {"Mã SP", "Tên sản phẩm", "Đơn giá", "Tồn kho"};
        modelTopSelling = new DefaultTableModel(colsTop, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblTopSelling = new JTable(modelTopSelling);
        tblTopSelling.setRowHeight(32);
        left.add(new JScrollPane(tblTopSelling), BorderLayout.CENTER);

        // RIGHT: Low Stock Alert
        JPanel right = new JPanel(new BorderLayout(0, 8));
        right.setBackground(ThemeConfig.SURFACE);
        right.setBorder(ThemeConfig.createCardBorder());

        JLabel lblLow = new JLabel("Cảnh báo: Sản phẩm sắp hết hàng (<= 10)");
        lblLow.setFont(ThemeConfig.FONT_HEADER);
        lblLow.setForeground(ThemeConfig.DANGER);
        right.add(lblLow, BorderLayout.NORTH);

        String[] colsLow = {"Mã SP", "Tên sản phẩm", "Tồn kho", "Đơn giá"};
        modelLowStock = new DefaultTableModel(colsLow, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblLowStock = new JTable(modelLowStock);
        tblLowStock.setRowHeight(32);
        right.add(new JScrollPane(tblLowStock), BorderLayout.CENTER);

        tablesPanel.add(left);
        tablesPanel.add(right);
        add(tablesPanel, BorderLayout.SOUTH);
    }

    public void loadDashboardData() {
        // Orders & Revenue
        List<HoaDonBan> allOrders = orderService.getAllOrders();
        BigDecimal totalRev = BigDecimal.ZERO;
        for (HoaDonBan hd : allOrders) {
            if (hd.getTongTien() != null) {
                totalRev = totalRev.add(hd.getTongTien());
            }
        }
        lblTotalRevenue.setText(CurrencyUtil.format(totalRev));
        lblTotalOrders.setText(String.valueOf(allOrders.size()));

        // Products
        List<SanPham> allProducts = productService.getAllProducts();
        lblTotalProducts.setText(String.valueOf(allProducts.size()));

        // Low stock
        List<SanPham> lowStock = productService.getLowStockAlert(10);
        lblLowStockCount.setText(String.valueOf(lowStock.size()));

        // Render Low stock table
        modelLowStock.setRowCount(0);
        for (SanPham sp : lowStock) {
            modelLowStock.addRow(new Object[]{
                sp.getMaQuanAo(),
                sp.getTenQuanAo(),
                sp.getSoLuong(),
                CurrencyUtil.format(sp.getDonGiaBan())
            });
        }

        // Render Top selling table
        List<SanPham> topSelling = productService.getTopSelling(10);
        modelTopSelling.setRowCount(0);
        for (SanPham sp : topSelling) {
            modelTopSelling.addRow(new Object[]{
                sp.getMaQuanAo(),
                sp.getTenQuanAo(),
                CurrencyUtil.format(sp.getDonGiaBan()),
                sp.getSoLuong()
            });
        }
    }
}

