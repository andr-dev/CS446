use diesel::{query_dsl::methods::FilterDsl, ExpressionMethods, RunQueryDsl};
use okapi::openapi3::OpenApi;
use rocket::{post, serde::json::Json, Route, State};
use rocket_okapi::{openapi, openapi_get_routes_spec};

use super::model::auth::{UserLoginRequest, UserLoginResponse};
use crate::{
    api::{token::generate_token, utils::hash_password},
    db::{model::users::User, schema::users},
    error::{ServiceError, ServiceResult},
    state::AppState,
};

#[openapi(tag = "Authentication")]
#[post("/login", format = "json", data = "<login_request>")]
fn auth_login(state: &State<AppState>, login_request: Json<UserLoginRequest>) -> ServiceResult<UserLoginResponse> {
    let mut dbcon = state.pool.get()?;

    let users: Vec<User> = users::dsl::users
        .filter(users::email.eq(&login_request.email))
        .load(&mut dbcon)?;

    debug_assert!(users.len() <= 1);

    let user = users.get(0).ok_or(ServiceError::AuthenticationError)?;

    if !user.password_hash.eq(&hash_password(&login_request.password)) {
        return Err(ServiceError::AuthenticationError);
    }

    if let Ok(token) = generate_token(user.user_id) {
        Ok(Json(UserLoginResponse { token }))
    } else {
        Err(ServiceError::InternalError)
    }
}

pub fn routes() -> (Vec<Route>, OpenApi) {
    openapi_get_routes_spec![auth_login]
}
