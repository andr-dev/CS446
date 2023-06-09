use std::collections::HashSet;

use chrono::{DateTime, NaiveDateTime, Utc};
use rocket::FromForm;
use rocket_okapi::JsonSchema;
use serde::{Deserialize, Serialize};

use crate::{
    db::model::listings::{Listing, NewListing},
    error::ServiceError,
};

#[derive(JsonSchema, FromForm, Clone, PartialEq)]
pub struct GetListingsRequest {
    pub price_min: Option<u16>,
    pub price_max: Option<u16>,
    pub rooms_min: Option<u16>,
    pub rooms_max: Option<u16>,

    pub page_number: u32,
    pub page_size: u32,
}

#[derive(JsonSchema, Serialize, Clone, PartialEq)]
pub struct GetListingsResponse {
    pub listings: Vec<ListingSummary>,
    pub pages: u32,
    pub liked: HashSet<String>,
}

#[derive(JsonSchema, Serialize, Clone, PartialEq)]
pub struct ListingSummary {
    pub listing_id: i32,
    pub address: String,
    pub price: u16,
    pub rooms: u16,
    pub lease_start: DateTime<Utc>,
    pub lease_end: DateTime<Utc>,
    pub img_ids: Vec<String>,
    pub residence_type: ResidenceType,
}

impl ListingSummary {
    pub fn try_from_db(l: Listing, img_ids: Vec<String>) -> Result<Self, ServiceError> {
        Ok(ListingSummary {
            listing_id: l.listing_id,
            address: format!(
                "{}, {} {}, {}",
                l.address_line, l.address_city, l.address_postalcode, l.address_country
            ),
            price: TryInto::<u16>::try_into(l.price).map_err(|e| ServiceError::InvalidFieldError {
                field: "price",
                reason: e.to_string(),
            })?,
            rooms: TryInto::<u16>::try_into(l.rooms).map_err(|e| ServiceError::InvalidFieldError {
                field: "rooms",
                reason: e.to_string(),
            })?,
            lease_start: l
                .lease_start
                .and_local_timezone(Utc)
                .single()
                .ok_or(ServiceError::InvalidFieldError {
                    field: "lease_start",
                    reason: format!("failed to convert to a unique datetime"),
                })?,
            lease_end: l
                .lease_end
                .and_local_timezone(Utc)
                .single()
                .ok_or(ServiceError::InvalidFieldError {
                    field: "lease_end",
                    reason: format!("failed to convert to a unique datetime"),
                })?,
            img_ids,
            residence_type: ResidenceType::Other,
        })
    }
}

enum_str! {
    pub enum ResidenceType {
        Apartment,
        House,
        TownHouse,
        Other,
    }
}

#[derive(JsonSchema, FromForm, Clone, PartialEq)]
pub struct GetListingDetailsRequest {
    pub listing_id: i32,
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
    pub owner_user_id: i32,
}

impl ListingDetails {
    pub fn try_from_db(l: Listing, img_ids: Vec<String>) -> Result<Self, ServiceError> {
        let description = l.listing_description.to_owned();

        let residence_type = ResidenceType::from_string(&l.residence_type).ok_or(ServiceError::InvalidFieldError {
            field: "residence_type",
            reason: format!("failed to parse"),
        })?;

        let owner_user_id = l.owner_user_id;

        let ls = ListingSummary::try_from_db(l, img_ids)?;

        Ok(ListingDetails {
            address: ls.address,
            price: ls.price,
            rooms: ls.rooms,
            lease_start: ls.lease_start,
            lease_end: ls.lease_end,
            description,
            img_ids: ls.img_ids,
            residence_type,
            owner_user_id,
        })
    }
}

#[derive(JsonSchema, Deserialize, Clone, PartialEq)]
pub struct CreateListingRequest {
    pub address_line: String,
    pub address_city: String,
    pub address_postalcode: String,
    pub address_country: String,
    pub price: u16,
    pub rooms: u16,
    pub lease_start: NaiveDateTime,
    pub lease_end: NaiveDateTime,
    pub description: String,
    pub img_ids: Vec<String>,
    pub residence_type: ResidenceType,
}

impl CreateListingRequest {
    pub fn try_into_new_listing<'a>(&'a self, listing_id: i32, user_id: i32) -> Result<NewListing<'a>, ServiceError> {
        Ok(NewListing {
            listing_id,
            address_line: &self.address_line,
            address_city: &self.address_city,
            address_postalcode: &self.address_postalcode,
            address_country: &self.address_country,
            price: self.price.into(),
            rooms: self.rooms.into(),
            lease_start: self.lease_start,
            lease_end: self.lease_end,
            listing_description: &self.description,
            residence_type: self.residence_type.name(),
            owner_user_id: user_id,
        })
    }
}

#[derive(JsonSchema, Serialize, Clone, PartialEq)]
pub struct CreateListingResponse {
    pub listing_id: i32,
}

#[derive(JsonSchema, Deserialize, Clone, PartialEq)]
pub struct ListingsImagesCreateRequest {
    pub image: String,
}

#[derive(JsonSchema, Serialize, Clone, PartialEq)]
pub struct ListingsImagesCreateResponse {
    pub image_id: String,
}
