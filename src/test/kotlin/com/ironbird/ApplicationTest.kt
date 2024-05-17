package com.ironbird

import com.ironbird.plugins.configureStaticRouting
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
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
