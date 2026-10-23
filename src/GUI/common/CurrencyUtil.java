package GUI.common;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyUtil {
    private static final Locale VIETNAM = Locale.forLanguageTag("vi-VN");
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(VIETNAM);

    public static String format(BigDecimal amount) {
        if (amount == null) {
            return CURRENCY_FORMAT.format(BigDecimal.ZERO);
        }
        return CURRENCY_FORMAT.format(amount);
    }

    public static String format(double amount) {
        return CURRENCY_FORMAT.format(amount);
    }

    public static String format(long amount) {
        return CURRENCY_FORMAT.format(amount);
    }

    public static BigDecimal parse(String str) {
        if (str == null || str.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            // Strip non-digit characters except period and comma
            String cleaned = str.replaceAll("[^0-9]", "");
            return new BigDecimal(cleaned);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }
}

