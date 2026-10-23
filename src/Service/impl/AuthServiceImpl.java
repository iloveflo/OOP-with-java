package Service.impl;

import DAO.*;
import Model.KhachHang;
import Model.NhanVien;
import Model.TaiKhoan;
import Service.IAuthService;
import Service.UserSession;
import Service.dto.CaptchaDTO;
import Service.exception.AuthenticationException;
import Service.exception.ValidationException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;
import java.util.Random;

public class AuthServiceImpl implements IAuthService {
    private final ITaiKhoanDAO taiKhoanDAO;
    private final IKhachHangDAO khachHangDAO;
    private final INhanVienDAO nhanVienDAO;
    private final Random random = new Random();

    public AuthServiceImpl() {
        this.taiKhoanDAO = new TaiKhoanDAOImpl();
        this.khachHangDAO = new KhachHangDAOImpl();
        this.nhanVienDAO = new NhanVienDAOImpl();
    }

    public AuthServiceImpl(ITaiKhoanDAO taiKhoanDAO, IKhachHangDAO khachHangDAO, INhanVienDAO nhanVienDAO) {
        this.taiKhoanDAO = taiKhoanDAO;
        this.khachHangDAO = khachHangDAO;
        this.nhanVienDAO = nhanVienDAO;
    }

    @Override
    public TaiKhoan login(String username, String password, String inputCaptcha, CaptchaDTO currentCaptcha) {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new ValidationException("Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu!");
        }

        if (currentCaptcha == null || !currentCaptcha.validate(inputCaptcha)) {
            throw new AuthenticationException("Mã bảo vệ (CAPTCHA) không chính xác!");
        }

        Optional<TaiKhoan> optTK = taiKhoanDAO.authenticate(username.trim(), password);
        if (optTK.isEmpty()) {
            throw new AuthenticationException("Tên đăng nhập hoặc mật khẩu không chính xác!");
        }

        TaiKhoan tk = optTK.get();
        if (tk.isDangNhap()) {
            throw new AuthenticationException("Tài khoản hiện đang được đăng nhập ở một thiết bị khác!");
        }

        // Cập nhật trạng thái đã đăng nhập
        taiKhoanDAO.updateTrangThaiDangNhap(tk.getMaTaiKhoan(), true);
        tk.setDangNhap(true);

        // Nạp thông tin người dùng vào Session
        switch (tk.getLoaiTaiKhoan()) {
            case KHACH_HANG -> {
                Optional<KhachHang> kh = khachHangDAO.findByMaTaiKhoan(tk.getMaTaiKhoan());
                UserSession.getInstance().startSession(tk, kh.orElse(null));
            }
            case NHAN_VIEN -> {
                Optional<NhanVien> nv = nhanVienDAO.findByMaTaiKhoan(tk.getMaTaiKhoan());
                UserSession.getInstance().startSession(tk, nv.orElse(null));
            }
            case ADMIN -> UserSession.getInstance().startSession(tk, null);
        }

        return tk;
    }

    @Override
    public void logout(String maTaiKhoan) {
        if (maTaiKhoan != null && !maTaiKhoan.isEmpty()) {
            taiKhoanDAO.updateTrangThaiDangNhap(maTaiKhoan, false);
        }
        UserSession.getInstance().clearSession();
    }

    @Override
    public CaptchaDTO getRandomCaptcha() {
        String countSql = "SELECT COUNT(*) FROM capcha";
        String selectSql = "SELECT MaAnh, LinkAnh, KetQua FROM capcha LIMIT ?, 1";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement psCount = conn.prepareStatement(countSql);
             ResultSet rsCount = psCount.executeQuery()) {
            if (rsCount.next()) {
                int total = rsCount.getInt(1);
                if (total > 0) {
                    int offset = random.nextInt(total);
                    try (PreparedStatement psSel = conn.prepareStatement(selectSql)) {
                        psSel.setInt(1, offset);
                        try (ResultSet rs = psSel.executeQuery()) {
                            if (rs.next()) {
                                return new CaptchaDTO(rs.getInt("MaAnh"), rs.getString("LinkAnh"), rs.getString("KetQua"));
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            // DB connection or query failed
        }
        // Fallback in-memory captcha
        String chars = "23456789abcdefhkmnprstuvwxyz";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return new CaptchaDTO(0, "", sb.toString());
    }
}

