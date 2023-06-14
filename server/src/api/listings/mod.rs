use std::collections::HashSet;

use diesel::{ExpressionMethods, QueryDsl, RunQueryDsl};
use okapi::openapi3::OpenApi;
use rocket::{get, serde::json::Json, Route, State};
use rocket_okapi::{openapi, openapi_get_routes_spec};

use super::model::listing::{
    GetListingDetailsRequest,
    GetListingDetailsResponse,
    GetListingsRequest,
    GetListingsResponse,
    ListingDetails,
    ListingSummary,
};
use crate::{
    db::{model::listings::Listing, schema::listings},
    error::{ServiceError, ServiceResult},
    state::AppState,
};

#[openapi]
#[get("/list?<listings_request..>")]
fn list(state: &State<AppState>, listings_request: GetListingsRequest) -> ServiceResult<GetListingsResponse> {
    let mut dbcon = state.pool.get()?;

    let fetched_listings: Vec<Listing> = listings::dsl::listings
        .filter(listings::price.ge(listings_request.price_min.map(|x| x as i64).unwrap_or(i64::MIN)))
        .filter(listings::price.le(listings_request.price_max.map(|x| x as i64).unwrap_or(i64::MAX)))
        .filter(listings::rooms.ge(listings_request.rooms_min.map(|x| x as i64).unwrap_or(i64::MIN)))
        .filter(listings::rooms.le(listings_request.rooms_max.map(|x| x as i64).unwrap_or(i64::MAX)))
        .load(&mut dbcon)?;

    Ok(Json(GetListingsResponse {
        listings: fetched_listings
            .into_iter()
            .map(|l| l.try_into())
            .collect::<Result<Vec<ListingSummary>, ServiceError>>()?,
        liked: HashSet::default(),
    }))
}

#[openapi]
#[get("/details?<details_request..>")]
fn details(
    state: &State<AppState>,
    details_request: GetListingDetailsRequest,
) -> ServiceResult<GetListingDetailsResponse> {
    let mut dbcon = state.pool.get()?;

    if let Ok(l) = listings::dsl::listings
        .find(details_request.listing_id)
        .first::<Listing>(&mut dbcon)
    {
        Ok(Json(GetListingDetailsResponse {
            details: ListingDetails::try_from(l)?,
            favourited: false,
        }))
    } else {
        Err(ServiceError::NotFound)
    }
}

pub fn routes() -> (Vec<Route>, OpenApi) {
    openapi_get_routes_spec![list, details]
}
