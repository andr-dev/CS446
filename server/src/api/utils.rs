use data_encoding::HEXLOWER;
use ring::digest::{Context, SHA256};

use crate::error::ServiceError;

pub(super) fn hash_password(password: &String) -> String {
    let mut context = Context::new(&SHA256);

    context.update(password.as_bytes());

    HEXLOWER.encode(context.finish().as_ref())
}

pub fn format_address(
    address_line: String,
    address_city: String,
    address_postalcode: String,
    address_country: String,
) -> String {
    format!(
        "{}, {} {}, {}",
        address_line, address_city, address_postalcode, address_country
    )
}

pub fn format_address_anonymous(
    address_line: String,
    address_city: String,
    address_postalcode: String,
    address_country: String,
) -> String {
    let mut address_line_iter = address_line.split_ascii_whitespace().peekable();

    if let Some(s) = address_line_iter.peek() {
        if s.chars().all(|c| c.is_numeric()) {
            address_line_iter.next();
        }
    }

    format_address(
        address_line_iter.collect::<Vec<_>>().join(" "),
        address_city,
        address_postalcode,
        address_country,
    )
}

/// Modified from (https://github.com/srishanbhattarai/geoutils)
///
/// Compute the Vincenty Inverse formula on two points 'start' and 'end'.
pub fn distance_meters(
    start_longitude: f32,
    start_latitude: f32,
    end_longitude: f32,
    end_latitude: f32,
) -> Result<f32, ServiceError> {
    // WGS-84 geocentric datum parameters
    let a: f32 = 6378137.0; // Semi-major axis
    let b: f32 = 6356752.314245; // Semi-minor axis
    let f: f32 = 1.0 / 298.257223563; // Inverse-flattening

    // Start and end points in Radians
    let p1 = (start_latitude.to_radians(), start_longitude.to_radians());
    let p2 = (end_latitude.to_radians(), end_longitude.to_radians());

    // Difference in longitudes
    let l = p2.1 - p1.1;

    // u = 'reduced latitude'
    let (tan_u1, tan_u2) = ((1.0 - f) * p1.0.tan(), (1.0 - f) * p2.0.tan());
    let (cos_u1, cos_u2) = (
        1.0 / (1.0 + tan_u1 * tan_u1).sqrt(),
        1.0 / (1.0 + tan_u2 * tan_u2).sqrt(),
    );
    let (sin_u1, sin_u2) = (tan_u1 * cos_u1, tan_u2 * cos_u2);

    // First approximation
    let mut lambda = l;
    let mut iter_limit = 100;
    let mut cos_sq_alpha = 0.0;
    let (mut sin_sigma, mut cos_sigma, mut cos2_sigma_m, mut sigma) = (0.0, 0.0, 0.0, 0.0);
    let (mut _sin_lambda, mut _cos_lambda) = (0.0, 0.0);
    loop {
        _sin_lambda = lambda.sin();
        _cos_lambda = lambda.cos();
        let sin_sq_sigma = (cos_u2 * _sin_lambda) * (cos_u2 * _sin_lambda)
            + (cos_u1 * sin_u2 - sin_u1 * cos_u2 * _cos_lambda) * (cos_u1 * sin_u2 - sin_u1 * cos_u2 * _cos_lambda);

        // Points coincide
        if sin_sq_sigma == 0.0 {
            break;
        }

        sin_sigma = sin_sq_sigma.sqrt();
        cos_sigma = sin_u1 * sin_u2 + cos_u1 * cos_u2 * _cos_lambda;
        sigma = sin_sigma.atan2(cos_sigma);
        let sin_alpha = cos_u1 * cos_u2 * _sin_lambda / sin_sigma;
        cos_sq_alpha = 1.0 - sin_alpha * sin_alpha;
        cos2_sigma_m = if cos_sq_alpha != 0.0 {
            cos_sigma - 2.0 * sin_u1 * sin_u2 / cos_sq_alpha
        } else {
            0.0
        };
        let c = f / 16.0 * cos_sq_alpha * (4.0 + f * (4.0 - 3.0 * cos_sq_alpha));
        let lambda_prime = lambda;
        lambda = l
            + (1.0 - c)
                * f
                * sin_alpha
                * (sigma + c * sin_sigma * (cos2_sigma_m + c * cos_sigma * (-1.0 + 2.0 * cos2_sigma_m * cos2_sigma_m)));

        iter_limit -= 1;
        if (lambda - lambda_prime).abs() > 1e-12 && iter_limit > 0 {
            continue;
        }

        break;
    }

    if iter_limit <= 0 {
        return Err(ServiceError::InternalError);
    }

    let u_sq = cos_sq_alpha * (a * a - b * b) / (b * b);
    let cap_a = 1.0 + u_sq / 16384.0 * (4096.0 + u_sq * (-768.0 + u_sq * (320.0 - 175.0 * u_sq)));
    let cap_b = u_sq / 1024.0 * (256.0 + u_sq * (-128.0 + u_sq * (74.0 - 47.0 * u_sq)));

    let delta_sigma = cap_b
        * sin_sigma
        * (cos2_sigma_m
            + cap_b / 4.0
                * (cos_sigma * (-1.0 + 2.0 * cos2_sigma_m * cos2_sigma_m)
                    - cap_b / 6.0
                        * cos2_sigma_m
                        * (-3.0 + 4.0 * sin_sigma * sin_sigma)
                        * (-3.0 + 4.0 * cos2_sigma_m * cos2_sigma_m)));
    let s = b * cap_a * (sigma - delta_sigma);

    Ok((s * 1000.0).round() / 1000.0)
}
