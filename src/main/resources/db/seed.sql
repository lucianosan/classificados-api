CREATE EXTENSION IF NOT EXISTS pgcrypto;
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'public' AND table_name = 'users' AND column_name = 'role'
  ) THEN
    ALTER TABLE public.users ADD COLUMN role TEXT NOT NULL DEFAULT 'user';
  END IF;
END $$;
ALTER TABLE public.users ALTER COLUMN created_at SET DEFAULT NOW();
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint WHERE conname = 'users_email_key' AND conrelid = 'public.users'::regclass
  ) THEN
    ALTER TABLE public.users ADD CONSTRAINT users_email_key UNIQUE (email);
  END IF;
END $$;
-- Usuários com UUIDs fixos; evita falhas em FK
INSERT INTO users (id, name, email, password_hash, created_at, role) VALUES
('11111111-1111-1111-1111-111111111111','Alice','alice@example.com','5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', NOW(), 'user'),
('22222222-2222-2222-2222-222222222222','Bruno','bruno@example.com','5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', NOW(), 'user')
ON CONFLICT (email) DO NOTHING;

-- Anúncios referenciando owner por email para evitar erro de FK 
INSERT INTO listings (id, owner_id, title, description, price, category, city, state, contact_phone, created_at, views, is_active) VALUES 
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', (SELECT id FROM users WHERE email='alice@example.com'), 'iPhone 13', 'iPhone 13 128GB, ótimo estado', 3500.00, 'Eletrônicos', 'São Paulo', 'SP', '11999999999', NOW(), 12, TRUE), 
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', (SELECT id FROM users WHERE email='alice@example.com'), 'Apartamento 2 quartos', 'Apartamento bem localizado', 250000.00, 'Imóveis', 'Rio de Janeiro', 'RJ', '21988888888', NOW(), 5, TRUE),
('cccccccc-cccc-cccc-cccc-cccccccccccc', (SELECT id FROM users WHERE email='bruno@example.com'), 'VW Gol 2015', 'Carro econômico, IPVA pago', 28000.00, 'Autos', 'Belo Horizonte', 'MG', '31977777777', NOW(), 32, TRUE),
('dddddddd-dddd-dddd-dddd-dddddddddddd', (SELECT id FROM users WHERE email='alice@example.com'), 'Sofá Retrátil', 'Sofá 2,30m, tecido suede', 1600.00, 'Móveis', 'Curitiba', 'PR', '41966666666', NOW(), 10, TRUE),
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', (SELECT id FROM users WHERE email='bruno@example.com'), 'Bicicleta Caloi', 'Aro 29, 21 marchas', 1200.00, 'Esportes', 'Porto Alegre', 'RS', '51955555555', NOW(), 8, TRUE),
('ffffffff-ffff-ffff-ffff-ffffffffffff', (SELECT id FROM users WHERE email='alice@example.com'), 'Serviço de Pintura', 'Pintura residencial e comercial', 800.00, 'Serviços', 'São Paulo', 'SP', '11944444444', NOW(), 21, TRUE),
('99999999-9999-9999-9999-999999999999', (SELECT id FROM users WHERE email='bruno@example.com'), 'Filhote de Labrador', '12 semanas, vacinado', 2500.00, 'Pets', 'Florianópolis', 'SC', '48933333333', NOW(), 14, TRUE),
('88888888-8888-8888-8888-888888888888', (SELECT id FROM users WHERE email='alice@example.com'), 'Jaqueta de Couro', 'Tamanho M, pouco uso', 350.00, 'Moda', 'Niterói', 'RJ', '21922222222', NOW(), 6, TRUE)
ON CONFLICT (id) DO NOTHING; 

-- Garante coluna de ID curto
ALTER TABLE public.listings ADD COLUMN IF NOT EXISTS public_id TEXT UNIQUE;
 
-- Atribui public_id curto (10 chars) para registros existentes 
UPDATE listings SET public_id = SUBSTRING(REPLACE(id::text,'-','') FROM 1 FOR 10) WHERE public_id IS NULL; 

-- Imagens
INSERT INTO listing_images (listing_id, url) SELECT id, '/assets/iphone.jpg' FROM listings WHERE title='iPhone 13';
INSERT INTO listing_images (listing_id, url) SELECT id, '/assets/apto.jpg' FROM listings WHERE title='Apartamento 2 quartos';
INSERT INTO listing_images (listing_id, url) SELECT id, '/assets/gol.jpg' FROM listings WHERE title='VW Gol 2015';
INSERT INTO listing_images (listing_id, url) SELECT id, '/assets/sofa.jpg' FROM listings WHERE title='Sofá Retrátil';
INSERT INTO listing_images (listing_id, url) SELECT id, '/assets/bike.jpg' FROM listings WHERE title='Bicicleta Caloi';
INSERT INTO listing_images (listing_id, url) SELECT id, '/assets/pintura.jpg' FROM listings WHERE title='Serviço de Pintura';
INSERT INTO listing_images (listing_id, url) SELECT id, '/assets/labrador.jpg' FROM listings WHERE title='Filhote de Labrador';
INSERT INTO listing_images (listing_id, url) SELECT id, '/assets/jaqueta.jpg' FROM listings WHERE title='Jaqueta de Couro';

-- Favoritos
INSERT INTO favorites (user_id, listing_id)
SELECT (SELECT id FROM users WHERE email='bruno@example.com'), id FROM listings WHERE title IN ('iPhone 13','VW Gol 2015')
ON CONFLICT (user_id, listing_id) DO NOTHING;
INSERT INTO users (id, name, email, password_hash, created_at, role)
VALUES ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Administrador', 'admin@site.com', encode(digest('admin123','sha256'),'hex'), NOW(), 'admin')
ON CONFLICT (email) DO NOTHING;
