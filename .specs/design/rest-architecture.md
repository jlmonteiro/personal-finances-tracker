# REST Architecture

## Overview

API First approach — OpenAPI 3.x specification is the source of truth. All endpoints are versioned under `/api/v1/`.

**Base URL:** `/api/v1`
**Auth:** Bearer token (JWT) in `Authorization` header. Dev profile bypasses auth.
**Content-Type:** `application/json`
**Error format:** RFC 7807 Problem Details

---

## Conventions

- Plural nouns for collections: `/categories`, `/payees`
- camelCase for JSON fields
- Strings for monetary values (no floats)
- ISO 8601 dates: `"2026-06-01"`
- UUIDv7 for all resource IDs
- PATCH for partial updates (most updates)
- Collections wrapped: `{"data": [...], "pagination": {...}}`
- Pagination on all list endpoints: `?page=1&size=20` (default size: 20, max: 100)
- Errors: RFC 7807 with `type`, `title`, `status`, `detail`

---

## Endpoints

### Configuration

| Method | Path | Description | Traces |
|--------|------|-------------|--------|
| GET | `/api/v1/configuration` | Get app configuration (or 404 if not set up) | FR-1 |
| POST | `/api/v1/configuration` | Create initial configuration (setup wizard) | FR-1, FR-2, FR-3 |
| PATCH | `/api/v1/configuration` | Update configuration (settings page) | FR-2, FR-3 |

#### POST /api/v1/configuration

```json
{
  "currency": "EUR",
  "monthStartDay": 1
}
```

**Response:** `201 Created`
```json
{
  "id": "019059a4-b4c7-7000-8000-000000000001",
  "currency": "EUR",
  "monthStartDay": 1,
  "createdAt": "2026-06-01T10:00:00Z"
}
```

---

### Income Sources

| Method | Path | Description | Traces |
|--------|------|-------------|--------|
| GET | `/api/v1/income-sources` | List all income sources | FR-4 |
| POST | `/api/v1/income-sources` | Create income source | FR-4 |
| PATCH | `/api/v1/income-sources/{id}` | Update income source | FR-4 |
| DELETE | `/api/v1/income-sources/{id}` | Delete income source | FR-4 |

#### POST /api/v1/income-sources

```json
{
  "name": "Salary",
  "amount": "3500.00",
  "frequency": "MONTHLY",
  "paymentDateType": "FIXED",
  "paymentDateRule": "25",
  "startDate": "2026-01-01",
  "endDate": null
}
```

**Response:** `201 Created`
```json
{
  "id": "019059a4-b4c7-7000-8000-000000000002",
  "name": "Salary",
  "amount": "3500.00",
  "frequency": "MONTHLY",
  "paymentDateType": "FIXED",
  "paymentDateRule": "25",
  "startDate": "2026-01-01",
  "endDate": null,
  "isActive": true,
  "createdAt": "2026-06-01T10:00:00Z"
}
```

**Frequency values:** `MONTHLY`, `WEEKLY`, `FORTNIGHTLY`, `FOUR_WEEKLY`
**Payment date types:** `FIXED`, `RELATIVE`
**Payment date rules:**
- Fixed: `"25"` (day of month)
- Relative: `"LAST_THURSDAY"`, `"FIRST_FRIDAY"`, `"EVERY_MONDAY"`

---

### Categories

| Method | Path | Description | Traces |
|--------|------|-------------|--------|
| GET | `/api/v1/categories` | List all categories | FR-5 |
| POST | `/api/v1/categories` | Create category | FR-5 |
| PATCH | `/api/v1/categories/{id}` | Update category | FR-5 |
| DELETE | `/api/v1/categories/{id}` | Delete category (fails if referenced) | FR-5 |

#### POST /api/v1/categories

```json
{
  "name": "Groceries",
  "icon": "shopping-cart"
}
```

**Response:** `201 Created`
```json
{
  "id": "019059a4-b4c7-7000-8000-000000000003",
  "name": "Groceries",
  "icon": "shopping-cart",
  "createdAt": "2026-06-01T10:00:00Z"
}
```

**Error (delete with references):** `409 Conflict`
```json
{
  "type": "/errors/category-in-use",
  "title": "Category In Use",
  "status": 409,
  "detail": "Cannot delete category 'Groceries' because it is referenced by 5 expenses."
}
```

---

### Payees

| Method | Path | Description | Traces |
|--------|------|-------------|--------|
| GET | `/api/v1/payees` | List all payees (includes associated categories) | FR-6, FR-7 |
| POST | `/api/v1/payees` | Create payee with category associations | FR-6, FR-7 |
| PATCH | `/api/v1/payees/{id}` | Update payee (name and/or categories) | FR-6, FR-7 |
| DELETE | `/api/v1/payees/{id}` | Delete payee (fails if referenced) | FR-6 |

#### POST /api/v1/payees

```json
{
  "name": "Dunnes Stores",
  "categoryIds": [
    "019059a4-b4c7-7000-8000-000000000003",
    "019059a4-b4c7-7000-8000-000000000004"
  ]
}
```

**Response:** `201 Created`
```json
{
  "id": "019059a4-b4c7-7000-8000-000000000005",
  "name": "Dunnes Stores",
  "categories": [
    {"id": "019059a4-b4c7-7000-8000-000000000003", "name": "Groceries", "icon": "shopping-cart"},
    {"id": "019059a4-b4c7-7000-8000-000000000004", "name": "Clothing", "icon": "shirt"}
  ],
  "createdAt": "2026-06-01T10:00:00Z"
}
```

---

### Financial Months

| Method | Path | Description | Traces |
|--------|------|-------------|--------|
| GET | `/api/v1/financial-months` | List all months (paginated summary) | FR-8 |
| GET | `/api/v1/financial-months/{id}` | Get month metadata (start/end dates) | FR-8 |
| GET | `/api/v1/financial-months/{id}/quarters` | List quarters for a month | FR-9 |
| POST | `/api/v1/financial-months` | Create a new financial month | FR-8, FR-9 |
| GET | `/api/v1/financial-months/current` | Get current month (convenience) | FR-14 |

#### POST /api/v1/financial-months

```json
{
  "year": 2026,
  "month": 6
}
```

**Response:** `201 Created`
```json
{
  "id": "019059a4-b4c7-7000-8000-000000000006",
  "startDate": "2026-06-01",
  "endDate": "2026-06-30",
  "createdAt": "2026-06-01T10:00:00Z"
}
```

#### GET /api/v1/financial-months/{id}/quarters

**Response:** `200 OK`
```json
{
  "data": [
    {"id": "...", "quarterNumber": 1, "startDate": "2026-06-01", "endDate": "2026-06-07"},
    {"id": "...", "quarterNumber": 2, "startDate": "2026-06-08", "endDate": "2026-06-15"},
    {"id": "...", "quarterNumber": 3, "startDate": "2026-06-16", "endDate": "2026-06-23"},
    {"id": "...", "quarterNumber": 4, "startDate": "2026-06-24", "endDate": "2026-06-30"}
  ]
}
```

**Notes:**
- Creating a month auto-generates quarters (FR-9) and income entries from active income sources (FR-4, FR-17)
- Prevents duplicates (409 if month already exists)

---

### Income Entries

| Method | Path | Description | Traces |
|--------|------|-------------|--------|
| GET | `/api/v1/financial-months/{monthId}/income-entries` | List income entries for a month | FR-17 |
| PATCH | `/api/v1/income-entries/{id}` | Adjust payment date for a specific month | FR-17 |

#### PATCH /api/v1/income-entries/{id}

```json
{
  "paymentDate": "2026-06-27"
}
```

**Response:** `200 OK` — returns updated entry with recalculated quarter assignment.

---

### Expenses

| Method | Path | Description | Traces |
|--------|------|-------------|--------|
| GET | `/api/v1/financial-months/{monthId}/expenses` | List expenses for a month (filterable by quarter, status) | FR-10, FR-18 |
| POST | `/api/v1/financial-months/{monthId}/expenses` | Create expense (planned or unplanned) | FR-10, FR-12 |
| PATCH | `/api/v1/expenses/{id}` | Update expense (record payment, override values) | FR-12, FR-21 |
| DELETE | `/api/v1/expenses/{id}` | Delete expense | FR-10 |

#### Query Parameters

- `?quarterId={id}` — filter by quarter
- `?status=PENDING|PAID|OVERDUE` — filter by derived status
- `?categoryId={id}` — filter by category

#### POST /api/v1/financial-months/{monthId}/expenses

```json
{
  "payeeId": "019059a4-b4c7-7000-8000-000000000005",
  "categoryId": "019059a4-b4c7-7000-8000-000000000003",
  "title": "Weekly groceries",
  "description": "Regular shop",
  "expectedValue": "120.00",
  "dueDate": "2026-06-07"
}
```

**Response:** `201 Created`
```json
{
  "id": "019059a4-b4c7-7000-8000-000000000010",
  "quarterId": "...",
  "quarterNumber": 1,
  "payee": {"id": "...", "name": "Dunnes Stores"},
  "category": {"id": "...", "name": "Groceries", "icon": "shopping-cart"},
  "title": "Weekly groceries",
  "description": "Regular shop",
  "expectedValue": "120.00",
  "actualValue": null,
  "dueDate": "2026-06-07",
  "paymentDate": null,
  "status": "PENDING",
  "isOverride": false,
  "recurringExpenseId": null,
  "createdAt": "2026-06-01T10:00:00Z"
}
```

#### PATCH /api/v1/expenses/{id} (Record Payment)

```json
{
  "actualValue": "115.50",
  "paymentDate": "2026-06-07"
}
```

#### PATCH /api/v1/expenses/{id} (Override Recurring)

```json
{
  "expectedValue": "500.00",
  "description": "Extra savings this month"
}
```

Sets `isOverride = true` automatically.

---

### Recurring Expenses

| Method | Path | Description | Traces |
|--------|------|-------------|--------|
| GET | `/api/v1/recurring-expenses` | List all recurring expense templates | FR-10 |
| POST | `/api/v1/recurring-expenses` | Create recurring expense | FR-10 |
| PATCH | `/api/v1/recurring-expenses/{id}` | Update recurring expense template | FR-10 |
| DELETE | `/api/v1/recurring-expenses/{id}` | Deactivate recurring expense | FR-10 |

#### POST /api/v1/recurring-expenses

```json
{
  "payeeId": "019059a4-b4c7-7000-8000-000000000005",
  "categoryId": "019059a4-b4c7-7000-8000-000000000003",
  "title": "Weekly groceries",
  "description": "Regular shop at Dunnes",
  "expectedValue": "120.00",
  "frequency": "WEEKLY",
  "startDate": "2026-06-01",
  "endDate": null
}
```

---

### Dashboard

| Method | Path | Description | Traces |
|--------|------|-------------|--------|
| GET | `/api/v1/financial-months/{monthId}/summary` | Month summary (totals, per-quarter breakdown) | FR-14, FR-15 |
| GET | `/api/v1/financial-months/{monthId}/category-breakdown` | Expenses grouped by category | FR-16 |

#### GET /api/v1/financial-months/{monthId}/summary

**Response:** `200 OK`
```json
{
  "monthId": "...",
  "startDate": "2026-06-01",
  "endDate": "2026-06-30",
  "totalIncome": "4200.00",
  "totalBudget": "3100.00",
  "totalActual": "1850.00",
  "remaining": "2350.00",
  "netBalance": "2350.00",
  "quarters": [
    {
      "quarterNumber": 1,
      "startDate": "2026-06-01",
      "endDate": "2026-06-07",
      "budget": "800.00",
      "actual": "750.00",
      "variance": "50.00"
    }
  ]
}
```

#### GET /api/v1/financial-months/{monthId}/category-breakdown

**Response:** `200 OK`
```json
{
  "data": [
    {
      "category": {"id": "...", "name": "Groceries", "icon": "shopping-cart"},
      "totalPlanned": "480.00",
      "totalActual": "465.00"
    }
  ]
}
```

---

### Export

| Method | Path | Description | Traces |
|--------|------|-------------|--------|
| GET | `/api/v1/financial-months/{monthId}/export/markdown` | Export month as Markdown | FR-22 |

**Response:** `200 OK`, `Content-Type: text/markdown`

Returns the full month breakdown as a Markdown document.

---

## Error Responses

All errors follow RFC 7807:

```json
{
  "type": "/errors/{error-type}",
  "title": "Human Readable Title",
  "status": 422,
  "detail": "Specific error description",
  "errors": []
}
```

| Error Type | Status | When |
|-----------|--------|------|
| `validation-failed` | 422 | Request body fails validation |
| `not-found` | 404 | Resource doesn't exist |
| `conflict` | 409 | Duplicate month, delete with references |
| `category-not-in-payee` | 422 | Selected category not associated with payee |
| `month-already-exists` | 409 | Duplicate financial month |
