import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class MainClass {
    public static void main(String[] args) {
        // Call the method to create the validation string
        String validationString = createValidationString();

        // Print the validation string
        System.out.println(validationString);
    }

    // Method to create the validation string
    private static String createValidationString() {
        // Declare class variables
        String host = "apitest.cybersource.com";
        String requestTarget = "post /pts/v2/payments/";
        String merchantId = "mymerchantid";
        String digest = "SHA-256=gXWufV4Zc7VkN9Wkv9jh/JuAVclqDusx3vkyo3uJFWU=";

        // Get the current date and format it
        String formattedDate = getCurrentDate();

        // Build the validation string
        StringBuilder validationString = new StringBuilder();
        validationString.append("host: ").append(host).append("\n");
        validationString.append("date: ").append(formattedDate).append("\n");
        validationString.append("(request-target): ").append(requestTarget).append("\n");
        validationString.append("digest: ").append(digest).append("\n");
        validationString.append("v-c-merchant-id: ").append(merchantId).append("\n");

        return validationString.toString();
    }

    // Method to get the current date and format it
    private static String getCurrentDate() {
        ZonedDateTime currentDate = ZonedDateTime.now();
        return DateTimeFormatter.RFC_1123_DATE_TIME.format(currentDate);
    }
}
