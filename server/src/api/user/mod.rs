use diesel::{ExpressionMethods, QueryDsl, RunQueryDsl};
use okapi::openapi3::OpenApi;
use rand::Rng;
use rocket::{get, post, serde::json::Json, Route, State};
use rocket_okapi::{openapi, openapi_get_routes_spec};

mod avatar;
use avatar::{
    okapi_add_operation_for_user_avatar_get_,
    okapi_add_operation_for_user_avatar_update_,
    user_avatar_get,
    user_avatar_update,
};

mod rating;
use rating::{
    okapi_add_operation_for_user_get_rating_,
    okapi_add_operation_for_user_rate_,
    user_get_rating,
    user_rate,
};

mod verify;
use verify::{
    okapi_add_operation_for_user_is_verified_,
    okapi_add_operation_for_user_verify_approve_,
    okapi_add_operation_for_user_verify_upload_,
    user_is_verified,
    user_verify_approve,
    user_verify_upload,
};

use super::{
    model::user::{
        ChangePasswordUserRequest,
        ChangePasswordUserResponse,
        CreateUserRequest,
        CreateUserResponse,
        GetUserResponse,
        UpdateUserRequest,
        UpdateUserResponse,
    },
    utils::hash_password,
};
use crate::{
    api::token::AuthenticatedUser,
    db::{
        model::users::{NewUser, User},
        schema::{listings, users},
        schema_view::user_rating,
    },
    error::{ServiceError, ServiceResult},
    state::AppState,
};

#[openapi(tag = "User")]
#[post("/create", format = "json", data = "<create_user_request>")]
fn user_create(
    state: &State<AppState>,
    create_user_request: Json<CreateUserRequest>,
) -> ServiceResult<CreateUserResponse> {
    let mut dbcon = state.pool.get()?;

    if !create_user_request.email.ends_with("@uwaterloo.ca") {
        return Err(ServiceError::InvalidFieldError {
            field: "email",
            reason: format!("{} does not end with @uwaterloo.ca", create_user_request.email),
        });
    }

    let user_id: i32 = rand::thread_rng().gen();

    diesel::insert_into(users::table)
        .values(&NewUser {
            user_id,
            first_name: &create_user_request.first_name,
            last_name: &create_user_request.last_name,
            email: &create_user_request.email,
            password_hash: &hash_password(&create_user_request.password),
            gender: &create_user_request.gender,
        })
        .execute(&mut dbcon)?;

    Ok(Json(CreateUserResponse { user_id }))
}

#[openapi(tag = "User")]
#[get("/get?<user_id>")]
fn user_get(state: &State<AppState>, user: AuthenticatedUser, user_id: Option<i32>) -> ServiceResult<GetUserResponse> {
    let mut dbcon = state.pool.get()?;

    let user: User = users::dsl::users
        .find(user_id.unwrap_or(user.user_id))
        .first(&mut dbcon)
        .unwrap();

    let rating: (i32, f32) = user_rating::dsl::user_rating
        .filter(user_rating::user_id.eq(user.user_id))
        .first(&mut dbcon)
        .unwrap_or((0, 0.0));

    let listing_ids: Vec<i32> = listings::dsl::listings
        .select(listings::dsl::listing_id)
        .filter(listings::owner_user_id.eq(user.user_id))
        .load::<i32>(&mut dbcon)?;

    debug_assert!(listing_ids.len() <= 1);

    Ok(Json(GetUserResponse {
        first_name: user.first_name,
        last_name: user.last_name,
        email: user.email,
        gender: user.gender,

        listing_id: listing_ids.first().copied(),
        rating: rating.1,
    }))
}

#[openapi(tag = "User")]
#[post("/update", format = "json", data = "<update_user_request>")]
fn user_update(
    state: &State<AppState>,
    user: AuthenticatedUser,
    update_user_request: Json<UpdateUserRequest>,
) -> ServiceResult<UpdateUserResponse> {
    let mut dbcon = state.pool.get()?;

    if let Some(first_name) = &update_user_request.first_name {
        diesel::update(users::dsl::users)
            .filter(users::dsl::user_id.eq(user.user_id))
            .set(users::dsl::first_name.eq(first_name))
            .execute(&mut dbcon)?;
    }

    if let Some(last_name) = &update_user_request.last_name {
        diesel::update(users::dsl::users)
            .filter(users::dsl::user_id.eq(user.user_id))
            .set(users::dsl::last_name.eq(last_name))
            .execute(&mut dbcon)?;
    }

    if let Some(gender) = &update_user_request.gender {
        diesel::update(users::dsl::users)
            .filter(users::dsl::user_id.eq(user.user_id))
            .set(users::dsl::gender.eq(gender))
            .execute(&mut dbcon)?;
    }

    Ok(Json(UpdateUserResponse {}))
}

#[openapi(tag = "User")]
#[post("/change_password", format = "json", data = "<change_password_user_request>")]
fn user_change_password(
    state: &State<AppState>,
    user: AuthenticatedUser,
    change_password_user_request: Json<ChangePasswordUserRequest>,
) -> ServiceResult<ChangePasswordUserResponse> {
    let mut dbcon = state.pool.get()?;

    let user: User = users::dsl::users.find(user.user_id).first(&mut dbcon).unwrap();

    if user.password_hash != hash_password(&change_password_user_request.password_old) {
        return Err(ServiceError::InternalError);
    }

    let password_hash = hash_password(&change_password_user_request.password_new);

    diesel::update(users::dsl::users)
        .filter(users::dsl::user_id.eq(user.user_id))
        .set(users::dsl::password_hash.eq(password_hash))
        .execute(&mut dbcon)?;

    Ok(Json(ChangePasswordUserResponse {}))
}

pub fn routes() -> (Vec<Route>, OpenApi) {
    openapi_get_routes_spec![
        user_avatar_get,
        user_avatar_update,
        user_change_password,
        user_create,
        user_get,
        user_get_rating,
        user_is_verified,
        user_rate,
        user_update,
        user_verify_approve,
        user_verify_upload,
    ]
}
