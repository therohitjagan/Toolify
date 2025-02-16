package com.therohitjagan.toolify.alltools.unitconvert;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import androidx.appcompat.app.AppCompatActivity;

import com.therohitjagan.toolify.R;

public class LengthConverterActivity extends AppCompatActivity {

    private EditText inputLength;
    private Spinner unitFromSpinner, unitToSpinner;
    private TextView resultText;
    private Button convertButton;
    private ViewGroup bannerContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_length_converter);

        // Initialize UI components
        inputLength = findViewById(R.id.inputLength);
        unitFromSpinner = findViewById(R.id.unitFromSpinner);
        unitToSpinner = findViewById(R.id.unitToSpinner);
        resultText = findViewById(R.id.resultText);
        convertButton = findViewById(R.id.convertButton);

        // Set up Spinners for unit selections
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.length_units, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitFromSpinner.setAdapter(adapter);
        unitToSpinner.setAdapter(adapter);

        // Set OnClickListener for Convert Button




        convertButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String input = inputLength.getText().toString();
                if (!input.isEmpty()) {
                    double length = Double.parseDouble(input);
                    String fromUnit = unitFromSpinner.getSelectedItem().toString();
                    String toUnit = unitToSpinner.getSelectedItem().toString();
                    double convertedLength = convertLength(length, fromUnit, toUnit);
                    resultText.setText("Converted Length: " + convertedLength + " " + toUnit);
                } else {
                    Toast.makeText(LengthConverterActivity.this, "Please enter a valid length", Toast.LENGTH_SHORT).show();
                }
            }
        });
        
    }

    // Length conversion logic based on selected units
    private double convertLength(double length, String fromUnit, String toUnit) {
        // Convert everything to meters first
        double lengthInMeters = convertToMeters(length, fromUnit);

        // Then convert from meters to the desired unit
        return convertFromMeters(lengthInMeters, toUnit);
    }

    // Convert any unit to meters
    private double convertToMeters(double length, String fromUnit) {
        switch (fromUnit) {
            case "Meters":
                return length;
            case "Centimeters":
                return length / 100;
            case "Millimeters":
                return length / 1000;
            case "Kilometers":
                return length * 1000;
            case "Inches":
                return length * 0.0254;
            case "Feet":
                return length * 0.3048;
            case "Miles":
                return length * 1609.34; // 1 mile = 1609.34 meters
            case "Yards":
                return length * 0.9144; // 1 yard = 0.9144 meters
            case "Light Years":
                return length * 9.461e15; // 1 light year = 9.461e15 meters
            default:
                return length;
        }
    }

    // Convert from meters to the desired unit
    private double convertFromMeters(double lengthInMeters, String toUnit) {
        switch (toUnit) {
            case "Meters":
                return lengthInMeters;
            case "Centimeters":
                return lengthInMeters * 100;
            case "Millimeters":
                return lengthInMeters * 1000;
            case "Kilometers":
                return lengthInMeters / 1000;
            case "Inches":
                return lengthInMeters / 0.0254;
            case "Feet":
                return lengthInMeters / 0.3048;
            case "Miles":
                return lengthInMeters / 1609.34;
            case "Yards":
                return lengthInMeters / 0.9144;
            case "Light Years":
                return lengthInMeters / 9.461e15;
            default:
                return lengthInMeters;
        }
    }
}
