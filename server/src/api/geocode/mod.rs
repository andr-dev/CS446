use geo_types::Point;
use okapi::openapi3::OpenApi;
use rocket::{get, serde::json::Json, Route, State};
use rocket_okapi::{openapi, openapi_get_routes_spec};
use tokio::sync::RwLockWriteGuard;

use super::{
    model::geocode::{ForwardGeocodeRequest, ForwardGeocodeResponse},
    token::AuthenticatedUser,
    utils::format_address,
};
use crate::{
    error::{ServiceError, ServiceResult},
    geocode::{opencage::Opencage, Forward, GeocodingError},
    state::AppState,
};

pub async fn geocode_address(
    opencage: RwLockWriteGuard<'_, Opencage<'_>>,
    address: String,
) -> Result<ForwardGeocodeResponse, ServiceError> {
    let response: Vec<Point> = opencage.forward(&address).await?;

    if let Some(point) = response.first() {
        Ok(ForwardGeocodeResponse {
            latitude: point.0.y as f32,
            longitude: point.0.x as f32,
        })
    } else {
        Err(ServiceError::GeocodingError(GeocodingError::Forward))
    }
}

#[openapi(tag = "Geocode")]
#[get("/forward?<geocode_request..>")]
async fn forward_geocode(
    state: &State<AppState<'_>>,
    _user: AuthenticatedUser,
    geocode_request: ForwardGeocodeRequest,
) -> ServiceResult<ForwardGeocodeResponse> {
    let opencage = state.opencage.write().await;

    let address = format_address(
        geocode_request.address_line,
        geocode_request.address_city,
        geocode_request.address_postalcode,
        geocode_request.address_country,
    );

    geocode_address(opencage, address).await.map(|resp| Json(resp))
}

pub fn routes() -> (Vec<Route>, OpenApi) {
    openapi_get_routes_spec![forward_geocode]
}
