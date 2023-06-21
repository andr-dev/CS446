use diesel::{
    r2d2::{ConnectionManager, Pool},
    SqliteConnection,
};
use tokio::fs::create_dir_all;

pub type ServicePool = Pool<ConnectionManager<SqliteConnection>>;

pub struct AppState {
    pub pool: ServicePool,
    pub media_dir: String,
}

impl AppState {
    pub async fn new() -> Result<Self, Box<dyn std::error::Error>> {
        let media_dir = std::env::var("MEDIA_DIR")?;

        create_dir_all(&media_dir).await?;

        let manager = ConnectionManager::<SqliteConnection>::new(std::env::var("DATABASE_URL")?);

        Ok(AppState {
            pool: Pool::builder().test_on_check_out(true).build(manager)?,
            media_dir,
        })
    }
}
