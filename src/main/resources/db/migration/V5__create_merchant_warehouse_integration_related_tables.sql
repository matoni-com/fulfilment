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

    flow_kind TEXT NOT NULL,
    direction TEXT NOT NULL,
    execution_mode TEXT NOT NULL,

    schedule TEXT,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    last_run_at TIMESTAMPTZ,
    next_planned_run_at TIMESTAMPTZ,
    notes TEXT,

    CONSTRAINT chk_merchant_flow_kind CHECK (flow_kind IN (
        'PRODUCT_IMPORT',
        'STOCK_UPDATE',
        'SALES_ORDER',
        'SHIPMENT_NOTIFICATION',
        'SALES_ORDER_CANCEL',
        'SALES_ORDER_RETURN'
    )),
    CONSTRAINT chk_merchant_flow_direction CHECK (direction IN ('IMPORT', 'EXPORT')),
    CONSTRAINT chk_merchant_execution_mode CHECK (execution_mode IN ('ACTIVE', 'PASSIVE'))
);

CREATE INDEX idx_merchant_flows_merchant_integration_configuration_id
    ON merchant_flows (merchant_integration_configuration_id);


CREATE TABLE warehouse_flows (
    id INT8 GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    warehouse_integration_configuration_id INT8 NOT NULL
        REFERENCES warehouse_integration_configurations(id) ON DELETE CASCADE,

    flow_kind TEXT NOT NULL,
    direction TEXT NOT NULL,
    execution_mode TEXT NOT NULL,

    schedule TEXT,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    last_run_at TIMESTAMPTZ,
    next_planned_run_at TIMESTAMPTZ,
    notes TEXT,

    CONSTRAINT chk_warehouse_flow_kind CHECK (flow_kind IN (
        'PRODUCT_IMPORT',
        'STOCK_UPDATE',
        'SALES_ORDER',
        'SHIPMENT_NOTIFICATION',
        'SALES_ORDER_CANCEL',
        'SALES_ORDER_RETURN'
    )),
    CONSTRAINT chk_warehouse_flow_direction CHECK (direction IN ('IMPORT', 'EXPORT')),
    CONSTRAINT chk_warehouse_execution_mode CHECK (execution_mode IN ('ACTIVE', 'PASSIVE'))
);

CREATE INDEX idx_warehouse_flows_warehouse_integration_configuration_id
    ON warehouse_flows (warehouse_integration_configuration_id);
