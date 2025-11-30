-- liquibase formatted sql

-- changeset kitdim:1.10-add-filed-shortLink-to-notifications.sql
ALTER TABLE price_notifications
ADD COLUMN short_link VARCHAR(255);

-- rollback ALTER TABLE price_notifications DROP COLUMN short_link;