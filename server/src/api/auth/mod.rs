use diesel::{query_dsl::methods::FilterDsl, ExpressionMethods, RunQueryDsl};
use rocket::{post, routes, serde::json::Json, Route, State};

use crate::{
    api::{token::generate_token, utils::hash_password},
    error::{ServiceError, ServiceResult},
    model::users::User,
    proto::auth::{UserLoginRequest, UserLoginResponse},
    state::AppState,
};

#[post("/auth/login", format = "json", data = "<login_request>")]
fn login(
    state: &State<AppState>,
    login_request: Json<UserLoginRequest>,
) -> ServiceResult<UserLoginResponse> {
    let mut dbcon = state.pool.get()?;

    let users: Vec<User> = crate::schema::users::dsl::users
        .filter(crate::schema::users::email.eq(&login_request.email))
        .load(&mut dbcon)?;

    println!("got users: {:?}", users);

    debug_assert!(users.len() <= 1);

    let user = users.get(0).ok_or(ServiceError::AuthenticationError)?;

    if !user
        .password_hash
        .eq(&hash_password(&login_request.password))
    {
        return Err(ServiceError::AuthenticationError);
    }

    if let Ok(token) = generate_token(user.user_id) {
        Ok(Json(UserLoginResponse { token }))
    } else {
        Err(ServiceError::InternalError)
    }
}

pub(super) fn routes() -> Vec<Route> {
    let routes = routes![login];

    routes
}
