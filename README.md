/**
 * Data class representing a request or response for DCMS (Debit Card Management System) Services.
 * This class encapsulates the common structure for interacting with DCMS services.
 *
 * @author M26256 (Alvary Mani Teja)
 * @since 10/16/2023
 */
public class DCMSServices {

    /** Header information for the request or response. */
    public Header header;

    /** Body content for the request or response. */
    public Body body;

    /**
     * Data class representing the header information for DCMS services.
     * This class contains details specific to the header of DCMS requests or responses.
     */
    public class Header {
        // Define fields specific to the header, if any.
    }

    /**
     * Data class representing the body content for DCMS services.
     * This class holds the actual data relevant to the request or response body.
     */
    public class Body {
        // Define fields specific to the body, if any.
    }
}
