package Model;

import java.math.BigDecimal;

public class ItemGioHang {
    private String maKhachHang;
    private SanPham sanPham;
    private BigDecimal donGiaBan;
    private int soLuongDat;
    private BigDecimal tongTien;
    private int trangThai; // 0: Trong gio, 1: Da dat

    public ItemGioHang() {}

    public ItemGioHang(String maKhachHang, SanPham sanPham, BigDecimal donGiaBan, int soLuongDat, int trangThai) {
        this.maKhachHang = maKhachHang;
        this.sanPham = sanPham;
        this.donGiaBan = donGiaBan;
        this.soLuongDat = soLuongDat;
        this.tongTien = donGiaBan.multiply(BigDecimal.valueOf(soLuongDat));
        this.trangThai = trangThai;
    }

    public String getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(String maKhachHang) {
        this.maKhachHang = maKhachHang;
    }

    public SanPham getSanPham() {
        return sanPham;
    }

    public void setSanPham(SanPham sanPham) {
        this.sanPham = sanPham;
    }

    public BigDecimal getDonGiaBan() {
        return donGiaBan;
    }

    public void setDonGiaBan(BigDecimal donGiaBan) {
        this.donGiaBan = donGiaBan;
        this.tinhTongTien();
    }

    public int getSoLuongDat() {
        return soLuongDat;
    }

    public void setSoLuongDat(int soLuongDat) {
        this.soLuongDat = soLuongDat;
        this.tinhTongTien();
    }

    public BigDecimal getTongTien() {
        return tongTien;
    }

    public void setTongTien(BigDecimal tongTien) {
        this.tongTien = tongTien;
    }

    public int getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(int trangThai) {
        this.trangThai = trangThai;
    }

    public void tinhTongTien() {
        if (this.donGiaBan != null) {
            this.tongTien = this.donGiaBan.multiply(BigDecimal.valueOf(this.soLuongDat));
        }
    }
}

