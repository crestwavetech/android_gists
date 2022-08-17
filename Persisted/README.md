# Persisted

Kotlin delegate for persisting relatively complex data types (`List`, `Map`, custom classes...), which are not supported directly by `SharedPreferences`, but can be serialized to JSON. Complements very convenient Kotlin delegates for `SharedPreferences` (see [here](https://proandroiddev.com/kotlin-delegates-in-android-1ab0a715762d#3381) or [here](https://hackernoon.com/kotlin-delegates-in-android-development-part-1-50346cf4aed7)).

Conceptually similar to [PerSista](https://github.com/erdo/persista), but with some differences:
* Android-only
* uses Moshi instead of kotlinx.serialization
* filename is passed explicitly to avoid "one instance per class" limitation
* wrapped in a Kotlin delegate (`ReadWriteProperty`) for easier usage
* synchronous-only API (because of `ReadWriteProperty`)
* presumably slower on startup because of eager initialization, but faster on subsequent access thanks to caching

Value is initialized eagerly on construction, because initialization on first read would require non-trivial synchronization in `getValue` & `setValue` to ensure thread safety.

Writing to disk happens sequentially on a single-threaded dispatcher to ensure correctness. In case of very frequent updates performance can be improved by adding `yield` & job cancellation in `setValue`.

Uses [Timber](https://github.com/JakeWharton/timber) for logging, tweak this part as you wish.

### Usage:
```
var strings: List<String> by context.persisted(emptyList(), "strings.json", moshi)
var program: Program? by context.persisted(null, "program.json", moshi)
```
