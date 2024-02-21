import java.nio.file.*;

private File processAndCopyToLocal(MultipartFile file, String localFolderPath) throws IOException {
    // Create a temporary file
    File tempFile = File.createTempFile("processed_", ".txt");

    try (InputStream zipInputStream = file.getInputStream();
         ZipInputStream zipStream = new ZipInputStream(zipInputStream);
         BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(tempFile))) {

        // Process and copy the content in chunks
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = zipStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
    }

    // Copy the temporary file to the local folder in chunks
    Path destinationPath = Path.of(localFolderPath, "processed_file.txt");
    try (InputStream inputStream = Files.newInputStream(tempFile.toPath());
         BufferedOutputStream outputStream = new BufferedOutputStream(Files.newOutputStream(destinationPath))) {

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
    }

    // Optional: Clean up the temporary file
    Files.delete(tempFile.toPath());

    return tempFile;
}
