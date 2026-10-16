package DAO;

import Model.LoaiTaiKhoan;
import Model.TaiKhoan;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TaiKhoanDAOImpl implements ITaiKhoanDAO {

    private TaiKhoan mapResultSetToEntity(ResultSet rs) throws SQLException {
        String maTK = rs.getString("MaTaiKhoan");
        String tenDN = rs.getString("TenDangNhap");
        String matKhau = rs.getString("MatKhau");
        String loaiStr = rs.getString("LoaiTaiKhoan");
        boolean dangNhap = rs.getBoolean("DangNhap");
        return new TaiKhoan(maTK, tenDN, matKhau, LoaiTaiKhoan.fromString(loaiStr), dangNhap);
    }

    @Override
    public Optional<TaiKhoan> findById(String maTaiKhoan) {
        String sql = "SELECT * FROM taikhoan WHERE MaTaiKhoan = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maTaiKhoan);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEntity(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<TaiKhoan> findAll() {
        List<TaiKhoan> list = new ArrayList<>();
        String sql = "SELECT * FROM taikhoan";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean save(TaiKhoan tk) {
        String sql = "INSERT INTO taikhoan (MaTaiKhoan, TenDangNhap, MatKhau, LoaiTaiKhoan, DangNhap) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tk.getMaTaiKhoan());
            ps.setString(2, tk.getTenDangNhap());
            ps.setString(3, tk.getMatKhau());
            ps.setString(4, tk.getLoaiTaiKhoan().getValue());
            ps.setBoolean(5, tk.isDangNhap());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(TaiKhoan tk) {
        String sql = "UPDATE taikhoan SET TenDangNhap = ?, MatKhau = ?, LoaiTaiKhoan = ?, DangNhap = ? WHERE MaTaiKhoan = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tk.getTenDangNhap());
            ps.setString(2, tk.getMatKhau());
            ps.setString(3, tk.getLoaiTaiKhoan().getValue());
            ps.setBoolean(4, tk.isDangNhap());
            ps.setString(5, tk.getMaTaiKhoan());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteById(String maTaiKhoan) {
        String sql = "DELETE FROM taikhoan WHERE MaTaiKhoan = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maTaiKhoan);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Optional<TaiKhoan> findByTenDangNhap(String username) {
        String sql = "SELECT * FROM taikhoan WHERE TenDangNhap = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEntity(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<TaiKhoan> authenticate(String username, String password) {
        String sql = "SELECT * FROM taikhoan WHERE TenDangNhap = ? AND MatKhau = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEntity(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public boolean updateTrangThaiDangNhap(String maTaiKhoan, boolean dangNhap) {
        String sql = "UPDATE taikhoan SET DangNhap = ? WHERE MaTaiKhoan = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, dangNhap);
            ps.setString(2, maTaiKhoan);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateMatKhau(String tenDangNhap, String matKhauMoi) {
        String sql = "UPDATE taikhoan SET MatKhau = ? WHERE TenDangNhap = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, matKhauMoi);
            ps.setString(2, tenDangNhap);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean isExist(String username, String maTaiKhoan) {
        String sql = "SELECT COUNT(*) FROM taikhoan WHERE TenDangNhap = ? OR MaTaiKhoan = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, maTaiKhoan);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}

