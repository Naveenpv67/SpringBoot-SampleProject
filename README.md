import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/aerospike")
public class AerospikeRWRestController {

    @Autowired
    private AerospikeRWService aerospikeRWService;

    @GetMapping("/datasets/{namespace}")
    public ResponseEntity<List<String>> getAllDatasets(@PathVariable String namespace) {
        List<String> datasets = aerospikeRWService.getAllDatasets(namespace);
        HttpStatus status = datasets.isEmpty() ? HttpStatus.INTERNAL_SERVER_ERROR : HttpStatus.OK;
        return new ResponseEntity<>(datasets, status);
    }

    @DeleteMapping("/set/{namespace}/{setName}")
    public ResponseEntity<String> deleteSet(@PathVariable String namespace, @PathVariable String setName) {
        String result = aerospikeRWService.deleteSet(namespace, setName);
        HttpStatus status = result.contains("Error") ? HttpStatus.INTERNAL_SERVER_ERROR : HttpStatus.OK;
        return new ResponseEntity<>(result, status);
    }

    @DeleteMapping("/bulk-delete/{namespace}")
    public ResponseEntity<String> bulkDeleteSets(@PathVariable String namespace) {
        String result = aerospikeRWService.bulkDeleteSets(namespace);
        HttpStatus status = result.contains("Error") ? HttpStatus.INTERNAL_SERVER_ERROR : HttpStatus.OK;
        return new ResponseEntity<>(result, status);
    }
}
