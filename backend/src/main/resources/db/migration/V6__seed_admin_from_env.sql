INSERT INTO users (id, email, password_hash, full_name, role, created_at)
SELECT gen_random_uuid(),
       '${admin.email}',
       '${admin.password-bcrypt}',
       'System Admin',
       'ADMIN',
       NOW()
WHERE '${admin.email}' <> '__DISABLED__'
  AND '${admin.password-bcrypt}' <> '__DISABLED__'
  AND NOT EXISTS (
      SELECT 1
      FROM users
      WHERE role = 'ADMIN'
  );
