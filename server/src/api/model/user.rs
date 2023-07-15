use rocket::FromForm;
use rocket_okapi::JsonSchema;
use serde::{Deserialize, Serialize};

#[derive(JsonSchema, Deserialize, Clone, PartialEq)]
pub struct CreateUserRequest {
    pub first_name: String,
    pub last_name: String,
    pub email: String,
    pub password: String,
    pub gender: String,
}

#[derive(JsonSchema, Serialize, Clone, PartialEq)]
pub struct CreateUserResponse {
    pub user_id: i32,
}

#[derive(JsonSchema, Serialize, Clone, PartialEq)]
pub struct GetUserResponse {
    pub first_name: String,
    pub last_name: String,
    pub email: String,
    pub gender: String,

    pub listing_id: Option<i32>,
}

#[derive(JsonSchema, Deserialize, Clone, PartialEq)]
pub struct UpdateUserRequest {
    pub first_name: Option<String>,
    pub last_name: Option<String>,
    pub gender: Option<String>,
}

#[derive(JsonSchema, Serialize, Clone, PartialEq)]
pub struct UpdateUserResponse {}

#[derive(JsonSchema, Deserialize, Clone, PartialEq)]
pub struct ChangePasswordUserRequest {
    pub password_old: String,
    pub password_new: String,
}

#[derive(JsonSchema, Serialize, Clone, PartialEq)]
pub struct ChangePasswordUserResponse {}

#[derive(JsonSchema, Deserialize, Clone, PartialEq)]
pub struct RateUserRequest {
    pub user_id: i32,
    pub rating: u8,
}

#[derive(JsonSchema, FromForm, Clone, PartialEq)]
pub struct GetRatingUserRequest {
    pub user_id: i32,
}
