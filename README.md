Here is a refined version of your document:

---

## OBP Proxy Integration Guide

### Step 1: Create a Folder for Proto Files

Create a folder to store your proto files, for example: `src/main/proto`. Add the following `obp_proxy.proto` file to the folder:

```proto
syntax = "proto3";

option java_multiple_files = true;
option java_package = "com.hdfcbank.dematservice.proto";

service MbProxy {
    rpc Call (Request) returns (Response) {}
}

message Request {
    string AppID = 1;
    bytes Body = 2;
    map<string, string> Query = 3;
    map<string, string> Headers = 4;
    string method = 5;
}

message Response {
    bytes Body = 1;
    int32 Status = 2;
    string Error = 3;
}
```

### Step 2: Insert OBP API Details into `admin_config` Table

Insert the OBP API specific details into the `admin_config` table. Below is an example SQL query:

```sql
INSERT INTO test.admin_config (PK, param_key, sub_param_key, config_group, param_value, priority_order)
VALUES ('api_config|DematLandingPageApplicationServiceSpi', 'DematLandingPageApplicationServiceSpi',
'obp', MAP('{"url":"https://10.226.163.7:9444/com.ofss.fc.cz.hdfc.obp.debos.webservice/DematLandingPageApplicationServiceSpi", "timeout": 10, "threshold": 10}'), 1);
```

### Step 3: Create `OBPHelper` Class

Create an `OBPHelper` class to obtain `ObpGrpcUtil` and pass the built OBP request in string format. 

```java
import io.grpc.ManagedChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ObpHelperService {
    private final ManagedChannel managedChannel;

    @Autowired
    public ObpHelperService(ManagedChannel managedChannel) {
        this.managedChannel = managedChannel;
    }

    public ObpGrpcUtil getObpCaller(String stringRequest, String appId, ObpApiType apiType) {
        return ObpGrpcUtil.builder()
                .appId(appId)
                .managedChannel(managedChannel)
                .method(HttpMethod.POST.name())
                .query(Collections.emptyMap())
                .headers(Collections.emptyMap())
                .apiType(apiType)
                .body(stringRequest)
                .build();
    }
}
```

### Step 4: Define Possible API Types

Define the possible API types (e.g., XML, JSON) in an enum:

```java
public enum ObpApiType {
    JSON_API,
    XML_API
}
```

### Step 5: Example Code to Call the OBP Service via gRPC

Here's an example code snippet to make the OBP call via gRPC and get the response:

```java
ObpGrpcUtil obpCaller = obpHelperService.getObpCaller(req, ObpConstants.DEMAT_LANDING_PAGE_APP_SERVICE_SPI, ObpApiType.XML_API);
String xmlModelResponse = obpCaller.call(String.class);
DematLandingPageResponseRoot dematLandingPageResponse = DematLandingPageBuilder.parseResponse(xmlModelResponse);
```

---

This structured format should help make your documentation clear, understandable, and visually appealing.
