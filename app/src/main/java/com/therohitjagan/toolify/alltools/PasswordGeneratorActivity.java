package com.therohitjagan.toolify.alltools;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.therohitjagan.toolify.R;

import java.security.SecureRandom;

public class PasswordGeneratorActivity extends AppCompatActivity {

    private EditText passwordLengthEditText, generatedPasswordEditText;
    private Button generateButton, copyButton;
    private CheckBox includeSymbolsCheckBox;
    private TextView strengthTextView;

    private ViewGroup bannerContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_generator);

        passwordLengthEditText = findViewById(R.id.passwordLengthEditText);
        generatedPasswordEditText = findViewById(R.id.generatedPasswordEditText);
        generateButton = findViewById(R.id.generateButton);
        copyButton = findViewById(R.id.copyButton);
        includeSymbolsCheckBox = findViewById(R.id.includeSymbolsCheckBox);
        strengthTextView = findViewById(R.id.strengthTextView);


        generateButton.setOnClickListener(v -> {

                String lengthString = passwordLengthEditText.getText().toString();
                if (!TextUtils.isEmpty(lengthString)) {
                    int length = Integer.parseInt(lengthString);
                    if (length >= 8) {
                        String password = generatePassword(length);
                        generatedPasswordEditText.setText(password);
                        setStrengthIndicator(password);
                    } else {
                        Toast.makeText(this, "Password length should be at least 8 characters.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Please enter a password length.", Toast.LENGTH_SHORT).show();
                }
            });

        copyButton.setOnClickListener(v -> {

                String password = generatedPasswordEditText.getText().toString();
                if (!TextUtils.isEmpty(password)) {
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("Password", password);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(this, "Password copied to clipboard", Toast.LENGTH_SHORT).show();
                }

            });


    }

    // Method to generate password
    private String generatePassword(int length) {
        String upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCase = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        String symbols = "!@#$%^&*()-_=+<>?";

        StringBuilder allCharacters = new StringBuilder(upperCase + lowerCase + numbers);
        if (includeSymbolsCheckBox.isChecked()) {
            allCharacters.append(symbols);
        }

        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(allCharacters.length());
            password.append(allCharacters.charAt(randomIndex));
        }

        return password.toString();
    }

    // Method to set password strength indicator
    private void setStrengthIndicator(String password) {
        int length = password.length();
        if (length >= 12 && password.matches(".*[A-Z].*") && password.matches(".*[a-z].*") && password.matches(".*[0-9].*") && password.matches(".*[!@#$%^&*()-_=+<>?].*")) {
            strengthTextView.setText("Strength: Strong");
            strengthTextView.setTextColor(getResources().getColor(R.color.green));
        } else if (length >= 8 && password.matches(".*[A-Za-z].*") && password.matches(".*[0-9].*")) {
            strengthTextView.setText("Strength: Medium");
            strengthTextView.setTextColor(getResources().getColor(R.color.orange));
        } else {
            strengthTextView.setText("Strength: Weak");
            strengthTextView.setTextColor(getResources().getColor(R.color.red));
        }
    }
}
