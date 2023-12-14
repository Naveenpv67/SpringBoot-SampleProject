// ExportController.java
@RestController
@RequestMapping("/api/export")
public class ExportController {

    @Autowired
    private ExportService exportService;

    @GetMapping("/{namespace}")
    public ResponseEntity<String> exportNamespace(@PathVariable String namespace) {
        exportService.exportNamespace(namespace);
        return ResponseEntity.ok("Namespace export process initiated. Check logs for details.");
    }
}




// ExportService.java
@Service
public class ExportService {

    @Value("${aerospike.backup.directory}")
    private String backupDirectory;

    public void exportNamespace(String namespace) {
        String outputFile = backupDirectory + File.separator + "exported_" + namespace + ".asb";
        String[] command = {"asbackup", "--namespace", namespace, "--output-file", outputFile};
        
        try {
            Process process = new ProcessBuilder(command).start();
            int exitCode = process.waitFor();
            
            if (exitCode == 0) {
                System.out.println("Namespace exported successfully to file: " + outputFile);
            } else {
                System.err.println("Error exporting namespace. Exit code: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

# application.properties
aerospike.backup.directory=/path/to/backup/directory
