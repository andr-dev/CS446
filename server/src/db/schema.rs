// @generated automatically by Diesel CLI.

diesel::table! {
    listings (listing_id) {
        listing_id -> BigInt,
        address_line -> Text,
        address_city -> Text,
        address_postalcode -> Text,
        address_country -> Text,
        price -> BigInt,
        rooms -> BigInt,
        lease_start -> Timestamp,
        lease_end -> Timestamp,
        listing_description -> Text,
        residence_type -> Text,
        owner_user_id -> BigInt,
    }
}

diesel::table! {
    users (user_id) {
        user_id -> BigInt,
        first_name -> Text,
        last_name -> Text,
        email -> Text,
        password_hash -> Text,
        gender -> Text,
    }
}

diesel::joinable!(listings -> users (owner_user_id));

diesel::allow_tables_to_appear_in_same_query!(
    listings,
    users,
);
