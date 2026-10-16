package Model;

public class Mau {
    private int maMau;
    private String tenMau;

    public Mau() {}

    public Mau(int maMau, String tenMau) {
        this.maMau = maMau;
        this.tenMau = tenMau;
    }

    public int getMaMau() {
        return maMau;
    }

    public void setMaMau(int maMau) {
        this.maMau = maMau;
    }

    public String getTenMau() {
        return tenMau;
    }

    public void setTenMau(String tenMau) {
        this.tenMau = tenMau;
    }

    @Override
    public String toString() {
        return tenMau;
    }
}

