app:
  cache:
    ttls:
      # Default TTL for any cache not specifically defined (in seconds)
      default-expiry: 3600
      # Specific TTLs for different payment flows
      req-fetch: 900
      otp-validation: 300
      payment-status: 600
      user-profile: 86400
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.cache.ttls")
public class CacheTtlConfig {

    private int defaultExpiry;
    private int reqFetch;
    private int otpValidation;
    private int paymentStatus;
    private int userProfile;
}

 private CacheTtlConfig ttlConfig;

app:
  cache:
    ttls:
      # Format: ${ENVIRONMENT_VARIABLE_NAME:DEFAULT_VALUE}
      # All values are in Seconds
      
      # Default TTL for any general cache
      default-expiry: ${CACHE_TTL_DEFAULT:3600}
      
      # Specific TTLs for Payment Flow sets
      req-fetch: ${CACHE_TTL_REQ_FETCH:900}
      otp-validation: ${CACHE_TTL_OTP_VALIDATION:300}
      payment-status: ${CACHE_TTL_PAYMENT_STATUS:600}
      user-profile: ${CACHE_TTL_USER_PROFILE:86400}

