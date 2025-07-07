

| 🔢 Step | Operation                                                                 | Type           | Notes                                      |
|--------|---------------------------------------------------------------------------|----------------|--------------------------------------------|
| 1️⃣     | `customerDetailsRepository.existsByCustId(custId)`                       | DB call        | Check 1                                    |
| 2️⃣     | `obpDownstreamCaller.callUserStatusService(requestDTO)`                  | CS call        | 3rd-party service                          |
| 3️⃣     | `customerDetailsRepository.existsByCustId(custId)`                       | DB call        | Duplicate check                             |
| 4️⃣     | `requestFetchTransactionDAO.checkRefIdExist(request.getReferenceId())`   | DB call        | Ref ID check                               |
| 5️⃣     | `rateLimitDAO.cacheRateLimitDetails(hashCustId)`                         | Cache call     | Rate limit cache                           |
| 6️⃣     | `requestFetchTransactionDAO.insertNbblDataIssuerTable()`                 | DB call        | IssuerFetchRequestTable insert             |
| 7️⃣     | `nbblDownstreamCaller.callNBBLReqFetchTxnDetailsV1()`                    | NBBL call      | External API                               |
| 8️⃣     | `obpDownstreamCaller.callLimitService()`                                 | CS call        | Limit inquiry                              |
| 9️⃣     | `getAccountDetails(req)`                                                 | OBP call       | OBP integration                            |
| 🔟     | `accountDetailsResponsesRepository.save(entity)`                         | DB call        | Save account response                      |
| 1️⃣1️⃣    | `requestFetchTransactionDAO.insertNbblResponseDataIssuerTable()`         | DB call        | Save ACK response                          |
| 1️⃣2️⃣    | `solace conuser` + polling                                              | Infra wait     | Solace listener + DB poll loop             |
| 1️⃣3️⃣    | `issuerFetchResponseTableRepository.findByReferenceId(refId)`           | DB call        | Can be skipped (redundant)                |
| 1️⃣4️⃣    | `performTptAndTransactionLimitCheckL1()`                                 | DB call        | Save TPT data                              |
| 1️⃣5️⃣    | `cipherAccountDetails()`                                                | CPU intensive  | Slow—large account list                    |
| 1️⃣6️⃣    | `cacheAccountDetails(...)`                                              | Cache call     | Push to Redis                              |
| 1️⃣7️⃣    | `paymentTransactionMasterDao.savePaymentMasterTable(...)`              | DB call        | Final txn save                             |
