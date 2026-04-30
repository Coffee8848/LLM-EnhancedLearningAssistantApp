package com.example.llm_enhancedlearningassistantapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private SessionManager sessionManager;
    private LlmApiClient llmApiClient;

    private EditText flashcardTopicInput;
    private TextView flashcardsErrorText;
    private TextView historySummaryText;
    private TextView promptText;
    private TextView responseText;
    private ProgressBar flashcardProgress;
    private LinearLayout studyPlanContainer;
    private TextView day1Text;
    private TextView day2Text;
    private TextView day3Text;
    private TextView day4Text;
    private TextView day5Text;
    private TextView day6Text;
    private TextView day7Text;

    private TextView card1Question;
    private TextView card1Answer;
    private TextView card2Question;
    private TextView card2Answer;
    private TextView card3Question;
    private TextView card3Answer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sessionManager = new SessionManager(this);
        llmApiClient = new LlmApiClient();

        TextView helloText = findViewById(R.id.tvHelloName);
        TextView interestsText = findViewById(R.id.tvInterestsSummary);
        Button openTaskButton = findViewById(R.id.btnOpenTask);
        Button logoutButton = findViewById(R.id.btnLogout);
        Button generateFlashcardsButton = findViewById(R.id.btnGenerateFlashcards);
        Button generateStudyPlanButton = findViewById(R.id.btnGenerateStudyPlan);

        flashcardTopicInput = findViewById(R.id.etFlashcardTopic);
        flashcardsErrorText = findViewById(R.id.tvFlashcardsError);
        historySummaryText = findViewById(R.id.tvHistorySummary);
        promptText = findViewById(R.id.tvHomePrompt);
        responseText = findViewById(R.id.tvHomeResponse);
        flashcardProgress = findViewById(R.id.pbFlashcardLoading);
        studyPlanContainer = findViewById(R.id.layoutStudyPlanContainer);
        day1Text = findViewById(R.id.tvPlanDay1);
        day2Text = findViewById(R.id.tvPlanDay2);
        day3Text = findViewById(R.id.tvPlanDay3);
        day4Text = findViewById(R.id.tvPlanDay4);
        day5Text = findViewById(R.id.tvPlanDay5);
        day6Text = findViewById(R.id.tvPlanDay6);
        day7Text = findViewById(R.id.tvPlanDay7);

        card1Question = findViewById(R.id.tvCard1Question);
        card1Answer = findViewById(R.id.tvCard1Answer);
        card2Question = findViewById(R.id.tvCard2Question);
        card2Answer = findViewById(R.id.tvCard2Answer);
        card3Question = findViewById(R.id.tvCard3Question);
        card3Answer = findViewById(R.id.tvCard3Answer);

        String username = sessionManager.getUsername();
        String interests = sessionManager.getInterests();
        String historySummary = sessionManager.getLearningHistorySummary();
        helloText.setText("Hello, " + username);
        interestsText.setText("Interests: " + interests);
        historySummaryText.setText("History: " + historySummary);
        promptText.setText("Prompt:");
        responseText.setText("Response:");
        studyPlanContainer.setVisibility(View.GONE);

        openTaskButton.setOnClickListener(v ->
                startActivity(new Intent(this, QuizActivity.class))
        );

        logoutButton.setOnClickListener(v -> {
            sessionManager.logout();
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        generateFlashcardsButton.setOnClickListener(v -> generateFlashcards());
        generateStudyPlanButton.setOnClickListener(v -> generateStudyPlan());
    }

    @Override
    protected void onResume() {
        super.onResume();
        historySummaryText.setText("History: " + sessionManager.getLearningHistorySummary());
    }

    private void generateFlashcards() {
        String topic = flashcardTopicInput.getText().toString().trim();
        if (topic.isEmpty()) {
            Toast.makeText(this, "Please enter a topic.", Toast.LENGTH_SHORT).show();
            return;
        }

        String prompt = "Create exactly 3 beginner-friendly flashcards for topic: " + topic
                + ". Student interests: " + sessionManager.getInterests()
                + ". Return ONLY valid JSON array with 3 objects. "
                + "Each object keys: question, answer.";

        setLoadingState(true, "cards");
        flashcardsErrorText.setText("");
        setFlashcardsPlaceholder();
        promptText.setText("Prompt:\n" + prompt);
        responseText.setText("Response:\n");

        llmApiClient.sendPrompt(sessionManager.getApiKey(), prompt, new LlmApiClient.LlmCallback() {
            @Override
            public void onSuccess(String responseText) {
                runOnUiThread(() -> {
                    setLoadingState(false, "");
                    showFlashcards(parseFlashcards(responseText));
                    HomeActivity.this.responseText.setText("Response:\n" + responseText);
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    setLoadingState(false, "");
                    flashcardsErrorText.setText("Failed: " + errorMessage);
                    HomeActivity.this.responseText.setText("Response:\nFailed: " + errorMessage);
                });
            }
        });
    }

    private void generateStudyPlan() {
        String history = sessionManager.getLearningHistorySummary();
        String prompt = "Suggest a study plan for next 7 days based on this history: " + history
                + ". Interests: " + sessionManager.getInterests()
                + ". Return ONLY valid JSON array with 7 objects."
                + " Each object keys: day, focus, task."
                + " day values must be Day 1..Day 7."
                + " No markdown, no extra text.";

        setLoadingState(true, "plan");
        flashcardsErrorText.setText("");
        studyPlanContainer.setVisibility(View.GONE);
        promptText.setText("Prompt:\n" + prompt);
        responseText.setText("Response:\n");

        llmApiClient.sendPrompt(sessionManager.getApiKey(), prompt, new LlmApiClient.LlmCallback() {
            @Override
            public void onSuccess(String response) {
                runOnUiThread(() -> {
                    setLoadingState(false, "");
                    showStudyPlan(parseStudyPlan(response));
                    responseText.setText("Response:\n" + response);
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    setLoadingState(false, "");
                    flashcardsErrorText.setText("Failed: " + errorMessage);
                    studyPlanContainer.setVisibility(View.VISIBLE);
                    setDefaultPlanPlaceholders("Failed to generate plan.");
                    responseText.setText("Response:\nFailed: " + errorMessage);
                });
            }
        });
    }

    private List<StudyDay> parseStudyPlan(String responseText) {
        List<StudyDay> days = new ArrayList<>();
        try {
            String cleaned = responseText.trim();
            cleaned = cleaned.replace("```json", "").replace("```", "").trim();
            int firstArray = cleaned.indexOf('[');
            int lastArray = cleaned.lastIndexOf(']');
            if (firstArray >= 0 && lastArray > firstArray) {
                cleaned = cleaned.substring(firstArray, lastArray + 1);
            }

            JSONArray array = new JSONArray(cleaned);
            for (int i = 0; i < array.length() && days.size() < 7; i++) {
                JSONObject item = array.getJSONObject(i);
                String day = item.optString("day", "Day " + (i + 1)).trim();
                String focus = item.optString("focus", "").trim();
                String task = item.optString("task", "").trim();
                days.add(new StudyDay(day, focus, task));
            }
        } catch (Exception ignored) {
        }

        while (days.size() < 7) {
            int d = days.size() + 1;
            days.add(new StudyDay("Day " + d, "Not generated", "Try again."));
        }
        return days;
    }

    private void showStudyPlan(List<StudyDay> days) {
        studyPlanContainer.setVisibility(View.VISIBLE);
        day1Text.setText(formatDay(days.get(0)));
        day2Text.setText(formatDay(days.get(1)));
        day3Text.setText(formatDay(days.get(2)));
        day4Text.setText(formatDay(days.get(3)));
        day5Text.setText(formatDay(days.get(4)));
        day6Text.setText(formatDay(days.get(5)));
        day7Text.setText(formatDay(days.get(6)));
    }

    private String formatDay(StudyDay day) {
        return day.day + "\nFocus: " + day.focus + "\nTask: " + day.task;
    }

    private void setDefaultPlanPlaceholders(String text) {
        day1Text.setText("Day 1\n" + text);
        day2Text.setText("Day 2\n" + text);
        day3Text.setText("Day 3\n" + text);
        day4Text.setText("Day 4\n" + text);
        day5Text.setText("Day 5\n" + text);
        day6Text.setText("Day 6\n" + text);
        day7Text.setText("Day 7\n" + text);
    }

    private List<Flashcard> parseFlashcards(String responseText) {
        List<Flashcard> cards = new ArrayList<>();
        try {
            String cleaned = responseText.trim();
            cleaned = cleaned.replace("```json", "").replace("```", "").trim();
            int firstArray = cleaned.indexOf('[');
            int lastArray = cleaned.lastIndexOf(']');
            if (firstArray >= 0 && lastArray > firstArray) {
                cleaned = cleaned.substring(firstArray, lastArray + 1);
            }

            JSONArray array = new JSONArray(cleaned);
            for (int i = 0; i < array.length() && cards.size() < 3; i++) {
                JSONObject item = array.getJSONObject(i);
                String q = item.optString("question", "").trim();
                String a = item.optString("answer", "").trim();
                if (!q.isEmpty() || !a.isEmpty()) {
                    cards.add(new Flashcard(q, a));
                }
            }
        } catch (Exception ignored) {
        }

        if (cards.isEmpty()) {
            cards.add(new Flashcard("Could not parse structured cards.", responseText.trim()));
        }
        while (cards.size() < 3) {
            cards.add(new Flashcard("Not generated", "Try again."));
        }
        return cards;
    }

    private void showFlashcards(List<Flashcard> cards) {
        card1Question.setText("Q1: " + cards.get(0).question);
        card1Answer.setText("A1: " + cards.get(0).answer);
        card2Question.setText("Q2: " + cards.get(1).question);
        card2Answer.setText("A2: " + cards.get(1).answer);
        card3Question.setText("Q3: " + cards.get(2).question);
        card3Answer.setText("A3: " + cards.get(2).answer);
    }

    private void setFlashcardsPlaceholder() {
        card1Question.setText("Q1: Loading...");
        card1Answer.setText("A1:");
        card2Question.setText("Q2: Loading...");
        card2Answer.setText("A2:");
        card3Question.setText("Q3: Loading...");
        card3Answer.setText("A3:");
    }

    private void setLoadingState(boolean loading, String type) {
        flashcardProgress.setVisibility(loading ? View.VISIBLE : View.GONE);
        Button generateButton = findViewById(R.id.btnGenerateFlashcards);
        Button planButton = findViewById(R.id.btnGenerateStudyPlan);
        if (loading) {
            generateButton.setEnabled(false);
            planButton.setEnabled(false);
            if ("cards".equals(type)) {
                generateButton.setText("Generating...");
            } else {
                generateButton.setText("Generate Cards");
            }
            if ("plan".equals(type)) {
                planButton.setText("Generating...");
            } else {
                planButton.setText("Generate 7-Day Plan");
            }
        } else {
            generateButton.setEnabled(true);
            planButton.setEnabled(true);
            generateButton.setText("Generate Cards");
            planButton.setText("Generate 7-Day Plan");
        }
    }

    private static class Flashcard {
        final String question;
        final String answer;

        Flashcard(String question, String answer) {
            this.question = question;
            this.answer = answer;
        }
    }

    private static class StudyDay {
        final String day;
        final String focus;
        final String task;

        StudyDay(String day, String focus, String task) {
            this.day = day;
            this.focus = focus;
            this.task = task;
        }
    }
}
