package com.example.chaevie;

import java.math.BigDecimal;

public final class TipCalculatorSmokeTest {
    private TipCalculatorSmokeTest() {
    }

    public static void main(String[] args) {
        calculatesNearestRoundedTipAndSplitTotal();
        usesDefaultsForEmptyOptionalFields();
        rejectsInvalidValues();
        System.out.println("TipCalculator smoke tests passed");
    }

    private static void calculatesNearestRoundedTipAndSplitTotal() {
        TipCalculator.CalculationResult result = TipCalculator.calculate(
                "100.50", "15", "2", TipCalculator.TipRounding.NEAREST);
        assertEquals("15.00", result.getTipAmount());
        assertEquals("115.50", result.getTotalAmount());
        assertEquals("57.75", result.getAmountPerPerson());
    }

    private static void usesDefaultsForEmptyOptionalFields() {
        TipCalculator.CalculationResult result = TipCalculator.calculate(
                "200", "", "", TipCalculator.TipRounding.UP);
        assertEquals("30.00", result.getTipAmount());
        assertEquals("230.00", result.getTotalAmount());
        assertEquals("230.00", result.getAmountPerPerson());
    }

    private static void rejectsInvalidValues() {
        expectFailure(() -> TipCalculator.calculate("", "15", "1", TipCalculator.TipRounding.DOWN));
        expectFailure(() -> TipCalculator.calculate("10.999", "15", "1", TipCalculator.TipRounding.DOWN));
        expectFailure(() -> TipCalculator.calculate("10", "31", "1", TipCalculator.TipRounding.DOWN));
        expectFailure(() -> TipCalculator.calculate("10", "15", "21", TipCalculator.TipRounding.DOWN));
        expectFailure(() -> TipCalculator.calculate("10", "15.5", "1", TipCalculator.TipRounding.DOWN));
    }

    private static void assertEquals(String expected, BigDecimal actual) {
        String actualValue = actual.toPlainString();
        if (!expected.equals(actualValue)) {
            throw new AssertionError("Expected " + expected + " but got " + actualValue);
        }
    }

    private static void expectFailure(Runnable runnable) {
        try {
            runnable.run();
        } catch (IllegalArgumentException expected) {
            return;
        }
        throw new AssertionError("Expected IllegalArgumentException");
    }
}
