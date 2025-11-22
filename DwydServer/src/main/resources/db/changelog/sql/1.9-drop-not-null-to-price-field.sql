-- liquibase formatted sql

-- changeset kitdim:1.9-drop-not-null-to-price-field
ALTER TABLE products ALTER COLUMN price DROP NOT NULL;

-- rollback ALTER TABLE products ALTER COLUMN price SET NOT NULL;