CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE users (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       email VARCHAR(120) NOT NULL UNIQUE,
                       name VARCHAR(120) NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       active BOOLEAN NOT NULL DEFAULT TRUE,
                       created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
                       updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE categories (
                            id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                            name VARCHAR(80) NOT NULL,
                            type VARCHAR(20) NOT NULL,
                            user_id UUID NOT NULL,
                            active BOOLEAN NOT NULL DEFAULT TRUE,
                            created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
                            updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

                            CONSTRAINT fk_categories_user
                                FOREIGN KEY (user_id)
                                    REFERENCES users(id)
                                    ON DELETE CASCADE,

                            CONSTRAINT uk_categories_user_name_type
                                UNIQUE (user_id, name, type),

                            CONSTRAINT ck_categories_type
                                CHECK (type IN ('INCOME', 'EXPENSE'))
);

CREATE TABLE transactions (
                              id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                              amount NUMERIC(15, 2) NOT NULL,
                              description VARCHAR(150) NOT NULL,
                              type VARCHAR(20) NOT NULL,
                              transaction_date DATE NOT NULL,
                              category_id UUID NOT NULL,
                              user_id UUID NOT NULL,
                              created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
                              updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

                              CONSTRAINT fk_transactions_category
                                  FOREIGN KEY (category_id)
                                      REFERENCES categories(id),

                              CONSTRAINT fk_transactions_user
                                  FOREIGN KEY (user_id)
                                      REFERENCES users(id)
                                      ON DELETE CASCADE,

                              CONSTRAINT ck_transactions_type
                                  CHECK (type IN ('INCOME', 'EXPENSE')),

                              CONSTRAINT ck_transactions_amount_positive
                                  CHECK (amount > 0)
);

CREATE INDEX idx_categories_user_id ON categories(user_id);
CREATE INDEX idx_transactions_user_id ON transactions(user_id);
CREATE INDEX idx_transactions_category_id ON transactions(category_id);
CREATE INDEX idx_transactions_date ON transactions(transaction_date);
CREATE INDEX idx_transactions_type ON transactions(type);
CREATE INDEX idx_transactions_user_date ON transactions(user_id, transaction_date);