package Model;

public class Co {
    private int maCo;
    private String tenCo;

    public Co() {}

    public Co(int maCo, String tenCo) {
        this.maCo = maCo;
        this.tenCo = tenCo;
    }

    public int getMaCo() {
        return maCo;
    }

    public void setMaCo(int maCo) {
        this.maCo = maCo;
    }

    public String getTenCo() {
        return tenCo;
    }

    public void setTenCo(String tenCo) {
        this.tenCo = tenCo;
    }

    @Override
    public String toString() {
        return tenCo;
    }
}

