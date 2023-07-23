CREATE TABLE user_verified (
    user_id INT NOT NULL PRIMARY KEY,
    verified BOOLEAN NOT NULL,
    image_id CHAR(36) NOT NULL,
    extension CHAR(4) NOT NULL,

    FOREIGN KEY(user_id) REFERENCES users(user_id)
)
