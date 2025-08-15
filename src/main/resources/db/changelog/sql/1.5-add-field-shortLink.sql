-- liquibase formatted sql

-- changeset kitdim:1.5-add-field-shortLink
ALTER TABLE products
ADD COLUMN short_link VARCHAR(255);

-- rollback ALTER TABLE products DROP COLUMN short_link;