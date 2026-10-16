package Service.impl;

import DAO.ISanPhamDAO;
import DAO.SanPhamDAOImpl;
import Model.SanPham;
import Service.IProductService;
import Service.exception.ValidationException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class ProductServiceImpl implements IProductService {
    private final ISanPhamDAO sanPhamDAO;

    public ProductServiceImpl() {
        this.sanPhamDAO = new SanPhamDAOImpl();
    }

    public ProductServiceImpl(ISanPhamDAO sanPhamDAO) {
        this.sanPhamDAO = sanPhamDAO;
    }

    private void validateProduct(SanPham sp) {
        if (sp == null) {
            throw new ValidationException("Dữ liệu sản phẩm không được rỗng!");
        }
        if (sp.getTenQuanAo() == null || sp.getTenQuanAo().trim().isEmpty()) {
            throw new ValidationException("Tên sản phẩm không được để trống!");
        }
        if (sp.getDonGiaNhap() != null && sp.getDonGiaNhap().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Đơn giá nhập không được âm!");
        }
        if (sp.getDonGiaBan() != null && sp.getDonGiaBan().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Đơn giá bán không được âm!");
        }
    }

    @Override
    public List<SanPham> getAllProducts() {
        return sanPhamDAO.findAll();
    }

    @Override
    public Optional<SanPham> getProductById(int id) {
        return sanPhamDAO.findById(id);
    }

    @Override
    public boolean addProduct(SanPham sp) {
        validateProduct(sp);
        return sanPhamDAO.save(sp);
    }

    @Override
    public boolean updateProduct(SanPham sp) {
        validateProduct(sp);
        return sanPhamDAO.update(sp);
    }

    @Override
    public boolean deleteProduct(int id) {
        return sanPhamDAO.deleteById(id);
    }

    @Override
    public List<SanPham> filterProducts(Integer maLoai, Integer maCo, Integer maChatLieu, Integer maDoiTuong, Integer maMau, Integer maMua, Integer maNSX) {
        return sanPhamDAO.filter(maLoai, maCo, maChatLieu, maDoiTuong, maMau, maMua, maNSX);
    }

    @Override
    public List<SanPham> getTopSelling(int limit) {
        return sanPhamDAO.getTopSelling(limit <= 0 ? 10 : limit);
    }

    @Override
    public List<SanPham> getLowStockAlert(int threshold) {
        return sanPhamDAO.getLowStock(threshold <= 0 ? 30 : threshold);
    }
}

