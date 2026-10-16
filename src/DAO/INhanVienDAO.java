package DAO;

import Model.NhanVien;
import java.util.Optional;

public interface INhanVienDAO extends IGenericDAO<NhanVien, String> {
    Optional<NhanVien> findByMaTaiKhoan(String maTaiKhoan);
    boolean isEmailOrPhoneExist(String email, String phone, String excludeMaNV);
}

