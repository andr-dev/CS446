use std::path::Path;

use diesel::{QueryDsl, RunQueryDsl};
use rocket::{get, post, serde::json::Json, State};
use rocket_okapi::openapi;
use tokio::fs::{read, write};
use uuid::Uuid;

use crate::{
    api::{
        model::listing::{ListingsImagesCreateRequest, ListingsImagesCreateResponse},
        token::AuthenticatedUser,
    },
    db::{
        model::listings::{ListingImage, NewListingImage},
        schema::listings_images,
    },
    error::{ServiceError, ServiceResult},
    state::AppState,
};

#[openapi(tag = "Listings")]
#[get("/images/<image_id>")]
pub async fn listings_images_get(
    state: &State<AppState>,
    _user: AuthenticatedUser,
    image_id: String,
) -> ServiceResult<String> {
    if image_id.chars().any(|c| !c.is_ascii_alphanumeric() && c != '-') {
        return Err(ServiceError::InvalidFieldError {
            field: "image_id",
            reason: format!("invalid character"),
        });
    }

    if Uuid::try_parse(&image_id).is_err() {
        return Err(ServiceError::InvalidFieldError {
            field: "image_id",
            reason: format!("invalid uuid"),
        });
    }

    let mut dbcon = state.pool.get()?;

    let listing_image: ListingImage = listings_images::dsl::listings_images.find(image_id).first(&mut dbcon)?;

    read(Path::new(&state.media_dir).join(format!("{}.{}", listing_image.image_id, listing_image.extension)))
        .await
        .map(|stream| Json(base64::encode(stream)))
        .map_err(|_| ServiceError::InternalError)
}

#[openapi(tag = "Listings")]
#[post("/images/create", format = "json", data = "<create_request>")]
pub async fn listings_images_create(
    state: &State<AppState>,
    user: AuthenticatedUser,
    create_request: Json<ListingsImagesCreateRequest>,
) -> ServiceResult<ListingsImagesCreateResponse> {
    let image = base64::decode(&create_request.image).map_err(|_| ServiceError::InvalidFieldError {
        field: "image",
        reason: format!("failed to decode base64 string"),
    })?;

    let data_type = infer::get(&image).ok_or(ServiceError::InvalidFieldError {
        field: "image",
        reason: format!("invalid image"),
    })?;

    if data_type.mime_type() != "image/jpeg" && data_type.mime_type() != "image/png" {
        return Err(ServiceError::InvalidFieldError {
            field: "image",
            reason: format!("invalid image"),
        });
    }

    let image_id = Uuid::new_v4().to_string();

    if write(
        Path::new(&state.media_dir).join(format!("{}.{}", image_id, data_type.extension())),
        &image,
    )
    .await
    .is_err()
    {
        return Err(ServiceError::InternalError);
    }

    let mut dbcon = state.pool.get()?;

    diesel::insert_into(listings_images::table)
        .values(&NewListingImage {
            image_id: &image_id,
            extension: data_type.extension(),
            user_id: user.user_id,
            listing_id: None,
        })
        .execute(&mut dbcon)?;

    Ok(Json(ListingsImagesCreateResponse { image_id }))
}
