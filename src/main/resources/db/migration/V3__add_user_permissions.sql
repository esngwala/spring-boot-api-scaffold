-- Direct permission grants for exceptional users in addition to role grants.

CREATE TABLE user_permissions (
    user_id BINARY(16) NOT NULL,
    permission_id BINARY(16) NOT NULL,
    CONSTRAINT pk_user_permissions PRIMARY KEY (user_id, permission_id),
    CONSTRAINT fk_user_permissions_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_user_permissions_permission FOREIGN KEY (permission_id) REFERENCES permissions(id)
);
