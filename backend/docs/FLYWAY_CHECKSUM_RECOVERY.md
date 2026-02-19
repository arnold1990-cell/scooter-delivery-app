# Flyway V2 checksum mismatch recovery (Spring Boot + PostgreSQL)

This runbook addresses:

- `Validate failed: Migrations have failed validation`
- `Migration checksum mismatch for migration version 2`

It is scoped to Flyway/database migration handling only.

## 1) Check Flyway state first

Use this query to see whether `V2` is marked `SUCCESS` or `FAILED` and inspect the full history.

```sql
SELECT *
FROM flyway_schema_history
ORDER BY installed_rank;
```

Optional focused check:

```sql
SELECT installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success
FROM flyway_schema_history
WHERE version = '2';
```

---

## 2) Two valid fix paths

## Option A (recommended for local dev): reset database/schema and re-run migrations

Best when you do **not** need to preserve existing local data.

### A1. Reset only `public` schema (psql)

```sql
DROP SCHEMA public CASCADE;
CREATE SCHEMA public;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO public;
```

Then start the app again so Flyway applies `V1..Vn` from scratch.

### A2. Drop and recreate the database (psql)

Run connected to `postgres` (not `scooterdb`):

```sql
SELECT pg_terminate_backend(pid)
FROM pg_stat_activity
WHERE datname = 'scooterdb'
  AND pid <> pg_backend_pid();

DROP DATABASE IF EXISTS scooterdb;
CREATE DATABASE scooterdb;
```

Then start the app again.

### A3. Windows PowerShell examples

```powershell
# Reset schema
psql -h localhost -p 5432 -U postgres -d scooterdb -c "DROP SCHEMA public CASCADE; CREATE SCHEMA public; GRANT ALL ON SCHEMA public TO postgres; GRANT ALL ON SCHEMA public TO public;"

# OR recreate DB (connect to postgres DB)
psql -h localhost -p 5432 -U postgres -d postgres -c "SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE datname='scooterdb' AND pid <> pg_backend_pid();"
psql -h localhost -p 5432 -U postgres -d postgres -c "DROP DATABASE IF EXISTS scooterdb;"
psql -h localhost -p 5432 -U postgres -d postgres -c "CREATE DATABASE scooterdb;"
```

---

## Option B (keep existing data): do not edit applied V2 again; add a new V3

If data must be preserved:

1. Restore `V2__...sql` so it matches what was originally applied in DB history.
2. Put new intended changes into a **new** migration file (e.g., `V3__<describe_changes>.sql`).
3. Start app; Flyway validates old migrations and applies `V3`.

### V3 placeholder template

```sql
-- V3__apply_delivery_updates.sql
-- Move any changes that were incorrectly edited into V2 into this new migration.

-- Example:
-- ALTER TABLE deliveries ADD COLUMN IF NOT EXISTS ...;
-- CREATE TABLE IF NOT EXISTS ...;
-- ALTER TYPE ...;
```

This is the production-safe migration path because migration history remains immutable.

---

## 3) If you intentionally keep modified V2 in local dev: `repair`

Use only when you accept updating Flyway metadata checksums.

> `flyway repair` updates entries in `flyway_schema_history` (checksums/descriptions) and removes failed markers where possible. It **does not** apply missing schema changes.

### Maven Flyway repair command

```powershell
mvn -Dflyway.url=jdbc:postgresql://localhost:5432/scooterdb -Dflyway.user=postgres -Dflyway.password=postgres flyway:repair
```

After repair:

1. Run app / `flyway:migrate`.
2. Verify schema has all intended objects.
3. If anything is still missing, create a new `V3` migration for those deltas.

### SQL-only fallback (only if Flyway tooling unavailable)

Manual updates to `flyway_schema_history` are a last resort and are error-prone. Prefer `flyway:repair`.

---

## 4) Verification SQL (post-fix)

### Flyway history

```sql
SELECT installed_rank, version, description, script, checksum, success
FROM flyway_schema_history
ORDER BY installed_rank;
```

### Verify expected delivery lifecycle objects exist

```sql
-- columns on deliveries
SELECT column_name, data_type, udt_name
FROM information_schema.columns
WHERE table_schema = 'public'
  AND table_name = 'deliveries'
ORDER BY ordinal_position;

-- required tables
SELECT table_name
FROM information_schema.tables
WHERE table_schema = 'public'
  AND table_name IN ('delivery_status_history', 'rider_location', 'payments', 'pricing_config', 'notifications', 'rider_ratings')
ORDER BY table_name;

-- enum values for delivery_status
SELECT e.enumlabel
FROM pg_enum e
JOIN pg_type t ON t.oid = e.enumtypid
WHERE t.typname = 'delivery_status'
ORDER BY e.enumsortorder;
```

---

## Final decision guidance

- **Local dev, no important data**: choose **Option A** (reset) for the fastest, cleanest recovery.
- **Need to keep data**: choose **Option B** and deliver changes in a new `V3` migration.
