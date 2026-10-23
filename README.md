# 👕 HỆ THỐNG QUẢN LÝ CỬA HÀNG QUẦN ÁO (CLOTHING STORE MANAGEMENT SYSTEM)

> **"Tôn vinh phong cách - Khẳng định cá tính"**  
> Ứng dụng Desktop chuyên nghiệp quản lý bán hàng, kho thời trang và chuỗi cung ứng, xây dựng trên nền tảng **Java Swing thuần** kết hợp thư viện giao diện phẳng **FlatLaf 3.5.4**, tuân thủ nghiêm ngặt **Kiến trúc phân tầng (Layered Architecture)** và nguyên lý lập trình hướng đối tượng (**OOP**).

---

## 📋 Mục lục

1. [Tổng quan dự án](#-tổng-quan-dự-án)
2. [Kiến trúc hệ thống & Nguyên lý OOP](#-kiến-trúc-hệ-thống--nguyên-lý-oop)
3. [Phân quyền người dùng (RBAC Matrix)](#-phân-quyền-người-dùng-rbac-matrix)
4. [Tính năng chi tiết theo phân hệ](#-tính-năng-chi-tiết-theo-phân-hệ)
5. [Thiết kế giao diện Modern Flat UI](#-thiết-kế-giao-diện-modern-flat-ui)
6. [Cấu trúc mã nguồn chi tiết](#-cấu-trúc-mã-nguồn-chi-tiết)
7. [Cơ sở dữ liệu (Database Schema)](#-cơ-sở-dữ-liệu-database-schema)
8. [Công nghệ & Thư viện phụ thuộc](#-công-nghệ--thư-viện-phụ-thuộc)
9. [Yêu cầu hệ thống & Hướng dẫn cài đặt](#-yêu-cầu-hệ-thống--hướng-dẫn-cài-đặt)
10. [Hướng dẫn biên dịch & Vận hành](#-hướng-dẫn-biên-dịch--vận-hành)

---

## 🌟 Tổng quan dự án

Dự án là giải pháp toàn diện cho hoạt động vận hành của một cửa hàng / chuỗi cửa hàng thời trang:
- **Quản lý bán hàng tại quầy (POS)**: Lập hóa đơn bán, chọn sản phẩm tồn kho, chiết khấu, tự động tính tổng tiền và in hóa đơn Excel.
- **Mua sắm trực tuyến cho khách hàng**: Giao diện thẻ sản phẩm (Product Cards Grid), quản lý giỏ hàng và đặt hàng trực tiếp.
- **Quản lý kho & Nhập hàng**: Nhập hàng từ nhà cung cấp, kiểm soát số lượng tồn, cảnh báo hàng sắp hết kho (`Số lượng <= 10`).
- **Thống kê doanh thu & Báo cáo**: Phân tích doanh thu, đơn hàng, mặt hàng bán chạy nhất, xuất hóa đơn/báo cáo định dạng Microsoft Excel (`.xlsx`).
- **Quản trị nhân sự & Khách hàng**: Quản lý hồ sơ nhân viên, phân quyền tài khoản, quản lý dữ liệu khách hàng.
- **Xác thực bảo mật**: Đăng nhập phân quyền, xác thực hình ảnh CAPTCHA, gửi OTP khôi phục mật khẩu qua Email (SMTP).

---

## 🏛 Kiến trúc hệ thống & Nguyên lý OOP

Hệ thống được thiết kế theo mô hình **3-Tier Layered Architecture** chuẩn mực công nghiệp, đảm bảo tách bạch rõ ràng giữa giao diện, nghiệp vụ và dữ liệu:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                      Presentation Layer (Package GUI)                   │
│   • GUI.auth: LoginFrame, RegisterDialog, ForgotPasswordDialog          │
│   • GUI.main: MainDashboardFrame (RBAC Dynamic Sidebar + CardLayout)    │
│   • GUI.panels: POS, Products, Orders, Imports, Staff, Shop, Cart...    │
│   • GUI.common: ThemeConfig, CurrencyUtil, ImageUtil, ValidationUtil    │
└────────────────────────────────────┬────────────────────────────────────┘
                                     │ Calls Interface Methods
┌────────────────────────────────────▼────────────────────────────────────┐
│                    Business Logic Layer (Package Service)               │
│   • IAuthService, IProductService, IOrderService, IUserService...      │
│   • UserSession (Singleton): Quản lý phiên làm việc & thông tin user    │
│   • Xử lý logic, tính toán tiền nong, validate nghiệp vụ               │
└────────────────────────────────────┬────────────────────────────────────┘
                                     │ Calls DAO Interface
┌────────────────────────────────────▼────────────────────────────────────┐
│                   Data Access Object Layer (Package DAO)                │
│   • DatabaseConnection (Singleton thread-safe kết nối JDBC)             │
│   • ISanPhamDAO, IHoaDonBanDAO, IHoaDonNhapDAO, ITaiKhoanDAO...         │
│   • Thực thi SQL, ánh xạ ResultSet -> Model Entities                    │
└────────────────────────────────────┬────────────────────────────────────┘
                                     │ JDBC Driver
┌────────────────────────────────────▼────────────────────────────────────┐
│                       MySQL Relational Database                         │
│                    `ClothingStore` Database Schema                      │
└─────────────────────────────────────────────────────────────────────────┘
```

### 1. Áp dụng 4 tính chất OOP cốt lõi
- **Encapsulation (Đóng gói)**: Toàn bộ thực thể trong `Model` (`SanPham`, `HoaDonBan`, `HoaDonNhap`, `TaiKhoan`, `NhanVien`, `KhachHang`,...) đóng gói dữ liệu với phạm vi `private/protected`, cung cấp quyền truy xuất qua getters/setters và các phương thức kiểm tra nghiệp vụ nội tại (`kiemTraConHang()`, `capNhatTonKho()`).
- **Inheritance (Kế thừa)**:
  - `NhanVien` và `KhachHang` kế thừa từ lớp trừu tượng `Nguoi` (tái sử dụng các trường định danh, họ tên, số điện thoại, email, địa chỉ).
  - `HoaDonBan` và `HoaDonNhap` kế thừa từ lớp trừu tượng `HoaDon` (quản lý số hóa đơn, ngày lập, nhân viên, tổng tiền, danh sách chi tiết).
- **Polymorphism (Đa hình)**:
  - Phân quyền động qua enum `LoaiTaiKhoan` (`ADMIN`, `NHAN_VIEN`, `KHACH_HANG`).
  - Giao diện `IGenericDAO<T, ID>` cho phép đa hình hóa các thao tác CRUD cơ bản trên các thực thể.
  - Tầng Service phụ thuộc vào Interface (`ISanPhamDAO`, `IOrderService`,...), cho phép dễ dàng thay thế cài đặt mà không ảnh hưởng tới tầng trên.
- **Abstraction (Trừu tượng hóa)**:
  - Tách bạch 100% giữa Interface (`IAuthService`, `IProductService`, `IOrderService`,...) và lớp hiện thực (`AuthServiceImpl`, `ProductServiceImpl`,...).
  - Tầng `GUI` chỉ tương tác với Interface của Service, hoàn toàn không chứa truy vấn SQL hay can thiệp trực tiếp vào CSDL.

### 2. Các mẫu thiết kế (Design Patterns)
- **Singleton Pattern**:
  - `DatabaseConnection`: Khởi tạo và quản lý một kết nối JDBC duy nhất, thread-safe, tối ưu hóa tài nguyên kết nối CSDL.
  - `UserSession`: Lưu trữ thông tin tài khoản và người dùng đang đăng nhập trong suốt phiên làm việc.
- **Data Access Object (DAO) Pattern**: Tách biệt hoàn toàn tầng logic truy xuất dữ liệu với nghiệp vụ.
- **Data Transfer Object (DTO) Pattern**: `CaptchaDTO` đóng gói dữ liệu truyền tải mã xác minh.
- **Template / Delegation Pattern**: `AppNavigator` và `ThemeConfig` tập trung hóa cấu hình và điều hướng giao diện.

---

## 👥 Phân quyền người dùng (RBAC Matrix)

Hệ thống phân quyền truy cập chặt chẽ dựa trên trường `LoaiTaiKhoan` trong bảng `taikhoan`:

| Chức năng / Phân hệ | Admin (Quản trị viên) | Nhân viên (NhanVien) | Khách hàng (KhachHang) |
|---|:---:|:---:|:---:|
| **Dashboard Thống kê tổng quan** | ✅ Đầy đủ | ❌ | ❌ |
| **Quản lý Sản phẩm (Xem & Tìm kiếm)** | ✅ | ✅ | ❌ |
| **Thêm / Sửa / Xóa Sản phẩm** | ✅ | ❌ (Chỉ xem) | ❌ |
| **Xem Đơn giá nhập của sản phẩm** | ✅ | ❌ (Ẩn giá nhập) | ❌ |
| **Xuất danh sách sản phẩm ra Excel** | ✅ (Kèm giá nhập) | ✅ (Không có giá nhập) | ❌ |
| **Bán hàng tại quầy (Tạo hóa đơn bán)** | ✅ | ✅ | ❌ |
| **Quản lý Đơn hàng & Xem chi tiết HĐ** | ✅ (Toàn bộ) | ✅ (Toàn bộ) | ❌ |
| **In / Xuất hóa đơn bán ra Excel** | ✅ | ✅ | ❌ |
| **Quản lý Nhập kho (HĐ Nhập & NCC)** | ✅ | ✅ | ❌ |
| **Cảnh báo hàng sắp hết kho (`<= 10`)** | ✅ | ✅ | ❌ |
| **Quản trị Nhân viên & Cấp tài khoản** | ✅ | ❌ | ❌ |
| **Quản trị Khách hàng** | ✅ | ❌ | ❌ |
| **Cửa hàng mua sắm dạng thẻ (Shop Grid)** | ❌ | ❌ | ✅ |
| **Giỏ hàng & Đặt hàng trực tuyến** | ❌ | ❌ | ✅ |
| **Xem lịch sử đơn mua cá nhân** | ❌ | ❌ | ✅ (Chỉ đơn của mình) |
| **Quản lý Hồ sơ cá nhân & Đổi mật khẩu** | ✅ | ✅ | ✅ |

---

## ✨ Tính năng chi tiết theo phân hệ

### 1. Phân hệ Xác thực & Tài khoản (`src/GUI/auth/`)
- **Đăng nhập (`LoginFrame`)**:
  - Giao diện 2 cột hiện đại: Cột trái là banner nhận diện thương hiệu thời trang; cột phải là form đăng nhập.
  - Xác thực CAPTCHA bằng ảnh ngẫu nhiên lấy từ bảng `capcha` trong CSDL, có nút refresh `↻` đổi mã bảo vệ và cơ chế sinh mã tự động dự phòng.
  - Checkbox bật/tắt ẩn hiện mật khẩu.
  - Hỗ trợ phím `Enter` đăng nhập nhanh trên tất cả các trường.
- **Đăng ký Khách hàng (`RegisterDialog`)**: Modal đăng ký thành viên mới dành cho khách hàng với kiểm tra định dạng tên đăng nhập, khớp mật khẩu, số điện thoại (10-11 số), email hợp lệ.
- **Khôi phục mật khẩu (`ForgotPasswordDialog`)**: Quy trình khôi phục 2 bước: Nhập tên đăng nhập -> Nhận mã OTP 6 số gửi qua Email -> Nhập mã OTP và thiết lập mật khẩu mới.

### 2. Phân hệ Khung sườn ứng dụng (`src/GUI/main/`)
- **`MainDashboardFrame`**:
  - **Sidebar bên trái (`#0F172A`)**: Chứa logo thương hiệu, Mini Card thông tin người dùng đang đăng nhập (`Username`, `Họ tên`, `Role Badge`), danh sách Menu điều hướng tương ứng với quyền của tài khoản, nút Đăng xuất an toàn ở chân trang.
  - **Header trên cùng**: Tiêu đề phân hệ đang mở, đồng hồ thời gian thực (`dd/MM/yyyy HH:mm:ss`).
  - **Content Area**: Điều phối các màn hình bằng `CardLayout`, hỗ trợ tự động tải lại dữ liệu khi người dùng chuyển đổi giữa các tab.

### 3. Phân hệ Quản trị Sản phẩm (`src/GUI/panels/ProductManagementPanel.java`)
- **Bảng dữ liệu**: Hiển thị ảnh thumbnail sản phẩm co giãn mượt mà, mã SP, tên, thể loại, kích cỡ, màu sắc, số lượng, giá bán, giá nhập (ẩn với Nhân viên) và trạng thái tồn kho (Hết hàng / Sắp hết / Còn hàng).
- **Bộ lọc & Tìm kiếm**: Tìm kiếm từ khóa theo tên hoặc mã SP; lọc kết hợp theo Thể loại, Cỡ (Size), Màu sắc, Đối tượng.
- **Thao tác**: Thêm mới, Chỉnh sửa, Xóa sản phẩm.
- **Xuất Excel**: Xuất danh mục sản phẩm ra tệp `.xlsx` thông qua Apache POI.
- **Modal Thêm/Sửa (`ProductFormDialog`)**: Bố cục 2 cột bằng `GridBagLayout`:
  - Cột 1: Thông tin 10 thuộc tính liên kết với các bảng danh mục CSDL (`theloai`, `co`, `chatlieu`, `mau`, `mua`, `doituong`, `noisanxuat`).
  - Cột 2: Khung xem trước ảnh lớn + nút `JFileChooser` chọn ảnh từ máy tính (tự động sao chép ảnh vào thư mục `images/` của dự án).

### 4. Phân hệ Bán hàng & Hóa đơn (`src/GUI/panels/`)
- **Bán hàng tại quầy POS (`InvoiceCreationDialog`)**:
  - Cột 1: Danh sách sản phẩm còn hàng trong kho kèm ô tìm kiếm nhanh, chọn số lượng và thêm vào bảng tạm.
  - Cột 2: Bảng chi tiết đơn hàng tạm tính, chọn khách hàng (hoặc Khách hàng vãng lai), nhập tỉ lệ chiết khấu (%), tự động tính tổng tiền thanh toán.
  - Nút "Thanh toán & Lưu hóa đơn": Tự động trừ tồn kho sản phẩm trong CSDL, ghi nhận hóa đơn bán và chi tiết hóa đơn vào CSDL qua transaction an toàn, hỏi người dùng có muốn xuất/in hóa đơn ra file Excel ngay lập tức.
- **Quản lý Đơn hàng (`OrderManagementPanel`)**:
  - Danh sách hóa đơn bán hàng theo thời gian thực (Số HĐ, Ngày bán, Khách hàng, Nhân viên lập, Tổng tiền).
  - Tìm kiếm nhanh theo Số hóa đơn hoặc Mã khách hàng.
  - Modal xem chi tiết danh sách mặt hàng, số lượng, đơn giá, chiết khấu của từng hóa đơn.
  - In/Xuất lại hóa đơn ra file Excel chuyên nghiệp.

### 5. Phân hệ Nhập kho (`src/GUI/panels/`)
- **Lập phiếu nhập (`ImportCreationDialog`)**: Chọn nhà cung cấp (`nhacungcap`), chọn sản phẩm, nhập số lượng, đơn giá nhập và chiết khấu.
- **Quản lý Nhập hàng (`ImportManagementPanel`)**: Quản lý lịch sử nhập kho, xem chi tiết và in phiếu nhập hàng ra Excel.

### 6. Phân hệ Mua sắm Khách hàng (`src/GUI/panels/`)
- **Cửa hàng thời trang (`ShopGridPanel`)**: Hiển thị sản phẩm dạng lưới thẻ (Product Cards Grid) với ảnh lớn, tên, kích cỡ, màu sắc, đơn giá bán nổi bật và nút "Thêm vào giỏ".
- **Giỏ hàng (`CartPanel`)**: Xem các mặt hàng đã chọn, tăng/giảm số lượng trực tiếp (+1 / -1), xóa mặt hàng, xem tổng tiền thanh toán và bấm "Đặt hàng ngay".

### 7. Phân hệ Quản trị Người dùng & Hồ sơ (`src/GUI/panels/`)
- **Quản lý Nhân viên (`StaffManagementPanel`)**: Quản lý hồ sơ nhân viên, chức vụ công việc (`congviec`), thêm mới nhân viên đồng thời cấp tài khoản đăng nhập hệ thống, cập nhật thông tin và xóa nhân viên.
- **Quản lý Khách hàng (`CustomerManagementPanel`)**: Quản lý thông tin khách hàng, số điện thoại, email, địa chỉ; hỗ trợ chỉnh sửa và xóa khách hàng.
- **Hồ sơ cá nhân & Đổi mật khẩu (`ProfilePanel`)**:
  - Giao diện Card Panel căn giữa màn hình thoáng đãng, sang trọng.
  - Tự động nhận diện dữ liệu tương ứng từ bảng `nhanvien` (đối với Admin/Nhân viên) hoặc `khachhang` (đối với Khách hàng).
  - Chế độ "Chỉnh sửa" thông tin cá nhân kèm validate SĐT và Email.
  - Form đổi mật khẩu bảo mật (kiểm tra mật khẩu cũ, khớp mật khẩu mới).

---

## 🎨 Thiết kế giao diện Modern Flat UI

Toàn bộ tầng giao diện được xây dựng mới 100% bằng Java Swing kết hợp thư viện FlatLaf Look and Feel:

- **Khởi tạo FlatLaf chuẩn mực**:
  ```java
  FlatLightLaf.setup();
  UIManager.put("Button.arc", 8);
  UIManager.put("Component.arc", 8);
  UIManager.put("TextComponent.arc", 8);
  UIManager.put("ScrollBar.thumbArc", 8);
  UIManager.put("Table.rowHeight", 38);
  UIManager.put("TableHeader.height", 40);
  ```
- **Hệ thống Design Tokens (`ThemeConfig.java`)**:
  - `PRIMARY`: `#2563EB` (Royal Blue)
  - `PRIMARY_DARK`: `#1D4ED8`
  - `PRIMARY_LIGHT`: `#EFF6FF`
  - `SIDEBAR_BG`: `#0F172A` (Slate 900)
  - `SIDEBAR_HOVER`: `#1E293B`
  - `SIDEBAR_ACTIVE`: `#2563EB`
  - `BG_MAIN`: `#F8FAFC`
  - `SURFACE`: `#FFFFFF`
  - `BORDER`: `#E2E8F0`
  - `TEXT_MAIN`: `#0F172A`
  - `TEXT_MUTED`: `#64748B`
  - `DANGER`: `#EF4444` | `SUCCESS`: `#10B981` | `WARNING`: `#F59E0B`
- **Quản lý Layout khoa học**:
  - **Tuyệt đối không dùng `null layout` (`setBounds`)**, không dùng GUI Builder tự sinh mã rác.
  - Sử dụng kết hợp `BorderLayout`, `GridBagLayout` (cho form nhập liệu 2 cột cân đối), `CardLayout` (cho chuyển tab), `FlowLayout` và `BoxLayout`.
  - Mọi trường dữ liệu, tiêu đề bảng, nút bấm co giãn hoàn hảo trên màn hình từ 1280x720 trở lên, không bị cắt xén chữ (truncation).
- **Quy chuẩn Format**:
  - Tiền tệ: Chuẩn VNĐ dạng `400.000 ₫` thông qua `Locale.forLanguageTag("vi-VN")`.
  - Ngày tháng: Chuẩn `dd/MM/yyyy HH:mm`.

---

## 📁 Cấu trúc mã nguồn chi tiết

```
OOP-with-java/
├── lib/                                            # Thư viện phụ thuộc (JARs)
│   ├── flatlaf-3.5.4.jar                           # Look and Feel phẳng hiện đại
│   ├── mysql-connector-j-9.2.0.jar                 # Driver kết nối MySQL
│   ├── poi-5.2.3.jar / poi-ooxml-*.jar             # Xử lý xuất báo cáo Excel (.xlsx)
│   ├── jakarta.mail-2.0.1.jar                      # Dịch vụ gửi email OTP
│   ├── jakarta.activation-2.0.1.jar
│   ├── commons-io-2.11.0.jar / commons-codec-*.jar
│   └── xmlbeans-5.1.1.jar / log4j-api-2.18.0.jar
├── images/                                         # Kho hình ảnh sản phẩm & mẫu captcha
│   ├── samples/                                    # Hơn 300 mẫu ảnh Captcha (22d5n.png,...)
│   └── ...                                         # Ảnh sản phẩm tải lên
├── src/
│   ├── Main.java                                   # Điểm khởi chạy ứng dụng
│   ├── Model/                                      # Domain Entities
│   │   ├── Nguoi.java                              # Abstract base class cho con người
│   │   ├── NhanVien.java / KhachHang.java          # Kế thừa Nguoi
│   │   ├── HoaDon.java                             # Abstract base class cho hóa đơn
│   │   ├── HoaDonBan.java / HoaDonNhap.java        # Kế thừa HoaDon
│   │   ├── ChiTietHoaDon.java / ItemGioHang.java   # Chi tiết mục hàng
│   │   ├── SanPham.java                            # Thực thể sản phẩm quần áo
│   │   ├── TaiKhoan.java / LoaiTaiKhoan.java       # Tài khoản & Enum vai trò
│   │   ├── NhaCungCap.java / CongViec.java         # Nhà cung ứng & Vị trí công việc
│   │   └── TheLoai.java / Co.java / Mau.java       # Các thuộc tính danh mục
│   │       / Mua.java / DoiTuong.java
│   │       / ChatLieu.java / NoiSanXuat.java
│   ├── DAO/                                        # Data Access Object Layer
│   │   ├── DatabaseConnection.java                 # Singleton quản lý kết nối MySQL
│   │   ├── IGenericDAO.java                        # Generic CRUD DAO Interface
│   │   ├── ISanPhamDAO.java / SanPhamDAOImpl.java  # Truy vấn sản phẩm (JOIN, filter, search)
│   │   ├── IHoaDonBanDAO.java / HoaDonBanDAOImpl.java
│   │   ├── IHoaDonNhapDAO.java / HoaDonNhapDAOImpl.java
│   │   ├── ITaiKhoanDAO.java / TaiKhoanDAOImpl.java
│   │   ├── INhanVienDAO.java / NhanVienDAOImpl.java
│   │   ├── IKhachHangDAO.java / KhachHangDAOImpl.java
│   │   ├── IGioHangDAO.java / GioHangDAOImpl.java
│   │   └── IDanhMucDAO.java / DanhMucDAOImpl.java  # Nạp dữ liệu các bảng thuộc tính
│   ├── Service/                                    # Business Logic Layer
│   │   ├── UserSession.java                        # Singleton quản lý phiên đăng nhập
│   │   ├── IAuthService.java / AuthServiceImpl.java
│   │   ├── IProductService.java / ProductServiceImpl.java
│   │   ├── IOrderService.java / OrderServiceImpl.java
│   │   ├── IImportService.java / ImportServiceImpl.java
│   │   ├── IUserService.java / UserServiceImpl.java
│   │   ├── IReportService.java / ReportServiceImpl.java # Xuất báo cáo Excel qua POI
│   │   ├── IOTPService.java / OTPServiceImpl.java       # Sinh & gửi OTP qua Jakarta Mail
│   │   ├── dto/
│   │   │   └── CaptchaDTO.java                     # Đóng gói dữ liệu xác thực Captcha
│   │   └── exception/                              # Custom Runtime Exceptions
│   │       ├── BusinessException.java
│   │       ├── AuthenticationException.java
│   │       └── ValidationException.java
│   └── GUI/                                        # Presentation Layer (Modern Flat UI)
│       ├── Start.java                              # Điểm mở giao diện đăng nhập
│       ├── common/                                 # Lớp tiện ích giao diện
│       │   ├── ThemeConfig.java                    # Cấu hình FlatLaf, font, bảng màu
│       │   ├── CurrencyUtil.java                   # Format tiền tệ VNĐ chuẩn
│       │   ├── ImageUtil.java                      # Smooth scale, placeholder, copy ảnh
│       │   ├── ValidationUtil.java                 # Kiểm tra định dạng Email, Phone, Số
│       │   └── AppNavigator.java                   # Điều hướng chuyển cửa sổ an toàn
│       ├── auth/                                   # Phân hệ Xác thực
│       │   ├── LoginFrame.java                     # Cửa sổ đăng nhập (Banner + Form + Captcha)
│       │   ├── RegisterDialog.java                 # Modal đăng ký khách hàng
│       │   └── ForgotPasswordDialog.java           # Modal khôi phục mật khẩu qua OTP
│       ├── main/                                   # Khung sườn ứng dụng chính
│       │   └── MainDashboardFrame.java             # Dashboard đa vai trò, Sidebar động
│       ├── panels/                                 # Các màn hình chức năng nghiệp vụ
│       │   ├── DashboardOverviewPanel.java         # Thống kê doanh thu, bán chạy, tồn kho
│       │   ├── ProductManagementPanel.java         # Bảng SP, tìm kiếm, lọc, xuất Excel
│       │   ├── ProductFormDialog.java              # Modal thêm/sửa sản phẩm 2 cột
│       │   ├── OrderManagementPanel.java           # Quản lý hóa đơn bán, xem chi tiết
│       │   ├── InvoiceCreationDialog.java          # Màn hình bán hàng tại quầy POS
│       │   ├── ImportManagementPanel.java          # Quản lý hóa đơn nhập hàng
│       │   ├── ImportCreationDialog.java           # Lập phiếu nhập kho từ nhà cung cấp
│       │   ├── StaffManagementPanel.java           # Quản lý nhân viên & tài khoản
│       │   ├── CustomerManagementPanel.java        # Quản lý danh sách khách hàng
│       │   ├── ShopGridPanel.java                  # Mua sắm dạng thẻ cho Khách hàng
│       │   ├── CartPanel.java                      # Giỏ hàng & Đặt hàng của Khách hàng
│       │   └── ProfilePanel.java                   # Hồ sơ cá nhân & Đổi mật khẩu
│       ├── theme/
│       │   └── UITheme.java                        # Compatibility facade trỏ vào ThemeConfig
│       └── util/
│           └── AppNavigator.java                   # Compatibility facade trỏ vào AppNavigator
├── ClothingStore.sql                               # Kịch bản tạo CSDL & dữ liệu mẫu
├── repomix.config.json                             # Cấu hình Repomix tự động đồng bộ
└── README.md                                       # Tài liệu hướng dẫn dự án
```

---

## 🗄️ Cơ sở dữ liệu (Database Schema)

Dự án sử dụng cơ sở dữ liệu quan hệ **MySQL** (`ClothingStore`), bao gồm 18 bảng:

| Tên bảng | Chức năng chi tiết |
|---|---|
| `taikhoan` | Tài khoản đăng nhập (`MaTaiKhoan`, `TenDangNhap`, `MatKhau`, `LoaiTaiKhoan`, `DangNhap`) |
| `sanpham` | Thông tin quần áo (`MaQuanAo`, `TenQuanAo`, `MaLoai`, `MaCo`, `MaMau`, `MaMua`, `MaDoiTuong`, `MaChatLieu`, `MaNSX`, `SoLuong`, `Anh`, `DonGiaNhap`, `DonGiaBan`) |
| `theloai` | Danh mục loại quần áo (Áo sơ mi, Áo thun, Quần jean, Váy...) |
| `co` | Kích cỡ (S, M, L, XL, XXL...) |
| `chatlieu` | Chất liệu vải (Cotton, Kaki, Jean, Lụa, Nỉ...) |
| `mau` | Màu sắc (Trắng, Đen, Xanh, Đỏ, Vàng...) |
| `mua` | Mùa thời trang (Xuân, Hạ, Thu, Đông, Bốn mùa) |
| `doituong` | Đối tượng sử dụng (Nam, Nữ, Trẻ em, Unisex) |
| `noisanxuat` | Xuất xứ / Nơi sản xuất (Việt Nam, Hàn Quốc, Nhật Bản...) |
| `nhacungcap` | Nhà cung cấp nhập hàng (`MaNCC`, `TenNCC`, `DiaChi`, `SoDienThoai`) |
| `congviec` | Vị trí chức vụ nhân viên (`MaCV`, `TenCV`) |
| `nhanvien` | Hồ sơ nhân viên (`MaNhanVien`, `TenNhanVien`, `GioiTinh`, `NgaySinh`, `DiaChi`, `SoDienThoai`, `Email`, `MaCV`, `MaTaiKhoan`) |
| `khachhang` | Hồ sơ khách hàng (`MaKhachHang`, `TenKhach`, `SoDienThoai`, `DiaChi`, `Email`, `MaTaiKhoan`) |
| `giohang` | Lưu giỏ hàng của khách (`MaKhachHang`, `MaQuanAo`, `SoLuong`, `DonGiaBan`, `TrangThai`) |
| `hoadonban` | Hóa đơn bán lẻ (`SoHoaDonBan`, `MaKhachHang`, `NgayBan`, `MaNhanVien`, `TongTien`) |
| `chitiethoadonban`| Chi tiết từng mặt hàng bán (`SoHoaDonBan`, `MaQuanAo`, `SoLuong`, `GiamGia`, `ThanhTien`) |
| `hoadonnhap` | Hóa đơn nhập hàng (`SoHoaDonNhap`, `MaNhanVien`, `NgayNhap`, `MaNCC`, `TongTien`) |
| `chitiethoadonnhap`| Chi tiết từng mặt hàng nhập (`SoHoaDonNhap`, `MaQuanAo`, `SoLuong`, `DonGia`, `GiamGia`, `ThanhTien`) |
| `capcha` | Danh mục ảnh và đáp án CAPTCHA (`MaAnh`, `LinkAnh`, `KetQua`) |

---

## 🛠 Công nghệ & Thư viện phụ thuộc

| Thư viện / Công nghệ | Phiên bản | Mục đích sử dụng |
|---|:---:|---|
| **Java SE (JDK)** | 17+ | Nền tảng ngôn ngữ chính |
| **Java Swing thuần** | Core | Xây dựng giao diện người dùng desktop chuẩn |
| **FlatLaf** | `3.5.4` | Look and Feel hiện đại, bo góc phẳng chuẩn 2026 |
| **MySQL Connector/J** | `9.2.0` | Driver kết nối JDBC CSDL MySQL |
| **Apache POI & POI-OOXML** | `5.2.3` | Tạo và xuất báo cáo, hóa đơn định dạng Microsoft Excel (`.xlsx`) |
| **Jakarta Mail & Activation** | `2.0.1` | Dịch vụ gửi email mã OTP khôi phục mật khẩu qua giao thức SMTP |
| **Apache Commons IO / Codec** | `2.11.0 / 1.15` | Tiện ích hỗ trợ xử lý dữ liệu nhị phân và tệp tin |
| **SparseBitSet** | `1.2` | Thư viện tối ưu cấu trúc dữ liệu cho Apache POI |
| **Log4j API** | `2.18.0` | Ghi nhận nhật ký hoạt động |

---

## 💻 Yêu cầu hệ thống & Hướng dẫn cài đặt

### 1. Yêu cầu môi trường
- **Hệ điều hành**: Windows 10/11, Linux (Ubuntu, Debian, Fedora,...) hoặc macOS.
- **Java Development Kit (JDK)**: Phiên bản 17 hoặc mới hơn (khuyên dùng OpenJDK 17 hoặc 21).
- **MySQL Database Server**: Phiên bản 8.0 trở lên.
- **Môi trường phát triển**: VS Code, IntelliJ IDEA, Eclipse hoặc NetBeans.

### 2. Thiết lập Cơ sở dữ liệu MySQL
1. Khởi động dịch vụ MySQL Server.
2. Mở MySQL client (hoặc MySQL Workbench / DBeaver / phpMyAdmin) và thực thi file kịch bản có sẵn trong thư mục gốc:
   ```bash
   mysql -u root -p < ClothingStore.sql
   ```
   *(File `ClothingStore.sql` sẽ tự động tạo database `ClothingStore`, cấu trúc 18 bảng và nạp sẵn toàn bộ dữ liệu mẫu bao gồm tài khoản, sản phẩm, danh mục và ảnh captcha).*

### 3. Cấu hình kết nối JDBC
Kiểm tra và cập nhật tài khoản/mật khẩu MySQL của bạn trong file [`src/DAO/DatabaseConnection.java`](file:///home/iloveflo/Documents/OOP-with-java/src/DAO/DatabaseConnection.java):
```java
private static final String URL = "jdbc:mysql://localhost:3306/ClothingStore?useSSL=false&allowPublicKeyRetrieval=true";
private static final String USER = "root";
private static final String PASSWORD = "your_mysql_password";
```

---

## 🚀 Hướng dẫn biên dịch & Vận hành

### 🔹 Cách 1: Chạy bằng dòng lệnh (Terminal / Command Prompt)

#### Trên Linux / macOS:
```bash
# 1. Tạo thư mục chứa file binary đã biên dịch
mkdir -p bin

# 2. Biên dịch toàn bộ mã nguồn
javac -cp "lib/*:src" -d bin src/Model/*.java src/DAO/*.java src/Service/*.java src/Service/*/*.java src/GUI/*/*.java src/GUI/*.java src/Main.java

# 3. Khởi chạy ứng dụng
java -cp "bin:lib/*" Main
```

#### Trên Windows (Command Prompt hoặc PowerShell):
```cmd
:: 1. Tạo thư mục bin
if not exist bin mkdir bin

:: 2. Biên dịch toàn bộ mã nguồn (dùng dấu chấm phẩy ; cho classpath)
javac -cp "lib/*;src" -d bin src/Model/*.java src/DAO/*.java src/Service/*.java src/Service/*/*.java src/GUI/*/*.java src/GUI/*.java src/Main.java

:: 3. Khởi chạy ứng dụng
java -cp "bin;lib/*" Main
```

---

### 🔹 Cách 2: Chạy trên Visual Studio Code
1. Mở thư mục dự án `OOP-with-java` bằng VS Code.
2. Cài đặt extension: **Extension Pack for Java** (của Microsoft / Red Hat).
3. VS Code sẽ tự động nhận diện cấu hình trong `.vscode/settings.json`, `.project` và `.classpath`:
   - Source folder: `src/`
   - Output folder: `bin/`
   - Libraries: `lib/**/*.jar`
4. Mở file [`src/Main.java`](file:///home/iloveflo/Documents/OOP-with-java/src/Main.java), nhấn nút **Run** (hoặc phím `F5`) để khởi chạy trực tiếp.

---

### 🔑 Tài khoản mẫu thử nghiệm hệ thống

Dựa trên dữ liệu khởi tạo trong `ClothingStore.sql`:

| Tên đăng nhập | Mật khẩu | Vai trò | Không gian làm việc hiển thị |
|---|---|---|---|
| `admin` | `admin123` | **Admin** | Toàn quyền: Thống kê doanh thu, Sản phẩm, Bán hàng, Nhập kho, Nhân viên, Khách hàng |
| `nhanvien1` | `123456` | **Nhân viên** | Bán hàng tại quầy POS, Quản lý đơn hàng, Nhập kho, Cảnh báo tồn kho |
| `khachhang1`| `123456` | **Khách hàng** | Cửa hàng thẻ sản phẩm, Giỏ hàng, Đặt hàng trực tuyến, Lịch sử mua hàng |

---

<p align="center">
  <b>Tôn vinh phong cách - Khẳng định cá tính</b><br>
  <i>Hệ thống Quản lý Cửa hàng Quần áo © 2026</i>
</p>
