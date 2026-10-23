package GUI.common;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public class ValidationUtil {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^0[0-9]{9,10}$");

    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }

    public static boolean isPositiveInt(String str) {
        try {
            int val = Integer.parseInt(str.trim());
            return val > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isNonNegativeInt(String str) {
        try {
            int val = Integer.parseInt(str.trim());
            return val >= 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isPositiveBigDecimal(String str) {
        try {
            BigDecimal val = new BigDecimal(str.trim().replaceAll("[^0-9.]", ""));
            return val.compareTo(BigDecimal.ZERO) >= 0;
        } catch (Exception e) {
            return false;
        }
    }
}

