package DAO;

import Model.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DanhMucDAOImpl implements IDanhMucDAO {

    @Override
    public List<TheLoai> getAllTheLoai() {
        List<TheLoai> list = new ArrayList<>();
        String sql = "SELECT MaLoai, TenLoai FROM theloai";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new TheLoai(rs.getInt("MaLoai"), rs.getString("TenLoai")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Co> getAllCo() {
        List<Co> list = new ArrayList<>();
        String sql = "SELECT MaCo, TenCo FROM co";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Co(rs.getInt("MaCo"), rs.getString("TenCo")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Mau> getAllMau() {
        List<Mau> list = new ArrayList<>();
        String sql = "SELECT MaMau, TenMau FROM mau";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Mau(rs.getInt("MaMau"), rs.getString("TenMau")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Mua> getAllMua() {
        List<Mua> list = new ArrayList<>();
        String sql = "SELECT MaMua, TenMua FROM mua";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Mua(rs.getInt("MaMua"), rs.getString("TenMua")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<DoiTuong> getAllDoiTuong() {
        List<DoiTuong> list = new ArrayList<>();
        String sql = "SELECT MaDoiTuong, TenDoiTuong FROM doituong";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new DoiTuong(rs.getInt("MaDoiTuong"), rs.getString("TenDoiTuong")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<ChatLieu> getAllChatLieu() {
        List<ChatLieu> list = new ArrayList<>();
        String sql = "SELECT MaChatLieu, TenChatLieu FROM chatlieu";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new ChatLieu(rs.getInt("MaChatLieu"), rs.getString("TenChatLieu")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<NoiSanXuat> getAllNoiSanXuat() {
        List<NoiSanXuat> list = new ArrayList<>();
        String sql = "SELECT MaNSX, TenNSX FROM noisanxuat";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new NoiSanXuat(rs.getInt("MaNSX"), rs.getString("TenNSX")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<NhaCungCap> getAllNhaCungCap() {
        List<NhaCungCap> list = new ArrayList<>();
        String sql = "SELECT MaNCC, TenNCC, DiaChi, SoDienThoai FROM nhacungcap";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new NhaCungCap(rs.getInt("MaNCC"), rs.getString("TenNCC"), rs.getString("DiaChi"), rs.getString("SoDienThoai")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<CongViec> getAllCongViec() {
        List<CongViec> list = new ArrayList<>();
        String sql = "SELECT MaCongViec, TenCongViec FROM congviec";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new CongViec(rs.getInt("MaCongViec"), rs.getString("TenCongViec")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public TheLoai getTheLoaiById(int id) {
        String sql = "SELECT MaLoai, TenLoai FROM theloai WHERE MaLoai = ?";
        return querySingleCategory(sql, id, (rs) -> new TheLoai(rs.getInt(1), rs.getString(2)));
    }

    @Override
    public Co getCoById(int id) {
        String sql = "SELECT MaCo, TenCo FROM co WHERE MaCo = ?";
        return querySingleCategory(sql, id, (rs) -> new Co(rs.getInt(1), rs.getString(2)));
    }

    @Override
    public Mau getMauById(int id) {
        String sql = "SELECT MaMau, TenMau FROM mau WHERE MaMau = ?";
        return querySingleCategory(sql, id, (rs) -> new Mau(rs.getInt(1), rs.getString(2)));
    }

    @Override
    public Mua getMuaById(int id) {
        String sql = "SELECT MaMua, TenMua FROM mua WHERE MaMua = ?";
        return querySingleCategory(sql, id, (rs) -> new Mua(rs.getInt(1), rs.getString(2)));
    }

    @Override
    public DoiTuong getDoiTuongById(int id) {
        String sql = "SELECT MaDoiTuong, TenDoiTuong FROM doituong WHERE MaDoiTuong = ?";
        return querySingleCategory(sql, id, (rs) -> new DoiTuong(rs.getInt(1), rs.getString(2)));
    }

    @Override
    public ChatLieu getChatLieuById(int id) {
        String sql = "SELECT MaChatLieu, TenChatLieu FROM chatlieu WHERE MaChatLieu = ?";
        return querySingleCategory(sql, id, (rs) -> new ChatLieu(rs.getInt(1), rs.getString(2)));
    }

    @Override
    public NoiSanXuat getNoiSanXuatById(int id) {
        String sql = "SELECT MaNSX, TenNSX FROM noisanxuat WHERE MaNSX = ?";
        return querySingleCategory(sql, id, (rs) -> new NoiSanXuat(rs.getInt(1), rs.getString(2)));
    }

    @Override
    public NhaCungCap getNhaCungCapById(int id) {
        String sql = "SELECT MaNCC, TenNCC, DiaChi, SoDienThoai FROM nhacungcap WHERE MaNCC = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new NhaCungCap(rs.getInt("MaNCC"), rs.getString("TenNCC"), rs.getString("DiaChi"), rs.getString("SoDienThoai"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public CongViec getCongViecById(int id) {
        String sql = "SELECT MaCongViec, TenCongViec FROM congviec WHERE MaCongViec = ?";
        return querySingleCategory(sql, id, (rs) -> new CongViec(rs.getInt(1), rs.getString(2)));
    }

    @FunctionalInterface
    private interface RowMapper<R> {
        R map(ResultSet rs) throws SQLException;
    }

    private <R> R querySingleCategory(String sql, int id, RowMapper<R> mapper) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapper.map(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

