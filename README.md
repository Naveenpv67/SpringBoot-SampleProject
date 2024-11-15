public class FlexcubeOriginatorInfo {
    private String originatorName;
    private String originatorAddress;
    private String originatorLocation;
    private String originatorCity;
    private String originatorState;
    private String originatorCountry;
    private String originatorPincode;
    private String originatorType;
    private String originatorGeocode;
    private String originatorIp;
    private String originatorDeviceId;
    private String originatorOs;
    private String originatorBrowser;

    // Getters and setters for all fields
}


import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Timestamp;

public void saveFlexcubeOrgInfoTransactionToDatabase(FlexcubeOrgInfoRequestOBPDTO transformedRequest, 
                                                     PaymentRequest paymentRequest, 
                                                     String orgInfoResponseBody, 
                                                     String errorCode, 
                                                     String errorMessage, 
                                                     ResultEnum result, 
                                                     int httpStatus) {
    try {
        // Parse JSON response to FlexcubeOriginatorInfo model
        ObjectMapper objectMapper = new ObjectMapper();
        FlexcubeOriginatorInfo originatorInfo = objectMapper.readValue(orgInfoResponseBody, FlexcubeOriginatorInfo.class);

        // Assuming entity is an instance of your database model
        entity.setOriginatorName(transformedRequest.getOriginatorName());
        entity.setOriginatorAddress(transformedRequest.getOriginatorAddress());
        entity.setOriginatorLocation(transformedRequest.getOriginatorLocation());
        entity.setOriginatorCity(transformedRequest.getOriginatorCity());
        entity.setOriginatorState(transformedRequest.getOriginatorState());
        entity.setOriginatorCountry(transformedRequest.getOriginatorCountry());
        entity.setOriginatorPincode(transformedRequest.getOriginatorPincode());
        entity.setOriginatorType(transformedRequest.getOriginatorType());
        entity.setOriginatorGeocode(transformedRequest.getOriginatorGeocode());
        entity.setOriginatorIp(transformedRequest.getOriginatorIp());
        entity.setOriginatorDeviceId(transformedRequest.getOriginatorDeviceId());
        entity.setOriginatorOs(transformedRequest.getOriginatorOs());
        entity.setOriginatorBrowser(transformedRequest.getOriginatorBrowser());

        // Timestamp Handling
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        entity.setRequestTimestamp(currentTimestamp);

        if (result == ResultEnum.SUCCESS) {
            // Set response timestamp only if successful
            entity.setResponseTimestamp(currentTimestamp);
        }

        // Save entity to database (assuming you have a save method)
        save(entity);

    } catch (Exception e) {
        // Handle exception
        e.printStackTrace();
    }
}
