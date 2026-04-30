package com.example.llm_enhancedlearningassistantapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ResultsActivity extends AppCompatActivity {
    private SessionManager sessionManager;
    private LlmApiClient llmApiClient;

    private TextView q1ExplainText;
    private TextView q2ExplainText;
    private TextView promptText;
    private TextView responseText;
    private ProgressBar loadingBar;

    private int score;
    private int total;
    private String q1Selected;
    private String q2Selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        sessionManager = new SessionManager(this);
        llmApiClient = new LlmApiClient();

        score = getIntent().getIntExtra("score", 0);
        total = getIntent().getIntExtra("total", 2);
        q1Selected = getIntent().getStringExtra("q1_selected");
        q2Selected = getIntent().getStringExtra("q2_selected");

        TextView resultTitle = findViewById(R.id.tvResultsTitle);
        TextView question1Result = findViewById(R.id.tvResultQuestion1);
        TextView question2Result = findViewById(R.id.tvResultQuestion2);
        TextView question3Result = findViewById(R.id.tvResultQuestion3);
        q1ExplainText = findViewById(R.id.tvExplainQuestion1);
        q2ExplainText = findViewById(R.id.tvExplainQuestion2);
        promptText = findViewById(R.id.tvResultsPrompt);
        responseText = findViewById(R.id.tvResultsResponse);

        loadingBar = findViewById(R.id.pbAiLoading);

        Button backButton = findViewById(R.id.btnBackFromResults);
        Button continueButton = findViewById(R.id.btnContinueFromResults);
        Button explainButton = findViewById(R.id.btnGenerateExplanations);

        resultTitle.setText("Your Results: " + score + " / " + total);
        question1Result.setText("1. " + DummyData.QUESTION_1 + "\nYour answer: " + q1Selected);
        question2Result.setText("2. " + DummyData.QUESTION_2 + "\nYour answer: " + q2Selected);
        question3Result.setText("3. Overall\n" + (score >= 1 ? "Good progress. Keep practicing." : "Review basics and retry."));
        q1ExplainText.setText("AI Explanation: Loading...");
        q2ExplainText.setText("AI Explanation: Loading...");
        promptText.setText("Prompt:");
        responseText.setText("Response:");

        sessionManager.setLastQuizResult(score, total, q1Selected, q2Selected);

        backButton.setOnClickListener(v -> finish());

        continueButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        explainButton.setOnClickListener(v -> generateExplanations());

        generateExplanations();
    }

    private void generateExplanations() {
        String prompt = "Explain these 2 answered questions for a beginner student."
                + "\nQ1: " + DummyData.QUESTION_1
                + "\nStudent answer: " + q1Selected
                + "\nCorrect answer: " + DummyData.QUESTION_1_OPTIONS[DummyData.QUESTION_1_CORRECT_INDEX]
                + "\nQ2: " + DummyData.QUESTION_2
                + "\nStudent answer: " + q2Selected
                + "\nCorrect answer: " + DummyData.QUESTION_2_OPTIONS[DummyData.QUESTION_2_CORRECT_INDEX]
                + "\nReturn format exactly:"
                + "\nQ1: <short explanation>"
                + "\nQ2: <short explanation>";

        setLoadingState(true);
        promptText.setText("Prompt:\n" + prompt);
        responseText.setText("Response:\n");

        llmApiClient.sendPrompt(sessionManager.getApiKey(), prompt, new LlmApiClient.LlmCallback() {
            @Override
            public void onSuccess(String response) {
                runOnUiThread(() -> {
                    setLoadingState(false);
                    bindExplanationText(response);
                    responseText.setText("Response:\n" + response);
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    setLoadingState(false);
                    q1ExplainText.setText("AI Explanation: Failed to generate.");
                    q2ExplainText.setText("AI Explanation: Failed to generate.");
                    responseText.setText("Response:\nFailed: " + errorMessage);
                });
            }
        });
    }

    private void bindExplanationText(String response) {
        String q1 = findLine(response, "Q1:");
        String q2 = findLine(response, "Q2:");
        q1ExplainText.setText("AI Explanation: " + (q1.isEmpty() ? response : q1));
        q2ExplainText.setText("AI Explanation: " + (q2.isEmpty() ? response : q2));
    }

    private String findLine(String input, String prefix) {
        String[] lines = input.split("\\r?\\n");
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.startsWith(prefix)) {
                return trimmed.substring(prefix.length()).trim();
            }
        }
        return "";
    }

    private void setLoadingState(boolean loading) {
        loadingBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        Button explainButton = findViewById(R.id.btnGenerateExplanations);
        explainButton.setEnabled(!loading);
    }
}
