

As discussed earlier, we are planning to make the **reqinit API asynchronous** using **SSE**. Below is the flow:

1. Front end calls **reqinit** and gets an immediate **ack response** (success/failure).
2. On **failure**, show the error and stop further processing.
3. On **success**, front end will call **watch-status** with `referenceId` to receive the actual reqinit transaction response via SSE.

**API Details:**

1. **ReqTxnInit (Async)**

   * Request: No change
   * Response: Acknowledgment with `referenceId`, `clientRefId`, status, and error details.

2. **Watch-Status (SSE)**

   * Method: GET
   * Endpoint: `/api/acquirer/watch-status/{referenceId}`
   * Response: SSE stream sending the actual transaction result.
