package com.example.chaevie;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Pattern;

public class DecimalAmountInputFilter implements InputFilter {
    private static final Pattern DECIMAL_AMOUNT_PATTERN = Pattern.compile("\\d*(?:[.,]\\d{0,2})?");

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        String newValue = dest.subSequence(0, dstart)
                + source.subSequence(start, end).toString()
                + dest.subSequence(dend, dest.length()).toString();
        return DECIMAL_AMOUNT_PATTERN.matcher(newValue).matches() ? null : "";
    }
}
