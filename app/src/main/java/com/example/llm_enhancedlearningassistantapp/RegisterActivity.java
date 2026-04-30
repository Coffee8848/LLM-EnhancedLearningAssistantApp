package com.example.llm_enhancedlearningassistantapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText usernameInput = findViewById(R.id.etRegisterUsername);
        EditText emailInput = findViewById(R.id.etEmail);
        EditText confirmEmailInput = findViewById(R.id.etConfirmEmail);
        EditText passwordInput = findViewById(R.id.etRegisterPassword);
        EditText confirmPasswordInput = findViewById(R.id.etConfirmPassword);
        Button backButton = findViewById(R.id.btnBackFromRegister);
        Button createButton = findViewById(R.id.btnCreateAccount);

        SessionManager sessionManager = new SessionManager(this);

        backButton.setOnClickListener(v -> finish());

        createButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String confirmEmail = confirmEmailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String confirmPassword = confirmPasswordInput.getText().toString().trim();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please complete all required fields.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!email.equalsIgnoreCase(confirmEmail)) {
                Toast.makeText(this, "Emails do not match.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
                return;
            }

            sessionManager.setUsername(username);
            sessionManager.setEmail(email);
            startActivity(new Intent(this, InterestsActivity.class));
            finish();
        });
    }
}
