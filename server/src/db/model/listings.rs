use chrono::NaiveDateTime;
use diesel::Queryable;

#[derive(Queryable, Debug, Clone)]
#[diesel(table_name = listings)]
pub struct Listing {
    pub listing_id: i64,
    pub address_line: String,
    pub address_city: String,
    pub address_postalcode: String,
    pub address_country: String,
    pub price: i64,
    pub rooms: i64,
    pub lease_start: NaiveDateTime,
    pub lease_end: NaiveDateTime,
    pub listing_description: String,
    pub residence_type: String,
    pub owner_user_id: i64,
}
