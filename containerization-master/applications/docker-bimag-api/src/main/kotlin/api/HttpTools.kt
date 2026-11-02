package com.example.api

import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.utils.io.jvm.javaio.*

private const val MAX_IMAGE_SIZE = 10 * 1024 * 1024

data class ImageUpload(
    val filename: String,
    val contentType: String,
    val data: ByteArray
)

suspend fun ApplicationCall.receiveImage(): ImageUpload? {
    var image: ImageUpload? = null

    receiveMultipart().forEachPart { part ->
        try {
            if (part is PartData.FileItem && image == null) {
                val data = part.provider()
                    .toInputStream()
                    .use { it.readNBytes(MAX_IMAGE_SIZE + 1) }

                if (data.isNotEmpty() && data.size <= MAX_IMAGE_SIZE) {
                    image = ImageUpload(
                        filename = part.originalFileName.orEmpty(),
                        contentType = part.contentType?.toString().orEmpty(),
                        data = data
                    )
                }
            }
        } finally {
            part.release()
        }
    }

    return image?.takeIf {
        it.filename.isNotBlank() &&
                it.contentType.startsWith("image/")
    }
}