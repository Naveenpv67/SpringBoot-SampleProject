## 1. Transaction Hold Table

```sql
CREATE TABLE tpt_transaction_hold (
    id bigserial not null primary key,
    request_id           VARCHAR(64)  NOT NULL,
    channel_id           VARCHAR(16)  NOT NULL,
    transaction_id       VARCHAR(64)  NOT NULL,
    customer_id          VARCHAR(16)  NOT NULL,
    from_account         VARCHAR(32)  NOT NULL,
    channel              VARCHAR(8)   NOT NULL,
    mode                 VARCHAR(8)   NOT NULL,
    amount               DECIMAL(16,2) NOT NULL,
    request_payload      TEXT         NOT NULL,
    response_data        TEXT         NOT NULL,
    status               VARCHAR(8)   NOT NULL,    -- 'SUCCESS' or 'FAILED'
    errorcode            VARCHAR(16),
    errormessage         VARCHAR(1000),
);
```

---

## 2. Transaction Confirm Table

```sql
CREATE TABLE tpt_transaction_confirm (
    id bigserial not null primary key,
    request_id           VARCHAR(64)  NOT NULL,
    channel_id           VARCHAR(16)  NOT NULL,
    transaction_id       VARCHAR(64)  NOT NULL,
    customer_id          VARCHAR(64)  NOT NULL,
    id_txn               VARCHAR(64),
    ref_no               VARCHAR(32),
    request_payload      TEXT         NOT NULL,
    response_data        TEXT         NOT NULL,
    status               VARCHAR(8)   NOT NULL,
    errorcode            VARCHAR(16),
    errormessage         VARCHAR(1000),
);
```

---

## 3. Transaction Release Table

```sql
CREATE TABLE tpt_transaction_release (
    id bigserial not null primary key,
    request_id           VARCHAR(64)  NOT NULL,
    channel_id           VARCHAR(16)  NOT NULL,
    token                VARCHAR(64)  NOT NULL,
    transaction_id       VARCHAR(64)  NOT NULL,
    customer_id          VARCHAR(64)  NOT NULL,
    request_payload      TEXT         NOT NULL,
    response_data        TEXT         NOT NULL,
    status               VARCHAR(8)   NOT NULL,
    errorcode            VARCHAR(16),
    errormessage         VARCHAR(1000),
);
```

---

## 4. Get User Status Table

```sql
CREATE TABLE tpt_user_status (
    id bigserial not null primary key,
    request_id               VARCHAR(64)  NOT NULL,
    channel_id               VARCHAR(16)  NOT NULL,
    platform                 VARCHAR(16)  NOT NULL,
    channel                  VARCHAR(8)   NOT NULL,
    login_id                 VARCHAR(16)  NOT NULL,
    identifiers              VARCHAR(64)  NOT NULL, -- Comma separated if multiple
    request_payload          TEXT         NOT NULL,
    response_data            TEXT         NOT NULL,
    status                   VARCHAR(8)   NOT NULL,
    errorcode                VARCHAR(16),
    errormessage             VARCHAR(1000),
    tpt_registration_status  VARCHAR(16),
    tpt_activation_time      BIGINT,
);
```
