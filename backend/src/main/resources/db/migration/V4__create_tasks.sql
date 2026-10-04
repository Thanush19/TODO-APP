CREATE TABLE tasks (
                       id UUID PRIMARY KEY,
                       user_id UUID NOT NULL,
                       title VARCHAR(200) NOT NULL,
                       description TEXT,
                       completed BOOLEAN NOT NULL DEFAULT FALSE,
                       priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
                       due_at TIMESTAMPTZ,
                       created_at TIMESTAMPTZ NOT NULL,
                       updated_at TIMESTAMPTZ NOT NULL,

                       CONSTRAINT fk_tasks_user
                           FOREIGN KEY (user_id)
                               REFERENCES users(id)
                               ON DELETE CASCADE,

                       CONSTRAINT chk_tasks_priority
                           CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH'))
);

CREATE INDEX idx_tasks_user_id
    ON tasks(user_id);

CREATE INDEX idx_tasks_user_id_created_at
    ON tasks(user_id, created_at);