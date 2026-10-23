package GUI.panels;

import GUI.common.CurrencyUtil;
import GUI.common.ThemeConfig;
import Model.*;
import Service.IOrderService;
import Service.IReportService;
import Service.UserSession;
import Service.impl.OrderServiceImpl;
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

public class OrderManagementPanel extends JPanel {
    private final IOrderService orderService = new OrderServiceImpl();
    private final IReportService reportService = new ReportServiceImpl();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    private List<HoaDonBan> orderList = new ArrayList<>();
    private JTable tblOrders;
    private DefaultTableModel modelOrders;
    private JTextField txtSearch;
    private JButton btnCreateInvoice;
    private JButton btnViewDetails;
    private JButton btnExport;

    public OrderManagementPanel() {
        setLayout(new BorderLayout(0, 12));
        setBackground(ThemeConfig.BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        initToolbar();
        initTable();
        loadOrders();
    }

    private void initToolbar() {
        JPanel toolbarCard = new JPanel(new BorderLayout(12, 10));
        toolbarCard.setBackground(ThemeConfig.SURFACE);
        toolbarCard.setBorder(ThemeConfig.createCardBorder());

        // Left: Search
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        searchPanel.setBackground(ThemeConfig.SURFACE);

        JLabel lblSearch = new JLabel("Tìm số HĐ / Mã KH:");
        lblSearch.setFont(ThemeConfig.FONT_BOLD);
        txtSearch = new JTextField(14);
        txtSearch.setPreferredSize(new Dimension(150, 36));

        JButton btnSearch = new JButton("Tìm");
        ThemeConfig.stylePrimaryButton(btnSearch);
        btnSearch.addActionListener(e -> filterOrders());

        JButton btnRefresh = new JButton("Làm mới");
        ThemeConfig.styleSecondaryButton(btnRefresh);
        btnRefresh.addActionListener(e -> loadOrders());

        searchPanel.add(lblSearch);
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnRefresh);

        // Right: Actions
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        actionPanel.setBackground(ThemeConfig.SURFACE);

        boolean isCustomer = UserSession.getInstance().getRole() == LoaiTaiKhoan.KHACH_HANG;
        if (!isCustomer) {
            btnCreateInvoice = new JButton("+ Lập hóa đơn bán");
            ThemeConfig.stylePrimaryButton(btnCreateInvoice);
            btnCreateInvoice.addActionListener(e -> openInvoiceCreationDialog());
            actionPanel.add(btnCreateInvoice);
        }

        btnViewDetails = new JButton("Xem chi tiết");
        ThemeConfig.styleSecondaryButton(btnViewDetails);
        btnViewDetails.setEnabled(false);
        btnViewDetails.addActionListener(e -> openDetailsDialog());
        actionPanel.add(btnViewDetails);

        btnExport = new JButton("In / Xuất hóa đơn");
        ThemeConfig.styleSecondaryButton(btnExport);
        btnExport.setEnabled(false);
        btnExport.addActionListener(e -> exportCurrentInvoice());
        actionPanel.add(btnExport);

        toolbarCard.add(searchPanel, BorderLayout.WEST);
        toolbarCard.add(actionPanel, BorderLayout.EAST);
        add(toolbarCard, BorderLayout.NORTH);
    }

    private void initTable() {
        String[] cols = {"Số HĐ", "Ngày bán", "Mã KH", "Tên khách hàng", "Nhân viên lập", "Tổng thanh toán"};
        modelOrders = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tblOrders = new JTable(modelOrders);
        tblOrders.setRowHeight(38);
        tblOrders.setShowGrid(true);
        tblOrders.setGridColor(ThemeConfig.BORDER);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tblOrders.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tblOrders.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        tblOrders.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

        tblOrders.getSelectionModel().addListSelectionListener(e -> {
            boolean selected = tblOrders.getSelectedRow() != -1;
            btnViewDetails.setEnabled(selected);
            btnExport.setEnabled(selected);
        });

        JScrollPane scroll = new JScrollPane(tblOrders);
        scroll.setBorder(BorderFactory.createLineBorder(ThemeConfig.BORDER, 1));
        add(scroll, BorderLayout.CENTER);
    }

    public void loadOrders() {
        txtSearch.setText("");
        LoaiTaiKhoan role = UserSession.getInstance().getRole();
        if (role == LoaiTaiKhoan.KHACH_HANG) {
            String maKH = UserSession.getInstance().getCurrentAccount().getMaTaiKhoan();
            orderList = orderService.getOrdersByCustomer(maKH);
        } else {
            orderList = orderService.getAllOrders();
        }
        renderTable(orderList);
    }

    private void renderTable(List<HoaDonBan> list) {
        modelOrders.setRowCount(0);
        for (HoaDonBan hd : list) {
            String ngay = hd.getNgayBan() != null ? dateFormat.format(hd.getNgayBan()) : "-";
            String maKH = hd.getKhachHang() != null ? hd.getKhachHang().getMaKhachHang() : "Khách lẻ";
            String tenKH = hd.getKhachHang() != null && hd.getKhachHang().getHoTen() != null ? hd.getKhachHang().getHoTen() : "-";
            String tenNV = hd.getNhanVien() != null && hd.getNhanVien().getHoTen() != null ? hd.getNhanVien().getHoTen() : (hd.getNhanVien() != null ? hd.getNhanVien().getMaNhanVien() : "-");

            modelOrders.addRow(new Object[]{
                hd.getSoHoaDon(),
                ngay,
                maKH,
                tenKH,
                tenNV,
                CurrencyUtil.format(hd.getTongTien())
            });
        }
    }

    private void filterOrders() {
        String kw = txtSearch.getText().trim().toLowerCase();
        if (kw.isEmpty()) {
            renderTable(orderList);
            return;
        }

        List<HoaDonBan> filtered = new ArrayList<>();
        for (HoaDonBan hd : orderList) {
            String soHD = String.valueOf(hd.getSoHoaDon());
            String maKH = hd.getKhachHang() != null ? hd.getKhachHang().getMaKhachHang().toLowerCase() : "";
            if (soHD.contains(kw) || maKH.contains(kw)) {
                filtered.add(hd);
            }
        }
        renderTable(filtered);
    }

    private void openInvoiceCreationDialog() {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        InvoiceCreationDialog dialog = new InvoiceCreationDialog(parent);
        dialog.setVisible(true);
        if (dialog.isInvoiceCreated()) {
            loadOrders();
        }
    }

    private void openDetailsDialog() {
        int row = tblOrders.getSelectedRow();
        if (row < 0 || row >= orderList.size()) return;

        HoaDonBan hd = orderList.get(row);
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);

        JDialog detailsDialog = new JDialog(parent, "Chi tiết hóa đơn #" + hd.getSoHoaDon(), true);
        detailsDialog.setSize(680, 480);
        detailsDialog.setLocationRelativeTo(parent);
        detailsDialog.setLayout(new BorderLayout());

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 12));
        header.setBackground(ThemeConfig.SURFACE);
        header.add(new JLabel("Số HĐ: " + hd.getSoHoaDon()));
        header.add(new JLabel("• Ngày bán: " + (hd.getNgayBan() != null ? dateFormat.format(hd.getNgayBan()) : "-")));
        header.add(new JLabel("• Tổng: " + CurrencyUtil.format(hd.getTongTien())));
        detailsDialog.add(header, BorderLayout.NORTH);

        String[] cols = {"Mã SP", "Tên sản phẩm", "Số lượng", "Đơn giá", "Giảm giá (%)", "Thành tiền"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        table.setRowHeight(32);

        if (hd.getDanhSachChiTiet() != null) {
            for (ChiTietHoaDon ct : hd.getDanhSachChiTiet()) {
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

    private void exportCurrentInvoice() {
        int row = tblOrders.getSelectedRow();
        if (row < 0 || row >= orderList.size()) return;

        HoaDonBan hd = orderList.get(row);
        try {
            reportService.exportHoaDonBan(String.valueOf(hd.getSoHoaDon()));
            JOptionPane.showMessageDialog(this, "Xuất hóa đơn #" + hd.getSoHoaDon() + " hoàn tất!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi xuất hóa đơn: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}

