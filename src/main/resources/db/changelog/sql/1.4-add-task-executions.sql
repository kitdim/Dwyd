-- liquibase formatted sql

-- changeset kitdim:1.4-add-task-executions
CREATE TABLE task_executions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_name VARCHAR(255),
    start_time TIMESTAMP,
    finish_time TIMESTAMP,
    task_status VARCHAR(50) CHECK (task_status IN ('RUNNING', 'FINISH', 'ERROR')),
    start_params VARCHAR(255),
    description VARCHAR(255)
);

-- rollback DROP TABLE task-executions;