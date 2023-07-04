use chrono::Utc;
use rocket::{get, serde::json::Json, Build, Rocket};
use rocket_okapi::{mount_endpoints_and_merged_docs, openapi, openapi_get_routes_spec, settings::OpenApiSettings};

use crate::error::ServiceResult;

mod auth;
mod constants;
mod listings;
mod model;
mod token;
mod user;
mod utils;

#[openapi(tag = "Server")]
#[get("/ping")]
fn ping() -> Json<&'static str> {
    Json("pong")
}

#[openapi(tag = "Server")]
#[get("/time")]
fn time() -> ServiceResult<i64> {
    Ok(Json(Utc::now().timestamp_millis()))
}

pub fn rocket() -> Rocket<Build> {
    let mut rocket_builder = rocket::build();

    let openapi_settings = OpenApiSettings::default();

    mount_endpoints_and_merged_docs! {
        rocket_builder,
        "/api",
        openapi_settings,

        "" => openapi_get_routes_spec![ping, time],
        "/auth" => auth::routes(),
        "/listings" => listings::routes(),
        "/user" => user::routes(),
    };

    rocket_builder
}
