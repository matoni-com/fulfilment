INSERT INTO addresses (street, house_number, zip, city, country)
VALUES ('street-mt', 'house-number-mt', 'zip-mt', 'city-mt', 'country-mt');

INSERT INTO addresses (street, house_number, zip, city, country)
VALUES ('street-wh', 'house-number-wh', 'zip-wh', 'city-wh', 'country-wh');

INSERT INTO merchants (id, company_name, address_id)
VALUES ('MT', 'Test Merchant', 1);

INSERT INTO warehouses (id, warehouse_name, address_id)
VALUES ('WH', 'Test Warehouse', 2);