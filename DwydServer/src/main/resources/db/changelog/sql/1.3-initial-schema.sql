-- liquibase formatted sql

-- changeset kitdim:1.0-initial-schema
CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    price DOUBLE NOT NULL,
    price_with_discount DOUBLE,
    last_price DOUBLE,
    article VARCHAR(255),
    market VARCHAR(50) CHECK (market IN ('OZON', 'WB', 'YANDEX')), -- Enum MarketCheckType
    check_time TIMESTAMP
);

-- rollback DROP TABLE products;