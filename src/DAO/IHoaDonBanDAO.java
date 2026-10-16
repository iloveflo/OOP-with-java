package DAO;

import Model.ChiTietHoaDon;
import Model.HoaDonBan;
import java.util.List;
import java.util.Optional;

public interface IHoaDonBanDAO {
    int taoHoaDonBan(HoaDonBan hdb) throws Exception;
    Optional<HoaDonBan> findById(int soHoaDonBan);
    List<HoaDonBan> findByKhachHang(String maKhachHang);
    List<HoaDonBan> findAll();
    List<ChiTietHoaDon> getChiTiet(int soHoaDonBan);
}

