private void logResponseDetails(ServerWebExchange exchange) {
        // Extract and log response body (if present)
        exchange.getResponse().beforeCommit(() -> {
            Flux<DataBuffer> body = exchange.getResponse().getBody();
            return DataBufferUtils.join(body)
                    .doOnNext(dataBuffer -> {
                        byte[] content = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(content);
                        logger.info("Response body: {}", new String(content, StandardCharsets.UTF_8));
                        DataBufferUtils.release(dataBuffer);
                    })
                    .then(Mono.empty());
        }).subscribe();
    }
