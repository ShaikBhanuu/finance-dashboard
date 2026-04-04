# Finance Dashboard Backend

Backend API for a finance dashboard with JWT authentication, role-based access control, and analytics. Built by **MoulaBhanu Shaik** for the Zorvyn Internship Screening Assignment.

## Stack
Java 17 · Spring Boot 3.3.6 · Spring Security · JPA/Hibernate · MySQL 8 · JWT · Swagger UI

## Project Structure
```
src/main/java/com/zorvyn/finance/
├── config/          # Security config, JWT config, Swagger config
├── controller/      # REST endpoints (Auth, Users, Transactions, Dashboard)
├── dto/             # Request/Response objects (never expose raw entities)
├── exception/       # Global exception handler and custom exceptions
├── model/           # JPA entities (User, Transaction) and enums
├── repository/      # Spring Data JPA repositories with custom queries
├── security/        # JWT service, filter, and UserDetails implementation
└── service/         # Business logic (Auth, User, Transaction, Dashboard)
```
## Quick Start

1. Clone the repo and open `src/main/resources/application.yml` — set your MySQL username and password.

2. Run:
```bash
mvn spring-boot:run
```

App auto-creates the database, tables, and 3 seed users on first run.

3. API docs: `http://localhost:8080/swagger-ui/index.html`

## Test Credentials
| Username | Password | Role    |
|----------|----------|---------|
| admin    | password | ADMIN   |
| analyst  | password | ANALYST |
| viewer   | password | VIEWER  |

## Access Control
| Action                              | VIEWER | ANALYST | ADMIN |
|-------------------------------------|--------|---------|-------|
| View transactions                   | Yes    | Yes     | Yes   |
| View dashboard summary and trends   | No     | Yes     | Yes   |
| Create, update, delete transactions | No     | No      | Yes   |
| Manage users and roles              | No     | No      | Yes   |

#### Windows (PowerShell)
```powershell
# Login
$r = Invoke-WebRequest -Uri "http://localhost:8080/api/auth/login" -Method POST -Headers @{"Content-Type"="application/json"} -Body '{"username":"admin","password":"password"}' -ContentType "application/json" -UseBasicParsing
$token = ($r.Content | ConvertFrom-Json).token

# Create transaction (ADMIN only)
Invoke-WebRequest -Uri "http://localhost:8080/api/transactions" -Method POST -Headers @{"Authorization"="Bearer $token"} -Body '{"amount":50000.00,"type":"INCOME","category":"Salary","description":"Monthly salary","date":"2026-04-01"}' -ContentType "application/json" -UseBasicParsing

# Dashboard summary (ANALYST or ADMIN)
Invoke-WebRequest -Uri "http://localhost:8080/api/dashboard/summary" -Method GET -Headers @{"Authorization"="Bearer $token"} -UseBasicParsing
```

### Mac/Linux (curl)
```bash
# Login
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password"}' | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

# Create transaction (ADMIN only)
curl -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"amount":50000.00,"type":"INCOME","category":"Salary","description":"Monthly salary","date":"2026-04-01"}'

# Dashboard summary (ANALYST or ADMIN)
curl -X GET http://localhost:8080/api/dashboard/summary \
  -H "Authorization: Bearer $TOKEN"
```

> Swagger UI is available at `http://localhost:8080/swagger-ui/index.html` for API exploration.
> For testing protected endpoints, use the commands above — Swagger's Authorize button
> does not attach JWT headers due to a known interaction with Spring Security's stateless configuration.

## Design Decisions
- BigDecimal for money — avoids floating point precision errors that Double produces
- DTOs over raw entities — passwords and internal fields never exposed in responses
- JWT stateless auth — scales better than server-side sessions
- SQL aggregations for dashboard — SUM and GROUP BY run in the database, not Java loops
- Seed data — 3 users preloaded covering all roles so reviewer can test immediately

## Assumptions
- Registered users get VIEWER role by default. Only ADMIN can upgrade roles.
- Hard delete used for simplicity. Production would use soft delete for audit trails.
- JWT expires in 24 hours.

## Author
**MoulaBhanu Shaik** | Backend Developer | Cisco Systems Bangalore (2024–2025) | MCA JNTU University 2027