use chrono::NaiveDateTime;
use diesel::{Insertable, Queryable};

use crate::db::schema::listings;

#[derive(Queryable, Debug, Clone)]
#[diesel(table_name = listings)]
pub struct Listing {
    pub listing_id: i32,
    pub address_line: String,
    pub address_city: String,
    pub address_postalcode: String,
    pub address_country: String,
    pub price: i32,
    pub rooms: i32,
    pub lease_start: NaiveDateTime,
    pub lease_end: NaiveDateTime,
    pub listing_description: String,
    pub residence_type: String,
    pub owner_user_id: i32,
}

#[derive(Insertable)]
#[diesel(table_name = listings)]
pub struct NewListing<'a> {
    pub listing_id: i32,
    pub address_line: &'a str,
    pub address_city: &'a str,
    pub address_postalcode: &'a str,
    pub address_country: &'a str,
    pub price: i32,
    pub rooms: i32,
    pub lease_start: NaiveDateTime,
    pub lease_end: NaiveDateTime,
    pub listing_description: &'a str,
    pub residence_type: &'a str,
    pub owner_user_id: i32,
}
