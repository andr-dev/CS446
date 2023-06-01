use chrono::{Duration, Utc};
use jsonwebtoken::{
    decode, encode, Algorithm, DecodingKey, EncodingKey, Header, TokenData, Validation,
};
use rocket::{
    http::Status,
    request::{FromRequest, Outcome},
    Request,
};
use serde_derive::{Deserialize, Serialize};

use crate::error::ServiceError;

pub const JWT_SECRET: &'static str = "test"; // std::env!("API_SECRET_KEY");
pub const JWT_AUDIENCE: &'static str = "server_cs458";

pub(super) fn generate_token(user_id: i64) -> Result<String, jsonwebtoken::errors::Error> {
    encode(
        &Header::new(Algorithm::HS512),
        &AuthenticatedUserClaims {
            aud: JWT_AUDIENCE.to_string(),
            sub: user_id,
            exp: (Utc::now() + Duration::hours(1)).timestamp_millis(),
        },
        &EncodingKey::from_secret(JWT_SECRET.as_bytes()),
    )
}

fn read_token(
    token: &str,
) -> Result<TokenData<AuthenticatedUserClaims>, jsonwebtoken::errors::Error> {
    let mut val = Validation::new(Algorithm::HS512);

    val.set_audience(&[JWT_AUDIENCE]);

    decode::<AuthenticatedUserClaims>(
        token,
        &DecodingKey::from_secret(JWT_SECRET.as_bytes()),
        &val,
    )
}

#[derive(Debug)]
pub struct AuthenticatedUser {
    pub user_id: i64,
}

#[derive(Debug, Serialize, Deserialize)]
struct AuthenticatedUserClaims {
    aud: String,
    sub: i64,
    exp: i64,
}

#[rocket::async_trait]
impl<'r> FromRequest<'r> for AuthenticatedUser {
    type Error = ServiceError;

    async fn from_request(request: &'r Request<'_>) -> Outcome<Self, Self::Error> {
        let keys: Vec<_> = request.headers().get("Authentication").collect();

        if keys.len() != 1 {
            return Outcome::Failure((Status::Unauthorized, ServiceError::AuthenticationError));
        }

        match read_token(keys[0]) {
            Ok(claim) => Outcome::Success(AuthenticatedUser {
                user_id: claim.claims.sub,
            }),
            Err(_) => Outcome::Failure((Status::Unauthorized, ServiceError::AuthenticationError)),
        }
    }
}
