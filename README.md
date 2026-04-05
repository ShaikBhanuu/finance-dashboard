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

## API Reference

### Auth
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | /api/auth/register | No | Register new user (assigned VIEWER role) |
| POST | /api/auth/login | No | Login and receive JWT token |

**Register request:**
```json
{
  "username": "john",
  "email": "john@example.com",
  "password": "secret123"
}
```

**Login request:**
```json
{
  "username": "admin",
  "password": "password"
}
```

**Login response:**
```json
{
  "token": "eyJhbGci...",
  "username": "admin",
  "role": "ADMIN"
}
```

---

### Transactions
| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| POST | /api/transactions | ADMIN | Create transaction |
| GET | /api/transactions | Any | Get all transactions |
| GET | /api/transactions/{id} | Any | Get transaction by ID |
| PUT | /api/transactions/{id} | ADMIN | Update transaction |
| DELETE | /api/transactions/{id} | ADMIN | Delete transaction |
| GET | /api/transactions/filter | Any | Filter transactions |

**Create/Update request:**
```json
{
  "amount": 50000.00,
  "type": "INCOME",
  "category": "Salary",
  "description": "Monthly salary",
  "date": "2026-04-01"
}
```

**Transaction response:**
```json
{
  "id": 1,
  "amount": 50000.00,
  "type": "INCOME",
  "category": "Salary",
  "description": "Monthly salary",
  "date": "2026-04-01",
  "createdAt": "2026-04-04T15:30:27",
  "username": "admin"
}
```

**Filter params:** `?type=INCOME&category=Salary&start=2026-01-01&end=2026-04-30`
All params are optional and can be combined.

---

### Dashboard
| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| GET | /api/dashboard/summary | ANALYST, ADMIN | Total income, expenses, net balance, by category |
| GET | /api/dashboard/trends | ANALYST, ADMIN | Monthly totals by type and year |

**Summary response:**
```json
{
  "totalIncome": 100000.00,
  "totalExpenses": 20000.00,
  "netBalance": 80000.00,
  "incomeByCategory": { "Salary": 100000.00 },
  "expenseByCategory": { "Food": 20000.00 }
}
```

**Trends params:** `?type=INCOME&year=2026`

---

### Users
| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| GET | /api/users | ADMIN | Get all users |
| GET | /api/users/{id} | ADMIN | Get user by ID |
| PUT | /api/users/{id}/role | ADMIN | Update user role |
| PUT | /api/users/{id}/status | ADMIN | Activate or deactivate user |

**Update role request:**
```json
{ "role": "ANALYST" }
```

**Update status request:**
```json
{ "active": false }
```

**User response:**
```json
{
  "id": 1,
  "username": "admin",
  "email": "admin@zorvyn.com",
  "role": "ADMIN",
  "active": true,
  "createdAt": "2026-04-04T18:07:56"
}
```
## Testing
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
---

## Tradeoffs

- **Spring Boot 3.3.6 over latest** — chosen for Springdoc Swagger UI compatibility.
  Latest Spring Boot 3.5.x has a known incompatibility with Springdoc that prevents
  Swagger UI from rendering API documentation correctly.

- **Hard delete over soft delete** — simpler implementation for this scope.
  Production systems handling financial data should use soft delete with a deleted_at
  timestamp to maintain complete audit trails.

- **Single database over microservices** — appropriate for this scale.
  A real finance system might separate user management and transaction services,
  but for this assignment a monolithic approach keeps complexity manageable.

- **In-memory seed data over migration tool** — used data.sql for simplicity.
  Production would use Flyway or Liquibase for versioned database migrations.
## Assumptions
- Registered users get VIEWER role by default. Only ADMIN can upgrade roles.
- Hard delete used for simplicity. Production would use soft delete for audit trails.
- JWT expires in 24 hours.

## Author
**MoulaBhanu Shaik** | Backend Developer | Cisco Systems Bangalore (2024–2025) | MCA JNTU University 2027