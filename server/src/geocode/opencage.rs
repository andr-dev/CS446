//! Modified from https://raw.githubusercontent.com/georust/geocoding/master/src/opencage.rs
//!
//! The [OpenCage Geocoding](https://opencagedata.com/) provider.
//!
//! Geocoding methods are implemented on the [`Opencage`](struct.Opencage.html)
//! struct. Please see the [API documentation](https://opencagedata.com/api) for details.
//! Note that rate limits apply to the free tier:
//! there is a [rate-limit](https://opencagedata.com/api#rate-limiting) of 1 request per second,
//! and a quota of calls allowed per 24-hour period. The remaining daily quota
//! can be retrieved using the [`remaining_calls()`](struct.Opencage.html#
//! method.remaining_calls) method. If you are a paid tier user, this value will
//! not be updated, and will remain `None`. ### A Note on Coordinate Order
//! This provider's API documentation shows all coordinates in `[Latitude,
//! Longitude]` order. However, `Geocoding` requires input `Point` coordinate
//! order as `[Longitude, Latitude]` `(x, y)`, and returns coordinates with that
//! order.
//! ```
use std::{
    collections::HashMap,
    fmt::Debug,
    sync::{Arc, Mutex},
};

use chrono::{naive::serde::ts_seconds::deserialize as from_ts, NaiveDateTime};
use geo_types::Point;
use num_traits::Float;
use reqwest::{
    header::{HeaderMap, HeaderValue, USER_AGENT},
    Client,
};
use rocket::async_trait;
use serde::{de::DeserializeOwned, Deserialize, Deserializer, Serialize};

use super::{Forward, GeocodingError, InputBounds, Reverse, UA_STRING};

macro_rules! add_optional_param {
    ($query:expr, $param:expr, $name:expr) => {
        if let Some(p) = $param {
            $query.push(($name, p))
        }
    };
}

// Please see the [API documentation](https://opencagedata.com/api#forward-opt) for details.
#[derive(Default)]
pub struct Parameters<'a> {
    pub language: Option<&'a str>,
    pub countrycode: Option<&'a str>,
    pub limit: Option<&'a str>,
}

impl<'a> Parameters<'a> {
    fn as_query(&self) -> Vec<(&'a str, &'a str)> {
        let mut query = vec![];
        add_optional_param!(query, self.language, "language");
        add_optional_param!(query, self.countrycode, "countrycode");
        add_optional_param!(query, self.limit, "limit");
        query
    }
}

pub fn deserialize_string_or_int<'de, D>(deserializer: D) -> Result<String, D::Error>
where
    D: Deserializer<'de>,
{
    #[derive(Deserialize)]
    #[serde(untagged)]
    enum StringOrInt {
        String(String),
        Int(i32),
    }

    match StringOrInt::deserialize(deserializer)? {
        StringOrInt::String(s) => Ok(s),
        StringOrInt::Int(i) => Ok(i.to_string()),
    }
}

// OpenCage has a custom rate-limit header, indicating remaining calls
// header! { (XRatelimitRemaining, "X-RateLimit-Remaining") => [i32] }
static XRL: &str = "x-ratelimit-remaining";
/// Use this constant if you don't need to restrict a `forward_full` call with a
/// bounding box
pub static NOBOX: Option<InputBounds<f64>> = None::<InputBounds<f64>>;

/// An instance of the Opencage Geocoding service
pub struct Opencage<'a> {
    api_key: String,
    client: Client,
    endpoint: String,
    pub parameters: Parameters<'a>,
    remaining: Arc<Mutex<Option<i32>>>,
}

impl<'a> Opencage<'a> {
    /// Create a new OpenCage geocoding instance
    pub fn new(api_key: String) -> Self {
        let mut headers = HeaderMap::new();
        headers.insert(USER_AGENT, HeaderValue::from_static(UA_STRING));
        let client = Client::builder()
            .default_headers(headers)
            .build()
            .expect("Couldn't build a client!");

        let parameters = Parameters::default();
        Opencage {
            api_key,
            client,
            parameters,
            endpoint: "https://api.opencagedata.com/geocode/v1/json".to_string(),
            remaining: Arc::new(Mutex::new(None)),
        }
    }
    /// Retrieve the remaining API calls in your daily quota
    ///
    /// Initially, this value is `None`. Any OpenCage API call using a "Free
    /// Tier" key will update this value to reflect the remaining quota for
    /// the API key. See the [API docs](https://opencagedata.com/api#rate-limiting) for details.
    pub fn remaining_calls(&self) -> Option<i32> {
        *self.remaining.lock().unwrap()
    }
    /// A reverse lookup of a point, returning an annotated response.
    ///
    /// This method passes the `no_record` parameter to the API.
    ///
    /// # Examples
    ///
    ///```
    /// use geocoding::{Opencage, Point};
    ///
    /// let oc = Opencage::new("dcdbf0d783374909b3debee728c7cc10".to_string());
    /// let p = Point::new(2.12870, 41.40139);
    /// // a full `OpencageResponse` struct
    /// let res = oc.reverse_full(&p).unwrap();
    /// // responses may include multiple results
    /// let first_result = &res.results[0];
    /// assert_eq!(
    ///     first_result.components["road"],
    ///     "Carrer de Calatrava"
    /// );
    /// ```
    pub async fn reverse_full<T>(&self, point: &Point<T>) -> Result<OpencageResponse<T>, GeocodingError>
    where
        T: Float + DeserializeOwned + Debug,
    {
        let q = format!(
            "{}, {}",
            // OpenCage expects lat, lon order
            (&point.y().to_f64().unwrap().to_string()),
            &point.x().to_f64().unwrap().to_string()
        );
        let mut query = vec![
            ("q", q.as_str()),
            ("key", &self.api_key),
            ("no_annotations", "0"),
            ("no_record", "1"),
        ];
        query.extend(self.parameters.as_query());

        let resp = self
            .client
            .get(&self.endpoint)
            .query(&query)
            .send()
            .await?
            .error_for_status()?;
        // it's OK to index into this vec, because reverse-geocoding only returns a
        // single result
        if let Some(headers) = resp.headers().get::<_>(XRL) {
            let mut lock = self.remaining.try_lock();
            if let Ok(ref mut mutex) = lock {
                // not ideal, but typed headers are currently impossible in 0.9.x
                let h = headers.to_str()?;
                let h: i32 = h.parse()?;
                **mutex = Some(h)
            }
        }
        let res: OpencageResponse<T> = resp.json().await?;
        Ok(res)
    }
    /// A forward-geocoding lookup of an address, returning an annotated
    /// response.
    ///
    /// it is recommended that you restrict the search space by passing a
    /// [bounding box](struct.InputBounds.html) to search within.
    /// If you don't need or want to restrict the search using a bounding box
    /// (usually not recommended), you may pass the
    /// [`NOBOX`](static.NOBOX.html) static value instead.
    ///
    /// Please see [the documentation](https://opencagedata.com/api#ambiguous-results) for details
    /// of best practices in order to obtain good-quality results.
    ///
    /// This method passes the `no_record` parameter to the API.
    ///
    /// # Examples
    ///
    ///```
    /// use geocoding::{Opencage, InputBounds, Point};
    ///
    /// let oc = Opencage::new("dcdbf0d783374909b3debee728c7cc10".to_string());
    /// let address = "UCL Centre for Advanced Spatial Analysis";
    /// // Optionally restrict the search space using a bounding box.
    /// // The first point is the bottom-left corner, the second is the top-right.
    /// let bbox = InputBounds::new(
    ///     Point::new(-0.13806939125061035, 51.51989264641164),
    ///     Point::new(-0.13427138328552246, 51.52319711775629),
    /// );
    /// let res = oc.forward_full(&address, bbox).unwrap();
    /// let first_result = &res.results[0];
    /// // the first result is correct
    /// assert!(first_result.formatted.contains("90 Tottenham Court Road"));
    /// ```
    ///
    /// ```
    /// // You can pass NOBOX if you don't need bounds.
    /// use geocoding::{Opencage, InputBounds, Point};
    /// use geocoding::opencage::{NOBOX};
    /// let oc = Opencage::new("dcdbf0d783374909b3debee728c7cc10".to_string());
    /// let address = "Moabit, Berlin";
    /// let res = oc.forward_full(&address, NOBOX).unwrap();
    /// let first_result = &res.results[0];
    /// assert_eq!(
    ///     first_result.formatted,
    ///     "Moabit, Berlin, Germany"
    /// );
    /// ```
    ///
    /// ```
    /// // There are several ways to construct a Point, such as from a tuple
    /// use geocoding::{Opencage, InputBounds, Point};
    /// let oc = Opencage::new("dcdbf0d783374909b3debee728c7cc10".to_string());
    /// let address = "UCL Centre for Advanced Spatial Analysis";
    /// let bbox = InputBounds::new(
    ///     (-0.13806939125061035, 51.51989264641164),
    ///     (-0.13427138328552246, 51.52319711775629),
    /// );
    /// let res = oc.forward_full(&address, bbox).unwrap();
    /// let first_result = &res.results[0];
    /// assert!(
    ///     first_result.formatted.contains(
    ///         "90 Tottenham Court Road"
    /// ));
    /// ```
    pub async fn forward_full<T, U>(&self, place: &str, bounds: U) -> Result<OpencageResponse<T>, GeocodingError>
    where
        T: Float + DeserializeOwned + Debug,
        U: Into<Option<InputBounds<T>>>,
    {
        let ann = String::from("0");
        let record = String::from("1");
        // we need this to avoid lifetime inconvenience
        let bd;
        let mut query = vec![
            ("q", place),
            ("key", &self.api_key),
            ("no_annotations", &ann),
            ("no_record", &record),
        ];

        // If search bounds are passed, use them
        if let Some(bds) = bounds.into() {
            bd = String::from(bds);
            query.push(("bounds", &bd));
        }
        query.extend(self.parameters.as_query());

        let resp = self
            .client
            .get(&self.endpoint)
            .query(&query)
            .send()
            .await?
            .error_for_status()?;
        if let Some(headers) = resp.headers().get::<_>(XRL) {
            let mut lock = self.remaining.try_lock();
            if let Ok(ref mut mutex) = lock {
                // not ideal, but typed headers are currently impossible in 0.9.x
                let h = headers.to_str()?;
                let h: i32 = h.parse()?;
                **mutex = Some(h)
            }
        }
        let res: OpencageResponse<T> = resp.json().await?;
        Ok(res)
    }
}

#[async_trait]
impl<'a, T> Reverse<T> for Opencage<'a>
where
    T: Float + DeserializeOwned + Debug + Sync,
{
    /// A reverse lookup of a point. More detail on the format of the
    /// returned `String` can be found [here](https://blog.opencagedata.com/post/99059889253/good-looking-addresses-solving-the-berlin-berlin)
    ///
    /// This method passes the `no_annotations` and `no_record` parameters to
    /// the API.
    async fn reverse(&self, point: &Point<T>) -> Result<Option<String>, GeocodingError> {
        let q = format!(
            "{}, {}",
            // OpenCage expects lat, lon order
            (&point.y().to_f64().unwrap().to_string()),
            &point.x().to_f64().unwrap().to_string()
        );
        let mut query = vec![
            ("q", q.as_str()),
            ("key", &self.api_key),
            ("no_annotations", "1"),
            ("no_record", "1"),
        ];
        query.extend(self.parameters.as_query());

        let resp = self
            .client
            .get(&self.endpoint)
            .query(&query)
            .send()
            .await?
            .error_for_status()?;
        if let Some(headers) = resp.headers().get::<_>(XRL) {
            let mut lock = self.remaining.try_lock();
            if let Ok(ref mut mutex) = lock {
                // not ideal, but typed headers are currently impossible in 0.9.x
                let h = headers.to_str()?;
                let h: i32 = h.parse()?;
                **mutex = Some(h)
            }
        }
        let res: OpencageResponse<T> = resp.json().await?;
        // it's OK to index into this vec, because reverse-geocoding only returns a
        // single result
        let address = &res.results[0];
        Ok(Some(address.formatted.to_string()))
    }
}

#[async_trait]
impl<'a, T> Forward<T> for Opencage<'a>
where
    T: Float + DeserializeOwned + Debug,
{
    /// A forward-geocoding lookup of an address. Please see [the documentation](https://opencagedata.com/api#ambiguous-results) for details
    /// of best practices in order to obtain good-quality results.
    ///
    /// This method passes the `no_annotations` and `no_record` parameters to
    /// the API.
    async fn forward(&self, place: &str) -> Result<Vec<Point<T>>, GeocodingError> {
        let mut query = vec![
            ("q", place),
            ("key", &self.api_key),
            ("no_annotations", "1"),
            ("no_record", "1"),
        ];
        query.extend(self.parameters.as_query());

        let resp = self
            .client
            .get(&self.endpoint)
            .query(&query)
            .send()
            .await?
            .error_for_status()?;
        if let Some(headers) = resp.headers().get::<_>(XRL) {
            let mut lock = self.remaining.try_lock();
            if let Ok(ref mut mutex) = lock {
                // not ideal, but typed headers are currently impossible in 0.9.x
                let h = headers.to_str()?;
                let h: i32 = h.parse()?;
                **mutex = Some(h)
            }
        }
        let res: OpencageResponse<T> = resp.json().await?;
        Ok(res
            .results
            .iter()
            .map(|res| Point::new(res.geometry["lng"], res.geometry["lat"]))
            .collect())
    }
}

#[derive(Debug, Serialize, Deserialize)]
pub struct OpencageResponse<T>
where
    T: Float,
{
    pub documentation: String,
    pub licenses: Vec<HashMap<String, String>>,
    pub rate: Option<HashMap<String, i32>>,
    pub results: Vec<Results<T>>,
    pub status: Status,
    pub stay_informed: HashMap<String, String>,
    pub thanks: String,
    pub timestamp: Timestamp,
    pub total_results: i32,
}

/// A forward geocoding result
#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct Results<T>
where
    T: Float,
{
    pub annotations: Option<Annotations<T>>,
    pub bounds: Option<Bounds<T>>,
    pub components: HashMap<String, serde_json::Value>,
    pub confidence: i8,
    pub formatted: String,
    pub geometry: HashMap<String, T>,
}

/// Annotations pertaining to the geocoding result
#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct Annotations<T>
where
    T: Float,
{
    pub dms: Option<HashMap<String, String>>,
    pub mgrs: Option<String>,
    pub maidenhead: Option<String>,
    pub mercator: Option<HashMap<String, T>>,
    pub osm: Option<HashMap<String, String>>,
    pub callingcode: i16,
    pub currency: Option<Currency>,
    pub flag: String,
    pub geohash: String,
    pub qibla: T,
    pub sun: Sun,
    pub timezone: Timezone,
    pub what3words: HashMap<String, String>,
}

/// Currency metadata
#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct Currency {
    pub alternate_symbols: Option<Vec<String>>,
    pub decimal_mark: String,
    pub html_entity: String,
    pub iso_code: String,
    #[serde(deserialize_with = "deserialize_string_or_int")]
    pub iso_numeric: String,
    pub name: String,
    pub smallest_denomination: i16,
    pub subunit: String,
    pub subunit_to_unit: i16,
    pub symbol: String,
    pub symbol_first: i16,
    pub thousands_separator: String,
}

/// Sunrise and sunset metadata
#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct Sun {
    pub rise: HashMap<String, i64>,
    pub set: HashMap<String, i64>,
}

/// Timezone metadata
#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct Timezone {
    pub name: String,
    pub now_in_dst: i16,
    pub offset_sec: i32,
    #[serde(deserialize_with = "deserialize_string_or_int")]
    pub offset_string: String,
    #[serde(deserialize_with = "deserialize_string_or_int")]
    pub short_name: String,
}

/// HTTP status metadata
#[derive(Debug, Serialize, Deserialize)]
pub struct Status {
    pub message: String,
    pub code: i16,
}

/// Timestamp metadata
#[derive(Debug, Serialize, Deserialize)]
pub struct Timestamp {
    pub created_http: String,
    #[serde(deserialize_with = "from_ts")]
    pub created_unix: NaiveDateTime,
}

/// Bounding-box metadata
#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct Bounds<T>
where
    T: Float,
{
    pub northeast: HashMap<String, T>,
    pub southwest: HashMap<String, T>,
}
