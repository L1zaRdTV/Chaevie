package com.example.chaevie;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
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
        setTitle("Калькулятор чаевых");
        setContentView(createContentView());
    }

    private View createContentView() {
        ScrollView scrollView = new ScrollView(this);
        scrollView.setBackgroundColor(Color.rgb(255, 248, 240));

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        int padding = dp(20);
        container.setPadding(padding, padding, padding, padding);
        scrollView.addView(container);

        TextView titleView = createTextView("Калькулятор чаевых", 28, true);
        titleView.setGravity(Gravity.CENTER_HORIZONTAL);
        container.addView(titleView);

        billAmountInput = createEditText("", "Например: 1200.50", InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        billAmountInput.setFilters(new InputFilter[] { new DecimalAmountInputFilter() });
        container.addView(createFieldBlock("Сумма счета (до двух знаков после запятой)", billAmountInput));

        tipPercentInput = createEditText("15", "15", InputType.TYPE_CLASS_NUMBER);
        tipPercentInput.setFilters(new InputFilter[] { new InputFilter.LengthFilter(2) });
        container.addView(createFieldBlock("Процент чаевых (целое число 5–30, по умолчанию 15%)", tipPercentInput));

        peopleCountInput = createEditText("1", "1", InputType.TYPE_CLASS_NUMBER);
        peopleCountInput.setFilters(new InputFilter[] { new InputFilter.LengthFilter(2) });
        container.addView(createFieldBlock("Количество человек (целое число 1–20, по умолчанию 1)", peopleCountInput));

        container.addView(createTextView("Округление суммы чаевых", 16, true));
        roundingGroup = new RadioGroup(this);
        roundingGroup.setOrientation(RadioGroup.VERTICAL);
        roundingGroup.addView(createRadioButton(1, "В меньшую сторону"));
        roundingGroup.addView(createRadioButton(2, "В большую сторону"));
        roundingGroup.addView(createRadioButton(3, "До ближайшего целого"));
        roundingGroup.check(3);
        container.addView(roundingGroup);

        Button calculateButton = new Button(this);
        calculateButton.setText("Рассчитать");
        calculateButton.setAllCaps(false);
        calculateButton.setOnClickListener(view -> calculateAndRenderResult());
        container.addView(calculateButton, createMarginLayoutParams());

        errorView = createTextView("", 16, true);
        errorView.setTextColor(Color.rgb(176, 0, 32));
        errorView.setVisibility(View.GONE);
        container.addView(errorView, createMarginLayoutParams());

        tipAmountView = createTextView("Сумма чаевых: —", 18, false);
        totalAmountView = createTextView("Общая сумма: —", 18, false);
        perPersonAmountView = createTextView("Сумма на каждого: —", 18, false);
        container.addView(tipAmountView, createMarginLayoutParams());
        container.addView(totalAmountView, createMarginLayoutParams());
        container.addView(perPersonAmountView, createMarginLayoutParams());

        return scrollView;
    }

    private LinearLayout createFieldBlock(String label, EditText editText) {
        LinearLayout fieldBlock = new LinearLayout(this);
        fieldBlock.setOrientation(LinearLayout.VERTICAL);
        fieldBlock.addView(createTextView(label, 16, true));
        fieldBlock.addView(editText);
        fieldBlock.setLayoutParams(createMarginLayoutParams());
        return fieldBlock;
    }

    private EditText createEditText(String text, String hint, int inputType) {
        EditText editText = new EditText(this);
        editText.setText(text);
        editText.setHint(hint);
        editText.setInputType(inputType);
        editText.setSingleLine(true);
        editText.setTextSize(18);
        return editText;
    }

    private TextView createTextView(String text, int textSizeSp, boolean bold) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextSize(textSizeSp);
        textView.setTextColor(Color.rgb(45, 34, 28));
        if (bold) {
            textView.setTypeface(textView.getTypeface(), android.graphics.Typeface.BOLD);
        }
        return textView;
    }

    private RadioButton createRadioButton(int id, String text) {
        RadioButton radioButton = new RadioButton(this);
        radioButton.setId(id);
        radioButton.setText(text);
        radioButton.setTextSize(16);
        return radioButton;
    }

    private LinearLayout.LayoutParams createMarginLayoutParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, dp(12), 0, 0);
        return params;
    }

    private void calculateAndRenderResult() {
        try {
            TipCalculator.CalculationResult result = TipCalculator.calculate(
                    billAmountInput.getText().toString(),
                    tipPercentInput.getText().toString(),
                    peopleCountInput.getText().toString(),
                    selectedRounding());
            errorView.setVisibility(View.GONE);
            tipAmountView.setText("Сумма чаевых: " + formatMoney(result.getTipAmount()));
            totalAmountView.setText("Общая сумма: " + formatMoney(result.getTotalAmount()));
            perPersonAmountView.setText("Сумма на каждого: " + formatMoney(result.getAmountPerPerson()));
        } catch (IllegalArgumentException exception) {
            errorView.setText(exception.getMessage());
            errorView.setVisibility(View.VISIBLE);
            tipAmountView.setText("Сумма чаевых: —");
            totalAmountView.setText("Общая сумма: —");
            perPersonAmountView.setText("Сумма на каждого: —");
        }
    }

    private TipCalculator.TipRounding selectedRounding() {
        int selectedId = roundingGroup.getCheckedRadioButtonId();
        if (selectedId == 1) {
            return TipCalculator.TipRounding.DOWN;
        }
        if (selectedId == 2) {
            return TipCalculator.TipRounding.UP;
        }
        return TipCalculator.TipRounding.NEAREST;
    }

    private String formatMoney(BigDecimal value) {
        return String.format(Locale.getDefault(), "%.2f", value);
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
