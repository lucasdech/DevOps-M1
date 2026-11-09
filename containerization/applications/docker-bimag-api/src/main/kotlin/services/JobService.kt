package com.example.service

import com.example.database.tables.Images
import com.example.database.tables.JobOperation
import com.example.database.tables.JobStatus
import com.example.database.tables.Jobs
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.insert
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.r2dbc.update
import kotlin.UInt

data class Job(
    val id: UInt,
    val source_image_id: UInt,
    val status: JobStatus,
    val operation: JobOperation,
    val width: Int?,
    val height: Int?,
    val keep_ratio: Boolean?,
    val angle: Int?,
    val size: Int?,
    val format: String?,
    val result_image_id: UInt?,
    val error: String?,
    val createdAt: Long,
    val updatedAt: Long,
)

class JobService( private val database: R2dbcDatabase) {

    suspend fun create(source_image_id: UInt, operation: JobOperation, width: Int? = null, height: Int? = null, keep_ratio: Boolean? = null, angle: Int? = null, size: Int? = null, format: String? = null): UInt = suspendTransaction(database) {

            val now = System.currentTimeMillis()

            Jobs.insert {
                it[Jobs.source_image_id] = source_image_id
                it[Jobs.operation] = operation
                it[Jobs.status] = JobStatus.pending
                it[Jobs.width] = width
                it[Jobs.height] = height
                it[Jobs.keep_ratio] = keep_ratio
                it[Jobs.angle] = angle
                it[Jobs.size] = size
                it[Jobs.format] = format
                it[Jobs.createdAt] = now
                it[Jobs.updatedAt] = now
            }[Jobs.id].value
        }


    suspend fun read(id: UInt): Job? = suspendTransaction(database) {
        Jobs.selectAll()
            .where { Jobs.id eq id }
            .map { row ->
                Job(
                    id = row[Jobs.id].value,
                    source_image_id = row[Jobs.source_image_id].value,
                    status = row[Jobs.status],
                    operation = row[Jobs.operation],
                    width = row[Jobs.width],
                    height = row[Jobs.height],
                    keep_ratio = row[Jobs.keep_ratio],
                    angle = row[Jobs.angle],
                    size = row[Jobs.size],
                    format = row[Jobs.format],
                    result_image_id = row[Jobs.result_image_id]?.value,
                    error = row[Jobs.error],
                    createdAt = row[Jobs.createdAt],
                    updatedAt = row[Jobs.updatedAt],
                )
            }
            .singleOrNull()
    }


    suspend fun markProcessing(id: UInt): Boolean = suspendTransaction(database) {
        Jobs.update({Jobs.id eq id}) {
            it[Jobs.status] = JobStatus.processing
            it[Jobs.updatedAt] = System.currentTimeMillis()
        } > 0
    }

    suspend fun markCompleted(id: UInt, result_image_id: UInt): Boolean = suspendTransaction(database) {
        Jobs.update({Jobs.id eq id}) {
            it[Jobs.status] = JobStatus.completed
            it[Jobs.result_image_id] = result_image_id
            it[Jobs.updatedAt] = System.currentTimeMillis()
        } > 0
    }

    suspend fun markFailed(id: UInt, error: String?): Boolean = suspendTransaction(database) {
        Jobs.update({Jobs.id eq id}) {
            it[Jobs.status] = JobStatus.failed
            it[Jobs.error] = error
            it[Jobs.updatedAt] = System.currentTimeMillis()
        } > 0
    }


}