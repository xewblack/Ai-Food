package com.example.aifoodapplication;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.*;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class SupabaseClient {

    private final String BASE_URL = "https://nurmzwblcjnejlaeyoxb.supabase.co";
    private final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im51cm16d2JsY2puZWpsYWV5b3hiIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDkxMTE0MTEsImV4cCI6MjA2NDY4NzQxMX0.99X5ARjWIJMZ5Gc3y9pv-3Jwr5iLSisYo6sTR4DeoKs";
    private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient httpClient = new OkHttpClient();

    // Регистрация
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

    // Вход
    public String login(String email, String password) throws IOException {
        Map<String, Object> jsonBody = new HashMap<>();
        jsonBody.put("email", email);
        jsonBody.put("password", password);

        Request request = new Request.Builder()
                .url(BASE_URL + "/auth/v1/token?grant_type=password")
                .post(RequestBody.create(new Gson().toJson(jsonBody), JSON))
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

    private String accessToken; // Храните текущий токен пользователя

    // Метод для установки токена после входа
    public void setAccessToken(String token) {
        this.accessToken = token;
    }

    // Проверка авторизации
    public boolean isUserAuthorized() {
        return accessToken != null && !accessToken.isEmpty();
    }
    // Создать ПИН-код и сохранить его (например, в базе)
    public boolean createPinCode(String userId, String pin) throws IOException {
        if (!isUserAuthorized()) {
            throw new SecurityException("Пользователь не авторизован");
        }
        String hashedPin = hashPin(pin);
        Map<String, Object> jsonBody = new HashMap<>();
        jsonBody.put("user_id", userId);
        jsonBody.put("pin_hash", hashedPin);

        Request request = new Request.Builder()
                .url(BASE_URL + "/rest/v1/user_pins")
                .post(RequestBody.create(new Gson().toJson(jsonBody), JSON))
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("Content-Type", "application/json")
                .build();

        Response response = httpClient.newCall(request).execute();
        return response.isSuccessful();
    }

    // Вход по ПИН-коду
    public boolean loginWithPin(String userId, String inputPin) throws IOException {
        // Получить хеш ПИНа из базы по userId
        String storedHash = getPinHashFromDatabase(userId);
        if (storedHash == null) {
            return false;
        }
        String inputHash = hashPin(inputPin);
        return inputHash.equals(storedHash);
    }

    // Хеширование ПИН-кода (SHA-256)
    private String hashPin(String pin) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(pin.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // Получение хеша ПИНа из базы по userId (пример, реализуйте через API)
    private String getPinHashFromDatabase(String userId) throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + "/rest/v1/user_pins?user_id=eq." + userId)
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + getAdminToken())
                .build();

        Response response = httpClient.newCall(request).execute();

        if (response.isSuccessful()) {
            String body = response.body().string();
            // Парсите JSON, чтобы получить pin_hash
            // Здесь — пример, замените на свой парсинг
            JsonObject json = new Gson().fromJson(body, JsonObject.class);
            if (json.has("pin_hash")) {
                return json.get("pin_hash").getAsString();
            }
        }
        return null;
    }

    // Получить токен администратора или сервисного аккаунта для доступа к API
    private String getAdminToken() {
        return "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im51cm16d2JsY2puZWpsYWV5b3hiIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDkxMTE0MTEsImV4cCI6MjA2NDY4NzQxMX0.99X5ARjWIJMZ5Gc3y9pv-3Jwr5iLSisYo6sTR4DeoKs";
    }


}