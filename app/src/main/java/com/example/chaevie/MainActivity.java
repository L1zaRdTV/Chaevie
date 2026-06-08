package com.example.chaevie;

import android.app.Activity;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.Locale;

public class MainActivity extends Activity {
    private EditText billAmountInput;
    private EditText tipPercentInput;
    private EditText peopleCountInput;
    private RadioGroup roundingGroup;
    private TextView errorView;
    private TextView tipAmountView;
    private TextView totalAmountView;
    private TextView perPersonAmountView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.app_name);
        setContentView(R.layout.activity_main);
        bindViews();
        configureInputFilters();
        configureActions();
    }

    private void bindViews() {
        billAmountInput = findViewById(R.id.billAmountInput);
        tipPercentInput = findViewById(R.id.tipPercentInput);
        peopleCountInput = findViewById(R.id.peopleCountInput);
        roundingGroup = findViewById(R.id.roundingGroup);
        errorView = findViewById(R.id.errorView);
        tipAmountView = findViewById(R.id.tipAmountView);
        totalAmountView = findViewById(R.id.totalAmountView);
        perPersonAmountView = findViewById(R.id.perPersonAmountView);
    }

    private void configureInputFilters() {
        billAmountInput.setFilters(new InputFilter[] { new DecimalAmountInputFilter() });
    }

    private void configureActions() {
        Button calculateButton = findViewById(R.id.calculateButton);
        calculateButton.setOnClickListener(view -> calculateAndRenderResult());
    }

    private void calculateAndRenderResult() {
        try {
            TipCalculator.CalculationResult result = TipCalculator.calculate(
                    billAmountInput.getText().toString(),
                    tipPercentInput.getText().toString(),
                    peopleCountInput.getText().toString(),
                    selectedRounding());
            renderResult(result);
        } catch (IllegalArgumentException exception) {
            renderError(exception.getMessage());
        }
    }

    private void renderResult(TipCalculator.CalculationResult result) {
        errorView.setVisibility(View.GONE);
        tipAmountView.setText(getString(R.string.tip_amount_format, formatMoney(result.getTipAmount())));
        totalAmountView.setText(getString(R.string.total_amount_format, formatMoney(result.getTotalAmount())));
        perPersonAmountView.setText(getString(R.string.per_person_amount_format, formatMoney(result.getAmountPerPerson())));
    }

    private void renderError(String message) {
        errorView.setText(message);
        errorView.setVisibility(View.VISIBLE);
        tipAmountView.setText(R.string.tip_amount_empty);
        totalAmountView.setText(R.string.total_amount_empty);
        perPersonAmountView.setText(R.string.per_person_amount_empty);
    }

    private TipCalculator.TipRounding selectedRounding() {
        int selectedId = roundingGroup.getCheckedRadioButtonId();
        if (selectedId == R.id.roundDownButton) {
            return TipCalculator.TipRounding.DOWN;
        }
        if (selectedId == R.id.roundUpButton) {
            return TipCalculator.TipRounding.UP;
        }
        return TipCalculator.TipRounding.NEAREST;
    }

    private String formatMoney(BigDecimal value) {
        return String.format(Locale.getDefault(), "%.2f", value);
    }
}
