package Model;

import java.math.BigDecimal;

public class SanPham {
    private int maQuanAo;
    private String tenQuanAo;
    private TheLoai theLoai;
    private Co co;
    private Mau mau;
    private Mua mua;
    private DoiTuong doiTuong;
    private ChatLieu chatLieu;
    private NoiSanXuat noiSanXuat;
    private int soLuong;
    private String anh;
    private BigDecimal donGiaNhap;
    private BigDecimal donGiaBan;

    public SanPham() {}

    public SanPham(int maQuanAo, String tenQuanAo, TheLoai theLoai, Co co, Mau mau, Mua mua,
                   DoiTuong doiTuong, ChatLieu chatLieu, NoiSanXuat noiSanXuat,
                   int soLuong, String anh, BigDecimal donGiaNhap, BigDecimal donGiaBan) {
        this.maQuanAo = maQuanAo;
        this.tenQuanAo = tenQuanAo;
        this.theLoai = theLoai;
        this.co = co;
        this.mau = mau;
        this.mua = mua;
        this.doiTuong = doiTuong;
        this.chatLieu = chatLieu;
        this.noiSanXuat = noiSanXuat;
        this.soLuong = soLuong;
        this.anh = anh;
        this.donGiaNhap = donGiaNhap;
        this.donGiaBan = donGiaBan;
    }

    public int getMaQuanAo() {
        return maQuanAo;
    }

    public void setMaQuanAo(int maQuanAo) {
        this.maQuanAo = maQuanAo;
    }

    public String getTenQuanAo() {
        return tenQuanAo;
    }

    public void setTenQuanAo(String tenQuanAo) {
        this.tenQuanAo = tenQuanAo;
    }

    public TheLoai getTheLoai() {
        return theLoai;
    }

    public void setTheLoai(TheLoai theLoai) {
        this.theLoai = theLoai;
    }

    public Co getCo() {
        return co;
    }

    public void setCo(Co co) {
        this.co = co;
    }

    public Mau getMau() {
        return mau;
    }

    public void setMau(Mau mau) {
        this.mau = mau;
    }

    public Mua getMua() {
        return mua;
    }

    public void setMua(Mua mua) {
        this.mua = mua;
    }

    public DoiTuong getDoiTuong() {
        return doiTuong;
    }

    public void setDoiTuong(DoiTuong doiTuong) {
        this.doiTuong = doiTuong;
    }

    public ChatLieu getChatLieu() {
        return chatLieu;
    }

    public void setChatLieu(ChatLieu chatLieu) {
        this.chatLieu = chatLieu;
    }

    public NoiSanXuat getNoiSanXuat() {
        return noiSanXuat;
    }

    public void setNoiSanXuat(NoiSanXuat noiSanXuat) {
        this.noiSanXuat = noiSanXuat;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = Math.max(0, soLuong);
    }

    public String getAnh() {
        return anh;
    }

    public void setAnh(String anh) {
        this.anh = anh;
    }

    public BigDecimal getDonGiaNhap() {
        return donGiaNhap;
    }

    public void setDonGiaNhap(BigDecimal donGiaNhap) {
        this.donGiaNhap = donGiaNhap;
    }

    public BigDecimal getDonGiaBan() {
        return donGiaBan;
    }

    public void setDonGiaBan(BigDecimal donGiaBan) {
        this.donGiaBan = donGiaBan;
    }

    public boolean kiemTraConHang(int soLuongYeuCau) {
        return this.soLuong >= soLuongYeuCau;
    }

    public void capNhatTonKho(int soLuongThayDoi) {
        this.soLuong += soLuongThayDoi;
        if (this.soLuong < 0) {
            this.soLuong = 0;
        }
    }

    @Override
    public String toString() {
        return "SanPham{" +
                "maQuanAo=" + maQuanAo +
                ", tenQuanAo='" + tenQuanAo + '\'' +
                ", soLuong=" + soLuong +
                ", donGiaBan=" + donGiaBan +
                '}';
    }
}

