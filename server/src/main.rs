extern crate proc_macro;

use cors::CORS;
use rocket::{Build, Rocket};
use state::AppState;

mod api;
mod cors;
mod error;
mod model;
mod proto;
mod schema;
mod state;

fn rocket() -> Rocket<Build> {
    rocket::build().mount("/api", api::routes())
}

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
