-- The shop's orders table: the lab's local dataset. Given: do not edit.
-- ReportApp and the tests load it into an H2 in-memory database that disappears when the program ends.
CREATE TABLE orders (
    code     VARCHAR(10) PRIMARY KEY,
    customer VARCHAR(20) NOT NULL,
    region   VARCHAR(10) NOT NULL,
    status   VARCHAR(10) NOT NULL,
    cents    INT         NOT NULL
);

INSERT INTO orders (code, customer, region, status, cents) VALUES
    ('A-101', 'ana', 'north', 'SHIPPED',     800),
    ('A-102', 'bo',  'south', 'OPEN',        990),
    ('A-103', 'cyd', 'north', 'OPEN',      15000),
    ('A-104', 'ana', 'north', 'OPEN',        450),
    ('A-105', 'dee', 'east',  'CANCELLED',   300),
    ('A-106', 'bo',  'south', 'OPEN',      12500),
    ('A-107', 'eli', 'east',  'SHIPPED',    2200),
    ('A-108', 'ana', 'north', 'OPEN',       9000),
    ('A-109', 'cyd', 'north', 'CANCELLED', 20000),
    ('A-110', 'fay', 'west',  'OPEN',       3000),
    ('A-111', 'bo',  'south', 'SHIPPED',     600),
    ('A-112', 'eli', 'east',  'OPEN',      10000);
