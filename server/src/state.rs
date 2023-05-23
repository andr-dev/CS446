use sqlx::{sqlite::SqlitePoolOptions, Pool, Sqlite};

pub struct AppState {
    pub pool: Pool<Sqlite>,
}

impl AppState {
    pub async fn new() -> Result<Self, Box<dyn std::error::Error>> {
        let pool = SqlitePoolOptions::new()
            .max_connections(4)
            .connect(&std::env::var("DATABASE_URL")?)
            .await?;

        Ok(AppState { pool })
    }
}
