package DAO;

import Model.KhachHang;
import java.util.Optional;

public interface IKhachHangDAO extends IGenericDAO<KhachHang, String> {
    Optional<KhachHang> findByMaTaiKhoan(String maTaiKhoan);
    boolean isEmailOrPhoneExist(String email, String phone, String excludeMaKH);
}

