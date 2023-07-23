use std::sync::Arc;

use diesel::{
    r2d2::{ConnectionManager, Pool},
    SqliteConnection,
};
use tokio::{fs::create_dir_all, sync::RwLock};

use crate::geocode::opencage::Opencage;

pub type ServicePool = Pool<ConnectionManager<SqliteConnection>>;

pub struct AppState<'a> {
    pub pool: ServicePool,
    pub media_dir: String,
    pub opencage: Arc<RwLock<Opencage<'a>>>,
    pub admin_user_id: i32,
}

impl<'a> AppState<'a> {
    pub async fn new() -> Result<AppState<'a>, Box<dyn std::error::Error>> {
        let media_dir = std::env::var("MEDIA_DIR")?;
        let opencage_apikey = std::env::var("OPENCAGE_APIKEY")?;
        let admin_user_id = std::env::var("ADMIN_USER_ID")?.parse()?;

        let opencage = Arc::new(RwLock::new(Opencage::new(opencage_apikey)));

        create_dir_all(&media_dir).await?;

        let manager = ConnectionManager::<SqliteConnection>::new(std::env::var("DATABASE_URL")?);

        Ok(AppState {
            pool: Pool::builder().test_on_check_out(true).build(manager)?,
            media_dir,
            opencage,
            admin_user_id,
        })
    }
}
