CREATE TABLE user_avatars (
    user_id INT NOT NULL PRIMARY KEY,
    image_id CHAR(36) UNIQUE NOT NULL,
    extension CHAR(4) NOT NULL,

    FOREIGN KEY(user_id) REFERENCES users(user_id)
)
