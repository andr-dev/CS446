CREATE TABLE listings_favourites (
    user_id INT NOT NULL,
    listing_id INT NOT NULL,

    FOREIGN KEY(user_id) REFERENCES users(user_id),
    FOREIGN KEY(listing_id) REFERENCES listings(listing_id),

    PRIMARY KEY(user_id, listing_id)
)
