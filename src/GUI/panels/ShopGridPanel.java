package GUI.panels;

import DAO.IDanhMucDAO;
import GUI.common.CurrencyUtil;
import GUI.common.ImageUtil;
import GUI.common.ThemeConfig;
import Model.*;
import Service.IOrderService;
import Service.IProductService;
import Service.UserSession;
import Service.impl.OrderServiceImpl;
import Service.impl.ProductServiceImpl;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class ShopGridPanel extends JPanel {
    private final IProductService productService = new ProductServiceImpl();
    private final IOrderService orderService = new OrderServiceImpl();

    private List<SanPham> productList = new ArrayList<>();
    private JPanel gridContainer;
    private JTextField txtSearch;
    private JComboBox<TheLoai> cboCategory;
    private JComboBox<Co> cboSize;

    public ShopGridPanel() {
        setLayout(new BorderLayout(0, 12));
        setBackground(ThemeConfig.BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        initToolbar();
        initGrid();
        loadCategories();
        loadProducts();
    }

    private void initToolbar() {
        JPanel toolbarCard = new JPanel(new BorderLayout(12, 10));
        toolbarCard.setBackground(ThemeConfig.SURFACE);
        toolbarCard.setBorder(ThemeConfig.createCardBorder());

        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        filterRow.setBackground(ThemeConfig.SURFACE);

        JLabel lblSearch = new JLabel("Tìm sản phẩm:");
        lblSearch.setFont(ThemeConfig.FONT_BOLD);
        txtSearch = new JTextField(14);
        txtSearch.setPreferredSize(new Dimension(160, 36));

        cboCategory = new JComboBox<>();
        cboCategory.setPreferredSize(new Dimension(140, 36));

        cboSize = new JComboBox<>();
        cboSize.setPreferredSize(new Dimension(100, 36));

        JButton btnFilter = new JButton("Tìm kiếm");
        ThemeConfig.stylePrimaryButton(btnFilter);
        btnFilter.addActionListener(e -> applyFilter());

        JButton btnReset = new JButton("Tất cả");
        ThemeConfig.styleSecondaryButton(btnReset);
        btnReset.addActionListener(e -> resetFilter());

        filterRow.add(lblSearch);
        filterRow.add(txtSearch);
        filterRow.add(cboCategory);
        filterRow.add(cboSize);
        filterRow.add(btnFilter);
        filterRow.add(btnReset);

        toolbarCard.add(filterRow, BorderLayout.WEST);
        add(toolbarCard, BorderLayout.NORTH);
    }

    private void initGrid() {
        gridContainer = new JPanel();
        gridContainer.setBackground(ThemeConfig.BG_MAIN);
        gridContainer.setLayout(new FlowLayout(FlowLayout.LEFT, 16, 16));

        JScrollPane scroll = new JScrollPane(gridContainer);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }

    private void loadCategories() {
        IDanhMucDAO dao = productService.getDanhMucDAO();
        if (dao == null) return;

        cboCategory.addItem(new TheLoai(0, "Tất cả thể loại"));
        dao.getAllTheLoai().forEach(cboCategory::addItem);

        cboSize.addItem(new Co(0, "Tất cả size"));
        dao.getAllCo().forEach(cboSize::addItem);
    }

    public void loadProducts() {
        productList = productService.getAllProducts();
        renderCards(productList);
    }

    private void renderCards(List<SanPham> list) {
        gridContainer.removeAll();

        if (list.isEmpty()) {
            JLabel lblEmpty = new JLabel("Không tìm thấy sản phẩm nào phù hợp.");
            lblEmpty.setFont(ThemeConfig.FONT_HEADER);
            lblEmpty.setForeground(ThemeConfig.TEXT_MUTED);
            gridContainer.add(lblEmpty);
        } else {
            for (SanPham sp : list) {
                JPanel card = createProductCard(sp);
                gridContainer.add(card);
            }
        }

        gridContainer.revalidate();
        gridContainer.repaint();
    }

    private JPanel createProductCard(SanPham sp) {
        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(220, 330));
        card.setBackground(ThemeConfig.SURFACE);
        card.setBorder(ThemeConfig.createCardBorder());
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        // Image
        JLabel lblImg = new JLabel();
        lblImg.setPreferredSize(new Dimension(190, 160));
        lblImg.setMaximumSize(new Dimension(190, 160));
        lblImg.setAlignmentX(0.5f);
        lblImg.setIcon(ImageUtil.getScaledImage(sp.getAnh(), 180, 150));
        lblImg.setBorder(BorderFactory.createLineBorder(ThemeConfig.BORDER, 1));

        // Name
        JLabel lblName = new JLabel("<html><center><b>" + sp.getTenQuanAo() + "</b></center></html>");
        lblName.setFont(ThemeConfig.FONT_REGULAR);
        lblName.setForeground(ThemeConfig.TEXT_MAIN);
        lblName.setAlignmentX(0.5f);
        lblName.setPreferredSize(new Dimension(190, 36));

        // Attributes pill
        String attrText = (sp.getCo() != null ? "Size " + sp.getCo().getTenCo() : "") +
            (sp.getMau() != null ? " • " + sp.getMau().getTenMau() : "");
        JLabel lblAttr = new JLabel(attrText);
        lblAttr.setFont(ThemeConfig.FONT_SMALL);
        lblAttr.setForeground(ThemeConfig.TEXT_MUTED);
        lblAttr.setAlignmentX(0.5f);

        // Price
        JLabel lblPrice = new JLabel(CurrencyUtil.format(sp.getDonGiaBan()));
        lblPrice.setFont(ThemeConfig.FONT_SUBTITLE);
        lblPrice.setForeground(ThemeConfig.PRIMARY);
        lblPrice.setAlignmentX(0.5f);

        // Add to cart button
        JButton btnAddCart = new JButton(sp.getSoLuong() > 0 ? "Thêm vào giỏ" : "Hết hàng");
        ThemeConfig.stylePrimaryButton(btnAddCart);
        btnAddCart.setMaximumSize(new Dimension(190, 36));
        btnAddCart.setAlignmentX(0.5f);
        btnAddCart.setEnabled(sp.getSoLuong() > 0);
        btnAddCart.addActionListener(e -> addToCart(sp));

        card.add(lblImg);
        card.add(Box.createVerticalStrut(8));
        card.add(lblName);
        card.add(Box.createVerticalStrut(4));
        card.add(lblAttr);
        card.add(Box.createVerticalStrut(6));
        card.add(lblPrice);
        card.add(Box.createVerticalStrut(8));
        card.add(btnAddCart);

        return card;
    }

    private void addToCart(SanPham sp) {
        String maKH = UserSession.getInstance().getMaTaiKhoan();
        if (maKH == null || maKH.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng đăng nhập để thêm sản phẩm vào giỏ hàng!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            orderService.themVaoGioHang(maKH, sp.getMaQuanAo(), 1);
            JOptionPane.showMessageDialog(this, "Đã thêm \"" + sp.getTenQuanAo() + "\" vào giỏ hàng!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void applyFilter() {
        String kw = txtSearch.getText().trim();
        TheLoai cat = (TheLoai) cboCategory.getSelectedItem();
        Co size = (Co) cboSize.getSelectedItem();

        Integer maLoai = (cat != null && cat.getMaLoai() > 0) ? cat.getMaLoai() : null;
        Integer maCo = (size != null && size.getMaCo() > 0) ? size.getMaCo() : null;

        List<SanPham> filtered = productService.searchAndFilter(kw, maLoai, maCo, null, null, null, null, null);
        renderCards(filtered);
    }

    private void resetFilter() {
        txtSearch.setText("");
        cboCategory.setSelectedIndex(0);
        cboSize.setSelectedIndex(0);
        loadProducts();
    }
}

