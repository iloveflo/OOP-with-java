package Service.impl;

import DAO.IKhachHangDAO;
import DAO.INhanVienDAO;
import DAO.ITaiKhoanDAO;
import DAO.KhachHangDAOImpl;
import DAO.NhanVienDAOImpl;
import DAO.TaiKhoanDAOImpl;
import Model.KhachHang;
import Model.LoaiTaiKhoan;
import Model.NhanVien;
import Model.TaiKhoan;
import java.util.List;
import Service.IUserService;
import Service.exception.AuthenticationException;
import Service.exception.ValidationException;
import java.util.Optional;

public class UserServiceImpl implements IUserService {
    private final ITaiKhoanDAO taiKhoanDAO;
    private final IKhachHangDAO khachHangDAO;
    private final INhanVienDAO nhanVienDAO;

    public UserServiceImpl() {
        this.taiKhoanDAO = new TaiKhoanDAOImpl();
        this.khachHangDAO = new KhachHangDAOImpl();
        this.nhanVienDAO = new NhanVienDAOImpl();
    }

    public UserServiceImpl(ITaiKhoanDAO taiKhoanDAO, IKhachHangDAO khachHangDAO, INhanVienDAO nhanVienDAO) {
        this.taiKhoanDAO = taiKhoanDAO;
        this.khachHangDAO = khachHangDAO;
        this.nhanVienDAO = nhanVienDAO;
    }

    @Override
    public void registerKhachHang(String username, String password, String hoTen, String sdt, String diaChi, String email) {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new ValidationException("Tên đăng nhập và mật khẩu không được rỗng!");
        }

        String trimmedUser = username.trim();
        if (taiKhoanDAO.isExist(trimmedUser, trimmedUser)) {
            throw new ValidationException("Tên đăng nhập hoặc mã tài khoản đã tồn tại!");
        }

        if (khachHangDAO.isEmailOrPhoneExist(email, sdt, null) || nhanVienDAO.isEmailOrPhoneExist(email, sdt, null)) {
            throw new ValidationException("Số điện thoại hoặc Email đã được đăng ký trong hệ thống!");
        }

        TaiKhoan tk = new TaiKhoan(trimmedUser, trimmedUser, password, LoaiTaiKhoan.KHACH_HANG, false);
        if (!taiKhoanDAO.save(tk)) {
            throw new ValidationException("Lỗi lưu tài khoản!");
        }

        KhachHang kh = new KhachHang(trimmedUser, hoTen, sdt, diaChi, email, trimmedUser);
        if (!khachHangDAO.save(kh)) {
            taiKhoanDAO.deleteById(trimmedUser);
            throw new ValidationException("Lỗi lưu thông tin khách hàng!");
        }
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new ValidationException("Mật khẩu mới không được để trống!");
        }

        Optional<TaiKhoan> optTK = taiKhoanDAO.authenticate(username, oldPassword);
        if (optTK.isEmpty()) {
            throw new AuthenticationException("Mật khẩu cũ không chính xác!");
        }

        taiKhoanDAO.updateMatKhau(username, newPassword);
    }

    @Override
    public void resetPassword(String username, String newPassword) {
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new ValidationException("Mật khẩu mới không được để trống!");
        }

        if (taiKhoanDAO.findByTenDangNhap(username).isEmpty()) {
            throw new ValidationException("Tài khoản không tồn tại!");
        }

        taiKhoanDAO.updateMatKhau(username, newPassword);
    }

    @Override
    public boolean updateKhachHangProfile(KhachHang kh) {
        if (kh == null) return false;
        return khachHangDAO.update(kh);
    }

    @Override
    public boolean updateNhanVienProfile(NhanVien nv) {
        if (nv == null) return false;
        return nhanVienDAO.update(nv);
    }

    @Override
    public Optional<KhachHang> getKhachHangByMaTaiKhoan(String maTaiKhoan) {
        return khachHangDAO.findByMaTaiKhoan(maTaiKhoan);
    }

    @Override
    public Optional<NhanVien> getNhanVienByMaTaiKhoan(String maTaiKhoan) {
        return nhanVienDAO.findByMaTaiKhoan(maTaiKhoan);
    }

    @Override
    public List<NhanVien> getAllNhanVien() {
        return nhanVienDAO.findAll();
    }

    @Override
    public List<KhachHang> getAllKhachHang() {
        return khachHangDAO.findAll();
    }

    @Override
    public List<TaiKhoan> getAllTaiKhoan() {
        return taiKhoanDAO.findAll();
    }

    @Override
    public boolean saveNhanVien(NhanVien nv, String username, String password) {
        if (taiKhoanDAO.isExist(username, nv.getMaNhanVien())) {
            throw new ValidationException("Tên đăng nhập hoặc mã nhân viên đã tồn tại!");
        }
        TaiKhoan tk = new TaiKhoan(nv.getMaNhanVien(), username, password, LoaiTaiKhoan.NHAN_VIEN, false);
        if (!taiKhoanDAO.save(tk)) {
            return false;
        }
        nv.setMaTaiKhoan(tk.getMaTaiKhoan());
        if (!nhanVienDAO.save(nv)) {
            taiKhoanDAO.deleteById(tk.getMaTaiKhoan());
            return false;
        }
        return true;
    }

    @Override
    public boolean updateNhanVien(NhanVien nv) {
        return nhanVienDAO.update(nv);
    }

    @Override
    public boolean deleteNhanVien(String maNV) {
        Optional<NhanVien> opt = nhanVienDAO.findById(maNV);
        boolean deleted = nhanVienDAO.deleteById(maNV);
        if (deleted && opt.isPresent() && opt.get().getMaTaiKhoan() != null) {
            taiKhoanDAO.deleteById(opt.get().getMaTaiKhoan());
        }
        return deleted;
    }

    @Override
    public boolean updateKhachHang(KhachHang kh) {
        return khachHangDAO.update(kh);
    }

    @Override
    public boolean deleteKhachHang(String maKH) {
        Optional<KhachHang> opt = khachHangDAO.findById(maKH);
        boolean deleted = khachHangDAO.deleteById(maKH);
        if (deleted && opt.isPresent() && opt.get().getMaTaiKhoan() != null) {
            taiKhoanDAO.deleteById(opt.get().getMaTaiKhoan());
        }
        return deleted;
    }
}

