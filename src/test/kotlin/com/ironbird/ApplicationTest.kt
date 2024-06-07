package com.ironbird

import com.ironbird.plugins.configureStaticRouting
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import org.junit.jupiter.api.Disabled
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {

    @Test
    @Disabled
    fun testRoot() = testApplication {
        application {
            configureStaticRouting()
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Hello World!", bodyAsText())
        }
    }
}
