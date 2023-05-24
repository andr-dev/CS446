use rocket::{post, routes, serde::json::Json, Route, State};

use crate::{
    api::auth::model::UserCreateInfo,
    error::{ServiceError, ServiceResult},
    state::AppState,
};

mod model;

#[post("/auth/create", format = "json", data = "<create_info>")]
fn create(state: &State<AppState>, create_info: Json<UserCreateInfo>) -> ServiceResult<String> {
    Err(ServiceError::InternalError)
}

pub(super) fn routes() -> Vec<Route> {
    let mut routes = routes![];

    routes
}
