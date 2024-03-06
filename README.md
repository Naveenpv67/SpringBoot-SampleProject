import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class MerchantService {

    private final List<Map<String, Object>> merchantServicesData = new ArrayList<>();

    public String onboardMerchant(Map<String, Object> merchantDetails) {
        String merchantId = UUID.randomUUID().toString();
        Map<String, Object> merchant = new HashMap<>();
        merchant.put("merchantId", merchantId);
        merchant.putAll(merchantDetails);
        merchantServicesData.add(merchant);
        return "Success";
    }

    public List<Map<String, Object>> getMerchants() {
        return new ArrayList<>(merchantServicesData);
    }

    public Map<String, Object> getMerchantDetails(String merchantId) {
        for (Map<String, Object> merchant : merchantServicesData) {
            if (merchant.get("merchantId").equals(merchantId)) {
                // Found the merchant with the specified merchantId
                return merchant;
            }
        }
        // If merchantId not found, return an empty map (you can handle this differently based on your requirements)
        return new HashMap<>();
    }
}

