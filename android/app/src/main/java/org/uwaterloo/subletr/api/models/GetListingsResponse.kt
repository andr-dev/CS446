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

import org.uwaterloo.subletr.api.models.ListingSummary

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * 
 *
 * @param listings 
 * @param pages 
 * @param liked 
 */


data class GetListingsResponse (

    @Json(name = "listings")
    val listings: kotlin.collections.List<ListingSummary>,

    @Json(name = "pages")
    val pages: kotlin.Int,

    @Json(name = "liked")
    val liked: kotlin.collections.Set<kotlin.String>

)

