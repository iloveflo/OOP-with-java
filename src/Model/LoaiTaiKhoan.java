package Model;

public enum LoaiTaiKhoan {
    ADMIN("Admin"),
    NHAN_VIEN("NhanVien"),
    KHACH_HANG("KhachHang");

    private final String value;

    LoaiTaiKhoan(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static LoaiTaiKhoan fromString(String text) {
        for (LoaiTaiKhoan loai : LoaiTaiKhoan.values()) {
            if (loai.value.equalsIgnoreCase(text) || loai.name().equalsIgnoreCase(text)) {
                return loai;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy loại tài khoản: " + text);
    }
}

