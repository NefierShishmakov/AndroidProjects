package com.example.nsuteachersanalyzer;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setFullWindow();

        this.setButtonsOnClickListeners();
    }

    private void setButtonsOnClickListeners() {
        Button openSignInActivityButton = findViewById(R.id.button_signIn);
        Button openSignUpActivityButton = findViewById(R.id.button_signUp);

        openSignInActivityButton.setOnClickListener(this.openActivityButtonOnClickListener);
        openSignUpActivityButton.setOnClickListener(this.openActivityButtonOnClickListener);
    }

    private void setFullWindow() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
    }

    @SuppressLint("NonConstantResourceId")
    private final View.OnClickListener openActivityButtonOnClickListener = view -> {
        Intent intent = null;

        switch (view.getId())
        {
            case R.id.button_signIn:
                intent = new Intent(this, SignInActivity.class);
                break;
            case R.id.button_signUp:
                intent = new Intent(this, SignUpActivity.class);
                break;
        }

        startActivity(intent);
    };
}