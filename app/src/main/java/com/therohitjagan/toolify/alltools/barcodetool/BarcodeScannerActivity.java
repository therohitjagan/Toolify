package com.therohitjagan.toolify.alltools.barcodetool;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.therohitjagan.toolify.R;
import com.therohitjagan.toolify.alltools.barcodetool.models.BarcodeDecoderUtil;
import com.therohitjagan.toolify.alltools.barcodetool.models.BarcodeUtil;
import com.therohitjagan.toolify.alltools.barcodetool.models.DatabaseHelper;

import java.io.IOException;
import java.util.List;

public class BarcodeScannerActivity extends AppCompatActivity {

    private static final int REQUEST_GALLERY = 1001;
    private static final int PERMISSION_REQUEST_CAMERA = 2001;

    private ImageButton btnSaveScanned;
    private DecoratedBarcodeView barcodeView;
    private ImageButton btnFlash, btnSwitchCamera, btnGallery;
    private boolean isFlashOn = false;
    private final int currentCameraId = 0; // 0 for back, 1 for front

    private String lastScannedData;
    private BarcodeFormat lastScannedFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scanner);

        barcodeView = findViewById(R.id.barcode_scanner);
        btnFlash = findViewById(R.id.btn_flash);
//        btnSwitchCamera = findViewById(R.id.btn_switch_camera);
        btnGallery = findViewById(R.id.btn_scan_gallery);
        btnSaveScanned = findViewById(R.id.btn_save_scanned);

        // Check camera permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
        } else {
            initScanner();
        }

        btnFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { toggleFlash(); }
        });

        /*btnSwitchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { switchCamera(); }
        });*/

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { openGallery(); }
        });

        btnSaveScanned.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Use the scanned result from the callback.
                if (lastScannedData != null && lastScannedFormat != null) {
                    Bitmap barcodeBitmap = BarcodeUtil.generateBarcode(lastScannedData, lastScannedFormat, 600, 300);
                    if (barcodeBitmap != null) {
                        // Save to gallery using similar approach as in the generator.
                        String savedURL = MediaStore.Images.Media.insertImage(getContentResolver(), barcodeBitmap, "ScannedBarcode", "Scanned barcode image");
                        if (savedURL != null) {
                            Toast.makeText(BarcodeScannerActivity.this, "Barcode image saved to gallery", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(BarcodeScannerActivity.this, "Error saving barcode image", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(BarcodeScannerActivity.this, "No scanned barcode available", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initScanner() {
        barcodeView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                // Pause scanning to avoid duplicate results
                barcodeView.pause();
                lastScannedData = result.getText();

                // Convert the scanned format from ZXing to BarcodeFormat (with fallback)
                try {
                    lastScannedFormat = BarcodeFormat.valueOf(result.getBarcodeFormat().toString());
                } catch (IllegalArgumentException e) {
                    lastScannedFormat = BarcodeFormat.QR_CODE;
                }

                // Optionally, save the scanned result to the history database
                DatabaseHelper dbHelper = new DatabaseHelper(BarcodeScannerActivity.this);
                dbHelper.insertHistory(lastScannedData, result.getBarcodeFormat().toString());

                // Navigate to the ResultActivity to show the scanned result.
                // Assume that ResultActivity.EXTRA_SCANNED_DATA is defined in your ResultActivity.
                Intent intent = new Intent(BarcodeScannerActivity.this, com.therohitjagan.toolify.alltools.barcodetool.BarcodeResult.class);
                intent.putExtra(com.therohitjagan.toolify.alltools.barcodetool.BarcodeResult.EXTRA_SCANNED_DATA, lastScannedData);
                startActivity(intent);

                // Optionally, you can resume scanning after a delay if you want to allow repeated scans.
                barcodeView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        barcodeView.resume();
                    }
                }, 2000);
            }

            @Override
            public void possibleResultPoints(List resultPoints) {
                // Optionally, handle possible result points
            }
        });
    }

    private void toggleFlash() {
        if (isFlashOn) {
            barcodeView.setTorchOff();
            btnFlash.setImageResource(R.drawable.ic_flash_on); // update to flash on icon
        } else {
            barcodeView.setTorchOn();
            btnFlash.setImageResource(R.drawable.ic_flash_off); // update to flash off icon
        }
        isFlashOn = !isFlashOn;
    }

    /*private void switchCamera() {
        // ZXing Embedded does not directly support switching cameras.
        // This is a placeholder implementation.
        currentCameraId = (currentCameraId == 0) ? 1 : 0;
        Toast.makeText(this, "Switching camera not fully implemented", Toast.LENGTH_SHORT).show();
    }*/

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_GALLERY);
    }

    // Handle gallery image result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_GALLERY && resultCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                // Use a helper method to decode a barcode from the bitmap
                String result = BarcodeDecoderUtil.decodeBitmap(bitmap);
                if (result != null) {
                    Toast.makeText(this, "Scanned from gallery: " + result, Toast.LENGTH_SHORT).show();
                    // Optionally, save to history
                    DatabaseHelper dbHelper = new DatabaseHelper(this);
                    dbHelper.insertHistory(result, "GALLERY");
                } else {
                    Toast.makeText(this, "No barcode found in image", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // Handle runtime permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initScanner();
            } else {
                Toast.makeText(this, "Camera permission is required.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        barcodeView.pause();
    }
}
