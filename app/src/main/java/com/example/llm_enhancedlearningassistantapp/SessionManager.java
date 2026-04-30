package com.example.llm_enhancedlearningassistantapp;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "learning_assistant_prefs";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_INTERESTS = "interests";
    private static final String KEY_LAST_SCORE = "last_score";
    private static final String KEY_LAST_TOTAL = "last_total";
    private static final String KEY_LAST_ANSWERS = "last_answers";

    private final SharedPreferences preferences;

    public SessionManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void setLoggedIn(boolean loggedIn) {
        preferences.edit().putBoolean(KEY_IS_LOGGED_IN, loggedIn).apply();
    }

    public void logout() {
        preferences.edit().putBoolean(KEY_IS_LOGGED_IN, false).apply();
    }

    public boolean isLoggedIn() {
        return preferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void setUsername(String username) {
        preferences.edit().putString(KEY_USERNAME, username).apply();
    }

    public String getUsername() {
        return preferences.getString(KEY_USERNAME, "Student");
    }

    public void setEmail(String email) {
        preferences.edit().putString(KEY_EMAIL, email).apply();
    }

    public String getEmail() {
        return preferences.getString(KEY_EMAIL, "");
    }

    public void setInterests(String interestsCsv) {
        preferences.edit().putString(KEY_INTERESTS, interestsCsv).apply();
    }

    public String getInterests() {
        return preferences.getString(KEY_INTERESTS, "Algorithms, Data Structures");
    }

    public void setLastQuizResult(int score, int total, String q1Answer, String q2Answer) {
        String answers = "Q1=" + q1Answer + ", Q2=" + q2Answer;
        preferences.edit()
                .putInt(KEY_LAST_SCORE, score)
                .putInt(KEY_LAST_TOTAL, total)
                .putString(KEY_LAST_ANSWERS, answers)
                .apply();
    }

    public String getLearningHistorySummary() {
        int score = preferences.getInt(KEY_LAST_SCORE, -1);
        int total = preferences.getInt(KEY_LAST_TOTAL, -1);
        String answers = preferences.getString(KEY_LAST_ANSWERS, "No previous quiz answers.");
        if (score < 0 || total < 0) {
            return "No quiz history yet.";
        }
        return "Last quiz score: " + score + "/" + total + ". " + answers;
    }

    public String getApiKey() {
        return BuildConfig.OPENAI_API_KEY;
    }
}
