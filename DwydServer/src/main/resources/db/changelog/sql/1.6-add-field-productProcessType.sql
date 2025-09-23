-- liquibase formatted sql

-- changeset kitdim:1.6-add-field-productProcessType
ALTER TABLE products
ADD COLUMN product_process_type VARCHAR(50) CHECK (product_process_type IN ('SALE', 'NOT_SALE', 'NOT_FOUND'));

-- rollback ALTER TABLE products DROP COLUMN product_process_type;