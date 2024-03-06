import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class MerchantService {

    private final List<Map<String, Object>> merchantServicesData = new ArrayList<>();

    public String onboardMerchant(Map<String, Object> request) {
        String brandName = (String) request.get("brandName");

        // Check if the brandName already exists
        if (isBrandNameExists(brandName)) {
            return "Brand name already exists. Choose a different brand name.";
        }

        String merchantId = UUID.randomUUID().toString();

        Map<String, Object> merchant = new HashMap<>();
        merchant.put("merchantId", merchantId);
        merchant.put("brandName", brandName);
        merchant.put("terminalId", request.get("terminalId"));
        merchant.put("services", request.get("services"));

        merchantServicesData.add(merchant);
        return "Success";
    }

    private boolean isBrandNameExists(String brandName) {
        for (Map<String, Object> merchant : merchantServicesData) {
            String existingBrandName = (String) merchant.get("brandName");
            if (existingBrandName != null && existingBrandName.equalsIgnoreCase(brandName)) {
                return true; // Brand name already exists
            }
        }
        return false; // Brand name does not exist
    }

    // Other methods...

}
