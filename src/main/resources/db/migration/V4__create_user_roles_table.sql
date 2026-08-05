CREATE TABLE user_roles (

                            user_id CHAR(36) NOT NULL,

                            role_id BIGINT NOT NULL,

                            PRIMARY KEY(user_id, role_id),

                            CONSTRAINT fk_user_role_user
                                FOREIGN KEY(user_id)
                                    REFERENCES users(id),

                            CONSTRAINT fk_user_role_role
                                FOREIGN KEY(role_id)
                                    REFERENCES roles(id)
);