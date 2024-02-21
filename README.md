import java.io.*;
import java.nio.file.*;
import java.util.zip.*;

private File processAndCopyToLocal(MultipartFile file, String localFolderPath) throws IOException {
    // Create a temporary folder
    Path tempFolder = Files.createTempDirectory("temp_zip");
    System.out.println("Temporary folder path: " + tempFolder.toAbsolutePath());

    try (InputStream zipInputStream = file.getInputStream();
         ZipInputStream zipStream = new ZipInputStream(zipInputStream)) {

        ZipEntry entry;
        while ((entry = zipStream.getNextEntry()) != null) {
            if (!entry.isDirectory()) {
                // Process and copy the content in chunks
                Path tempFile = tempFolder.resolve(entry.getName());
                System.out.println("Extracting to temporary file: " + tempFile.toAbsolutePath());

                try (BufferedOutputStream outputStream = new BufferedOutputStream(Files.newOutputStream(tempFile))) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = zipStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }

                System.out.println("Size of temporary file after processing: " + Files.size(tempFile) + " bytes");

                // Move the processed file to the local folder
                Path destinationPath = Path.of(localFolderPath, "processed_file.txt");
                Files.move(tempFile, destinationPath, StandardCopyOption.REPLACE_EXISTING);

                System.out.println("Size of destination file: " + Files.size(destinationPath) + " bytes");

                // Optional: Clean up the temporary folder
                Files.deleteIfExists(tempFolder);
            }
        }
    }

    return new File(localFolderPath, "processed_file.txt");
}
