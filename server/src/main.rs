extern crate proc_macro;

#[macro_use]
mod macros;

use rocket::fs::FileServer;

mod api;
use api::rocket;

mod cors;
use cors::CORS;

mod db;
mod error;

mod geocode;

mod state;
use state::AppState;

#[rocket::main]
async fn main() -> Result<(), Box<dyn std::error::Error>> {
    let app_state = AppState::new().await?;

    let _ = rocket()
        .mount("/", FileServer::from("../docs/swaggerui"))
        .manage::<AppState>(app_state)
        .attach(CORS)
        .launch()
        .await?;

    Ok(())
}
