-- =========================
-- USERS (password = 123456)
-- =========================
INSERT INTO users (email, mobile, password, role, active, created_at)
VALUES
('admin@shopee.com', '9999990000',
 '$2a$10$7EqJtq98hPqEX7fNZaFWoOhi5oZsYj1N0G6K1xXjL9R9X3Tn5hK6W',
 'SUPER_ADMIN', true, now()),

('rahul@shopee.com', '8888880000',
 '$2a$10$7EqJtq98hPqEX7fNZaFWoOhi5oZsYj1N0G6K1xXjL9R9X3Tn5hK6W',
 'FRANCHISE_ADMIN', true, now()),

('amit@shopee.com', '7777770000',
 '$2a$10$7EqJtq98hPqEX7fNZaFWoOhi5oZsYj1N0G6K1xXjL9R9X3Tn5hK6W',
 'FRANCHISE_ADMIN', true, now());

-- =========================
-- FRANCHISES
-- =========================
INSERT INTO franchise (outlet_name, owner_id, address, city, state, valid_from, valid_to, created_at)
VALUES
(
 'Shopee Andheri',
 (SELECT user_id FROM users WHERE email='rahul@shopee.com'),
 'Shop 12', 'Mumbai', 'MH', '2025-01-01', '2026-01-01', now()
),
(
 'Shopee Bandra',
 (SELECT user_id FROM users WHERE email='rahul@shopee.com'),
 'Shop 45', 'Mumbai', 'MH', '2025-01-01', '2026-01-01', now()
),
(
 'Shopee Pune',
 (SELECT user_id FROM users WHERE email='amit@shopee.com'),
 'FC Road', 'Pune', 'MH', '2025-01-01', '2026-01-01', now()
);
