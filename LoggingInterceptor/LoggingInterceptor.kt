package com.crestwavetech.logginginterceptor

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import timber.log.Timber

class LoggingInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val name = chain.request().header("Log-As")
        val request = chain.request().newBuilder().removeHeader("Log-As").build()

        name?.let { log(request.prepareText(it)) }
        val response = chain.proceed(request)
        name?.let { log(response.prepareText(it)) }
        return response
    }

    private fun log(text: String) {
        val logText =
            if (text.length < 600) text
            else "${text.take(380)}\n<...>\n${text.takeLast(200)}"
        Timber.i(logText)
    }

    // workaround for https://github.com/JakeWharton/timber/issues/339
    private fun logFull(text: String) = text.chunked(3000).forEach { Timber.i(it) }

    private fun Request.prepareText(name: String): String {
        val body = body?.let {
            val buffer = Buffer()
            it.writeTo(buffer)
            buffer.readString(Charsets.UTF_8)
        } ?: url.query ?: url.encodedPath
        return "$name request ($method): $body"
    }

    private fun Response.prepareText(name: String): String {
        val body = body?.let {
            val source = it.source()
            source.request(Long.MAX_VALUE) // buffer the entire body
            source.buffer.clone().readString(Charsets.UTF_8)
                .replace(newLine, " ")  // remove pretty-print to make body more compact
        }
        return "$name response ($code): $body"
    }

    companion object {
        private val newLine = Regex("\n *")
    }
}
