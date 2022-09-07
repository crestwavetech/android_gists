package com.crestwavetech.tokenauthenticator

import com.crestwavetech.tokenauthenticator.NetworkApi.Companion.isGetTokenRequest
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(private val authRepository: AuthRepository) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        val request = response.request
        if (request.isGetTokenRequest()) {
            return null  // `getNewToken` itself got 401, give up
        }
        if (response.responseCount > 1) {
            return null  // we've got new token & retried, but still get 401, give up
        }
        val requestToken = request.header("Authorization")?.replace("Bearer ", "")

        // synchronizing to prevent multiple `getNewToken` calls when many parallel requests have got 401
        // see https://stackoverflow.com/questions/22450036/refreshing-oauth-token-using-retrofit-without-modifying-all-calls#comment72191030_31624433
        val token: String? = synchronized(this) {
            if (requestToken != authRepository.token) {  // token is already updated by another request
                authRepository.token
            } else {
                authRepository.getNewToken()
                    ?: return null  // `getNewToken` failed, give up
            }
        }

        return request.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()
    }
}

private val Response.responseCount: Int
    get() = generateSequence(this) { it.priorResponse }.count()
