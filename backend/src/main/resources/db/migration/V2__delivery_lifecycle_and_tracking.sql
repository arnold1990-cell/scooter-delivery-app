CREATE TYPE IF NOT EXISTS delivery_status_new AS ENUM (
  'PENDING', 'ASSIGNED', 'PICKED_UP', 'IN_TRANSIT', 'DELIVERED', 'CANCELLED', 'FAILED'
);

ALTER TABLE deliveries
  ALTER COLUMN status DROP DEFAULT;

ALTER TABLE deliveries
  ALTER COLUMN status TYPE delivery_status_new
  USING (
    CASE status::text
      WHEN 'REQUESTED' THEN 'PENDING'
      WHEN 'REJECTED' THEN 'FAILED'
      WHEN 'ACCEPTED' THEN 'ASSIGNED'
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

ALTER TABLE deliveries
  ALTER COLUMN status SET DEFAULT 'PENDING'::delivery_status_new;

ALTER TYPE delivery_status RENAME TO delivery_status_old;
ALTER TYPE delivery_status_new RENAME TO delivery_status;
DROP TYPE IF EXISTS delivery_status_old;

ALTER TABLE deliveries
  ADD COLUMN pickup_latitude NUMERIC(10,7),
  ADD COLUMN pickup_longitude NUMERIC(10,7),
  ADD COLUMN dropoff_latitude NUMERIC(10,7),
  ADD COLUMN dropoff_longitude NUMERIC(10,7),
  ADD COLUMN estimated_pickup_time TIMESTAMP,
  ADD COLUMN estimated_delivery_time TIMESTAMP,
  ADD COLUMN distance_km NUMERIC(10,3);

CREATE TABLE delivery_status_history (
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

CREATE INDEX idx_delivery_status_history_delivery ON delivery_status_history(delivery_id, created_at);

CREATE TYPE rider_status AS ENUM ('AVAILABLE', 'BUSY', 'OFFLINE');
ALTER TABLE rider_profiles
  ADD COLUMN status rider_status NOT NULL DEFAULT 'OFFLINE';

CREATE TABLE rider_location (
  rider_id UUID PRIMARY KEY REFERENCES users(id),
  latitude NUMERIC(10,7) NOT NULL,
  longitude NUMERIC(10,7) NOT NULL,
  updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
