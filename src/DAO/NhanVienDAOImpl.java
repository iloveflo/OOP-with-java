package DAO;

import Model.CongViec;
import Model.NhanVien;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NhanVienDAOImpl implements INhanVienDAO {

    private NhanVien mapResultSetToEntity(ResultSet rs) throws SQLException {
        NhanVien nv = new NhanVien();
        nv.setMaNhanVien(String.valueOf(rs.getInt("MaNhanVien")));
        nv.setHoTen(rs.getString("TenNhanVien"));
        nv.setGioiTinh(rs.getString("GioiTinh"));
        nv.setNgaySinh(rs.getDate("NgaySinh"));
        nv.setDiaChi(rs.getString("DiaChi"));
        nv.setSoDienThoai(rs.getString("SoDienThoai"));
        nv.setEmail(rs.getString("Email"));
        nv.setCongViec(new CongViec(rs.getInt("MaCongViec"), rs.getString("TenCongViec")));
        nv.setMaTaiKhoan(rs.getString("MaTaiKhoan"));
        return nv;
    }

    private static final String BASE_SELECT = """
        SELECT nv.MaNhanVien, nv.TenNhanVien, nv.GioiTinh, nv.NgaySinh, nv.DiaChi,
               nv.SoDienThoai, nv.Email, nv.MaTaiKhoan, nv.MaCongViec, cv.TenCongViec
        FROM nhanvien nv
        LEFT JOIN congviec cv ON nv.MaCongViec = cv.MaCongViec
    """;

    @Override
    public Optional<NhanVien> findById(String maNhanVien) {
        String sql = BASE_SELECT + " WHERE nv.MaNhanVien = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(maNhanVien));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEntity(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<NhanVien> findAll() {
        List<NhanVien> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(BASE_SELECT);
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
    public boolean save(NhanVien nv) {
        String sql = """
            INSERT INTO nhanvien (MaNhanVien, TenNhanVien, GioiTinh, NgaySinh, DiaChi, SoDienThoai, Email, MaCongViec, MaTaiKhoan)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(nv.getMaNhanVien()));
            ps.setString(2, nv.getHoTen());
            ps.setString(3, nv.getGioiTinh());
            ps.setDate(4, nv.getNgaySinh() != null ? new java.sql.Date(nv.getNgaySinh().getTime()) : null);
            ps.setString(5, nv.getDiaChi());
            ps.setString(6, nv.getSoDienThoai());
            ps.setString(7, nv.getEmail());
            ps.setObject(8, nv.getCongViec() != null ? nv.getCongViec().getMaCongViec() : null, Types.INTEGER);
            ps.setString(9, nv.getMaTaiKhoan());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(NhanVien nv) {
        String sql = """
            UPDATE nhanvien
            SET TenNhanVien=?, GioiTinh=?, NgaySinh=?, DiaChi=?, SoDienThoai=?, Email=?, MaCongViec=?
            WHERE MaNhanVien=?
        """;
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nv.getHoTen());
            ps.setString(2, nv.getGioiTinh());
            ps.setDate(3, nv.getNgaySinh() != null ? new java.sql.Date(nv.getNgaySinh().getTime()) : null);
            ps.setString(4, nv.getDiaChi());
            ps.setString(5, nv.getSoDienThoai());
            ps.setString(6, nv.getEmail());
            ps.setObject(7, nv.getCongViec() != null ? nv.getCongViec().getMaCongViec() : null, Types.INTEGER);
            ps.setInt(8, Integer.parseInt(nv.getMaNhanVien()));
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteById(String maNhanVien) {
        String sql = "DELETE FROM nhanvien WHERE MaNhanVien = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(maNhanVien));
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Optional<NhanVien> findByMaTaiKhoan(String maTaiKhoan) {
        String sql = BASE_SELECT + " WHERE nv.MaTaiKhoan = ?";
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
    public boolean isEmailOrPhoneExist(String email, String phone, String excludeMaNV) {
        String sql = "SELECT COUNT(*) FROM nhanvien WHERE (Email = ? OR SoDienThoai = ?) AND (? IS NULL OR MaNhanVien != ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, phone);
            ps.setObject(3, excludeMaNV != null ? Integer.parseInt(excludeMaNV) : null, Types.INTEGER);
            ps.setObject(4, excludeMaNV != null ? Integer.parseInt(excludeMaNV) : null, Types.INTEGER);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}

