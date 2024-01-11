@Override
public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    ServerHttpRequest request = exchange.getRequest();
    logRequestDetails(request);
    return chain.filter(exchange);
}

private static final String CHANNEL_ID_HEADER = "ChannelId";
private static final String MBR_CHANNEL_VALUE = "BB54";
private static final String GIGA_CHANNEL_VALUE = "BB55";
private static final String MBR_TRANSACTION_ID = "MBR-TXN-ID";
private static final String GIGA_TRANSACTION_ID = "GIGA-TXN-ID";

private void logRequestDetails(ServerHttpRequest request) {
    HttpHeaders headers = request.getHeaders();
    String channelIdValue = headers.getFirst(CHANNEL_ID_HEADER);

    if (MBR_CHANNEL_VALUE.equals(channelIdValue) || GIGA_CHANNEL_VALUE.equals(channelIdValue)) {
        String transactionId = MBR_CHANNEL_VALUE.equals(channelIdValue) ? MBR_TRANSACTION_ID : GIGA_TRANSACTION_ID;
        request = request.mutate().header("TransactionId", transactionId).build();
    }

    // Log only essential information
    logger.info("Initiated gateway processing. ChannelId: {}", channelIdValue);
}
