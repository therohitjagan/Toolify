package com.therohitjagan.toolify.alltools.calculators;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.therohitjagan.toolify.R;

import org.mariuszgromada.math.mxparser.Expression;

public class CalculatorActivity extends AppCompatActivity {

    private TextView tvExpression, tvResult;
    private StringBuilder currentExpression = new StringBuilder();
    private SwitchMaterial switchMode;
    private GridLayout gridScientific;

    private ViewGroup bannerContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        tvExpression = findViewById(R.id.tvExpression);
        tvResult = findViewById(R.id.tvResult);
        switchMode = findViewById(R.id.switchMode);
        gridScientific = findViewById(R.id.gridScientific);

        switchMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                gridScientific.setVisibility(View.VISIBLE);
            } else {
                gridScientific.setVisibility(View.GONE);
            }
        });

        setButtonListeners();

    }

    private void setButtonListeners() {
        int[] buttonIds = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9,
                R.id.btnAdd, R.id.btnSubtract, R.id.btnMultiply, R.id.btnDivide, R.id.btnDot,
                R.id.btnSin, R.id.btnCos, R.id.btnTan, R.id.btnPi, R.id.btnLog, R.id.btnLn, R.id.btnSqrt, R.id.btnPower,
                R.id.btnClear, R.id.btnDelete
        };

        for (int id : buttonIds) {
            Button button = findViewById(id);
            button.setOnClickListener(v -> {
                String value = ((Button) v).getText().toString();
                appendExpression(value);
            });
        }

        findViewById(R.id.btnEquals).setOnClickListener(v -> calculateResult());
        findViewById(R.id.btnClear).setOnClickListener(v -> clearExpression());
        findViewById(R.id.btnDelete).setOnClickListener(v -> deleteLastCharacter());
    }

    private void appendExpression(String value) {
        switch (value) {
            case "π": value = "3.1416"; break;
            case "√": value = "sqrt("; break;
            case "log": value = "log10("; break;
            case "ln": value = "log("; break;
            case "sin": value = "sin("; break;
            case "cos": value = "cos("; break;
            case "tan": value = "tan("; break;
            case "^": value = "^"; break;
        }
        currentExpression.append(value);
        tvExpression.setText(currentExpression.toString());
    }

    private void calculateResult() {
        try {
            String expression = currentExpression.toString().replaceAll("÷", "/").replaceAll("×", "*");
            Expression exp = new Expression(expression);
            double result = exp.calculate();
            if (Double.isNaN(result)) {
                tvResult.setText("Error");
            } else {
                tvResult.setText(String.valueOf(result));
            }
        } catch (Exception e) {
            tvResult.setText("Error");
        }
    }

    private void clearExpression() {
        currentExpression.setLength(0);
        tvExpression.setText("");
        tvResult.setText("");
    }

    private void deleteLastCharacter() {
        if (currentExpression.length() > 0) {
            currentExpression.deleteCharAt(currentExpression.length() - 1);
            tvExpression.setText(currentExpression.toString());
        }
    }
}