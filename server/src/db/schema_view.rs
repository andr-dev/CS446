diesel::table! {
    user_rating (user_id) {
        user_id -> Integer,
        rating -> Float,
    }
}
