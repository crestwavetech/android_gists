package com.crestwavetech.tokenauthenticator

import com.crestwavetech.tokenauthenticator.NetworkApi.Companion.isGetTokenRequest
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor(private val authRepository: AuthRepository) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        if (authRepository.token != null && !request.isGetTokenRequest()) {
            request = request.newBuilder()
                .addHeader("Authorization", "Bearer ${authRepository.token}").build()
        }
        return chain.proceed(request)
    }
}
