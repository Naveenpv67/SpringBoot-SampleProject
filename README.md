Certainly! Here is a flow diagram to visualize the process of handling the cache and third-party data retrieval:

1. **Start**: User makes a request to access the DMAT holding statement.
2. **Extract Data**: Extract `Customer ID`, `Account Number`, and `Session Token` from the request.
3. **Check Cache**: Check if the combination of `Customer ID`, `Account Number`, and `Session Token` exists in Aerospike.
    - **Yes**: Return cached data.
    - **No**: Continue to the next step.
4. **Check Existing Cache for Customer ID and Account Number**: 
    - **Yes**: If the combination exists but the session token is different, delete the old cache entry.
    - **No**: Proceed to fetch new data.
5. **Fetch Data**: Call the third-party service to fetch the DMAT holding statement.
6. **Store Data**: Store the new data in Aerospike with the `Customer ID`, `Account Number`, and `Session Token`.
7. **Return Data**: Return the newly fetched data to the user.
8. **End**.

Here's a visual representation of the flow:

```plaintext
                +-------------------------------+
                |           Start               |
                +-------------------------------+
                             |
                             v
                +-------------------------------+
                | Extract Customer ID, Account  |
                | Number, and Session Token     |
                +-------------------------------+
                             |
                             v
                +-------------------------------+
                |  Check if the combination of  |
                |  Customer ID, Account Number, |
                |  and Session Token exists in  |
                |  Aerospike                    |
                +-------------------------------+
                             |
         +-------------------+-------------------+
         |                                       |
         v                                       v
+-----------------------+              +-----------------------------+
| Combination Exists    |              | Combination Does Not Exist  |
| Return Cached Data    |              |                             |
+-----------------------+              +-----------------------------+
                                      | Check Existing Cache for     |
                                      | Customer ID and Account      |
                                      | Number                       |
                                      +-----------------------------+
                                                 |
                                      +----------+----------+
                                      |                     |
                                      v                     v
                          +-------------------+     +---------------------+
                          | Combination Exists|     | Combination Does Not|
                          | Delete Old Entry  |     | Exist               |
                          +-------------------+     +---------------------+
                                                         |
                                                         v
                                      +----------------------------------+
                                      | Fetch Data from Third-Party      |
                                      +----------------------------------+
                                                         |
                                                         v
                                      +----------------------------------+
                                      | Store Data in Aerospike with     |
                                      | Customer ID, Account Number,     |
                                      | and Session Token                |
                                      +----------------------------------+
                                                         |
                                                         v
                                      +----------------------------------+
                                      | Return Fetched Data to User      |
                                      +----------------------------------+
                                                         |
                                                         v
                                      +----------------------------------+
                                      |               End                |
                                      +----------------------------------+
```

This flow ensures that data is fetched and cached appropriately, reducing unnecessary third-party calls and maintaining data freshness.
