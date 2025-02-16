package com.therohitjagan.toolify.alltools.conversion;

import android.media.AudioManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;
import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.content.ClipboardManager;
import android.content.ClipData;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.therohitjagan.toolify.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class SpeechActivity extends AppCompatActivity {

    private TextToSpeech textToSpeech;
    private EditText editText;
    private MaterialButton btnSpeak;
    FloatingActionButton btnCopy, btnDelete;
    private Spinner spinnerLanguages;
    private SeekBar seekBarSpeed, seekBarPitch, seekBarVolume;
    private AudioManager audioManager;
    private float pitch = 1.0f;
    private float speed = 1.0f;

    //private ViewGroup bannerContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech);

        initializeViews();
        setupLanguageSpinner();
        setupTextToSpeech();
        setupSeekBars();
        setupButtons();

    }

    private void initializeViews() {
        editText = findViewById(R.id.editText);
        btnSpeak = findViewById(R.id.btnSpeak);
        btnCopy = findViewById(R.id.btnCopy);
        btnDelete = findViewById(R.id.btnDelete);
        spinnerLanguages = findViewById(R.id.spinnerLanguages);
        seekBarSpeed = findViewById(R.id.seekBarSpeed);
        seekBarPitch = findViewById(R.id.seekBarPitch);
        seekBarVolume = findViewById(R.id.seekBarVolume);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }

    private void setupLanguageSpinner() {
        ArrayList<String> languageList = new ArrayList<>(Arrays.asList(
                "English (US)",
                "English (UK)",
                "English (India)"
        ));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                languageList
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLanguages.setAdapter(adapter);

        spinnerLanguages.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedLanguage = languageList.get(position);
                switch (selectedLanguage) {
                    case "English (India)":
                        textToSpeech.setLanguage(new Locale("en", "IN"));
                        break;
                    case "English (US)":
                        textToSpeech.setLanguage(Locale.US);
                        break;
                    case "English (UK)":
                        textToSpeech.setLanguage(Locale.UK);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                textToSpeech.setLanguage(Locale.US);
            }
        });
    }

    private void setupTextToSpeech() {
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.US);
                spinnerLanguages.setSelection(0);
                btnSpeak.setEnabled(true);
            } else {
                Toast.makeText(SpeechActivity.this, "TTS Initialization failed", Toast.LENGTH_SHORT).show();
                btnSpeak.setEnabled(false);
            }
        });
    }

    private void setupSeekBars() {
        // Speed control (0.5x to 2.0x)
        seekBarSpeed.setProgress(50); // Default 1.0x
        seekBarSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                speed = 0.5f + ((float) progress / 100) * 1.5f; // Range from 0.5 to 2.0
                textToSpeech.setSpeechRate(speed);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Pitch control (0.5x to 2.0x)
        seekBarPitch.setProgress(50); // Default 1.0x
        seekBarPitch.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                pitch = 0.5f + ((float) progress / 100) * 1.5f; // Range from 0.5 to 2.0
                textToSpeech.setPitch(pitch);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Volume control
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        seekBarVolume.setMax(maxVolume);
        seekBarVolume.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void setupButtons() {
        btnSpeak.setOnClickListener(v -> speak());

        btnCopy.setOnClickListener(v -> {
            String text = editText.getText().toString();
            if (!TextUtils.isEmpty(text)) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("speech_text", text);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, "Text copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });

        btnDelete.setOnClickListener(v -> {
            editText.setText("");
            if (textToSpeech.isSpeaking()) {
                textToSpeech.stop();
            }
        });
    }

    private void speak() {

            String text = editText.getText().toString();
            if (TextUtils.isEmpty(text)) {
                Toast.makeText(this, "Please enter some text", Toast.LENGTH_SHORT).show();
                return;
            }

            if (textToSpeech.isSpeaking()) {
                textToSpeech.stop();
            }

            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);

    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}