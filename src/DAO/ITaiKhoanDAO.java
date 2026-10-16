package DAO;

import Model.TaiKhoan;
import java.util.Optional;

public interface ITaiKhoanDAO extends IGenericDAO<TaiKhoan, String> {
    Optional<TaiKhoan> findByTenDangNhap(String username);
    Optional<TaiKhoan> authenticate(String username, String password);
    boolean updateTrangThaiDangNhap(String maTaiKhoan, boolean dangNhap);
    boolean updateMatKhau(String tenDangNhap, String matKhauMoi);
    boolean isExist(String username, String maTaiKhoan);
}

