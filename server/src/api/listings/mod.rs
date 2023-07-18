use std::collections::HashSet;

use chrono::{NaiveDate, NaiveTime};
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
    geocode::geocode_address,
    model::listing::{
        CreateListingRequest,
        CreateListingResponse,
        FavouriteListingRequest,
        GetListingDetailsRequest,
        GetListingDetailsResponse,
        GetListingsRequest,
        GetListingsResponse,
        ListingDetails,
        ListingSummary,
    },
    token::AuthenticatedUser,
    utils::{distance_meters, format_address},
};
use crate::{
    db::{
        model::listings::{Listing, ListingFavourite, ListingImage},
        paginate::{Paginate, PaginationError},
        schema::{listings, listings_favourites, listings_images},
    },
    error::{ServiceError, ServiceResult},
    state::AppState,
};

#[openapi(tag = "Listings")]
#[post("/create", format = "json", data = "<listing_request>")]
async fn listings_create(
    state: &State<AppState<'_>>,
    user: AuthenticatedUser,
    mut listing_request: Json<CreateListingRequest>,
) -> ServiceResult<CreateListingResponse> {
    let mut dbcon = state.pool.get()?;

    for img_id in &listing_request.img_ids {
        let listing_image: ListingImage = listings_images::dsl::listings_images.find(img_id).first(&mut dbcon)?;

        if listing_image.user_id != user.user_id || listing_image.listing_id.is_some() {
            return Err(ServiceError::Forbidden);
        }
    }

    let listing_id: i32 = rand::thread_rng().gen();

    let opencage = state.opencage.write().await;

    listing_request.address_line = listing_request.address_line.trim().to_owned();
    listing_request.address_city = listing_request.address_city.trim().to_owned();
    listing_request.address_postalcode = listing_request.address_postalcode.trim().to_owned();
    listing_request.address_country = listing_request.address_country.trim().to_owned();

    let location = geocode_address(
        opencage,
        format_address(
            listing_request.address_line.to_owned(),
            listing_request.address_city.to_owned(),
            listing_request.address_postalcode.to_owned(),
            listing_request.address_country.to_owned(),
        ),
    )
    .await?;

    diesel::insert_into(listings::table)
        .values(&listing_request.try_into_new_listing(
            listing_id,
            user.user_id,
            location.latitude,
            location.longitude,
        )?)
        .execute(&mut dbcon)?;

    for img_id in &listing_request.img_ids {
        diesel::update(listings_images::dsl::listings_images)
            .filter(listings_images::image_id.eq(img_id))
            .set(listings_images::listing_id.eq(listing_id))
            .execute(&mut dbcon)?;
    }

    Ok(Json(CreateListingResponse { listing_id }))
}

#[openapi(tag = "Listings")]
#[get("/details?<details_request..>")]
fn listings_details(
    state: &State<AppState>,
    user: AuthenticatedUser,
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

        let longitude = l.longitude;
        let latitude = l.latitude;

        let favourited: Vec<(i32, i32)> = listings_favourites::dsl::listings_favourites
            .find((user.user_id, l.listing_id))
            .load(&mut dbcon)?;

        Ok(Json(GetListingDetailsResponse {
            details: ListingDetails::try_from_db(
                l,
                distance_meters(details_request.longitude, details_request.latitude, longitude, latitude)
                    .map_err(|_| ServiceError::InternalError)?,
                listing_images.into_iter().map(|li| li.image_id).collect(),
            )?,
            favourited: !favourited.is_empty(),
        }))
    } else {
        Err(ServiceError::NotFound)
    }
}

#[openapi(tag = "Listings")]
#[post("/favourite", format = "json", data = "<listing_request>")]
fn listings_favourite(
    state: &State<AppState>,
    user: AuthenticatedUser,
    listing_request: Json<FavouriteListingRequest>,
) -> ServiceResult<String> {
    let mut dbcon = state.pool.get()?;

    diesel::insert_into(listings_favourites::table)
        .values(ListingFavourite {
            user_id: user.user_id,
            listing_id: listing_request.listing_id,
        })
        .execute(&mut dbcon)?;

    Ok(Json("OK".to_owned()))
}

#[openapi(tag = "Listings")]
#[post("/unfavourite", format = "json", data = "<listing_request>")]
fn listings_unfavourite(
    state: &State<AppState>,
    user: AuthenticatedUser,
    listing_request: Json<FavouriteListingRequest>,
) -> ServiceResult<String> {
    let mut dbcon = state.pool.get()?;

    diesel::delete(listings_favourites::table)
        .filter(listings_favourites::dsl::user_id.eq(user.user_id))
        .filter(listings_favourites::dsl::listing_id.eq(listing_request.listing_id))
        .execute(&mut dbcon)?;

    Ok(Json("OK".to_owned()))
}

#[openapi(tag = "Listings")]
#[get("/list?<listings_request..>")]
fn listings_list(state: &State<AppState>, listings_request: GetListingsRequest) -> ServiceResult<GetListingsResponse> {
    let mut dbcon = state.pool.get()?;

    let db_request = listings::dsl::listings
        .filter(listings::price.ge(listings_request.price_min.map(|x| x as i32).unwrap_or(i32::MIN)))
        .filter(listings::price.le(listings_request.price_max.map(|x| x as i32).unwrap_or(i32::MAX)))
        .filter(
            listings::rooms_available.ge(listings_request
                .rooms_available_min
                .map(|x| x as i32)
                .unwrap_or(i32::MIN)),
        )
        .filter(
            listings::rooms_available.le(listings_request
                .rooms_available_max
                .map(|x| x as i32)
                .unwrap_or(i32::MAX)),
        )
        .filter(listings::rooms_total.ge(listings_request.rooms_total_min.map(|x| x as i32).unwrap_or(i32::MIN)))
        .filter(listings::rooms_total.le(listings_request.rooms_total_max.map(|x| x as i32).unwrap_or(i32::MAX)))
        .filter(
            listings::bathrooms_available.ge(listings_request
                .bathrooms_available_min
                .map(|x| x as i32)
                .unwrap_or(i32::MIN)),
        )
        .filter(
            listings::bathrooms_available.le(listings_request
                .bathrooms_available_max
                .map(|x| x as i32)
                .unwrap_or(i32::MAX)),
        )
        .filter(
            listings::bathrooms_total.ge(listings_request
                .bathrooms_total_min
                .map(|x| x as i32)
                .unwrap_or(i32::MIN)),
        )
        .filter(
            listings::bathrooms_total.le(listings_request
                .bathrooms_total_max
                .map(|x| x as i32)
                .unwrap_or(i32::MAX)),
        )
        .filter(
            listings::bathrooms_ensuite.ge(listings_request
                .bathrooms_ensuite_min
                .map(|x| x as i32)
                .unwrap_or(i32::MIN)),
        )
        .filter(
            listings::bathrooms_ensuite.le(listings_request
                .bathrooms_ensuite_max
                .map(|x| x as i32)
                .unwrap_or(i32::MAX)),
        )
        .filter(
            listings::lease_start.ge(listings_request
                .lease_start
                .map(|lsf| lsf.0)
                .unwrap_or(NaiveDate::MIN)
                .and_time(NaiveTime::default())),
        )
        .filter(
            listings::lease_end.le(listings_request
                .lease_end
                .map(|lsf| lsf.0)
                .unwrap_or(NaiveDate::from_isoywd(3000, 1, chrono::Weekday::Mon))
                .and_time(NaiveTime::default())),
        );

    let fetched_listings: (Vec<Listing>, i64) = if let Some(gender) = listings_request.gender {
        db_request
            .filter(listings::gender.eq(gender))
            .paginate(listings_request.page_number.into(), listings_request.page_size.into())
            .map_err(|e| match e {
                PaginationError::InvalidLimit => ServiceError::InvalidFieldError {
                    field: "page_size",
                    reason: format!("invalid"),
                },
            })?
            .load(&mut dbcon)?
    } else {
        db_request
            .paginate(listings_request.page_number.into(), listings_request.page_size.into())
            .map_err(|e| match e {
                PaginationError::InvalidLimit => ServiceError::InvalidFieldError {
                    field: "page_size",
                    reason: format!("invalid"),
                },
            })?
            .load(&mut dbcon)?
    };

    let pages = fetched_listings.1.try_into().or(Err(ServiceError::InternalError))?;

    Ok(Json(GetListingsResponse {
        listings: fetched_listings
            .0
            .into_iter()
            .map(|l| {
                let listing_images = listings_images::dsl::listings_images
                    .filter(listings_images::listing_id.eq(l.listing_id))
                    .load::<ListingImage>(&mut dbcon)?;

                let distance_meters = distance_meters(
                    listings_request.longitude,
                    listings_request.latitude,
                    l.longitude,
                    l.latitude,
                )?;

                if distance_meters >= listings_request.distance_meters_min.unwrap_or(f32::MIN)
                    && distance_meters <= listings_request.distance_meters_max.unwrap_or(f32::MAX)
                {
                    Ok(Some(ListingSummary::try_from_db(
                        l,
                        distance_meters,
                        listing_images.into_iter().map(|li| li.image_id).collect(),
                    )?))
                } else {
                    Ok(None)
                }
            })
            .filter_map(|result| match result {
                Ok(loption) => {
                    if let Some(l) = loption {
                        Some(Ok(l))
                    } else {
                        None
                    }
                },
                Err(e) => Some(Err(e)),
            })
            .collect::<Result<Vec<ListingSummary>, ServiceError>>()?,
        pages,
        liked: HashSet::default(),
    }))
}

pub fn routes() -> (Vec<Route>, OpenApi) {
    openapi_get_routes_spec![
        listings_create,
        listings_details,
        listings_favourite,
        listings_images_create,
        listings_images_get,
        listings_list,
        listings_unfavourite,
    ]
}
