package Model;

public class CongViec {
    private int maCongViec;
    private String tenCongViec;

    public CongViec() {}

    public CongViec(int maCongViec, String tenCongViec) {
        this.maCongViec = maCongViec;
        this.tenCongViec = tenCongViec;
    }

    public int getMaCongViec() {
        return maCongViec;
    }

    public void setMaCongViec(int maCongViec) {
        this.maCongViec = maCongViec;
    }

    public String getTenCongViec() {
        return tenCongViec;
    }

    public void setTenCongViec(String tenCongViec) {
        this.tenCongViec = tenCongViec;
    }

    @Override
    public String toString() {
        return tenCongViec;
    }
}

