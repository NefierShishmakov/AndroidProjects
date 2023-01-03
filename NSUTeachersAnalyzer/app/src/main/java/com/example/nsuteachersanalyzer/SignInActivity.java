package com.example.nsuteachersanalyzer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.apache.commons.validator.routines.EmailValidator;

import messages.ErrorMessages;
import constants.IntentExtraKeysConstants;
import database.DatabaseHelper;
import database.User;
import utils.Utils;

public class SignInActivity extends AppCompatActivity {

    private DatabaseHelper database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        this.database = new DatabaseHelper(this);

        this.setFullWindow();

        this.setButtonsOnClickListeners();
    }

    private void setButtonsOnClickListeners() {
        Button logInButton = findViewById(R.id.button_logIn);
        logInButton.setOnClickListener(this.logInButtonOnClickListener);

        ImageButton showMainWindowImageButton = findViewById(R.id.signIn_back_button);
        showMainWindowImageButton.setOnClickListener(this.showMainWindowImageButtonOnClickListener);
    }

    private void setFullWindow() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
    }

    private final View.OnClickListener showMainWindowImageButtonOnClickListener = view -> {
        Intent mainWindowIntent = new Intent(this, MainActivity.class);
        startActivity(mainWindowIntent);
    };

    private final View.OnClickListener logInButtonOnClickListener = view -> {
        EditText emailEditText = findViewById(R.id.email_edit_text_signIn);
        EditText passwordEditText = findViewById(R.id.password_edit_text_signIn);
        TextView errorTextView = findViewById(R.id.error_textViewSignIn);

        if (!Utils.noEmptyEditTexts(new EditText[] {emailEditText, passwordEditText})) {
            Utils.setErrorText(errorTextView, ErrorMessages.NOT_ALL_FIELDS_ENTERED_ERROR_TEXT);
            return;
        }

        if (!EmailValidator.getInstance().isValid(emailEditText.getText().toString())) {
            Utils.setErrorText(errorTextView, ErrorMessages.NOT_CORRECT_EMAIL_ERROR_TEXT);
            return;
        }

        if (!this.database.isUserEmailExists(DatabaseHelper.getDatabaseCorrectString(emailEditText.getText().toString()))) {
            Utils.setErrorText(errorTextView, ErrorMessages.NO_EMAIL_IN_DATABASE_ERROR_TEXT);
            return;
        }

        if (!this.database.isPasswordValid(DatabaseHelper.getDatabaseCorrectString(passwordEditText.getText().toString()),
                DatabaseHelper.getDatabaseCorrectString(emailEditText.getText().toString()))) {
            Utils.setErrorText(errorTextView, ErrorMessages.NOT_VALID_PASSWORD);
            return;
        }

        User user = this.database.getUser(DatabaseHelper.getDatabaseCorrectString(emailEditText.getText().toString()));
        String job = user.getJob();

        Intent resultIntent = null;

        if (job.equals(getResources().getString(R.string.student_job_title)))
        {
            resultIntent = new Intent(this, TeachersListActivity.class);
            resultIntent.putExtra(IntentExtraKeysConstants.SIGN_IN_ACTIVITY_KEY1, user.getId());
        }
        else if (job.equals(getResources().getString(R.string.teacher_job_title)))
        {
            resultIntent = new Intent(this, TeacherCommentsActivity.class);
            resultIntent.putExtra(IntentExtraKeysConstants.SIGN_IN_ACTIVITY_KEY2, user.getId());
        }

        startActivity(resultIntent);
    };
}