package com.example.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CommonUtils {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    /**
     * Generates the current date and time in the format YYYYMMDDhhmmss.
     *
     * @return Formatted date-time string.
     */
    public static String generateFormattedDate() {
        return LocalDateTime.now().format(DATE_FORMATTER);
    }

    /**
     * Logs the date request and reversal date request.
     * For demonstration purposes.
     */
    public static void logDateRequests() {
        String formattedDate = generateFormattedDate();
        System.out.println("DatRequest: " + formattedDate);
        System.out.println("ReversalDatRequest: " + formattedDate);
    }
}


import com.example.utils.CommonUtils;

public class ExampleApp {
    public static void main(String[] args) {
        // Log the date requests
        CommonUtils.logDateRequests();

        // Get the formatted date directly
        String formattedDate = CommonUtils.generateFormattedDate();
        System.out.println("Formatted Date: " + formattedDate);
    }
}
