<configuration>

    <!-- Define variables for reuse -->
    <property name="LOG_FILE_PATH" value="${LOG_FILE_PATH:-logs}"/>
    <property name="SERVICE_NAME" value="${spring.application.name:-application-logs}"/>
    <property name="MAX_FILE_SIZE" value="${MAX_FILE_SIZE:-10MB}"/>
    <property name="MAX_HISTORY" value="${MAX_HISTORY:-10}"/>
    <property name="TOTAL_SIZE_CAP" value="${TOTAL_SIZE_CAP:-100MB}"/>

    <!-- Info log file appender -->
    <appender name="INFO_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_FILE_PATH}/${SERVICE_NAME}-info.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>${LOG_FILE_PATH}/${SERVICE_NAME}-info.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <maxFileSize>${MAX_FILE_SIZE}</maxFileSize>
            <maxHistory>${MAX_HISTORY}</maxHistory>
            <totalSizeCap>${TOTAL_SIZE_CAP}</totalSizeCap>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>INFO</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
    </appender>

    <!-- Debug log file appender -->
    <appender name="DEBUG_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_FILE_PATH}/${SERVICE_NAME}-debug.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>${LOG_FILE_PATH}/${SERVICE_NAME}-debug.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <maxFileSize>${MAX_FILE_SIZE}</maxFileSize>
            <maxHistory>${MAX_HISTORY}</maxHistory>
            <totalSizeCap>${TOTAL_SIZE_CAP}</totalSizeCap>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>DEBUG</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
    </appender>

    <!-- Error log file appender -->
    <appender name="ERROR_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_FILE_PATH}/${SERVICE_NAME}-error.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>${LOG_FILE_PATH}/${SERVICE_NAME}-error.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <maxFileSize>${MAX_FILE_SIZE}</maxFileSize>
            <maxHistory>${MAX_HISTORY}</maxHistory>
            <totalSizeCap>${TOTAL_SIZE_CAP}</totalSizeCap>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>ERROR</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
    </appender>

    <!-- Root logger configuration -->
    <root level="DEBUG">
        <appender-ref ref="INFO_FILE"/>
        <appender-ref ref="DEBUG_FILE"/>
        <appender-ref ref="ERROR_FILE"/>
    </root>

</configuration>



LOG_FILE_PATH=/var/log/myapp
MAX_FILE_SIZE=10MB
MAX_HISTORY=10
TOTAL_SIZE_CAP=100MB
