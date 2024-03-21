import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateConverter {
    // Method to get the current date and format it to the expected format
    private static String getCurrentDate() {
        ZonedDateTime currentDate = ZonedDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.ENGLISH);
        return formatter.format(currentDate);
    }

    public static void main(String[] args) {
        String expectedDateString = getCurrentDate();
        System.out.println("Expected Date Format: " + expectedDateString);
    }
}
