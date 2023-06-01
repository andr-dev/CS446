use diesel::{QueryDsl, RunQueryDsl};
use rand::Rng;
use rocket::{get, post, routes, serde::json::Json, Route, State};

use crate::{
    api::token::AuthenticatedUser,
    error::{ServiceError, ServiceResult},
    model::users::{NewUser, User},
    proto::user::{CreateUserRequest, CreateUserResponse},
    state::AppState,
};

use super::utils::hash_password;

#[post("/user/create", format = "json", data = "<create_user_request>")]
fn create(
    state: &State<AppState>,
    create_user_request: Json<CreateUserRequest>,
) -> ServiceResult<CreateUserResponse> {
    let mut dbcon = state.pool.get()?;

    if !create_user_request.email.ends_with("@uwaterloo.ca") {
        return Err(ServiceError::InternalError);
    }

    let user_id: i64 = rand::thread_rng().gen();

    diesel::insert_into(crate::schema::users::table)
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

#[get("/user/email")]
fn email(state: &State<AppState>, user: AuthenticatedUser) -> ServiceResult<String> {
    let mut dbcon = state.pool.get()?;

    let user: User = crate::schema::users::dsl::users
        .find(user.user_id)
        .first(&mut dbcon)
        .unwrap();

    Ok(Json(user.email))
}

pub(super) fn routes() -> Vec<Route> {
    let routes = routes![create, email];

    routes
}
