I have a request-fetch API that initiates a payment process. In its response, I receive specific data required for the next steps in the flow. I want to create a reusable CommonCacheDTO to store only the relevant parts of this response, not the entire payload.

🔧 Tech Setup:
I'm already using an Aerospike utility class to manage caching.

I’ll be providing the complete service method for you to integrate this caching logic.

✅ Data to Cache:
tpv → an array of my custom class TPV[]

successUrl → String

failureUrl → String

Additional support for Map<String, Object> or other user-defined types if needed

🎯 Requirement:
Build and populate CommonCacheDTO with only the above fields

Store the DTO using Aerospike

Ensure this data is accessible across other parts of the payment flow

The goal is clean, efficient reuse of select data from the initial response, without caching the entire response object.
