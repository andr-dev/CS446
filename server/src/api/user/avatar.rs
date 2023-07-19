use std::path::Path;

use diesel::{ExpressionMethods, QueryDsl, RunQueryDsl};
use rocket::{get, post, serde::json::Json, State};
use rocket_okapi::openapi;
use tokio::fs::{read, write};
use uuid::Uuid;

use crate::{
    api::{
        model::user::{UserAvatarUpdateRequest, UserAvatarUpdateResponse},
        token::AuthenticatedUser,
    },
    db::{model::users::UserAvatar, schema::user_avatars},
    error::{ServiceError, ServiceResult},
    state::AppState,
};

#[openapi(tag = "User")]
#[get("/avatar/<user_id>")]
pub async fn user_avatar_get(
    state: &State<AppState<'_>>,
    _user: AuthenticatedUser,
    user_id: i32,
) -> ServiceResult<String> {
    let mut dbcon = state.pool.get()?;

    let user_avatar: UserAvatar = user_avatars::dsl::user_avatars.find(user_id).first(&mut dbcon)?;

    read(Path::new(&state.media_dir).join(format!("{}.{}", user_avatar.image_id, user_avatar.extension)))
        .await
        .map(|stream| Json(base64::encode(stream)))
        .map_err(|_| ServiceError::InternalError)
}

#[openapi(tag = "User")]
#[post("/avatar/update", format = "json", data = "<update_request>")]
pub async fn user_avatar_update(
    state: &State<AppState<'_>>,
    user: AuthenticatedUser,
    update_request: Json<UserAvatarUpdateRequest>,
) -> ServiceResult<UserAvatarUpdateResponse> {
    let avatar = base64::decode(&update_request.avatar).map_err(|_| ServiceError::InvalidFieldError {
        field: "avatar",
        reason: format!("failed to decode base64 string"),
    })?;

    let data_type = infer::get(&avatar).ok_or(ServiceError::InvalidFieldError {
        field: "avatar",
        reason: format!("invalid image"),
    })?;

    if data_type.mime_type() != "image/jpeg" && data_type.mime_type() != "image/png" {
        return Err(ServiceError::InvalidFieldError {
            field: "avatar",
            reason: format!("invalid image"),
        });
    }

    let image_id = Uuid::new_v4().to_string();

    if write(
        Path::new(&state.media_dir).join(format!("{}.{}", image_id, data_type.extension())),
        &avatar,
    )
    .await
    .is_err()
    {
        return Err(ServiceError::InternalError);
    }

    let mut dbcon = state.pool.get()?;

    diesel::insert_into(user_avatars::dsl::user_avatars)
        .values(&UserAvatar {
            image_id: image_id.clone(),
            extension: data_type.extension().to_string(),
            user_id: user.user_id,
        })
        .on_conflict(user_avatars::dsl::user_id)
        .do_update()
        .set((
            user_avatars::dsl::image_id.eq(image_id.clone()),
            user_avatars::dsl::extension.eq(data_type.extension().to_string()),
        ))
        .execute(&mut dbcon)?;

    Ok(Json(UserAvatarUpdateResponse { avatar_id: image_id }))
}
