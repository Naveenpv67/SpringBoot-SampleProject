import io.github.millij.poi.PoiContext;
import io.github.millij.poi.ss.reader.XlsxReader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class ExcelService {

    public Map<String, Object> readAndParseFile(MultipartFile file) throws IOException {
        // Convert MultipartFile to File
        File tempFile = convertMultipartFileToFile(file);

        // Use Poiji to read Excel data
        PoiContext context = PoiContext.newContext().build();
        XlsxReader xlsxReader = context.read().xlsx(tempFile);
        List<InvoiceExcel> invoices = xlsxReader.read(InvoiceExcel.class);

        // Print and process the list of invoices
        System.out.println("Printing List Data: " + invoices);

        // Cleanup: Delete the temporary file
        tempFile.delete();

        return null;
    }

    private File convertMultipartFileToFile(MultipartFile file) throws IOException {
        File tempFile = File.createTempFile("temp", null);
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(file.getBytes());
        }
        return tempFile;
    }
}
