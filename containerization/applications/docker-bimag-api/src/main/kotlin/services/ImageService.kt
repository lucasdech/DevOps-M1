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


    suspend fun process(data: ByteArray, size: Int): ByteArray =
        withContext(Dispatchers.IO) {
            require(data.isNotEmpty()) { "Image cannot be empty" }
            require(size in 1..4096) { "Invalid image size" }

            val process = ProcessBuilder(
                "convert",
                "-",
                "-resize", "${size}x${size}>",
                "png:-"
            )
                .start()

            try {
                val output = async {
                    process.inputStream.use { it.readBytes() }
                }
                val errors = async {
                    process.errorStream.bufferedReader().use { it.readText() }
                }
                val input = async {
                    process.outputStream.use { it.write(data) }
                }
                val finished = process.waitFor(30, TimeUnit.SECONDS)
                if (!finished) {
                    process.destroyForcibly()
                    error("ImageMagick processing timed out")
                }
                input.await()
                val result = output.await()
                val errorOutput = errors.await()
                check(process.exitValue() == 0) {
                    "ImageMagick processing failed: $errorOutput"
                }
                check(result.isNotEmpty()) {
                    "ImageMagick returned an empty image"
                }
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

