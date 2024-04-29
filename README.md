// Check if default fields are null or empty and populate with default values
        Map<String, String> defaultFieldsMap = transactionLogProperties.getDefaultFieldsMap();
        for (Map.Entry<String, String> entry : defaultFieldsMap.entrySet()) {
            String key = entry.getKey();
            String defaultValue = entry.getValue();
            if (tmiMap.get(key) == null || tmiMap.get(key).isEmpty()) {
                tmiMap.put(key, defaultValue);
            }
        }


   // Eliminate unnecessary fields from tmiMap
        List<String> fieldsToEliminate = transactionLogProperties.getFieldsToEliminate();
        for (String field : fieldsToEliminate) {
            tmiMap.remove(field);
        }



// Hash specified field values in tmiMap
        List<String> fieldsToHash = transactionLogProperties.getFieldsToHash();
        for (String field : fieldsToHash) {
            if (tmiMap.containsKey(field)) {
                String valueToHash = tmiMap.get(field);
                String hashedValue = hashValue(valueToHash);
                tmiMap.put(field, hashedValue);
            }
        }

        


// Method to hash a value using SHA-512 algorithm
    private String hashValue(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            byte[] hashBytes = digest.digest(value.getBytes());
            StringBuilder stringBuilder = new StringBuilder();
            for (byte b : hashBytes) {
                stringBuilder.append(String.format("%02x", b));
            }
            return stringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            // Handle exception
            return null;
        }
    }
        
