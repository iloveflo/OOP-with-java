package Service.dto;

public class CaptchaDTO {
    private int maAnh;
    private String linkAnh;
    private String ketQua;

    public CaptchaDTO() {}

    public CaptchaDTO(int maAnh, String linkAnh, String ketQua) {
        this.maAnh = maAnh;
        this.linkAnh = linkAnh;
        this.ketQua = ketQua;
    }

    public int getMaAnh() {
        return maAnh;
    }

    public void setMaAnh(int maAnh) {
        this.maAnh = maAnh;
    }

    public String getLinkAnh() {
        return linkAnh;
    }

    public void setLinkAnh(String linkAnh) {
        this.linkAnh = linkAnh;
    }

    public String getKetQua() {
        return ketQua;
    }

    public void setKetQua(String ketQua) {
        this.ketQua = ketQua;
    }

    public boolean validate(String userInput) {
        return this.ketQua != null && this.ketQua.equalsIgnoreCase(userInput != null ? userInput.trim() : "");
    }
}

