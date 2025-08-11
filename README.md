package com.example.util;

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

    public static String convertToIndianRupeesWords(long number) {
        if (number == 0) {
            return "zero rupees only";
        }
        String words = "";
        if (number >= 10000000) {
            words += convertToIndianRupeesWords(number / 10000000) + " crore ";
            number %= 10000000;
        }
        if (number >= 100000) {
            words += convertToIndianRupeesWords(number / 100000) + " lakh ";
            number %= 100000;
        }
        if (number >= 1000) {
            words += convertToIndianRupeesWords(number / 1000) + " thousand ";
            number %= 1000;
        }
        if (number >= 100) {
            words += convertToIndianRupeesWords(number / 100) + " hundred ";
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
        words = words.trim() + " rupees only";
        return words.replaceAll("\\s+", " ");
    }
}
