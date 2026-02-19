DO $$
BEGIN
  CREATE TYPE delivery_status_new AS ENUM (
    'PENDING', 'ASSIGNED', 'PICKED_UP', 'IN_TRANSIT', 'DELIVERED', 'CANCELLED', 'FAILED'
  );
EXCEPTION
  WHEN duplicate_object THEN NULL;
END $$;

DO $$
BEGIN
  IF EXISTS (
    SELECT 1
    FROM information_schema.columns
    WHERE table_schema = 'public'
      AND table_name = 'deliveries'
      AND column_name = 'status'
      AND udt_name = 'delivery_status'
  ) THEN
    ALTER TABLE deliveries
      ALTER COLUMN status DROP DEFAULT;

    ALTER TABLE deliveries
      ALTER COLUMN status TYPE delivery_status_new
      USING (
        CASE status::text
          WHEN 'REQUESTED' THEN 'PENDING'
          WHEN 'ACCEPTED' THEN 'ASSIGNED'
          WHEN 'REJECTED' THEN 'FAILED'
          WHEN 'PENDING' THEN 'PENDING'
          WHEN 'ASSIGNED' THEN 'ASSIGNED'
          WHEN 'PICKED_UP' THEN 'PICKED_UP'
          WHEN 'IN_TRANSIT' THEN 'IN_TRANSIT'
          WHEN 'DELIVERED' THEN 'DELIVERED'
          WHEN 'CANCELLED' THEN 'CANCELLED'
          WHEN 'FAILED' THEN 'FAILED'
          ELSE 'PENDING'
        END
      )::delivery_status_new;
  END IF;
END $$;

ALTER TABLE deliveries
  ALTER COLUMN status SET DEFAULT 'PENDING'::delivery_status_new;

DO $$
BEGIN
  IF EXISTS (SELECT 1 FROM pg_type WHERE typname = 'delivery_status')
     AND EXISTS (SELECT 1 FROM pg_type WHERE typname = 'delivery_status_new') THEN
    ALTER TYPE delivery_status RENAME TO delivery_status_old;
    ALTER TYPE delivery_status_new RENAME TO delivery_status;
    DROP TYPE IF EXISTS delivery_status_old;
  ELSIF EXISTS (SELECT 1 FROM pg_type WHERE typname = 'delivery_status_new')
     AND NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'delivery_status') THEN
    ALTER TYPE delivery_status_new RENAME TO delivery_status;
  END IF;
END $$;

ALTER TABLE deliveries
  ALTER COLUMN status SET DEFAULT 'PENDING'::delivery_status;

ALTER TABLE deliveries
  ADD COLUMN IF NOT EXISTS pickup_latitude NUMERIC(10,7),
  ADD COLUMN IF NOT EXISTS pickup_longitude NUMERIC(10,7),
  ADD COLUMN IF NOT EXISTS dropoff_latitude NUMERIC(10,7),
  ADD COLUMN IF NOT EXISTS dropoff_longitude NUMERIC(10,7),
  ADD COLUMN IF NOT EXISTS estimated_pickup_time TIMESTAMP,
  ADD COLUMN IF NOT EXISTS estimated_delivery_time TIMESTAMP,
  ADD COLUMN IF NOT EXISTS distance_km NUMERIC(10,3);

CREATE TABLE IF NOT EXISTS delivery_status_history (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  delivery_id UUID NOT NULL REFERENCES deliveries(id),
  status delivery_status NOT NULL,
  changed_by_user_id UUID REFERENCES users(id),
  changed_by_role VARCHAR(30),
  notes TEXT,
  latitude NUMERIC(10,7),
  longitude NUMERIC(10,7),
  created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_delivery_status_history_delivery ON delivery_status_history(delivery_id, created_at);

DO $$
BEGIN
  CREATE TYPE rider_status AS ENUM ('AVAILABLE', 'BUSY', 'OFFLINE');
EXCEPTION
  WHEN duplicate_object THEN NULL;
END $$;

ALTER TABLE rider_profiles
  ADD COLUMN IF NOT EXISTS status rider_status NOT NULL DEFAULT 'OFFLINE';

DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1
    FROM pg_enum e
    JOIN pg_type t ON t.oid = e.enumtypid
    WHERE t.typname = 'approval_status'
      AND e.enumlabel = 'SUSPENDED'
  ) THEN
    ALTER TYPE approval_status ADD VALUE 'SUSPENDED';
  END IF;
END $$;

CREATE TABLE IF NOT EXISTS rider_location (
  rider_id UUID PRIMARY KEY REFERENCES users(id),
  latitude NUMERIC(10,7) NOT NULL,
  longitude NUMERIC(10,7) NOT NULL,
  updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
