import com.aerospike.client.AerospikeClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class ExportService {

    @Autowired
    private AerospikeClient aerospikeClient;

    @Value("${aerospike.backup.directory}")
    private String backupDirectory;

    public void exportNamespace(String namespace) {
        String outputFile = backupDirectory + File.separator + "exported_" + namespace + ".asb";
        String[] command = {
                "asbackup",
                "--host", aerospikeClient.getClientPolicy().hosts[0].name,
                "--port", String.valueOf(aerospikeClient.getClientPolicy().hosts[0].port),
                "--namespace", namespace,
                "--output-file", outputFile
        };

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
