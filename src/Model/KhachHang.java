package Model;

public class KhachHang extends Nguoi {
    private String maTaiKhoan;

    public KhachHang() {
        super();
    }

    public KhachHang(String maKhachHang, String hoTen, String soDienThoai, String diaChi, String email, String maTaiKhoan) {
        super(maKhachHang, hoTen, soDienThoai, email, diaChi);
        this.maTaiKhoan = maTaiKhoan;
    }

    public String getMaKhachHang() {
        return getMaDinhDanh();
    }

    public void setMaKhachHang(String maKhachHang) {
        setMaDinhDanh(maKhachHang);
    }

    public String getMaTaiKhoan() {
        return maTaiKhoan;
    }

    public void setMaTaiKhoan(String maTaiKhoan) {
        this.maTaiKhoan = maTaiKhoan;
    }

    @Override
    public String getVaiTro() {
        return "Khách Hàng";
    }

    @Override
    public String toString() {
        return "KhachHang{" +
                "maKhachHang='" + maDinhDanh + '\'' +
                ", hoTen='" + hoTen + '\'' +
                ", soDienThoai='" + soDienThoai + '\'' +
                ", email='" + email + '\'' +
                ", diaChi='" + diaChi + '\'' +
                ", maTaiKhoan='" + maTaiKhoan + '\'' +
                '}';
    }
}

