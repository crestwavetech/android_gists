# LoggingInterceptor

[OkHttp](https://github.com/square/okhttp) Interceptor to log network requests/responses, more convenient than a standard `HttpLoggingInterceptor`.

### Features:

* Logging itself & displayed name are configured via headers, making it easy to log only specific requests
* Compactly logs the most useful parts: request/respose body, method (GET/POST), query params, HTTP response code, etc
* Headers are not logged, saving up space (there is almost never anything interesting in them)
* Newlines & indentation ("pretty print") are compactified: we can easily get them back in any JSON formatter, if needed
* For long bodies, only the first and last part are logged; tweak that as you wish or use `logFull()`

### Usage:

With Retrofit:
```
interface NetworkApi {
    @POST("v1/weather/current")
    @Headers("Log-As: getCurrentWeather")
    suspend fun getCurrentWeather(@Body request: WeatherRequest) : WeatherResponse
}

Retrofit.Builder()
    .client(
        OkHttpClient.Builder()
            .addInterceptor(LoggingInterceptor())
            .build()
    )
    ....
```
Resulting logs:
```
getCurrentWeather request (POST): {"lat":41.15,"lon":-8.6}
getCurrentWeather response (200): {"temperature":25.0,"humidity":65,"weatherKind":"SUNNY"}
```
