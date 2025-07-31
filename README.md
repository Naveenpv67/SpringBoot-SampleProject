
## **Centralized Conversation Maintenance App – Context Summary**

### **Purpose**

To create a centralized system and database schema for storing, managing, and retrieving chatbot conversation data from multiple banking apps (e.g., Payzapp, SBI Yono, ICICI Bank). This enables banks to maintain, analyze, and query all chatbot interactions for customers, sessions, and channels (apps) in one place.

### **Key Requirements**

- **Store**: Conversation data (user requests and chatbot responses) from various source apps/channels.
- **Read/Query**: Efficiently retrieve conversation histories by customer, session, or channel.
- **Support**: Adding new apps/channels dynamically as banks adopt new platforms or interfaces.

### **Main Entities & Relationships**

1. **Customer**
   - Identified by `customer_id`
   - Represents a bank customer interacting via any chatbot

2. **Channel (App/Platform)**
   - Identified by `channel_id`
   - Stores metadata: channel name (e.g., "SBI Yono"), type (app/web/SDK/etc.)

3. **Session**
   - Identified by `session_id`
   - Ties a customer to a specific log-in session on a particular channel
   - Stores: start/end time, device info, IP, etc.

4. **Conversation**
   - Each entry tied to a session, customer, and channel
   - Records input/request text, output/response text, timestamp, and optional metadata

**Relationships:**
- Each customer can have multiple sessions, potentially across multiple channels.
- Each session can have multiple conversation turns (request/response pairs).
- Each conversation is associated with one session and, transitively, one customer and one channel.

### **Example DB Schema (Can be Used for SQL or NoSQL)**

- **customers**
    - customer_id (PK), created_at
- **channels**
    - channel_id (PK), channel_name, channel_type, created_at, metadata
- **sessions**
    - session_id (PK), customer_id (FK), channel_id (FK), start_time, end_time, device_info, ip_address, metadata
- **conversations**
    - conversation_id (PK), session_id (FK), input_text, response_text, timestamp, metadata

#### **Sample Document (NoSQL style):**
```json
{
  "customer_id": "cust123",
  "sessions": [{
      "session_id": "sess456",
      "channel_id": "ch789",
      "device_info": "iPhone 14 Pro, iOS 17.2",
      "conversations": [
        {
          "conversation_id": "conv001",
          "input_text": "Check account balance",
          "response_text": "Your balance is $1000",
          "timestamp": "2025-07-31T10:06:00Z"
        }
      ]
  }]
}
```

### **API Design (for Use by All Integrating Chatbots/Apps)**

#### **1. Save Conversation API**
- Accepts: `customer_id`, `session_id`, `input_text`, `response_text`, `channel` (with full info if new), `timestamp`
- Logic:
    - If the channel is new, its details (name, type, etc.) must be provided, and the system adds it to the channels table/collection automatically (auto-onboard).
    - If the channel exists, just save the session and conversation using the known channel_id.
- Example Payload:
    ```json
    {
      "customer_id": "cust123",
      "session_id": "sess456",
      "input_text": "Check account balance",
      "response_text": "Your balance is $1000",
      "channel": {
        "channel_id": "ch789",
        "channel_name": "Payzapp",
        "channel_type": "app"
      },
      "timestamp": "2025-07-31T10:06:00Z"
    }
    ```

#### **2. Read Conversation API**
- Query by `customer_id` to get all conversations (across all channels and sessions)
- Or by both `customer_id` and `session_id` for narrow filtering
- Optional: Query for all sessions on a channel, or aggregate by channel

### **Design & Operational Notes**

- **Channels should be registered/onboarded** before use, but for system flexibility, the Save API is designed to accept full channel info and auto-register a channel on first encounter (“auto-onboarding”) if it isn’t already present.
- **Metadata fields** (on sessions, conversations, and channels) support extensibility (e.g., locale, bot version, device, etc.).
- **Scalable/no rigid schema:** Especially suitable for NoSQL (e.g., Aerospike, MongoDB) and can also be used in SQL with references.
- **Index customer_id, session_id, channel_id** for efficient querying and analytics.

### **Example Use Case Scenarios**

- Get all conversations for a customer across all channels.
- Get session-wise chat history for a customer in a specific app (channel).
- Aggregate/report on chatbot usage and performance per channel/app (e.g., for analytics dashboards).

