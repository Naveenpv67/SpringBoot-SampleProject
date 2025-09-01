flowchart TD
    A[Start: IBMB receives payment request from client] --> B[IBMB calls User Status API to CS to check TPT activation time]
    B --> C{Has 24-hr wait passed?}
    C -- No --> D[Return Error to client: 24-hr TPT activation not completed]
    C -- Yes --> E{Is activation time < 48 hrs?}
    E -- Yes --> F{Is transaction amount <= 50,000?}
    F -- No --> G[Return Error to client: Max 50k transfer in first 48 hrs exceeded]
    F -- Yes --> H[IBMB calls Available Limit API to CS]
    E -- No --> H
    H --> I{Is limit available?}
    I -- No --> J[Return Limit Breach Error to client]
    I -- Yes --> K[IBMB calls Hold API to CS to reserve amount]
    K --> L{Hold Successful?}
    L -- No --> M[Return Hold Failure Error to client]
    L -- Yes --> N[IBMB calls Actual Debit API to Flexcube]
    N --> O{Debit Successful?}
    O -- No --> Z1[IBMB calls Release API to CS]
    Z1 --> Z2[Return Debit Failure to client]
    O -- Yes --> P[IBMB calls NBBL third party for ACK]
    P --> Q{NBBL ACK Successful?}
    Q -- No --> Z3[IBMB calls Release API to CS]
    Z3 --> Z4[Return NBBL Failure to client]
    Q -- Yes --> R[Poll for Asynchronous Response from NBBL]
    R --> S{Is Async Response Success?}
    S -- No --> Z5[IBMB calls Release API to CS]
    Z5 --> Z6[Return Txn Failure to client]
    S -- Yes --> T[IBMB calls Confirm API to CS]
    T --> U[Return Success to client]
