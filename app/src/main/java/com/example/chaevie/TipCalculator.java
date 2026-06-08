package com.example.chaevie;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class TipCalculator {
    public static final int DEFAULT_TIP_PERCENT = 15;
    public static final int DEFAULT_PEOPLE_COUNT = 1;
    public static final int MIN_TIP_PERCENT = 5;
    public static final int MAX_TIP_PERCENT = 30;
    public static final int MIN_PEOPLE_COUNT = 1;
    public static final int MAX_PEOPLE_COUNT = 20;

    private TipCalculator() {
    }

    public enum TipRounding {
        DOWN,
        UP,
        NEAREST
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
        BigDecimal bill = parseBillAmount(billInput);
        int tipPercent = parseIntegerWithDefault(tipPercentInput, DEFAULT_TIP_PERCENT, "Процент чаевых");
        int peopleCount = parseIntegerWithDefault(peopleInput, DEFAULT_PEOPLE_COUNT, "Количество человек");

        if (tipPercent < MIN_TIP_PERCENT || tipPercent > MAX_TIP_PERCENT) {
            throw new IllegalArgumentException("Процент чаевых должен быть целым числом от 5 до 30.");
        }
        if (peopleCount < MIN_PEOPLE_COUNT || peopleCount > MAX_PEOPLE_COUNT) {
            throw new IllegalArgumentException("Количество человек должно быть целым числом от 1 до 20.");
        }
        if (rounding == null) {
            throw new IllegalArgumentException("Выберите способ округления чаевых.");
        }

        BigDecimal rawTip = bill.multiply(BigDecimal.valueOf(tipPercent)).divide(BigDecimal.valueOf(100));
        BigDecimal roundedTip = roundTipToWholeAmount(rawTip, rounding).setScale(2, RoundingMode.UNNECESSARY);
        BigDecimal total = bill.add(roundedTip).setScale(2, RoundingMode.HALF_UP);
        BigDecimal perPerson = total.divide(BigDecimal.valueOf(peopleCount), 2, RoundingMode.HALF_UP);
        return new CalculationResult(roundedTip, total, perPerson);
    }

    private static BigDecimal parseBillAmount(String billInput) {
        if (billInput == null || billInput.trim().isEmpty()) {
            throw new IllegalArgumentException("Введите сумму счета.");
        }

        String normalizedInput = billInput.trim().replace(',', '.');
        if (!normalizedInput.matches("\\d+(\\.\\d{1,2})?")) {
            throw new IllegalArgumentException("Сумма счета должна быть неотрицательным числом с точностью до двух знаков.");
        }

        BigDecimal amount = new BigDecimal(normalizedInput).setScale(2, RoundingMode.UNNECESSARY);
        if (amount.signum() < 0) {
            throw new IllegalArgumentException("Сумма счета не может быть отрицательной.");
        }
        return amount;
    }

    private static int parseIntegerWithDefault(String input, int defaultValue, String fieldName) {
        if (input == null || input.trim().isEmpty()) {
            return defaultValue;
        }
        String trimmedInput = input.trim();
        if (!trimmedInput.matches("\\d+")) {
            throw new IllegalArgumentException(fieldName + " должен быть целым неотрицательным числом.");
        }
        return Integer.parseInt(trimmedInput);
    }

    private static BigDecimal roundTipToWholeAmount(BigDecimal tip, TipRounding rounding) {
        switch (rounding) {
            case DOWN:
                return tip.setScale(0, RoundingMode.FLOOR);
            case UP:
                return tip.setScale(0, RoundingMode.CEILING);
            case NEAREST:
                return tip.setScale(0, RoundingMode.HALF_UP);
            default:
                throw new IllegalArgumentException("Неизвестный способ округления чаевых.");
        }
    }
}
