package Model;

public class TaiKhoan {
    private String maTaiKhoan;
    private String tenDangNhap;
    private String matKhau;
    private LoaiTaiKhoan loaiTaiKhoan;
    private boolean dangNhap;

    public TaiKhoan() {}

    public TaiKhoan(String maTaiKhoan, String tenDangNhap, String matKhau, LoaiTaiKhoan loaiTaiKhoan) {
        this(maTaiKhoan, tenDangNhap, matKhau, loaiTaiKhoan, false);
    }

    public TaiKhoan(String maTaiKhoan, String tenDangNhap, String matKhau, LoaiTaiKhoan loaiTaiKhoan, boolean dangNhap) {
        this.maTaiKhoan = maTaiKhoan;
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.loaiTaiKhoan = loaiTaiKhoan;
        this.dangNhap = dangNhap;
    }

    public String getMaTaiKhoan() {
        return maTaiKhoan;
    }

    public void setMaTaiKhoan(String maTaiKhoan) {
        this.maTaiKhoan = maTaiKhoan;
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public LoaiTaiKhoan getLoaiTaiKhoan() {
        return loaiTaiKhoan;
    }

    public void setLoaiTaiKhoan(LoaiTaiKhoan loaiTaiKhoan) {
        this.loaiTaiKhoan = loaiTaiKhoan;
    }

    public boolean isDangNhap() {
        return dangNhap;
    }

    public void setDangNhap(boolean dangNhap) {
        this.dangNhap = dangNhap;
    }

    public boolean kiemTraMatKhau(String password) {
        return this.matKhau != null && this.matKhau.equals(password);
    }

    @Override
    public String toString() {
        return "TaiKhoan{" +
                "maTaiKhoan='" + maTaiKhoan + '\'' +
                ", tenDangNhap='" + tenDangNhap + '\'' +
                ", loaiTaiKhoan=" + loaiTaiKhoan +
                ", dangNhap=" + dangNhap +
                '}';
    }
}

