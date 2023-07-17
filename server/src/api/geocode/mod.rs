use geo_types::Point;
use okapi::openapi3::OpenApi;
use rocket::{get, serde::json::Json, Route, State};
use rocket_okapi::{openapi, openapi_get_routes_spec};

use super::{
    model::geocode::{ReverseGeocodeRequest, ReverseGeocodeResponse},
    utils::format_address,
};
use crate::{
    error::{ServiceError, ServiceResult},
    geocode::{Forward, GeocodingError},
    state::AppState,
};

#[openapi(tag = "Geocode")]
#[get("/reverse?<geocode_request..>")]
async fn reverse_geocode(
    state: &State<AppState<'_>>,
    geocode_request: ReverseGeocodeRequest,
) -> ServiceResult<ReverseGeocodeResponse> {
    let opencage = state.opencage.write().await;

    let response: Vec<Point> = opencage
        .forward(&format_address(
            geocode_request.address_line,
            geocode_request.address_city,
            geocode_request.address_postalcode,
            geocode_request.address_country,
        ))
        .await?;

    if let Some(point) = response.first() {
        Ok(Json(ReverseGeocodeResponse {
            latitude: point.0.y as f32,
            longitude: point.0.x as f32,
        }))
    } else {
        Err(ServiceError::GeocodingError(GeocodingError::Forward))
    }
}

pub fn routes() -> (Vec<Route>, OpenApi) {
    openapi_get_routes_spec![reverse_geocode]
}
