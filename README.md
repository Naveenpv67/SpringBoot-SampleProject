import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.lang.reflect.Field;

@JsonSerialize
@JsonInclude(Include.CUSTOM)
public class BankDetailsDTO {

    private String bankCode;
    private String channel;
    private String serviceCode;
    private String transactingPartyCode;
    private String externalReferenceNo;
    private String transactionBranch;
    private String userId;

    // Constructors, getters, setters (as needed)

    // Define a custom method to conditionally exclude fields based on their values
    public boolean shouldExcludeField(Field field) throws IllegalAccessException {
        // Customize the exclusion logic for each field here
        if (field.getName().equals("bankCode")) {
            String value = (String) field.get(this);
            return "ignoreValue".equals(value);
        }
        // Add similar logic for other fields

        // Default behavior (include the field)
        return false;
    }
}
