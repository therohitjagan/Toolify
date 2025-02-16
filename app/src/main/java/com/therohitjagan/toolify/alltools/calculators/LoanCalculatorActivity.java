package com.therohitjagan.toolify.alltools.calculators;


import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.therohitjagan.toolify.R;

public class LoanCalculatorActivity extends AppCompatActivity {

    // Declare all UI components
    private EditText loanAmount, interestRate, loanTenure;
    private TextView emiResult;
    private CardView emiResultCard;

    private ViewGroup bannerContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_calculator);

        // Initialize all UI components
        loanAmount = findViewById(R.id.loanAmount);
        interestRate = findViewById(R.id.interestRate);
        loanTenure = findViewById(R.id.loanTenure);
        emiResult = findViewById(R.id.emiResult);
        emiResultCard = findViewById(R.id.emiResultCard);

        // Calculate EMI Button
        Button calculateButton = findViewById(R.id.calculateButton);


        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculateEMI();
            }


        });

    }

    // Function to calculate EMI
    private void calculateEMI() {
        try {
            // Fetch values from input fields
            String loanAmountText = loanAmount.getText().toString();
            String interestRateText = interestRate.getText().toString();
            String loanTenureText = loanTenure.getText().toString();

            // Validate input fields
            if (loanAmountText.isEmpty() || interestRateText.isEmpty() || loanTenureText.isEmpty()) {
                Toast.makeText(LoanCalculatorActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Convert input to numeric values
            double principal = Double.parseDouble(loanAmountText);
            double rateOfInterest = Double.parseDouble(interestRateText) / 12 / 100;  // Monthly interest
            int tenureInMonths = Integer.parseInt(loanTenureText) * 12;  // Convert tenure to months

            // EMI calculation formula
            double emi = (principal * rateOfInterest * Math.pow(1 + rateOfInterest, tenureInMonths)) /
                    (Math.pow(1 + rateOfInterest, tenureInMonths) - 1);

            // Format and display EMI result
            emiResult.setText("EMI: ₹" + String.format("%.2f", emi));
            emiResultCard.setVisibility(View.VISIBLE); // Show the EMI result card

        } catch (NumberFormatException e) {
            // Catch any number format exception
            emiResultCard.setVisibility(View.GONE); // Hide result card if error occurs
            Toast.makeText(LoanCalculatorActivity.this, "Invalid input. Please enter valid values", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            // Catch any other exceptions
            emiResultCard.setVisibility(View.GONE); // Hide result card if error occurs
            Toast.makeText(LoanCalculatorActivity.this, "Something went wrong. Please try again", Toast.LENGTH_SHORT).show();
        }
    }
}
