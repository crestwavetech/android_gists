# TokenAuthenticator

Complete example of OkHttp Interceptor & Authenticator for working with time-limited access tokens. This allows us to extract all the "add token / refresh / retry" logic, instead of having to deal with it in every request.

There are already some [official](https://square.github.io/okhttp/recipes/#handling-authentication-kt-java) [and](https://blog.coinbase.com/okhttp-oauth-token-refreshes-b598f55dd3b2) [other](https://www.lordcodes.com/articles/authorization-of-web-requests-for-okhttp-and-retrofit) [resources](https://stackoverflow.com/q/22450036) on this topic. They give a good overall perspective, but lack some details required for a robust & efficient implementation, if refreshing the token is done by a network request too.

Our solution includes three main parts:
1. `TokenAuthenticator` gets called when some request returns code 401 ("Unauthorized") and allows to retry it with updated token. In principle, this could be done manually via some `Interceptor`, but using `Authenticator` helps simplify our logic a little.
1. `AuthRepository` contains current token and a method to refresh it.
1. `TokenInterceptor` just adds this token to all requests (except `getToken` itself). Otherwise we would [hit 401 & retry on every request](https://stackoverflow.com/questions/22450036/refreshing-oauth-token-using-retrofit-without-modifying-all-calls#comment53676723_31624433), which is not good.

### Objectives:

* avoid any local token expiration checks: even when technically possible, they're not guaranteed to be consistent with server-side logic
* reuse the same OkHttp, Retrofit & NetworkApi instances to not waste resources (thread pools, etc)
* handle scenarios where `getToken` itself gets code 401 or when retrying original request with a new token still hits 401, preventing infinite loops
* handle multiple parallel requests failing with 401, without excessive `getToken` requests or deadlock caused by thread limits in OkHttp
* handle possible network or parsing exceptions in `getToken`

### Implementation details:

In this specific example we put a "Bearer" token into "Authorization" header, and `getToken` resides in the same REST API as all the other methods, but changing that shouldn't make much difference.

In a real project `token` would typically be persisted to a secure storage, and `apiKey` derived from some user-provided credentials.

`AuthRepository` uses Koin's lazy `inject` method to solve circular dependency issue. The same effect in Dagger/Hilt can be achieved by [injecting `Lazy<NetworkApi>`](https://stackoverflow.com/a/51360214).

### Usage:

With Koin:
```
module {
    singleOf(::TokenAuthenticator)
    singleOf(::TokenInterceptor)
    singleOf(::AuthRepository)

    single<OkHttpClient> {
        OkHttpClient.Builder()
            .authenticator(get<TokenAuthenticator>())
            .addInterceptor(get<TokenInterceptor>())
            .build()
    }

    single<NetworkApi> {
        Retrofit.Builder()
            .client(get())
            ...
            .build()
            .create(NetworkApi::class.java)
    }
}
```

