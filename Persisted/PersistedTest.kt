package com.crestwavetech.persisted

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.*
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

@RunWith(AndroidJUnit4::class)
class PersistedTest {

    private lateinit var appContext: Context
    private val moshi: Moshi = Moshi.Builder().build()

    @Before
    fun setUp() {
        appContext = InstrumentationRegistry.getInstrumentation().targetContext
    }

    @Test
    fun listOfStrings() = runBlocking {
        val filename = "test_strings.json"
        File(appContext.filesDir, filename).delete()  // cleanup
        var persistedStrings: List<String>? by appContext.persisted(null, filename, moshi)
        persistedStrings shouldEqual null

        listOf(
            listOf("abcdef"),
            emptyList(),
            null,
            listOf("abc", "def"),
        ).forEach { strings ->
            persistedStrings = strings
            persistedStrings shouldEqual strings  // test caching
        }

        val finalValue = listOf("final value")
        persistedStrings = finalValue
        delay(100)  // wait for writing to complete

        val strings2: List<String>? by appContext.persisted(null, filename, moshi)
        strings2 shouldEqual finalValue  // ensure value is really written to disk
    }

    @Test
    fun nonNullListOfStrings() = runBlocking {
        val filename = "test_strings.json"
        File(appContext.filesDir, filename).delete()  // cleanup
        var persistedStrings: List<String> by appContext.persisted(emptyList(), filename, moshi)
        persistedStrings shouldEqual emptyList()

        listOf(
            listOf("abcdef"),
            emptyList(),
            listOf("abc", "def"),
        ).forEach { strings ->
            persistedStrings = strings
            persistedStrings shouldEqual strings  // test caching
        }

        val finalValue = listOf("final value")
        persistedStrings = finalValue
        delay(100)  // wait for writing to complete

        val strings2: List<String> by appContext.persisted(emptyList(), filename, moshi)
        strings2 shouldEqual finalValue  // ensure value is really written to disk
    }

    // To check file writes themselves are ordered correctly it needs additional assertions inside
    // `setValue()`, e.g. based on list size (beware of https://youtrack.jetbrains.com/issue/KT-52730):
    //    private var lastSize = AtomicInteger(-1)
    //    ...
    //    scope.launch {
    //    val size = (value as? List<*>)?.size ?: -1
    //    if (!lastSize.compareAndSet(size - 1, size))
    //        throw IllegalStateException("wrong size $size")
    @Ignore("requires modifying SUT")
    @Test
    fun sequentialWrite() = runBlocking {
        val filename = "test_strings.json"
        File(appContext.filesDir, filename).delete()  // cleanup
        var persistedStrings: List<String>? by appContext.persisted(null, filename, moshi)
        var strings = emptyList<String>()
        repeat(1000) {
            persistedStrings = strings
            persistedStrings shouldEqual strings
            strings = strings + UUID.randomUUID().toString()
        }
        delay(1.seconds)
    }

    @Test
    fun complexMap() = runBlocking {
        val filename = "test_map.json"
        var persistedMap: Map<String, List<Long>>? by appContext.persisted(null, filename, moshi)
        val map = mapOf(
            "abc" to List(Random.nextInt(30)) { it.toLong() },
            "def" to listOf(Long.MAX_VALUE),
        )
        persistedMap = map
        persistedMap shouldEqual map  // test caching
        delay(100)  // wait for writing to complete

        val persistedMap2: Map<String, List<Long>>? by appContext.persisted(null, filename, moshi)
        persistedMap2 shouldEqual map  // ensure value is really written to disk
    }

    @Test
    fun customClass() = runBlocking {
        val filename = "test_program.json"
        File(appContext.filesDir, filename).delete()  // cleanup
        var persistedProgram: Program? by appContext.persisted(null, filename, moshi)
        persistedProgram shouldEqual null
        persistedProgram = defaultProgram
        persistedProgram shouldEqual defaultProgram  // test caching
        persistedProgram = persistedProgram?.copy(id = 42)
        persistedProgram?.id shouldEqual 42
        delay(100)  // wait for writing to complete

        val persistedProgram2: Program? by appContext.persisted(null, filename, moshi)
        persistedProgram2?.id shouldEqual 42  // ensure value is really written to disk
    }

    @Test
    fun nonNullCustomClass() = runBlocking {
        val filename = "test_program.json"
        File(appContext.filesDir, filename).delete()  // cleanup
        val persistedProgram: Program by appContext.persisted(defaultProgram, filename, moshi)
        persistedProgram shouldEqual defaultProgram  // test non-null default value
    }

    // from Kluent library
    private infix fun <T> T.shouldEqual(expected: T?) = assertEquals(expected, this)
}

@JsonClass(generateAdapter = true)
data class Program(val id: Long, val name: String)

private val defaultProgram = Program(-1, "default")
