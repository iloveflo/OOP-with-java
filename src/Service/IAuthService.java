package Service;

import Model.TaiKhoan;
import Service.dto.CaptchaDTO;

public interface IAuthService {
    TaiKhoan login(String username, String password, String inputCaptcha, CaptchaDTO currentCaptcha);
    void logout(String maTaiKhoan);
    CaptchaDTO getRandomCaptcha();
}

