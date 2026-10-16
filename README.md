# 👕 CỬA HÀNG QUẦN ÁO BDTHD - ENTERPRISE DESKTOP POS & ERP SYSTEM

> **BDTHD – Tôn vinh phong cách, khẳng định cá tính**  
> Ứng dụng quản trị và bán hàng chuyên nghiệp dành cho chuỗi cửa hàng thời trang, xây dựng trên nền tảng **Java Swing**, thiết kế theo **Kiến trúc hướng đối tượng (OOP)** đa tầng và giao diện **Enterprise UI 2026** với thư viện FlatLaf.

---

## 📋 Mục lục

1. [Tổng quan & Kiến trúc phần mềm](#-tổng-quan--kiến-trúc-phần-mềm)
2. [Thiết kế giao diện chuẩn Doanh nghiệp 2026](#-thiết-kế-giao-diện-chuẩn-doanh-nghiệp-2026)
3. [Tính năng hệ thống](#-tính-năng-hệ-thống)
4. [Phân quyền người dùng (RBAC)](#-phân-quyền-người-dùng-rbac)
5. [Cấu trúc mã nguồn](#-cấu-trúc-mã-nguồn)
6. [Công nghệ & Thư viện sử dụng](#-công-nghệ--thư-viện-sử-dụng)
7. [Yêu cầu hệ thống](#-yêu-cầu-hệ-thống)
8. [Hướng dẫn cài đặt & Khởi chạy](#-hướng-dẫn-cài-đặt--khởi-chạy)
9. [Cơ sở dữ liệu](#-cơ-sở-dữ-liệu)

---

## 🏛 Tổng quan & Kiến trúc phần mềm

Hệ thống được tái cấu trúc hoàn chỉnh theo mô hình **3-Tier Layered Architecture** chuẩn mực công nghiệp, tuân thủ nghiêm ngặt các nguyên lý **SOLID** và lập trình hướng đối tượng (**OOP**):

```
┌─────────────────────────────────────────────────────────────┐
│                   Presentation Layer (GUI)                  │
│       FlatLaf Look & Feel, UITheme Tokens, AppNavigator     │
└──────────────────────────────┬──────────────────────────────┘
                               │ Calls
┌──────────────────────────────▼──────────────────────────────┐
│                    Business Service Layer                   │
│  IAuthService, IProductService, IOrderService, IReportService│
└──────────────────────────────┬──────────────────────────────┘
                               │ Calls
┌──────────────────────────────▼──────────────────────────────┐
│                   Data Access Layer (DAO)                   │
│   DatabaseConnection (Singleton), ISanPhamDAO, IOrderDAO... │
└──────────────────────────────┬──────────────────────────────┘
                               │ JDBC
┌──────────────────────────────▼──────────────────────────────┐
│                      MySQL Database                         │
└─────────────────────────────────────────────────────────────┘
```

### 1. Nguyên lý OOP & Thiết kế cốt lõi
- **Encapsulation (Đóng gói)**: Toàn bộ thực thể trong package `Model` (`SanPham`, `TaiKhoan`, `KhachHang`, `NhanVien`, `TheLoai`,...) che giấu trường dữ liệu và chỉ cung cấp qua getter/setter, tự quản lý tính toàn vẹn nghiệp vụ.
- **Abstraction & Interface Segregation (Trừu tượng hóa & Phân tách giao diện)**: Tách rời hoàn toàn đặc tả giao diện (`Service`, `DAO`) khỏi cài đặt cụ thể (`ServiceImpl`, `DAOImpl`). Tầng GUI chỉ phụ thuộc vào Interface, không phụ thuộc vào lớp triển khai trực tiếp.
- **Polymorphism (Đa hình)**: Xử lý linh hoạt các loại tài khoản qua `enum LoaiTaiKhoan`, cơ chế lật trang linh hoạt `Refreshable` trên tất cả các view con.
- **Single Responsibility (Trách nhiệm đơn nhất)**: Tách triệt để mã SQL ra khỏi giao diện Swing; GUI chỉ xử lý hiển thị và bắt sự kiện người dùng, nghiệp vụ do Service đảm nhiệm, truy vấn CSDL do DAO phụ trách.

### 2. Các mẫu thiết kế (Design Patterns) áp dụng
- **Singleton Pattern**:
  - `DatabaseConnection`: Quản lý tập trung một kết nối CSDL duy nhất, thread-safe, tối ưu hóa tài nguyên.
  - `UserSession`: Lưu trữ phiên làm việc của người dùng hiện tại xuyên suốt vòng đời ứng dụng.
- **Data Access Object (DAO) Pattern**: Tách biệt hoàn toàn tầng logic truy xuất dữ liệu với nghiệp vụ.
- **Data Transfer Object (DTO) Pattern**: `CaptchaDTO` đóng gói dữ liệu truyền tải xác minh.
- **View Cache & Navigation Controller Pattern**: `AppNavigator` và `panelCache` trong Dashboard điều phối chuyển trang mượt mà không cấp phát lại bộ nhớ thừa.

---

## 🎨 Thiết kế giao diện chuẩn Doanh nghiệp 2026

Ứng dụng xóa bỏ hoàn toàn giao diện xám vuông vức của Java Swing truyền thống, chuyển đổi toàn diện sang ngôn ngữ thiết kế phẳng hiện đại:

- **FlatLaf 3.5.4 Engine**: Sử dụng Look and Feel hiện đại, bo tròn góc chuẩn mực (arc: 8px – 12px), loại bỏ viền nổi beveled 3D cổ điển.
- **Bảng màu Slate & Royal Indigo**:
  - Nền ứng dụng chính: `#F8FAFC` (Slate 50)
  - Card & Container: `#FFFFFF` với đường viền mờ `#E2E8F0`
  - Sidebar Dashboard: Nền tối sang trọng `#0F172A` (Slate 900)
  - Màu chủ đạo (Primary): `#4F46E5` (Indigo 600), Hover: `#4338CA`
  - Trạng thái phản hồi: Success `#16A34A`, Danger `#DC2626`, Warning `#D97706`.
- **Hệ thống bảng dữ liệu (JTable Enterprise)**:
  - Chiều cao dòng (Row Height) mở rộng lên **38px** kèm padding thoải mái.
  - Tiêu đề cột (Header) cao **40px**, chữ đậm, nền `#F1F5F9`.
  - Hiệu ứng sọc ngựa vằn (Zebra striping) xen kẽ giữa `#FFFFFF` và `#F8FAFC`.
  - Vùng chọn (Selection) dịu mắt với tone màu tím phấn `#EEF2FF`.
- **Cơ chế chuyển trang mượt mà (Smooth Page Switching)**:
  - Ứng dụng mô hình **CardLayout & View Cache**: Các trang trên thanh Sidebar chỉ khởi tạo 1 lần (Lazy Loading) và lật thẻ tức thì (< 1ms).
  - Không còn tình trạng giật màn hình hoặc hủy/tạo lại JPanel liên tục khi chuyển đổi chức năng.
  - Tự động highlight trạng thái nút menu đang chọn (`Active State`).
  - [AppNavigator](src/GUI/util/AppNavigator.java) hỗ trợ chuyển cửa sổ (Window Switching) giữ nguyên vị trí tọa độ, không làm chớp lộ màn hình Desktop.

---

## ✨ Tính năng hệ thống

### 🔐 1. Xác thực, Tài khoản & Bảo mật
- **Đăng nhập / Đăng ký**: Hỗ trợ đăng nhập phân quyền tự động, đăng ký tài khoản khách hàng mới.
- **Xác thực CAPTCHA**: Tạo mã hình ảnh ngẫu nhiên chống brute-force đăng nhập.
- **Quản lý phiên (Session Management)**: Ghi nhận trạng thái `DangNhap` trong CSDL, hỗ trợ đăng xuất an toàn.
- **Quên mật khẩu & OTP Email**: Tích hợp gửi mã OTP bảo mật 6 số qua email thật (Jakarta Mail / SMTP) để cấp lại mật khẩu.
- **Đổi mật khẩu & Hồ sơ cá nhân**: Cập nhật mật khẩu và xem/sửa thông tin cá nhân trực tiếp trên giao diện Dashboard.

### 👗 2. Quản lý Sản phẩm Thời trang
- **Danh mục đa chiều**: Quản lý thuộc tính chi tiết: Thể loại, Kích cỡ, Chất liệu, Màu sắc, Đối tượng, Mùa, Nơi sản xuất.
- **CRUD Sản phẩm**: Thêm, cập nhật, xóa sản phẩm kèm chọn tải ảnh minh họa trực tiếp (`JFileChooser`).
- **Bộ lọc đa tiêu chí**: Lọc nhanh sản phẩm theo bất kỳ tiêu chí danh mục nào kết hợp.
- **Cảnh báo tồn kho**: Theo dõi danh sách mặt hàng bán chạy và mặt hàng sắp hết hàng (tồn kho dưới ngưỡng cảnh báo).

### 🛒 3. Bán hàng & Giỏ hàng Khách hàng
- Xem danh sách sản phẩm trực quan, xem chi tiết và ảnh sản phẩm kích thước lớn.
- Thêm sản phẩm vào giỏ hàng cá nhân, tùy chỉnh số lượng.
- Xóa từng mặt hàng trong giỏ hoặc xác nhận đặt hàng nhanh toàn bộ giỏ hàng.
- Tích hợp hướng dẫn chọn size chuẩn xác và hướng dẫn mua hàng ngay trong ứng dụng.

### 📄 4. Quản lý Hóa đơn & Báo cáo
- **Hóa đơn bán hàng**: Xem lịch sử mua hàng, tìm kiếm hóa đơn theo mã khách hàng.
- **Hóa đơn nhập hàng**: Nhân viên và Quản lý lập hóa đơn nhập quần áo từ nhà cung cấp, tự động tính tổng tiền và chiết khấu.
- **Lập hóa đơn tại quầy**: Hỗ trợ nhân viên lập hóa đơn thanh toán trực tiếp cho khách.
- **Thống kê & Xuất Excel**: Thống kê doanh thu theo tháng, biểu diễn trực quan và xuất file báo cáo **Excel (.xlsx)** chuyên nghiệp thông qua Apache POI.

---

## 👥 Phân quyền người dùng (RBAC)

Hệ thống cung cấp 3 không gian làm việc chuyên biệt tùy theo vai trò đăng nhập:

| Vai trò | Giao diện chính | Quyền hạn nghiệp vụ |
|---|---|---|
| **Admin (Quản trị viên)** | `Menu.java` | Toàn quyền quản trị hệ thống: Quản lý sản phẩm, danh mục, nhân viên, khách hàng, duyệt hóa đơn bán/nhập, thống kê doanh thu và xuất báo cáo Excel. |
| **Nhân viên (NhanVien)** | `NhanvienForm.java` | Xem danh sách sản phẩm, lập hóa đơn bán hàng cho khách, lập hóa đơn nhập kho, kiểm tra hàng sắp hết, cập nhật thông tin cá nhân. |
| **Khách hàng (KhachHang)** | `KhachhangForm.java` | Tra cứu sản phẩm thời trang, quản lý giỏ hàng, đặt hàng trực tuyến, xem tài khoản cá nhân, xem cẩm nang chọn size và hỗ trợ. |

---

## 📁 Cấu trúc mã nguồn

```
OOP-with-java/
├── lib/                                    # Thư viện phụ thuộc (JARs)
│   ├── flatlaf-3.5.4.jar                   # Look and Feel hiện đại 2026
│   ├── mysql-connector-j-9.2.0.jar         # Driver kết nối MySQL
│   ├── poi-5.2.3.jar / poi-ooxml-*.jar     # Đọc/ghi tài liệu Excel
│   ├── jakarta.mail-2.0.1.jar              # Gửi email OTP
│   └── ...                                 # Các thư viện bổ trợ khác
├── src/
│   ├── Main.java                           # Điểm kích hoạt ứng dụng
│   ├── Model/                              # Domain Entities & Value Objects
│   │   ├── TaiKhoan.java / LoaiTaiKhoan.java
│   │   ├── SanPham.java
│   │   ├── KhachHang.java / NhanVien.java
│   │   ├── HoaDonBan.java / HoaDonNhap.java / DonHang.java
│   │   └── TheLoai.java / Co.java / Mau.java / Mua.java...
│   ├── DAO/                                # Tầng truy cập dữ liệu (JDBC Data Access)
│   │   ├── DatabaseConnection.java         # Singleton kết nối CSDL MySQL
│   │   ├── ISanPhamDAO.java / SanPhamDAOImpl.java
│   │   ├── IKhachHangDAO.java / KhachHangDAOImpl.java
│   │   ├── INhanVienDAO.java / NhanVienDAOImpl.java
│   │   ├── IHoaDonBanDAO.java / HoaDonBanDAOImpl.java
│   │   ├── IHoaDonNhapDAO.java / HoaDonNhapDAOImpl.java
│   │   ├── IDonHangDAO.java / DonHangDAOImpl.java
│   │   ├── ITaiKhoanDAO.java / TaiKhoanDAOImpl.java
│   │   └── IDanhMucDAO.java / DanhMucDAOImpl.java
│   ├── Service/                            # Tầng xử lý nghiệp vụ (Business Logic)
│   │   ├── UserSession.java                # Quản lý phiên đăng nhập
│   │   ├── IAuthService.java / AuthServiceImpl.java
│   │   ├── IProductService.java / ProductServiceImpl.java
│   │   ├── IOrderService.java / OrderServiceImpl.java
│   │   ├── IUserService.java / UserServiceImpl.java
│   │   ├── IReportService.java / ReportServiceImpl.java
│   │   ├── IImportService.java / ImportServiceImpl.java
│   │   ├── IOTPService.java / OTPServiceImpl.java
│   │   └── dto/CaptchaDTO.java
│   └── GUI/                                # Tầng giao diện người dùng (Presentation)
│       ├── Start.java                      # Màn hình Đăng nhập & Captcha
│       ├── register.java                   # Màn hình Đăng ký tài khoản
│       ├── Quenmatkhau.java / OTP.java     # Luồng quên mật khẩu & xác nhận OTP
│       ├── Doimatkhau.java / OTP1.java    # Đổi mật khẩu
│       ├── Menu.java                       # Dashboard Quản trị viên (Admin)
│       ├── NhanvienForm.java               # Dashboard Nhân viên
│       ├── KhachhangForm.java              # Dashboard Khách hàng
│       ├── Sanpham.java / SanphamKH.java   # Panels quản lý và xem sản phẩm
│       ├── NVdanhSachsPham.java            # Tra cứu sản phẩm cho nhân viên
│       ├── Khachhang.java / Nhanvien.java  # Quản lý khách hàng & nhân viên
│       ├── Giohang.java                    # Quản lý giỏ hàng
│       ├── Hoadonban.java / Hoadonnhap.java
│       ├── Nhanvienlaphoadon.java / HoadonnhapNV.java
│       ├── Thongke.java                    # Báo cáo doanh thu & xuất Excel
│       ├── Thongtintk.java                 # Quản lý thông tin tài khoản
│       ├── Huongdanchonsize.java / Huongdandathang.java / Lienhehotro.java
│       ├── Refreshable.java                # Giao diện cho phép làm mới dữ liệu
│       ├── theme/
│       │   └── UITheme.java                # Hệ thống Design Tokens & Component Styling
│       └── util/
│           └── AppNavigator.java           # Điều hướng cửa sổ mượt mà
├── .agent_context/
│   └── repo_summary.xml                    # Bản đồ cấu trúc thư mục tự động
└── README.md
```

---

## 🛠 Công nghệ & Thư viện sử dụng

| Công nghệ / Thư viện | Phiên bản | Mục đích sử dụng |
|---|---|---|
| **Java SE (JDK)** | 17+ | Ngôn ngữ phát triển chính |
| **Java Swing** | Core | Xây dựng kiến trúc giao diện Desktop |
| **FlatLaf** | 3.5.4 | Look & Feel phẳng, hiện đại chuẩn doanh nghiệp 2026 |
| **MySQL Connector/J** | 9.2.0 | Trình điều khiển kết nối cơ sở dữ liệu MySQL |
| **Apache POI** | 5.2.3 | Xử lý xuất báo cáo thống kê định dạng Excel (.xlsx) |
| **Jakarta Mail** | 2.0.1 | Dịch vụ gửi email chứa mã OTP bảo mật |
| **Apache Commons IO / Codec** | 2.11.0 / 1.15 | Tiện ích xử lý dữ liệu và mã hóa |
| **Log4j** | 2.18.0 | Ghi nhật ký lỗi và vận hành ứng dụng |

---

## 💻 Yêu cầu hệ thống

- **Hệ điều hành**: Windows 10/11, macOS hoặc Linux (Ubuntu, Debian, Fedora,...).
- **Java Development Kit (JDK)**: Phiên bản 17 trở lên.
- **MySQL Database Server**: Phiên bản 8.0 trở lên.
- **Môi trường phát triển**: IntelliJ IDEA, VS Code, NetBeans hoặc Eclipse.

---

## 🚀 Hướng dẫn cài đặt & Khởi chạy

### Bước 1: Sao chép kho mã nguồn
```bash
git clone https://github.com/iloveflo/OOP-with-java.git
cd OOP-with-java
```

### Bước 2: Thiết lập Cơ sở dữ liệu MySQL
1. Khởi động dịch vụ MySQL trên máy chủ / máy cục bộ.
2. Tạo cơ sở dữ liệu mới:
   ```sql
   CREATE DATABASE ClothingStore CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
3. Nhập dữ liệu ban đầu từ file kịch bản SQL (nếu có).

### Bước 3: Cấu hình kết nối Cơ sở dữ liệu
Mở file `src/DAO/DatabaseConnection.java` và chỉnh sửa các thông số phù hợp:

```java
private static final String URL = "jdbc:mysql://localhost:3306/ClothingStore?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC";
private static final String USER = "root";
private static final String PASSWORD = "your_mysql_password";
```

### Bước 4: Biên dịch và chạy ứng dụng

#### 🔹 Trên Linux / macOS:
```bash
# Tạo thư mục đầu ra
mkdir -p bin

# Biên dịch toàn bộ mã nguồn
javac -cp "lib/*" -d bin $(find src -name "*.java")

# Chạy ứng dụng
java -cp "bin:lib/*" Main
```

#### 🔹 Trên Windows (Command Prompt / PowerShell):
```powershell
# Tạo thư mục đầu ra
if not exist bin mkdir bin

# Biên dịch toàn bộ mã nguồn
javac -cp "lib/*" -d bin src/Main.java src/Model/*.java src/DAO/*.java src/DAO/impl/*.java src/Service/*.java src/Service/impl/*.java src/Service/dto/*.java src/GUI/*.java src/GUI/theme/*.java src/GUI/util/*.java

# Chạy ứng dụng (lưu ý dùng dấu chấm phẩy ; cho classpath)
java -cp "bin;lib/*" Main
```

---

## 🗄️ Cơ sở dữ liệu

Hệ thống quản lý dữ liệu chặt chẽ qua các bảng quan hệ:

| Bảng CSDL | Diễn giải chức năng |
|---|---|
| `taikhoan` | Lưu thông tin đăng nhập, loại quyền (`Admin`, `NhanVien`, `KhachHang`), trạng thái đăng nhập |
| `sanpham` | Thông tin chi tiết mã quần áo, tên, giá nhập, giá bán, số lượng, liên kết danh mục |
| `theloai`, `co`, `chatlieu`, `mau`, `doituong`, `mua`, `noisanxuat` | Bảng danh mục thuộc tính sản phẩm |
| `khachhang` | Hồ sơ khách hàng (họ tên, điện thoại, email, địa chỉ, liên kết tài khoản) |
| `nhanvien` | Hồ sơ nhân viên (họ tên, ngày sinh, giới tính, liên kết công việc, tài khoản) |
| `giohang` | Lưu vết giỏ hàng tạm thời của khách hàng |
| `hoadonban` & `chitiethoadonban` | Hóa đơn xuất kho / bán lẻ và chi tiết từng mặt hàng |
| `hoadonnhap` & `chitiethoadonnhap` | Hóa đơn nhập hàng từ nhà cung cấp |
| `CapCha` | Quản lý kho mã captcha và đường dẫn ảnh xác thực |

---

## 👨‍💻 Bản quyền & Đóng góp

- **Nhóm phát triển**: BDTHD Software Team
- **Giấy phép**: Dự án mã nguồn mở phục vụ học tập, nghiên cứu kiến trúc phần mềm và thực hành OOP / Java Swing nâng cao.

<p align="center">
  <b>⭐ Nếu dự án hữu ích với bạn, hãy ủng hộ bằng cách bấm Star trên repository! ⭐</b>
</p>