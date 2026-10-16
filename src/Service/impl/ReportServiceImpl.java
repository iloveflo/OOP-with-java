package Service.impl;

import DAO.DatabaseConnection;
import Service.IReportService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

public class ReportServiceImpl implements IReportService {

    private String getReportsDirectory() {
        String folderPath = System.getProperty("user.home") + File.separator + "ClothingStoreReports";
        File dir = new File(folderPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return folderPath;
    }

    private void openFileIfExists(File file) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                Desktop.getDesktop().open(file);
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public void exportHoaDonBan(String soHoaDon) {
        if (soHoaDon == null || soHoaDon.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Vui lòng chọn hóa đơn để in.");
            return;
        }

        String query = """
            SELECT 
                hdb.SoHoaDonBan, hdb.NgayBan, hdb.TongTien,
                kh.TenKhach, kh.DiaChi AS DiaChiKhach, kh.SoDienThoai AS SDTKhach, kh.Email AS EmailKhach,
                nv.TenNhanVien, nv.SoDienThoai AS SDTNhanVien,
                sp.TenQuanAo, cthd.SoLuong, cthd.GiamGia, cthd.ThanhTien
            FROM hoadonban hdb
            JOIN khachhang kh ON hdb.MaKhachHang = kh.MaKhachHang
            JOIN nhanvien nv ON hdb.MaNhanVien = nv.MaNhanVien
            JOIN chitiethoadonban cthd ON hdb.SoHoaDonBan = cthd.SoHoaDonBan
            JOIN sanpham sp ON cthd.MaQuanAo = sp.MaQuanAo
            WHERE hdb.SoHoaDonBan = ?;
        """;

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement cmd = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {

            cmd.setString(1, soHoaDon);
            try (ResultSet rs = cmd.executeQuery()) {
                if (rs.next()) {
                    Workbook wb = new XSSFWorkbook();
                    Sheet sheet = wb.createSheet("HoaDon");

                    int row = 0;
                    Row titleRow = sheet.createRow(row++);
                    Cell titleCell = titleRow.createCell(0);
                    titleCell.setCellValue("HÓA ĐƠN BÁN HÀNG - BDTHD");
                    CellStyle styleTitle = wb.createCellStyle();
                    Font fontTitle = wb.createFont();
                    fontTitle.setBold(true);
                    fontTitle.setFontHeightInPoints((short) 18);
                    styleTitle.setFont(fontTitle);
                    styleTitle.setAlignment(HorizontalAlignment.CENTER);
                    titleCell.setCellStyle(styleTitle);
                    sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

                    sheet.createRow(row++).createCell(0).setCellValue("Số hóa đơn: " + soHoaDon);
                    sheet.createRow(row++).createCell(0).setCellValue("Ngày bán: " + rs.getDate("NgayBan"));
                    sheet.createRow(row++).createCell(0).setCellValue("Khách hàng: " + rs.getString("TenKhach"));
                    sheet.createRow(row++).createCell(0).setCellValue("Số điện thoại: " + rs.getString("SDTKhach"));
                    sheet.createRow(row++).createCell(0).setCellValue("Email: " + rs.getString("EmailKhach"));
                    sheet.createRow(row++).createCell(0).setCellValue("Nhân viên bán: " + rs.getString("TenNhanVien"));
                    sheet.createRow(row++).createCell(0).setCellValue("Liên hệ hỗ trợ: " + rs.getString("SDTNhanVien"));
                    row++;

                    Row header = sheet.createRow(row++);
                    header.createCell(0).setCellValue("Tên sản phẩm");
                    header.createCell(1).setCellValue("Số lượng");
                    header.createCell(2).setCellValue("Giảm giá (%)");
                    header.createCell(3).setCellValue("Thành tiền");

                    rs.beforeFirst();
                    while (rs.next()) {
                        Row r = sheet.createRow(row++);
                        r.createCell(0).setCellValue(rs.getString("TenQuanAo"));
                        r.createCell(1).setCellValue(rs.getInt("SoLuong"));
                        r.createCell(2).setCellValue(rs.getFloat("GiamGia"));
                        r.createCell(3).setCellValue(rs.getDouble("ThanhTien"));
                    }

                    Row totalRow = sheet.createRow(row++);
                    totalRow.createCell(2).setCellValue("Tổng tiền:");
                    totalRow.createCell(3).setCellFormula("SUM(D9:D" + (row - 1) + ")");

                    for (int i = 0; i <= 3; i++) {
                        sheet.autoSizeColumn(i);
                    }

                    String fileName = "HoaDon_" + soHoaDon.replaceAll("[^a-zA-Z0-9]", "_") + ".xlsx";
                    String filePath = getReportsDirectory() + File.separator + fileName;
                    File outFile = new File(filePath);
                    try (FileOutputStream fos = new FileOutputStream(outFile)) {
                        wb.write(fos);
                    }
                    wb.close();

                    JOptionPane.showMessageDialog(null, "Xuất hóa đơn thành công!\nFile: " + filePath);
                    openFileIfExists(outFile);
                } else {
                    JOptionPane.showMessageDialog(null, "Không tìm thấy hóa đơn!");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi xuất hóa đơn: " + ex.getMessage());
        }
    }

    @Override
    public void searchHoaDonBanByMaKhachHang(DefaultTableModel model, String maKH) {
        model.setRowCount(0);
        String sql = "SELECT hdb.SoHoaDonBan, ct.MaQuanAo, hdb.MaKhachHang, hdb.MaNhanVien, ct.SoLuong, hdb.NgayBan, ct.ThanhTien " +
                     "FROM hoadonban hdb " +
                     "JOIN chitiethoadonban ct ON hdb.SoHoaDonBan = ct.SoHoaDonBan " +
                     "WHERE hdb.MaKhachHang = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, maKH);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Object[] row = {
                        rs.getInt("SoHoaDonBan"),
                        rs.getInt("MaQuanAo"),
                        rs.getString("MaKhachHang"),
                        rs.getInt("MaNhanVien"),
                        rs.getInt("SoLuong"),
                        rs.getDate("NgayBan"),
                        rs.getDouble("ThanhTien")
                    };
                    model.addRow(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void exportHoaDonNhap(String soHoaDonNhap) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            String sqlHeader = """
                SELECT hd.SoHoaDonNhap, hd.NgayNhap, hd.TongTien, nv.TenNhanVien
                FROM hoadonnhap hd
                JOIN nhanvien nv ON hd.MaNhanVien = nv.MaNhanVien
                WHERE hd.SoHoaDonNhap = ?
            """;

            String tenNhanVien;
            Date ngayNhap;
            double tongTien;

            try (PreparedStatement psHeader = conn.prepareStatement(sqlHeader)) {
                psHeader.setString(1, soHoaDonNhap);
                try (ResultSet rsHeader = psHeader.executeQuery()) {
                    if (!rsHeader.next()) {
                        JOptionPane.showMessageDialog(null, "Không tìm thấy thông tin hóa đơn.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                    tenNhanVien = rsHeader.getString("TenNhanVien");
                    ngayNhap = rsHeader.getDate("NgayNhap");
                    tongTien = rsHeader.getDouble("TongTien");
                }
            }

            String sqlDetails = """
                SELECT 
                    sp.TenQuanAo AS `Tên quần áo`,
                    cthdn.SoLuong AS `Số lượng`,
                    cthdn.DonGia AS `Đơn giá`,
                    cthdn.GiamGia AS `Giảm giá (%)`,
                    cthdn.ThanhTien AS `Thành tiền`
                FROM chitiethoadonnhap cthdn
                JOIN sanpham sp ON cthdn.MaQuanAo = sp.MaQuanAo
                WHERE cthdn.SoHoaDonNhap = ?
            """;

            try (PreparedStatement psDetails = conn.prepareStatement(sqlDetails)) {
                psDetails.setString(1, soHoaDonNhap);
                try (ResultSet rsDetails = psDetails.executeQuery()) {
                    Workbook workbook = new XSSFWorkbook();
                    Sheet sheet = workbook.createSheet("HoaDonNhap");

                    Row rowTitle = sheet.createRow(0);
                    Cell titleCell = rowTitle.createCell(0);
                    titleCell.setCellValue("HÓA ĐƠN NHẬP");
                    CellStyle styleTitle = workbook.createCellStyle();
                    Font fontTitle = workbook.createFont();
                    fontTitle.setFontHeightInPoints((short) 16);
                    fontTitle.setBold(true);
                    styleTitle.setFont(fontTitle);
                    titleCell.setCellStyle(styleTitle);
                    sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));

                    String[][] info = {
                        {"Số HĐ:", soHoaDonNhap},
                        {"Ngày nhập:", new SimpleDateFormat("dd/MM/yyyy").format(ngayNhap)},
                        {"Nhân viên:", tenNhanVien},
                        {"Tổng tiền:", String.format("%,.0f đ", tongTien)}
                    };

                    for (int i = 0; i < info.length; i++) {
                        Row row = sheet.createRow(2 + i);
                        row.createCell(0).setCellValue(info[i][0]);
                        row.createCell(1).setCellValue(info[i][1]);
                    }

                    Row rowHeader = sheet.createRow(7);
                    String[] columns = {"Tên quần áo", "Số lượng", "Đơn giá", "Giảm giá (%)", "Thành tiền"};
                    for (int i = 0; i < columns.length; i++) {
                        rowHeader.createCell(i).setCellValue(columns[i]);
                    }

                    int rowIndex = 8;
                    while (rsDetails.next()) {
                        Row row = sheet.createRow(rowIndex++);
                        for (int i = 0; i < columns.length; i++) {
                            row.createCell(i).setCellValue(rsDetails.getString(i + 1));
                        }
                    }

                    for (int i = 0; i < columns.length; i++) {
                        sheet.autoSizeColumn(i);
                    }

                    String filePath = getReportsDirectory() + File.separator + "HoaDonNhap_" + soHoaDonNhap + ".xlsx";
                    File outFile = new File(filePath);

                    try (FileOutputStream out = new FileOutputStream(outFile)) {
                        workbook.write(out);
                    }
                    workbook.close();

                    openFileIfExists(outFile);
                    JOptionPane.showMessageDialog(null, "Xuất hóa đơn thành công!\nFile: " + filePath, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi xuất hóa đơn: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void exportBaoCaoDoanhThu(JTable tblThongke, String filePath) throws Exception {
        if (tblThongke.getRowCount() == 0) {
            throw new Exception("Không có dữ liệu để xuất!");
        }

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("BáoCáo");

        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        String thangNam = tblThongke.getValueAt(0, 0).toString();
        titleCell.setCellValue("BÁO CÁO DOANH THU THÁNG " + thangNam + " – BDTHD");

        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));
        CellStyle titleStyle = workbook.createCellStyle();
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 12);
        titleStyle.setFont(titleFont);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleCell.setCellStyle(titleStyle);

        String[] nhan = {"Tổng Số lượng Bán:", "Tổng Doanh Thu:", "Tổng Lợi Nhuận:"};
        Object[] data = {
            tblThongke.getValueAt(0, 1),
            tblThongke.getValueAt(0, 2) + " VNĐ",
            tblThongke.getValueAt(0, 3) + " VNĐ"
        };

        for (int i = 0; i < nhan.length; i++) {
            Row row = sheet.createRow(i + 2);
            Cell cellLabel = row.createCell(0);
            Cell cellValue = row.createCell(1);
            cellLabel.setCellValue(nhan[i]);
            cellValue.setCellValue(data[i].toString());

            CellStyle borderStyle = workbook.createCellStyle();
            borderStyle.setBorderTop(BorderStyle.THIN);
            borderStyle.setBorderBottom(BorderStyle.THIN);
            borderStyle.setBorderLeft(BorderStyle.THIN);
            borderStyle.setBorderRight(BorderStyle.THIN);

            Font boldFont = workbook.createFont();
            boldFont.setBold(true);
            borderStyle.setFont(boldFont);

            cellLabel.setCellStyle(borderStyle);
            cellValue.setCellStyle(borderStyle);
        }

        for (int i = 0; i <= 2; i++) {
            sheet.autoSizeColumn(i);
        }

        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (FileOutputStream out = new FileOutputStream(file)) {
            workbook.write(out);
        }
        workbook.close();

        openFileIfExists(file);
    }
}

