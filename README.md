Hi Front End Team,
For sdk,we are planning to make reqinit api as Asynchronous using SSE as we have already discussed very earlier
please find the below details for the same.
1. Front end will call reqinit api and receive the acknowledgment response immediately (success/failure).
2. If acknowledgment response is failure, front end will show the failure message to user and will not call any further api.
3. If acknowledgment response is success, front end will call watch-status api with referenceId to receive the actual reqinit transaction response via SSE stream.
API Details:
1. ReqTxnInit API (Asynchronous) 
   - Request remains same as before
   - Response Body: Acknowledgment response with referenceId,clientRefId, error details and status (success/failure)  
2. Watch-Status API (SSE Stream)
   - Method: GET
   - Endpoint: /api/acquirer/watch-status/{referenceId}
   - Response: SSE stream that sends the actual reqinit transaction response when available.
