import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class JsonArrayFromFoldersController {

    @PostMapping("/processFolders")
    public ResponseEntity<ProcessedFilesResponse> processFolders(@RequestBody String mainFolderPath) {
        try {
            List<JsonNode> jsonArray = new ArrayList<>();
            int processedFilesCount = 0;

            File mainFolder = new File(mainFolderPath);

            if (!mainFolder.exists() || !mainFolder.isDirectory()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            File[] subfolders = mainFolder.listFiles(File::isDirectory);
            if (subfolders != null) {
                for (File subfolder : subfolders) {
                    File jsonFile = findJsonFileInFolder(subfolder);

                    if (jsonFile != null) {
                        JsonNode jsonObject = readAndParseJson(jsonFile);
                        if (jsonObject != null) {
                            jsonArray.add(jsonObject);
                            processedFilesCount++;
                        }
                    }
                }
            }

            ProcessedFilesResponse response = new ProcessedFilesResponse(jsonArray, processedFilesCount);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private File findJsonFileInFolder(File folder) {
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));
        if (files != null && files.length > 0) {
            return files[0];
        }
        return null;
    }

    private JsonNode readAndParseJson(File jsonFile) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readTree(jsonFile);
    }
}
