package DAO;

import Model.*;
import java.util.List;

public interface IDanhMucDAO {
    List<TheLoai> getAllTheLoai();
    List<Co> getAllCo();
    List<Mau> getAllMau();
    List<Mua> getAllMua();
    List<DoiTuong> getAllDoiTuong();
    List<ChatLieu> getAllChatLieu();
    List<NoiSanXuat> getAllNoiSanXuat();
    List<NhaCungCap> getAllNhaCungCap();
    List<CongViec> getAllCongViec();

    TheLoai getTheLoaiById(int id);
    Co getCoById(int id);
    Mau getMauById(int id);
    Mua getMuaById(int id);
    DoiTuong getDoiTuongById(int id);
    ChatLieu getChatLieuById(int id);
    NoiSanXuat getNoiSanXuatById(int id);
    NhaCungCap getNhaCungCapById(int id);
    CongViec getCongViecById(int id);
}

