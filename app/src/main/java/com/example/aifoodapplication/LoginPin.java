package com.example.aifoodapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginPin extends AppCompatActivity {

    private EditText pinInput;
    private Button loginButton;
    private String userId; // Идентификатор пользователя, который входит
    private SupabaseClient apiClient; // ваш API клиент

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_pin);

        // Инициализация API клиента
        apiClient = new SupabaseClient();

        // Получение userId из Intent или другого источника
        userId = getIntent().getStringExtra("user_id");
        if (userId == null) {
            Toast.makeText(this, "Пользователь не определён", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        pinInput = findViewById(R.id.editPinCodeLogin);
        loginButton = findViewById(R.id.buttonNextPin);

        // Установка отступов для системных панелей
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loginButton.setOnClickListener(v -> {
            String pin = pinInput.getText().toString().trim();

            if (pin.length() < 4) {
                Toast.makeText(this, "ПИН должен быть длиной не менее 4 символов", Toast.LENGTH_SHORT).show();
                return;
            }

            // Проверка PIN-кода в отдельном потоке
            new Thread(() -> {
                try {
                    boolean isValid = apiClient.loginWithPin(userId, pin);
                    runOnUiThread(() -> {
                        if (isValid) {
                            Toast.makeText(LoginPin.this, "Вход выполнен успешно", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(LoginPin.this, "Неверный PIN-код", Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (Exception e) {
                    runOnUiThread(() -> Toast.makeText(LoginPin.this, "Ошибка: " + e.getMessage(), Toast.LENGTH_LONG).show());
                }
            }).start();
        });
    }
}