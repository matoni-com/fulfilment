-- Enums for flow properties
CREATE TYPE flow_kind_t AS ENUM (
  'PRODUCT_IMPORT',
  'STOCK_UPDATE',
  'SALES_ORDER',
  'SHIPMENT_NOTIFICATION',
  'SALES_ORDER_CANCEL',
  'SALES_ORDER_RETURN'
);

CREATE TYPE flow_direction_t AS ENUM ('IMPORT', 'EXPORT');

CREATE TYPE execution_mode_t AS ENUM ('ACTIVE', 'PASSIVE');

-- Integration configurations ---------------------------------------------

CREATE TABLE merchant_integration_configurations (
    id INT8 GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    merchant_id TEXT NOT NULL REFERENCES merchants(id) ON DELETE CASCADE,
    connection_settings JSONB NOT NULL
);

CREATE INDEX idx_merchant_integration_configurations_merchant_id
    ON merchant_integration_configurations (merchant_id);

CREATE TABLE warehouse_integration_configurations (
    id INT8 GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    warehouse_id TEXT NOT NULL REFERENCES warehouses(id) ON DELETE CASCADE,
    connection_settings JSONB NOT NULL
);

CREATE INDEX idx_warehouse_integration_configurations_warehouse_id
    ON warehouse_integration_configurations (warehouse_id);

CREATE TABLE merchant_warehouse_integration_connections (
    id INT8 GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    merchant_integration_configuration_id INT8 NOT NULL
        REFERENCES merchant_integration_configurations(id) ON DELETE CASCADE,
    warehouse_integration_configuration_id INT8 NOT NULL
        REFERENCES warehouse_integration_configurations(id) ON DELETE CASCADE,
    UNIQUE (merchant_integration_configuration_id, warehouse_integration_configuration_id)
);

CREATE INDEX idx_mwic_merchant_integration_configuration_id
    ON merchant_warehouse_integration_connections (merchant_integration_configuration_id);

CREATE INDEX idx_mwic_warehouse_integration_configuration_id
    ON merchant_warehouse_integration_connections (warehouse_integration_configuration_id);

-- Flows ------------------------------------------------------------------

CREATE TABLE merchant_flows (
    id INT8 GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    merchant_integration_configuration_id INT8 NOT NULL
        REFERENCES merchant_integration_configurations(id) ON DELETE CASCADE,
    flow_kind flow_kind_t NOT NULL,
    direction flow_direction_t NOT NULL,
    execution_mode execution_mode_t NOT NULL,
    schedule TEXT,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    last_run_at TIMESTAMPTZ,
    next_planned_run_at TIMESTAMPTZ,
    notes TEXT
);

CREATE INDEX idx_merchant_flows_merchant_integration_configuration_id
    ON merchant_flows (merchant_integration_configuration_id);

CREATE TABLE warehouse_flows (
    id INT8 GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    warehouse_integration_configuration_id INT8 NOT NULL
        REFERENCES warehouse_integration_configurations(id) ON DELETE CASCADE,
    flow_kind flow_kind_t NOT NULL,
    direction flow_direction_t NOT NULL,
    execution_mode execution_mode_t NOT NULL,
    schedule TEXT,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    last_run_at TIMESTAMPTZ,
    next_planned_run_at TIMESTAMPTZ,
    notes TEXT
);

CREATE INDEX idx_warehouse_flows_warehouse_integration_configuration_id
    ON warehouse_flows (warehouse_integration_configuration_id);
