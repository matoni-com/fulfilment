CREATE TABLE warehouse_flows (
    id TEXT PRIMARY KEY,
    warehouse_integration_configuration_id TEXT NOT NULL REFERENCES warehouse_integration_configurations(id) ON DELETE CASCADE,
    flow_kind TEXT NOT NULL,
    direction TEXT NOT NULL,
    execution_mode TEXT NOT NULL,
    schedule TEXT,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    last_run_at TIMESTAMPTZ,
    next_planned_run_at TIMESTAMPTZ,
    notes TEXT
);


