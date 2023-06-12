use std::collections::HashSet;

use chrono::{DateTime, Utc};
use rocket::FromForm;
use rocket_okapi::JsonSchema;
use serde::Serialize;

#[derive(JsonSchema, FromForm, Clone, PartialEq)]
pub struct GetListingsRequest {
    pub price_min: Option<u16>,
    pub price_max: Option<u16>,
    pub rooms_min: Option<u16>,
    pub rooms_max: Option<u16>,
}

#[derive(JsonSchema, Serialize, Clone, PartialEq)]
pub struct GetListingsResponse {
    pub listings: Vec<ListingSummary>,
    pub liked: HashSet<String>,
}

#[derive(JsonSchema, Serialize, Clone, PartialEq)]
pub struct ListingSummary {
    pub listing_id: String,
    pub address: String,
    pub price: u16,
    pub rooms: u16,
    pub lease_start: DateTime<Utc>,
    pub lease_end: DateTime<Utc>,
    pub residence_type: ResidenceType,
}

#[derive(JsonSchema, Serialize, Clone, PartialEq)]
pub enum ResidenceType {
    Apartment,
    House,
    TownHouse,
}

#[derive(JsonSchema, FromForm, Clone, PartialEq)]
pub struct GetListingDetailsRequest {
    pub listing_id: String,
}

#[derive(JsonSchema, Serialize, Clone, PartialEq)]
pub struct GetListingDetailsResponse {
    pub details: ListingDetails,
    pub favourited: bool,
}

#[derive(JsonSchema, Serialize, Clone, PartialEq)]
pub struct ListingDetails {
    pub address: String,
    pub price: u16,
    pub rooms: u16,
    pub lease_start: DateTime<Utc>,
    pub lease_end: DateTime<Utc>,
    pub description: String,
    pub img_ids: Vec<String>,
    pub residence_type: ResidenceType,
    pub owner_id: String,
}
