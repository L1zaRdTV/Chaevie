package com.example.chaevie;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Pattern;

public final class TipCalculator {
    public static final int DEFAULT_TIP_PERCENT = 15;
    public static final int DEFAULT_PEOPLE_COUNT = 1;
    public static final int MIN_TIP_PERCENT = 5;
    public static final int MAX_TIP_PERCENT = 30;
    public static final int MIN_PEOPLE_COUNT = 1;
    public static final int MAX_PEOPLE_COUNT = 20;

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
    private static final Pattern BILL_PATTERN = Pattern.compile("\\d+(?:\\.\\d{1,2})?");
    private static final Pattern INTEGER_PATTERN = Pattern.compile("\\d+");

    private TipCalculator() {
    }

    public enum TipRounding {
        DOWN(RoundingMode.FLOOR),
        UP(RoundingMode.CEILING),
        NEAREST(RoundingMode.HALF_UP);

        private final RoundingMode roundingMode;

        TipRounding(RoundingMode roundingMode) {
            this.roundingMode = roundingMode;
        }

        private BigDecimal round(BigDecimal value) {
            return value.setScale(0, roundingMode).setScale(2, RoundingMode.UNNECESSARY);
        }
    }

    public static final class CalculationResult {
        private final BigDecimal tipAmount;
        private final BigDecimal totalAmount;
        private final BigDecimal amountPerPerson;

        private CalculationResult(BigDecimal tipAmount, BigDecimal totalAmount, BigDecimal amountPerPerson) {
            this.tipAmount = tipAmount;
            this.totalAmount = totalAmount;
            this.amountPerPerson = amountPerPerson;
        }

        public BigDecimal getTipAmount() {
            return tipAmount;
        }

        public BigDecimal getTotalAmount() {
            return totalAmount;
        }

        public BigDecimal getAmountPerPerson() {
            return amountPerPerson;
        }
    }

    public static CalculationResult calculate(String billInput, String tipPercentInput, String peopleInput,
            TipRounding rounding) {
        if (rounding == null) {
            throw new IllegalArgumentException("Выберите способ округления чаевых.");
        }

        BigDecimal bill = parseBillAmount(billInput);
        int tipPercent = parseIntegerWithDefault(tipPercentInput, DEFAULT_TIP_PERCENT, "Процент чаевых");
        int peopleCount = parseIntegerWithDefault(peopleInput, DEFAULT_PEOPLE_COUNT, "Количество человек");
        validateRange(tipPercent, MIN_TIP_PERCENT, MAX_TIP_PERCENT,
                "Процент чаевых должен быть целым числом от 5 до 30.");
        validateRange(peopleCount, MIN_PEOPLE_COUNT, MAX_PEOPLE_COUNT,
                "Количество человек должно быть целым числом от 1 до 20.");

        BigDecimal tip = rounding.round(bill.multiply(BigDecimal.valueOf(tipPercent)).divide(HUNDRED));
        BigDecimal total = bill.add(tip).setScale(2, RoundingMode.HALF_UP);
        BigDecimal perPerson = total.divide(BigDecimal.valueOf(peopleCount), 2, RoundingMode.HALF_UP);
        return new CalculationResult(tip, total, perPerson);
    }

    private static BigDecimal parseBillAmount(String billInput) {
        String normalizedInput = normalizeRequired(billInput, "Введите сумму счета.").replace(',', '.');
        if (!BILL_PATTERN.matcher(normalizedInput).matches()) {
            throw new IllegalArgumentException("Сумма счета должна быть неотрицательным числом с точностью до двух знаков.");
        }
        return new BigDecimal(normalizedInput).setScale(2, RoundingMode.UNNECESSARY);
    }

    private static int parseIntegerWithDefault(String input, int defaultValue, String fieldName) {
        if (input == null || input.trim().isEmpty()) {
            return defaultValue;
        }

        String trimmedInput = input.trim();
        if (!INTEGER_PATTERN.matcher(trimmedInput).matches()) {
            throw new IllegalArgumentException(fieldName + " должен быть целым неотрицательным числом.");
        }

        try {
            return Integer.parseInt(trimmedInput);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(fieldName + " слишком большой.");
        }
    }

    private static String normalizeRequired(String input, String emptyMessage) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException(emptyMessage);
        }
        return input.trim();
    }

    private static void validateRange(int value, int min, int max, String message) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(message);
        }
    }
}
