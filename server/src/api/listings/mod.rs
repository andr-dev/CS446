use std::collections::HashSet;

use diesel::{ExpressionMethods, QueryDsl, RunQueryDsl};
use okapi::openapi3::OpenApi;
use rand::Rng;
use rocket::{get, post, serde::json::Json, Route, State};
use rocket_okapi::{openapi, openapi_get_routes_spec};

mod images;
use images::{
    listings_images_create,
    listings_images_get,
    okapi_add_operation_for_listings_images_create_,
    okapi_add_operation_for_listings_images_get_,
};

use super::{
    model::listing::{
        CreateListingRequest,
        CreateListingResponse,
        GetListingDetailsRequest,
        GetListingDetailsResponse,
        GetListingsRequest,
        GetListingsResponse,
        ListingDetails,
        ListingSummary,
    },
    token::AuthenticatedUser,
};
use crate::{
    db::{model::listings::Listing, schema::listings},
    error::{ServiceError, ServiceResult},
    state::AppState,
};

#[openapi]
#[post("/create", format = "json", data = "<listing_request>")]
fn listings_create(
    state: &State<AppState>,
    user: AuthenticatedUser,
    listing_request: Json<CreateListingRequest>,
) -> ServiceResult<CreateListingResponse> {
    let mut dbcon = state.pool.get()?;

    let listing_id: i32 = rand::thread_rng().gen();

    diesel::insert_into(listings::table)
        .values(&listing_request.try_into_new_listing(listing_id, user.user_id)?)
        .execute(&mut dbcon)?;

    Ok(Json(CreateListingResponse { listing_id }))
}

#[openapi]
#[get("/list?<listings_request..>")]
fn listings_list(state: &State<AppState>, listings_request: GetListingsRequest) -> ServiceResult<GetListingsResponse> {
    let mut dbcon = state.pool.get()?;

    let fetched_listings: Vec<Listing> = listings::dsl::listings
        .filter(listings::price.ge(listings_request.price_min.map(|x| x as i32).unwrap_or(i32::MIN)))
        .filter(listings::price.le(listings_request.price_max.map(|x| x as i32).unwrap_or(i32::MAX)))
        .filter(listings::rooms.ge(listings_request.rooms_min.map(|x| x as i32).unwrap_or(i32::MIN)))
        .filter(listings::rooms.le(listings_request.rooms_max.map(|x| x as i32).unwrap_or(i32::MAX)))
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
fn listings_details(
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
    openapi_get_routes_spec![
        listings_create,
        listings_list,
        listings_details,
        listings_images_get,
        listings_images_create
    ]
}
