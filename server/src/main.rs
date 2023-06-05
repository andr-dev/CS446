extern crate proc_macro;

mod api;

mod cors;
use cors::CORS;

mod db;
mod error;

mod state;
use state::AppState;

use api::rocket;

#[rocket::main]
async fn main() -> Result<(), Box<dyn std::error::Error>> {
    let app_state = AppState::new().await?;

    let _ = rocket()
        .manage::<AppState>(app_state)
        .attach(CORS)
        .launch()
        .await?;

    Ok(())
}
