/**
 * Represents a request for the Permanent Blocking of a Debit Card.
 * This model class contains information related to the request for permanently blocking a debit card.
 *
 * @author N20076 (Naveen)
 * @version 1.0.0
 * @since 04/18/2823
 */
@Getter
@Setter
public class ScvReg {
    /**
     * Information related to reissuing a Debit Card.
     */
    private ReIssueDCReq reissueDCReq;

    /**
     * Information related to reissuing a Debit Card.
     */
    private ReIssueDBCard reIssueDBCard;

    /**
     * Information related to hotlisting a Debit Card.
     */
    private CardHotlistReq cardHotlistReq;

    /**
     * A request string associated with the request.
     */
    private RequestString requestString;
}
