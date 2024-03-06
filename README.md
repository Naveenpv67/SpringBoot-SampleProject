@PutMapping("/update/{merchantId}")
    public String updateMerchantDetails(
            @PathVariable String merchantId,
            @RequestParam(required = false) String brandName,
            @RequestParam(required = false) String terminalId,
            @RequestParam(required = false) List<String> services) {

        Map<String, Object> updatedDetails = new HashMap<>();
        if (brandName != null) updatedDetails.put("brandName", brandName);
        if (terminalId != null) updatedDetails.put("terminalId", terminalId);
        if (services != null) updatedDetails.put("services", services);

        return merchantService.updateMerchantDetails(merchantId, updatedDetails);
    }



@DeleteMapping("/delete/{merchantId}")
    public String deleteMerchant(@PathVariable String merchantId) {
        return merchantService.deleteMerchant(merchantId);
    }


    public String deleteMerchant(String merchantId) {
        Iterator<Map<String, Object>> iterator = merchantServicesData.iterator();
        while (iterator.hasNext()) {
            Map<String, Object> merchant = iterator.next();
            if (merchant.get("merchantId").equals(merchantId)) {
                iterator.remove();
                return "Merchant deleted successfully";
            }
        }
        return "Merchant not found for the given merchantId";
    }

public String updateMerchantDetails(String merchantId, Map<String, Object> updatedDetails) {
        for (Map<String, Object> merchant : merchantServicesData) {
            if (merchant.get("merchantId").equals(merchantId)) {
                // Update the merchant details based on the keys provided in updatedDetails
                merchant.putAll(updatedDetails);
                return "Merchant details updated successfully";
            }
        }
        return "Merchant not found for the given merchantId";
    }



    
