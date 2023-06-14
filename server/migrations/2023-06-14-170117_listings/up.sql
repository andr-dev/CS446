CREATE TABLE listings (
  listing_id BIGINT NOT NULL PRIMARY KEY,
  address_line VARCHAR NOT NULL,
  address_city VARCHAR NOT NULL,
  address_postalcode VARCHAR UNIQUE NOT NULL,
  address_country VARCHAR NOT NULL,
  price BIGINT NOT NULL,
  rooms BIGINT NOT NULL,
  lease_start DATETIME NOT NULL,
  lease_end DATETIME NOT NULL,
  listing_description VARCHAR NOT NULL,
  residence_type VARCHAR NOT NULL,
  owner_user_id BIGINT NOT NULL,
  FOREIGN KEY(owner_user_id) REFERENCES users(user_id)
)
