package com.example.aerospikedemo.service;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.aerospikedemo.model.RgInitTxn;

@RestController
@RequestMapping("/acquirer")
public class AcquirerController {

    @Autowired
    private AcquirerService acquirerService;

    @PostMapping("/process")
    public CompletableFuture<ResponseEntity<String>> processTransaction(@RequestBody RgInitTxn txn) {
        return acquirerService.processTransaction(txn)
                .thenApply(response -> ResponseEntity.ok("Transaction completed with response: " + response));
    }

    @PostMapping("/callback")
    public ResponseEntity<String> callback(@RequestBody RgInitTxn updatedTxn) {
        // Update the transaction in Aerospike with the IBMP response and status
        updatedTxn.setIbStatus("completed");
        acquirerService.updateTransaction(updatedTxn);
        return ResponseEntity.ok("Callback received and processed");
    }
}
