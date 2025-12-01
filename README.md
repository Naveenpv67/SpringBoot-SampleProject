Here is a **clean, high-level Jira story description with API details** and **no mention of PaymentService**:

---

### **Jira Story Description (High-Level)**

Implement an **asynchronous request initiation flow** for the acquirer-service using **Server-Sent Events (SSE)**.

The existing **ReqTxnInit API** should be modified to return an immediate **acknowledgment response** (success/failure) after triggering the NBBL payment initiation request.

* On **failure**, the front end stops further processing.
* On **success**, the front end calls a newly introduced **watch-status API** to listen for the final transaction result via SSE.

NBBL will send the actual payment transaction response to our existing **callback API**.
When this response is received, the service should push the final result to the client through the SSE stream exposed by the watch-status API.

---

### **API Details (High-Level)**

**1. ReqTxnInit API (Async)**

* **Method:** POST
* **Function:** Initiates payment request to NBBL and returns immediate acknowledgment.
* **Response:** `referenceId`, `clientRefId`, acknowledgment status (success/failure), error details (if any).

**2. Watch-Status API (SSE Stream)**

* **Method:** GET
* **Endpoint:** `/api/acquirer/watch-status/{referenceId}`
* **Function:** Provides an SSE stream that delivers the actual payment transaction response when available.

**3. Existing Callback API (No change to endpoint)**

* **Function:** Receives final transaction result from NBBL and triggers delivery of this response through the SSE stream to the client.

---

Let me know if you want an acceptance criteria section too.
