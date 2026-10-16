package DAO;

import Model.ItemGioHang;
import Model.SanPham;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GioHangDAOImpl implements IGioHangDAO {

    @Override
    public List<ItemGioHang> getGioHangByKhachHang(String maKhachHang, Integer trangThai) {
        List<ItemGioHang> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
            SELECT g.MaKhachHang, g.MaQuanAo, g.DonGiaBan, g.SoLuongDat, g.TongTien, g.TrangThai,
                   s.TenQuanAo, s.Anh, s.SoLuong AS TonKho
            FROM giohang g
            JOIN sanpham s ON g.MaQuanAo = s.MaQuanAo
            WHERE g.MaKhachHang = ?
        """);
        if (trangThai != null) {
            sql.append(" AND g.TrangThai = ?");
        }

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            ps.setString(1, maKhachHang);
            if (trangThai != null) {
                ps.setInt(2, trangThai);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SanPham sp = new SanPham();
                    sp.setMaQuanAo(rs.getInt("MaQuanAo"));
                    sp.setTenQuanAo(rs.getString("TenQuanAo"));
                    sp.setAnh(rs.getString("Anh"));
                    sp.setSoLuong(rs.getInt("TonKho"));

                    ItemGioHang item = new ItemGioHang();
                    item.setMaKhachHang(rs.getString("MaKhachHang"));
                    item.setSanPham(sp);
                    item.setDonGiaBan(rs.getBigDecimal("DonGiaBan"));
                    item.setSoLuongDat(rs.getInt("SoLuongDat"));
                    item.setTongTien(rs.getBigDecimal("TongTien"));
                    item.setTrangThai(rs.getInt("TrangThai"));

                    list.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<ItemGioHang> getGioHangByTrangThai(int trangThai) {
        List<ItemGioHang> list = new ArrayList<>();
        String sql = """
            SELECT g.MaKhachHang, g.MaQuanAo, g.DonGiaBan, g.SoLuongDat, g.TongTien, g.TrangThai,
                   s.TenQuanAo, s.Anh, s.SoLuong AS TonKho
            FROM giohang g
            JOIN sanpham s ON g.MaQuanAo = s.MaQuanAo
            WHERE g.TrangThai = ?
        """;
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, trangThai);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SanPham sp = new SanPham();
                    sp.setMaQuanAo(rs.getInt("MaQuanAo"));
                    sp.setTenQuanAo(rs.getString("TenQuanAo"));
                    sp.setAnh(rs.getString("Anh"));
                    sp.setSoLuong(rs.getInt("TonKho"));

                    ItemGioHang item = new ItemGioHang();
                    item.setMaKhachHang(rs.getString("MaKhachHang"));
                    item.setSanPham(sp);
                    item.setDonGiaBan(rs.getBigDecimal("DonGiaBan"));
                    item.setSoLuongDat(rs.getInt("SoLuongDat"));
                    item.setTongTien(rs.getBigDecimal("TongTien"));
                    item.setTrangThai(rs.getInt("TrangThai"));

                    list.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean themVaoGio(String maKhachHang, int maQuanAo, BigDecimal donGiaBan, int soLuong) {
        String sql = """
            INSERT INTO giohang (MaKhachHang, MaQuanAo, DonGiaBan, SoLuongDat, TrangThai)
            VALUES (?, ?, ?, ?, 0)
            ON DUPLICATE KEY UPDATE SoLuongDat = SoLuongDat + ?, DonGiaBan = ?
        """;
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maKhachHang);
            ps.setInt(2, maQuanAo);
            ps.setBigDecimal(3, donGiaBan);
            ps.setInt(4, soLuong);
            ps.setInt(5, soLuong);
            ps.setBigDecimal(6, donGiaBan);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean capNhatSoLuong(String maKhachHang, int maQuanAo, int soLuongMoi) {
        String sql = "UPDATE giohang SET SoLuongDat = ? WHERE MaKhachHang = ? AND MaQuanAo = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, soLuongMoi);
            ps.setString(2, maKhachHang);
            ps.setInt(3, maQuanAo);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean xoaKhoiGio(String maKhachHang, int maQuanAo) {
        String sql = "DELETE FROM giohang WHERE MaKhachHang = ? AND MaQuanAo = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maKhachHang);
            ps.setInt(2, maQuanAo);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateTrangThai(String maKhachHang, int trangThaiMoi) {
        String sql = "UPDATE giohang SET TrangThai = ? WHERE MaKhachHang = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, trangThaiMoi);
            ps.setString(2, maKhachHang);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public int countItemTrongGio(String maKhachHang) {
        String sql = "SELECT COUNT(*) FROM giohang WHERE MaKhachHang = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maKhachHang);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}

