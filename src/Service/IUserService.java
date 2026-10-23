package Service;

import Model.KhachHang;
import Model.NhanVien;
import Model.TaiKhoan;
import java.util.List;
import java.util.Optional;

public interface IUserService {
    void registerKhachHang(String username, String password, String hoTen, String sdt, String diaChi, String email);
    void changePassword(String username, String oldPassword, String newPassword);
    void resetPassword(String username, String newPassword);
    boolean updateKhachHangProfile(KhachHang kh);
    boolean updateNhanVienProfile(NhanVien nv);
    Optional<KhachHang> getKhachHangByMaTaiKhoan(String maTaiKhoan);
    Optional<NhanVien> getNhanVienByMaTaiKhoan(String maTaiKhoan);
    List<NhanVien> getAllNhanVien();
    List<KhachHang> getAllKhachHang();
    List<TaiKhoan> getAllTaiKhoan();
    boolean saveNhanVien(NhanVien nv, String username, String password);
    boolean updateNhanVien(NhanVien nv);
    boolean deleteNhanVien(String maNV);
    boolean updateKhachHang(KhachHang kh);
    boolean deleteKhachHang(String maKH);
}

