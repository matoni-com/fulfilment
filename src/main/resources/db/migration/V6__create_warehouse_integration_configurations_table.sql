CREATE TABLE warehouse_integration_configurations (
    id TEXT PRIMARY KEY,
    warehouse_id TEXT NOT NULL REFERENCES warehouses(id) ON DELETE CASCADE,
    connection_settings JSONB NOT NULL
);

