package com.therohitjagan.toolify.alltools.barcodetool;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.therohitjagan.toolify.R;

public class BarcodeGeneratorActivity extends AppCompatActivity {

    private EditText etInputData;
    private Spinner spinnerFormat;
    private Spinner spinnerDataType;  // New spinner for selecting the barcode data type
    private Button btnGenerate;
    private ImageView ivBarcode;
    private ImageButton btnCopy, btnShare, btnSave;

    private Bitmap generatedBitmap;
    private String generatedData;
    private BarcodeFormat selectedFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_generator);

        // Initialize views
        etInputData = findViewById(R.id.et_input_data);
        spinnerFormat = findViewById(R.id.spinner_format);
        spinnerDataType = findViewById(R.id.spinner_data_type); // Make sure this view exists in your layout
        btnGenerate = findViewById(R.id.btn_generate_barcode);
        ivBarcode = findViewById(R.id.iv_generated_barcode);
        btnCopy = findViewById(R.id.btn_copy);
        btnShare = findViewById(R.id.btn_share);
        btnSave = findViewById(R.id.btn_save);

        // Populate spinnerFormat with supported barcode formats
        final BarcodeFormat[] formats = {
                BarcodeFormat.QR_CODE,
                BarcodeFormat.CODE_128,
                BarcodeFormat.CODE_39,
                BarcodeFormat.EAN_13,
                BarcodeFormat.UPC_A
                // Add more formats if needed.
        };

        String[] formatNames = new String[formats.length];
        for (int i = 0; i < formats.length; i++) {
            formatNames[i] = formats[i].toString();
        }
        ArrayAdapter<String> formatAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, formatNames);
        formatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFormat.setAdapter(formatAdapter);

        // Populate spinnerDataType with barcode data types (Text, Link, Address, Contact, Email)
        String[] dataTypes = {"Text", "Link", "Address", "Contact", "Email"};
        ArrayAdapter<String> dataTypeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, dataTypes);
        dataTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDataType.setAdapter(dataTypeAdapter);

        // Set the generate button click listener
        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generatedData = etInputData.getText().toString().trim();
                if (generatedData.isEmpty()) {
                    Toast.makeText(BarcodeGeneratorActivity.this, "Enter some data", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Optionally, get the selected data type and perform any data-specific validation
                String selectedDataType = spinnerDataType.getSelectedItem().toString();
                // For demonstration, just show a toast with the selected data type
                Toast.makeText(BarcodeGeneratorActivity.this, "Data Type: " + selectedDataType, Toast.LENGTH_SHORT).show();

                // Get the selected barcode format from the format spinner
                selectedFormat = formats[spinnerFormat.getSelectedItemPosition()];

                // Generate the barcode bitmap
                generatedBitmap = generateBarcode(generatedData, selectedFormat, 600, 300);
                if (generatedBitmap != null) {
                    ivBarcode.setImageBitmap(generatedBitmap);
                }
            }
        });

        // Set the copy button listener
        btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                copyToClipboard(generatedData);
            }
        });

        // Set the share button listener
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareBarcode();
            }
        });

        // Set the save button listener
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveBarcodeImage();
            }
        });
    }

    // Method to generate a barcode bitmap using ZXing
    private Bitmap generateBarcode(String data, BarcodeFormat format, int width, int height) {
        try {
            MultiFormatWriter writer = new MultiFormatWriter();
            BitMatrix bitMatrix = writer.encode(data, format, width, height);
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            return bitmap;
        } catch (Exception e) {
            Toast.makeText(this, "Error generating barcode: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    // Copy the generated data to the clipboard
    private void copyToClipboard(String data) {
        if (data == null || data.isEmpty()) {
            Toast.makeText(this, "No data to copy", Toast.LENGTH_SHORT).show();
            return;
        }
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Barcode Data", data);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
    }

    // Share the generated barcode image
    private void shareBarcode() {
        if (generatedBitmap == null) {
            Toast.makeText(this, "Generate a barcode first", Toast.LENGTH_SHORT).show();
            return;
        }
        // Save bitmap temporarily to share
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), generatedBitmap, "Barcode", null);
        Uri uri = Uri.parse(path);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/png");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(intent, "Share Barcode"));
    }

    // Save the generated barcode image to the device gallery
    private void saveBarcodeImage() {
        if (generatedBitmap == null) {
            Toast.makeText(this, "Generate a barcode first", Toast.LENGTH_SHORT).show();
            return;
        }
        String savedURL = MediaStore.Images.Media.insertImage(getContentResolver(), generatedBitmap, "Barcode", "Generated barcode image");
        if (savedURL != null) {
            Toast.makeText(this, "Barcode saved to gallery", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error saving image", Toast.LENGTH_SHORT).show();
        }
    }
}
