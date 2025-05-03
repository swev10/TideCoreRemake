package com.tideCore.util;

public class NumberFormatter {

    public static String format(int number) {
        if (number >= 1_000_000) {
            double millions = number / 1_000_000.0;
            return String.format("%.1fM", millions).replace(".0", "");
        } else if (number >= 1_000) {
            double thousands = number / 1_000.0;
            return String.format("%.1fk", thousands).replace(".0", "");
        } else {
            return String.valueOf(number);
        }
    }
}
