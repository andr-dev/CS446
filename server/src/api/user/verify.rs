use std::path::Path;

use diesel::{ExpressionMethods, QueryDsl, RunQueryDsl};
use rocket::{get, post, serde::json::Json, State};
use rocket_okapi::openapi;
use tokio::fs::write;
use uuid::Uuid;

use crate::{
    api::{
        model::user::{UserVerifyUploadRequest, UserVerifyUploadResponse},
        token::AuthenticatedUser,
    },
    db::{model::users::UserVerified, schema::user_verified},
    error::{ServiceError, ServiceResult},
    state::AppState,
};

#[openapi(tag = "User")]
#[post("/verify/upload", format = "json", data = "<upload_request>")]
pub async fn user_verify_upload(
    state: &State<AppState<'_>>,
    user: AuthenticatedUser,
    upload_request: Json<UserVerifyUploadRequest>,
) -> ServiceResult<UserVerifyUploadResponse> {
    let image = base64::decode(&upload_request.image).map_err(|_| ServiceError::InvalidFieldError {
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

    diesel::insert_into(user_verified::table)
        .values(&UserVerified {
            user_id: user.user_id,
            verified: false,
            image_id: image_id,
            extension: data_type.extension().to_string(),
        })
        .execute(&mut dbcon)?;

    Ok(Json(UserVerifyUploadResponse {}))
}

#[openapi(tag = "User")]
#[post("/verify/approve?<user_id>")]
pub fn user_verify_approve(state: &State<AppState>, user: AuthenticatedUser, user_id: i32) -> ServiceResult<String> {
    if user.user_id != state.admin_user_id {
        return Err(ServiceError::Forbidden);
    }

    let mut dbcon = state.pool.get()?;

    diesel::update(user_verified::table)
        .filter(user_verified::dsl::user_id.eq(user_id))
        .set(user_verified::dsl::verified.eq(true))
        .execute(&mut dbcon)?;

    Ok(Json("OK".to_owned()))
}

#[openapi(tag = "User")]
#[get("/verified?<user_id>")]
pub fn user_is_verified(state: &State<AppState>, user: AuthenticatedUser, user_id: Option<i32>) -> ServiceResult<bool> {
    let mut dbcon = state.pool.get()?;

    let user: Vec<UserVerified> = user_verified::dsl::user_verified
        .find(user_id.unwrap_or(user.user_id))
        .load(&mut dbcon)?;

    Ok(Json(user.first().map(|user| user.verified).unwrap_or(false)))
}
