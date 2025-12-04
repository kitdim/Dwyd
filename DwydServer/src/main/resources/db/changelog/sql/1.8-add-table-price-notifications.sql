-- liquibase formatted sql

-- changeset kitdim:1.8-add-table-price-notifications
CREATE TABLE price_notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    product_id BIGINT,
    all_prices VARCHAR(255)
);

-- rollback DROP TABLE price_notifications;