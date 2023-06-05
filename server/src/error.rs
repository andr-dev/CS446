use rocket::{http::Status, response::Responder, serde::json::Json, Request};
use rocket_okapi::{
    okapi::schemars::schema_for, response::OpenApiResponderInner, util::add_schema_response,
};
use thiserror::Error;

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

    #[error("Internal Error")]
    InternalError,

    #[error("Authentication Error")]
    AuthenticationError,
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
        Status::InternalServerError.respond_to(req)
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
