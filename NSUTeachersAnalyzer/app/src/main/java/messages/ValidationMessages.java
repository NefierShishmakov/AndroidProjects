package messages;

import constants.ValidationConstants;

public class ValidationMessages {
    private ValidationMessages() {}

    public static final String FIO_VALIDATION_MESSAGE = "Первая буква заглавная и всё поле состоит " +
            "только из букв.";
    public static final String PASSWORD_VALIDATION_MESSAGE = "Длина - минимум " + ValidationConstants.MIN_PASSWORD_LENGTH +
            " символов, " + "одна заглавная буква, одна цифра, один специальный символ, кроме пробела.";

    public static final String COMMENT_VALIDATION_MESSAGE = "Длина отзыва должна быть " +
            "минимум " + ValidationConstants.MIN_COMMENT_LENGTH + " символов";
}
