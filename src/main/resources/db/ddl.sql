CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS pgcrypto;
CREATE TABLE IF NOT EXISTS users (id UUID PRIMARY KEY, name TEXT NOT NULL, email TEXT NOT NULL UNIQUE, password_hash TEXT NOT NULL, created_at TIMESTAMP NOT NULL, role TEXT DEFAULT 'user');
CREATE TABLE IF NOT EXISTS listings (id UUID PRIMARY KEY, public_id TEXT UNIQUE, owner_id UUID NOT NULL REFERENCES users(id), title TEXT NOT NULL, description TEXT NOT NULL, price NUMERIC(12,2) NOT NULL, category TEXT NOT NULL, city TEXT NOT NULL, state TEXT NOT NULL, contact_phone TEXT, created_at TIMESTAMP NOT NULL, views INT NOT NULL DEFAULT 0, is_active BOOLEAN NOT NULL DEFAULT TRUE);
CREATE INDEX IF NOT EXISTS idx_listings_category ON listings(category);
CREATE INDEX IF NOT EXISTS idx_listings_city ON listings(city);
CREATE INDEX IF NOT EXISTS idx_listings_created_at ON listings(created_at);
CREATE TABLE IF NOT EXISTS listing_images (id BIGSERIAL PRIMARY KEY, listing_id UUID NOT NULL REFERENCES listings(id) ON DELETE CASCADE, url TEXT NOT NULL);
CREATE TABLE IF NOT EXISTS favorites (id BIGSERIAL PRIMARY KEY, user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE, listing_id UUID NOT NULL REFERENCES listings(id) ON DELETE CASCADE, CONSTRAINT uk_fav_user_listing UNIQUE (user_id, listing_id));
