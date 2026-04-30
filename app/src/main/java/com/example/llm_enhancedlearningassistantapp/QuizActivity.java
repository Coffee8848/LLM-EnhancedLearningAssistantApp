package com.example.llm_enhancedlearningassistantapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class QuizActivity extends AppCompatActivity {
    private SessionManager sessionManager;
    private LlmApiClient llmApiClient;

    private RadioGroup question1Group;
    private RadioGroup question2Group;
    private TextView hintQ1Text;
    private TextView hintQ2Text;
    private TextView promptText;
    private TextView responseText;
    private ProgressBar hintLoadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        sessionManager = new SessionManager(this);
        llmApiClient = new LlmApiClient();

        TextView titleText = findViewById(R.id.tvTaskTitle);
        TextView descriptionText = findViewById(R.id.tvTaskDescription);
        TextView q1Text = findViewById(R.id.tvQuestion1);
        TextView q2Text = findViewById(R.id.tvQuestion2);

        question1Group = findViewById(R.id.rgQuestion1);
        question2Group = findViewById(R.id.rgQuestion2);
        hintQ1Text = findViewById(R.id.tvHintQ1);
        hintQ2Text = findViewById(R.id.tvHintQ2);
        promptText = findViewById(R.id.tvQuizPrompt);
        responseText = findViewById(R.id.tvQuizResponse);
        hintLoadingBar = findViewById(R.id.pbHintLoading);

        Button backButton = findViewById(R.id.btnBackFromQuiz);
        Button hintQ1Button = findViewById(R.id.btnGenerateHintQ1);
        Button hintQ2Button = findViewById(R.id.btnGenerateHintQ2);
        Button submitButton = findViewById(R.id.btnSubmitQuiz);

        titleText.setText(DummyData.TASK_TITLE);
        descriptionText.setText(DummyData.TASK_DESCRIPTION);
        q1Text.setText("1. " + DummyData.QUESTION_1);
        q2Text.setText("2. " + DummyData.QUESTION_2);
        promptText.setText("Prompt:");
        responseText.setText("Response:");

        backButton.setOnClickListener(v -> finish());
        hintQ1Button.setOnClickListener(v -> generateHintForQuestion1());
        hintQ2Button.setOnClickListener(v -> generateHintForQuestion2());
        submitButton.setOnClickListener(v -> submitQuiz());
    }

    private void generateHintForQuestion1() {
        String prompt = "Give one short hint without revealing the final answer. Question: "
                + DummyData.QUESTION_1
                + ". Options: O(n), O(log n), O(n log n).";
        askHint(prompt, hintQ1Text);
    }

    private void generateHintForQuestion2() {
        String prompt = "Give one short hint without revealing the final answer. Question: "
                + DummyData.QUESTION_2
                + ". Options: Queue, Stack, Linked List.";
        askHint(prompt, hintQ2Text);
    }

    private void askHint(String prompt, TextView targetHintView) {
        setHintLoading(true);
        promptText.setText("Prompt:\n" + prompt);
        responseText.setText("Response:\n");
        llmApiClient.sendPrompt(sessionManager.getApiKey(), prompt, new LlmApiClient.LlmCallback() {
            @Override
            public void onSuccess(String response) {
                runOnUiThread(() -> {
                    setHintLoading(false);
                    targetHintView.setText("AI Hint: " + response);
                    responseText.setText("Response:\n" + response);
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    setHintLoading(false);
                    targetHintView.setText("AI Hint: Failed to generate.");
                    responseText.setText("Response:\nFailed: " + errorMessage);
                });
            }
        });
    }

    private void submitQuiz() {
        int q1Index = getSelectedIndex(question1Group);
        int q2Index = getSelectedIndex(question2Group);

        if (q1Index < 0 || q2Index < 0) {
            Toast.makeText(this, "Please answer both questions.", Toast.LENGTH_SHORT).show();
            return;
        }

        int score = 0;
        if (q1Index == DummyData.QUESTION_1_CORRECT_INDEX) {
            score++;
        }
        if (q2Index == DummyData.QUESTION_2_CORRECT_INDEX) {
            score++;
        }

        Intent intent = new Intent(this, ResultsActivity.class);
        intent.putExtra("score", score);
        intent.putExtra("total", 2);
        intent.putExtra("q1_selected", DummyData.QUESTION_1_OPTIONS[q1Index]);
        intent.putExtra("q2_selected", DummyData.QUESTION_2_OPTIONS[q2Index]);
        startActivity(intent);
    }

    private int getSelectedIndex(RadioGroup group) {
        int selectedId = group.getCheckedRadioButtonId();
        if (selectedId == -1) {
            return -1;
        }
        RadioButton selectedButton = findViewById(selectedId);
        return group.indexOfChild(selectedButton);
    }

    private void setHintLoading(boolean loading) {
        hintLoadingBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        Button hintQ1Button = findViewById(R.id.btnGenerateHintQ1);
        Button hintQ2Button = findViewById(R.id.btnGenerateHintQ2);
        hintQ1Button.setEnabled(!loading);
        hintQ2Button.setEnabled(!loading);
    }
}
