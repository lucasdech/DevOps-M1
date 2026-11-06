package com.example.service


import com.example.database.tables.Images
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.deleteWhere
import org.jetbrains.exposed.v1.r2dbc.insert
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import java.util.concurrent.TimeUnit

data class Image(
    val id: UInt,
    val filename: String,
    val contentType: String,
    val data: ByteArray,
    val createdAt: Long
)

class ImageService(private val database: R2dbcDatabase) {

    // Liste des choix d'angle de rotation possible
    companion object {
      val RESIZE_RANGE = 1..4096
      val ROTATION_ANGLES = setOf(90, 180, 270)
      val OUTPUT_FORMATS = mapOf(
          "png" to "image/png",
          "jpeg" to "image/jpeg",
          "webp" to "image/webp",
          "gif" to "image/gif"
      )
      val THUMBNAIL_SIZES = 1..512
      const val DEFAULT_THUMBNAIL_SIZE = 128
    }

    suspend fun create(filename: String, contentType: String, data: ByteArray): UInt = suspendTransaction(database) {
        require(filename.isNotBlank()) { "Filename cannot be blank" }
        require(contentType.startsWith("image/")) {
            "Invalid image content type"
        }
        require(data.isNotEmpty()) { "Image cannot be empty" }

        Images.insert {
            it[Images.filename] = filename
            it[Images.contentType] = contentType
            it[Images.data] = data
            it[Images.createdAt] = System.currentTimeMillis()
        }[Images.id].value
    }


    suspend fun process(data: ByteArray, size: Int): ByteArray {
        require(data.isNotEmpty()) { "Image cannot be empty" }
        require(size in 1..4096) { "Invalid image size" }

        return runImageMagick(data, listOf("-resize", "${size}x${size}>"))
    }

    suspend fun rotate(data: ByteArray, angle: Int): ByteArray {
        require(data.isNotEmpty()) { "Image cannot be empty" }
        require(angle in ROTATION_ANGLES) { "Invalid rotation angle" }

        return runImageMagick(data, listOf("-rotate", angle.toString()))
    }
    suspend fun convert(data: ByteArray, format: String): ByteArray {
        require(data.isNotEmpty()) { "Image cannot be empty" }
        require(format in OUTPUT_FORMATS) { "Invalid output format" }

        return runImageMagick(data, emptyList(), format)
    }

    suspend fun thumbnail(data: ByteArray, size: Int): ByteArray {
        require(data.isNotEmpty()) { "Image cannot be empty" }
        require(size in THUMBNAIL_SIZES) { "Invalid thumbnail size" }

        return runImageMagick(data, listOf("-thumbnail", "${size}x${size}>"))
    }

    suspend fun resize(data: ByteArray, width: Int?, height: Int?, keepRatio: Boolean): ByteArray {
        require(data.isNotEmpty()) { "Image cannot be empty" }
        require(width != null || height != null) { "Width or height is required" }
        require(width == null || width in RESIZE_RANGE) { "Invalid width" }
        require(height == null || height in RESIZE_RANGE) { "Invalid height" }
        require(keepRatio || (width != null && height != null)) {
            "Width and height are required when the ratio is not kept"
        }

        val geometry = "${width ?: ""}x${height ?: ""}" + if (keepRatio) "" else "!"
        return runImageMagick(data, listOf("-resize", geometry))
    }

    private suspend fun runImageMagick(
        data: ByteArray,
        args: List<String>,
        outputFormat: String = "png"
    ): ByteArray =
        withContext(Dispatchers.IO) {
            val process = ProcessBuilder(listOf("convert", "-") + args + "$outputFormat:-")
                .start()

            try {
                val output = async { process.inputStream.use { it.readBytes() } }
                val errors = async { process.errorStream.bufferedReader().use { it.readText() } }
                val input = async { process.outputStream.use { it.write(data) } }
                val finished = process.waitFor(30, TimeUnit.SECONDS)
                if (!finished) {
                    process.destroyForcibly()
                    error("ImageMagick processing timed out")
                }
                input.await()
                val result = output.await()
                val errorOutput = errors.await()
                check(process.exitValue() == 0) { "ImageMagick processing failed: $errorOutput" }
                check(result.isNotEmpty()) { "ImageMagick returned an empty image" }
                result
            } finally {
                if (process.isAlive) {
                    process.destroyForcibly()
                }
            }
        }

    suspend fun read(id: UInt): Image? = suspendTransaction(database) {
        Images.selectAll()
            .where { Images.id eq id }
            .map { row ->
                Image(
                    id = row[Images.id].value,
                    filename = row[Images.filename],
                    contentType = row[Images.contentType],
                    data = row[Images.data],
                    createdAt = row[Images.createdAt]
                )
            }
            .singleOrNull()
    }

    suspend fun delete(id: UInt): Boolean = suspendTransaction(database) {
        Images.deleteWhere {
            Images.id eq id
        } > 0
    }
}

