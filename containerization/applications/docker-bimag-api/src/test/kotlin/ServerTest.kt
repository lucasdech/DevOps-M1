package com.example

import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlin.test.*

class ServerTest {

    @Test
    fun `test root endpoint`() = testApplication {
        configure()
        assertEquals(HttpStatusCode.OK, client.get("/").status)
    }

    @Test
    fun `rotate refuses an invalid angle`() = testApplication {
        configure()
        assertEquals(
            HttpStatusCode.BadRequest,
            client.post("/api/image/1/rotate?angle=45").status
        )
    }

    @Test
    fun `convert refuses an invalid format`() = testApplication {
        configure()
        assertEquals(
            HttpStatusCode.BadRequest,
            client.post("/api/image/1/convert?format=exe").status
        )
    }

    @Test
    fun `thumbnail refuses an invalid size`() = testApplication {
        configure()
        assertEquals(
            HttpStatusCode.BadRequest,
            client.post("/api/image/1/thumbnail?size=9999").status
        )
    }

    @Test
    fun `resize refuses a request without dimensions`() = testApplication {
        configure()
        assertEquals(
            HttpStatusCode.BadRequest,
            client.post("/api/image/1/resize").status
        )
    }

    @Test
    fun `resize refuses to distort with a single dimension`() = testApplication {
        configure()
        assertEquals(
            HttpStatusCode.BadRequest,
            client.post("/api/image/1/resize?width=100&keepRatio=false").status
        )
    }

}
