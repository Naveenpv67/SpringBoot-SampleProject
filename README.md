import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "transaction")
public class TransactionLogProperties {
    private String masterInfo;
    private String mandatoryFields;
    private String defaultFields;
    private String nbFields;
    private String mbFields;
    private String fieldsToEliminate;
    private String fieldsToHash;
    private String fieldsToEncrypt;
    private String aliasName;
    private Map<String, String> masterInfoMap;
    private Map<String, String> defaultFieldsMap;
    private Map<String, String> aliasNameMap;

    // Getters only

    public void setMasterInfo(String masterInfo) {
        this.masterInfo = masterInfo;
        // Split the comma-separated string and populate the masterInfoMap
        String[] masterInfoArray = masterInfo.split(",");
        masterInfoMap = new HashMap<>();
        for (String info : masterInfoArray) {
            masterInfoMap.put(info, null); // Initialize all values to null
        }
    }

    public void setMandatoryFields(String mandatoryFields) {
        this.mandatoryFields = mandatoryFields;
    }

    public void setDefaultFields(String defaultFields) {
        this.defaultFields = defaultFields;
        // Convert defaultFields string to map
        defaultFieldsMap = new HashMap<>();
        if (defaultFields != null && !defaultFields.isEmpty()) {
            String[] defaultFieldsArray = defaultFields.split(",");
            for (String field : defaultFieldsArray) {
                String[] keyValue = field.split(":");
                defaultFieldsMap.put(keyValue[0], keyValue[1]);
            }
        }
    }

    public void setNbFields(String nbFields) {
        this.nbFields = nbFields;
    }

    public void setMbFields(String mbFields) {
        this.mbFields = mbFields;
    }

    public void setFieldsToEliminate(String fieldsToEliminate) {
        this.fieldsToEliminate = fieldsToEliminate;
    }

    public void setFieldsToHash(String fieldsToHash) {
        this.fieldsToHash = fieldsToHash;
    }

    public void setFieldsToEncrypt(String fieldsToEncrypt) {
        this.fieldsToEncrypt = fieldsToEncrypt;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
        // Convert aliasName string to map
        aliasNameMap = new HashMap<>();
        if (aliasName != null && !aliasName.isEmpty()) {
            String[] aliasNameArray = aliasName.split(",");
            for (String alias : aliasNameArray) {
                String[] keyValue = alias.split(":");
                aliasNameMap.put(keyValue[0], keyValue[1]);
            }
        }
    }

    public Map<String, String> getMasterInfoMap() {
        return masterInfoMap;
    }

    public List<String> getMandatoryFieldsList() {
        return Arrays.asList(mandatoryFields.split(","));
    }

    public List<String> getNbFieldsList() {
        return Arrays.asList(nbFields.split(","));
    }

    public List<String> getMbFieldsList() {
        return Arrays.asList(mbFields.split(","));
    }

    public List<String> getFieldsToEliminateList() {
        return Arrays.asList(fieldsToEliminate.split(","));
    }

    public List<String> getFieldsToHashList() {
        return Arrays.asList(fieldsToHash.split(","));
    }

    public List<String> getFieldsToEncryptList() {
        return Arrays.asList(fieldsToEncrypt.split(","));
    }

    public Map<String, String> getDefaultFieldsMap() {
        return defaultFieldsMap;
    }

    public Map<String, String> getAliasNameMap() {
        return aliasNameMap;
    }
}



# Transaction Logs
transaction.master_info=session_id,customer_id,ip_address,location,channel_id,maker_id,maker_timestamp,checker_id,checker_timestamp,device_id,account_number,user_agent
transaction.mandatory_fields=channel_id,maker_id,maker_timestamp,checker_id,checker_timestamp,customer_id
transaction.default_fields=maker_id:system,channel_id:8854,session_id:system,customer_id:admin@el
transaction_nb_fields=browser_version,ip_address
transaction_mb_fields=device_name,device_id
transaction.fields_to_eliminate=proxy_ip_address,server_name
transaction.fields_to_hash=customer_id,device_id
transaction.fields_to_encrypt=account_number,user_agent
transaction.alias_name=proxy_ip_address:pxy_ip_address,device_os_platform:platform

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
public class WasteController {

    @Autowired
    private TransactionLogProperties transactionLogProperties;

    @PostMapping("/log")
    public void logRequest(@RequestBody RequestDTO requestDTO) {
        // Convert RequestDTO object to JSON
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String json = objectMapper.writeValueAsString(requestDTO);
            // Convert JSON to HashMap
            Map<String, Object> mappedValues = objectMapper.readValue(json, HashMap.class);
            // Map the values using alias names if provided
            for (Map.Entry<String, Object> entry : mappedValues.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                // Check if alias name exists in the properties file
                if (transactionLogProperties.getAliasNameMap().containsKey(key)) {
                    String alias = transactionLogProperties.getAliasNameMap().get(key);
                    mappedValues.put(alias, value);
                }
            }
            // Log the mapped values
            logToLibrary(mappedValues);
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exception
        }
    }

    // Method to log values to the library
    private void logToLibrary(Map<String, Object> mappedValues) {
        // Logic to log values using the library jar
    }
}

