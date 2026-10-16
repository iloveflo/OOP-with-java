package Model;

public class DoiTuong {
    private int maDoiTuong;
    private String tenDoiTuong;

    public DoiTuong() {}

    public DoiTuong(int maDoiTuong, String tenDoiTuong) {
        this.maDoiTuong = maDoiTuong;
        this.tenDoiTuong = tenDoiTuong;
    }

    public int getMaDoiTuong() {
        return maDoiTuong;
    }

    public void setMaDoiTuong(int maDoiTuong) {
        this.maDoiTuong = maDoiTuong;
    }

    public String getTenDoiTuong() {
        return tenDoiTuong;
    }

    public void setTenDoiTuong(String tenDoiTuong) {
        this.tenDoiTuong = tenDoiTuong;
    }

    @Override
    public String toString() {
        return tenDoiTuong;
    }
}

