package com.example.llm_enhancedlearningassistantapp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LlmApiClient {
    public interface LlmCallback {
        void onSuccess(String responseText);
        void onError(String errorMessage);
    }

    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";
    private static final String MODEL = "gpt-4o-mini";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();

    public void sendPrompt(String apiKey, String prompt, LlmCallback callback) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            callback.onError("Missing API key. Please enter your API key on the Home screen.");
            return;
        }

        try {
            JSONObject root = new JSONObject();
            root.put("model", MODEL);

            JSONArray messages = new JSONArray();
            messages.put(new JSONObject()
                    .put("role", "system")
                    .put("content", "You are a helpful learning assistant for beginner students. Keep answers short and clear."));
            messages.put(new JSONObject()
                    .put("role", "user")
                    .put("content", prompt));

            root.put("messages", messages);
            root.put("temperature", 0.7);

            RequestBody body = RequestBody.create(root.toString(), JSON);
            Request request = new Request.Builder()
                    .url(OPENAI_URL)
                    .addHeader("Authorization", "Bearer " + apiKey.trim())
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    callback.onError("Network error: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String bodyText = response.body() != null ? response.body().string() : "";
                    if (!response.isSuccessful()) {
                        callback.onError("API error (" + response.code() + "): " + extractError(bodyText));
                        return;
                    }

                    try {
                        JSONObject obj = new JSONObject(bodyText);
                        JSONArray choices = obj.getJSONArray("choices");
                        String content = choices
                                .getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content");
                        callback.onSuccess(content.trim());
                    } catch (Exception ex) {
                        callback.onError("Failed to parse response: " + ex.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            callback.onError("Request build failed: " + e.getMessage());
        }
    }

    private String extractError(String bodyText) {
        try {
            JSONObject obj = new JSONObject(bodyText);
            return obj.getJSONObject("error").getString("message");
        } catch (Exception ignored) {
            return bodyText.isEmpty() ? "Unknown server error." : bodyText;
        }
    }
}
