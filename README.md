I want to verify that my database column lengths are compatible with the specifications provided in a third-party TSD document.

The TSD document contains the expected column lengths.

The database schema (SQL CREATE statements) contains the actual column lengths.

For each column, check:

✅ If Database Length ≥ TSD Length → No issue.

❌ If Database Length < TSD Length → Flag as error.

Goal: Identify any mismatches where the database column is too small to store the third-party data.
