package utils;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Pattern;

import constants.ValidationConstants;

public class Utils {
    private Utils() {}

    public static boolean noEmptyEditTexts(EditText[] editTextArray) {
        for (EditText editText: editTextArray) {
            if (editText.getText().toString().isEmpty()) {
                return false;
            }
        }

        return true;
    }

    public static void setErrorText(TextView errorTextView, String error) {
        errorTextView.setVisibility(View.VISIBLE);
        errorTextView.setText(error);
    }

    public static EditText isFIOCorrect(EditText[] FIO) {
        for (EditText editText: FIO) {
            if (!Pattern.matches(ValidationConstants.FIO_REGEX, editText.getText().toString())) {
                return editText;
            }
        }

        return null;
    }

    public static String getFullMessage(String error, String validation) {
        return error + System.lineSeparator() + validation;
    }

    public static String getPartOfComment(String fullComment)
    {
        return fullComment.substring(0, 22) + "...";
    }
}
