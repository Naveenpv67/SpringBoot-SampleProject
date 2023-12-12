import com.aerospike.client.AerospikeClient;
import com.aerospike.client.cluster.Node;
import com.aerospike.client.cluster.Partition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AerospikeRWService {

    @Autowired
    private AerospikeClient aerospikeClient;

    @Value("${aerospike.host}")
    private String aerospikeHost;

    @Value("${aerospike.port}")
    private int aerospikePort;

    public List<String> getAllDatasets(String namespace) {
        try {
            // Query Aerospike for all set names in the specified namespace
            Node[] nodes = aerospikeClient.getNodes();
            Set<String> datasetNames = new HashSet<>();
            for (Node node : nodes) {
                Partition[] partitions = node.getPartitions();
                for (Partition partition : partitions) {
                    Set<String> setNames = partition.getSets();
                    datasetNames.addAll(setNames);
                }
            }

            return new ArrayList<>(datasetNames);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public String deleteSet(String namespace, String setName) {
        try {
            // Delete the specified set
            aerospikeClient.truncate(null, namespace, setName, null);

            return "Set '" + setName + "' deleted successfully.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error deleting set '" + setName + "': " + e.getMessage();
        }
    }

    public String bulkDeleteSets(String namespace) {
        try {
            // Delete all sets in the specified namespace
            Node[] nodes = aerospikeClient.getNodes();
            for (Node node : nodes) {
                Partition[] partitions = node.getPartitions();
                for (Partition partition : partitions) {
                    Set<String> setNames = partition.getSets();
                    for (String setName : setNames) {
                        aerospikeClient.truncate(null, namespace, setName, null);
                    }
                }
            }

            return "All sets in namespace '" + namespace + "' deleted successfully.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error deleting sets in namespace '" + namespace + "': " + e.getMessage();
        }
    }
}
