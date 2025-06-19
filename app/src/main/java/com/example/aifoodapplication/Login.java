package com.example.aifoodapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Login extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextEmail = findViewById(R.id.editTextMailLogin);
        editTextPassword = findViewById(R.id.editTextYourPassword);

        Button buttonLogin = findViewById(R.id.login_button);
        buttonLogin.setOnClickListener(view -> {
            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();

            validateAndPerformLogin(email, password);
        });
    }

    private void validateAndPerformLogin(final String email, final String password) {
        // Базовая валидация полей
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Введите Email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Введите пароль", Toast.LENGTH_SHORT).show();
            return;
        }

        // Регулярное выражение для проверки корректности Email
        final Pattern VALID_EMAIL_ADDRESS_REGEX =
                Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        if (!matcher.find()) {
            Toast.makeText(this, "Введен некорректный Email", Toast.LENGTH_SHORT).show();
            return;
        }

        // Начинаем процесс авторизации в фоне
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SupabaseClient client = new SupabaseClient();
                    String accessToken = client.login(email, password);

                    if (accessToken != null && !accessToken.isEmpty()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Login.this, "Авторизация прошла успешно", Toast.LENGTH_SHORT).show();

                                // Переход на следующий экран
                                Intent intent = new Intent(Login.this, MainActivity.class);
                                startActivity(intent);
                                finish(); // Закрываем текущую активность
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Login.this, "Ошибка авторизации", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Login.this, "Ошибка сети: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }
    public void register(View view) {
        Intent intent = new Intent(Login.this, Registration.class);
        startActivity(intent);
    }
}