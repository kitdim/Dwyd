-- liquibase formatted sql

-- changeset kitdim:1.7-add-field-userId-to-products.sql
ALTER TABLE products
ADD COLUMN user_id BIGINT;

-- rollback ALTER TABLE products DROP COLUMN userId;