use std::path::{Path, PathBuf};

use diesel::{QueryDsl, RunQueryDsl};
use rocket::{data::ToByteUnit, fs::NamedFile, get, post, serde::json::Json, Data, State};
use rocket_okapi::openapi;
use tokio::fs::write;
use uuid::Uuid;

use crate::{
    api::{model::listing::ListingsImagesCreateResponse, token::AuthenticatedUser},
    db::model::listings::{ListingImage, NewListingImage},
    error::{ServiceError, ServiceResult},
    state::AppState,
};

#[openapi]
#[get("/images/<image_id..>")]
pub async fn listings_images_get(
    state: &State<AppState>,
    _user: AuthenticatedUser,
    image_id: PathBuf,
) -> Option<NamedFile> {
    let mut dbcon = state.pool.get().ok()?;

    let listing_image: ListingImage = crate::db::schema::listings_images::dsl::listings_images
        .find(image_id.to_str()?)
        .first(&mut dbcon)
        .ok()?;

    NamedFile::open(Path::new(&state.media_dir).join(format!("{}.{}", listing_image.image_id, listing_image.extension)))
        .await
        .ok()
}

#[openapi]
#[post("/images/create", data = "<image>")]
pub async fn listings_images_create(
    state: &State<AppState>,
    user: AuthenticatedUser,
    image: Data<'_>,
) -> ServiceResult<ListingsImagesCreateResponse> {
    let image_id = Uuid::new_v4().to_string();

    let capvec = image
        .open(1.megabytes())
        .into_bytes()
        .await
        .map_err(|_| ServiceError::InternalError)?;

    if !capvec.is_complete() {
        return Err(ServiceError::InternalError);
    }

    let data_type = infer::get(&*capvec).ok_or(ServiceError::InternalError)?;

    if data_type.mime_type() != "image/jpeg" && data_type.mime_type() != "image/png" {
        return Err(ServiceError::InternalError);
    }

    if write(
        Path::new(&state.media_dir).join(format!("{}.{}", image_id, data_type.extension())),
        &*capvec,
    )
    .await
    .is_err()
    {
        return Err(ServiceError::InternalError);
    }

    let mut dbcon = state.pool.get()?;

    diesel::insert_into(crate::db::schema::listings_images::table)
        .values(&NewListingImage {
            image_id: &image_id,
            extension: data_type.extension(),
            user_id: user.user_id,
            listing_id: None,
        })
        .execute(&mut dbcon)?;

    Ok(Json(ListingsImagesCreateResponse { image_id }))
}
