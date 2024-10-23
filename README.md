spring:
  datasource:
    url: jdbc:yugabytedb://127.0.0.1:5433/yugabyte?load-balance=true
    username: yugabyte
    password: yugabyte
    driver-class-name: com.yugabyte.Driver
    hikari:
      minimum-idle: 5
      maximum-pool-size: 20
      auto-commit: false
