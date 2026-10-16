package Model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class HoaDon {
    protected int soHoaDon;
    protected Date ngayLap;
    protected NhanVien nhanVien;
    protected BigDecimal tongTien;
    protected List<ChiTietHoaDon> danhSachChiTiet;

    public HoaDon() {
        this.danhSachChiTiet = new ArrayList<>();
        this.ngayLap = new Date();
        this.tongTien = BigDecimal.ZERO;
    }

    public HoaDon(int soHoaDon, Date ngayLap, NhanVien nhanVien, BigDecimal tongTien) {
        this.soHoaDon = soHoaDon;
        this.ngayLap = ngayLap;
        this.nhanVien = nhanVien;
        this.tongTien = tongTien;
        this.danhSachChiTiet = new ArrayList<>();
    }

    public int getSoHoaDon() {
        return soHoaDon;
    }

    public void setSoHoaDon(int soHoaDon) {
        this.soHoaDon = soHoaDon;
    }

    public Date getNgayLap() {
        return ngayLap;
    }

    public void setNgayLap(Date ngayLap) {
        this.ngayLap = ngayLap;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    public BigDecimal getTongTien() {
        return tongTien;
    }

    public void setTongTien(BigDecimal tongTien) {
        this.tongTien = tongTien;
    }

    public List<ChiTietHoaDon> getDanhSachChiTiet() {
        return danhSachChiTiet;
    }

    public void setDanhSachChiTiet(List<ChiTietHoaDon> danhSachChiTiet) {
        this.danhSachChiTiet = danhSachChiTiet;
    }

    public void themChiTiet(ChiTietHoaDon chiTiet) {
        this.danhSachChiTiet.add(chiTiet);
        this.tinhLaiTongTien();
    }

    public void tinhLaiTongTien() {
        BigDecimal tong = BigDecimal.ZERO;
        for (ChiTietHoaDon ct : danhSachChiTiet) {
            tong = tong.add(ct.tinhThanhTien());
        }
        this.tongTien = tong;
    }

    public abstract String getLoaiHoaDon();
}

