use std::ops::Deref;

use chrono::NaiveDate;
use rocket::{
    async_trait,
    form::{FromFormField, ValueField},
};
use schemars::JsonSchema;

pub mod auth;
pub mod listing;
pub mod user;

#[derive(Clone, PartialEq)]
pub struct NaiveDateForm(pub NaiveDate);

#[async_trait]
impl<'v> FromFormField<'v> for NaiveDateForm {
    fn from_value(field: ValueField<'v>) -> rocket::form::Result<'v, Self> {
        Ok(NaiveDateForm(
            NaiveDate::parse_from_str(field.value, "YYYY-MM-DD").map_err(rocket::form::Error::custom)?,
        ))
    }
}

impl Deref for NaiveDateForm {
    type Target = NaiveDate;

    fn deref(&self) -> &Self::Target {
        &self.0
    }
}

impl JsonSchema for NaiveDateForm {
    fn schema_name() -> String {
        <String as JsonSchema>::schema_name()
    }

    fn json_schema(gen: &mut schemars::gen::SchemaGenerator) -> schemars::schema::Schema {
        <String as JsonSchema>::json_schema(gen)
    }
}
