package constants;

public class ValidationConstants {
    private ValidationConstants() {}

    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int MIN_COMMENT_LENGTH = 25;

    public static final String FIO_REGEX = "^[\\u0410-\\u042F][\\u0430-\\u044F]+";
    public static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\s\\da-zA-Z]).{"
            + MIN_PASSWORD_LENGTH + ",}$";
}
