# Finance Dashboard — API Documentation & Postman Testing Guide

---

## Overview

The Finance Dashboard backend is a role-based REST API built with Java
Spring Boot. It manages financial records, user accounts, and provides
aggregated analytics for dashboard consumption. This document covers
every endpoint in the system with full request/response details and
step-by-step Postman testing instructions.

---

## Setting Up Postman

### Creating the Environment

Before testing, set up a Postman environment to store tokens and
reusable values so you don't paste them manually into every request.

1. Open Postman and click **Environments** in the left sidebar
2. Click **Add** to create a new environment
3. Name it `Finance Dashboard - Local`
4. Add the following variables:

| Variable | Initial Value | Description |
|---|---|---|
| `base_url` | `http://localhost:8080` | API base URL |
| `admin_token` | *(empty)* | Populated after admin login |
| `analyst_token` | *(empty)* | Populated after analyst login |
| `viewer_token` | *(empty)* | Populated after viewer login |
| `record_id` | *(empty)* | Populated after creating a record |
| `user_id` | *(empty)* | Populated when needed |

5. Click **Save**
6. Select this environment from the top-right dropdown in Postman

### Auto-Capturing Tokens

For each login request, add this script to the **Tests** tab to
automatically save the token to your environment:
```javascript
const response = pm.response.json();
pm.environment.set("admin_token", response.token);
```

Change `admin_token` to `analyst_token` or `viewer_token` depending
on which login request you are running.

### Setting the Authorization Header

For every protected request:

1. Go to the **Authorization** tab of the request
2. Select **Bearer Token** from the Type dropdown
3. Enter `{{admin_token}}` (or the appropriate role variable)

Alternatively, set it manually in the **Headers** tab:

| Key | Value |
|---|---|
| `Authorization` | `Bearer {{admin_token}}` |
| `Content-Type` | `application/json` |

---

## Base URL
```
http://localhost:8080
```

All endpoint paths below are appended to this base URL.

---

## Default Test Credentials

These accounts are seeded automatically by Flyway on first startup.

| Role | Email | Password |
|---|---|---|
| ADMIN | admin@finance.com | Admin@1234 |
| ANALYST | analyst@finance.com | User@1234 |
| VIEWER | viewer@finance.com | User@1234 |

---

## Table of Contents

1. [Auth Endpoints](#1-auth-endpoints)
2. [User Management Endpoints](#2-user-management-endpoints)
3. [Financial Record Endpoints](#3-financial-record-endpoints)
4. [Dashboard & Analytics Endpoints](#4-dashboard--analytics-endpoints)
5. [Role Access Matrix](#5-role-access-matrix)
6. [Error Reference](#6-error-reference)
7. [Postman Test Sequence](#7-postman-test-sequence)

---

## 1. Auth Endpoints

### 1.1 Register a New User

Creates a new user account and returns a JWT token immediately.
No authentication required — this endpoint is public.

---

**Endpoint**
```
POST /api/auth/register
```

**Authorization** — None (public)

---

**Setting Up in Postman**

1. Create a new request and set the method to **POST**
2. Enter the URL: `{{base_url}}/api/auth/register`
3. Go to the **Body** tab
4. Select **raw** and choose **JSON** from the format dropdown
5. Paste the request body below

---

**Request Body**
```json
{
  "email": "newuser@finance.com",
  "password": "Secure@123",
  "fullName": "New User",
  "role": "ANALYST"
}
```

**Field Rules**

| Field | Type | Required | Validation |
|---|---|---|---|
| `email` | String | Yes | Valid email format, must be unique in the system |
| `password` | String | Yes | Minimum 8 characters |
| `fullName` | String | Yes | Cannot be blank |
| `role` | String | Yes | Exactly one of: `VIEWER`, `ANALYST`, `ADMIN` |

---

**Success Response — 201 Created**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJuZXd1c2VyQGZpbmFuY2UuY29tIn0...",
  "email": "newuser@finance.com",
  "role": "ANALYST"
}
```

---

**Error Responses**

**400 — Duplicate email**
```json
{
  "code": "BAD_REQUEST",
  "message": "Email already registered: newuser@finance.com",
  "fieldErrors": null,
  "timestamp": "2024-11-10T10:00:00"
}
```

**400 — Validation failure**
```json
{
  "code": "VALIDATION_FAILED",
  "message": "One or more fields are invalid",
  "fieldErrors": {
    "email": "Must be a valid email address",
    "password": "Password must be at least 8 characters",
    "fullName": "Full name is required",
    "role": "Role is required"
  },
  "timestamp": "2024-11-10T10:00:00"
}
```

---

**Postman Tests to Add**

In the **Tests** tab of this request:
```javascript
pm.test("Status is 201 Created", () => {
  pm.response.to.have.status(201);
});

pm.test("Response has token", () => {
  const json = pm.response.json();
  pm.expect(json.token).to.be.a("string");
  pm.expect(json.token.length).to.be.greaterThan(0);
});

pm.test("Role matches request", () => {
  pm.expect(pm.response.json().role).to.eql("ANALYST");
});
```

---

### 1.2 Login

Authenticates an existing user and returns a fresh JWT token.
No authentication required — this endpoint is public.

---

**Endpoint**
```
POST /api/auth/login
```

**Authorization** — None (public)

---

**Setting Up in Postman**

1. Create a new request and set the method to **POST**
2. Enter the URL: `{{base_url}}/api/auth/login`
3. Go to the **Body** tab, select **raw → JSON**
4. Paste the request body below
5. In the **Tests** tab, add the auto-save script to capture the token

---

**Request Body**
```json
{
  "email": "admin@finance.com",
  "password": "Admin@1234"
}
```

**Field Rules**

| Field | Type | Required |
|---|---|---|
| `email` | String | Yes |
| `password` | String | Yes |

---

**Success Response — 200 OK**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBmaW5hbmNlLmNvbSJ9...",
  "email": "admin@finance.com",
  "role": "ADMIN"
}
```

---

**Error Responses**

**401 — Wrong credentials**
```json
{
  "code": "INVALID_CREDENTIALS",
  "message": "Invalid email or password",
  "fieldErrors": null,
  "timestamp": "2024-11-10T10:00:00"
}
```

**403 — Deactivated account**
```json
{
  "code": "ACCOUNT_DISABLED",
  "message": "This account has been deactivated",
  "fieldErrors": null,
  "timestamp": "2024-11-10T10:00:00"
}
```

---

**Postman Tests to Add**
```javascript
pm.test("Status is 200 OK", () => {
  pm.response.to.have.status(200);
});

pm.test("Token is present", () => {
  const json = pm.response.json();
  pm.expect(json.token).to.be.a("string");
});

// Auto-save token — change variable name per login request
pm.environment.set("admin_token", pm.response.json().token);
pm.test("Token saved to environment", () => {
  pm.expect(pm.environment.get("admin_token")).to.not.be.empty;
});
```

---

## 2. User Management Endpoints

### 2.1 Get All Users

Returns every user in the system. Restricted to ADMIN only.

---

**Endpoint**
```
GET /api/users
```

**Authorization** — ADMIN only

---

**Setting Up in Postman**

1. Create a new request and set the method to **GET**
2. Enter the URL: `{{base_url}}/api/users`
3. Go to the **Authorization** tab
4. Select **Bearer Token** and enter `{{admin_token}}`
5. No request body needed

---

**Success Response — 200 OK**
```json
[
  {
    "id": "9602c853-49be-43c9-939b-cda8bb3f8703",
    "email": "admin@finance.com",
    "fullName": "System Admin",
    "role": "ADMIN",
    "isActive": true,
    "createdAt": "2024-11-01T10:00:00"
  },
  {
    "id": "77e6bcc9-6cf7-44ff-a433-f5c5143d2002",
    "email": "analyst@finance.com",
    "fullName": "Sarah Mitchell",
    "role": "ANALYST",
    "isActive": true,
    "createdAt": "2024-11-01T10:01:00"
  },
  {
    "id": "a210c9c9-ccc5-49e8-8c72-de16436fea4b",
    "email": "viewer@finance.com",
    "fullName": "James Carter",
    "role": "VIEWER",
    "isActive": true,
    "createdAt": "2024-11-01T10:02:00"
  }
]
```

---

**Error Responses**

**403 — Non-admin or missing token**
```json
{
  "code": "ACCESS_DENIED",
  "message": "You do not have permission to perform this action",
  "fieldErrors": null,
  "timestamp": "2024-11-10T10:00:00"
}
```

---

**Postman Tests to Add**
```javascript
pm.test("Status is 200 OK", () => {
  pm.response.to.have.status(200);
});

pm.test("Returns an array", () => {
  pm.expect(pm.response.json()).to.be.an("array");
});

pm.test("Each user has required fields", () => {
  pm.response.json().forEach(user => {
    pm.expect(user).to.have.property("id");
    pm.expect(user).to.have.property("email");
    pm.expect(user).to.have.property("role");
    pm.expect(user).to.have.property("isActive");
  });
});

// Save first non-admin user ID for later tests
const users = pm.response.json();
const viewer = users.find(u => u.role === "VIEWER");
if (viewer) pm.environment.set("user_id", viewer.id);
```

---

### 2.2 Get My Profile

Returns the profile of the currently authenticated user.
Available to all roles — each user only ever sees their own data.

---

**Endpoint**
```
GET /api/users/me
```

**Authorization** — VIEWER, ANALYST, ADMIN

---

**Setting Up in Postman**

1. Create a new request and set the method to **GET**
2. Enter the URL: `{{base_url}}/api/users/me`
3. In the **Authorization** tab select **Bearer Token**
4. Enter `{{viewer_token}}` to test as a VIEWER

---

**Success Response — 200 OK**
```json
{
  "id": "a210c9c9-ccc5-49e8-8c72-de16436fea4b",
  "email": "viewer@finance.com",
  "fullName": "James Carter",
  "role": "VIEWER",
  "isActive": true,
  "createdAt": "2024-11-01T10:02:00"
}
```

---

**Postman Tests to Add**
```javascript
pm.test("Status is 200 OK", () => {
  pm.response.to.have.status(200);
});

pm.test("Returns own profile only", () => {
  const json = pm.response.json();
  pm.expect(json.email).to.eql("viewer@finance.com");
});
```

---

### 2.3 Update User Role

Promotes or demotes any user to a different role. ADMIN only.

---

**Endpoint**
```
PATCH /api/users/{id}/role
```

**Authorization** — ADMIN only

---

**Setting Up in Postman**

1. Create a new request and set the method to **PATCH**
2. Enter the URL: `{{base_url}}/api/users/{{user_id}}/role`
3. Set Authorization to `{{admin_token}}`
4. In the **Body** tab select **raw → JSON**
5. Paste the request body below

---

**Request Body**
```json
{
  "role": "ANALYST"
}
```

**Field Rules**

| Field | Type | Required | Validation |
|---|---|---|---|
| `role` | String | Yes | One of: `VIEWER`, `ANALYST`, `ADMIN` |

---

**Success Response — 200 OK**
```json
{
  "id": "a210c9c9-ccc5-49e8-8c72-de16436fea4b",
  "email": "viewer@finance.com",
  "fullName": "James Carter",
  "role": "ANALYST",
  "isActive": true,
  "createdAt": "2024-11-01T10:02:00"
}
```

The `role` field now reflects the updated value.

---

**Error Responses**

**404 — User not found**
```json
{
  "code": "NOT_FOUND",
  "message": "User not found: a210c9c9-0000-0000-0000-de16436fea4b",
  "fieldErrors": null,
  "timestamp": "2024-11-10T10:00:00"
}
```

**400 — Invalid role value**
```json
{
  "code": "VALIDATION_FAILED",
  "message": "One or more fields are invalid",
  "fieldErrors": {
    "role": "Role is required"
  },
  "timestamp": "2024-11-10T10:00:00"
}
```

---

**Postman Tests to Add**
```javascript
pm.test("Status is 200 OK", () => {
  pm.response.to.have.status(200);
});

pm.test("Role is updated correctly", () => {
  pm.expect(pm.response.json().role).to.eql("ANALYST");
});
```

---

### 2.4 Toggle User Active Status

Activates or deactivates a user account. Calling this endpoint on an
active user deactivates them and vice versa. ADMIN only.

---

**Endpoint**
```
PATCH /api/users/{id}/status
```

**Authorization** — ADMIN only

---

**Setting Up in Postman**

1. Create a new request and set the method to **PATCH**
2. Enter the URL: `{{base_url}}/api/users/{{user_id}}/status`
3. Set Authorization to `{{admin_token}}`
4. No request body required — leave the Body empty

---

**Success Response — 200 OK**
```json
{
  "id": "a210c9c9-ccc5-49e8-8c72-de16436fea4b",
  "email": "viewer@finance.com",
  "fullName": "James Carter",
  "role": "VIEWER",
  "isActive": false,
  "createdAt": "2024-11-01T10:02:00"
}
```

The `isActive` field toggles on each call. Call it again to re-activate.

---

**Postman Tests to Add**
```javascript
pm.test("Status is 200 OK", () => {
  pm.response.to.have.status(200);
});

pm.test("isActive has been toggled", () => {
  // Should be false after first call on an active user
  pm.expect(pm.response.json().isActive).to.eql(false);
});
```

**Verification step** — after deactivating, try to log in as that user
using the Login endpoint. You should receive `403 ACCOUNT_DISABLED`.

---

## 3. Financial Record Endpoints

### 3.1 Create a Financial Record

Creates a new income or expense record. ADMIN only.

---

**Endpoint**
```
POST /api/records
```

**Authorization** — ADMIN only

---

**Setting Up in Postman**

1. Create a new request and set the method to **POST**
2. Enter the URL: `{{base_url}}/api/records`
3. Set Authorization to `{{admin_token}}`
4. In the **Body** tab select **raw → JSON**
5. Paste the request body below
6. In the **Tests** tab add the script to save the returned `id`

---

**Request Body**
```json
{
  "amount": 95000.00,
  "type": "INCOME",
  "category": "Salary",
  "recordDate": "2025-01-01",
  "description": "January salary"
}
```

**Field Rules**

| Field | Type | Required | Validation |
|---|---|---|---|
| `amount` | Decimal | Yes | Must be positive. Max 13 digits with 2 decimal places |
| `type` | String | Yes | Exactly `INCOME` or `EXPENSE` |
| `category` | String | Yes | Cannot be blank. Maximum 100 characters |
| `recordDate` | Date | Yes | Format must be `yyyy-MM-dd` |
| `description` | String | No | Maximum 1000 characters |

---

**Success Response — 201 Created**
```json
{
  "id": "c3d4e5f6-1234-5678-abcd-ef0123456789",
  "amount": 95000.00,
  "type": "INCOME",
  "category": "Salary",
  "recordDate": "2025-01-01",
  "description": "January salary",
  "createdByEmail": "admin@finance.com",
  "createdAt": "2025-01-01T10:00:00",
  "updatedAt": "2025-01-01T10:00:00"
}
```

---

**Error Responses**

**400 — Validation failure**
```json
{
  "code": "VALIDATION_FAILED",
  "message": "One or more fields are invalid",
  "fieldErrors": {
    "amount": "Amount must be greater than zero",
    "type": "Type is required (INCOME or EXPENSE)",
    "category": "Category is required",
    "recordDate": "Record date is required"
  },
  "timestamp": "2025-01-01T10:00:00"
}
```

**403 — Non-admin token**
```json
{
  "code": "ACCESS_DENIED",
  "message": "You do not have permission to perform this action",
  "fieldErrors": null,
  "timestamp": "2025-01-01T10:00:00"
}
```

---

**Postman Tests to Add**
```javascript
pm.test("Status is 201 Created", () => {
  pm.response.to.have.status(201);
});

pm.test("Record has an ID", () => {
  pm.expect(pm.response.json().id).to.be.a("string");
});

pm.test("Amount matches request", () => {
  pm.expect(pm.response.json().amount).to.eql(95000.00);
});

pm.test("Type is correct", () => {
  pm.expect(pm.response.json().type).to.eql("INCOME");
});

// Save record ID for update and delete tests
pm.environment.set("record_id", pm.response.json().id);
pm.test("Record ID saved to environment", () => {
  pm.expect(pm.environment.get("record_id")).to.not.be.empty;
});
```

---

### 3.2 Get All Records

Returns a paginated list of all active financial records.
All filter parameters are optional and can be combined freely.

---

**Endpoint**
```
GET /api/records
```

**Authorization** — VIEWER, ANALYST, ADMIN

---

**Setting Up in Postman**

1. Create a new request and set the method to **GET**
2. Enter the URL: `{{base_url}}/api/records`
3. Set Authorization to any valid token
4. Go to the **Params** tab to add optional query parameters

---

**Query Parameters**

| Parameter | Type | Required | Description | Example Value |
|---|---|---|---|---|
| `type` | String | No | Filter by record type | `INCOME` or `EXPENSE` |
| `category` | String | No | Filter by exact category name | `Salary` |
| `startDate` | Date | No | Records on or after this date | `2024-11-01` |
| `endDate` | Date | No | Records on or before this date | `2024-11-30` |
| `page` | Integer | No | Zero-based page number (default: 0) | `0` |
| `size` | Integer | No | Records per page (default: 20) | `10` |

**Example URL Combinations**
```
# All records, default pagination
GET /api/records

# Income records only
GET /api/records?type=INCOME

# Expense records in November 2024
GET /api/records?type=EXPENSE&startDate=2024-11-01&endDate=2024-11-30

# Salary category, 5 per page
GET /api/records?category=Salary&page=0&size=5

# Full filter combination
GET /api/records?type=EXPENSE&category=Rent&startDate=2024-07-01&endDate=2024-12-31
```

---

**Success Response — 200 OK**
```json
{
  "content": [
    {
      "id": "c3d4e5f6-1234-5678-abcd-ef0123456789",
      "amount": 95000.00,
      "type": "INCOME",
      "category": "Salary",
      "recordDate": "2025-01-01",
      "description": "January salary",
      "createdByEmail": "admin@finance.com",
      "createdAt": "2025-01-01T10:00:00",
      "updatedAt": "2025-01-01T10:00:00"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20
  },
  "totalElements": 61,
  "totalPages": 4,
  "last": false,
  "first": true,
  "numberOfElements": 20
}
```

Records are sorted by `recordDate` descending — most recent first.

---

**Postman Tests to Add**
```javascript
pm.test("Status is 200 OK", () => {
  pm.response.to.have.status(200);
});

pm.test("Response has pagination structure", () => {
  const json = pm.response.json();
  pm.expect(json).to.have.property("content");
  pm.expect(json).to.have.property("totalElements");
  pm.expect(json).to.have.property("totalPages");
  pm.expect(json.content).to.be.an("array");
});

pm.test("Records are sorted newest first", () => {
  const records = pm.response.json().content;
  if (records.length > 1) {
    const first = new Date(records[0].recordDate);
    const second = new Date(records[1].recordDate);
    pm.expect(first >= second).to.be.true;
  }
});
```

---

### 3.3 Get a Single Record

Returns one financial record by its ID.

---

**Endpoint**
```
GET /api/records/{id}
```

**Authorization** — VIEWER, ANALYST, ADMIN

---

**Setting Up in Postman**

1. Create a new request and set the method to **GET**
2. Enter the URL: `{{base_url}}/api/records/{{record_id}}`
3. Set Authorization to any valid token
4. No request body needed

---

**Success Response — 200 OK**
```json
{
  "id": "c3d4e5f6-1234-5678-abcd-ef0123456789",
  "amount": 95000.00,
  "type": "INCOME",
  "category": "Salary",
  "recordDate": "2025-01-01",
  "description": "January salary",
  "createdByEmail": "admin@finance.com",
  "createdAt": "2025-01-01T10:00:00",
  "updatedAt": "2025-01-01T10:00:00"
}
```

---

**Error Responses**

**404 — Record not found or already deleted**
```json
{
  "code": "NOT_FOUND",
  "message": "Financial record not found: c3d4e5f6-1234-5678-abcd-ef0123456789",
  "fieldErrors": null,
  "timestamp": "2025-01-01T10:00:00"
}
```

---

**Postman Tests to Add**
```javascript
pm.test("Status is 200 OK", () => {
  pm.response.to.have.status(200);
});

pm.test("ID matches requested record", () => {
  const recordId = pm.environment.get("record_id");
  pm.expect(pm.response.json().id).to.eql(recordId);
});
```

---

### 3.4 Update a Financial Record

Replaces all fields of an existing record with new values.
Provide all fields in the body even if only one has changed.
ADMIN only.

---

**Endpoint**
```
PUT /api/records/{id}
```

**Authorization** — ADMIN only

---

**Setting Up in Postman**

1. Create a new request and set the method to **PUT**
2. Enter the URL: `{{base_url}}/api/records/{{record_id}}`
3. Set Authorization to `{{admin_token}}`
4. In the **Body** tab select **raw → JSON**

---

**Request Body**
```json
{
  "amount": 100000.00,
  "type": "INCOME",
  "category": "Salary",
  "recordDate": "2025-01-01",
  "description": "January salary (corrected amount)"
}
```

---

**Success Response — 200 OK**
```json
{
  "id": "c3d4e5f6-1234-5678-abcd-ef0123456789",
  "amount": 100000.00,
  "type": "INCOME",
  "category": "Salary",
  "recordDate": "2025-01-01",
  "description": "January salary (corrected amount)",
  "createdByEmail": "admin@finance.com",
  "createdAt": "2025-01-01T10:00:00",
  "updatedAt": "2025-01-01T11:30:00"
}
```

Notice that `updatedAt` reflects the time of the update while
`createdAt` remains unchanged.

---

**Postman Tests to Add**
```javascript
pm.test("Status is 200 OK", () => {
  pm.response.to.have.status(200);
});

pm.test("Amount has been updated", () => {
  pm.expect(pm.response.json().amount).to.eql(100000.00);
});

pm.test("createdAt is unchanged", () => {
  pm.expect(pm.response.json().createdAt).to.eql("2025-01-01T10:00:00");
});

pm.test("updatedAt has changed", () => {
  const json = pm.response.json();
  pm.expect(json.updatedAt).to.not.eql(json.createdAt);
});
```

---

### 3.5 Soft Delete a Financial Record

Marks a record as deleted. It will no longer appear in any listing or
lookup response, but the data is permanently retained in the database
for audit purposes. ADMIN only.

---

**Endpoint**
```
DELETE /api/records/{id}
```

**Authorization** — ADMIN only

---

**Setting Up in Postman**

1. Create a new request and set the method to **DELETE**
2. Enter the URL: `{{base_url}}/api/records/{{record_id}}`
3. Set Authorization to `{{admin_token}}`
4. No request body needed

---

**Success Response — 204 No Content**

No response body is returned. A `204` status confirms the soft delete
was successful.

---

**Error Responses**

**404 — Record not found or already deleted**
```json
{
  "code": "NOT_FOUND",
  "message": "Financial record not found: c3d4e5f6-1234-5678-abcd-ef0123456789",
  "fieldErrors": null,
  "timestamp": "2025-01-01T10:00:00"
}
```

---

**Postman Tests to Add**
```javascript
pm.test("Status is 204 No Content", () => {
  pm.response.to.have.status(204);
});

pm.test("Response body is empty", () => {
  pm.expect(pm.response.text()).to.be.empty;
});
```

**Verification step** — immediately after running the delete, run
`GET /api/records/{{record_id}}` and confirm it returns
`404 NOT_FOUND`. The record is hidden from the API but still exists
in the database with `is_deleted = true`.

---

## 4. Dashboard & Analytics Endpoints

All three dashboard endpoints require ANALYST or ADMIN role.
VIEWER accounts are intentionally restricted from analytics — they
may browse individual records but cannot access aggregated data.

---

### 4.1 Get Dashboard Summary

Returns the complete financial snapshot for the dashboard including
total income, total expenses, net balance, a breakdown by category,
and the 10 most recently created records.

---

**Endpoint**
```
GET /api/dashboard/summary
```

**Authorization** — ANALYST, ADMIN

---

**Setting Up in Postman**

1. Create a new request and set the method to **GET**
2. Enter the URL: `{{base_url}}/api/dashboard/summary`
3. Set Authorization to `{{analyst_token}}` or `{{admin_token}}`
4. No parameters or body needed

---

**Success Response — 200 OK**
```json
{
  "totalIncome": 745000.00,
  "totalExpenses": 337100.00,
  "netBalance": 407900.00,
  "totalRecords": 60,
  "categoryTotals": [
    { "category": "Salary",        "total": 575000.00 },
    { "category": "Freelance",     "total": 107000.00 },
    { "category": "Rent",          "total": 108000.00 },
    { "category": "Tax",           "total": 50000.00  },
    { "category": "Travel",        "total": 63000.00  },
    { "category": "Investment",    "total": 45000.00  },
    { "category": "Consulting",    "total": 30500.00  },
    { "category": "Groceries",     "total": 53300.00  },
    { "category": "Equipment",     "total": 21500.00  },
    { "category": "Dining",        "total": 37300.00  },
    { "category": "Health",        "total": 16000.00  },
    { "category": "Utilities",     "total": 26100.00  },
    { "category": "Transport",     "total": 19700.00  },
    { "category": "Subscriptions", "total": 15000.00  }
  ],
  "recentActivity": [
    {
      "id": "...",
      "amount": 9800.00,
      "type": "EXPENSE",
      "category": "Health",
      "recordDate": "2024-12-30",
      "description": "Annual health checkup and dental",
      "createdByEmail": "admin@finance.com",
      "createdAt": "2024-12-30T10:00:00",
      "updatedAt": "2024-12-30T10:00:00"
    }
  ]
}
```

**Response Field Reference**

| Field | Description |
|---|---|
| `totalIncome` | Sum of all non-deleted INCOME records |
| `totalExpenses` | Sum of all non-deleted EXPENSE records |
| `netBalance` | `totalIncome` minus `totalExpenses` |
| `totalRecords` | Count of all non-deleted records |
| `categoryTotals` | Per-category totals sorted highest to lowest |
| `recentActivity` | The 10 most recently created non-deleted records |

---

**Error Responses**

**403 — VIEWER token or no token**
```json
{
  "code": "ACCESS_DENIED",
  "message": "You do not have permission to perform this action",
  "fieldErrors": null,
  "timestamp": "2024-11-10T10:00:00"
}
```

---

**Postman Tests to Add**
```javascript
pm.test("Status is 200 OK", () => {
  pm.response.to.have.status(200);
});

pm.test("All summary fields are present", () => {
  const json = pm.response.json();
  pm.expect(json).to.have.property("totalIncome");
  pm.expect(json).to.have.property("totalExpenses");
  pm.expect(json).to.have.property("netBalance");
  pm.expect(json).to.have.property("totalRecords");
  pm.expect(json).to.have.property("categoryTotals");
  pm.expect(json).to.have.property("recentActivity");
});

pm.test("Net balance equals income minus expenses", () => {
  const json = pm.response.json();
  const expected = json.totalIncome - json.totalExpenses;
  pm.expect(json.netBalance).to.be.closeTo(expected, 0.01);
});

pm.test("Recent activity has at most 10 records", () => {
  pm.expect(pm.response.json().recentActivity.length).to.be.at.most(10);
});

pm.test("Category totals are sorted descending", () => {
  const totals = pm.response.json().categoryTotals;
  for (let i = 0; i < totals.length - 1; i++) {
    pm.expect(totals[i].total >= totals[i + 1].total).to.be.true;
  }
});
```

---

### 4.2 Get Monthly Trends

Returns total income and total expenses grouped by month, ordered from
newest to oldest. Each month appears twice in the list — once for
INCOME and once for EXPENSE. Designed to power time-series and bar
charts on the frontend.

---

**Endpoint**
```
GET /api/dashboard/trends/monthly
```

**Authorization** — ANALYST, ADMIN

---

**Setting Up in Postman**

1. Create a new request and set the method to **GET**
2. Enter the URL: `{{base_url}}/api/dashboard/trends/monthly`
3. Set Authorization to `{{analyst_token}}` or `{{admin_token}}`
4. No parameters or body needed

---

**Success Response — 200 OK**
```json
[
  { "month": "2024-12", "type": "INCOME",  "total": 160000.00 },
  { "month": "2024-12", "type": "EXPENSE", "total": 80100.00  },
  { "month": "2024-11", "type": "INCOME",  "total": 135000.00 },
  { "month": "2024-11", "type": "EXPENSE", "total": 40800.00  },
  { "month": "2024-10", "type": "INCOME",  "total": 130000.00 },
  { "month": "2024-10", "type": "EXPENSE", "total": 90600.00  },
  { "month": "2024-09", "type": "INCOME",  "total": 110500.00 },
  { "month": "2024-09", "type": "EXPENSE", "total": 71800.00  },
  { "month": "2024-08", "type": "INCOME",  "total": 130000.00 },
  { "month": "2024-08", "type": "EXPENSE", "total": 48700.00  },
  { "month": "2024-07", "type": "INCOME",  "total": 112000.00 },
  { "month": "2024-07", "type": "EXPENSE", "total": 43200.00  }
]
```

Months with no records of a given type are omitted from the response.

---

**Postman Tests to Add**
```javascript
pm.test("Status is 200 OK", () => {
  pm.response.to.have.status(200);
});

pm.test("Returns an array", () => {
  pm.expect(pm.response.json()).to.be.an("array");
});

pm.test("Each entry has month, type, and total", () => {
  pm.response.json().forEach(entry => {
    pm.expect(entry).to.have.property("month");
    pm.expect(entry).to.have.property("type");
    pm.expect(entry).to.have.property("total");
    pm.expect(["INCOME", "EXPENSE"]).to.include(entry.type);
  });
});

pm.test("Months are in yyyy-MM format", () => {
  pm.response.json().forEach(entry => {
    pm.expect(entry.month).to.match(/^\d{4}-\d{2}$/);
  });
});
```

---

### 4.3 Get Category Breakdown

Returns total amounts grouped by category, sorted from highest to
lowest. All parameters are optional — omit them to get totals across
all types and all time. Combine parameters to narrow the scope.

---

**Endpoint**
```
GET /api/dashboard/breakdown
```

**Authorization** — ANALYST, ADMIN

---

**Setting Up in Postman**

1. Create a new request and set the method to **GET**
2. Enter the URL: `{{base_url}}/api/dashboard/breakdown`
3. Set Authorization to `{{analyst_token}}` or `{{admin_token}}`
4. Add optional parameters in the **Params** tab

---

**Query Parameters**

| Parameter | Type | Required | Description | Example |
|---|---|---|---|---|
| `type` | String | No | Scope to `INCOME` or `EXPENSE` only | `EXPENSE` |
| `startDate` | Date | No | Include records on or after this date | `2024-11-01` |
| `endDate` | Date | No | Include records on or before this date | `2024-11-30` |

**Example URL Combinations**
```
# All categories, all time (no filters)
GET /api/dashboard/breakdown

# Expense categories only
GET /api/dashboard/breakdown?type=EXPENSE

# Income sources in Q4 2024
GET /api/dashboard/breakdown?type=INCOME&startDate=2024-10-01&endDate=2024-12-31

# All category totals for November 2024
GET /api/dashboard/breakdown?startDate=2024-11-01&endDate=2024-11-30
```

---

**Success Response — 200 OK**
```json
[
  { "category": "Rent",          "total": 108000.00 },
  { "category": "Travel",        "total": 63000.00  },
  { "category": "Groceries",     "total": 53300.00  },
  { "category": "Tax",           "total": 50000.00  },
  { "category": "Dining",        "total": 37300.00  },
  { "category": "Utilities",     "total": 26100.00  },
  { "category": "Equipment",     "total": 21500.00  },
  { "category": "Transport",     "total": 19700.00  },
  { "category": "Health",        "total": 16000.00  },
  { "category": "Subscriptions", "total": 15000.00  }
]
```

---

**Postman Tests to Add**
```javascript
pm.test("Status is 200 OK", () => {
  pm.response.to.have.status(200);
});

pm.test("Returns an array", () => {
  pm.expect(pm.response.json()).to.be.an("array");
});

pm.test("Each entry has category and total", () => {
  pm.response.json().forEach(entry => {
    pm.expect(entry).to.have.property("category");
    pm.expect(entry).to.have.property("total");
    pm.expect(entry.total).to.be.above(0);
  });
});

pm.test("Results are sorted descending by total", () => {
  const results = pm.response.json();
  for (let i = 0; i < results.length - 1; i++) {
    pm.expect(results[i].total >= results[i + 1].total).to.be.true;
  }
});
```

---

## 5. Role Access Matrix

| Endpoint | VIEWER | ANALYST | ADMIN |
|---|---|---|---|
| POST `/api/auth/register` | ✓ | ✓ | ✓ |
| POST `/api/auth/login` | ✓ | ✓ | ✓ |
| GET `/api/users` | ✗ | ✗ | ✓ |
| GET `/api/users/me` | ✓ | ✓ | ✓ |
| PATCH `/api/users/{id}/role` | ✗ | ✗ | ✓ |
| PATCH `/api/users/{id}/status` | ✗ | ✗ | ✓ |
| POST `/api/records` | ✗ | ✗ | ✓ |
| GET `/api/records` | ✓ | ✓ | ✓ |
| GET `/api/records/{id}` | ✓ | ✓ | ✓ |
| PUT `/api/records/{id}` | ✗ | ✗ | ✓ |
| DELETE `/api/records/{id}` | ✗ | ✗ | ✓ |
| GET `/api/dashboard/summary` | ✗ | ✓ | ✓ |
| GET `/api/dashboard/trends/monthly` | ✗ | ✓ | ✓ |
| GET `/api/dashboard/breakdown` | ✗ | ✓ | ✓ |

---

## 6. Error Reference

Every error response across the entire API follows this consistent shape:
```json
{
  "code": "ERROR_CODE",
  "message": "Human readable description of what went wrong",
  "fieldErrors": null,
  "timestamp": "2024-11-10T10:00:00"
}
```

The `fieldErrors` object is only present and populated for
`VALIDATION_FAILED` responses. For all other error types it is `null`.

| HTTP Status | Code | Cause |
|---|---|---|
| 400 | `VALIDATION_FAILED` | One or more request body fields failed `@Valid` checks |
| 400 | `BAD_REQUEST` | Business rule violation such as duplicate email |
| 401 | `INVALID_CREDENTIALS` | Wrong email or password on login |
| 403 | `ACCESS_DENIED` | Missing token, expired token, or insufficient role |
| 403 | `ACCOUNT_DISABLED` | User account has been deactivated by an admin |
| 404 | `NOT_FOUND` | Resource does not exist or has been soft-deleted |
| 500 | `INTERNAL_ERROR` | Unexpected server error — check the application logs |

---

## 7. Postman Test Sequence

Run these steps in order on a fresh database to validate every layer
of the system. Each step is numbered and builds on the previous one.

---

### Step 1 — Login as Admin

**Request**
- Method: `POST`
- URL: `{{base_url}}/api/auth/login`
- Body:
```json
{
  "email": "admin@finance.com",
  "password": "Admin@1234"
}
```

**Expected Result**

Status `200 OK` with a `token` in the response body.

**Action**

In the Tests tab save the token automatically:
```javascript
pm.environment.set("admin_token", pm.response.json().token);
```

---

### Step 2 — Login as Analyst

**Request**
- Method: `POST`
- URL: `{{base_url}}/api/auth/login`
- Body:
```json
{
  "email": "analyst@finance.com",
  "password": "User@1234"
}
```

**Expected Result**

Status `200 OK`. Save the token:
```javascript
pm.environment.set("analyst_token", pm.response.json().token);
```

---

### Step 3 — Login as Viewer

**Request**
- Method: `POST`
- URL: `{{base_url}}/api/auth/login`
- Body:
```json
{
  "email": "viewer@finance.com",
  "password": "User@1234"
}
```

**Expected Result**

Status `200 OK`. Save the token:
```javascript
pm.environment.set("viewer_token", pm.response.json().token);
```

---

### Step 4 — Test Validation on Register

**Request**
- Method: `POST`
- URL: `{{base_url}}/api/auth/register`
- Body:
```json
{
  "email": "not-an-email",
  "password": "short",
  "fullName": "",
  "role": "ADMIN"
}
```

**Expected Result**

Status `400 VALIDATION_FAILED` with `fieldErrors` containing entries
for `email`, `password`, and `fullName`.

---

### Step 5 — List All Users as Admin

**Request**
- Method: `GET`
- URL: `{{base_url}}/api/users`
- Authorization: `{{admin_token}}`

**Expected Result**

Status `200 OK` with an array of 3 users. In the Tests tab save the
viewer's ID for later steps:
```javascript
const viewer = pm.response.json().find(u => u.role === "VIEWER");
pm.environment.set("user_id", viewer.id);
```

---

### Step 6 — Confirm VIEWER Cannot List Users

**Request**
- Method: `GET`
- URL: `{{base_url}}/api/users`
- Authorization: `{{viewer_token}}`

**Expected Result**

Status `403 ACCESS_DENIED`. This confirms role enforcement is working
for the user management endpoint.

---

### Step 7 — Get Own Profile

**Request**
- Method: `GET`
- URL: `{{base_url}}/api/users/me`
- Authorization: `{{viewer_token}}`

**Expected Result**

Status `200 OK` returning only James Carter's profile. The viewer
cannot access other users' profiles via this endpoint.

---

### Step 8 — Create an Income Record

**Request**
- Method: `POST`
- URL: `{{base_url}}/api/records`
- Authorization: `{{admin_token}}`
- Body:
```json
{
  "amount": 95000.00,
  "type": "INCOME",
  "category": "Salary",
  "recordDate": "2025-01-01",
  "description": "January salary"
}
```

**Expected Result**

Status `201 Created`. Save the ID in the Tests tab:
```javascript
pm.environment.set("record_id", pm.response.json().id);
```

---

### Step 9 — Create an Expense Record

**Request**
- Method: `POST`
- URL: `{{base_url}}/api/records`
- Authorization: `{{admin_token}}`
- Body:
```json
{
  "amount": 18000.00,
  "type": "EXPENSE",
  "category": "Rent",
  "recordDate": "2025-01-02",
  "description": "January rent"
}
```

**Expected Result**

Status `201 Created`.

---

### Step 10 — Confirm VIEWER Cannot Create Records

**Request**
- Method: `POST`
- URL: `{{base_url}}/api/records`
- Authorization: `{{viewer_token}}`
- Body:
```json
{
  "amount": 100.00,
  "type": "EXPENSE",
  "category": "Test",
  "recordDate": "2025-01-01"
}
```

**Expected Result**

Status `403 ACCESS_DENIED`. Role enforcement confirmed for record
creation.

---

### Step 11 — List Records With Each Filter

Run each of these as a separate request with any valid token:

**No filters**
- URL: `{{base_url}}/api/records`
- Expected: All records paginated, sorted newest first

**Income only**
- URL: `{{base_url}}/api/records?type=INCOME`
- Expected: Only records where `type` is `INCOME`

**Date range filter**
- URL: `{{base_url}}/api/records?startDate=2025-01-01&endDate=2025-01-31`
- Expected: Only January 2025 records

**Category filter with pagination**
- URL: `{{base_url}}/api/records?category=Salary&page=0&size=5`
- Expected: Up to 5 Salary records on page zero

---

### Step 12 — Fetch the Created Record by ID

**Request**
- Method: `GET`
- URL: `{{base_url}}/api/records/{{record_id}}`
- Authorization: `{{viewer_token}}`

**Expected Result**

Status `200 OK` returning the record created in Step 8.

---

### Step 13 — Update the Record

**Request**
- Method: `PUT`
- URL: `{{base_url}}/api/records/{{record_id}}`
- Authorization: `{{admin_token}}`
- Body:
```json
{
  "amount": 100000.00,
  "type": "INCOME",
  "category": "Salary",
  "recordDate": "2025-01-01",
  "description": "January salary (corrected amount)"
}
```

**Expected Result**

Status `200 OK`. Confirm `amount` is now `100000.00` and `updatedAt`
is later than `createdAt`.

---

### Step 14 — Soft Delete the Record

**Request**
- Method: `DELETE`
- URL: `{{base_url}}/api/records/{{record_id}}`
- Authorization: `{{admin_token}}`

**Expected Result**

Status `204 No Content` with no response body.

---

### Step 15 — Confirm Record is Invisible After Delete

**Request**
- Method: `GET`
- URL: `{{base_url}}/api/records/{{record_id}}`
- Authorization: `{{admin_token}}`

**Expected Result**

Status `404 NOT_FOUND`. The record is hidden from the API but remains
in the database with `is_deleted = true`.

---

### Step 16 — Dashboard Summary as Analyst

**Request**
- Method: `GET`
- URL: `{{base_url}}/api/dashboard/summary`
- Authorization: `{{analyst_token}}`

**Expected Result**

Status `200 OK` with `totalIncome`, `totalExpenses`, `netBalance`,
`totalRecords`, populated `categoryTotals` array, and up to 10 items
in `recentActivity`.

---

### Step 17 — Confirm VIEWER Cannot Access Dashboard

**Request**
- Method: `GET`
- URL: `{{base_url}}/api/dashboard/summary`
- Authorization: `{{viewer_token}}`

**Expected Result**

Status `403 ACCESS_DENIED`. Analytics are restricted to ANALYST and
ADMIN roles.

---

### Step 18 — Monthly Trends

**Request**
- Method: `GET`
- URL: `{{base_url}}/api/dashboard/trends/monthly`
- Authorization: `{{analyst_token}}`

**Expected Result**

Status `200 OK` with an array of entries. With the V3 seed data you
should see 12 entries covering July through December 2024, two per
month (one INCOME, one EXPENSE), ordered newest first.

---

### Step 19 — Category Breakdown Combinations

Run all three as separate requests:

**Expense categories only**
- URL: `{{base_url}}/api/dashboard/breakdown?type=EXPENSE`
- Authorization: `{{analyst_token}}`
- Expected: Only expense categories, sorted by total descending

**Income sources in Q4 2024**
- URL: `{{base_url}}/api/dashboard/breakdown?type=INCOME&startDate=2024-10-01&endDate=2024-12-31`
- Authorization: `{{admin_token}}`
- Expected: Only income categories within the date range

**All categories for November 2024**
- URL: `{{base_url}}/api/dashboard/breakdown?startDate=2024-11-01&endDate=2024-11-30`
- Authorization: `{{analyst_token}}`
- Expected: Both income and expense categories scoped to November

---

### Step 20 — Update User Role

**Request**
- Method: `PATCH`
- URL: `{{base_url}}/api/users/{{user_id}}/role`
- Authorization: `{{admin_token}}`
- Body:
```json
{
  "role": "ANALYST"
}
```

**Expected Result**

Status `200 OK` with `role` now showing `ANALYST`.

**Verification** — log in again as that user and confirm the new token
contains the `ANALYST` role. They should now be able to access the
dashboard endpoints.

---

### Step 21 — Deactivate a User

**Request**
- Method: `PATCH`
- URL: `{{base_url}}/api/users/{{user_id}}/status`
- Authorization: `{{admin_token}}`
- No body

**Expected Result**

Status `200 OK` with `isActive: false`.

---

### Step 22 — Confirm Deactivated User Cannot Login

**Request**
- Method: `POST`
- URL: `{{base_url}}/api/auth/login`
- Body:
```json
{
  "email": "viewer@finance.com",
  "password": "User@1234"
}
```

**Expected Result**

Status `403 ACCOUNT_DISABLED`. The user's existing token also becomes
effectively unusable — any protected request with it will be rejected
since `isAccountNonLocked()` returns false.

---

### Step 23 — No Token Sanity Check

Run each of these with **no Authorization header** set:

- `GET {{base_url}}/api/records`
- `GET {{base_url}}/api/users`
- `GET {{base_url}}/api/dashboard/summary`

**Expected Result for All Three**

Status `403 ACCESS_DENIED`. No endpoint other than `/api/auth/**`
is accessible without a valid token.

---

*All 23 steps passing confirms the complete backend is functioning
correctly — authentication, JWT validation, role-based access control,
CRUD operations, soft delete behaviour, analytics aggregations,
validation, and error handling are all verified.*