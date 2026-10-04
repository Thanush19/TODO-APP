CREATE TABLE tags (
                      id UUID PRIMARY KEY,
                      user_id UUID NOT NULL,
                      name VARCHAR(100) NOT NULL,
                      created_at TIMESTAMPTZ NOT NULL,
                      updated_at TIMESTAMPTZ NOT NULL,

                      CONSTRAINT fk_tags_user
                          FOREIGN KEY (user_id)
                              REFERENCES users(id)
                              ON DELETE CASCADE,

                      CONSTRAINT uq_tags_user_name
                          UNIQUE (user_id, name)
);

CREATE INDEX idx_tags_user_id
    ON tags(user_id);