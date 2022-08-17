package com.crestwavetech.persisted

import android.content.Context
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Kotlin delegate for persisting relatively complex data types (List, Map, custom classes...),
 * which are not supported directly by SharedPreferences, but can be serialized to JSON.
 */
@OptIn(ExperimentalStdlibApi::class, ExperimentalCoroutinesApi::class)
inline fun <reified T> Context.persisted(defaultValue: T, filename: String, moshi: Moshi) =
    object : ReadWriteProperty<Any?, T> {
        private val file = File(filesDir, filename)
        private val adapter: JsonAdapter<T> = moshi.adapter()
        private val scope = CoroutineScope(Dispatchers.IO.limitedParallelism(1))
        @Volatile private var cachedValue: T = getInitialValue()

        private fun getInitialValue(): T = try {
            val json = file.readText()
            Timber.i("reading '$filename': $json")
            adapter.fromJson(json) ?: defaultValue
        } catch (e: Exception) {
            Timber.e("reading '$filename' failed: $e")
            defaultValue
        }

        override fun getValue(thisRef: Any?, property: KProperty<*>): T = cachedValue

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            cachedValue = value
            scope.launch {
                try {
                    val json = adapter.toJson(value)
                    Timber.i("persisting '$filename': $json")
                    file.writeText(json)
                } catch (e: Exception) {
                    Timber.e("persisting '$filename' failed: $e")
                }
            }
        }
    }
