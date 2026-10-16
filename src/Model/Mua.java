package Model;

public class Mua {
    private int maMua;
    private String tenMua;

    public Mua() {}

    public Mua(int maMua, String tenMua) {
        this.maMua = maMua;
        this.tenMua = tenMua;
    }

    public int getMaMua() {
        return maMua;
    }

    public void setMaMua(int maMua) {
        this.maMua = maMua;
    }

    public String getTenMua() {
        return tenMua;
    }

    public void setTenMua(String tenMua) {
        this.tenMua = tenMua;
    }

    @Override
    public String toString() {
        return tenMua;
    }
}

