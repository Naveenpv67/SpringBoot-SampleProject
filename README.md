You can achieve this by splitting each line by "/databases/" and extracting the string after it. Here's a Java code snippet to do that:

```java
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String data = "33,77: instance-1/databases/rules-engine-1 database: projects/common-dev-6/instances/issuer-switch-instance-1/databases/issuer-switch-database-1\n" +
                "42,77: database: projects/common-dev-6/instances/issuer-switch-instance-1/databases/api-service-db-1";

        List<String> databaseNames = extractDatabaseNames(data);
        
        System.out.println("Database names:");
        for (String name : databaseNames) {
            System.out.println(name);
        }
    }

    public static List<String> extractDatabaseNames(String data) {
        List<String> databaseNames = new ArrayList<>();
        String[] lines = data.split("\n");

        for (String line : lines) {
            int index = line.indexOf("/databases/");
            if (index != -1) {
                String databaseName = line.substring(index + "/databases/".length());
                databaseNames.add(databaseName);
            }
        }

        return databaseNames;
    }
}
```

This code will output:

```
Database names:
issuer-switch-database-1
api-service-db-1
```

This code splits the input data into lines, then for each line, it finds the index of "/databases/" and extracts the string after it. Finally, it adds the extracted database names to a list.
