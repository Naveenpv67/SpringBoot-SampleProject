import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.aerospike.client.Record;
import com.aerospike.client.policy.Policy;
import com.aerospike.client.query.Filter;
import com.aerospike.client.query.KeyQualifier;
import com.aerospike.client.query.RecordSet;
import com.aerospike.client.query.Statement;
import com.aerospike.client.task.RegisterTask;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AerospikeService {

    private AerospikeClient aerospikeClient;

    public AerospikeService(AerospikeClient aerospikeClient) {
        this.aerospikeClient = aerospikeClient;
    }

    public String getSymmetricKeyForDeviceID(String deviceID) {
        Key key = new Key("namespace", "EncrChanKeys", deviceID);
        Record record = aerospikeClient.get(new Policy(), key, "SymmetricKey", "SymmetricKeyExpiresAt");

        if (record == null || record.bins.isEmpty()) {
            // Handle case where record is not found
            return null;
        }

        String symmetricKey = record.getString("SymmetricKey");
        long expiresAtMillis = record.getLong("SymmetricKeyExpiresAt");

        // Validate symmetric key expiration
        if (System.currentTimeMillis() > expiresAtMillis) {
            // Handle case where symmetric key is expired
            return null;
        }

        return symmetricKey;
    }

    public static void main(String[] args) {
        // Assuming you have an initialized AerospikeClient instance
        AerospikeClient aerospikeClient = new AerospikeClient("localhost", 3000);

        AerospikeService aerospikeService = new AerospikeService(aerospikeClient);

        String deviceID = "yourDeviceID";
        String symmetricKey = aerospikeService.getSymmetricKeyForDeviceID(deviceID);

        if (symmetricKey != null) {
            System.out.println("Symmetric Key: " + symmetricKey);
        } else {
            System.out.println("Symmetric Key not found or expired.");
        }
    }
}
