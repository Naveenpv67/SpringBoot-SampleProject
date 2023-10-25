/**
 * POJO class representing a request for hotlisting a debit card.
 * This class contains information about the card number, reason, and remarks for hotlisting.
 *
 * @author N20076 (Naveen)
 * @version 1.0.0
 * @since 16/10/2023
 */
@Data
public class CardHotlistReq {
    /**
     * The card number of the debit card to be hotlisted.
     */
    private Long cardNo;

    /**
     * The reason for hotlisting the debit card, e.g., loss or theft.
     */
    private String reason;

    /**
     * Additional remarks or comments related to the hotlisting request.
     */
    private String remarks;
}
