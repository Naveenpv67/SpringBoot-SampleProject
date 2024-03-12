@SuppressWarnings("unused")
@GetMapping("/getFileFromPath")
private void getFileFromPath_local(@RequestParam(required = false) String fileId) throws Exception {
    // Sanitize fileId to allow only alphanumeric characters and whitespaces
    fileId = fileId.replaceAll("[^\\w\\s]", "");

    // Convert fileId to canonical form
    File file = new File(fileId);
    fileId = String.valueOf(file.getCanonicalFile());

    // Obtain and validate storage path from application properties
    String storagePath = applicationProperties.getBillerfileStoragePath();
    if (storagePath == null || storagePath.isEmpty()) {
        throw new Exception("Storage Path not configured or empty.");
    }

    // Construct safe storage path
    Path safeStoragePath = Paths.get(storagePath).normalize().toAbsolutePath();

    // Construct zip file path and validate against safe storage path
    Path zipFilePath = Paths.get(safeStoragePath.toString(), fileId + Constants.ZIP_FILE_EXTENSION).normalize().toAbsolutePath();
    if (!zipFilePath.startsWith(safeStoragePath)) {
        throw new Exception("Invalid file path or potential path traversal attempt.");
    }

    // Check if zip file exists and is readable
    if (!Files.exists(zipFilePath) || !Files.isRegularFile(zipFilePath) || !Files.isReadable(zipFilePath)) {
        throw new Exception("Zip file does not exist or is not readable.");
    }

    // Read file bytes
    byte[] newData = Files.readAllBytes(zipFilePath);

    // Upload data to GCS
    uploadToGCS(fileId, newData);

    System.out.println("Data uploaded to GCS");
}
