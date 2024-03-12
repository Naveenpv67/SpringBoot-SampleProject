if (zipFilepath.toFile() != null && zipFilepath.toFile().exists() && zipFilepath.toFile().canRead()) {
    if (Files.size(zipFilepath) < 0) {
        throw new Exception("Unable to access " + zipFilepath.getFileName());
    }
} else {
    throw new Exception("Zip file does not exist or is not readable.");
}
