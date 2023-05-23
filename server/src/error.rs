use rocket::serde::Serialize;
use sqlx::FromRow;

#[derive(FromRow, Debug)]
pub struct ServiceError {
    pub error: Box<dyn std::error::Error>,
}

impl Serialize for ServiceError {
    fn serialize<S>(&self, serializer: S) -> Result<S::Ok, S::Error>
    where
        S: rocket::serde::Serializer,
    {
        serializer.serialize_str(&self.error.to_string())
    }
}
