package com.example.nsuteachersanalyzer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.validator.routines.EmailValidator;

import java.util.Map;

import messages.ErrorMessages;
import constants.IntentExtraKeysConstants;
import constants.ValidationConstants;
import database.DatabaseHelper;
import database.Employee;
import messages.ValidationMessages;
import utils.Utils;

public class SignUpActivity extends AppCompatActivity {

    private ImageButton dropJobTitlesImageButton;
    private DatabaseHelper database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        this.database = new DatabaseHelper(this);

        this.setFullWindow();

        this.setButtonsOnClickListeners();

        TextView studentTextView = findViewById(R.id.student_job_title);
        TextView teacherTextView = findViewById(R.id.teacher_job_title);
        studentTextView.setOnClickListener(this.jobTitlesOnClickListener);
        teacherTextView.setOnClickListener(this.jobTitlesOnClickListener);
    }

    private void setButtonsOnClickListeners() {
        Button showCommentsActivityButton = findViewById(R.id.button_data_signUp);
        showCommentsActivityButton.setOnClickListener(this.showCommentsActivityButtonOnClickListener);

        this.dropJobTitlesImageButton = findViewById(R.id.drop_button);
        this.dropJobTitlesImageButton.setOnClickListener(this.dropJobTitlesImageButtonOnClickListener);

        ImageButton showMainWindowImageButton = findViewById(R.id.signUp_back_button);
        showMainWindowImageButton.setOnClickListener(this.showMainWindowImageButtonOnClickListener);
    }

    private void setFullWindow() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
    }

    private final View.OnClickListener showMainWindowImageButtonOnClickListener = view -> {
        Intent showMainWindowIntent = new Intent(this, MainActivity.class);
        startActivity(showMainWindowIntent);
    };

    private final View.OnClickListener dropJobTitlesImageButtonOnClickListener = view -> {
        ImageButton currButton = (ImageButton) view;
        LinearLayout jobTitlesLinearLayout = findViewById(R.id.job_titles);

        switch (jobTitlesLinearLayout.getVisibility()) {
            case View.GONE:
                jobTitlesLinearLayout.setVisibility(View.VISIBLE);
                currButton.setRotation(90);
                break;
            case View.VISIBLE:
                jobTitlesLinearLayout.setVisibility(View.GONE);
                currButton.setRotation(0);
                break;
            case View.INVISIBLE:
                break;
        }
    };

    private final View.OnClickListener showCommentsActivityButtonOnClickListener = view -> {
        EditText nameEditText = findViewById(R.id.name_editText);
        EditText surnameEditText = findViewById(R.id.surname_editText);
        EditText thirdnameEditText = findViewById(R.id.thirdname_editText);
        EditText emailEditText = findViewById(R.id.email_editText_SignUp);
        EditText passwordEditText = findViewById(R.id.password_editText_SignUp);
        EditText passwordConfirmationEditText = findViewById(R.id.password_confirm_editText_SignUp);
        TextView jobTextView = findViewById(R.id.jobLabel);

        TextView errorTextView = findViewById(R.id.error_textView_SignUp);

        EditText[] editTextArray = new EditText[] {nameEditText, surnameEditText, thirdnameEditText,
                emailEditText, passwordEditText, passwordConfirmationEditText};

        if (!Utils.noEmptyEditTexts(editTextArray) || jobTextView.getText().toString().isEmpty()) {
            Utils.setErrorText(errorTextView, ErrorMessages.NOT_ALL_FIELDS_ENTERED_ERROR_TEXT);
            return;
        }

        EditText[] FIO = new EditText[] {nameEditText, surnameEditText, thirdnameEditText};

        Map<EditText, String> errorsMap = Map.of(
                nameEditText, Utils.getFullMessage(ErrorMessages.NOT_CORRECT_NAME_ERROR_TEXT,
                        ValidationMessages.FIO_VALIDATION_MESSAGE),
                surnameEditText, Utils.getFullMessage(ErrorMessages.NOT_CORRECT_SURNAME_ERROR_TEXT,
                        ValidationMessages.FIO_VALIDATION_MESSAGE),
                thirdnameEditText, Utils.getFullMessage(ErrorMessages.NOT_CORRECT_THIRDNAME_ERROR_TEXT,
                        ValidationMessages.FIO_VALIDATION_MESSAGE));

        EditText errorEditText = Utils.isFIOCorrect(FIO);

        if (errorEditText != null) {
            Utils.setErrorText(errorTextView, errorsMap.get(errorEditText));
            return;
        }

        if (!EmailValidator.getInstance().isValid(emailEditText.getText().toString())) {
            Utils.setErrorText(errorTextView, ErrorMessages.NOT_CORRECT_EMAIL_ERROR_TEXT);
            return;
        }

        if (!passwordEditText.getText().toString().matches(ValidationConstants.PASSWORD_REGEX)) {
            Utils.setErrorText(errorTextView, Utils.getFullMessage(ErrorMessages.NOT_CORRECT_PASSWORD,
                    ValidationMessages.PASSWORD_VALIDATION_MESSAGE));
            return;
        }

        if (!passwordEditText.getText().toString().equals(passwordConfirmationEditText.getText().toString())) {
            Utils.setErrorText(errorTextView, ErrorMessages.PASSWORDS_NOT_MATCH_ERROR_TEXT);
            return;
        }

        if (this.database.isUserEmailExists(DatabaseHelper.getDatabaseCorrectString(emailEditText.getText().toString()))) {
            Utils.setErrorText(errorTextView, ErrorMessages.EMAIL_ALREADY_IN_DATABASE_ERROR_TEXT);
            return;
        }

        String correctedName = DatabaseHelper.getDatabaseCorrectString(nameEditText.getText().toString());
        String correctedSurname = DatabaseHelper.getDatabaseCorrectString(surnameEditText.getText().toString());
        String correctedThirdName = DatabaseHelper.getDatabaseCorrectString(thirdnameEditText.getText().toString());

        if (this.database.isUserFIOExists(correctedSurname, correctedName, correctedThirdName)) {
            Utils.setErrorText(errorTextView, ErrorMessages.FIO_ALREADY_IN_DATABASE_ERROR_TEXT);
            return;
        }

        Employee newEmployee = new Employee(surnameEditText.getText().toString(),
                nameEditText.getText().toString(), thirdnameEditText.getText().toString(),
                jobTextView.getText().toString(), emailEditText.getText().toString(),
                passwordEditText.getText().toString());

        this.database.addNewEmployee(newEmployee);

        int maxId = this.database.getMaxEmployeeId();
        String job = jobTextView.getText().toString();

        Intent resultIntent = null;

        if (job.equals(getResources().getString(R.string.student_job_title)))
        {
            resultIntent = new Intent(this, TeachersListActivity.class);
            resultIntent.putExtra(IntentExtraKeysConstants.SIGN_UP_ACTIVITY_KEY1, maxId);
        }
        else if (job.equals(getResources().getString(R.string.teacher_job_title)))
        {
            resultIntent = new Intent(this, TeacherCommentsActivity.class);
            resultIntent.putExtra(IntentExtraKeysConstants.SIGN_UP_ACTIVITY_KEY2, maxId);
        }

        startActivity(resultIntent);
    };

    private final View.OnClickListener jobTitlesOnClickListener = view -> {
        LinearLayout jobTitlesLinearLayout = findViewById(R.id.job_titles);
        TextView jobTitleText = findViewById(R.id.jobLabel);
        TextView currentTextView = (TextView) view;

        jobTitleText.setText(currentTextView.getText().toString());
        jobTitleText.setTextColor(currentTextView.getTextColors().getDefaultColor());
        jobTitlesLinearLayout.setVisibility(View.GONE);

        this.dropJobTitlesImageButton.setRotation(0);
    };
}