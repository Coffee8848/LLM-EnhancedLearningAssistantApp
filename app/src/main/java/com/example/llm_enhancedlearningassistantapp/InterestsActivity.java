package com.example.llm_enhancedlearningassistantapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class InterestsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interests);

        Button backButton = findViewById(R.id.btnBackFromInterests);
        Button nextButton = findViewById(R.id.btnNextFromInterests);
        SessionManager sessionManager = new SessionManager(this);

        List<CheckBox> checkBoxes = new ArrayList<>();
        checkBoxes.add(findViewById(R.id.cbAlgorithms));
        checkBoxes.add(findViewById(R.id.cbDataStructures));
        checkBoxes.add(findViewById(R.id.cbWebDev));
        checkBoxes.add(findViewById(R.id.cbTesting));
        checkBoxes.add(findViewById(R.id.cbDatabases));
        checkBoxes.add(findViewById(R.id.cbMobile));
        checkBoxes.add(findViewById(R.id.cbAi));
        checkBoxes.add(findViewById(R.id.cbMath));

        backButton.setOnClickListener(v -> finish());

        nextButton.setOnClickListener(v -> {
            List<String> picked = new ArrayList<>();
            for (CheckBox box : checkBoxes) {
                if (box.isChecked()) {
                    picked.add(box.getText().toString());
                }
            }

            if (picked.isEmpty()) {
                Toast.makeText(this, "Select at least one interest.", Toast.LENGTH_SHORT).show();
                return;
            }

            String csv = android.text.TextUtils.join(", ", picked);
            sessionManager.setInterests(csv);
            sessionManager.setLoggedIn(true);

            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}
