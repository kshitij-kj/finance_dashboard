-- Seeds a default admin user so the system is usable immediately after deployment.
-- Password is: Admin@1234
-- BCrypt hash generated with strength 10.
INSERT INTO users (email, password_hash, full_name, role, is_active)
VALUES (
    'admin@finance.com',
    '$2a$10$gIF3Fv1qtBEjUby8b7o36e1ojfMpiCS2DQRKatsB6.ZVnYS8IVh6m',
    'System Admin',
    'ADMIN',
    TRUE
)
ON CONFLICT (email) DO NOTHING;