# Banking Transaction Management System

A Spring Boot banking web application with account management, deposits, withdrawals, transfers, transaction history, and an admin console.

## Stack (latest)

| Component | Version |
|---|---|
| Spring Boot | 4.2.0-RC1 |
| Spring Framework | 7.1.0-RC1 |
| Spring Security | 7.2.0-RC1 |
| Spring Data JPA | 4.2.x |
| Hibernate ORM | 7.4.13.Final |
| Thymeleaf + Spring Security extras | 3.1.5.RELEASE |
| Flyway | 12.11.0 |
| H2 Database | 2.5.252 |
| Tomcat (embedded) | 11.0.27 |
| Lombok | 1.18.48 |
| Java | 27 |
| Gradle | 9.8.1 |

## Features

- User registration & login with BCrypt passwords and role-based access (USER/ADMIN)
- Bank accounts: SAVINGS / CHECKING, unique account numbers, initial deposits
- Deposits, withdrawals (with insufficient-funds protection), transfers between accounts
- Per-account and per-user transaction history with pagination and type filtering
- Admin console: users, accounts, transactions, account status changes (ACTIVE/FROZEN/CLOSED)
- Flyway-managed schema, H2 in-memory database
- Bean validation on all forms, global error handling
- Thymeleaf UI with Bootstrap 5

## Requirements

- Java 27+

## Build

```sh
./build.sh
```

## Run

```sh
./run.sh
```

- App: http://localhost:8080
- H2 console: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:bankingdb`
  - User: `sa` (no password)

## Stop

```sh
./stop.sh
```

## Demo credentials

| Username | Password | Role |
|---|---|---|
| admin | Admin@123 | ADMIN |
| user | User@123 | USER |

## Test

```sh
./gradlew test
```

## Project structure

```
src/main/java/com/artificialintelligence/banking/
  config/        SecurityConfig, DataInitializer
  domain/        User, BankAccount, Transaction, enums
  repository/    JPA repositories
  service/       UserService, AccountService, TransactionService
  web/controller Web controllers
  web/dto/       Form DTOs
  web/error/     GlobalExceptionHandler
src/main/resources/
  db/migration/  Flyway SQL migrations
  templates/     Thymeleaf templates
```