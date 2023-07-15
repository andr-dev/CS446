// @generated automatically by Diesel CLI.

diesel::table! {
    listings (listing_id) {
        listing_id -> Integer,
        address_line -> Text,
        address_city -> Text,
        address_postalcode -> Text,
        address_country -> Text,
        longitude -> Float,
        latitude -> Float,
        price -> Integer,
        rooms_available -> Integer,
        rooms_total -> Integer,
        bathrooms_available -> Integer,
        bathrooms_ensuite -> Integer,
        bathrooms_total -> Integer,
        lease_start -> Timestamp,
        lease_end -> Timestamp,
        listing_description -> Text,
        residence_type -> Text,
        gender -> Text,
        owner_user_id -> Integer,
    }
}

diesel::table! {
    listings_favourites (user_id, listing_id) {
        user_id -> Integer,
        listing_id -> Integer,
    }
}

diesel::table! {
    listings_images (image_id) {
        image_id -> Text,
        extension -> Text,
        user_id -> Integer,
        listing_id -> Nullable<Integer>,
    }
}

diesel::table! {
    user_ratings (user_id, rater_user_id) {
        user_id -> Integer,
        rater_user_id -> Integer,
        rating -> Integer,
    }
}

diesel::table! {
    users (user_id) {
        user_id -> Integer,
        first_name -> Text,
        last_name -> Text,
        email -> Text,
        password_hash -> Text,
        gender -> Text,
    }
}

diesel::joinable!(listings -> users (owner_user_id));
diesel::joinable!(listings_favourites -> listings (listing_id));
diesel::joinable!(listings_favourites -> users (user_id));
diesel::joinable!(listings_images -> listings (listing_id));
diesel::joinable!(listings_images -> users (user_id));

diesel::allow_tables_to_appear_in_same_query!(
    listings,
    listings_favourites,
    listings_images,
    user_ratings,
    users,
);
