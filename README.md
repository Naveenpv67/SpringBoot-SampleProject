SECRETS:
  YUGABYTE_USERNAME: "yugabyte"
  YUGABYTE_PASSWORD: "yugabyte"

  AEROSPIKE_USERNAME: "dbuser_rw"
  AEROSPIKE_PASSWORD: "}=qET3J!LEWP{ph"

  APP_NBBL_ENC_SECKEY: "UkxFIUBBTVBTJEBBdXRoZW4wOTg3NiEjdXVRV08zJjE="
  APP_NBBL_PIN_SECKEY: "w3EW+0wNlCtQW9LCUcs89VM6pydUqhFaVhl7jFeI5b8="


===
CONFIG_MAP:
  SPRING_APPLICATION_NAME: "EPI_SERVICE"

  YUGABYTE_URL: "jdbc:yugabytedb://10.216.33.7:5433/dev4_issuer"
  YUGABYTE_DRIVER_CLASS_NAME: "com.yugabyte.Driver"

  JPA_SHOW_SQL: "true"

  SERVER_PORT: "8080"
  SERVER_SERVLET_CONTEXT_PATH: "/EPI_SERVICE"

  AEROSPIKE_HOST: "10.216.34.143:3000"
  AEROSPIKE_NAMESPACEVALUE: "aero-dev"
  AEROSPIKE_TTL: "70"

  USER_SESSION_AUDIT_ENABLED: "true"

  OBP_VALIDATE_PIN_URL: "https://10.226.163.7:9444/com.ofss.fc.cz.hdfc.obp.snorkelauth.webservice/ValidatePIN"
