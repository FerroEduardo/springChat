# SpringChat

## Overview

A simple chat application demonstrating the usage of the Spring Framework and WebSocket.

## Features
- Java 11
- Spring Boot 2.3.3.RELEASE
- Spring Security 2.3.3.RELEASE
- Spring WebSockets(SockJS+STOMP.js) 2.3.3.RELEASE
- Thymeleaf 2.3.3.RELEASE
- Data JPA 2.3.3.RELEASE
- PostgreSQL 12.4
- Gradle 6.5
- [Gradle Test Logger Plugin 2.1.0](https://github.com/radarsh/gradle-test-logger-plugin)
- JUnit 5 Tests
- [Bootstrap](https://getbootstrap.com/)
- ...

## Run

### Run Project

```shell script
./gradlew bootRun
```

### Generate Jar File

```shell script
./gradlew bootJar
```

### Clean Output Files

```shell script
./gradlew clean
```

Generated files can be found at `build/libs/`.

### Run Tests

```shell script
./gradlew clean test
```

Generated test reports can be found at `build/reports/`.

## Database

[More Details](/database.md)

## Logging

To see all logging, enable TRACE log level for `logging.level.org.springframework.web` and `logging.level.com.ferroeduardo.springchat` directly in `application.properties`.

## Images

<img src="/img/chat_new.png" width="600px" />

<img src="/img/admin_0.png" width="600px" />

