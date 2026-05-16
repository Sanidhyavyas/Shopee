-- =========================
-- USERS (password = 123456)
-- =========================
INSERT INTO users (name, email, mobile, password, role, active, created_at)
VALUES
('Super Admin', 'admin@shopee.com', '9999990000',
 '$2a$10$7EqJtq98hPqEX7fNZaFWoOhi5oZsYj1N0G6K1xXjL9R9X3Tn5hK6W',
 'SUPER_ADMIN', true, now()),

('Rahul Kumar', 'rahul@shopee.com', '8888880000',
 '$2a$10$7EqJtq98hPqEX7fNZaFWoOhi5oZsYj1N0G6K1xXjL9R9X3Tn5hK6W',
 'FRANCHISE_ADMIN', true, now()),

('Amit Sharma', 'amit@shopee.com', '7777770000',
 '$2a$10$7EqJtq98hPqEX7fNZaFWoOhi5oZsYj1N0G6K1xXjL9R9X3Tn5hK6W',
 'FRANCHISE_ADMIN', true, now())
ON CONFLICT (email) DO NOTHING;

-- =========================
-- FRANCHISES
-- =========================
INSERT INTO franchise (outlet_name, owner_id, address, city, state, valid_from, valid_to, created_at)
SELECT 'Shopee Andheri',
       (SELECT user_id FROM users WHERE email='rahul@shopee.com'),
       'Shop 12', 'Mumbai', 'MH', '2025-01-01', '2026-01-01', now()
WHERE NOT EXISTS (SELECT 1 FROM franchise WHERE outlet_name='Shopee Andheri');

INSERT INTO franchise (outlet_name, owner_id, address, city, state, valid_from, valid_to, created_at)
SELECT 'Shopee Bandra',
       (SELECT user_id FROM users WHERE email='rahul@shopee.com'),
       'Shop 45', 'Mumbai', 'MH', '2025-01-01', '2026-01-01', now()
WHERE NOT EXISTS (SELECT 1 FROM franchise WHERE outlet_name='Shopee Bandra');

INSERT INTO franchise (outlet_name, owner_id, address, city, state, valid_from, valid_to, created_at)
SELECT 'Shopee Pune',
       (SELECT user_id FROM users WHERE email='amit@shopee.com'),
       'FC Road', 'Pune', 'MH', '2025-01-01', '2026-01-01', now()
WHERE NOT EXISTS (SELECT 1 FROM franchise WHERE outlet_name='Shopee Pune');

