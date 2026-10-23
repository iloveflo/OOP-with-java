package Service.impl;

import DAO.*;
import Model.*;
import Service.IOrderService;
import Service.exception.BusinessException;
import Service.exception.ValidationException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class OrderServiceImpl implements IOrderService {
    private final IGioHangDAO gioHangDAO;
    private final ISanPhamDAO sanPhamDAO;
    private final IHoaDonBanDAO hoaDonBanDAO;
    private final IKhachHangDAO khachHangDAO;
    private final INhanVienDAO nhanVienDAO;

    public OrderServiceImpl() {
        this.gioHangDAO = new GioHangDAOImpl();
        this.sanPhamDAO = new SanPhamDAOImpl();
        this.hoaDonBanDAO = new HoaDonBanDAOImpl();
        this.khachHangDAO = new KhachHangDAOImpl();
        this.nhanVienDAO = new NhanVienDAOImpl();
    }

    public OrderServiceImpl(IGioHangDAO gioHangDAO, ISanPhamDAO sanPhamDAO, IHoaDonBanDAO hoaDonBanDAO, IKhachHangDAO khachHangDAO, INhanVienDAO nhanVienDAO) {
        this.gioHangDAO = gioHangDAO;
        this.sanPhamDAO = sanPhamDAO;
        this.hoaDonBanDAO = hoaDonBanDAO;
        this.khachHangDAO = khachHangDAO;
        this.nhanVienDAO = nhanVienDAO;
    }

    @Override
    public List<ItemGioHang> getGioHang(String maKhachHang, Integer trangThai) {
        return gioHangDAO.getGioHangByKhachHang(maKhachHang, trangThai);
    }

    @Override
    public List<ItemGioHang> getGioHangByTrangThai(int trangThai) {
        return gioHangDAO.getGioHangByTrangThai(trangThai);
    }

    @Override
    public void themVaoGioHang(String maKhachHang, int maQuanAo, int soLuong) {
        if (soLuong <= 0) {
            throw new ValidationException("Số lượng đặt phải lớn hơn 0!");
        }

        Optional<SanPham> optSP = sanPhamDAO.findById(maQuanAo);
        if (optSP.isEmpty()) {
            throw new BusinessException("Sản phẩm không tồn tại!");
        }

        SanPham sp = optSP.get();
        if (!sp.kiemTraConHang(soLuong)) {
            throw new BusinessException("Không đủ số lượng trong kho! Tồn kho hiện tại: " + sp.getSoLuong());
        }

        gioHangDAO.themVaoGio(maKhachHang, maQuanAo, sp.getDonGiaBan(), soLuong);
    }

    @Override
    public void capNhatSoLuong(String maKhachHang, int maQuanAo, int soLuongMoi) {
        if (soLuongMoi <= 0) {
            gioHangDAO.xoaKhoiGio(maKhachHang, maQuanAo);
            return;
        }

        Optional<SanPham> optSP = sanPhamDAO.findById(maQuanAo);
        if (optSP.isPresent() && !optSP.get().kiemTraConHang(soLuongMoi)) {
            throw new BusinessException("Tồn kho không đủ! Hiện còn: " + optSP.get().getSoLuong());
        }

        gioHangDAO.capNhatSoLuong(maKhachHang, maQuanAo, soLuongMoi);
    }

    @Override
    public void xoaKhoiGioHang(String maKhachHang, int maQuanAo) {
        gioHangDAO.xoaKhoiGio(maKhachHang, maQuanAo);
    }

    @Override
    public void datHang(String maKhachHang) {
        List<ItemGioHang> items = gioHangDAO.getGioHangByKhachHang(maKhachHang, 0);
        if (items.isEmpty()) {
            throw new BusinessException("Giỏ hàng của bạn đang trống!");
        }

        // Kiểm tra tồn kho trước khi đổi trạng thái
        for (ItemGioHang item : items) {
            Optional<SanPham> optSP = sanPhamDAO.findById(item.getSanPham().getMaQuanAo());
            if (optSP.isEmpty() || !optSP.get().kiemTraConHang(item.getSoLuongDat())) {
                throw new BusinessException("Sản phẩm '" + item.getSanPham().getTenQuanAo() + "' không đủ số lượng trong kho!");
            }
        }

        gioHangDAO.updateTrangThai(maKhachHang, 1);
    }

    @Override
    public HoaDonBan lapHoaDonBan(String maKhachHang, String maNhanVien, List<ItemGioHang> items) throws Exception {
        if (items == null || items.isEmpty()) {
            throw new ValidationException("Danh sách mặt hàng thanh toán không được rỗng!");
        }

        KhachHang kh = khachHangDAO.findById(maKhachHang).orElse(null);
        NhanVien nv = nhanVienDAO.findById(maNhanVien).orElse(null);

        HoaDonBan hdb = new HoaDonBan();
        hdb.setKhachHang(kh);
        hdb.setNhanVien(nv);
        hdb.setNgayLap(new Date());

        for (ItemGioHang item : items) {
            SanPham sp = sanPhamDAO.findById(item.getSanPham().getMaQuanAo())
                    .orElseThrow(() -> new BusinessException("Sản phẩm không tồn tại!"));

            if (!sp.kiemTraConHang(item.getSoLuongDat())) {
                throw new BusinessException("Sản phẩm '" + sp.getTenQuanAo() + "' không đủ tồn kho để thanh toán!");
            }

            ChiTietHoaDon ct = new ChiTietHoaDon();
            ct.setSanPham(sp);
            ct.setSoLuong(item.getSoLuongDat());
            ct.setDonGia(item.getDonGiaBan());
            ct.setGiamGia(0.0);
            ct.setThanhTien(item.getDonGiaBan().multiply(BigDecimal.valueOf(item.getSoLuongDat())));
            hdb.themChiTiet(ct);
        }

        int soHD = hoaDonBanDAO.taoHoaDonBan(hdb);
        hdb.setSoHoaDon(soHD);

        // Xóa các sản phẩm đã thanh toán khỏi giỏ hàng
        for (ItemGioHang item : items) {
            gioHangDAO.xoaKhoiGio(maKhachHang, item.getSanPham().getMaQuanAo());
        }

        return hdb;
    }

    @Override
    public HoaDonBan lapHoaDonDonLe(String maKhachHang, String maNhanVien, int maQuanAo, int soLuong, BigDecimal donGiaBan) throws Exception {
        ItemGioHang item = new ItemGioHang();
        item.setMaKhachHang(maKhachHang);
        SanPham sp = new SanPham();
        sp.setMaQuanAo(maQuanAo);
        item.setSanPham(sp);
        item.setSoLuongDat(soLuong);
        item.setDonGiaBan(donGiaBan);

        return lapHoaDonBan(maKhachHang, maNhanVien, List.of(item));
    }

    @Override
    public List<HoaDonBan> getAllOrders() {
        return hoaDonBanDAO.findAll();
    }

    @Override
    public List<HoaDonBan> getOrdersByCustomer(String maKhachHang) {
        return hoaDonBanDAO.findByKhachHang(maKhachHang);
    }

    @Override
    public Optional<HoaDonBan> getOrderById(int id) {
        return hoaDonBanDAO.findById(id);
    }
}

