package DAO;

import Model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SanPhamDAOImpl implements ISanPhamDAO {

    private SanPham mapResultSetToEntity(ResultSet rs) throws SQLException {
        SanPham sp = new SanPham();
        sp.setMaQuanAo(rs.getInt("MaQuanAo"));
        sp.setTenQuanAo(rs.getString("TenQuanAo"));
        sp.setTheLoai(new TheLoai(rs.getInt("MaLoai"), rs.getString("TenLoai")));
        sp.setCo(new Co(rs.getInt("MaCo"), rs.getString("TenCo")));
        sp.setMau(new Mau(rs.getInt("MaMau"), rs.getString("TenMau")));
        sp.setMua(new Mua(rs.getInt("MaMua"), rs.getString("TenMua")));
        sp.setDoiTuong(new DoiTuong(rs.getInt("MaDoiTuong"), rs.getString("TenDoiTuong")));
        sp.setChatLieu(new ChatLieu(rs.getInt("MaChatLieu"), rs.getString("TenChatLieu")));
        sp.setNoiSanXuat(new NoiSanXuat(rs.getInt("MaNSX"), rs.getString("TenNSX")));
        sp.setSoLuong(rs.getInt("SoLuong"));
        sp.setAnh(rs.getString("Anh"));
        sp.setDonGiaNhap(rs.getBigDecimal("DonGiaNhap"));
        sp.setDonGiaBan(rs.getBigDecimal("DonGiaBan"));
        return sp;
    }

    private static final String BASE_SELECT = """
        SELECT s.MaQuanAo, s.TenQuanAo, s.SoLuong, s.Anh, s.DonGiaNhap, s.DonGiaBan,
               s.MaLoai, t.TenLoai,
               s.MaCo, c.TenCo,
               s.MaMau, m.TenMau,
               s.MaMua, mu.TenMua,
               s.MaDoiTuong, dt.TenDoiTuong,
               s.MaChatLieu, cl.TenChatLieu,
               s.MaNSX, ns.TenNSX
        FROM sanpham s
        LEFT JOIN theloai t ON s.MaLoai = t.MaLoai
        LEFT JOIN co c ON s.MaCo = c.MaCo
        LEFT JOIN mau m ON s.MaMau = m.MaMau
        LEFT JOIN mua mu ON s.MaMua = mu.MaMua
        LEFT JOIN doituong dt ON s.MaDoiTuong = dt.MaDoiTuong
        LEFT JOIN chatlieu cl ON s.MaChatLieu = cl.MaChatLieu
        LEFT JOIN noisanxuat ns ON s.MaNSX = ns.MaNSX
    """;

    @Override
    public Optional<SanPham> findById(Integer maQuanAo) {
        String sql = BASE_SELECT + " WHERE s.MaQuanAo = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maQuanAo);
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
    public List<SanPham> findAll() {
        List<SanPham> list = new ArrayList<>();
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
    public boolean save(SanPham sp) {
        String sql = """
            INSERT INTO sanpham (MaQuanAo, TenQuanAo, MaLoai, MaCo, MaChatLieu, MaMau, MaDoiTuong, MaMua, MaNSX, SoLuong, Anh, DonGiaNhap, DonGiaBan)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sp.getMaQuanAo());
            ps.setString(2, sp.getTenQuanAo());
            ps.setObject(3, sp.getTheLoai() != null ? sp.getTheLoai().getMaLoai() : null, Types.INTEGER);
            ps.setObject(4, sp.getCo() != null ? sp.getCo().getMaCo() : null, Types.INTEGER);
            ps.setObject(5, sp.getChatLieu() != null ? sp.getChatLieu().getMaChatLieu() : null, Types.INTEGER);
            ps.setObject(6, sp.getMau() != null ? sp.getMau().getMaMau() : null, Types.INTEGER);
            ps.setObject(7, sp.getDoiTuong() != null ? sp.getDoiTuong().getMaDoiTuong() : null, Types.INTEGER);
            ps.setObject(8, sp.getMua() != null ? sp.getMua().getMaMua() : null, Types.INTEGER);
            ps.setObject(9, sp.getNoiSanXuat() != null ? sp.getNoiSanXuat().getMaNSX() : null, Types.INTEGER);
            ps.setInt(10, sp.getSoLuong());
            ps.setString(11, sp.getAnh());
            ps.setBigDecimal(12, sp.getDonGiaNhap());
            ps.setBigDecimal(13, sp.getDonGiaBan());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(SanPham sp) {
        String sql = """
            UPDATE sanpham
            SET TenQuanAo=?, MaLoai=?, MaCo=?, MaChatLieu=?, MaMau=?, MaDoiTuong=?, MaMua=?, MaNSX=?, SoLuong=?, Anh=?, DonGiaNhap=?, DonGiaBan=?
            WHERE MaQuanAo=?
        """;
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sp.getTenQuanAo());
            ps.setObject(2, sp.getTheLoai() != null ? sp.getTheLoai().getMaLoai() : null, Types.INTEGER);
            ps.setObject(3, sp.getCo() != null ? sp.getCo().getMaCo() : null, Types.INTEGER);
            ps.setObject(4, sp.getChatLieu() != null ? sp.getChatLieu().getMaChatLieu() : null, Types.INTEGER);
            ps.setObject(5, sp.getMau() != null ? sp.getMau().getMaMau() : null, Types.INTEGER);
            ps.setObject(6, sp.getDoiTuong() != null ? sp.getDoiTuong().getMaDoiTuong() : null, Types.INTEGER);
            ps.setObject(7, sp.getMua() != null ? sp.getMua().getMaMua() : null, Types.INTEGER);
            ps.setObject(8, sp.getNoiSanXuat() != null ? sp.getNoiSanXuat().getMaNSX() : null, Types.INTEGER);
            ps.setInt(9, sp.getSoLuong());
            ps.setString(10, sp.getAnh());
            ps.setBigDecimal(11, sp.getDonGiaNhap());
            ps.setBigDecimal(12, sp.getDonGiaBan());
            ps.setInt(13, sp.getMaQuanAo());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteById(Integer maQuanAo) {
        String sql = "DELETE FROM sanpham WHERE MaQuanAo = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maQuanAo);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<SanPham> filter(Integer maLoai, Integer maCo, Integer maChatLieu, Integer maDoiTuong, Integer maMau, Integer maMua, Integer maNSX) {
        List<SanPham> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(BASE_SELECT).append(" WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (maLoai != null) { sql.append(" AND s.MaLoai = ? "); params.add(maLoai); }
        if (maCo != null) { sql.append(" AND s.MaCo = ? "); params.add(maCo); }
        if (maChatLieu != null) { sql.append(" AND s.MaChatLieu = ? "); params.add(maChatLieu); }
        if (maDoiTuong != null) { sql.append(" AND s.MaDoiTuong = ? "); params.add(maDoiTuong); }
        if (maMau != null) { sql.append(" AND s.MaMau = ? "); params.add(maMau); }
        if (maMua != null) { sql.append(" AND s.MaMua = ? "); params.add(maMua); }
        if (maNSX != null) { sql.append(" AND s.MaNSX = ? "); params.add(maNSX); }

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToEntity(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<SanPham> getTopSelling(int limit) {
        List<SanPham> list = new ArrayList<>();
        String sql = """
            SELECT s.MaQuanAo, s.TenQuanAo, s.SoLuong, s.Anh, s.DonGiaNhap, s.DonGiaBan,
                   s.MaLoai, t.TenLoai, s.MaCo, c.TenCo, s.MaMau, m.TenMau,
                   s.MaMua, mu.TenMua, s.MaDoiTuong, dt.TenDoiTuong,
                   s.MaChatLieu, cl.TenChatLieu, s.MaNSX, ns.TenNSX,
                   SUM(ct.SoLuong) AS TongBan
            FROM sanpham s
            JOIN chitiethoadonban ct ON s.MaQuanAo = ct.MaQuanAo
            LEFT JOIN theloai t ON s.MaLoai = t.MaLoai
            LEFT JOIN co c ON s.MaCo = c.MaCo
            LEFT JOIN mau m ON s.MaMau = m.MaMau
            LEFT JOIN mua mu ON s.MaMua = mu.MaMua
            LEFT JOIN doituong dt ON s.MaDoiTuong = dt.MaDoiTuong
            LEFT JOIN chatlieu cl ON s.MaChatLieu = cl.MaChatLieu
            LEFT JOIN noisanxuat ns ON s.MaNSX = ns.MaNSX
            GROUP BY s.MaQuanAo, s.TenQuanAo, s.SoLuong, s.Anh, s.DonGiaNhap, s.DonGiaBan,
                     s.MaLoai, t.TenLoai, s.MaCo, c.TenCo, s.MaMau, m.TenMau,
                     s.MaMua, mu.TenMua, s.MaDoiTuong, dt.TenDoiTuong,
                     s.MaChatLieu, cl.TenChatLieu, s.MaNSX, ns.TenNSX
            ORDER BY TongBan DESC
            LIMIT ?
        """;
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SanPham sp = mapResultSetToEntity(rs);
                    sp.setSoLuong(rs.getInt("TongBan"));
                    list.add(sp);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<SanPham> getLowStock(int threshold) {
        List<SanPham> list = new ArrayList<>();
        String sql = BASE_SELECT + " WHERE s.SoLuong <= ? ORDER BY s.SoLuong ASC";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, threshold);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToEntity(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean updateSoLuong(int maQuanAo, int delta) {
        String sql = "UPDATE sanpham SET SoLuong = SoLuong + ? WHERE MaQuanAo = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, delta);
            ps.setInt(2, maQuanAo);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}

