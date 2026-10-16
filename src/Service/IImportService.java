package Service;

import Model.HoaDonNhap;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface IImportService {
    HoaDonNhap lapHoaDonNhap(String maNhanVien, int maNCC, int maQuanAo, int soLuong, BigDecimal donGiaNhap, double giamGia) throws Exception;
    List<HoaDonNhap> getAllHoaDonNhap();
    Optional<HoaDonNhap> getHoaDonNhapById(int soHDN);
}

