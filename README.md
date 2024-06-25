private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    public static double calculatePercentage(BigDecimal base, BigDecimal pct) {
        if (base.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        
        // Calculate the percentage with rounding for calculation only
        BigDecimal result = base.multiply(ONE_HUNDRED).divide(pct, 10, BigDecimal.ROUND_HALF_UP);

        // Convert to string without scientific notation
        String resultStr = result.toPlainString();

        // Split into integer and fractional parts
        String[] parts = resultStr.split("\\.");
        String integerPart = parts[0];
        String fractionalPart = (parts.length > 1) ? parts[1] : "00";

        // Ensure two decimal places
        if (fractionalPart.length() > 2) {
            fractionalPart = fractionalPart.substring(0, 2);
        } else if (fractionalPart.length() < 2) {
            fractionalPart = fractionalPart + "0";
        }

        // Combine integer and fractional parts with a decimal point
        resultStr = integerPart + "." + fractionalPart;

        // Convert back to double
        return Double.parseDouble(resultStr);
    }
