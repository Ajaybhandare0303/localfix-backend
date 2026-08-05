CREATE TABLE users (
                       id CHAR(36) PRIMARY KEY,

                       first_name VARCHAR(100) NOT NULL,
                       last_name VARCHAR(100) NOT NULL,

                       email VARCHAR(255) NOT NULL UNIQUE,
                       mobile VARCHAR(15) NOT NULL UNIQUE,

                       password VARCHAR(255) NOT NULL,

                       account_status VARCHAR(20) NOT NULL,

                       email_verified BOOLEAN DEFAULT FALSE,
                       mobile_verified BOOLEAN DEFAULT FALSE,

                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                           ON UPDATE CURRENT_TIMESTAMP
);