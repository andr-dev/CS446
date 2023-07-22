CREATE TABLE user_verified (
    user_id INT NOT NULL PRIMARY KEY,
    verified BOOLEAN NOT NULL,

    FOREIGN KEY(user_id) REFERENCES users(user_id)
)
