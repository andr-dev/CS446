diesel::table! {
    user_rating (user_id, rating) {
        user_id -> Integer,
        rating -> Float,
    }
}
