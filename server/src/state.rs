use diesel::{
    r2d2::{ConnectionManager, Pool},
    SqliteConnection,
};

pub type ServicePool = Pool<ConnectionManager<SqliteConnection>>;

pub struct AppState {
    pub pool: ServicePool,
}

impl AppState {
    pub async fn new() -> Result<Self, Box<dyn std::error::Error>> {
        let manager = ConnectionManager::<SqliteConnection>::new(std::env::var("DATABASE_URL")?);

        Ok(AppState {
            pool: Pool::builder().test_on_check_out(true).build(manager)?,
        })
    }
}
