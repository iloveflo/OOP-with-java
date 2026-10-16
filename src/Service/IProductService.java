package Service;

import Model.SanPham;
import java.util.List;
import java.util.Optional;

public interface IProductService {
    List<SanPham> getAllProducts();
    Optional<SanPham> getProductById(int id);
    boolean addProduct(SanPham sp);
    boolean updateProduct(SanPham sp);
    boolean deleteProduct(int id);
    List<SanPham> filterProducts(Integer maLoai, Integer maCo, Integer maChatLieu, Integer maDoiTuong, Integer maMau, Integer maMua, Integer maNSX);
    List<SanPham> getTopSelling(int limit);
    List<SanPham> getLowStockAlert(int threshold);
}

