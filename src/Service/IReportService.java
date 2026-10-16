package Service;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public interface IReportService {
    void exportHoaDonBan(String soHoaDon);
    void searchHoaDonBanByMaKhachHang(DefaultTableModel model, String maKH);
    void exportHoaDonNhap(String soHoaDonNhap);
    void exportBaoCaoDoanhThu(JTable tblThongke, String filePath) throws Exception;
}

