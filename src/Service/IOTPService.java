package Service;

import javax.swing.JFrame;

public interface IOTPService {
    String generateAndSendOTP(String tenDangNhap);
    boolean sendOTP(String tenDangNhap, JFrame currentFrame);
    boolean verifyAndResetPassword(String nhapOTP, String expectedOTP, String tenDangNhap, JFrame currentFrame);
    boolean verifyOTPAndChangePassword(String inputOTP, String realOTP, String username, String newPassword);
}

