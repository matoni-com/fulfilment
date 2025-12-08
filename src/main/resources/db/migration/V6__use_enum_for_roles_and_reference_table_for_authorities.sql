-- User authorities

DROP TABLE authorities;

CREATE TABLE authorities (
    authority TEXT PRIMARY KEY
);

CREATE TABLE users_authorities (
    user_id INT8 NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    authority TEXT NOT NULL REFERENCES authorities(authority) ON DELETE CASCADE,
    PRIMARY KEY (user_id, authority)
);


-- Client roles

CREATE TYPE client_role_type AS ENUM ('MERCHANT', 'WAREHOUSE');

ALTER TABLE clients ALTER COLUMN role TYPE client_role_type USING role::client_role_type;