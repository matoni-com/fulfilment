CREATE TABLE addresses (
    id               INT8 PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name             TEXT,
    last_name        TEXT,
    additional_name  TEXT,
    company_name     TEXT,
    street           TEXT NOT NULL,
    street2          TEXT,
    house_number     TEXT NOT NULL,
    zip              TEXT NOT NULL,
    city             TEXT NOT NULL,
    country          TEXT NOT NULL,
    postbox          TEXT,
    gps_location     TEXT,
    telephone_number TEXT,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT Now(),
    updated_at       TIMESTAMPTZ NOT NULL DEFAULT Now()
);

CREATE TABLE merchants (
    id TEXT PRIMARY KEY,
    company_name TEXT,
    address_id INT8 NOT NULL UNIQUE REFERENCES addresses(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT Now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT Now()
);

CREATE TABLE warehouses (
    id TEXT PRIMARY KEY,
    warehouse_name TEXT,
    address_id INT8 NOT NULL UNIQUE REFERENCES addresses(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT Now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT Now()
);

CREATE TABLE clients (
    id INT8 PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    api_key TEXT NOT NULL UNIQUE,
    api_secret TEXT NOT NULL,
    role TEXT NOT NULL
);

CREATE TABLE merchants_clients (
    merchant_id TEXT NOT NULL REFERENCES merchants(id) ON DELETE CASCADE,
    client_id INT8 NOT NULL UNIQUE REFERENCES clients(id) ON DELETE CASCADE,
    UNIQUE(merchant_id, client_id)
);

CREATE TABLE warehouses_clients (
    warehouse_id TEXT NOT NULL REFERENCES warehouses(id) ON DELETE CASCADE,
    client_id INT8 NOT NULL UNIQUE REFERENCES clients(id) ON DELETE CASCADE,
    UNIQUE(warehouse_id, client_id)
);

