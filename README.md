CREATE TABLE tpt_limit_audit (
    ref_id VARCHAR(60) NOT NULL,
    request_type VARCHAR(50) NOT NULL,
    hash_cust_id VARCHAR(64),
    channel VARCHAR(50),
    channel_id VARCHAR(58),
    tpt_limit_value VARCHAR(100),
    tpt_limit_result VARCHAR(50),
    tpt_limit_ts TIMESTAMP,
    request_payload JSONB,
    response_data JSONB,
    error_code VARCHAR(20),
    error_message VARCHAR(255),
    PRIMARY KEY (ref_id, request_type)
);
