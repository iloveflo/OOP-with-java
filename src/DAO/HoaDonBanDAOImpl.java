package DAO;

import Model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HoaDonBanDAOImpl implements IHoaDonBanDAO {

    @Override
    public int taoHoaDonBan(HoaDonBan hdb) throws Exception {
        String insertHD = "INSERT INTO hoadonban (MaKhachHang, NgayBan, MaNhanVien, TongTien) VALUES (?, NOW(), ?, ?)";
        String insertCT = "INSERT INTO chitiethoadonban (SoHoaDonBan, MaQuanAo, SoLuong, GiamGia, ThanhTien) VALUES (?, ?, ?, ?, ?)";
        String updateKho = "UPDATE sanpham SET SoLuong = SoLuong - ? WHERE MaQuanAo = ?";

        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            conn.setAutoCommit(false);

            int soHD = 0;
            try (PreparedStatement psHD = conn.prepareStatement(insertHD, Statement.RETURN_GENERATED_KEYS)) {
                psHD.setString(1, hdb.getKhachHang() != null ? hdb.getKhachHang().getMaKhachHang() : null);
                psHD.setObject(2, hdb.getNhanVien() != null ? Integer.parseInt(hdb.getNhanVien().getMaNhanVien()) : null, Types.INTEGER);
                psHD.setBigDecimal(3, hdb.getTongTien());
                psHD.executeUpdate();

                ResultSet keys = psHD.getGeneratedKeys();
                if (keys.next()) {
                    soHD = keys.getInt(1);
                    hdb.setSoHoaDon(soHD);
                } else {
                    conn.rollback();
                    throw new SQLException("Không lấy được số hóa đơn tạo mới!");
                }
            }

            try (PreparedStatement psCT = conn.prepareStatement(insertCT);
                 PreparedStatement psKho = conn.prepareStatement(updateKho)) {
                for (ChiTietHoaDon ct : hdb.getDanhSachChiTiet()) {
                    psCT.setInt(1, soHD);
                    psCT.setInt(2, ct.getSanPham().getMaQuanAo());
                    psCT.setInt(3, ct.getSoLuong());
                    psCT.setDouble(4, ct.getGiamGia());
                    psCT.setBigDecimal(5, ct.getThanhTien());
                    psCT.executeUpdate();

                    psKho.setInt(1, ct.getSoLuong());
                    psKho.setInt(2, ct.getSanPham().getMaQuanAo());
                    psKho.executeUpdate();
                }
            }

            conn.commit();
            return soHD;
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            throw e;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    @Override
    public Optional<HoaDonBan> findById(int soHoaDonBan) {
        String sql = """
            SELECT hdb.SoHoaDonBan, hdb.NgayBan, hdb.TongTien,
                   kh.MaKhachHang, kh.TenKhach, kh.SoDienThoai AS SDT_KH, kh.DiaChi AS DiaChi_KH, kh.Email AS Email_KH,
                   nv.MaNhanVien, nv.TenNhanVien
            FROM hoadonban hdb
            LEFT JOIN khachhang kh ON hdb.MaKhachHang = kh.MaKhachHang
            LEFT JOIN nhanvien nv ON hdb.MaNhanVien = nv.MaNhanVien
            WHERE hdb.SoHoaDonBan = ?
        """;
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, soHoaDonBan);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    HoaDonBan hdb = new HoaDonBan();
                    hdb.setSoHoaDon(rs.getInt("SoHoaDonBan"));
                    hdb.setNgayLap(rs.getTimestamp("NgayBan"));
                    hdb.setTongTien(rs.getBigDecimal("TongTien"));

                    if (rs.getString("MaKhachHang") != null) {
                        KhachHang kh = new KhachHang();
                        kh.setMaKhachHang(rs.getString("MaKhachHang"));
                        kh.setHoTen(rs.getString("TenKhach"));
                        kh.setSoDienThoai(rs.getString("SDT_KH"));
                        kh.setDiaChi(rs.getString("DiaChi_KH"));
                        kh.setEmail(rs.getString("Email_KH"));
                        hdb.setKhachHang(kh);
                    }

                    if (rs.getObject("MaNhanVien") != null) {
                        NhanVien nv = new NhanVien();
                        nv.setMaNhanVien(String.valueOf(rs.getInt("MaNhanVien")));
                        nv.setHoTen(rs.getString("TenNhanVien"));
                        hdb.setNhanVien(nv);
                    }

                    hdb.setDanhSachChiTiet(getChiTiet(soHoaDonBan));
                    return Optional.of(hdb);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<HoaDonBan> findByKhachHang(String maKhachHang) {
        List<HoaDonBan> list = new ArrayList<>();
        String sql = "SELECT SoHoaDonBan FROM hoadonban WHERE MaKhachHang = ? ORDER BY NgayBan DESC";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maKhachHang);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    findById(rs.getInt("SoHoaDonBan")).ifPresent(list::add);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<HoaDonBan> findAll() {
        List<HoaDonBan> list = new ArrayList<>();
        String sql = "SELECT SoHoaDonBan FROM hoadonban ORDER BY NgayBan DESC";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                findById(rs.getInt("SoHoaDonBan")).ifPresent(list::add);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<ChiTietHoaDon> getChiTiet(int soHoaDonBan) {
        List<ChiTietHoaDon> list = new ArrayList<>();
        String sql = """
            SELECT ct.SoHoaDonBan, ct.MaQuanAo, ct.SoLuong, ct.GiamGia, ct.ThanhTien,
                   s.TenQuanAo, s.Anh, s.DonGiaBan
            FROM chitiethoadonban ct
            JOIN sanpham s ON ct.MaQuanAo = s.MaQuanAo
            WHERE ct.SoHoaDonBan = ?
        """;
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, soHoaDonBan);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SanPham sp = new SanPham();
                    sp.setMaQuanAo(rs.getInt("MaQuanAo"));
                    sp.setTenQuanAo(rs.getString("TenQuanAo"));
                    sp.setAnh(rs.getString("Anh"));
                    sp.setDonGiaBan(rs.getBigDecimal("DonGiaBan"));

                    ChiTietHoaDon ct = new ChiTietHoaDon();
                    ct.setSoHoaDon(rs.getInt("SoHoaDonBan"));
                    ct.setSanPham(sp);
                    ct.setSoLuong(rs.getInt("SoLuong"));
                    ct.setDonGia(rs.getBigDecimal("DonGiaBan"));
                    ct.setGiamGia(rs.getDouble("GiamGia"));
                    ct.setThanhTien(rs.getBigDecimal("ThanhTien"));

                    list.add(ct);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}

