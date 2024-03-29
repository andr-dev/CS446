/**
 *
 * Please note:
 * This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * Do not edit this file manually.
 *
 */

@file:Suppress(
    "ArrayInDataClass",
    "EnumEntryName",
    "RemoveRedundantQualifierName",
    "UnusedImport"
)

package org.uwaterloo.subletr.api.models

import org.uwaterloo.subletr.api.models.ResidenceType

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * 
 *
 * @param listingId 
 * @param address 
 * @param longitude 
 * @param latitude 
 * @param distanceMeters 
 * @param price 
 * @param roomsAvailable 
 * @param roomsTotal 
 * @param bathroomsAvailable 
 * @param bathroomsEnsuite 
 * @param bathroomsTotal 
 * @param leaseStart 
 * @param leaseEnd 
 * @param imgIds 
 * @param residenceType 
 */


data class ListingSummary (

    @Json(name = "listing_id")
    val listingId: kotlin.Int,

    @Json(name = "address")
    val address: kotlin.String,

    @Json(name = "longitude")
    val longitude: kotlin.Float,

    @Json(name = "latitude")
    val latitude: kotlin.Float,

    @Json(name = "distance_meters")
    val distanceMeters: kotlin.Float,

    @Json(name = "price")
    val price: kotlin.Int,

    @Json(name = "rooms_available")
    val roomsAvailable: kotlin.Int,

    @Json(name = "rooms_total")
    val roomsTotal: kotlin.Int,

    @Json(name = "bathrooms_available")
    val bathroomsAvailable: kotlin.Int,

    @Json(name = "bathrooms_ensuite")
    val bathroomsEnsuite: kotlin.Int,

    @Json(name = "bathrooms_total")
    val bathroomsTotal: kotlin.Int,

    @Json(name = "lease_start")
    val leaseStart: java.time.OffsetDateTime,

    @Json(name = "lease_end")
    val leaseEnd: java.time.OffsetDateTime,

    @Json(name = "img_ids")
    val imgIds: kotlin.collections.List<kotlin.String>,

    @Json(name = "residence_type")
    val residenceType: ResidenceType

)

