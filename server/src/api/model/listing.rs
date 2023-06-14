use std::collections::HashSet;

use chrono::{DateTime, Utc};
use rocket::FromForm;
use rocket_okapi::JsonSchema;
use serde::Serialize;

use crate::{db::model::listings::Listing, error::ServiceError};

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
    pub listing_id: i64,
    pub address: String,
    pub price: u16,
    pub rooms: u16,
    pub lease_start: DateTime<Utc>,
    pub lease_end: DateTime<Utc>,
    pub residence_type: ResidenceType,
}

impl TryFrom<Listing> for ListingSummary {
    type Error = ServiceError;

    fn try_from(l: Listing) -> Result<Self, Self::Error> {
        Ok(ListingSummary {
            listing_id: l.listing_id,
            address: format!(
                "{}, {} {}, {}",
                l.address_line, l.address_city, l.address_postalcode, l.address_country
            ),
            price: l.price.try_into().map_err(|_| ServiceError::InternalError)?,
            rooms: l.rooms.try_into().map_err(|_| ServiceError::InternalError)?,
            lease_start: l
                .lease_start
                .and_local_timezone(Utc)
                .single()
                .ok_or(ServiceError::InternalError)?,
            lease_end: l
                .lease_end
                .and_local_timezone(Utc)
                .single()
                .ok_or(ServiceError::InternalError)?,
            residence_type: ResidenceType::Other,
        })
    }
}

#[derive(JsonSchema, Serialize, Clone, PartialEq)]
pub enum ResidenceType {
    Apartment,
    House,
    TownHouse,
    Other,
}

#[derive(JsonSchema, FromForm, Clone, PartialEq)]
pub struct GetListingDetailsRequest {
    pub listing_id: i64,
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
    pub owner_user_id: i64,
}

impl TryFrom<Listing> for ListingDetails {
    type Error = ServiceError;

    fn try_from(l: Listing) -> Result<Self, Self::Error> {
        let ls = ListingSummary::try_from(l.clone())?;

        Ok(ListingDetails {
            address: ls.address,
            price: ls.price,
            rooms: ls.rooms,
            lease_start: ls.lease_start,
            lease_end: ls.lease_end,
            description: l.listing_description,
            img_ids: vec![],
            residence_type: ResidenceType::Other,
            owner_user_id: l.owner_user_id,
        })
    }
}
