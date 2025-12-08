-- Many-to-many: users <-> merchants
CREATE TABLE users_merchants (
    user_id INT8 NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    merchant_id TEXT NOT NULL REFERENCES merchants(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, merchant_id)
);

-- Many-to-many: users <-> warehouses
CREATE TABLE users_warehouses (
    user_id INT8 NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    warehouse_id TEXT NOT NULL REFERENCES warehouses(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, warehouse_id)
);
