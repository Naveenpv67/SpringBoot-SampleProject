/**
 * Endpoint for adding consent for PIN change of Senior Citizen Debit Card.
 *
 * This API allows Senior Citizens to provide their consent for a physical PIN change request.
 *
 * @param addConsentPinChange The request body containing the consent information for PIN change.
 * @return ResponseEntity with an AddConsentResponse containing the result of the consent operation.
 * @throws Exception If an error occurs during the consent process.
 *
 * @apiNote This endpoint is specifically designed for Senior Citizens' Debit Cards.
 * @apiNote To use this API, ensure that the request is in JSON format and the response will be in JSON.
 * @apiNote The response contains information about the success or failure of the consent request.
 *
 * @author N28876 (Naveen)
 * @version 1.0.0
 * @since 04/18/2023
 */
@PostMapping(path = URLConstants.PIN_CHANGE_ADD_CONSENT_DEBIT_CARDS,
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
public ResponseEntity<AddConsentResponse> pinChangeAddConsent(@RequestBody AddConsentPinChange addConsentPinChange) throws Exception {
    validateRequestBodyPinChangeAddConsent(addConsentPinChange);

    AddConsentResponse addConsentResponse = entityFactory.getFactory(EntityCode.RESPONSE_FACTORY)
        .getEntity(EntityCode.ADD_CONSENT_RESPONSE.toString());

    return addConsentResponse;
}
