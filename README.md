import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class PaymentService {

    private final YamlConfig yamlConfig;

    public PaymentService(YamlConfig yamlConfig) {
        this.yamlConfig = yamlConfig;
    }

    public CompletableFuture<Void> buildRequestMapTemp(PaymentRequestDTO paymentRequestDTO, 
                                                       PaymentRequest request, 
                                                       ValidateOTPProjection otpProjection, 
                                                       PayTxnMasterTable payTxnMasterTable) {

        return CompletableFuture.runAsync(() -> {
            PayRequestTransaction payRequestTransaction = new PayRequestTransaction();
            request.setVarMobNo(paymentRequestDTO.getMobileNo());

            Device device = payRequestTransaction.getDevice();
            if (device == null) {
                device = new Device();
                payRequestTransaction.setDevice(device);
            }

            // Process Device Tags using Parallel Stream
            if (paymentRequestDTO.getDevicetag() != null) {
                List<PayRequestTransaction.Tag> tags = paymentRequestDTO.getDevicetag()
                    .parallelStream()
                    .map(dtoTag -> {
                        PayRequestTransaction.Tag tag = new PayRequestTransaction.Tag();
                        tag.setName(dtoTag.getName());  // ✅ Setting Name

                        // Assign Value based on Tag Name
                        switch (dtoTag.getName()) {
                            case "OS":
                                tag.setValue(dtoTag.getValue() == null || dtoTag.getValue().isEmpty() 
                                    ? yamlConfig.getOs() : dtoTag.getValue());
                                break;
                            case "APP":
                                tag.setValue(dtoTag.getValue() == null || dtoTag.getValue().isEmpty() 
                                    ? yamlConfig.getApp() : dtoTag.getValue());
                                break;
                            case "IP":
                                tag.setValue(dtoTag.getValue() == null || dtoTag.getValue().isEmpty() 
                                    ? yamlConfig.getIp() : dtoTag.getValue());
                                break;
                            case "ID":
                                tag.setValue(dtoTag.getValue() == null || dtoTag.getValue().isEmpty() 
                                    ? yamlConfig.getId() : dtoTag.getValue());
                                break;
                            case "BROWSER":
                                tag.setValue(dtoTag.getValue() == null || dtoTag.getValue().isEmpty() 
                                    ? yamlConfig.getBrowser() : dtoTag.getValue());
                                break;
                            default:
                                return null; // Skip invalid tags
                        }

                        return tag;
                    })
                    .filter(tag -> tag != null) // Remove null values
                    .collect(Collectors.toList());

                device.setTag(tags.toArray(new PayRequestTransaction.Tag[0]));
            }

            device.setMobile(request.getVarMobNo());
        });
    }
}
