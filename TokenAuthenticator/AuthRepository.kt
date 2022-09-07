package com.crestwavetech.tokenauthenticator

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class AuthRepository : KoinComponent {
    // we can't use constructor injection here because it would create a circular dependency
    private val networkApi: NetworkApi by inject()
    private val apiKey: String = "SECRET_API_KEY"
    var token: String? = null
        private set

    // `runCatching` is required, because HttpException or JsonDataException would crash the app,
    // see https://github.com/square/retrofit/issues/3505 & https://github.com/square/okhttp/issues/5151
    // Synchronous `Call.execute` is required to prevent deadlock in case of parallel requests
    // (maxRequestsPerHost = 5 in OkHttp), see https://github.com/square/okhttp/issues/6747
    fun getNewToken(): String? = runCatching {
        val tokenResponse = networkApi.getToken(TokenRequest(apiKey)).execute()
        tokenResponse.body()?.token?.also { token = it }
    }.onFailure {
        Timber.e("getToken failed: $it")
    }.getOrNull()
}
