package com.therohitjagan.toolify.alltools.barcodetool.models;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

public class BarcodeUtil {

    /**
     * Generate a bitmap image for the given barcode data and format.
     *
     * @param data   The text to encode.
     * @param format The barcode format.
     * @param width  The width of the generated bitmap.
     * @param height The height of the generated bitmap.
     * @return A Bitmap of the barcode, or null if there was an error.
     */
    public static Bitmap generateBarcode(String data, BarcodeFormat format, int width, int height) {
        try {
            MultiFormatWriter writer = new MultiFormatWriter();
            BitMatrix bitMatrix = writer.encode(data, format, width, height);
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++){
                for (int y = 0; y < height; y++){
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

