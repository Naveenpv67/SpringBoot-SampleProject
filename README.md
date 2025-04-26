import org.springframework.util.StringUtils;
import java.util.Optional;
import java.util.function.Consumer;

private void setResponseErrorDetailsIfEmpty(PaymentResponseTransaction paymentResponseTransaction,
                                            String nbblErrorMessage, String nbblErrorCode) {
    if (paymentResponseTransaction == null) return;

    setIfAbsent(paymentResponseTransaction.getErrorMessage(), nbblErrorMessage, paymentResponseTransaction::setErrorMessage);
    setIfAbsent(paymentResponseTransaction.getErrorCode(), nbblErrorCode, paymentResponseTransaction::setErrorCode);
}

private void setIfAbsent(String currentValue, String newValue, Consumer<String> setter) {
    Optional.ofNullable(currentValue)
            .filter(StringUtils::hasText)
            .or(() -> Optional.ofNullable(newValue).filter(StringUtils::hasText))
            .ifPresent(setter);
}
