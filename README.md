import java.lang.reflect.Field;

private TransactionLoggingRequest populateNullString(TransactionLoggingRequest transactionRequest) {
    if (transactionRequest == null) {
        return null; // Handle the case where the object is null
    }

    // Get all fields of the TransactionLoggingRequest class
    Field[] fields = transactionRequest.getClass().getDeclaredFields();

    // Iterate over each field
    for (Field field : fields) {
        // Set the field accessible, allowing to modify private fields
        field.setAccessible(true);
        
        try {
            // Set the value of the field to "null" (null string)
            field.set(transactionRequest, "null");
        } catch (IllegalAccessException e) {
            e.printStackTrace(); // Handle the exception accordingly
        }
    }

    return transactionRequest;
}
