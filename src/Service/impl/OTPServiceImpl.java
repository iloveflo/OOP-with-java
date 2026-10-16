package Service.impl;

import DAO.DatabaseConnection;
import GUI.OTP;
import GUI.Start;
import Service.IOTPService;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;
import java.util.Random;

public class OTPServiceImpl implements IOTPService {

    private final Random random = new Random();

    @Override
    public boolean sendOTP(String tenDangNhap, JFrame currentFrame) {
        if (tenDangNhap == null || tenDangNhap.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Vui lòng nhập tên đăng nhập.");
            return false;
        }

        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            String sql = "SELECT tk.LoaiTaiKhoan, kh.Email AS EmailKH, nv.Email AS EmailNV "
                       + "FROM taikhoan tk "
                       + "LEFT JOIN khachhang kh ON tk.MaTaiKhoan = kh.MaTaiKhoan "
                       + "LEFT JOIN nhanvien nv ON tk.MaTaiKhoan = nv.MaTaiKhoan "
                       + "WHERE tk.TenDangNhap = ?";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, tenDangNhap.trim());
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String loai = rs.getString("LoaiTaiKhoan");
                        String email = "KhachHang".equalsIgnoreCase(loai) ? rs.getString("EmailKH") : rs.getString("EmailNV");

                        if (email == null || email.trim().isEmpty()) {
                            JOptionPane.showMessageDialog(null, "Không tìm thấy email cho tài khoản này.");
                            return false;
                        }

                        String otp = generateOTP(6);
                        boolean sent = sendEmail(email, otp);

                        if (sent) {
                            JOptionPane.showMessageDialog(null, "Mã OTP đã được gửi tới email: " + email);
                            GUI.util.AppNavigator.switchWindow(currentFrame, new OTP(tenDangNhap.trim(), otp));
                            return true;
                        } else {
                            JOptionPane.showMessageDialog(null, "Gửi email thất bại. Kiểm tra lại kết nối.");
                            return false;
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Tên đăng nhập không tồn tại.");
                        return false;
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi: " + ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean verifyAndResetPassword(String nhapOTP, String expectedOTP, String tenDangNhap, JFrame currentFrame) {
        if (nhapOTP == null || nhapOTP.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Vui lòng nhập mã OTP.");
            return false;
        }

        if (!nhapOTP.trim().equals(expectedOTP)) {
            JOptionPane.showMessageDialog(null, "Mã OTP không đúng.");
            return false;
        }

        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            String newPass = "admin123";
            String sql = "UPDATE taikhoan SET MatKhau = ? WHERE TenDangNhap = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, newPass);
                stmt.setString(2, tenDangNhap);

                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(null, "Đặt lại mật khẩu thành công!\nMật khẩu mới là: admin123");
                    GUI.util.AppNavigator.switchWindow(currentFrame, new Start());
                    return true;
                } else {
                    JOptionPane.showMessageDialog(null, "Không tìm thấy tài khoản.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean verifyOTPAndChangePassword(String inputOTP, String realOTP, String username, String newPassword) {
        if (inputOTP == null || !inputOTP.trim().equals(realOTP)) {
            return false;
        }

        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            String sql = "UPDATE taikhoan SET MatKhau = ? WHERE TenDangNhap = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, newPassword);
                ps.setString(2, username);
                return ps.executeUpdate() > 0;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private String generateOTP(int length) {
        String digits = "0123456789";
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < length; i++) {
            otp.append(digits.charAt(random.nextInt(digits.length())));
        }
        return otp.toString();
    }

    private boolean sendEmail(String toEmail, String otp) {
        final String fromEmail = "binha10k56@gmail.com";
        final String password = ""; // Có thể cấu hình hoặc app password

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(fromEmail));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            msg.setSubject("Mã OTP");
            msg.setText("Mã OTP của bạn là: " + otp);
            Transport.send(msg);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

