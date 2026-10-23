package GUI.panels;

import GUI.common.CurrencyUtil;
import GUI.common.ThemeConfig;
import Model.ChiTietHoaDon;
import Model.HoaDonNhap;
import Service.IImportService;
import Service.IReportService;
import Service.impl.ImportServiceImpl;
import Service.impl.ReportServiceImpl;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.text.SimpleDateFormat;
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

public class ImportManagementPanel extends JPanel {
    private final IImportService importService = new ImportServiceImpl();
    private final IReportService reportService = new ReportServiceImpl();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    private List<HoaDonNhap> importList = new ArrayList<>();
    private JTable tblImports;
    private DefaultTableModel modelImports;
    private JTextField txtSearch;
    private JButton btnAddImport;
    private JButton btnViewDetails;
    private JButton btnExport;

    public ImportManagementPanel() {
        setLayout(new BorderLayout(0, 12));
        setBackground(ThemeConfig.BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        initToolbar();
        initTable();
        loadImports();
    }

    private void initToolbar() {
        JPanel toolbarCard = new JPanel(new BorderLayout(12, 10));
        toolbarCard.setBackground(ThemeConfig.SURFACE);
        toolbarCard.setBorder(ThemeConfig.createCardBorder());

        // Left
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        searchPanel.setBackground(ThemeConfig.SURFACE);

        JLabel lblSearch = new JLabel("Tìm kiếm phiếu:");
        lblSearch.setFont(ThemeConfig.FONT_BOLD);
        txtSearch = new JTextField(14);
        txtSearch.setPreferredSize(new Dimension(150, 36));

        JButton btnSearch = new JButton("Tìm");
        ThemeConfig.stylePrimaryButton(btnSearch);
        btnSearch.addActionListener(e -> filterImports());

        JButton btnRefresh = new JButton("Làm mới");
        ThemeConfig.styleSecondaryButton(btnRefresh);
        btnRefresh.addActionListener(e -> loadImports());

        searchPanel.add(lblSearch);
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnRefresh);

        // Right
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        actionPanel.setBackground(ThemeConfig.SURFACE);

        btnAddImport = new JButton("+ Nhập hàng mới");
        ThemeConfig.stylePrimaryButton(btnAddImport);
        btnAddImport.addActionListener(e -> openImportCreationDialog());
        actionPanel.add(btnAddImport);

        btnViewDetails = new JButton("Xem chi tiết");
        ThemeConfig.styleSecondaryButton(btnViewDetails);
        btnViewDetails.setEnabled(false);
        btnViewDetails.addActionListener(e -> openDetailsDialog());
        actionPanel.add(btnViewDetails);

        btnExport = new JButton("In phiếu nhập");
        ThemeConfig.styleSecondaryButton(btnExport);
        btnExport.setEnabled(false);
        btnExport.addActionListener(e -> exportCurrentImport());
        actionPanel.add(btnExport);

        toolbarCard.add(searchPanel, BorderLayout.WEST);
        toolbarCard.add(actionPanel, BorderLayout.EAST);
        add(toolbarCard, BorderLayout.NORTH);
    }

    private void initTable() {
        String[] cols = {"Số HĐN", "Ngày nhập", "Nhà cung cấp", "Nhân viên nhập", "Tổng tiền nhập"};
        modelImports = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tblImports = new JTable(modelImports);
        tblImports.setRowHeight(38);
        tblImports.setShowGrid(true);
        tblImports.setGridColor(ThemeConfig.BORDER);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tblImports.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tblImports.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);

        tblImports.getSelectionModel().addListSelectionListener(e -> {
            boolean selected = tblImports.getSelectedRow() != -1;
            btnViewDetails.setEnabled(selected);
            btnExport.setEnabled(selected);
        });

        JScrollPane scroll = new JScrollPane(tblImports);
        scroll.setBorder(BorderFactory.createLineBorder(ThemeConfig.BORDER, 1));
        add(scroll, BorderLayout.CENTER);
    }

    public void loadImports() {
        txtSearch.setText("");
        importList = importService.getAllHoaDonNhap();
        renderTable(importList);
    }

    private void renderTable(List<HoaDonNhap> list) {
        modelImports.setRowCount(0);
        for (HoaDonNhap hdn : list) {
            String ngay = hdn.getNgayNhap() != null ? dateFormat.format(hdn.getNgayNhap()) : "-";
            String ncc = hdn.getNhaCungCap() != null ? hdn.getNhaCungCap().getTenNCC() : "-";
            String nv = hdn.getNhanVien() != null && hdn.getNhanVien().getHoTen() != null ? hdn.getNhanVien().getHoTen() : (hdn.getNhanVien() != null ? hdn.getNhanVien().getMaNhanVien() : "-");

            modelImports.addRow(new Object[]{
                hdn.getSoHoaDon(),
                ngay,
                ncc,
                nv,
                CurrencyUtil.format(hdn.getTongTien())
            });
        }
    }

    private void filterImports() {
        String kw = txtSearch.getText().trim().toLowerCase();
        if (kw.isEmpty()) {
            renderTable(importList);
            return;
        }

        List<HoaDonNhap> filtered = new ArrayList<>();
        for (HoaDonNhap hdn : importList) {
            String so = String.valueOf(hdn.getSoHoaDon());
            String ncc = hdn.getNhaCungCap() != null ? hdn.getNhaCungCap().getTenNCC().toLowerCase() : "";
            if (so.contains(kw) || ncc.contains(kw)) {
                filtered.add(hdn);
            }
        }
        renderTable(filtered);
    }

    private void openImportCreationDialog() {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        ImportCreationDialog dialog = new ImportCreationDialog(parent);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loadImports();
        }
    }

    private void openDetailsDialog() {
        int row = tblImports.getSelectedRow();
        if (row < 0 || row >= importList.size()) return;

        HoaDonNhap hdn = importList.get(row);
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);

        JDialog detailsDialog = new JDialog(parent, "Chi tiết phiếu nhập #" + hdn.getSoHoaDon(), true);
        detailsDialog.setSize(680, 440);
        detailsDialog.setLocationRelativeTo(parent);
        detailsDialog.setLayout(new BorderLayout());

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 12));
        header.setBackground(ThemeConfig.SURFACE);
        header.add(new JLabel("Số HĐN: " + hdn.getSoHoaDon()));
        header.add(new JLabel("• Nhà CC: " + (hdn.getNhaCungCap() != null ? hdn.getNhaCungCap().getTenNCC() : "-")));
        header.add(new JLabel("• Tổng: " + CurrencyUtil.format(hdn.getTongTien())));
        detailsDialog.add(header, BorderLayout.NORTH);

        String[] cols = {"Mã SP", "Tên sản phẩm", "Số lượng", "Đơn giá nhập", "Giảm giá (%)", "Thành tiền"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        table.setRowHeight(32);

        if (hdn.getDanhSachChiTiet() != null) {
            for (ChiTietHoaDon ct : hdn.getDanhSachChiTiet()) {
                model.addRow(new Object[]{
                    ct.getSanPham() != null ? ct.getSanPham().getMaQuanAo() : "-",
                    ct.getSanPham() != null ? ct.getSanPham().getTenQuanAo() : "-",
                    ct.getSoLuong(),
                    CurrencyUtil.format(ct.getDonGia()),
                    ct.getGiamGia(),
                    CurrencyUtil.format(ct.getThanhTien())
                });
            }
        }

        detailsDialog.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        footer.setBackground(ThemeConfig.SURFACE);
        JButton btnClose = new JButton("Đóng");
        ThemeConfig.styleSecondaryButton(btnClose);
        btnClose.addActionListener(e -> detailsDialog.dispose());
        footer.add(btnClose);
        detailsDialog.add(footer, BorderLayout.SOUTH);

        detailsDialog.setVisible(true);
    }

    private void exportCurrentImport() {
        int row = tblImports.getSelectedRow();
        if (row < 0 || row >= importList.size()) return;

        HoaDonNhap hdn = importList.get(row);
        try {
            reportService.exportHoaDonNhap(String.valueOf(hdn.getSoHoaDon()));
            JOptionPane.showMessageDialog(this, "Đã xuất phiếu nhập #" + hdn.getSoHoaDon() + " ra file Excel!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi xuất file: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}

