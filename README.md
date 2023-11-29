public class ProcessedFilesResponse {
    private List<JsonNode> processedFiles;
    private int processedFilesCount;

    public ProcessedFilesResponse(List<JsonNode> processedFiles, int processedFilesCount) {
        this.processedFiles = processedFiles;
        this.processedFilesCount = processedFilesCount;
    }

    // Getters and setters
}
