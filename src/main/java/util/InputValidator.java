package util;

public class InputValidator {

    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean isNumeric(String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }

        return value.matches("\\d+");
    }

    public static boolean isValidEmail(String email) {

        if (email == null) {
            return false;
        }

        String pattern =
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

        return email.matches(pattern);
    }

    public static boolean isPositiveDecimal(String value) {

        try {
            double number = Double.parseDouble(value);
            return number > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isValidPhone(String phone) {

        if (phone == null) {
            return false;
        }

        return phone.matches("\\d{11}");
    }
}