CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TYPE user_role AS ENUM ('CUSTOMER', 'RIDER', 'ADMIN');
CREATE TYPE approval_status AS ENUM ('PENDING', 'APPROVED', 'REJECTED');
CREATE TYPE delivery_status AS ENUM (
  'REQUESTED','ASSIGNED','ACCEPTED','PICKED_UP','IN_TRANSIT','DELIVERED','CANCELLED','REJECTED'
);

CREATE TABLE users (
  id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  email         VARCHAR(255) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  full_name     VARCHAR(255) NOT NULL,
  role          user_role    NOT NULL DEFAULT 'CUSTOMER',
  created_at    TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE rider_profiles (
  id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id         UUID NOT NULL UNIQUE REFERENCES users(id),
  license_number  VARCHAR(100),
  approval_status approval_status NOT NULL DEFAULT 'PENDING',
  is_online       BOOLEAN NOT NULL DEFAULT FALSE,
  created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE deliveries (
  id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  customer_id      UUID NOT NULL REFERENCES users(id),
  rider_id         UUID REFERENCES users(id),
  pickup_address   TEXT NOT NULL,
  dropoff_address  TEXT NOT NULL,
  price            NUMERIC(10,2) NOT NULL DEFAULT 0,
  status           delivery_status NOT NULL DEFAULT 'REQUESTED',
  notes            TEXT,
  created_at       TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at       TIMESTAMP NOT NULL DEFAULT NOW()
);
