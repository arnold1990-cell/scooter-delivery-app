-- Move post-V2 adjustments into a new migration to preserve Flyway checksum history.

-- Ensure deliveries.status has a valid default for the new delivery_status enum.
ALTER TABLE deliveries
  ALTER COLUMN status SET DEFAULT 'PENDING'::delivery_status;

-- Add newly introduced rider approval state.
ALTER TYPE approval_status ADD VALUE IF NOT EXISTS 'SUSPENDED';
