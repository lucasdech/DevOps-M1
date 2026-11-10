package com.example.api

import com.example.database.tables.JobOperation
import com.example.service.ImageService
import com.example.service.JobQueue
import com.example.service.JobService
import io.ktor.http.*
import io.ktor.openapi.*
import io.ktor.server.application.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.openapi.*

private fun ApplicationCall.imageId(): UInt? =
    parameters["id"]?.toUIntOrNull()

fun Application.configureRouting(imageService: ImageService, jobService: JobService, jobQueue: JobQueue) {
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

            post("/process") {
                val imageId = call.request.queryParameters["imageId"]?.toUIntOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest)
                val operation =
                    JobOperation.entries.firstOrNull { it.name == call.request.queryParameters["operation"] }
                        ?: return@post call.respond(HttpStatusCode.BadRequest)
                val angle = call.request.queryParameters["angle"]?.toIntOrNull()
                val format = call.request.queryParameters["format"]?.lowercase()
                val sizeParam = call.request.queryParameters["size"]
                val size = if (sizeParam == null) ImageService.DEFAULT_THUMBNAIL_SIZE else sizeParam.toIntOrNull()
                val widthParam = call.request.queryParameters["width"]
                val heightParam = call.request.queryParameters["height"]
                val width = widthParam?.toIntOrNull()
                val height = heightParam?.toIntOrNull()
                val keepRatio = call.request.queryParameters["keepRatio"] != "false"

                val valid = when (operation) {
                    JobOperation.rotate -> angle in ImageService.ROTATION_ANGLES
                    JobOperation.convert -> format in ImageService.OUTPUT_FORMATS
                    JobOperation.thumbnail -> size in ImageService.THUMBNAIL_SIZES
                    JobOperation.resize -> (widthParam == null || width in ImageService.RESIZE_RANGE) &&
                            (heightParam == null || height in ImageService.RESIZE_RANGE) &&
                            (widthParam != null || heightParam != null) &&
                            (keepRatio || (widthParam != null && heightParam != null))
                }
                if (!valid) return@post call.respond(HttpStatusCode.BadRequest)
                imageService.read(imageId) ?: return@post call.respond(HttpStatusCode.NotFound)
                val jobId = jobService.create(
                    imageId, operation,
                    width = width,
                    height = height,
                    keep_ratio = keepRatio,
                    angle = angle,
                    size = size,
                    format = format
                )
                jobQueue.publish(jobId, operation)
                call.respond(HttpStatusCode.Accepted, jobId.toString())
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

        route("/api/jobs") {
            get("/{id}") {
                val id = call.imageId() ?: return@get call.respond(HttpStatusCode.BadRequest)
                val job = jobService.read(id) ?: return@get call.respond(HttpStatusCode.NotFound)
                call.respond(job)
            }
        }
    }
}