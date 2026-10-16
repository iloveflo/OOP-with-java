package DAO;

import Model.SanPham;
import java.util.List;

public interface ISanPhamDAO extends IGenericDAO<SanPham, Integer> {
    List<SanPham> filter(Integer maLoai, Integer maCo, Integer maChatLieu, Integer maDoiTuong, Integer maMau, Integer maMua, Integer maNSX);
    List<SanPham> getTopSelling(int limit);
    List<SanPham> getLowStock(int threshold);
    boolean updateSoLuong(int maQuanAo, int delta);
}

