package com.therohitjagan.toolify.alltools.texttools;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.therohitjagan.toolify.R;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class TextSummarizationActivity extends AppCompatActivity {
    private TextInputEditText inputText;
    private TextInputLayout inputLayout;
    private MaterialButton btnSummarize;
    private TextView summaryText;

    private ViewGroup bannerContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_summarization);

        inputLayout = findViewById(R.id.inputLayout);
        inputText = findViewById(R.id.inputText);
        btnSummarize = findViewById(R.id.btnSummarize);
        summaryText = findViewById(R.id.summaryText);

        btnSummarize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = inputText.getText().toString();
                if (!text.isEmpty()) {
                    summaryText.setText(summarizeText(text));
                }
            }
        });


    }

    private String summarizeText(String text) {
        String[] sentences = text.split("(?<!\\d)\\.\\s*");
        Map<String, Integer> wordFrequencies = new HashMap<>();

        for (String sentence : sentences) {
            String[] words = sentence.toLowerCase().split("\\W+");
            for (String word : words) {
                wordFrequencies.put(word, wordFrequencies.getOrDefault(word, 0) + 1);
            }
        }

        PriorityQueue<String> sentenceQueue = new PriorityQueue<>(Comparator.comparingInt(s -> -calculateSentenceScore(s, wordFrequencies)));
        sentenceQueue.addAll(Arrays.asList(sentences));

        StringBuilder summary = new StringBuilder();
        int sentenceCount = Math.max(1, sentences.length / 3);
        for (int i = 0; i < sentenceCount; i++) {
            summary.append(sentenceQueue.poll()).append(". ");
        }

        return summary.toString().trim();
    }

    private int calculateSentenceScore(String sentence, Map<String, Integer> wordFrequencies) {
        int score = 0;
        for (String word : sentence.toLowerCase().split("\\W+")) {
            score += wordFrequencies.getOrDefault(word, 0);
        }
        return score;
    }
}
