package Model;

public class NoiSanXuat {
    private int maNSX;
    private String tenNSX;

    public NoiSanXuat() {}

    public NoiSanXuat(int maNSX, String tenNSX) {
        this.maNSX = maNSX;
        this.tenNSX = tenNSX;
    }

    public int getMaNSX() {
        return maNSX;
    }

    public void setMaNSX(int maNSX) {
        this.maNSX = maNSX;
    }

    public String getTenNSX() {
        return tenNSX;
    }

    public void setTenNSX(String tenNSX) {
        this.tenNSX = tenNSX;
    }

    @Override
    public String toString() {
        return tenNSX;
    }
}

