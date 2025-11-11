CREATE TABLE merchant_integration_configurations (
    id TEXT PRIMARY KEY,
    merchant_id TEXT NOT NULL REFERENCES merchants(id) ON DELETE CASCADE,
    connection_settings JSONB NOT NULL
);

