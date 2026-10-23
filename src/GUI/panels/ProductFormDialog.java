package GUI.panels;

import DAO.IDanhMucDAO;
import GUI.common.CurrencyUtil;
import GUI.common.ImageUtil;
import GUI.common.ThemeConfig;
import GUI.common.ValidationUtil;
import Model.*;
import Service.IProductService;
import Service.UserSession;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.math.BigDecimal;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ProductFormDialog extends JDialog {
    private final IProductService productService;
    private final SanPham existingProduct;
    private boolean saved = false;

    private JTextField txtName;
    private JComboBox<TheLoai> cboCategory;
    private JComboBox<Co> cboSize;
    private JComboBox<ChatLieu> cboMaterial;
    private JComboBox<Mau> cboColor;
    private JComboBox<Mua> cboSeason;
    private JComboBox<DoiTuong> cboTarget;
    private JComboBox<NoiSanXuat> cboOrigin;
    private JTextField txtImportPrice;
    private JTextField txtSalePrice;
    private JTextField txtQuantity;
    private JTextField txtImagePath;
    private JLabel lblImagePreview;

    public ProductFormDialog(Frame parent, IProductService productService, SanPham existingProduct) {
        super(parent, existingProduct == null ? "Thêm sản phẩm mới" : "Chỉnh sửa thông tin sản phẩm", true);
        this.productService = productService;
        this.existingProduct = existingProduct;
        initUI();
        loadCategories();
        if (existingProduct != null) {
            populateData();
        }
    }

    public boolean isSaved() {
        return saved;
    }

    private void initUI() {
        setSize(820, 680);
        setMinimumSize(new Dimension(780, 620));
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout());
        getContentPane().setBackground(ThemeConfig.BG_MAIN);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ThemeConfig.SURFACE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeConfig.BORDER),
            BorderFactory.createEmptyBorder(18, 24, 18, 24)
        ));

        JLabel lblTitle = new JLabel(existingProduct == null ? "Thêm sản phẩm mới vào kho" : "Cập nhật sản phẩm #" + existingProduct.getMaQuanAo());
        lblTitle.setFont(ThemeConfig.FONT_TITLE);
        lblTitle.setForeground(ThemeConfig.TEXT_MAIN);
        headerPanel.add(lblTitle, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // Center Content: 2 Columns
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(ThemeConfig.BG_MAIN);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 8, 0, 8);

        // LEFT COLUMN: Attributes Form
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.65; gbc.weighty = 1.0;
        JPanel leftForm = createAttributesForm();
        contentPanel.add(leftForm, gbc);

        // RIGHT COLUMN: Image Preview & Picker
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.35; gbc.weighty = 1.0;
        JPanel rightImage = createImageSection();
        contentPanel.add(rightImage, gbc);

        JScrollPane scroll = new JScrollPane(contentPanel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        // Bottom Actions
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 16));
        bottomPanel.setBackground(ThemeConfig.SURFACE);
        bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ThemeConfig.BORDER));

        JButton btnCancel = new JButton("Hủy bỏ");
        ThemeConfig.styleSecondaryButton(btnCancel);
        btnCancel.addActionListener(e -> dispose());

        JButton btnSave = new JButton(existingProduct == null ? "Lưu sản phẩm" : "Cập nhật");
        ThemeConfig.stylePrimaryButton(btnSave);
        btnSave.addActionListener(e -> handleSave());

        bottomPanel.add(btnCancel);
        bottomPanel.add(btnSave);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createAttributesForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(ThemeConfig.SURFACE);
        form.setBorder(ThemeConfig.createCardBorder());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        txtName = new JTextField();
        cboCategory = new JComboBox<>();
        cboSize = new JComboBox<>();
        cboMaterial = new JComboBox<>();
        cboColor = new JComboBox<>();
        cboSeason = new JComboBox<>();
        cboTarget = new JComboBox<>();
        cboOrigin = new JComboBox<>();
        txtImportPrice = new JTextField();
        txtSalePrice = new JTextField();
        txtQuantity = new JTextField();

        // Check if user is Admin to show/edit import price
        boolean isAdmin = UserSession.getInstance().getRole() == LoaiTaiKhoan.ADMIN;
        if (!isAdmin) {
            txtImportPrice.setEnabled(false);
            txtImportPrice.setText("0");
        }

        addRow(form, gbc, 0, "Tên sản phẩm (*):", txtName);
        addRow(form, gbc, 1, "Thể loại:", cboCategory);
        addRow(form, gbc, 2, "Kích cỡ (Size):", cboSize);
        addRow(form, gbc, 3, "Chất liệu:", cboMaterial);
        addRow(form, gbc, 4, "Màu sắc:", cboColor);
        addRow(form, gbc, 5, "Mùa phù hợp:", cboSeason);
        addRow(form, gbc, 6, "Đối tượng:", cboTarget);
        addRow(form, gbc, 7, "Nơi sản xuất:", cboOrigin);
        addRow(form, gbc, 8, "Đơn giá nhập (₫):", txtImportPrice);
        addRow(form, gbc, 9, "Đơn giá bán (* ₫):", txtSalePrice);
        addRow(form, gbc, 10, "Số lượng trong kho:", txtQuantity);

        return form;
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, int row, String label, javax.swing.JComponent comp) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35;
        JLabel lbl = new JLabel(label);
        lbl.setFont(ThemeConfig.FONT_REGULAR);
        lbl.setForeground(ThemeConfig.TEXT_MAIN);
        panel.add(lbl, gbc);

        gbc.gridx = 1; gbc.weightx = 0.65;
        comp.setPreferredSize(new Dimension(comp.getPreferredSize().width, 34));
        panel.add(comp, gbc);
    }

    private JPanel createImageSection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(ThemeConfig.SURFACE);
        panel.setBorder(ThemeConfig.createCardBorder());

        JLabel lblTitle = new JLabel("Ảnh sản phẩm");
        lblTitle.setFont(ThemeConfig.FONT_HEADER);
        lblTitle.setForeground(ThemeConfig.TEXT_MAIN);
        lblTitle.setAlignmentX(0.5f);

        lblImagePreview = new JLabel();
        lblImagePreview.setPreferredSize(new Dimension(220, 240));
        lblImagePreview.setMaximumSize(new Dimension(220, 240));
        lblImagePreview.setAlignmentX(0.5f);
        lblImagePreview.setIcon(ImageUtil.createPlaceholder(220, 240, "No Image"));
        lblImagePreview.setBorder(BorderFactory.createLineBorder(ThemeConfig.BORDER, 1));

        txtImagePath = new JTextField();
        txtImagePath.setEditable(false);
        txtImagePath.setMaximumSize(new Dimension(220, 32));
        txtImagePath.setAlignmentX(0.5f);

        JButton btnChooseImage = new JButton("Chọn ảnh từ máy");
        ThemeConfig.styleSecondaryButton(btnChooseImage);
        btnChooseImage.setAlignmentX(0.5f);
        btnChooseImage.setMaximumSize(new Dimension(220, 36));
        btnChooseImage.addActionListener(e -> chooseImage());

        panel.add(lblTitle);
        panel.add(Box.createVerticalStrut(12));
        panel.add(lblImagePreview);
        panel.add(Box.createVerticalStrut(10));
        panel.add(txtImagePath);
        panel.add(Box.createVerticalStrut(10));
        panel.add(btnChooseImage);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private void loadCategories() {
        IDanhMucDAO dao = productService.getDanhMucDAO();
        if (dao == null) return;

        dao.getAllTheLoai().forEach(cboCategory::addItem);
        dao.getAllCo().forEach(cboSize::addItem);
        dao.getAllChatLieu().forEach(cboMaterial::addItem);
        dao.getAllMau().forEach(cboColor::addItem);
        dao.getAllMua().forEach(cboSeason::addItem);
        dao.getAllDoiTuong().forEach(cboTarget::addItem);
        dao.getAllNoiSanXuat().forEach(cboOrigin::addItem);
    }

    private void populateData() {
        txtName.setText(existingProduct.getTenQuanAo());
        if (existingProduct.getTheLoai() != null) selectCategoryItem(cboCategory, existingProduct.getTheLoai().getMaLoai());
        if (existingProduct.getCo() != null) selectSizeItem(cboSize, existingProduct.getCo().getMaCo());
        if (existingProduct.getChatLieu() != null) selectMaterialItem(cboMaterial, existingProduct.getChatLieu().getMaChatLieu());
        if (existingProduct.getMau() != null) selectColorItem(cboColor, existingProduct.getMau().getMaMau());
        if (existingProduct.getMua() != null) selectSeasonItem(cboSeason, existingProduct.getMua().getMaMua());
        if (existingProduct.getDoiTuong() != null) selectTargetItem(cboTarget, existingProduct.getDoiTuong().getMaDoiTuong());
        if (existingProduct.getNoiSanXuat() != null) selectOriginItem(cboOrigin, existingProduct.getNoiSanXuat().getMaNSX());

        txtImportPrice.setText(existingProduct.getDonGiaNhap() != null ? existingProduct.getDonGiaNhap().toPlainString() : "0");
        txtSalePrice.setText(existingProduct.getDonGiaBan() != null ? existingProduct.getDonGiaBan().toPlainString() : "0");
        txtQuantity.setText(String.valueOf(existingProduct.getSoLuong()));

        String img = existingProduct.getAnh();
        txtImagePath.setText(img != null ? img : "");
        lblImagePreview.setIcon(ImageUtil.getScaledImage(img, 220, 240));
    }

    private void selectCategoryItem(JComboBox<TheLoai> cbo, int id) {
        for (int i = 0; i < cbo.getItemCount(); i++) {
            if (cbo.getItemAt(i).getMaLoai() == id) { cbo.setSelectedIndex(i); break; }
        }
    }
    private void selectSizeItem(JComboBox<Co> cbo, int id) {
        for (int i = 0; i < cbo.getItemCount(); i++) {
            if (cbo.getItemAt(i).getMaCo() == id) { cbo.setSelectedIndex(i); break; }
        }
    }
    private void selectMaterialItem(JComboBox<ChatLieu> cbo, int id) {
        for (int i = 0; i < cbo.getItemCount(); i++) {
            if (cbo.getItemAt(i).getMaChatLieu() == id) { cbo.setSelectedIndex(i); break; }
        }
    }
    private void selectColorItem(JComboBox<Mau> cbo, int id) {
        for (int i = 0; i < cbo.getItemCount(); i++) {
            if (cbo.getItemAt(i).getMaMau() == id) { cbo.setSelectedIndex(i); break; }
        }
    }
    private void selectSeasonItem(JComboBox<Mua> cbo, int id) {
        for (int i = 0; i < cbo.getItemCount(); i++) {
            if (cbo.getItemAt(i).getMaMua() == id) { cbo.setSelectedIndex(i); break; }
        }
    }
    private void selectTargetItem(JComboBox<DoiTuong> cbo, int id) {
        for (int i = 0; i < cbo.getItemCount(); i++) {
            if (cbo.getItemAt(i).getMaDoiTuong() == id) { cbo.setSelectedIndex(i); break; }
        }
    }
    private void selectOriginItem(JComboBox<NoiSanXuat> cbo, int id) {
        for (int i = 0; i < cbo.getItemCount(); i++) {
            if (cbo.getItemAt(i).getMaNSX() == id) { cbo.setSelectedIndex(i); break; }
        }
    }

    private void chooseImage() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Hình ảnh (*.jpg, *.png, *.jpeg)", "jpg", "png", "jpeg"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            try {
                String savedPath = ImageUtil.copyImageToProject(selectedFile);
                txtImagePath.setText(savedPath);
                lblImagePreview.setIcon(ImageUtil.getScaledImage(savedPath, 220, 240));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Không thể sao chép ảnh: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleSave() {
        String name = txtName.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên sản phẩm không được để trống!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String importPriceStr = txtImportPrice.getText().trim();
        String salePriceStr = txtSalePrice.getText().trim();
        String qtyStr = txtQuantity.getText().trim();

        if (!ValidationUtil.isPositiveBigDecimal(salePriceStr)) {
            JOptionPane.showMessageDialog(this, "Đơn giá bán không hợp lệ (phải là số >= 0)!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!qtyStr.isEmpty() && !ValidationUtil.isNonNegativeInt(qtyStr)) {
            JOptionPane.showMessageDialog(this, "Số lượng không hợp lệ (phải là số nguyên >= 0)!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        BigDecimal importPrice = importPriceStr.isEmpty() ? BigDecimal.ZERO : CurrencyUtil.parse(importPriceStr);
        BigDecimal salePrice = CurrencyUtil.parse(salePriceStr);
        int qty = qtyStr.isEmpty() ? 0 : Integer.parseInt(qtyStr);

        SanPham sp = existingProduct == null ? new SanPham() : existingProduct;
        sp.setTenQuanAo(name);
        sp.setTheLoai((TheLoai) cboCategory.getSelectedItem());
        sp.setCo((Co) cboSize.getSelectedItem());
        sp.setChatLieu((ChatLieu) cboMaterial.getSelectedItem());
        sp.setMau((Mau) cboColor.getSelectedItem());
        sp.setMua((Mua) cboSeason.getSelectedItem());
        sp.setDoiTuong((DoiTuong) cboTarget.getSelectedItem());
        sp.setNoiSanXuat((NoiSanXuat) cboOrigin.getSelectedItem());
        sp.setDonGiaNhap(importPrice);
        sp.setDonGiaBan(salePrice);
        sp.setSoLuong(qty);
        sp.setAnh(txtImagePath.getText().trim());

        try {
            boolean result;
            if (existingProduct == null) {
                result = productService.addProduct(sp);
            } else {
                result = productService.updateProduct(sp);
            }

            if (result) {
                saved = true;
                JOptionPane.showMessageDialog(this, (existingProduct == null ? "Thêm" : "Cập nhật") + " sản phẩm thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Thao tác thất bại. Vui lòng kiểm tra lại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}

