package com.therohitjagan.toolify.alltools.texttools;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.therohitjagan.toolify.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexTesterActivity extends AppCompatActivity {
    private TextInputEditText inputText, regexPattern;
    private MaterialButton btnTestRegex;
    private TextView resultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regex_tester);

        regexPattern = findViewById(R.id.regexPattern);
        inputText = findViewById(R.id.inputText);
        btnTestRegex = findViewById(R.id.btnTestRegex);
        resultText = findViewById(R.id.resultText);

        btnTestRegex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testRegex();
            }
        });
    }

    private void testRegex() {
        String patternStr = regexPattern.getText().toString();
        String inputStr = inputText.getText().toString();

        if (patternStr.isEmpty() || inputStr.isEmpty()) {
            resultText.setText("Please enter both regex pattern and input text.");
            return;
        }

        try {
            Pattern pattern = Pattern.compile(patternStr);
            Matcher matcher = pattern.matcher(inputStr);
            StringBuilder matches = new StringBuilder();

            while (matcher.find()) {
                matches.append("Match found: ").append(matcher.group()).append("\n");
            }

            if (matches.length() > 0) {
                resultText.setText(matches.toString());
            } else {
                resultText.setText("No matches found.");
            }
        } catch (Exception e) {
            resultText.setText("Invalid regex pattern.");
        }
    }
}
