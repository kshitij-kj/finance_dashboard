CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS users (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name     VARCHAR(255) NOT NULL,
    role          VARCHAR(20)  NOT NULL
                  CHECK (role IN ('VIEWER', 'ANALYST', 'ADMIN')),
    is_active     BOOLEAN NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS financial_records (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_by  UUID NOT NULL REFERENCES users(id),
    amount      NUMERIC(15, 2) NOT NULL CHECK (amount > 0),
    type        VARCHAR(10) NOT NULL
                CHECK (type IN ('INCOME', 'EXPENSE')),
    category    VARCHAR(100) NOT NULL,
    record_date DATE NOT NULL,
    description TEXT,
    is_deleted  BOOLEAN NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Indexes for the most common query patterns
CREATE INDEX IF NOT EXISTS idx_records_type
    ON financial_records(type)
    WHERE is_deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_records_category
    ON financial_records(category)
    WHERE is_deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_records_date
    ON financial_records(record_date)
    WHERE is_deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_records_created_by
    ON financial_records(created_by);