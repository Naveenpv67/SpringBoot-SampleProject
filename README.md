package com.example.util;

import java.math.BigDecimal;

public class AmountInWordsUtil {

    private static final String[] units = {
        "", "one", "two", "three", "four", "five",
        "six", "seven", "eight", "nine", "ten", "eleven",
        "twelve", "thirteen", "fourteen", "fifteen",
        "sixteen", "seventeen", "eighteen", "nineteen"
    };

    private static final String[] tens = {
        "", "", "twenty", "thirty", "forty", "fifty",
        "sixty", "seventy", "eighty", "ninety"
    };

    public static String convertToIndianRupeesWords(BigDecimal amount) {
        if (amount == null) {
            return "";
        }
        long rupees = amount.longValue();
        int paise = amount.remainder(BigDecimal.ONE).movePointRight(2).intValue();

        StringBuilder result = new StringBuilder();
        if (rupees == 0) {
            result.append("zero rupees");
        } else {
            result.append(convertNumberToWords(rupees).trim()).append(" rupees");
        }
        if (paise > 0) {
            result.append(" and ").append(convertNumberToWords(paise).trim()).append(" paise");
        }
        result.append(" only");
        return result.toString().replaceAll("\\s+", " ");
    }

    private static String convertNumberToWords(long number) {
        String words = "";
        if (number >= 10000000) {
            words += convertNumberToWords(number / 10000000) + " crore ";
            number %= 10000000;
        }
        if (number >= 100000) {
            words += convertNumberToWords(number / 100000) + " lakh ";
            number %= 100000;
        }
        if (number >= 1000) {
            words += convertNumberToWords(number / 1000) + " thousand ";
            number %= 1000;
        }
        if (number >= 100) {
            words += convertNumberToWords(number / 100) + " hundred ";
            number %= 100;
        }
        if (number > 0) {
            if (!words.isEmpty()) {
                words += "and ";
            }
            if (number < 20) {
                words += units[(int) number] + " ";
            } else {
                words += tens[(int) number / 10] + " ";
                if ((number % 10) > 0) {
                    words += units[(int) number % 10] + " ";
                }
            }
        }
        return words;
    }
}

BigDecimal amount = new BigDecimal("1770.55");
String inWords = AmountInWordsUtil.convertToIndianRupeesWords(amount);
// Output: "one thousand seven hundred and seventy rupees and fifty five paise only"
System.out.println(inWords);
