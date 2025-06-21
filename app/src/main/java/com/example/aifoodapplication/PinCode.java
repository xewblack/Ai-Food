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

public class PinCode extends AppCompatActivity {

    private EditText pinInput;
    private Button saveButton;
    private String userId; // ID пользователя, для которого создается PIN
    private SupabaseClient apiClient; // ваш API клиент

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pin_code);

        // Инициализация API клиента
        apiClient = new SupabaseClient();

        // Получение userId из Intent (или другого источника)
        userId = getIntent().getStringExtra("user_id");
        if (userId == null) {
            Toast.makeText(this, "Пользователь не определён", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        pinInput = findViewById(R.id.editPinCodeLogin);
        saveButton = findViewById(R.id.buttonSave);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        saveButton.setOnClickListener(v -> {
            String pin = pinInput.getText().toString().trim();

            if (pin.length() < 4) {
                Toast.makeText(this, "ПИН должен быть длиной не менее 4 символов", Toast.LENGTH_SHORT).show();
                return;
            }

            // Создаём PIN-код через API
            new Thread(() -> {
                try {
                    boolean success = apiClient.createPinCode(userId, pin);
                    runOnUiThread(() -> {
                        if (success) {
                            Toast.makeText(PinCode.this, "ПИН успешно сохранён", Toast.LENGTH_LONG).show();
                            // Можно завершить активность или перейти дальше
                            finish();
                        } else {
                            Toast.makeText(PinCode.this, "Ошибка при сохранении PIN", Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (Exception e) {
                    runOnUiThread(() -> Toast.makeText(PinCode.this, "Ошибка: " + e.getMessage(), Toast.LENGTH_LONG).show());
                }
            }).start();
        });
    }
}