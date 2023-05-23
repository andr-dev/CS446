use rocket::{get, routes, Route};

mod auth;

#[get("/ping")]
fn ping() -> &'static str {
    "pong"
}

pub(super) fn routes() -> Vec<Route> {
    let mut routes = routes![ping];

    routes.extend(auth::routes());

    routes
}
