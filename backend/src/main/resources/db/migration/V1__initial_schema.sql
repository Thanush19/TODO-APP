CREATE TABLE schema_info (
                             id BIGSERIAL PRIMARY KEY,
                             application_name VARCHAR(100) NOT NULL,
                             created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO schema_info (application_name)
VALUES ('todo-backend');