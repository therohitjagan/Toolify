package com.therohitjagan.toolify.alltools.conversion;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.therohitjagan.toolify.R;

import java.util.ArrayList;
import java.util.Locale;

public class TextSpeech extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 123;
    private SpeechRecognizer speechRecognizer;
    private TextToSpeech textToSpeech;
    private TextView transcribedText, recordingTime;
    private LinearProgressIndicator waveformProgress;
    private ImageButton microphoneButton;
    private boolean isRecording = false;

    private ViewGroup bannerContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_speech);

        // Initialize UI components
        transcribedText = findViewById(R.id.transcribedText);
        waveformProgress = findViewById(R.id.waveformProgress);
        microphoneButton = findViewById(R.id.microphoneButton);
        //recordingTime = findViewById(R.id.recordingTime);

        // Check if speech recognition is available
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Toast.makeText(this, "Speech recognition is not available on this device",
                    Toast.LENGTH_LONG).show();
            microphoneButton.setEnabled(false);
            return;
        }

        // Initialize Speech Recognizer
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new SpeechRecognitionListener());

        // Set microphone button onClickListener
        microphoneButton.setOnClickListener(view -> {
            if (checkPermission()) {
                if (isRecording) {
                    stopRecording();
                } else {
                    startRecording();
                }
            } else {
                requestPermission();
            }
        });

        // Initialize TextToSpeech
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int langResult = textToSpeech.setLanguage(Locale.US);
                if (langResult == TextToSpeech.LANG_MISSING_DATA ||
                        langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(TextSpeech.this,
                            "Language not supported",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(TextSpeech.this,
                        "TextToSpeech initialization failed",
                        Toast.LENGTH_SHORT).show();
            }
        });


    }

    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECORD_AUDIO},
                PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                startRecording();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startRecording() {
        isRecording = true;
        waveformProgress.setVisibility(View.VISIBLE);
        waveformProgress.setIndeterminate(true);
        microphoneButton.setImageResource(R.drawable.ic_stop);

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);

        try {
            speechRecognizer.startListening(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Error starting speech recognition",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void stopRecording() {
        isRecording = false;
        waveformProgress.setVisibility(View.GONE);
        microphoneButton.setImageResource(R.drawable.ic_mic);
        speechRecognizer.stopListening();
    }

    private class SpeechRecognitionListener implements android.speech.RecognitionListener {
        @Override
        public void onResults(Bundle results) {
            ArrayList<String> matches = results
                    .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            if (matches != null && !matches.isEmpty()) {
                String text = matches.get(0);
                transcribedText.setText(text);
            }
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
            ArrayList<String> matches = partialResults
                    .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            if (matches != null && !matches.isEmpty()) {
                String text = matches.get(0);
                transcribedText.setText(text);
            }
        }

        @Override
        public void onError(int error) {
            String errorMessage;
            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                    errorMessage = "Audio recording error";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    errorMessage = "Client side error";
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    errorMessage = "Insufficient permissions";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    errorMessage = "Network error";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    errorMessage = "Network timeout";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    errorMessage = "No match found";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    errorMessage = "RecognitionService busy";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    errorMessage = "Server error";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    errorMessage = "No speech input";
                    break;
                default:
                    errorMessage = "Unknown error occurred";
                    break;
            }
            Toast.makeText(TextSpeech.this, errorMessage, Toast.LENGTH_SHORT).show();
            stopRecording();
        }

        @Override
        public void onReadyForSpeech(Bundle params) {
            Toast.makeText(TextSpeech.this, "Listening...", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onBeginningOfSpeech() {
            waveformProgress.setIndeterminate(false);
            waveformProgress.setProgress(0);
        }

        @Override
        public void onRmsChanged(float rmsdB) {
            int progress = (int) (rmsdB * 10);
            waveformProgress.setProgress(progress);
        }

        @Override
        public void onBufferReceived(byte[] buffer) {}

        @Override
        public void onEndOfSpeech() {
            stopRecording();
        }

        @Override
        public void onEvent(int eventType, Bundle params) {}
    }

    @Override
    protected void onDestroy() {
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}