package Model;

import java.math.BigDecimal;
import java.util.Date;

public class HoaDonNhap extends HoaDon {
    private NhaCungCap nhaCungCap;

    public HoaDonNhap() {
        super();
    }

    public HoaDonNhap(int soHoaDon, Date ngayLap, NhanVien nhanVien, NhaCungCap nhaCungCap, BigDecimal tongTien) {
        super(soHoaDon, ngayLap, nhanVien, tongTien);
        this.nhaCungCap = nhaCungCap;
    }

    public NhaCungCap getNhaCungCap() {
        return nhaCungCap;
    }

    public void setNhaCungCap(NhaCungCap nhaCungCap) {
        this.nhaCungCap = nhaCungCap;
    }

    @Override
    public String getLoaiHoaDon() {
        return "Hóa Đơn Nhập";
    }

    @Override
    public String toString() {
        return "HoaDonNhap{" +
                "soHoaDon=" + soHoaDon +
                ", ngayLap=" + ngayLap +
                ", nhaCungCap=" + (nhaCungCap != null ? nhaCungCap.getTenNCC() : "null") +
                ", tongTien=" + tongTien +
                '}';
    }
}

