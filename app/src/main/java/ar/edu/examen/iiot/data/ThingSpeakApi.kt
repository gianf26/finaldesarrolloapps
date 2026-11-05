package ar.edu.examen.iiot.data

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

/** Canal de terceros, de uso público. No representa una instalación industrial real. */
interface ThingSpeakApi {
    @GET("channels/2793812/feeds.json")
    suspend fun getLatest(@Query("results") results: Int = 1): FeedResponse
}

data class FeedResponse(
    @SerializedName("feeds") val feeds: List<FeedDto> = emptyList()
)

data class FeedDto(
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("field1") val temperature: String? = null,
    @SerializedName("field2") val humidity: String? = null,
    @SerializedName("field3") val pressure: String? = null
)
