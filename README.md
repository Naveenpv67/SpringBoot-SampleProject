// Construct zip file path
Path zipFilePath = Paths.get(safeStoragePath.toString(), fileId + Constants.ZIP_FILE_EXTENSION).normalize().toAbsolutePath();

// Validate zipFilePath against safe storage path
if (!zipFilePath.startsWith(safeStoragePath)) {
    throw new Exception("Invalid file path or potential path traversal attempt.");
}

// Check if zip file exists and is readable
if (!Files.exists(zipFilePath) || !Files.isRegularFile(zipFilePath) || !Files.isReadable(zipFilePath)) {
    throw new Exception("Zip file does not exist or is not readable.");
}

// Read file bytes
byte[] newData = Files.readAllBytes(zipFilePath);
