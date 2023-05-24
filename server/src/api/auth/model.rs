use serde_derive::Deserialize;

#[derive(Deserialize)]
pub struct UserCreateInfo {
    pub first_name: String,
    pub last_name: String,
    pub email: String,
    pub password: String,
    pub password_confirm: String,
    pub gender: String,
}
