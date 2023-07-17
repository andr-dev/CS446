use rocket::FromForm;
use schemars::JsonSchema;
use serde::Serialize;

#[derive(JsonSchema, FromForm, Clone, PartialEq)]
pub struct ForwardGeocodeRequest {
    pub address_line: String,
    pub address_city: String,
    pub address_postalcode: String,
    pub address_country: String,
}

#[derive(JsonSchema, Serialize, Clone, PartialEq)]
pub struct ForwardGeocodeResponse {
    pub latitude: f32,
    pub longitude: f32,
}
