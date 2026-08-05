CREATE TABLE roles (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(50) NOT NULL UNIQUE,
                       description VARCHAR(255),
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                           ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO roles (name, description)
VALUES
    ('ADMIN', 'System Administrator'),
    ('CUSTOMER', 'Customer of LocalFix'),
    ('PROFESSIONAL', 'Service Professional');