use serde::{Serialize, Deserialize};

#[derive(Serialize, Deserialize)]
#[allow(clippy::derive_partial_eq_without_eq)]
#[derive(Clone, PartialEq, ::prost::Message)]
pub struct UserLoginRequest {
    #[prost(string, tag = "1")]
    pub email: ::prost::alloc::string::String,
    #[prost(string, tag = "2")]
    pub password: ::prost::alloc::string::String,
}
#[derive(Serialize, Deserialize)]
#[allow(clippy::derive_partial_eq_without_eq)]
#[derive(Clone, PartialEq, ::prost::Message)]
pub struct UserLoginResponse {
    #[prost(string, tag = "1")]
    pub token: ::prost::alloc::string::String,
}
