use chrono::{Duration, Utc};
use jsonwebtoken::{decode, encode, Algorithm, DecodingKey, EncodingKey, Header, TokenData, Validation};
use okapi::openapi3::{Object, SecurityRequirement, SecurityScheme, SecuritySchemeData};
use rocket::{
    http::Status,
    request::{FromRequest, Outcome},
    Request,
};
use rocket_okapi::request::{OpenApiFromRequest, RequestHeaderInput};
use serde_derive::{Deserialize, Serialize};

use super::constants::{JWT_AUDIENCE, JWT_EXP_HOURS, JWT_SECRET};
use crate::error::ServiceError;

#[derive(Debug, Serialize, Deserialize)]
struct AuthenticatedUserClaims {
    aud: String,
    sub: i32,
    exp: i64,
}

pub(super) fn generate_token(user_id: i32) -> Result<String, jsonwebtoken::errors::Error> {
    encode(
        &Header::new(Algorithm::HS512),
        &AuthenticatedUserClaims {
            aud: JWT_AUDIENCE.to_string(),
            sub: user_id,
            exp: (Utc::now() + Duration::hours(JWT_EXP_HOURS)).timestamp_millis(),
        },
        &EncodingKey::from_secret(JWT_SECRET.as_bytes()),
    )
}

fn read_token(token: &str) -> Result<TokenData<AuthenticatedUserClaims>, jsonwebtoken::errors::Error> {
    let mut val = Validation::new(Algorithm::HS512);

    val.set_audience(&[JWT_AUDIENCE]);

    decode::<AuthenticatedUserClaims>(token, &DecodingKey::from_secret(JWT_SECRET.as_bytes()), &val)
}

#[derive(Debug)]
pub struct AuthenticatedUser {
    pub user_id: i32,
}

#[rocket::async_trait]
impl<'r> FromRequest<'r> for AuthenticatedUser {
    type Error = ServiceError;

    async fn from_request(request: &'r Request<'_>) -> Outcome<Self, Self::Error> {
        let keys: Vec<_> = request.headers().get("Authorization").collect();

        if keys.len() == 1 {
            if let ("Bearer ", token) = keys[0].split_at(7) {
                if let Ok(claim) = read_token(token) {
                    return Outcome::Success(AuthenticatedUser {
                        user_id: claim.claims.sub,
                    });
                }
            }
        }

        Outcome::Failure((Status::Unauthorized, ServiceError::AuthenticationError))
    }
}

impl<'a> OpenApiFromRequest<'a> for AuthenticatedUser {
    fn from_request_input(
        _gen: &mut rocket_okapi::gen::OpenApiGenerator,
        _name: String,
        _required: bool,
    ) -> rocket_okapi::Result<rocket_okapi::request::RequestHeaderInput> {
        let security_scheme = SecurityScheme {
            description: Some("Requires a JWT token to access.".to_owned()),
            data: SecuritySchemeData::Http {
                scheme: "bearer".to_string(),
                bearer_format: Some("JWT".to_string()),
            },
            extensions: Object::default(),
        };

        let mut security_req = SecurityRequirement::new();
        security_req.insert("HttpAuth".to_owned(), Vec::new());

        Ok(RequestHeaderInput::Security(
            "HttpAuth".to_owned(),
            security_scheme,
            security_req,
        ))
    }
}
