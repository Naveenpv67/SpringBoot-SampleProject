/**
 * This is a Response class for the Permanent Blocking of Debit Card REST API.
 * It represents the response structure for the permanent blocking operation of a debit card.
 *
 * @author [Your Name]
 * @version [Version Number]
 * @since [Date]
 */
@Data
public class PermanentBlockDebitCardResponseDTO extends AResponse {
    /**
     * The status of the response, including error codes and reply text.
     */
    public Status status;

    /**
     * The maintenance type associated with the response.
     */
    public String maintenanceType;

    /**
     * The configuration version ID for the response.
     */
    public String configVersionId;

    /**
     * Additional response information in string format.
     */
    private ResponseString responseString;
}
