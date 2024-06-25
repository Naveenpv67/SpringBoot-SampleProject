import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class PercentageCalculator {

    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    public static Double percentage(BigDecimal base, BigDecimal pct) {
        if (base.intValue() == 0) {
            return 0.0;
        }
        
        BigDecimal result = base.divide(pct, new MathContext(10)).multiply(ONE_HUNDRED);
        String resultStr = result.toPlainString();
        int decimalPointIndex = resultStr.indexOf('.');
        
        // Ensure we have a decimal point and at least 2 decimal places
        if (decimalPointIndex != -1) {
            int decimals = resultStr.length() - decimalPointIndex - 1;
            if (decimals >= 2) {
                resultStr = resultStr.substring(0, decimalPointIndex + 3); // include decimal point and two decimal places
            } else {
                // Add zeros if necessary
                resultStr += "0".repeat(2 - decimals);
            }
        } else {
            // If no decimal point, add ".00"
            resultStr += ".00";
        }

        return Double.valueOf(resultStr);
    }

    public static void main(String[] args) {
        BigDecimal base = new BigDecimal("50");
        BigDecimal pct = new BigDecimal("200");

        System.out.println(percentage(base, pct)); // Expected output: 25.00
    }
}
