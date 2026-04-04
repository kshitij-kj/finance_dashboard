-- ============================================================
-- Seed viewer and analyst users
-- Password for both: User@1234
-- ============================================================
INSERT INTO users (email, password_hash, full_name, role, is_active)
VALUES
    (
        'analyst@finance.com',
        '$2a$10$VUxF70D/CmgevmaMinfyaujNnSvdfnvRehE.1KNwVraIav4fId9ei',
        'Sarah Mitchell',
        'ANALYST',
        TRUE
    ),
    (
        'viewer@finance.com',
        '$2a$10$yDYyqcrFmLS8kzzekGRfsu29RK/UgJVs8CXye4rYnQ.TFzxd9c4ii',
        'James Carter',
        'VIEWER',
        TRUE
    )
ON CONFLICT (email) DO NOTHING;

-- ============================================================
-- Seed financial records
-- All linked to the admin user (inserted in V2)
-- Covers 6 months of realistic income/expense data
-- ============================================================

-- We reference admin's id by subquery to avoid hardcoding UUIDs
DO $$
DECLARE
    admin_id UUID;
BEGIN
    SELECT id INTO admin_id FROM users WHERE email = 'admin@finance.com';

    -- ==================
    -- JULY 2024
    -- ==================
    INSERT INTO financial_records (created_by, amount, type, category, record_date, description) VALUES
    (admin_id, 95000.00, 'INCOME',  'Salary',      '2024-07-01', 'Monthly salary - July'),
    (admin_id, 18000.00, 'EXPENSE', 'Rent',         '2024-07-02', 'Office rent - July'),
    (admin_id,  4200.00, 'EXPENSE', 'Utilities',    '2024-07-05', 'Electricity and internet bills'),
    (admin_id,  8500.00, 'EXPENSE', 'Groceries',    '2024-07-10', 'Monthly grocery shopping'),
    (admin_id,  3200.00, 'EXPENSE', 'Transport',    '2024-07-12', 'Fuel and commute costs'),
    (admin_id, 12000.00, 'INCOME',  'Freelance',    '2024-07-15', 'UI design project payment'),
    (admin_id,  6800.00, 'EXPENSE', 'Dining',       '2024-07-20', 'Team lunch and client dinners'),
    (admin_id,  2500.00, 'EXPENSE', 'Subscriptions','2024-07-25', 'SaaS tools and streaming services'),
    (admin_id,  5000.00, 'INCOME',  'Consulting',   '2024-07-28', 'Strategic consulting session');

    -- ==================
    -- AUGUST 2024
    -- ==================
    INSERT INTO financial_records (created_by, amount, type, category, record_date, description) VALUES
    (admin_id, 95000.00, 'INCOME',  'Salary',       '2024-08-01', 'Monthly salary - August'),
    (admin_id, 18000.00, 'EXPENSE', 'Rent',          '2024-08-02', 'Office rent - August'),
    (admin_id,  3900.00, 'EXPENSE', 'Utilities',     '2024-08-05', 'Electricity and internet bills'),
    (admin_id,  7200.00, 'EXPENSE', 'Groceries',     '2024-08-08', 'Monthly grocery shopping'),
    (admin_id,  3500.00, 'EXPENSE', 'Transport',     '2024-08-14', 'Fuel and cab fares'),
    (admin_id, 20000.00, 'INCOME',  'Freelance',     '2024-08-16', 'Backend API development project'),
    (admin_id,  9500.00, 'EXPENSE', 'Equipment',     '2024-08-18', 'Mechanical keyboard and monitor stand'),
    (admin_id,  4100.00, 'EXPENSE', 'Dining',        '2024-08-22', 'Client entertainment'),
    (admin_id,  2500.00, 'EXPENSE', 'Subscriptions', '2024-08-25', 'SaaS tools and streaming services'),
    (admin_id, 15000.00, 'INCOME',  'Investment',    '2024-08-30', 'Dividend payout from mutual funds');

    -- ==================
    -- SEPTEMBER 2024
    -- ==================
    INSERT INTO financial_records (created_by, amount, type, category, record_date, description) VALUES
    (admin_id, 95000.00, 'INCOME',  'Salary',       '2024-09-01', 'Monthly salary - September'),
    (admin_id, 18000.00, 'EXPENSE', 'Rent',          '2024-09-02', 'Office rent - September'),
    (admin_id,  4500.00, 'EXPENSE', 'Utilities',     '2024-09-05', 'Electricity and internet bills'),
    (admin_id,  8800.00, 'EXPENSE', 'Groceries',     '2024-09-09', 'Monthly grocery shopping'),
    (admin_id,  2800.00, 'EXPENSE', 'Transport',     '2024-09-11', 'Monthly transport pass and fuel'),
    (admin_id,  7500.00, 'INCOME',  'Consulting',    '2024-09-14', 'Business strategy consulting'),
    (admin_id, 35000.00, 'EXPENSE', 'Travel',        '2024-09-18', 'Business trip flights and hotel'),
    (admin_id,  5200.00, 'EXPENSE', 'Dining',        '2024-09-21', 'Conference networking dinners'),
    (admin_id,  2500.00, 'EXPENSE', 'Subscriptions', '2024-09-25', 'SaaS tools and streaming services'),
    (admin_id,  8000.00, 'INCOME',  'Freelance',     '2024-09-27', 'Mobile app design consultation');

    -- ==================
    -- OCTOBER 2024
    -- ==================
    INSERT INTO financial_records (created_by, amount, type, category, record_date, description) VALUES
    (admin_id,  95000.00, 'INCOME',  'Salary',       '2024-10-01', 'Monthly salary - October'),
    (admin_id,  18000.00, 'EXPENSE', 'Rent',          '2024-10-02', 'Office rent - October'),
    (admin_id,   4100.00, 'EXPENSE', 'Utilities',     '2024-10-05', 'Electricity and internet bills'),
    (admin_id,   9200.00, 'EXPENSE', 'Groceries',     '2024-10-07', 'Monthly grocery shopping'),
    (admin_id,   3100.00, 'EXPENSE', 'Transport',     '2024-10-10', 'Fuel and cab fares'),
    (admin_id,  25000.00, 'INCOME',  'Freelance',     '2024-10-13', 'Full-stack web application project'),
    (admin_id,  12000.00, 'EXPENSE', 'Equipment',     '2024-10-16', 'External SSD and webcam upgrade'),
    (admin_id,   3800.00, 'EXPENSE', 'Dining',        '2024-10-19', 'Team outing and client lunch'),
    (admin_id,   2500.00, 'EXPENSE', 'Subscriptions', '2024-10-25', 'SaaS tools and streaming services'),
    (admin_id,  10000.00, 'INCOME',  'Investment',    '2024-10-28', 'Stock dividend income'),
    (admin_id,  50000.00, 'EXPENSE', 'Tax',           '2024-10-30', 'Quarterly advance tax payment');

    -- ==================
    -- NOVEMBER 2024
    -- ==================
    INSERT INTO financial_records (created_by, amount, type, category, record_date, description) VALUES
    (admin_id,  95000.00, 'INCOME',  'Salary',       '2024-11-01', 'Monthly salary - November'),
    (admin_id,  18000.00, 'EXPENSE', 'Rent',          '2024-11-02', 'Office rent - November'),
    (admin_id,   4300.00, 'EXPENSE', 'Utilities',     '2024-11-05', 'Electricity and internet bills'),
    (admin_id,   7600.00, 'EXPENSE', 'Groceries',     '2024-11-08', 'Monthly grocery shopping'),
    (admin_id,   2900.00, 'EXPENSE', 'Transport',     '2024-11-11', 'Fuel and monthly pass'),
    (admin_id,  18000.00, 'INCOME',  'Consulting',    '2024-11-14', 'Product roadmap consulting'),
    (admin_id,   5500.00, 'EXPENSE', 'Dining',        '2024-11-17', 'Client dinners and team events'),
    (admin_id,   2500.00, 'EXPENSE', 'Subscriptions', '2024-11-25', 'SaaS tools and streaming services'),
    (admin_id,  22000.00, 'INCOME',  'Freelance',     '2024-11-26', 'Data pipeline development project'),
    (admin_id,   6200.00, 'EXPENSE', 'Health',        '2024-11-28', 'Health insurance premium and checkup');

    -- ==================
    -- DECEMBER 2024
    -- ==================
    INSERT INTO financial_records (created_by, amount, type, category, record_date, description) VALUES
    (admin_id, 110000.00, 'INCOME',  'Salary',       '2024-12-01', 'Monthly salary + year-end bonus'),
    (admin_id,  18000.00, 'EXPENSE', 'Rent',          '2024-12-02', 'Office rent - December'),
    (admin_id,   5100.00, 'EXPENSE', 'Utilities',     '2024-12-05', 'Electricity and internet bills'),
    (admin_id,  12500.00, 'EXPENSE', 'Groceries',     '2024-12-10', 'Festive season shopping'),
    (admin_id,   4200.00, 'EXPENSE', 'Transport',     '2024-12-12', 'Holiday travel and fuel'),
    (admin_id,  30000.00, 'INCOME',  'Freelance',     '2024-12-14', 'Year-end sprint project completion'),
    (admin_id,  28000.00, 'EXPENSE', 'Travel',        '2024-12-20', 'Family holiday trip'),
    (admin_id,   8500.00, 'EXPENSE', 'Dining',        '2024-12-22', 'Festive dinners and celebrations'),
    (admin_id,   2500.00, 'EXPENSE', 'Subscriptions', '2024-12-25', 'SaaS tools and streaming services'),
    (admin_id,  20000.00, 'INCOME',  'Investment',    '2024-12-28', 'Year-end portfolio rebalancing gains'),
    (admin_id,   9800.00, 'EXPENSE', 'Health',        '2024-12-30', 'Annual health checkup and dental');

END $$;