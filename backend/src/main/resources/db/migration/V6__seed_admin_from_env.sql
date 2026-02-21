INSERT INTO users (id, email, password_hash, full_name, role, created_at)
SELECT gen_random_uuid(),
       '${admin.email}',
       '${admin.password-bcrypt}',
       'System Admin',
       'ADMIN',
       NOW()
WHERE '${admin.email}' <> ''
  AND '${admin.password-bcrypt}' <> ''
  AND NOT EXISTS (
      SELECT 1
      FROM users
      WHERE role = 'ADMIN'
  );
