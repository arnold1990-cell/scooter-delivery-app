CREATE TYPE payment_method AS ENUM ('CASH', 'MOBILE', 'CARD');
CREATE TYPE payment_status AS ENUM ('PENDING', 'PAID', 'FAILED');

CREATE TABLE payments (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  delivery_id UUID NOT NULL UNIQUE REFERENCES deliveries(id),
  amount NUMERIC(12,2) NOT NULL,
  method payment_method NOT NULL,
  status payment_status NOT NULL DEFAULT 'PENDING',
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE pricing_config (
  id SMALLINT PRIMARY KEY,
  base_fare NUMERIC(12,2) NOT NULL,
  per_km_rate NUMERIC(12,2) NOT NULL,
  peak_multiplier NUMERIC(6,2) NOT NULL DEFAULT 1.0,
  updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

INSERT INTO pricing_config (id, base_fare, per_km_rate, peak_multiplier)
VALUES (1, 5.00, 1.50, 1.00);

CREATE TYPE notification_type AS ENUM ('DELIVERY_ASSIGNED', 'DELIVERY_PICKED_UP', 'DELIVERY_DELIVERED', 'SYSTEM');

CREATE TABLE notifications (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID NOT NULL REFERENCES users(id),
  message TEXT NOT NULL,
  type notification_type NOT NULL,
  read BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE rider_ratings (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  delivery_id UUID NOT NULL UNIQUE REFERENCES deliveries(id),
  rider_id UUID NOT NULL REFERENCES users(id),
  customer_id UUID NOT NULL REFERENCES users(id),
  rating INT NOT NULL,
  comment TEXT,
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  CONSTRAINT rider_ratings_rating_range CHECK (rating BETWEEN 1 AND 5)
);
