use diesel::{Insertable, Queryable};

use crate::db::schema::users;

#[derive(Queryable, Debug)]
pub struct User {
    pub user_id: i64,
    pub first_name: String,
    pub last_name: String,
    pub email: String,
    pub password_hash: String,
    pub gender: String,
}

#[derive(Insertable)]
#[diesel(table_name = users)]
pub struct NewUser<'a> {
    pub user_id: i64,
    pub first_name: &'a str,
    pub last_name: &'a str,
    pub email: &'a str,
    pub password_hash: &'a str,
    pub gender: &'a str,
}
