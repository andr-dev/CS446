extern crate proc_macro;

mod api;
use api::rocket;

mod cors;
use cors::CORS;

mod db;
mod error;

mod state;
use state::AppState;

use rocket::fs::FileServer;

#[rocket::main]
async fn main() -> Result<(), Box<dyn std::error::Error>> {
    let app_state = AppState::new().await?;

    let _ = rocket()
        .mount("/", FileServer::from("../docs"))
        .manage::<AppState>(app_state)
        .attach(CORS)
        .launch()
        .await?;

    Ok(())
}
