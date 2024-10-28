package com.example.paytransaction.dto;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class PaymentRequest {

    private String userId;
    private String customerId;
    private String accountNo;
    private String remitAccountNo;
    private PayRequestTransaction payRequestTransaction;

    @Data
    public static class PayRequestTransaction {
        private String refNo;
        private String nbblMerchantId;
        private String merchantName;
        private String mcc;
        private String issuerBankId;
        private String transcationTs;
        private String initiationMode;
        private String result;
        private String errCode;
        private String errReason;
        private BigDecimal amount;
        private String currency;
        private String netBType;
        private String remarks;
        private Device device;
        private OriginatorData originator;
        private Transaction transaction;
        private List<Map<String, Object>> amountBreakUp;
        private List<Map<String, Object>> additionalInfo;

        @Data
        public static class OriginatorData {
            private String name;
            private String type;
            private Address address;

            @Data
            public static class Address {
                private String location;
                private String city;
                private String state;
                private String country;
                private String pincode;
            }
        }

        @Data
        public static class Transaction {
            private String txnID;
            private String refID;
            private String note;
            private String initiationMode;
        }

        @Data
        public static class Device {
            private String mobile;
            private Tag[] tag;
        }

        @Data
        public static class Tag {
            private String name;
            private String value;
        }
    }
}
