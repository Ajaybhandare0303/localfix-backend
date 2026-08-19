CREATE TABLE service_categories (

                                    id BINARY(16) PRIMARY KEY,

                                    name VARCHAR(100) NOT NULL UNIQUE,

                                    description VARCHAR(500),

                                    icon VARCHAR(255),

                                    active BOOLEAN NOT NULL DEFAULT TRUE,

                                    created_at DATETIME NOT NULL,

                                    updated_at DATETIME NOT NULL
);