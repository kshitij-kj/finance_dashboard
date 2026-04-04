# Finance Dashboard — Backend API

A role-based finance management backend built with Java 17, Spring Boot 3.3,
Spring Security 6, PostgreSQL, and JWT authentication.

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.3.6 |
| Security | Spring Security 6 + JWT (jjwt 0.12) |
| Persistence | Spring Data JPA + Hibernate 6 |
| Database | PostgreSQL 15 |
| Migrations | Flyway |
| Mapping | MapStruct 1.6 |
| Utilities | Lombok |

## Setup

### Prerequisites
- Java 17+
- PostgreSQL 15 running locally
- Maven 3.8+

### Steps
```bash
# 1. Create the database
psql -U postgres -c "CREATE DATABASE finance_db;"

# 2. Clone and configure
# Edit src/main/resources/application.yml with your DB password

# 3. Run
./mvnw spring-boot:run
```

Flyway will automatically create all tables and seed a default admin user
on first startup.

**Default admin credentials:**
- Email: `admin@finance.com`
- Password: `Admin@1234`

## Role Matrix

| Action | VIEWER | ANALYST | ADMIN |
|---|---|---|---|
| Login / Register | ✓ | ✓ | ✓ |
| View own profile | ✓ | ✓ | ✓ |
| List records | ✓ | ✓ | ✓ |
| View single record | ✓ | ✓ | ✓ |
| Create record | — | — | ✓ |
| Update record | — | — | ✓ |
| Delete record | — | — | ✓ |
| Dashboard summary | — | ✓ | ✓ |
| Monthly trends | — | ✓ | ✓ |
| Category breakdown | — | ✓ | ✓ |
| List all users | — | — | ✓ |
| Update user role | — | — | ✓ |
| Toggle user status | — | — | ✓ |

## API Reference

### Auth
| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/api/auth/register` | Public | Register new user |
| POST | `/api/auth/login` | Public | Login, returns JWT |

### Financial Records
| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/api/records` | ADMIN | Create record |
| GET | `/api/records` | Any | List records (paginated) |
| GET | `/api/records/{id}` | Any | Get single record |
| PUT | `/api/records/{id}` | ADMIN | Update record |
| DELETE | `/api/records/{id}` | ADMIN | Soft delete record |

**Query parameters for `GET /api/records`:**
- `type` — `INCOME` or `EXPENSE`
- `category` — exact category string
- `startDate` / `endDate` — ISO date format `yyyy-MM-dd`
- `page` (default 0) / `size` (default 20)

### Dashboard
| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/api/dashboard/summary` | ANALYST, ADMIN | Totals + recent activity |
| GET | `/api/dashboard/trends/monthly` | ANALYST, ADMIN | Monthly income vs expense |
| GET | `/api/dashboard/breakdown` | ANALYST, ADMIN | Category totals |

**Query parameters for `GET /api/dashboard/breakdown`:**
- `type` — filter by `INCOME` or `EXPENSE`
- `startDate` / `endDate` — scope to date range

### Users
| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/api/users` | ADMIN | List all users |
| GET | `/api/users/me` | Any | Own profile |
| PATCH | `/api/users/{id}/role` | ADMIN | Update role |
| PATCH | `/api/users/{id}/status` | ADMIN | Toggle active/inactive |

## Error Response Format

All errors return a consistent shape:
```json
{
  "code": "VALIDATION_FAILED",
  "message": "One or more fields are invalid",
  "fieldErrors": {
    "email": "Must be a valid email address",
    "password": "Password must be at least 8 characters"
  },
  "timestamp": "2024-11-10T10:30:00"
}
```

## Design Decisions

**UUID primary keys** — avoids sequential ID guessing attacks and is
safer for distributed systems.

**Soft delete** — financial records are never physically deleted.
`is_deleted = true` hides them from all queries while preserving the
audit trail. Critical for finance applications.

**Method-level security (`@PreAuthorize`)** — role guards live next to
the code they protect rather than in a separate URL mapping. Harder to
accidentally misconfigure.

**`BigDecimal` for money** — floating point cannot represent 0.1 exactly
in binary. `BigDecimal` with `scale=2` gives exact decimal arithmetic
for all currency operations.

**Flyway migrations** — schema changes are versioned SQL scripts committed
to source control. Any developer can recreate the exact schema by running
the app. `ddl-auto: validate` ensures the entity model stays in sync.

**Partial indexes** — indexes on `financial_records` use
`WHERE is_deleted = FALSE` so only active records are indexed. Keeps
indexes small and queries fast as the table grows.

## Assumptions

- A single `role` per user is sufficient for this system.
- Records are immutable once soft-deleted (no restore endpoint).
- Authentication is stateless JWT — no refresh token rotation implemented.
- All monetary amounts are stored in a single implicit currency.