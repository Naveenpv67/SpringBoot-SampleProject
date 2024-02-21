import java.io.*;
import java.nio.file.*;

private File processAndCopyToLocal(MultipartFile file, String localFolderPath) throws IOException {
    // Create a temporary file
    File tempFile = File.createTempFile("processed_", ".txt");
    System.out.println("Temporary file path: " + tempFile.getAbsolutePath());

    try (InputStream zipInputStream = file.getInputStream();
         ZipInputStream zipStream = new ZipInputStream(zipInputStream);
         BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(tempFile))) {

        // Process and copy the content in chunks
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = zipStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        System.out.println("Size of temporary file after processing: " + tempFile.length() + " bytes");
    } catch (IOException e) {
        System.err.println("Error during processing: " + e.getMessage());
        throw e; // Re-throw the exception after logging
    }

    // Copy the temporary file to the local folder in chunks
    Path destinationPath = Path.of(localFolderPath, "processed_file.txt");
    System.out.println("Destination path: " + destinationPath.toAbsolutePath());

    try (InputStream inputStream = Files.newInputStream(tempFile.toPath());
         BufferedOutputStream outputStream = new BufferedOutputStream(Files.newOutputStream(destinationPath))) {

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        System.out.println("Size of destination file: " + Files.size(destinationPath) + " bytes");
    } catch (IOException e) {
        System.err.println("Error during copying to local folder: " + e.getMessage());
        throw e; // Re-throw the exception after logging
    }

    // Optional: Clean up the temporary file
    if (!Files.deleteIfExists(tempFile.toPath())) {
        System.err.println("Failed to delete temporary file: " + tempFile.getAbsolutePath());
    }

    return tempFile;
}
