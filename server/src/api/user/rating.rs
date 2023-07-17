use diesel::{ExpressionMethods, QueryDsl, RunQueryDsl};
use rocket::{get, post, serde::json::Json, State};
use rocket_okapi::openapi;

use crate::{
    api::{
        model::user::{GetRatingUserRequest, RateUserRequest},
        token::AuthenticatedUser,
    },
    db::{model::users::UserRating, schema::user_ratings, schema_view::user_rating},
    error::{ServiceError, ServiceResult},
    state::AppState,
};

#[openapi(tag = "User")]
#[post("/rate", format = "json", data = "<rate_request>")]
pub fn user_rate(
    state: &State<AppState>,
    user: AuthenticatedUser,
    rate_request: Json<RateUserRequest>,
) -> ServiceResult<String> {
    if rate_request.rating < 1 || rate_request.rating > 5 {
        return Err(ServiceError::InvalidFieldError {
            field: "rating",
            reason: format!("{} is not a value between 1 and 5", rate_request.rating),
        });
    }

    let mut dbcon = state.pool.get()?;

    diesel::insert_into(user_ratings::table)
        .values(UserRating {
            user_id: rate_request.user_id,
            rater_user_id: user.user_id,
            rating: rate_request.rating.into(),
        })
        .on_conflict((user_ratings::user_id, user_ratings::rater_user_id))
        .do_update()
        .set(user_ratings::rating.eq(Into::<i32>::into(rate_request.rating)))
        .execute(&mut dbcon)?;

    Ok(Json("OK".to_owned()))
}

#[openapi(tag = "User")]
#[get("/get_rating?<rating_request..>")]
pub fn user_get_rating(
    state: &State<AppState>,
    _user: AuthenticatedUser,
    rating_request: GetRatingUserRequest,
) -> ServiceResult<f32> {
    let mut dbcon = state.pool.get()?;

    let rating: (i32, f32) = user_rating::dsl::user_rating
        .filter(user_rating::user_id.eq(rating_request.user_id))
        .first(&mut dbcon)
        .unwrap_or((0, 0.0));

    Ok(Json(rating.1))
}
