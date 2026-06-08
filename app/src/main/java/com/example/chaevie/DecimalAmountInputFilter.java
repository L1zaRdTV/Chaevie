package com.example.chaevie;

import android.text.InputFilter;
import android.text.Spanned;

public class DecimalAmountInputFilter implements InputFilter {
    private static final String DECIMAL_AMOUNT_PATTERN = "\\d*(?:[.,]\\d{0,2})?";

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        String newValue = dest.subSequence(0, dstart)
                + source.subSequence(start, end).toString()
                + dest.subSequence(dend, dest.length());
        if (newValue.matches(DECIMAL_AMOUNT_PATTERN)) {
            return null;
        }
        return "";
    }
}
