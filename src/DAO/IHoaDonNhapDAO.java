package DAO;

import Model.ChiTietHoaDon;
import Model.HoaDonNhap;
import java.util.List;
import java.util.Optional;

public interface IHoaDonNhapDAO {
    int taoHoaDonNhap(HoaDonNhap hdn) throws Exception;
    Optional<HoaDonNhap> findById(int soHoaDonNhap);
    List<HoaDonNhap> findAll();
    List<ChiTietHoaDon> getChiTiet(int soHoaDonNhap);
}

