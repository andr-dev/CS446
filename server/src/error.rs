use rocket::{http::Status, response::Responder, serde::json::Json, Request};
use rocket_okapi::{okapi::schemars::schema_for, response::OpenApiResponderInner, util::add_schema_response};
use thiserror::Error;

use crate::geocode::GeocodingError;

#[derive(Error, Debug)]
pub enum ServiceError {
    #[error("SerdeJson Error {source:?}")]
    SerdeJson {
        #[from]
        source: serde_json::Error,
    },

    #[error("Diesel Error {source:?}")]
    Diesel {
        #[from]
        source: diesel::result::Error,
    },

    #[error("Database Error {source:?}")]
    R2D2 {
        #[from]
        source: r2d2::Error,
    },

    #[error("Invalid field {}, reason: {}", field, reason)]
    InvalidFieldError { field: &'static str, reason: String },

    #[error("Internal Error")]
    InternalError,

    #[error("Authentication Error")]
    AuthenticationError,

    #[error("Geocoding Error")]
    GeocodingError(#[from] GeocodingError),

    #[error("Not Found Error")]
    NotFound,

    #[error("Forbidden")]
    Forbidden,
}

impl rocket_okapi::JsonSchema for ServiceError {
    fn schema_name() -> String {
        format!("ServiceError")
    }

    fn json_schema(
        _: &mut rocket_okapi::okapi::schemars::gen::SchemaGenerator,
    ) -> rocket_okapi::okapi::schemars::schema::Schema {
        rocket_okapi::okapi::schemars::schema::Schema::Object(schema_for!(String).schema)
    }
}

pub type ServiceResult<T> = std::result::Result<Json<T>, ServiceError>;

impl<'r, 'o: 'r> Responder<'r, 'o> for ServiceError {
    fn respond_to(self, req: &'r Request<'_>) -> rocket::response::Result<'o> {
        match self {
            ServiceError::AuthenticationError => Status::Unauthorized.respond_to(req),
            ServiceError::SerdeJson { source } => Json(source.to_string()).respond_to(req),
            ServiceError::Diesel { source } => Json(source.to_string()).respond_to(req),
            ServiceError::R2D2 { source } => Json(source.to_string()).respond_to(req),
            ServiceError::InvalidFieldError { field, reason } => {
                Json(format!("Invalid field {}, reason: {}", field, reason)).respond_to(req)
            },
            ServiceError::GeocodingError(e) => Json(e.to_string()).respond_to(req),
            ServiceError::InternalError => Status::InternalServerError.respond_to(req),
            ServiceError::NotFound => Status::NotFound.respond_to(req),
            ServiceError::Forbidden => Status::Forbidden.respond_to(req),
        }
    }
}

impl OpenApiResponderInner for ServiceError {
    fn responses(
        gen: &mut rocket_okapi::gen::OpenApiGenerator,
    ) -> rocket_okapi::Result<rocket_okapi::okapi::openapi3::Responses> {
        let mut responses = rocket_okapi::okapi::openapi3::Responses::default();

        let schema = gen.json_schema::<ServiceError>();

        add_schema_response(&mut responses, 500, "application/json", schema)?;

        Ok(responses)
    }
}
