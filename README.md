/**
 * Data class representing a request for permanently blocking a debit card within the Debit Card Management System.
 *
 * This class encapsulates all the necessary information required to initiate the permanent block request for a debit card.
 *
 * @author N20876 (Naveen)
 * @version 1.0.0
 * @since 16/04/2023
 */
@Getter
@Setter
public class PermanentBlockDebitCardRequest extends ARequest {
    private static final long serialVersionUID = 1L;
    
    /**
     * The session context associated with the request.
     */
    private SessionContext sessionContext;
    
    /**
     * The request payload containing information for debit card hotlisting.
     */
    private DebitCardHotlistingRequestDTO debitCardHotlistingRequestDTO;
}



/**
 * Data class representing a Debit Card Hotlisting Request.
 *
 * This class encapsulates a request string for debit card hotlisting.
 *
 * @author [Author's Name]
 * @version [Version Number]
 * @since [Date]
 */
public class DebitCardHotlistingRequestDTO {
    @JsonProperty("requestString")
    private DebitCardHotlistingRequestString requestString;
}




/**
 * Data class representing a request or response for DCMS (Debit Card Management System) services.
 *
 * This class serves as a generic structure for interacting with various DCMS services. It may contain
 * common fields required for service requests or responses within the DCMS ecosystem.
 *
 * @author [Author's Name]
 * @version [Version Number]
 * @since [Date]
 */
public class DCMSServices {
    public Header header;
    public Body body;

    public class Header {
        // Fields and methods specific to the Header class
    }

    public class Body {
        public SrvReq srvReq;

        public class SrvReq {
            private ReIssueDCReq reissueDCReq;
            private ReIssueDBCard reIssueDBCard;
            private CardHotlistReq cardHotlistReq;

            // Fields and methods specific to the SrvReq class
        }
    }
}



/**
 * Data class representing a request for hotlisting a debit card.
 *
 * This class contains information about the card number, reason, and remarks for hotlisting.
 *
 * @author [Author's Name]
 * @version [Version Number]
 * @since [Date]
 */
public class CardHotlistReq {
    private Long cardNo;
    private String reason;
    private String remarks;
}


