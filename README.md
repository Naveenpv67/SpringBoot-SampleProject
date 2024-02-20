import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@RestController
@RequestMapping("/api")
public class ZipProcessorController {

    @Value("${gcp.bucketName}")
    private String bucketName;

    private final Storage storage;

    public ZipProcessorController() {
        this.storage = StorageOptions.getDefaultInstance().getService();
    }

    @PostMapping("/process-zip")
    public ResponseEntity<String> processZipFile(@RequestParam("file") MultipartFile file) {
        try {
            File processedFile = processAndUpload(file);
            return ResponseEntity.ok("Processing completed and file uploaded to GCP Cloud Storage.");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error processing the zip file: " + e.getMessage());
        }
    }

    private File processAndUpload(MultipartFile file) throws IOException {
        File tempFile = File.createTempFile("processed_", ".txt");

        try (InputStream zipInputStream = file.getInputStream();
             ZipInputStream zipStream = new ZipInputStream(zipInputStream);
             FileOutputStream outputStream = new FileOutputStream(tempFile)) {

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = zipStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        uploadToCloudStorage(tempFile);

        // Clean up temporary file if needed
        tempFile.delete();

        return tempFile;
    }

    private void uploadToCloudStorage(File file) {
        try (InputStream inputStream = storage.getInputStream()) {
            BlobId blobId = BlobId.of(bucketName, "processed_file.txt");
            storage.create(blobId, inputStream);

        } catch (IOException e) {
            throw new RuntimeException("Error uploading file to GCP Cloud Storage: " + e.getMessage());
        }
    }
}





import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

private File processAndUpload(MultipartFile file) throws IOException {
    // Create a temporary file
    File tempFile = File.createTempFile("processed_", ".txt");

    try (InputStream zipInputStream = file.getInputStream();
         ZipInputStream zipStream = new ZipInputStream(zipInputStream);
         FileOutputStream outputStream = new FileOutputStream(tempFile)) {

        // Process and copy the content directly to the temporary file
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = zipStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
    }

    // Upload the temporary file to GCP Cloud Storage
    uploadToCloudStorage(tempFile);

    // Optional: Clean up the temporary file
    Files.delete(tempFile.toPath());

    return tempFile;
}


