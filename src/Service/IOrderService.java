package Service;

import Model.HoaDonBan;
import Model.ItemGioHang;
import java.math.BigDecimal;
import java.util.List;

public interface IOrderService {
    List<ItemGioHang> getGioHang(String maKhachHang, Integer trangThai);
    List<ItemGioHang> getGioHangByTrangThai(int trangThai);
    void themVaoGioHang(String maKhachHang, int maQuanAo, int soLuong);
    void capNhatSoLuong(String maKhachHang, int maQuanAo, int soLuongMoi);
    void xoaKhoiGioHang(String maKhachHang, int maQuanAo);
    void datHang(String maKhachHang);
    HoaDonBan lapHoaDonBan(String maKhachHang, String maNhanVien, List<ItemGioHang> items) throws Exception;
    HoaDonBan lapHoaDonDonLe(String maKhachHang, String maNhanVien, int maQuanAo, int soLuong, BigDecimal donGiaBan) throws Exception;
    List<HoaDonBan> getAllOrders();
    List<HoaDonBan> getOrdersByCustomer(String maKhachHang);
    java.util.Optional<HoaDonBan> getOrderById(int id);
}

