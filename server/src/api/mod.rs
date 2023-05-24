use chrono::{DateTime, Utc};
use rocket::{get, routes, serde::json::Json, Route};

use crate::error::ServiceResult;

mod auth;

#[get("/ping")]
fn ping() -> ServiceResult<&'static str> {
    Ok(Json("pong"))
}

#[get("/time")]
fn time() -> ServiceResult<DateTime<Utc>> {
    Ok(Json(Utc::now()))
}

pub(super) fn routes() -> Vec<Route> {
    let mut routes = routes![ping, time];

    routes.extend(auth::routes());

    routes
}
