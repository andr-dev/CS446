use rocket::{http::Status, response::Responder, serde::json::Json, Request};
use thiserror::Error;

#[derive(Error, Debug)]
pub enum ServiceError {
    #[error("SerdeJson Error {source:?}")]
    SerdeJson {
        #[from]
        source: serde_json::Error,
    },
    #[error("Internal Error")]
    InternalError,
}

pub type ServiceResult<T> = std::result::Result<Json<T>, ServiceError>;

impl<'r, 'o: 'r> Responder<'r, 'o> for ServiceError {
    fn respond_to(self, req: &'r Request<'_>) -> rocket::response::Result<'o> {
        Status::InternalServerError.respond_to(req)
    }
}
