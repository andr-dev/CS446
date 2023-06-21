CREATE TABLE listings_images (
  image_id CHAR(36) NOT NULL PRIMARY KEY,
  extension CHAR(4) NOT NULL,
  user_id INT NOT NULL,
  listing_id INT,

  FOREIGN KEY(user_id) REFERENCES users(user_id),
  FOREIGN KEY(listing_id) REFERENCES listings(listing_id)
)
