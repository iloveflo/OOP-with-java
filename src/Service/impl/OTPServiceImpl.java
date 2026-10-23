package Service.impl;

import DAO.DatabaseConnection;
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
    public String generateAndSendOTP(String tenDangNhap) {
        if (tenDangNhap == null || tenDangNhap.trim().isEmpty()) {
            return null;
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
                            return null;
                        }

                        String otp = generateOTP(6);
                        boolean sent = sendEmail(email, otp);
                        return sent ? otp : null;
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean sendOTP(String tenDangNhap, JFrame currentFrame) {
        String otp = generateAndSendOTP(tenDangNhap);
        if (otp != null) {
            JOptionPane.showMessageDialog(currentFrame, "Mã OTP đã được gửi tới email của tài khoản!");
            return true;
        } else {
            JOptionPane.showMessageDialog(currentFrame, "Không thể gửi OTP. Vui lòng kiểm tra lại tên đăng nhập hoặc kết nối.");
            return false;
        }
    }

    @Override
    public boolean verifyAndResetPassword(String nhapOTP, String expectedOTP, String tenDangNhap, JFrame currentFrame) {
        if (nhapOTP == null || nhapOTP.trim().isEmpty()) {
            JOptionPane.showMessageDialog(currentFrame, "Vui lòng nhập mã OTP.");
            return false;
        }

        if (!nhapOTP.trim().equals(expectedOTP)) {
            JOptionPane.showMessageDialog(currentFrame, "Mã OTP không đúng.");
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
                    JOptionPane.showMessageDialog(currentFrame, "Đặt lại mật khẩu thành công!\nMật khẩu mới là: admin123");
                    return true;
                } else {
                    JOptionPane.showMessageDialog(currentFrame, "Không tìm thấy tài khoản.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(currentFrame, "Lỗi: " + e.getMessage());
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

