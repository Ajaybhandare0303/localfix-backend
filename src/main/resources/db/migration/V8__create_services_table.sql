CREATE TABLE services (

                          id BINARY(16) PRIMARY KEY,

                          category_id BINARY(16) NOT NULL,

                          name VARCHAR(100) NOT NULL,

                          description VARCHAR(500),

                          estimated_duration INT NOT NULL,

                          base_price DECIMAL(10,2) NOT NULL,

                          active BOOLEAN NOT NULL DEFAULT TRUE,

                          created_at DATETIME NOT NULL,

                          updated_at DATETIME NOT NULL,

                          CONSTRAINT uk_service_category_name
                              UNIQUE (category_id, name),

                          CONSTRAINT fk_service_category
                              FOREIGN KEY (category_id)
                                  REFERENCES service_categories(id)
);