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

#[derive(JsonSchema, Serialize, Deserialize, Clone, PartialEq)]
pub struct GetUserResponse {
    pub first_name: String,
    pub last_name: String,
    pub email: String,
    pub gender: String,

    pub listing_id: Option<i32>,
}

#[derive(JsonSchema, Serialize, Deserialize, Clone, PartialEq)]
pub struct UpdateUserRequest {
    pub first_name: Option<String>,
    pub last_name: Option<String>,
    pub gender: Option<String>,
}

#[derive(JsonSchema, Serialize, Deserialize, Clone, PartialEq)]
pub struct UpdateUserResponse {}

#[derive(JsonSchema, Serialize, Deserialize, Clone, PartialEq)]
pub struct ChangePasswordUserRequest {
    pub password_old: String,
    pub password_new: String,
}

#[derive(JsonSchema, Serialize, Deserialize, Clone, PartialEq)]
pub struct ChangePasswordUserResponse {}
