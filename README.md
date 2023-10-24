/**
 * Endpoint for permanently blocking a Debit Card.
 *
 * This API allows you to permanently block a debit card, rendering it unusable.
 * Use this API when a debit card needs to be blocked due to loss, theft, or other reasons.
 *
 * @param permanentBlockDebitCardRequest The request body containing the information for blocking the debit card.
 * @return ResponseEntity with a PermanentBlockDebitCardResponse containing the result of the block operation.
 * @throws Exception If an error occurs during the blocking process.
 *
 * @apiNote To use this API, ensure that the request is in JSON format, and the response will be in JSON.
 * @apiNote The response contains information about the success or failure of the permanent block request.
 *
 * @author N20076 (Naveen)
 * @version 1.0.0
 * @since 04/10/2023
 */
@PostMapping(path = URLConstants.PERMANENT_BLOCK_DEBIT_CARD,
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
public ResponseEntity<PermanentBlockDebitCardResponse> permanentBlockDebitCard(
    @RequestBody PermanentBlockDebitCardRequest permanentBlockDebitCardRequest) throws Exception {
    PermanentBlockDebitCardResponse permanentBlockDebitCardResponse = (PermanentBlockDebitCardResponse)
        entityFactory.getFactory(EntityCode.RESPONSE_FACTORY)
            .getEntity(EntityCode.PERMANENT_BLOCK_DEBIT_CARD_RESPONSE.toString());
    return permanentBlockDebitCardResponse;
}
