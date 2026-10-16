package Service.impl;

import DAO.*;
import Model.*;
import Service.IImportService;
import Service.exception.BusinessException;
import Service.exception.ValidationException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class ImportServiceImpl implements IImportService {
    private final IHoaDonNhapDAO hoaDonNhapDAO;
    private final ISanPhamDAO sanPhamDAO;
    private final IDanhMucDAO danhMucDAO;
    private final INhanVienDAO nhanVienDAO;

    public ImportServiceImpl() {
        this.hoaDonNhapDAO = new HoaDonNhapDAOImpl();
        this.sanPhamDAO = new SanPhamDAOImpl();
        this.danhMucDAO = new DanhMucDAOImpl();
        this.nhanVienDAO = new NhanVienDAOImpl();
    }

    public ImportServiceImpl(IHoaDonNhapDAO hoaDonNhapDAO, ISanPhamDAO sanPhamDAO, IDanhMucDAO danhMucDAO, INhanVienDAO nhanVienDAO) {
        this.hoaDonNhapDAO = hoaDonNhapDAO;
        this.sanPhamDAO = sanPhamDAO;
        this.danhMucDAO = danhMucDAO;
        this.nhanVienDAO = nhanVienDAO;
    }

    @Override
    public HoaDonNhap lapHoaDonNhap(String maNhanVien, int maNCC, int maQuanAo, int soLuong, BigDecimal donGiaNhap, double giamGia) throws Exception {
        if (soLuong <= 0) {
            throw new ValidationException("Số lượng nhập phải lớn hơn 0!");
        }
        if (donGiaNhap == null || donGiaNhap.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Đơn giá nhập phải lớn hơn 0!");
        }

        SanPham sp = sanPhamDAO.findById(maQuanAo)
                .orElseThrow(() -> new BusinessException("Sản phẩm không tồn tại!"));

        NhaCungCap ncc = danhMucDAO.getNhaCungCapById(maNCC);
        if (ncc == null) {
            throw new BusinessException("Nhà cung cấp không tồn tại!");
        }

        NhanVien nv = nhanVienDAO.findById(maNhanVien).orElse(null);

        HoaDonNhap hdn = new HoaDonNhap();
        hdn.setNhanVien(nv);
        hdn.setNhaCungCap(ncc);
        hdn.setNgayLap(new Date());

        ChiTietHoaDon ct = new ChiTietHoaDon();
        ct.setSanPham(sp);
        ct.setSoLuong(soLuong);
        ct.setDonGia(donGiaNhap);
        ct.setGiamGia(giamGia);
        ct.setThanhTien(ct.tinhThanhTien());
        hdn.themChiTiet(ct);

        int soHDN = hoaDonNhapDAO.taoHoaDonNhap(hdn);
        hdn.setSoHoaDon(soHDN);
        return hdn;
    }

    @Override
    public List<HoaDonNhap> getAllHoaDonNhap() {
        return hoaDonNhapDAO.findAll();
    }

    @Override
    public Optional<HoaDonNhap> getHoaDonNhapById(int soHDN) {
        return hoaDonNhapDAO.findById(soHDN);
    }
}

