import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@RestController
public class FileController {

    @GetMapping("/getFilesInFolder")
    public List<String> getFilesInFolder(@RequestParam String folderPath) {
        File folder = new File(folderPath);

        if (folder.exists() && folder.isDirectory()) {
            String[] fileNames = folder.list();
            return Arrays.asList(fileNames);
        } else {
            throw new IllegalArgumentException("Invalid folder path or not a directory");
        }
    }
}
