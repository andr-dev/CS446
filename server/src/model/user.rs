use serde_derive::Serialize;
use sqlx::FromRow;

use crate::{error::ServiceResult, state::ServicePool};

#[derive(FromRow, Serialize, Debug)]
pub struct DbUserInfo {
    pub first_name: String,
    pub last_name: String,
    pub email: String,
    pub password_hash: String,
    pub gender: String,
}

impl DbUserInfo {
    // pub async fn create_user(pool: &ServicePool, create_info: UserCreateInfo) -> ServiceResult {}
}
