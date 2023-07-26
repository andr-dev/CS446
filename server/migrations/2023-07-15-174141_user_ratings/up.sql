CREATE TABLE user_ratings (
    user_id INT NOT NULL,
    rater_user_id INT NOT NULL,
    rating INT NOT NULL CHECK(rating >= 1 AND rating <= 5),

    FOREIGN KEY(user_id) REFERENCES users(user_id),
    FOREIGN KEY(rater_user_id) REFERENCES users(user_id),

    PRIMARY KEY(user_id, rater_user_id)
);

CREATE VIEW user_rating
AS SELECT user_id AS user_id, AVG(rating) AS rating
FROM user_ratings
GROUP BY user_id
