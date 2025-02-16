package com.therohitjagan.toolify.alltools.texttools;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.therohitjagan.toolify.R;

public class WordCharacterCountActivity extends AppCompatActivity {
    private TextInputEditText inputText;
    private TextInputLayout inputLayout;
    private TextView wordCountText, charCountText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_character_count);

        inputLayout = findViewById(R.id.inputLayout);
        inputText = findViewById(R.id.inputText);
        wordCountText = findViewById(R.id.wordCountText);
        charCountText = findViewById(R.id.charCountText);

        inputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateCount(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void updateCount(String text) {
        int charCount = text.length();
        int wordCount = text.trim().isEmpty() ? 0 : text.trim().split("\\s+").length;

        charCountText.setText("Characters: " + charCount);
        wordCountText.setText("Words: " + wordCount);
    }
}
