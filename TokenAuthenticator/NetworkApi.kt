package com.crestwavetech.tokenauthenticator

import com.squareup.moshi.JsonClass
import okhttp3.Request
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface NetworkApi {
    @POST("weather/current")
    suspend fun getCurrentWeather(@Body request: WeatherRequest): WeatherResponse

    @POST(GET_TOKEN_PATH)
    fun getToken(@Body request: TokenRequest): Call<TokenResponse>

    companion object {
        private const val GET_TOKEN_PATH = "auth/get_token"

        fun Request.isGetTokenRequest() = url.toString().endsWith(GET_TOKEN_PATH)
    }
}

@JsonClass(generateAdapter = true)
class TokenRequest(val apiKey: String)

@JsonClass(generateAdapter = true)
class TokenResponse(val token: String)

@JsonClass(generateAdapter = true)
class WeatherRequest(val lat: Float, val lon: Float)

@JsonClass(generateAdapter = true)
class WeatherResponse(val temperature: Float, val humidity: Int, val weatherKind: String)
