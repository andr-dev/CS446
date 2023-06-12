use std::collections::HashSet;

use chrono::{DateTime, Utc};
use okapi::openapi3::OpenApi;
use rocket::{get, serde::json::Json, Route, State};
use rocket_okapi::{openapi, openapi_get_routes_spec};

use super::model::listing::{
    GetListingDetailsRequest,
    GetListingDetailsResponse,
    GetListingsRequest,
    GetListingsResponse,
    ListingDetails,
    ResidenceType,
};
use crate::{error::ServiceResult, state::AppState};

#[openapi]
#[get("/list?<listings_request..>")]
fn list(state: &State<AppState>, listings_request: GetListingsRequest) -> ServiceResult<GetListingsResponse> {
    Ok(Json(GetListingsResponse {
        listings: vec![],
        liked: HashSet::default(),
    }))
}

#[openapi]
#[get("/details?<details_request..>")]
fn details(
    state: &State<AppState>,
    details_request: GetListingDetailsRequest,
) -> ServiceResult<GetListingDetailsResponse> {
    Ok(Json(GetListingDetailsResponse {
        details: ListingDetails {
            address: "Address".to_string(),
            price: 123,
            rooms: 2,
            lease_start: DateTime::<Utc>::MIN_UTC,
            lease_end: DateTime::<Utc>::MAX_UTC,
            description: "Description".to_string(),
            img_ids: vec![],
            residence_type: ResidenceType::Apartment,
            owner_id: "owner_id".to_string(),
        },
        favourited: false,
    }))
}

pub fn routes() -> (Vec<Route>, OpenApi) {
    openapi_get_routes_spec![list, details]
}
