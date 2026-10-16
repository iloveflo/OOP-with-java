package DAO;

import Model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HoaDonNhapDAOImpl implements IHoaDonNhapDAO {

    @Override
    public int taoHoaDonNhap(HoaDonNhap hdn) throws Exception {
        String insertHDN = "INSERT INTO hoadonnhap (MaNhanVien, NgayNhap, MaNCC, TongTien) VALUES (?, NOW(), ?, ?)";
        String insertCT = "INSERT INTO chitiethoadonnhap (SoHoaDonNhap, MaQuanAo, SoLuong, DonGia, GiamGia, ThanhTien) VALUES (?, ?, ?, ?, ?, ?)";
        String updateKho = "UPDATE sanpham SET SoLuong = SoLuong + ?, DonGiaNhap = ? WHERE MaQuanAo = ?";

        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            conn.setAutoCommit(false);

            int soHDN = 0;
            try (PreparedStatement psHD = conn.prepareStatement(insertHDN, Statement.RETURN_GENERATED_KEYS)) {
                psHD.setObject(1, hdn.getNhanVien() != null ? Integer.parseInt(hdn.getNhanVien().getMaNhanVien()) : null, Types.INTEGER);
                psHD.setObject(2, hdn.getNhaCungCap() != null ? hdn.getNhaCungCap().getMaNCC() : null, Types.INTEGER);
                psHD.setBigDecimal(3, hdn.getTongTien());
                psHD.executeUpdate();

                ResultSet keys = psHD.getGeneratedKeys();
                if (keys.next()) {
                    soHDN = keys.getInt(1);
                    hdn.setSoHoaDon(soHDN);
                } else {
                    conn.rollback();
                    throw new SQLException("Không lấy được số hóa đơn nhập tạo mới!");
                }
            }

            try (PreparedStatement psCT = conn.prepareStatement(insertCT);
                 PreparedStatement psKho = conn.prepareStatement(updateKho)) {
                for (ChiTietHoaDon ct : hdn.getDanhSachChiTiet()) {
                    psCT.setInt(1, soHDN);
                    psCT.setInt(2, ct.getSanPham().getMaQuanAo());
                    psCT.setInt(3, ct.getSoLuong());
                    psCT.setBigDecimal(4, ct.getDonGia());
                    psCT.setDouble(5, ct.getGiamGia());
                    psCT.setBigDecimal(6, ct.getThanhTien());
                    psCT.executeUpdate();

                    psKho.setInt(1, ct.getSoLuong());
                    psKho.setBigDecimal(2, ct.getDonGia());
                    psKho.setInt(3, ct.getSanPham().getMaQuanAo());
                    psKho.executeUpdate();
                }
            }

            conn.commit();
            return soHDN;
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
    public Optional<HoaDonNhap> findById(int soHoaDonNhap) {
        String sql = """
            SELECT hd.SoHoaDonNhap, hd.NgayNhap, hd.TongTien,
                   ncc.MaNCC, ncc.TenNCC, ncc.DiaChi AS DiaChi_NCC, ncc.SoDienThoai AS SDT_NCC,
                   nv.MaNhanVien, nv.TenNhanVien
            FROM hoadonnhap hd
            LEFT JOIN nhacungcap ncc ON hd.MaNCC = ncc.MaNCC
            LEFT JOIN nhanvien nv ON hd.MaNhanVien = nv.MaNhanVien
            WHERE hd.SoHoaDonNhap = ?
        """;
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, soHoaDonNhap);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    HoaDonNhap hdn = new HoaDonNhap();
                    hdn.setSoHoaDon(rs.getInt("SoHoaDonNhap"));
                    hdn.setNgayLap(rs.getTimestamp("NgayNhap"));
                    hdn.setTongTien(rs.getBigDecimal("TongTien"));

                    if (rs.getObject("MaNCC") != null) {
                        NhaCungCap ncc = new NhaCungCap();
                        ncc.setMaNCC(rs.getInt("MaNCC"));
                        ncc.setTenNCC(rs.getString("TenNCC"));
                        ncc.setDiaChi(rs.getString("DiaChi_NCC"));
                        ncc.setSoDienThoai(rs.getString("SDT_NCC"));
                        hdn.setNhaCungCap(ncc);
                    }

                    if (rs.getObject("MaNhanVien") != null) {
                        NhanVien nv = new NhanVien();
                        nv.setMaNhanVien(String.valueOf(rs.getInt("MaNhanVien")));
                        nv.setHoTen(rs.getString("TenNhanVien"));
                        hdn.setNhanVien(nv);
                    }

                    hdn.setDanhSachChiTiet(getChiTiet(soHoaDonNhap));
                    return Optional.of(hdn);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<HoaDonNhap> findAll() {
        List<HoaDonNhap> list = new ArrayList<>();
        String sql = "SELECT SoHoaDonNhap FROM hoadonnhap ORDER BY NgayNhap DESC";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                findById(rs.getInt("SoHoaDonNhap")).ifPresent(list::add);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<ChiTietHoaDon> getChiTiet(int soHoaDonNhap) {
        List<ChiTietHoaDon> list = new ArrayList<>();
        String sql = """
            SELECT ct.SoHoaDonNhap, ct.MaQuanAo, ct.SoLuong, ct.DonGia, ct.GiamGia, ct.ThanhTien,
                   s.TenQuanAo, s.Anh
            FROM chitiethoadonnhap ct
            JOIN sanpham s ON ct.MaQuanAo = s.MaQuanAo
            WHERE ct.SoHoaDonNhap = ?
        """;
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, soHoaDonNhap);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SanPham sp = new SanPham();
                    sp.setMaQuanAo(rs.getInt("MaQuanAo"));
                    sp.setTenQuanAo(rs.getString("TenQuanAo"));
                    sp.setAnh(rs.getString("Anh"));

                    ChiTietHoaDon ct = new ChiTietHoaDon();
                    ct.setSoHoaDon(rs.getInt("SoHoaDonNhap"));
                    ct.setSanPham(sp);
                    ct.setSoLuong(rs.getInt("SoLuong"));
                    ct.setDonGia(rs.getBigDecimal("DonGia"));
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

