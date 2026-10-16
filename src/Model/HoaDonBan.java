package Model;

import java.math.BigDecimal;
import java.util.Date;

public class HoaDonBan extends HoaDon {
    private KhachHang khachHang;

    public HoaDonBan() {
        super();
    }

    public HoaDonBan(int soHoaDon, Date ngayLap, NhanVien nhanVien, KhachHang khachHang, BigDecimal tongTien) {
        super(soHoaDon, ngayLap, nhanVien, tongTien);
        this.khachHang = khachHang;
    }

    public KhachHang getKhachHang() {
        return khachHang;
    }

    public void setKhachHang(KhachHang khachHang) {
        this.khachHang = khachHang;
    }

    @Override
    public String getLoaiHoaDon() {
        return "Hóa Đơn Bán";
    }

    @Override
    public String toString() {
        return "HoaDonBan{" +
                "soHoaDon=" + soHoaDon +
                ", ngayLap=" + ngayLap +
                ", khachHang=" + (khachHang != null ? khachHang.getHoTen() : "null") +
                ", tongTien=" + tongTien +
                '}';
    }
}

