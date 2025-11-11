-- Create PostgreSQL ENUM types for flow columns
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'flow_kind_t') THEN
    CREATE TYPE flow_kind_t AS ENUM (
      'PRODUCT_IMPORT',
      'STOCK_UPDATE',
      'SALES_ORDER',
      'SHIPMENT_NOTIFICATION',
      'SALES_ORDER_CANCEL',
      'SALES_ORDER_RETURN'
    );
  END IF;

  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'flow_direction_t') THEN
    CREATE TYPE flow_direction_t AS ENUM ('IMPORT', 'EXPORT');
  END IF;

  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'execution_mode_t') THEN
    CREATE TYPE execution_mode_t AS ENUM ('ACTIVE', 'PASSIVE');
  END IF;
END$$;

-- Alter merchant_flows to use ENUM types
ALTER TABLE merchant_flows
  ALTER COLUMN flow_kind TYPE flow_kind_t USING flow_kind::flow_kind_t,
  ALTER COLUMN direction TYPE flow_direction_t USING direction::flow_direction_t,
  ALTER COLUMN execution_mode TYPE execution_mode_t USING execution_mode::execution_mode_t;

-- Alter warehouse_flows to use ENUM types
ALTER TABLE warehouse_flows
  ALTER COLUMN flow_kind TYPE flow_kind_t USING flow_kind::flow_kind_t,
  ALTER COLUMN direction TYPE flow_direction_t USING direction::flow_direction_t,
  ALTER COLUMN execution_mode TYPE execution_mode_t USING execution_mode::execution_mode_t;


