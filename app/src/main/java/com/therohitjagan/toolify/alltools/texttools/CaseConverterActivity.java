package com.therohitjagan.toolify.alltools.texttools;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.therohitjagan.toolify.R;

public class CaseConverterActivity extends AppCompatActivity {
    private TextInputEditText inputText;
    private TextInputLayout inputLayout;
    private Button btnUppercase, btnLowercase, btnSentenceCase, btnTitleCase, btnCopy;
    private ViewGroup bannerContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_case_converter);

        inputLayout = findViewById(R.id.inputLayout);
        inputText = findViewById(R.id.inputText);
        btnUppercase = findViewById(R.id.btnUppercase);
        btnLowercase = findViewById(R.id.btnLowercase);
        btnSentenceCase = findViewById(R.id.btnSentenceCase);
        btnTitleCase = findViewById(R.id.btnTitleCase);
        btnCopy = findViewById(R.id.btnCopy);

        btnUppercase.setOnClickListener(v -> convertText("UPPER"));
        btnLowercase.setOnClickListener(v -> convertText("LOWER"));
        btnSentenceCase.setOnClickListener(v -> convertText("SENTENCE"));
        btnTitleCase.setOnClickListener(v -> convertText("TITLE"));
        btnCopy.setOnClickListener(v -> copyToClipboard());

    }

    private void convertText(String mode) {
        String text = inputText.getText().toString();


            if (text.isEmpty()) {
                Toast.makeText(this, "Please enter text", Toast.LENGTH_SHORT).show();
                return;
            }
        //String result = "";

            String result = "";

            switch (mode) {
                case "UPPER":
                    result = text.toUpperCase();
                    break;
                case "LOWER":
                    result = text.toLowerCase();
                    break;
                case "SENTENCE":
                    result = text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
                    break;
                case "TITLE":
                    StringBuilder titleCase = new StringBuilder();
                    for (String word : text.split(" ")) {
                        if (word.length() > 1) {
                            titleCase.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1).toLowerCase()).append(" ");
                        } else {
                            titleCase.append(word.toUpperCase()).append(" ");
                        }
                    }
                    result = titleCase.toString().trim();
                    break;
            }

            inputText.setText(result);

    }

    private void copyToClipboard() {
        String text = inputText.getText().toString();
        if (text.isEmpty()) {
            Toast.makeText(this, "Nothing to copy", Toast.LENGTH_SHORT).show();
            return;
        }
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Converted Text", text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
    }
}
