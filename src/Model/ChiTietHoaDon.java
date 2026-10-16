package Model;

import java.math.BigDecimal;

public class ChiTietHoaDon {
    private int soHoaDon;
    private SanPham sanPham;
    private int soLuong;
    private BigDecimal donGia;
    private double giamGia; // Phan tram giam gia (0 -> 100)
    private BigDecimal thanhTien;

    public ChiTietHoaDon() {}

    public ChiTietHoaDon(int soHoaDon, SanPham sanPham, int soLuong, BigDecimal donGia, double giamGia) {
        this.soHoaDon = soHoaDon;
        this.sanPham = sanPham;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.giamGia = giamGia;
        this.thanhTien = tinhThanhTien();
    }

    public int getSoHoaDon() {
        return soHoaDon;
    }

    public void setSoHoaDon(int soHoaDon) {
        this.soHoaDon = soHoaDon;
    }

    public SanPham getSanPham() {
        return sanPham;
    }

    public void setSanPham(SanPham sanPham) {
        this.sanPham = sanPham;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
        this.thanhTien = tinhThanhTien();
    }

    public BigDecimal getDonGia() {
        return donGia;
    }

    public void setDonGia(BigDecimal donGia) {
        this.donGia = donGia;
        this.thanhTien = tinhThanhTien();
    }

    public double getGiamGia() {
        return giamGia;
    }

    public void setGiamGia(double giamGia) {
        this.giamGia = giamGia;
        this.thanhTien = tinhThanhTien();
    }

    public BigDecimal getThanhTien() {
        return thanhTien;
    }

    public void setThanhTien(BigDecimal thanhTien) {
        this.thanhTien = thanhTien;
    }

    public BigDecimal tinhThanhTien() {
        if (this.donGia == null) return BigDecimal.ZERO;
        BigDecimal tong = this.donGia.multiply(BigDecimal.valueOf(this.soLuong));
        double heSo = (100.0 - this.giamGia) / 100.0;
        return tong.multiply(BigDecimal.valueOf(heSo));
    }
}

