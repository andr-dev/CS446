use rocket_okapi::JsonSchema;
use serde::{Deserialize, Serialize};

#[derive(JsonSchema, Serialize, Deserialize, Clone, PartialEq)]
pub struct UserLoginRequest {
    pub email: String,
    pub password: String,
}

#[derive(JsonSchema, Serialize, Deserialize, Clone, PartialEq)]
pub struct UserLoginResponse {
    pub token: String,
}
