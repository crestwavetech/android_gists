# Android Gists

Pieces of code which proved to be useful in our Android development practice. They're too small to deserve publishing as libraries (and libraries might need more customization options, which complicates things), but valuable enough to be reused across our projects.

[GetPostServer](GetPostServer/) - Simple file-base server on Python to provide mock network responses for GET and POST requests

[Persisted](Persisted/) - Kotlin delegate for persisting relatively complex data types (`List`, `Map`, custom classes...), which can be serialized to JSON

[LoggingInterceptor](LoggingInterceptor/) - OkHttp Interceptor to log network requests/responses, more convenient (for us) than a standard `HttpLoggingInterceptor`

[TokenAuthenticator](TokenAuthenticator/) - Example of OkHttp Interceptor & Authenticator for working with time-limited access tokens

We share them as a repository because organizations on GitHub [still can't publish Gists](https://webapps.stackexchange.com/q/11011).

### License

Licensed under the [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0)

	Copyright (C) 2022 CrestWave Technologies

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	    http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.

