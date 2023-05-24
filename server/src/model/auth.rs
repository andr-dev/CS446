use sqlx::types::Uuid;

pub struct DbUserAuthInfo {
    pub user_id: Uuid,
    pub password_hash: String,
}

impl DbUserAuthInfo {
    
}