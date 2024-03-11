@GetMapping("/getFileFromPath")
private void getFileFromPath_local(@RequestParam(required = false) String fileId) throws Exception {
    fileId = fileId.replaceAll("[^\\w\\s]", "");

    String storagePath = applicationProperties.getBillerfileStoragePath();

    if (storagePath == null) {
        throw new Exception("Storage Path not configured...");
    }

    // Specify the file extension directly
    String fileExtension = ".zip";

    // Use Paths.get to construct the path safely
    Path zipFilePath = Paths.get(storagePath, fileId + fileExtension).normalize();

    // Check if the file path is within the expected directory
    if (!zipFilePath.startsWith(Paths.get(storagePath))) {
        throw new Exception("Invalid file path or unable to access file");
    }

    if (!isValidFilePath(zipFilePath)) {
        throw new Exception("Invalid file path or unable to access file");
    }

    String fileName = zipFilePath.getFileName().toString();
    Path safePath = Paths.get(storagePath, fileName).normalize();

    byte[] newData = Files.readAllBytes(safePath);
    uploadToGCS(fileId, newData);
    System.out.println("Data uploaded to GCS");
}

private boolean isValidFilePath(Path filePath) {
    // Check if the file path is valid and readable
    return filePath.toFile() != null && filePath.toFile().exists() && filePath.toFile().canRead() && Files.size(filePath) > 0;
}
