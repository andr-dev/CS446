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
 * @param price 
 * @param rooms 
 * @param leaseStart 
 * @param leaseEnd 
 * @param residenceType 
 */


data class ListingSummary (

    @Json(name = "listing_id")
    val listingId: kotlin.Long,

    @Json(name = "address")
    val address: kotlin.String,

    @Json(name = "price")
    val price: kotlin.Int,

    @Json(name = "rooms")
    val rooms: kotlin.Int,

    @Json(name = "lease_start")
    val leaseStart: java.time.OffsetDateTime,

    @Json(name = "lease_end")
    val leaseEnd: java.time.OffsetDateTime,

    @Json(name = "residence_type")
    val residenceType: ResidenceType

)

