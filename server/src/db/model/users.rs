use diesel::{Insertable, Queryable};

use crate::db::schema::{user_avatars, user_ratings, users};

#[derive(Queryable, Debug)]
#[diesel(table_name = users)]
pub struct User {
    pub user_id: i32,
    pub first_name: String,
    pub last_name: String,
    pub email: String,
    pub password_hash: String,
    pub gender: String,
}

#[derive(Insertable)]
#[diesel(table_name = users)]
pub struct NewUser<'a> {
    pub user_id: i32,
    pub first_name: &'a str,
    pub last_name: &'a str,
    pub email: &'a str,
    pub password_hash: &'a str,
    pub gender: &'a str,
}

#[derive(Queryable, Insertable, Debug)]
#[diesel(table_name = user_ratings)]
pub struct UserRating {
    pub user_id: i32,
    pub rater_user_id: i32,
    pub rating: i32,
}

#[derive(Queryable, Insertable, Debug, Clone)]
#[diesel(table_name = user_avatars)]
pub struct UserAvatar {
    pub user_id: i32,
    pub image_id: String,
    pub extension: String,
}
