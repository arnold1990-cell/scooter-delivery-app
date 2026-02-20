INSERT INTO users (id, email, password_hash, full_name, role, created_at)
SELECT gen_random_uuid(),
       'admin@example.com',
       crypt('Admin@12345', gen_salt('bf')),
       'System Admin',
       'ADMIN',
       NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE email = 'admin@example.com'
);
