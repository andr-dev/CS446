use rocket_okapi::JsonSchema;
use serde::{Deserialize, Serialize};

#[derive(JsonSchema, Serialize, Deserialize, Clone, PartialEq)]
pub struct CreateUserRequest {
    pub first_name: String,
    pub last_name: String,
    pub email: String,
    pub password: String,
    pub gender: String,
}

#[derive(JsonSchema, Serialize, Deserialize, Clone, PartialEq)]
pub struct CreateUserResponse {
    pub user_id: i32,
}
