package com.example.api

import com.example.service.ImageService
import io.ktor.http.*
import io.ktor.openapi.*
import io.ktor.server.application.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.openapi.*

private fun ApplicationCall.imageId(): UInt? =
    parameters["id"]?.toUIntOrNull()

fun Application.configureRouting(imageService: ImageService) {
    routing {
        get("/") {
            call.respondText("Welcome to the bimag api \uD83D\uDE0E")
        }

        get("/api/health") {
            call.respond(HttpStatusCode.OK, "API is healthy!")
        }

        swaggerUI("/swagger") {
            info = OpenApiInfo("Bimag API", "1.0.0")
            source = OpenApiDocSource.Routing(
                ContentType.Application.Json
            ) {
                routingRoot.descendants()
            }
        }

        route("/api/image") {
            post {
                val image = call.receiveImage()
                    ?: return@post call.respond(HttpStatusCode.BadRequest)
                val id = imageService.create(
                    filename = image.filename,
                    contentType = image.contentType,
                    data = image.data
                )

                call.respond(HttpStatusCode.Created, id.toString())
            }

            post("/{id}/process") {
                val id = call.imageId()
                    ?: return@post call.respond(HttpStatusCode.BadRequest)
                val size = call.request.queryParameters["size"]?.toIntOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest)
                val image = imageService.read(id)
                    ?: return@post call.respond(HttpStatusCode.NotFound)
                val processedData = imageService.process(image.data, size)
                val processedId = imageService.create(
                    filename = "processed-${image.filename}",
                    contentType = "image/png",
                    data = processedData
                )

                call.respond(HttpStatusCode.Created, processedId.toString())
            }

            get("/{id}") {
                val id = call.imageId()
                    ?: return@get call.respond(HttpStatusCode.BadRequest)
                val image = imageService.read(id)
                    ?: return@get call.respond(HttpStatusCode.NotFound)

                call.respondBytes(
                    image.data,
                    ContentType.parse(image.contentType)
                )
            }

            delete("/{id}") {
                val id = call.imageId()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest)
                val deleted = imageService.delete(id)

                call.respond(
                    if (deleted) HttpStatusCode.NoContent
                    else HttpStatusCode.NotFound
                )
            }
        }
    }
}