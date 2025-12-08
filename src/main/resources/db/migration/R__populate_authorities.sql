-- Insert predefined authorities (idempotent - safe to run multiple times)
INSERT INTO authorities (authority) VALUES
    ('HELLO2'),
    ('READ_PRODUCTS')
ON CONFLICT (authority) DO NOTHING;