package Service;

public interface IUserService {
    void registerKhachHang(String username, String password, String hoTen, String sdt, String diaChi, String email);
    void changePassword(String username, String oldPassword, String newPassword);
    void resetPassword(String username, String newPassword);
}

