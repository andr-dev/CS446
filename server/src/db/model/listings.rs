use chrono::NaiveDateTime;
use diesel::{Insertable, Queryable};

use crate::db::schema::{listings, listings_images};

#[derive(Queryable, Debug, Clone)]
#[diesel(table_name = listings)]
pub struct Listing {
    pub listing_id: i32,
    pub address_line: String,
    pub address_city: String,
    pub address_postalcode: String,
    pub address_country: String,
    pub longitude: f32,
    pub latitude: f32,
    pub price: i32,
    pub rooms_available: i32,
    pub rooms_total: i32,
    pub bathrooms_available: i32,
    pub bathrooms_ensuite: i32,
    pub bathrooms_total: i32,
    pub lease_start: NaiveDateTime,
    pub lease_end: NaiveDateTime,
    pub listing_description: String,
    pub residence_type: String,
    pub gender: String,
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
    pub longitude: f32,
    pub latitude: f32,
    pub price: i32,
    pub rooms_available: i32,
    pub rooms_total: i32,
    pub bathrooms_available: i32,
    pub bathrooms_ensuite: i32,
    pub bathrooms_total: i32,
    pub lease_start: NaiveDateTime,
    pub lease_end: NaiveDateTime,
    pub listing_description: &'a str,
    pub residence_type: &'a str,
    pub gender: &'a str,
    pub owner_user_id: i32,
}

#[derive(Queryable, Debug, Clone)]
#[diesel(table_name = listings_images)]
pub struct ListingImage {
    pub image_id: String,
    pub extension: String,
    pub user_id: i32,
    pub listing_id: Option<i32>,
}

#[derive(Insertable)]
#[diesel(table_name = listings_images)]
pub struct NewListingImage<'a> {
    pub image_id: &'a str,
    pub extension: &'a str,
    pub user_id: i32,
    pub listing_id: Option<i32>,
}
