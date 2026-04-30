package com.example.llm_enhancedlearningassistantapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(this);
        if (sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            return;
        }

        EditText usernameInput = findViewById(R.id.etUsername);
        EditText passwordInput = findViewById(R.id.etPassword);
        Button loginButton = findViewById(R.id.btnLogin);
        TextView createAccountLink = findViewById(R.id.tvCreateAccount);

        loginButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter username and password.", Toast.LENGTH_SHORT).show();
                return;
            }

            sessionManager.setUsername(username);
            sessionManager.setLoggedIn(true);
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });

        createAccountLink.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class))
        );
    }
}
