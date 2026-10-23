package GUI.main;

import GUI.auth.LoginFrame;
import GUI.common.AppNavigator;
import GUI.common.ThemeConfig;
import GUI.panels.*;
import Model.LoaiTaiKhoan;
import Model.TaiKhoan;
import Service.IAuthService;
import Service.UserSession;
import Service.impl.AuthServiceImpl;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

public class MainDashboardFrame extends JFrame {
    private final IAuthService authService = new AuthServiceImpl();
    private final SimpleDateFormat clockFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    private JPanel sidebarMenuPanel;
    private JPanel contentCards;
    private CardLayout cardLayout;
    private JLabel lblModuleTitle;
    private JLabel lblClock;

    private final Map<String, JButton> menuButtons = new HashMap<>();

    // Panels
    private DashboardOverviewPanel pnlOverview;
    private ProductManagementPanel pnlProduct;
    private OrderManagementPanel pnlOrder;
    private ImportManagementPanel pnlImport;
    private StaffManagementPanel pnlStaff;
    private CustomerManagementPanel pnlCustomer;
    private ProfilePanel pnlProfile;
    private ShopGridPanel pnlShop;
    private CartPanel pnlCart;

    public MainDashboardFrame() {
        setTitle("Hệ thống Quản lý Cửa hàng Quần áo - Clothing Store Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setMinimumSize(new Dimension(1024, 680));
        setLocationRelativeTo(null);

        initUI();
        startClockTimer();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(ThemeConfig.BG_MAIN);

        // 1. LEFT SIDEBAR
        JPanel sidebar = createSidebar();
        root.add(sidebar, BorderLayout.WEST);

        // 2. RIGHT WRAPPER (Top Header + Center Cards)
        JPanel rightWrapper = new JPanel(new BorderLayout());
        rightWrapper.setBackground(ThemeConfig.BG_MAIN);

        JPanel header = createTopHeader();
        rightWrapper.add(header, BorderLayout.NORTH);

        cardLayout = new CardLayout();
        contentCards = new JPanel(cardLayout);
        contentCards.setBackground(ThemeConfig.BG_MAIN);

        buildRolePanels();

        rightWrapper.add(contentCards, BorderLayout.CENTER);
        root.add(rightWrapper, BorderLayout.CENTER);

        setContentPane(root);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(240, 800));
        sidebar.setBackground(ThemeConfig.SIDEBAR_BG);
        sidebar.setLayout(new BorderLayout());

        // TOP: Brand Logo & User Card
        JPanel topSection = new JPanel();
        topSection.setBackground(ThemeConfig.SIDEBAR_BG);
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
        topSection.setBorder(BorderFactory.createEmptyBorder(24, 20, 20, 20));

        JLabel lblBrand = new JLabel("CLOTHING STORE");
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 19));
        lblBrand.setForeground(Color.WHITE);
        lblBrand.setAlignmentX(0.0f);

        JLabel lblSub = new JLabel("Fashion Retail Management");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSub.setForeground(ThemeConfig.TEXT_MUTED);
        lblSub.setAlignmentX(0.0f);

        topSection.add(lblBrand);
        topSection.add(Box.createVerticalStrut(2));
        topSection.add(lblSub);
        topSection.add(Box.createVerticalStrut(20));

        // User Info Mini Card
        JPanel userCard = createUserMiniCard();
        topSection.add(userCard);

        sidebar.add(topSection, BorderLayout.NORTH);

        // CENTER: Dynamic Menu Items
        sidebarMenuPanel = new JPanel();
        sidebarMenuPanel.setBackground(ThemeConfig.SIDEBAR_BG);
        sidebarMenuPanel.setLayout(new BoxLayout(sidebarMenuPanel, BoxLayout.Y_AXIS));
        sidebarMenuPanel.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        sidebar.add(sidebarMenuPanel, BorderLayout.CENTER);

        // BOTTOM: Logout button
        JPanel bottomSection = new JPanel(new BorderLayout());
        bottomSection.setBackground(ThemeConfig.SIDEBAR_BG);
        bottomSection.setBorder(BorderFactory.createEmptyBorder(16, 16, 20, 16));

        JButton btnLogout = new JButton("Đăng xuất");
        btnLogout.setFont(ThemeConfig.FONT_BOLD);
        btnLogout.setBackground(new Color(185, 28, 28)); // Soft red
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        btnLogout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> handleLogout());

        bottomSection.add(btnLogout, BorderLayout.CENTER);
        sidebar.add(bottomSection, BorderLayout.SOUTH);

        return sidebar;
    }

    private JPanel createUserMiniCard() {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setBackground(new Color(30, 41, 59));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(51, 65, 85), 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        card.setAlignmentX(0.0f);

        TaiKhoan acc = UserSession.getInstance().getCurrentAccount();
        String username = acc != null ? acc.getTenDangNhap() : "Guest";
        String roleStr = "Khách Hàng";
        Color roleBadgeColor = ThemeConfig.SUCCESS;

        if (acc != null && acc.getLoaiTaiKhoan() != null) {
            switch (acc.getLoaiTaiKhoan()) {
                case ADMIN -> { roleStr = "Quản Trị Viên"; roleBadgeColor = ThemeConfig.DANGER; }
                case NHAN_VIEN -> { roleStr = "Nhân Viên"; roleBadgeColor = ThemeConfig.PRIMARY; }
                case KHACH_HANG -> { roleStr = "Khách Hàng"; roleBadgeColor = ThemeConfig.SUCCESS; }
            }
        }

        JPanel textWrap = new JPanel();
        textWrap.setBackground(new Color(30, 41, 59));
        textWrap.setLayout(new BoxLayout(textWrap, BoxLayout.Y_AXIS));

        JLabel lblName = new JLabel(username);
        lblName.setFont(ThemeConfig.FONT_HEADER);
        lblName.setForeground(Color.WHITE);

        JLabel lblRole = new JLabel(roleStr);
        lblRole.setFont(ThemeConfig.FONT_SMALL_BOLD);
        lblRole.setForeground(roleBadgeColor);

        textWrap.add(lblName);
        textWrap.add(Box.createVerticalStrut(2));
        textWrap.add(lblRole);

        card.add(textWrap, BorderLayout.CENTER);
        return card;
    }

    private JPanel createTopHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ThemeConfig.SURFACE);
        header.setPreferredSize(new Dimension(800, 60));
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeConfig.BORDER),
            BorderFactory.createEmptyBorder(0, 24, 0, 24)
        ));

        lblModuleTitle = new JLabel("Tổng quan hệ thống");
        lblModuleTitle.setFont(ThemeConfig.FONT_SUBTITLE);
        lblModuleTitle.setForeground(ThemeConfig.TEXT_MAIN);

        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 18));
        rightHeader.setBackground(ThemeConfig.SURFACE);

        lblClock = new JLabel(clockFormat.format(new Date()));
        lblClock.setFont(ThemeConfig.FONT_REGULAR);
        lblClock.setForeground(ThemeConfig.TEXT_MUTED);

        rightHeader.add(lblClock);

        header.add(lblModuleTitle, BorderLayout.WEST);
        header.add(rightHeader, BorderLayout.EAST);
        return header;
    }

    private void buildRolePanels() {
        LoaiTaiKhoan role = UserSession.getInstance().getRole();
        if (role == null) role = LoaiTaiKhoan.ADMIN;

        pnlProfile = new ProfilePanel();

        switch (role) {
            case ADMIN -> {
                pnlOverview = new DashboardOverviewPanel();
                pnlProduct = new ProductManagementPanel();
                pnlOrder = new OrderManagementPanel();
                pnlImport = new ImportManagementPanel();
                pnlStaff = new StaffManagementPanel();
                pnlCustomer = new CustomerManagementPanel();

                contentCards.add(pnlOverview, "OVERVIEW");
                contentCards.add(pnlProduct, "PRODUCT");
                contentCards.add(pnlOrder, "ORDER");
                contentCards.add(pnlImport, "IMPORT");
                contentCards.add(pnlStaff, "STAFF");
                contentCards.add(pnlCustomer, "CUSTOMER");
                contentCards.add(pnlProfile, "PROFILE");

                addMenuItem("Tổng quan", "OVERVIEW");
                addMenuItem("Quản lý Sản phẩm", "PRODUCT");
                addMenuItem("Quản lý Đơn hàng", "ORDER");
                addMenuItem("Quản lý Nhập kho", "IMPORT");
                addMenuItem("Quản lý Nhân viên", "STAFF");
                addMenuItem("Quản lý Khách hàng", "CUSTOMER");
                addMenuItem("Hồ sơ cá nhân", "PROFILE");

                switchTab("OVERVIEW", "Tổng quan hệ thống");
            }
            case NHAN_VIEN -> {
                pnlOrder = new OrderManagementPanel();
                pnlProduct = new ProductManagementPanel();
                pnlImport = new ImportManagementPanel();
                pnlOverview = new DashboardOverviewPanel();

                contentCards.add(pnlOrder, "ORDER");
                contentCards.add(pnlProduct, "PRODUCT");
                contentCards.add(pnlImport, "IMPORT");
                contentCards.add(pnlOverview, "LOW_STOCK");
                contentCards.add(pnlProfile, "PROFILE");

                addMenuItem("Bán hàng / Đơn hàng", "ORDER");
                addMenuItem("Danh mục Sản phẩm", "PRODUCT");
                addMenuItem("Quản lý Nhập kho", "IMPORT");
                addMenuItem("Cảnh báo tồn kho", "LOW_STOCK");
                addMenuItem("Hồ sơ cá nhân", "PROFILE");

                switchTab("ORDER", "Bán hàng & Quản lý đơn hàng");
            }
            case KHACH_HANG -> {
                pnlShop = new ShopGridPanel();
                pnlCart = new CartPanel();
                pnlOrder = new OrderManagementPanel();

                contentCards.add(pnlShop, "SHOP");
                contentCards.add(pnlCart, "CART");
                contentCards.add(pnlOrder, "ORDER_HISTORY");
                contentCards.add(pnlProfile, "PROFILE");

                addMenuItem("Mua sắm sản phẩm", "SHOP");
                addMenuItem("Giỏ hàng của tôi", "CART");
                addMenuItem("Lịch sử đơn mua", "ORDER_HISTORY");
                addMenuItem("Hồ sơ cá nhân", "PROFILE");

                switchTab("SHOP", "Danh mục mua sắm thời trang");
            }
        }
    }

    private void addMenuItem(String title, String cardKey) {
        JButton btn = new JButton(title);
        btn.setFont(ThemeConfig.FONT_REGULAR);
        btn.setForeground(new Color(203, 213, 225));
        btn.setBackground(ThemeConfig.SIDEBAR_BG);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        btn.setMaximumSize(new Dimension(216, 42));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e -> switchTab(cardKey, title));

        menuButtons.put(cardKey, btn);
        sidebarMenuPanel.add(btn);
        sidebarMenuPanel.add(Box.createVerticalStrut(4));
    }

    private void switchTab(String cardKey, String title) {
        lblModuleTitle.setText(title);

        for (Map.Entry<String, JButton> entry : menuButtons.entrySet()) {
            JButton btn = entry.getValue();
            if (entry.getKey().equals(cardKey)) {
                btn.setBackground(ThemeConfig.SIDEBAR_ACTIVE);
                btn.setForeground(Color.WHITE);
                btn.setFont(ThemeConfig.FONT_BOLD);
            } else {
                btn.setBackground(ThemeConfig.SIDEBAR_BG);
                btn.setForeground(new Color(203, 213, 225));
                btn.setFont(ThemeConfig.FONT_REGULAR);
            }
        }

        cardLayout.show(contentCards, cardKey);

        // Auto reload data when switching tabs
        if ("OVERVIEW".equals(cardKey) && pnlOverview != null) pnlOverview.loadDashboardData();
        if ("PRODUCT".equals(cardKey) && pnlProduct != null) pnlProduct.loadData();
        if ("ORDER".equals(cardKey) && pnlOrder != null) pnlOrder.loadOrders();
        if ("ORDER_HISTORY".equals(cardKey) && pnlOrder != null) pnlOrder.loadOrders();
        if ("CART".equals(cardKey) && pnlCart != null) pnlCart.loadCart();
        if ("SHOP".equals(cardKey) && pnlShop != null) pnlShop.loadProducts();
    }

    private void startClockTimer() {
        Timer timer = new Timer(1000, e -> lblClock.setText(clockFormat.format(new Date())));
        timer.start();
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn đăng xuất khỏi hệ thống?",
            "Xác nhận đăng xuất", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            String maTK = UserSession.getInstance().getMaTaiKhoan();
            authService.logout(maTK);
            AppNavigator.switchWindow(this, new LoginFrame());
        }
    }
}

