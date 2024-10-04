package com.example.aerospikedemo.service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.aerospikedemo.utils.AerospikeUtil;

@Service
public class AcquirerService {

    @Autowired
    private AerospikeUtil aerospikeUtil;

    @Autowired
    private WebClient.Builder webClientBuilder;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public CompletableFuture<String> processTransaction(RgInitTxn txn) {
        // Save initial transaction to Aerospike
        aerospikeUtil.addUpdateCache("transactions", txn.getPK(), txn);

        // Save Pramukhi and JustPay requests
        txn.setJspRq("Pramukhi request data");
        txn.setIbRq("JustPay request data");
        aerospikeUtil.addUpdateCache("transactions", txn.getPK(), txn);

        // Call third-party service
        WebClient webClient = webClientBuilder.build();
        CompletableFuture<String> futureResponse = webClient.post()
                .uri("http://ibmb-service/endpoint")
                .bodyValue(txn.getIbRq())
                .retrieve()
                .bodyToMono(String.class)
                .toFuture();

        // Poll the database every 30 milliseconds until the status is completed
        return futureResponse.thenCompose(response -> {
            txn.setIbRs(response);
            aerospikeUtil.addUpdateCache("transactions", txn.getPK(), txn);

            CompletableFuture<String> pollingFuture = new CompletableFuture<>();
            scheduler.scheduleAtFixedRate(() -> {
                RgInitTxn updatedTxn = aerospikeUtil.getRecord("transactions", txn.getPK(), RgInitTxn.class);
                if (updatedTxn != null && "completed".equals(updatedTxn.getIbStatus())) {
                    pollingFuture.complete(updatedTxn.getIbRs());
                }
            }, 0, 30, TimeUnit.MILLISECONDS);

            return pollingFuture;
        });
    }

    public void updateTransaction(RgInitTxn updatedTxn) {
        aerospikeUtil.addUpdateCache("transactions", updatedTxn.getPK(), updatedTxn);
    }
}
