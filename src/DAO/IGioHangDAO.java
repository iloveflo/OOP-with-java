package DAO;

import Model.ItemGioHang;
import java.math.BigDecimal;
import java.util.List;

public interface IGioHangDAO {
    List<ItemGioHang> getGioHangByKhachHang(String maKhachHang, Integer trangThai);
    List<ItemGioHang> getGioHangByTrangThai(int trangThai);
    boolean themVaoGio(String maKhachHang, int maQuanAo, BigDecimal donGiaBan, int soLuong);
    boolean capNhatSoLuong(String maKhachHang, int maQuanAo, int soLuongMoi);
    boolean xoaKhoiGio(String maKhachHang, int maQuanAo);
    boolean updateTrangThai(String maKhachHang, int trangThaiMoi);
    int countItemTrongGio(String maKhachHang);
}

