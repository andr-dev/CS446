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

package org.uwaterloo.subletr.api.apis

import java.io.IOException
import okhttp3.OkHttpClient
import okhttp3.HttpUrl

import org.uwaterloo.subletr.api.models.CreateListingRequest
import org.uwaterloo.subletr.api.models.CreateListingResponse
import org.uwaterloo.subletr.api.models.GetListingDetailsResponse
import org.uwaterloo.subletr.api.models.GetListingsResponse
import org.uwaterloo.subletr.api.models.ListingsImagesCreateRequest
import org.uwaterloo.subletr.api.models.ListingsImagesCreateResponse

import com.squareup.moshi.Json

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.uwaterloo.subletr.api.infrastructure.ApiClient
import org.uwaterloo.subletr.api.infrastructure.ApiResponse
import org.uwaterloo.subletr.api.infrastructure.ClientException
import org.uwaterloo.subletr.api.infrastructure.ClientError
import org.uwaterloo.subletr.api.infrastructure.ServerException
import org.uwaterloo.subletr.api.infrastructure.ServerError
import org.uwaterloo.subletr.api.infrastructure.MultiValueMap
import org.uwaterloo.subletr.api.infrastructure.PartConfig
import org.uwaterloo.subletr.api.infrastructure.RequestConfig
import org.uwaterloo.subletr.api.infrastructure.RequestMethod
import org.uwaterloo.subletr.api.infrastructure.ResponseType
import org.uwaterloo.subletr.api.infrastructure.Success
import org.uwaterloo.subletr.api.infrastructure.toMultiValue

class ListingsApi(basePath: kotlin.String = defaultBasePath, client: OkHttpClient = ApiClient.defaultClient) : ApiClient(basePath, client) {
    companion object {
        @JvmStatic
        val defaultBasePath: String by lazy {
            System.getProperties().getProperty(ApiClient.baseUrlKey, "/api")
        }
    }

    /**
     * 
     * 
     * @param createListingRequest 
     * @return CreateListingResponse
     * @throws IllegalStateException If the request is not correctly configured
     * @throws IOException Rethrows the OkHttp execute method exception
     * @throws UnsupportedOperationException If the API returns an informational or redirection response
     * @throws ClientException If the API returns a client error response
     * @throws ServerException If the API returns a server error response
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(IllegalStateException::class, IOException::class, UnsupportedOperationException::class, ClientException::class, ServerException::class)
    suspend fun listingsCreate(createListingRequest: CreateListingRequest) : CreateListingResponse = withContext(Dispatchers.IO) {
        val localVarResponse = listingsCreateWithHttpInfo(createListingRequest = createListingRequest)

        return@withContext when (localVarResponse.responseType) {
            ResponseType.Success -> (localVarResponse as Success<*>).data as CreateListingResponse
            ResponseType.Informational -> throw UnsupportedOperationException("Client does not support Informational responses.")
            ResponseType.Redirection -> throw UnsupportedOperationException("Client does not support Redirection responses.")
            ResponseType.ClientError -> {
                val localVarError = localVarResponse as ClientError<*>
                throw ClientException("Client error : ${localVarError.statusCode} ${localVarError.message.orEmpty()}", localVarError.statusCode, localVarResponse)
            }
            ResponseType.ServerError -> {
                val localVarError = localVarResponse as ServerError<*>
                throw ServerException("Server error : ${localVarError.statusCode} ${localVarError.message.orEmpty()}", localVarError.statusCode, localVarResponse)
            }
        }
    }

    /**
     * 
     * 
     * @param createListingRequest 
     * @return ApiResponse<CreateListingResponse?>
     * @throws IllegalStateException If the request is not correctly configured
     * @throws IOException Rethrows the OkHttp execute method exception
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(IllegalStateException::class, IOException::class)
    suspend fun listingsCreateWithHttpInfo(createListingRequest: CreateListingRequest) : ApiResponse<CreateListingResponse?> = withContext(Dispatchers.IO) {
        val localVariableConfig = listingsCreateRequestConfig(createListingRequest = createListingRequest)

        return@withContext request<CreateListingRequest, CreateListingResponse>(
            localVariableConfig
        )
    }

    /**
     * To obtain the request config of the operation listingsCreate
     *
     * @param createListingRequest 
     * @return RequestConfig
     */
    fun listingsCreateRequestConfig(createListingRequest: CreateListingRequest) : RequestConfig<CreateListingRequest> {
        val localVariableBody = createListingRequest
        val localVariableQuery: MultiValueMap = mutableMapOf()
        val localVariableHeaders: MutableMap<String, String> = mutableMapOf()
        localVariableHeaders["Content-Type"] = "application/json"
        localVariableHeaders["Accept"] = "application/json"

        return RequestConfig(
            method = RequestMethod.POST,
            path = "/listings/create",
            query = localVariableQuery,
            headers = localVariableHeaders,
            requiresAuthentication = true,
            body = localVariableBody
        )
    }

    /**
     * 
     * 
     * @param listingId 
     * @param longitude 
     * @param latitude 
     * @return GetListingDetailsResponse
     * @throws IllegalStateException If the request is not correctly configured
     * @throws IOException Rethrows the OkHttp execute method exception
     * @throws UnsupportedOperationException If the API returns an informational or redirection response
     * @throws ClientException If the API returns a client error response
     * @throws ServerException If the API returns a server error response
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(IllegalStateException::class, IOException::class, UnsupportedOperationException::class, ClientException::class, ServerException::class)
    suspend fun listingsDetails(listingId: kotlin.Int, longitude: kotlin.Float, latitude: kotlin.Float) : GetListingDetailsResponse = withContext(Dispatchers.IO) {
        val localVarResponse = listingsDetailsWithHttpInfo(listingId = listingId, longitude = longitude, latitude = latitude)

        return@withContext when (localVarResponse.responseType) {
            ResponseType.Success -> (localVarResponse as Success<*>).data as GetListingDetailsResponse
            ResponseType.Informational -> throw UnsupportedOperationException("Client does not support Informational responses.")
            ResponseType.Redirection -> throw UnsupportedOperationException("Client does not support Redirection responses.")
            ResponseType.ClientError -> {
                val localVarError = localVarResponse as ClientError<*>
                throw ClientException("Client error : ${localVarError.statusCode} ${localVarError.message.orEmpty()}", localVarError.statusCode, localVarResponse)
            }
            ResponseType.ServerError -> {
                val localVarError = localVarResponse as ServerError<*>
                throw ServerException("Server error : ${localVarError.statusCode} ${localVarError.message.orEmpty()}", localVarError.statusCode, localVarResponse)
            }
        }
    }

    /**
     * 
     * 
     * @param listingId 
     * @param longitude 
     * @param latitude 
     * @return ApiResponse<GetListingDetailsResponse?>
     * @throws IllegalStateException If the request is not correctly configured
     * @throws IOException Rethrows the OkHttp execute method exception
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(IllegalStateException::class, IOException::class)
    suspend fun listingsDetailsWithHttpInfo(listingId: kotlin.Int, longitude: kotlin.Float, latitude: kotlin.Float) : ApiResponse<GetListingDetailsResponse?> = withContext(Dispatchers.IO) {
        val localVariableConfig = listingsDetailsRequestConfig(listingId = listingId, longitude = longitude, latitude = latitude)

        return@withContext request<Unit, GetListingDetailsResponse>(
            localVariableConfig
        )
    }

    /**
     * To obtain the request config of the operation listingsDetails
     *
     * @param listingId 
     * @param longitude 
     * @param latitude 
     * @return RequestConfig
     */
    fun listingsDetailsRequestConfig(listingId: kotlin.Int, longitude: kotlin.Float, latitude: kotlin.Float) : RequestConfig<Unit> {
        val localVariableBody = null
        val localVariableQuery: MultiValueMap = mutableMapOf<kotlin.String, kotlin.collections.List<kotlin.String>>()
            .apply {
                put("listing_id", listOf(listingId.toString()))
                put("longitude", listOf(longitude.toString()))
                put("latitude", listOf(latitude.toString()))
            }
        val localVariableHeaders: MutableMap<String, String> = mutableMapOf()
        localVariableHeaders["Accept"] = "application/json"

        return RequestConfig(
            method = RequestMethod.GET,
            path = "/listings/details",
            query = localVariableQuery,
            headers = localVariableHeaders,
            requiresAuthentication = false,
            body = localVariableBody
        )
    }

    /**
     * 
     * 
     * @param listingsImagesCreateRequest 
     * @return ListingsImagesCreateResponse
     * @throws IllegalStateException If the request is not correctly configured
     * @throws IOException Rethrows the OkHttp execute method exception
     * @throws UnsupportedOperationException If the API returns an informational or redirection response
     * @throws ClientException If the API returns a client error response
     * @throws ServerException If the API returns a server error response
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(IllegalStateException::class, IOException::class, UnsupportedOperationException::class, ClientException::class, ServerException::class)
    suspend fun listingsImagesCreate(listingsImagesCreateRequest: ListingsImagesCreateRequest) : ListingsImagesCreateResponse = withContext(Dispatchers.IO) {
        val localVarResponse = listingsImagesCreateWithHttpInfo(listingsImagesCreateRequest = listingsImagesCreateRequest)

        return@withContext when (localVarResponse.responseType) {
            ResponseType.Success -> (localVarResponse as Success<*>).data as ListingsImagesCreateResponse
            ResponseType.Informational -> throw UnsupportedOperationException("Client does not support Informational responses.")
            ResponseType.Redirection -> throw UnsupportedOperationException("Client does not support Redirection responses.")
            ResponseType.ClientError -> {
                val localVarError = localVarResponse as ClientError<*>
                throw ClientException("Client error : ${localVarError.statusCode} ${localVarError.message.orEmpty()}", localVarError.statusCode, localVarResponse)
            }
            ResponseType.ServerError -> {
                val localVarError = localVarResponse as ServerError<*>
                throw ServerException("Server error : ${localVarError.statusCode} ${localVarError.message.orEmpty()}", localVarError.statusCode, localVarResponse)
            }
        }
    }

    /**
     * 
     * 
     * @param listingsImagesCreateRequest 
     * @return ApiResponse<ListingsImagesCreateResponse?>
     * @throws IllegalStateException If the request is not correctly configured
     * @throws IOException Rethrows the OkHttp execute method exception
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(IllegalStateException::class, IOException::class)
    suspend fun listingsImagesCreateWithHttpInfo(listingsImagesCreateRequest: ListingsImagesCreateRequest) : ApiResponse<ListingsImagesCreateResponse?> = withContext(Dispatchers.IO) {
        val localVariableConfig = listingsImagesCreateRequestConfig(listingsImagesCreateRequest = listingsImagesCreateRequest)

        return@withContext request<ListingsImagesCreateRequest, ListingsImagesCreateResponse>(
            localVariableConfig
        )
    }

    /**
     * To obtain the request config of the operation listingsImagesCreate
     *
     * @param listingsImagesCreateRequest 
     * @return RequestConfig
     */
    fun listingsImagesCreateRequestConfig(listingsImagesCreateRequest: ListingsImagesCreateRequest) : RequestConfig<ListingsImagesCreateRequest> {
        val localVariableBody = listingsImagesCreateRequest
        val localVariableQuery: MultiValueMap = mutableMapOf()
        val localVariableHeaders: MutableMap<String, String> = mutableMapOf()
        localVariableHeaders["Content-Type"] = "application/json"
        localVariableHeaders["Accept"] = "application/json"

        return RequestConfig(
            method = RequestMethod.POST,
            path = "/listings/images/create",
            query = localVariableQuery,
            headers = localVariableHeaders,
            requiresAuthentication = true,
            body = localVariableBody
        )
    }

    /**
     * 
     * 
     * @param imageId 
     * @return kotlin.String
     * @throws IllegalStateException If the request is not correctly configured
     * @throws IOException Rethrows the OkHttp execute method exception
     * @throws UnsupportedOperationException If the API returns an informational or redirection response
     * @throws ClientException If the API returns a client error response
     * @throws ServerException If the API returns a server error response
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(IllegalStateException::class, IOException::class, UnsupportedOperationException::class, ClientException::class, ServerException::class)
    suspend fun listingsImagesGet(imageId: kotlin.String) : kotlin.String = withContext(Dispatchers.IO) {
        val localVarResponse = listingsImagesGetWithHttpInfo(imageId = imageId)

        return@withContext when (localVarResponse.responseType) {
            ResponseType.Success -> (localVarResponse as Success<*>).data as kotlin.String
            ResponseType.Informational -> throw UnsupportedOperationException("Client does not support Informational responses.")
            ResponseType.Redirection -> throw UnsupportedOperationException("Client does not support Redirection responses.")
            ResponseType.ClientError -> {
                val localVarError = localVarResponse as ClientError<*>
                throw ClientException("Client error : ${localVarError.statusCode} ${localVarError.message.orEmpty()}", localVarError.statusCode, localVarResponse)
            }
            ResponseType.ServerError -> {
                val localVarError = localVarResponse as ServerError<*>
                throw ServerException("Server error : ${localVarError.statusCode} ${localVarError.message.orEmpty()}", localVarError.statusCode, localVarResponse)
            }
        }
    }

    /**
     * 
     * 
     * @param imageId 
     * @return ApiResponse<kotlin.String?>
     * @throws IllegalStateException If the request is not correctly configured
     * @throws IOException Rethrows the OkHttp execute method exception
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(IllegalStateException::class, IOException::class)
    suspend fun listingsImagesGetWithHttpInfo(imageId: kotlin.String) : ApiResponse<kotlin.String?> = withContext(Dispatchers.IO) {
        val localVariableConfig = listingsImagesGetRequestConfig(imageId = imageId)

        return@withContext request<Unit, kotlin.String>(
            localVariableConfig
        )
    }

    /**
     * To obtain the request config of the operation listingsImagesGet
     *
     * @param imageId 
     * @return RequestConfig
     */
    fun listingsImagesGetRequestConfig(imageId: kotlin.String) : RequestConfig<Unit> {
        val localVariableBody = null
        val localVariableQuery: MultiValueMap = mutableMapOf()
        val localVariableHeaders: MutableMap<String, String> = mutableMapOf()
        localVariableHeaders["Accept"] = "application/json"

        return RequestConfig(
            method = RequestMethod.GET,
            path = "/listings/images/{image_id}".replace("{"+"image_id"+"}", encodeURIComponent(imageId.toString())),
            query = localVariableQuery,
            headers = localVariableHeaders,
            requiresAuthentication = true,
            body = localVariableBody
        )
    }

    /**
     * 
     * 
     * @param longitude 
     * @param latitude 
     * @param pageNumber 
     * @param pageSize 
     * @param distanceMetersMin  (optional)
     * @param distanceMetersMax  (optional)
     * @param priceMin  (optional)
     * @param priceMax  (optional)
     * @param roomsAvailableMin  (optional)
     * @param roomsAvailableMax  (optional)
     * @param roomsTotalMin  (optional)
     * @param roomsTotalMax  (optional)
     * @param bathroomsAvailableMin  (optional)
     * @param bathroomsAvailableMax  (optional)
     * @param bathroomsTotalMin  (optional)
     * @param bathroomsTotalMax  (optional)
     * @param bathroomsEnsuiteMin  (optional)
     * @param bathroomsEnsuiteMax  (optional)
     * @param gender  (optional)
     * @return GetListingsResponse
     * @throws IllegalStateException If the request is not correctly configured
     * @throws IOException Rethrows the OkHttp execute method exception
     * @throws UnsupportedOperationException If the API returns an informational or redirection response
     * @throws ClientException If the API returns a client error response
     * @throws ServerException If the API returns a server error response
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(IllegalStateException::class, IOException::class, UnsupportedOperationException::class, ClientException::class, ServerException::class)
    suspend fun listingsList(longitude: kotlin.Float, latitude: kotlin.Float, pageNumber: kotlin.Int, pageSize: kotlin.Int, distanceMetersMin: kotlin.Float? = null, distanceMetersMax: kotlin.Float? = null, priceMin: kotlin.Int? = null, priceMax: kotlin.Int? = null, roomsAvailableMin: kotlin.Int? = null, roomsAvailableMax: kotlin.Int? = null, roomsTotalMin: kotlin.Int? = null, roomsTotalMax: kotlin.Int? = null, bathroomsAvailableMin: kotlin.Int? = null, bathroomsAvailableMax: kotlin.Int? = null, bathroomsTotalMin: kotlin.Int? = null, bathroomsTotalMax: kotlin.Int? = null, bathroomsEnsuiteMin: kotlin.Int? = null, bathroomsEnsuiteMax: kotlin.Int? = null, gender: kotlin.String? = null) : GetListingsResponse = withContext(Dispatchers.IO) {
        val localVarResponse = listingsListWithHttpInfo(longitude = longitude, latitude = latitude, pageNumber = pageNumber, pageSize = pageSize, distanceMetersMin = distanceMetersMin, distanceMetersMax = distanceMetersMax, priceMin = priceMin, priceMax = priceMax, roomsAvailableMin = roomsAvailableMin, roomsAvailableMax = roomsAvailableMax, roomsTotalMin = roomsTotalMin, roomsTotalMax = roomsTotalMax, bathroomsAvailableMin = bathroomsAvailableMin, bathroomsAvailableMax = bathroomsAvailableMax, bathroomsTotalMin = bathroomsTotalMin, bathroomsTotalMax = bathroomsTotalMax, bathroomsEnsuiteMin = bathroomsEnsuiteMin, bathroomsEnsuiteMax = bathroomsEnsuiteMax, gender = gender)

        return@withContext when (localVarResponse.responseType) {
            ResponseType.Success -> (localVarResponse as Success<*>).data as GetListingsResponse
            ResponseType.Informational -> throw UnsupportedOperationException("Client does not support Informational responses.")
            ResponseType.Redirection -> throw UnsupportedOperationException("Client does not support Redirection responses.")
            ResponseType.ClientError -> {
                val localVarError = localVarResponse as ClientError<*>
                throw ClientException("Client error : ${localVarError.statusCode} ${localVarError.message.orEmpty()}", localVarError.statusCode, localVarResponse)
            }
            ResponseType.ServerError -> {
                val localVarError = localVarResponse as ServerError<*>
                throw ServerException("Server error : ${localVarError.statusCode} ${localVarError.message.orEmpty()}", localVarError.statusCode, localVarResponse)
            }
        }
    }

    /**
     * 
     * 
     * @param longitude 
     * @param latitude 
     * @param pageNumber 
     * @param pageSize 
     * @param distanceMetersMin  (optional)
     * @param distanceMetersMax  (optional)
     * @param priceMin  (optional)
     * @param priceMax  (optional)
     * @param roomsAvailableMin  (optional)
     * @param roomsAvailableMax  (optional)
     * @param roomsTotalMin  (optional)
     * @param roomsTotalMax  (optional)
     * @param bathroomsAvailableMin  (optional)
     * @param bathroomsAvailableMax  (optional)
     * @param bathroomsTotalMin  (optional)
     * @param bathroomsTotalMax  (optional)
     * @param bathroomsEnsuiteMin  (optional)
     * @param bathroomsEnsuiteMax  (optional)
     * @param gender  (optional)
     * @return ApiResponse<GetListingsResponse?>
     * @throws IllegalStateException If the request is not correctly configured
     * @throws IOException Rethrows the OkHttp execute method exception
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(IllegalStateException::class, IOException::class)
    suspend fun listingsListWithHttpInfo(longitude: kotlin.Float, latitude: kotlin.Float, pageNumber: kotlin.Int, pageSize: kotlin.Int, distanceMetersMin: kotlin.Float?, distanceMetersMax: kotlin.Float?, priceMin: kotlin.Int?, priceMax: kotlin.Int?, roomsAvailableMin: kotlin.Int?, roomsAvailableMax: kotlin.Int?, roomsTotalMin: kotlin.Int?, roomsTotalMax: kotlin.Int?, bathroomsAvailableMin: kotlin.Int?, bathroomsAvailableMax: kotlin.Int?, bathroomsTotalMin: kotlin.Int?, bathroomsTotalMax: kotlin.Int?, bathroomsEnsuiteMin: kotlin.Int?, bathroomsEnsuiteMax: kotlin.Int?, gender: kotlin.String?) : ApiResponse<GetListingsResponse?> = withContext(Dispatchers.IO) {
        val localVariableConfig = listingsListRequestConfig(longitude = longitude, latitude = latitude, pageNumber = pageNumber, pageSize = pageSize, distanceMetersMin = distanceMetersMin, distanceMetersMax = distanceMetersMax, priceMin = priceMin, priceMax = priceMax, roomsAvailableMin = roomsAvailableMin, roomsAvailableMax = roomsAvailableMax, roomsTotalMin = roomsTotalMin, roomsTotalMax = roomsTotalMax, bathroomsAvailableMin = bathroomsAvailableMin, bathroomsAvailableMax = bathroomsAvailableMax, bathroomsTotalMin = bathroomsTotalMin, bathroomsTotalMax = bathroomsTotalMax, bathroomsEnsuiteMin = bathroomsEnsuiteMin, bathroomsEnsuiteMax = bathroomsEnsuiteMax, gender = gender)

        return@withContext request<Unit, GetListingsResponse>(
            localVariableConfig
        )
    }

    /**
     * To obtain the request config of the operation listingsList
     *
     * @param longitude 
     * @param latitude 
     * @param pageNumber 
     * @param pageSize 
     * @param distanceMetersMin  (optional)
     * @param distanceMetersMax  (optional)
     * @param priceMin  (optional)
     * @param priceMax  (optional)
     * @param roomsAvailableMin  (optional)
     * @param roomsAvailableMax  (optional)
     * @param roomsTotalMin  (optional)
     * @param roomsTotalMax  (optional)
     * @param bathroomsAvailableMin  (optional)
     * @param bathroomsAvailableMax  (optional)
     * @param bathroomsTotalMin  (optional)
     * @param bathroomsTotalMax  (optional)
     * @param bathroomsEnsuiteMin  (optional)
     * @param bathroomsEnsuiteMax  (optional)
     * @param gender  (optional)
     * @return RequestConfig
     */
    fun listingsListRequestConfig(longitude: kotlin.Float, latitude: kotlin.Float, pageNumber: kotlin.Int, pageSize: kotlin.Int, distanceMetersMin: kotlin.Float?, distanceMetersMax: kotlin.Float?, priceMin: kotlin.Int?, priceMax: kotlin.Int?, roomsAvailableMin: kotlin.Int?, roomsAvailableMax: kotlin.Int?, roomsTotalMin: kotlin.Int?, roomsTotalMax: kotlin.Int?, bathroomsAvailableMin: kotlin.Int?, bathroomsAvailableMax: kotlin.Int?, bathroomsTotalMin: kotlin.Int?, bathroomsTotalMax: kotlin.Int?, bathroomsEnsuiteMin: kotlin.Int?, bathroomsEnsuiteMax: kotlin.Int?, gender: kotlin.String?) : RequestConfig<Unit> {
        val localVariableBody = null
        val localVariableQuery: MultiValueMap = mutableMapOf<kotlin.String, kotlin.collections.List<kotlin.String>>()
            .apply {
                put("longitude", listOf(longitude.toString()))
                put("latitude", listOf(latitude.toString()))
                if (distanceMetersMin != null) {
                    put("distance_meters_min", listOf(distanceMetersMin.toString()))
                }
                if (distanceMetersMax != null) {
                    put("distance_meters_max", listOf(distanceMetersMax.toString()))
                }
                if (priceMin != null) {
                    put("price_min", listOf(priceMin.toString()))
                }
                if (priceMax != null) {
                    put("price_max", listOf(priceMax.toString()))
                }
                if (roomsAvailableMin != null) {
                    put("rooms_available_min", listOf(roomsAvailableMin.toString()))
                }
                if (roomsAvailableMax != null) {
                    put("rooms_available_max", listOf(roomsAvailableMax.toString()))
                }
                if (roomsTotalMin != null) {
                    put("rooms_total_min", listOf(roomsTotalMin.toString()))
                }
                if (roomsTotalMax != null) {
                    put("rooms_total_max", listOf(roomsTotalMax.toString()))
                }
                if (bathroomsAvailableMin != null) {
                    put("bathrooms_available_min", listOf(bathroomsAvailableMin.toString()))
                }
                if (bathroomsAvailableMax != null) {
                    put("bathrooms_available_max", listOf(bathroomsAvailableMax.toString()))
                }
                if (bathroomsTotalMin != null) {
                    put("bathrooms_total_min", listOf(bathroomsTotalMin.toString()))
                }
                if (bathroomsTotalMax != null) {
                    put("bathrooms_total_max", listOf(bathroomsTotalMax.toString()))
                }
                if (bathroomsEnsuiteMin != null) {
                    put("bathrooms_ensuite_min", listOf(bathroomsEnsuiteMin.toString()))
                }
                if (bathroomsEnsuiteMax != null) {
                    put("bathrooms_ensuite_max", listOf(bathroomsEnsuiteMax.toString()))
                }
                if (gender != null) {
                    put("gender", listOf(gender.toString()))
                }
                put("page_number", listOf(pageNumber.toString()))
                put("page_size", listOf(pageSize.toString()))
            }
        val localVariableHeaders: MutableMap<String, String> = mutableMapOf()
        localVariableHeaders["Accept"] = "application/json"

        return RequestConfig(
            method = RequestMethod.GET,
            path = "/listings/list",
            query = localVariableQuery,
            headers = localVariableHeaders,
            requiresAuthentication = false,
            body = localVariableBody
        )
    }


    private fun encodeURIComponent(uriComponent: kotlin.String): kotlin.String =
        HttpUrl.Builder().scheme("http").host("localhost").addPathSegment(uriComponent).build().encodedPathSegments[0]
}
