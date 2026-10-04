CREATE TABLE categories (
                            id UUID PRIMARY KEY,
                            user_id UUID,
                            name VARCHAR(100) NOT NULL,
                            type VARCHAR(20) NOT NULL,
                            created_at TIMESTAMPTZ NOT NULL,
                            updated_at TIMESTAMPTZ NOT NULL,

                            CONSTRAINT fk_categories_user
                                FOREIGN KEY (user_id)
                                    REFERENCES users(id)
                                    ON DELETE CASCADE,

                            CONSTRAINT chk_categories_type
                                CHECK (type IN ('SYSTEM', 'CUSTOM')),

                            CONSTRAINT chk_system_category_user
                                CHECK (
                                    (type = 'SYSTEM' AND user_id IS NULL)
                                        OR
                                    (type = 'CUSTOM' AND user_id IS NOT NULL)
                                    )
);

CREATE UNIQUE INDEX uq_categories_system_name
    ON categories(name)
    WHERE type = 'SYSTEM';

CREATE UNIQUE INDEX uq_categories_custom_user_name
    ON categories(user_id, name)
    WHERE type = 'CUSTOM';

CREATE INDEX idx_categories_user_id
    ON categories(user_id);