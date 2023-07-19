use std::collections::HashSet;

use chrono::{DateTime, NaiveDateTime, Utc};
use rocket::FromForm;
use rocket_okapi::JsonSchema;
use serde::{Deserialize, Serialize};

use super::NaiveDateForm;
use crate::{
    api::utils::{format_address, format_address_anonymous},
    db::model::listings::{Listing, NewListing},
    error::ServiceError,
};

#[derive(JsonSchema, FromForm, Clone, PartialEq)]
pub struct GetListingsRequest {
    pub longitude: f32,
    pub latitude: f32,

    pub distance_meters_min: Option<f32>,
    pub distance_meters_max: Option<f32>,

    pub price_min: Option<u16>,
    pub price_max: Option<u16>,

    pub rooms_available_min: Option<u16>,
    pub rooms_available_max: Option<u16>,
    pub rooms_total_min: Option<u16>,
    pub rooms_total_max: Option<u16>,

    pub bathrooms_available_min: Option<u16>,
    pub bathrooms_available_max: Option<u16>,
    pub bathrooms_total_min: Option<u16>,
    pub bathrooms_total_max: Option<u16>,
    pub bathrooms_ensuite_min: Option<u16>,
    pub bathrooms_ensuite_max: Option<u16>,

    pub lease_start: Option<NaiveDateForm>,
    pub lease_end: Option<NaiveDateForm>,

    pub gender: Option<String>,

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
    pub longitude: f32,
    pub latitude: f32,
    pub distance_meters: f32,
    pub price: u16,
    pub rooms_available: u16,
    pub rooms_total: u16,
    pub bathrooms_available: u16,
    pub bathrooms_ensuite: u16,
    pub bathrooms_total: u16,
    pub lease_start: DateTime<Utc>,
    pub lease_end: DateTime<Utc>,
    pub img_ids: Vec<String>,
    pub residence_type: ResidenceType,
}
impl ListingSummary {
    pub fn try_from_db(
        l: Listing,
        user_id: i32,
        distance_meters: f32,
        img_ids: Vec<String>,
    ) -> Result<Self, ServiceError> {
        Ok(ListingSummary {
            listing_id: l.listing_id,
            address: if l.owner_user_id == user_id {
                format_address(l.address_line, l.address_city, l.address_postalcode, l.address_country)
            } else {
                format_address_anonymous(l.address_line, l.address_city, l.address_postalcode, l.address_country)
            },
            longitude: l.longitude,
            latitude: l.latitude,
            distance_meters,
            price: TryInto::<u16>::try_into(l.price).map_err(|e| ServiceError::InvalidFieldError {
                field: "price",
                reason: e.to_string(),
            })?,
            rooms_available: TryInto::<u16>::try_into(l.rooms_available).map_err(|e| {
                ServiceError::InvalidFieldError {
                    field: "rooms_available",
                    reason: e.to_string(),
                }
            })?,
            rooms_total: TryInto::<u16>::try_into(l.rooms_total).map_err(|e| ServiceError::InvalidFieldError {
                field: "rooms_total",
                reason: e.to_string(),
            })?,
            bathrooms_available: TryInto::<u16>::try_into(l.bathrooms_available).map_err(|e| {
                ServiceError::InvalidFieldError {
                    field: "bathrooms_available",
                    reason: e.to_string(),
                }
            })?,
            bathrooms_ensuite: TryInto::<u16>::try_into(l.bathrooms_ensuite).map_err(|e| {
                ServiceError::InvalidFieldError {
                    field: "bathrooms_ensuite",
                    reason: e.to_string(),
                }
            })?,
            bathrooms_total: TryInto::<u16>::try_into(l.bathrooms_total).map_err(|e| {
                ServiceError::InvalidFieldError {
                    field: "bathrooms_total",
                    reason: e.to_string(),
                }
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

    pub longitude: f32,
    pub latitude: f32,
}

#[derive(JsonSchema, Serialize, Clone, PartialEq)]
pub struct GetListingDetailsResponse {
    pub details: ListingDetails,
    pub favourited: bool,
}

#[derive(JsonSchema, Serialize, Clone, PartialEq)]
pub struct ListingDetails {
    pub address: String,
    pub longitude: f32,
    pub latitude: f32,
    pub distance_meters: f32,
    pub price: u16,
    pub rooms_available: u16,
    pub rooms_total: u16,
    pub bathrooms_available: u16,
    pub bathrooms_ensuite: u16,
    pub bathrooms_total: u16,
    pub lease_start: DateTime<Utc>,
    pub lease_end: DateTime<Utc>,
    pub description: String,
    pub img_ids: Vec<String>,
    pub residence_type: ResidenceType,
    pub gender: String,
    pub owner_user_id: i32,
}

impl ListingDetails {
    pub fn try_from_db(
        l: Listing,
        user_id: i32,
        distance_meters: f32,
        img_ids: Vec<String>,
    ) -> Result<Self, ServiceError> {
        let description = l.listing_description.to_owned();

        let residence_type = ResidenceType::from_string(&l.residence_type).ok_or(ServiceError::InvalidFieldError {
            field: "residence_type",
            reason: format!("failed to parse"),
        })?;

        let longitude = l.longitude;
        let latitude = l.latitude;

        let gender = l.gender.clone();

        let owner_user_id = l.owner_user_id;

        let ls = ListingSummary::try_from_db(l, user_id, distance_meters, img_ids)?;

        Ok(ListingDetails {
            address: ls.address,
            longitude,
            latitude,
            distance_meters,
            price: ls.price,
            rooms_available: ls.rooms_available,
            rooms_total: ls.rooms_total,
            bathrooms_available: ls.bathrooms_available,
            bathrooms_ensuite: ls.bathrooms_ensuite,
            bathrooms_total: ls.bathrooms_total,
            lease_start: ls.lease_start,
            lease_end: ls.lease_end,
            description,
            img_ids: ls.img_ids,
            residence_type,
            owner_user_id,
            gender,
        })
    }
}

#[derive(JsonSchema, Deserialize, Clone, PartialEq)]
pub struct CreateListingRequest {
    pub address_line: String,
    pub address_city: String,
    pub address_postalcode: String,
    pub address_country: String,
    pub price: i32,
    pub rooms_available: i32,
    pub rooms_total: i32,
    pub bathrooms_available: i32,
    pub bathrooms_ensuite: i32,
    pub bathrooms_total: i32,
    pub lease_start: NaiveDateTime,
    pub lease_end: NaiveDateTime,
    pub description: String,
    pub img_ids: Vec<String>,
    pub residence_type: ResidenceType,
    pub gender: String,
}

impl CreateListingRequest {
    pub fn try_into_new_listing<'a>(
        &'a self,
        listing_id: i32,
        user_id: i32,
        latitude: f32,
        longitude: f32,
    ) -> Result<NewListing<'a>, ServiceError> {
        Ok(NewListing {
            listing_id,
            address_line: &self.address_line,
            address_city: &self.address_city,
            address_postalcode: &self.address_postalcode,
            address_country: &self.address_country,
            longitude,
            latitude,
            price: self.price.into(),
            rooms_available: self.rooms_available.into(),
            rooms_total: self.rooms_total.into(),
            bathrooms_available: self.bathrooms_available.into(),
            bathrooms_ensuite: self.bathrooms_ensuite.into(),
            bathrooms_total: self.bathrooms_total.into(),
            lease_start: self.lease_start,
            lease_end: self.lease_end,
            listing_description: &self.description,
            residence_type: self.residence_type.name(),
            gender: &self.gender,
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

#[derive(JsonSchema, Deserialize, Clone, PartialEq)]
pub struct FavouriteListingRequest {
    pub listing_id: i32,
}
