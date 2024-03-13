import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

public class FileHandler {

    // Method to append CSV data to a file
    public void appendCsvFile(Map<String, Object> map) throws IOException, CsvRequiredFieldEmptyException, CsvDataTypeMismatchException {
        Class<Object> dataType = (Class<Object>) map.get("CLASS_TYPE");
        String fileName = (String) map.get("FILE_NAME_WITH_STORAGE_PATH");

        // Validate file name
        if (!isValidFileName(fileName)) {
            throw new IllegalArgumentException("Invalid file name.");
        }

        // Construct the file path
        Path filePath = Paths.get(fileName).normalize().toAbsolutePath();

        // Validate file path
        if (!isValidFilePath(filePath)) {
            throw new IllegalArgumentException("Invalid file path or potential path traversal attempt.");
        }

        try (StringWriter stringWriter = new StringWriter();
             CSVWriter csvWriter = new CSVWriter(stringWriter)) {

            // Mapping strategy for CSV writing
            ColumnPositionMappingStrategy<Object> mappingStrategy = new ColumnPositionMappingStrategy<>();
            mappingStrategy.setType(dataType);
            mappingStrategy.setColumnMapping((String) map.get("COLUMN_LIST_FOR_HEADER"));

            // Create CSV writer with mapping strategy
            StatefulBeanToCsv<Object> beanWriter = new StatefulBeanToCsvBuilder<>(csvWriter)
                    .withMappingStrategy(mappingStrategy)
                    .build();

            // Write data to CSV
            beanWriter.write((ArrayList) map.get("DATA"));

            // Write the CSV data to the file
            Files.write(filePath, stringWriter.toString().getBytes(StandardCharsets.UTF_8));
        }
    }

    // Method to validate file name
    private boolean isValidFileName(String fileName) {
        // Basic validation: Allow alphanumeric characters, underscores, dashes, and dots
        // Adjust this regex pattern based on your specific requirements
        String regexPattern = "^[\\w-.]+$";
        return fileName.matches(regexPattern);
    }

    // Method to validate file path
    private boolean isValidFilePath(Path filePath) {
        // Example: Allow only files within a specific directory
        Path baseDirectory = Paths.get("/path/to/allowed/directory");
        return filePath.startsWith(baseDirectory);
    }
}
