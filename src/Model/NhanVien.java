package Model;

import java.util.Date;

public class NhanVien extends Nguoi {
    private String gioiTinh;
    private Date ngaySinh;
    private CongViec congViec;
    private String maTaiKhoan;

    public NhanVien() {
        super();
    }

    public NhanVien(String maNhanVien, String hoTen, String gioiTinh, Date ngaySinh, String diaChi, String soDienThoai, String email, CongViec congViec, String maTaiKhoan) {
        super(maNhanVien, hoTen, soDienThoai, email, diaChi);
        this.gioiTinh = gioiTinh;
        this.ngaySinh = ngaySinh;
        this.congViec = congViec;
        this.maTaiKhoan = maTaiKhoan;
    }

    public String getMaNhanVien() {
        return getMaDinhDanh();
    }

    public void setMaNhanVien(String maNhanVien) {
        setMaDinhDanh(maNhanVien);
    }

    public String getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public Date getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(Date ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public CongViec getCongViec() {
        return congViec;
    }

    public void setCongViec(CongViec congViec) {
        this.congViec = congViec;
    }

    public String getMaTaiKhoan() {
        return maTaiKhoan;
    }

    public void setMaTaiKhoan(String maTaiKhoan) {
        this.maTaiKhoan = maTaiKhoan;
    }

    @Override
    public String getVaiTro() {
        return congViec != null ? congViec.getTenCongViec() : "Nhân Viên";
    }

    @Override
    public String toString() {
        return "NhanVien{" +
                "maNhanVien='" + maDinhDanh + '\'' +
                ", hoTen='" + hoTen + '\'' +
                ", gioiTinh='" + gioiTinh + '\'' +
                ", congViec=" + (congViec != null ? congViec.getTenCongViec() : "null") +
                '}';
    }
}

