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
 * @param address 
 * @param price 
 * @param rooms 
 * @param leaseStart 
 * @param leaseEnd 
 * @param description 
 * @param imgIds 
 * @param residenceType 
 * @param ownerUserId 
 */


data class ListingDetails (

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

    @Json(name = "description")
    val description: kotlin.String,

    @Json(name = "img_ids")
    val imgIds: kotlin.collections.List<kotlin.String>,

    @Json(name = "residence_type")
    val residenceType: ResidenceType,

    @Json(name = "owner_user_id")
    val ownerUserId: kotlin.Int

)

