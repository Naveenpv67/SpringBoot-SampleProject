/**
 * POJO class representing a request or response for DCMS (Debit Card Management System) services.
 * This class serves as a generic structure for interacting with various DCMS services.
 * It may contain common fields required for service requests or responses within the DCMS ecosystem.
 *
 * @author N28876 (Naveen)
 * @version 1.0.0
 * @since 16/10/2023
 */
@Getter
@Setter
public class SryRea {
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
}
