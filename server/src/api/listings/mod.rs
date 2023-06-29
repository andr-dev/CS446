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
    db::{
        model::listings::{Listing, ListingImage},
        paginate::Paginate,
        schema::{listings, listings_images},
    },
    error::{ServiceError, ServiceResult},
    state::AppState,
};

#[openapi(tag = "Listings")]
#[post("/create", format = "json", data = "<listing_request>")]
fn listings_create(
    state: &State<AppState>,
    user: AuthenticatedUser,
    listing_request: Json<CreateListingRequest>,
) -> ServiceResult<CreateListingResponse> {
    let mut dbcon = state.pool.get()?;

    for img_id in &listing_request.img_ids {
        let listing_image: ListingImage = listings_images::dsl::listings_images.find(img_id).first(&mut dbcon)?;

        if listing_image.user_id != user.user_id || listing_image.listing_id.is_some() {
            return Err(ServiceError::InternalError);
        }
    }

    let listing_id: i32 = rand::thread_rng().gen();

    for img_id in &listing_request.img_ids {
        diesel::update(listings_images::dsl::listings_images)
            .filter(listings_images::image_id.eq(img_id))
            .set(listings_images::listing_id.eq(listing_id))
            .execute(&mut dbcon)?;
    }

    diesel::insert_into(listings::table)
        .values(&listing_request.try_into_new_listing(listing_id, user.user_id)?)
        .execute(&mut dbcon)?;

    Ok(Json(CreateListingResponse { listing_id }))
}

#[openapi(tag = "Listings")]
#[get("/list?<listings_request..>")]
fn listings_list(state: &State<AppState>, listings_request: GetListingsRequest) -> ServiceResult<GetListingsResponse> {
    let mut dbcon = state.pool.get()?;

    let fetched_listings: (Vec<Listing>, i64) = listings::dsl::listings
        .filter(listings::price.ge(listings_request.price_min.map(|x| x as i32).unwrap_or(i32::MIN)))
        .filter(listings::price.le(listings_request.price_max.map(|x| x as i32).unwrap_or(i32::MAX)))
        .filter(listings::rooms.ge(listings_request.rooms_min.map(|x| x as i32).unwrap_or(i32::MIN)))
        .filter(listings::rooms.le(listings_request.rooms_max.map(|x| x as i32).unwrap_or(i32::MAX)))
        .paginate(listings_request.page_number.into(), listings_request.page_size.into())
        .map_err(|_| ServiceError::InternalError)?
        .load(&mut dbcon)?;

    let pages = fetched_listings.1.try_into().map_err(|_| ServiceError::InternalError)?;

    Ok(Json(GetListingsResponse {
        listings: fetched_listings
            .0
            .into_iter()
            .flat_map(|l| {
                listings_images::dsl::listings_images
                    .filter(listings_images::listing_id.eq(l.listing_id))
                    .load::<ListingImage>(&mut dbcon)
                    .map(|listing_images| {
                        ListingSummary::try_from_db(l, listing_images.into_iter().map(|li| li.image_id).collect())
                    })
            })
            .collect::<Result<Vec<ListingSummary>, ServiceError>>()?,
        pages,
        liked: HashSet::default(),
    }))
}

#[openapi(tag = "Listings")]
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
        let listing_images: Vec<ListingImage> = listings_images::dsl::listings_images
            .filter(listings_images::listing_id.eq(l.listing_id))
            .load(&mut dbcon)?;

        Ok(Json(GetListingDetailsResponse {
            details: ListingDetails::try_from_db(l, listing_images.into_iter().map(|li| li.image_id).collect())?,
            favourited: false,
        }))
    } else {
        Err(ServiceError::NotFound)
    }
}

pub fn routes() -> (Vec<Route>, OpenApi) {
    openapi_get_routes_spec![
        listings_create,
        listings_details,
        listings_images_get,
        listings_images_create,
        listings_list,
    ]
}
