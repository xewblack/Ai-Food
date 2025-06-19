package com.example.aifoodapplication;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Registration extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        editTextEmail = findViewById(R.id.editTextMailReg);
        editTextPassword = findViewById(R.id.editTextYourPassword);

        Button buttonRegister = findViewById(R.id.register_button);
        buttonRegister.setOnClickListener(view -> {
            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();

            validateAndPerformRegistration(email, password);
        });
    }

    private void validateAndPerformRegistration(final String email, final String password) {
        // Простая валидация E-mail и пароля
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Введите Email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Введите пароль", Toast.LENGTH_SHORT).show();
            return;
        }

        // Проверка соответствия E-mail регулярного выражения
        final Pattern VALID_EMAIL_ADDRESS_REGEX =
                Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        if (!matcher.find()) {
            Toast.makeText(this, "Введен некорректный E-mail", Toast.LENGTH_SHORT).show();
            return;
        }

        // Запускаем регистрацию в новом потоке
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SupabaseClient client = new SupabaseClient();
                    boolean success = client.signUp(email, password);

                    if (success) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Registration.this, "Регистрация прошла успешно", Toast.LENGTH_SHORT).show();

                                // Перенаправляемся на следующий экран
                                Intent intent = new Intent(Registration.this, MainActivity.class);
                                startActivity(intent);
                                finish(); // Закрываем текущую активность
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Registration.this, "Ошибка регистрации", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Registration.this, "Ошибка сети: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    public void signIn(View view) {
        Intent intent = new Intent(Registration.this, Login.class);
        startActivity(intent);
    }
}