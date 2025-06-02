spring:
  application:
    name: ${SPRING_APPLICATION_NAME}
  yugabyte:
    url: ${YUGABYTE_URL}
    username: ${YUGABYTE_USERNAME}
    password: ${YUGABYTE_PASSWORD}
    driver-class-name: ${YUGABYTE_DRIVER_CLASS_NAME}
  jpa:
    show-sql: ${JPA_SHOW_SQL}

server:
  port: ${SERVER_PORT}
  servlet:
    context-path: ${SERVER_SERVLET_CONTEXT_PATH}

app:
  nbbl:
    enc:
      seckey: ${APP_NBBL_ENC_SECKEY}
    pin:
      seckey: ${APP_NBBL_PIN_SECKEY}

aerospike:
  host: ${AEROSPIKE_HOST}
  username: ${AEROSPIKE_USERNAME}
  paswd: ${AEROSPIKE_PASWD}
  namespacevalue: ${AEROSPIKE_NAMESPACEVALUE}
  ttl: ${AEROSPIKE_TTL}

user:
  session:
    audit:
      enabled: ${USER_SESSION_AUDIT_ENABLED}
      
obp:
  validate-pin-url: ${OBP_VALIDATE_PIN_URL}



====

spring:
  application:
    name: ${SPRING_APPLICATION_NAME}
  yugabyte:
    url: ${YUGABYTE_URL}
    username: ${YUGABYTE_USERNAME}
    password: ${YUGABYTE_PASSWORD}
    driver-class-name: ${YUGABYTE_DRIVER_CLASS_NAME}
  jpa:
    show-sql: ${JPA_SHOW_SQL}

server:
  port: ${SERVER_PORT}
  servlet:
    context-path: ${SERVER_SERVLET_CONTEXT_PATH}

app:
  nbbl:
    enc:
      seckey: ${APP_NBBL_ENC_SECKEY}
    pin:
      seckey: ${APP_NBBL_PIN_SECKEY}

aerospike:
  host: ${AEROSPIKE_HOST}
  username: ${AEROSPIKE_USERNAME}
  paswd: ${AEROSPIKE_PASWD}
  namespacevalue: ${AEROSPIKE_NAMESPACEVALUE}
  ttl: ${AEROSPIKE_TTL}

user:
  session:
    audit:
      enabled: ${USER_SESSION_AUDIT_ENABLED}
      
obp:
  validate-pin-url: ${OBP_VALIDATE_PIN_URL}
