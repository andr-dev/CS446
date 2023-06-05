// @generated automatically by Diesel CLI.

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
