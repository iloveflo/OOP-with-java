package GUI.panels;

import DAO.IDanhMucDAO;
import GUI.common.CurrencyUtil;
import GUI.common.ImageUtil;
import GUI.common.ThemeConfig;
import Model.*;
import Service.IProductService;
import Service.UserSession;
import Service.impl.ProductServiceImpl;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ProductManagementPanel extends JPanel {
    private final IProductService productService = new ProductServiceImpl();
    private final boolean isAdmin;
    private List<SanPham> currentProductList = new ArrayList<>();

    // Toolbar
    private JTextField txtSearch;
    private JComboBox<TheLoai> cboCategory;
    private JComboBox<Co> cboSize;
    private JComboBox<Mau> cboColor;
    private JComboBox<DoiTuong> cboTarget;
    private JButton btnAdd;
    private JButton btnEdit;
    private JButton btnDelete;
    private JButton btnExportExcel;

    // Table
    private JTable table;
    private DefaultTableModel tableModel;

    public ProductManagementPanel() {
        this.isAdmin = UserSession.getInstance().getRole() == LoaiTaiKhoan.ADMIN;
        setLayout(new BorderLayout(0, 12));
        setBackground(ThemeConfig.BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        initToolbar();
        initTable();
        loadCategories();
        loadData();
    }

    private void initToolbar() {
        JPanel toolbarCard = new JPanel(new BorderLayout(12, 10));
        toolbarCard.setBackground(ThemeConfig.SURFACE);
        toolbarCard.setBorder(ThemeConfig.createCardBorder());

        // TOP ROW: Search & Filters
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        filterRow.setBackground(ThemeConfig.SURFACE);

        JLabel lblSearch = new JLabel("Tìm kiếm:");
        lblSearch.setFont(ThemeConfig.FONT_BOLD);
        txtSearch = new JTextField(12);
        txtSearch.setPreferredSize(new Dimension(140, 36));

        cboCategory = new JComboBox<>();
        cboCategory.setPreferredSize(new Dimension(130, 36));
        cboSize = new JComboBox<>();
        cboSize.setPreferredSize(new Dimension(90, 36));
        cboColor = new JComboBox<>();
        cboColor.setPreferredSize(new Dimension(100, 36));
        cboTarget = new JComboBox<>();
        cboTarget.setPreferredSize(new Dimension(100, 36));

        JButton btnFilter = new JButton("Lọc");
        ThemeConfig.stylePrimaryButton(btnFilter);
        btnFilter.addActionListener(e -> applyFilter());

        JButton btnReset = new JButton("Đặt lại");
        ThemeConfig.styleSecondaryButton(btnReset);
        btnReset.addActionListener(e -> resetFilter());

        filterRow.add(lblSearch);
        filterRow.add(txtSearch);
        filterRow.add(cboCategory);
        filterRow.add(cboSize);
        filterRow.add(cboColor);
        filterRow.add(cboTarget);
        filterRow.add(btnFilter);
        filterRow.add(btnReset);

        // RIGHT ACTIONS
        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        actionRow.setBackground(ThemeConfig.SURFACE);

        if (isAdmin) {
            btnAdd = new JButton("+ Thêm mới");
            ThemeConfig.stylePrimaryButton(btnAdd);
            btnAdd.addActionListener(e -> openAddDialog());
            actionRow.add(btnAdd);

            btnEdit = new JButton("Sửa");
            ThemeConfig.styleSecondaryButton(btnEdit);
            btnEdit.setEnabled(false);
            btnEdit.addActionListener(e -> openEditDialog());
            actionRow.add(btnEdit);

            btnDelete = new JButton("Xóa");
            ThemeConfig.styleDangerButton(btnDelete);
            btnDelete.setEnabled(false);
            btnDelete.addActionListener(e -> deleteSelectedProduct());
            actionRow.add(btnDelete);
        }

        btnExportExcel = new JButton("Xuất Excel");
        ThemeConfig.styleSecondaryButton(btnExportExcel);
        btnExportExcel.addActionListener(e -> exportToExcel());
        actionRow.add(btnExportExcel);

        toolbarCard.add(filterRow, BorderLayout.WEST);
        toolbarCard.add(actionRow, BorderLayout.EAST);
        add(toolbarCard, BorderLayout.NORTH);
    }

    private void initTable() {
        String[] columns;
        if (isAdmin) {
            columns = new String[]{"Mã", "Ảnh", "Tên sản phẩm", "Thể loại", "Size", "Màu", "Số lượng", "Giá bán", "Giá nhập", "Trạng thái"};
        } else {
            columns = new String[]{"Mã", "Ảnh", "Tên sản phẩm", "Thể loại", "Size", "Màu", "Số lượng", "Giá bán", "Trạng thái"};
        }

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 1 ? javax.swing.Icon.class : Object.class;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(48);
        table.setShowGrid(true);
        table.setGridColor(ThemeConfig.BORDER);
        table.setSelectionBackground(ThemeConfig.PRIMARY_LIGHT);
        table.setSelectionForeground(ThemeConfig.TEXT_MAIN);

        // Center align columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(1).setPreferredWidth(60);

        table.getSelectionModel().addListSelectionListener(e -> {
            boolean selected = table.getSelectedRow() != -1;
            if (btnEdit != null) btnEdit.setEnabled(selected);
            if (btnDelete != null) btnDelete.setEnabled(selected);
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(ThemeConfig.BORDER, 1));
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadCategories() {
        IDanhMucDAO dao = productService.getDanhMucDAO();
        if (dao == null) return;

        cboCategory.addItem(new TheLoai(0, "Tất cả loại"));
        dao.getAllTheLoai().forEach(cboCategory::addItem);

        cboSize.addItem(new Co(0, "Tất cả size"));
        dao.getAllCo().forEach(cboSize::addItem);

        cboColor.addItem(new Mau(0, "Tất cả màu"));
        dao.getAllMau().forEach(cboColor::addItem);

        cboTarget.addItem(new DoiTuong(0, "Tất cả ĐT"));
        dao.getAllDoiTuong().forEach(cboTarget::addItem);
    }

    public void loadData() {
        currentProductList = productService.getAllProducts();
        renderTable(currentProductList);
    }

    private void renderTable(List<SanPham> list) {
        tableModel.setRowCount(0);
        for (SanPham sp : list) {
            String status;
            if (sp.getSoLuong() <= 0) {
                status = "Hết hàng";
            } else if (sp.getSoLuong() <= 10) {
                status = "Sắp hết (" + sp.getSoLuong() + ")";
            } else {
                status = "Còn hàng";
            }

            javax.swing.ImageIcon thumbnail = ImageUtil.getScaledImage(sp.getAnh(), 40, 40);

            if (isAdmin) {
                tableModel.addRow(new Object[]{
                    sp.getMaQuanAo(),
                    thumbnail,
                    sp.getTenQuanAo(),
                    sp.getTheLoai() != null ? sp.getTheLoai().getTenLoai() : "-",
                    sp.getCo() != null ? sp.getCo().getTenCo() : "-",
                    sp.getMau() != null ? sp.getMau().getTenMau() : "-",
                    sp.getSoLuong(),
                    CurrencyUtil.format(sp.getDonGiaBan()),
                    CurrencyUtil.format(sp.getDonGiaNhap()),
                    status
                });
            } else {
                tableModel.addRow(new Object[]{
                    sp.getMaQuanAo(),
                    thumbnail,
                    sp.getTenQuanAo(),
                    sp.getTheLoai() != null ? sp.getTheLoai().getTenLoai() : "-",
                    sp.getCo() != null ? sp.getCo().getTenCo() : "-",
                    sp.getMau() != null ? sp.getMau().getTenMau() : "-",
                    sp.getSoLuong(),
                    CurrencyUtil.format(sp.getDonGiaBan()),
                    status
                });
            }
        }
    }

    private void applyFilter() {
        String keyword = txtSearch.getText().trim();
        TheLoai cat = (TheLoai) cboCategory.getSelectedItem();
        Co size = (Co) cboSize.getSelectedItem();
        Mau color = (Mau) cboColor.getSelectedItem();
        DoiTuong target = (DoiTuong) cboTarget.getSelectedItem();

        Integer maLoai = (cat != null && cat.getMaLoai() > 0) ? cat.getMaLoai() : null;
        Integer maCo = (size != null && size.getMaCo() > 0) ? size.getMaCo() : null;
        Integer maMau = (color != null && color.getMaMau() > 0) ? color.getMaMau() : null;
        Integer maDT = (target != null && target.getMaDoiTuong() > 0) ? target.getMaDoiTuong() : null;

        currentProductList = productService.searchAndFilter(keyword, maLoai, maCo, null, maDT, maMau, null, null);
        renderTable(currentProductList);
    }

    private void resetFilter() {
        txtSearch.setText("");
        cboCategory.setSelectedIndex(0);
        cboSize.setSelectedIndex(0);
        cboColor.setSelectedIndex(0);
        cboTarget.setSelectedIndex(0);
        loadData();
    }

    private void openAddDialog() {
        Frame topFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        ProductFormDialog dialog = new ProductFormDialog(topFrame, productService, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loadData();
        }
    }

    private void openEditDialog() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= currentProductList.size()) return;

        SanPham selected = currentProductList.get(row);
        Frame topFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        ProductFormDialog dialog = new ProductFormDialog(topFrame, productService, selected);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loadData();
        }
    }

    private void deleteSelectedProduct() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= currentProductList.size()) return;

        SanPham selected = currentProductList.get(row);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn xóa sản phẩm \"" + selected.getTenQuanAo() + "\" (Mã: " + selected.getMaQuanAo() + ")?",
            "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = productService.deleteProduct(selected.getMaQuanAo());
            if (ok) {
                JOptionPane.showMessageDialog(this, "Đã xóa sản phẩm thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Không thể xóa sản phẩm (có thể sản phẩm đã có trong hóa đơn)!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportToExcel() {
        if (currentProductList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Danh sách trống, không có dữ liệu để xuất!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("Danh_sach_san_pham.xlsx"));
        chooser.setFileFilter(new FileNameExtensionFilter("Excel Workbook (*.xlsx)", "xlsx"));

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (!file.getName().endsWith(".xlsx")) {
                file = new File(file.getAbsolutePath() + ".xlsx");
            }

            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Sản phẩm");
                Row header = sheet.createRow(0);
                header.createCell(0).setCellValue("Mã SP");
                header.createCell(1).setCellValue("Tên sản phẩm");
                header.createCell(2).setCellValue("Thể loại");
                header.createCell(3).setCellValue("Kích cỡ");
                header.createCell(4).setCellValue("Màu");
                header.createCell(5).setCellValue("Số lượng");
                header.createCell(6).setCellValue("Đơn giá bán");
                if (isAdmin) header.createCell(7).setCellValue("Đơn giá nhập");

                int rowIdx = 1;
                for (SanPham sp : currentProductList) {
                    Row r = sheet.createRow(rowIdx++);
                    r.createCell(0).setCellValue(sp.getMaQuanAo());
                    r.createCell(1).setCellValue(sp.getTenQuanAo());
                    r.createCell(2).setCellValue(sp.getTheLoai() != null ? sp.getTheLoai().getTenLoai() : "");
                    r.createCell(3).setCellValue(sp.getCo() != null ? sp.getCo().getTenCo() : "");
                    r.createCell(4).setCellValue(sp.getMau() != null ? sp.getMau().getTenMau() : "");
                    r.createCell(5).setCellValue(sp.getSoLuong());
                    r.createCell(6).setCellValue(sp.getDonGiaBan() != null ? sp.getDonGiaBan().doubleValue() : 0);
                    if (isAdmin) r.createCell(7).setCellValue(sp.getDonGiaNhap() != null ? sp.getDonGiaNhap().doubleValue() : 0);
                }

                for (int i = 0; i <= (isAdmin ? 7 : 6); i++) {
                    sheet.autoSizeColumn(i);
                }

                try (FileOutputStream fos = new FileOutputStream(file)) {
                    workbook.write(fos);
                }

                JOptionPane.showMessageDialog(this, "Xuất file Excel thành công: " + file.getName(), "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi xuất Excel: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

