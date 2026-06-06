-- =========================
-- AUDIT LOG
-- =========================
CREATE TABLE IF NOT EXISTS audit_log (
    id             BIGSERIAL PRIMARY KEY,
    actor_id       BIGINT        NOT NULL,
    actor_email    VARCHAR(255)  NOT NULL,
    actor_role     VARCHAR(50)   NOT NULL,
    action         VARCHAR(100)  NOT NULL,
    entity_type    VARCHAR(100)  NOT NULL,
    entity_id      VARCHAR(255)  NOT NULL,
    old_value      TEXT,
    new_value      TEXT,
    ip_address     VARCHAR(45),
    timestamp      TIMESTAMP     NOT NULL DEFAULT NOW(),
    franchise_id   BIGINT
);

CREATE INDEX IF NOT EXISTS idx_audit_log_actor_id     ON audit_log (actor_id);
CREATE INDEX IF NOT EXISTS idx_audit_log_entity       ON audit_log (entity_type, entity_id);
CREATE INDEX IF NOT EXISTS idx_audit_log_franchise_ts ON audit_log (franchise_id, timestamp);
CREATE INDEX IF NOT EXISTS idx_audit_log_action       ON audit_log (action);

-- =========================
-- REFRESH TOKEN
-- =========================
CREATE TABLE IF NOT EXISTS refresh_token (
    id          BIGSERIAL PRIMARY KEY,
    token       VARCHAR(36)  NOT NULL UNIQUE,
    user_id     BIGINT       NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    expires_at  TIMESTAMP    NOT NULL,
    revoked     BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_refresh_token_token   ON refresh_token (token);
CREATE INDEX IF NOT EXISTS idx_refresh_token_user_id ON refresh_token (user_id);

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

