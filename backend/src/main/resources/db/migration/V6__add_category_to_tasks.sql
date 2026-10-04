ALTER TABLE tasks
    ADD COLUMN category_id UUID;

ALTER TABLE tasks
    ADD CONSTRAINT fk_tasks_category
        FOREIGN KEY (category_id)
            REFERENCES categories (id)
            ON DELETE SET NULL;

CREATE INDEX idx_tasks_category_id
    ON tasks (category_id);