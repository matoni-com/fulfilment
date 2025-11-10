CREATE TABLE merchant_warehouse_integration_connections (
    id TEXT PRIMARY KEY,
    merchant_integration_configuration_id TEXT NOT NULL REFERENCES merchant_integration_configurations(id) ON DELETE CASCADE,
    warehouse_integration_configuration_id TEXT NOT NULL REFERENCES warehouse_integration_configurations(id) ON DELETE CASCADE,
    UNIQUE(merchant_integration_configuration_id, warehouse_integration_configuration_id)
);

