package com.therohitjagan.toolify.alltools.barcodetool;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.therohitjagan.toolify.R;

public class BarcodeResult extends AppCompatActivity {
    private ViewGroup bannerContainer;

    public static final String EXTRA_SCANNED_DATA = "EXTRA_SCANNED_DATA";

    private TextView tvScannedData;
    private ImageButton btnResultCopy, btnResultShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_result);

        tvScannedData = findViewById(R.id.tv_scanned_data);
        btnResultCopy = findViewById(R.id.btn_result_copy);
        btnResultShare = findViewById(R.id.btn_result_share);

        String data = getIntent().getStringExtra(EXTRA_SCANNED_DATA);
        if (data != null) {
            tvScannedData.setText(data);
        }

        btnResultCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                copyToClipboard(data);
            }
        });

        btnResultShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareData(data);
            }
        });



    }


    private void copyToClipboard(String data) {
        if (data == null || data.isEmpty()) {
            Toast.makeText(this, "No data to copy", Toast.LENGTH_SHORT).show();
            return;
        }
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Scanned Data", data);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
    }

    private void shareData(String data) {
        if (data == null || data.isEmpty()) {
            Toast.makeText(this, "No data to share", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, data);
        startActivity(Intent.createChooser(shareIntent, "Share Data"));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
