import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class TransactionNarrativeHelper {

    @Value("${transaction.merchant-name-max-length}")
    private int merchantNameMaxLength;

    @Value("${transaction.narrative-patterns.debit}")
    private String debitPattern;

    @Value("${transaction.narrative-patterns.reversal}")
    private String reversalPattern;

    /**
     * Generic builder method for transaction narrative
     */
    public String buildTransactionNarrative(String pattern, String referenceId, String merchantName, String txnId) {
        String cleanedMerchantName = removeSpacesAndTrim(merchantName, merchantNameMaxLength);

        return String.format(pattern,
                StringUtils.hasText(referenceId) ? referenceId : "",
                cleanedMerchantName,
                StringUtils.hasText(txnId) ? txnId : "");
    }

    /**
     * Removes spaces and trims string to maxLength.
     */
    private String removeSpacesAndTrim(String input, int maxLength) {
        if (input == null) {
            return "";
        }
        String noSpaces = input.replaceAll("\\s+", "");
        return noSpaces.length() > maxLength ? noSpaces.substring(0, maxLength) : noSpaces;
    }

    // Expose pattern getters if needed
    public String getDebitPattern() {
        return debitPattern;
    }

    public String getReversalPattern() {
        return reversalPattern;
    }
}



===

// For debit
String debitNarrative = narrativeHelper.buildTransactionNarrative(
        narrativeHelper.getDebitPattern(),
        request.getReferenceId(),
        request.getPayRequestTransaction().getMerchantName(),
        request.getPayRequestTransaction().getTransaction().getTxnID()
);

// For reversal
String reversalNarrative = narrativeHelper.buildTransactionNarrative(
        narrativeHelper.getReversalPattern(),
        request.getReferenceId(),
        request.getPayRequestTransaction().getMerchantName(),
        request.getPayRequestTransaction().getTransaction().getTxnID()
);



====

transaction:
  merchant-name-max-length: 40

  narrative-patterns:
    debit: "%s/%s/%s"
    reversal: "%s-%s-%s"
