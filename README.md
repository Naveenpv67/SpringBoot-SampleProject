flowchart TD
    A[Start: IBMB receives payment request from client] --> B[IBMB calls User Status API to CS to check TPT activation time]
    B --> C{Has 24-hr wait passed?}
    C -- No --> D[Return Error to client: 24-hr TPT activation not completed]
    C -- Yes --> E{Is activation time < 48 hrs?}
    E -- Yes --> F{Is transaction amount <= 50,000?}
    F -- No --> G[Return Error to client: Max 50k transfer in first 48 hrs exceeded]
    F -- Yes --> H[IBMB calls Available Limit API to CS]
    E -- No --> H[IBMB calls Available Limit API to CS]
    H --> I{Is limit available?}
    I -- No --> J[Return Limit Breach Error to client]
    I -- Yes --> K[IBMB calls Hold API to CS to reserve amount]
    K --> L{Hold Successful?}
    L -- No --> M[Return Hold Failure Error to client]
    L -- Yes --> N[IBMB calls Actual Debit API to Flexcube]
    N --> O{Debit Successful?}
    O -- Yes --> P[IBMB calls Confirm API to CS]
    P --> Q[Return Success to client]
    O -- No --> R[IBMB calls Release API to CS]
    R --> S[Return Debit Failure to client]
