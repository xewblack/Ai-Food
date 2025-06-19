package com.example.aifoodapplication;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SupabaseClient {

    private final String BASE_URL = "https://nurmzwblcjnejlaeyoxb.supabase.co";
    private final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im51cm16d2JsY2puZWpsYWV5b3hiIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDkxMTE0MTEsImV4cCI6MjA2NDY4NzQxMX0.99X5ARjWIJMZ5Gc3y9pv-3Jwr5iLSisYo6sTR4DeoKs";
    private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient httpClient = new OkHttpClient();

    public boolean signUp(String email, String password) throws IOException {
        Map<String, Object> jsonBody = new HashMap<>();
        jsonBody.put("email", email);
        jsonBody.put("password", password);

        Request request = new Request.Builder()
                .url(BASE_URL + "/auth/v1/signup")
                .post(RequestBody.create(new Gson().toJson(jsonBody), JSON))
                .addHeader("apikey", API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

        Response response = httpClient.newCall(request).execute();
        int code = response.code();

        if (code >= 200 && code < 300) {
            return true;
        } else {
            String errorMessage = "";
            try {
                errorMessage = response.body().string();
            } catch (Exception ignored) {}

            throw new IOException("Сервер вернул ошибку с кодом: " + code +
                    "\nСообщение об ошибке: " + errorMessage);
        }
    }

    public String login(String email, String password) throws IOException {
        Map<String, Object> jsonBody = new HashMap<>();
        jsonBody.put("email", email);
        jsonBody.put("password", password);

        Request request = new Request.Builder()
                .url(BASE_URL + "/auth/v1/token?grant_type=password")
                .post(RequestBody.create(jsonBody.toString(), JSON))
                .addHeader("apikey", API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

        Response response = httpClient.newCall(request).execute();
        int code = response.code();

        if (response.isSuccessful()) {
            JsonObject obj = new Gson().fromJson(response.body().string(), JsonObject.class);
            return obj.getAsJsonPrimitive("access_token").getAsString();
        } else {
            throw new IOException("Ошибка входа с кодом ответа сервера: " + code);
        }
    }
}