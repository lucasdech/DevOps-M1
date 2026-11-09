package com.example.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.database.tables.JobOperation
import io.lettuce.core.api.StatefulRedisConnection

class JobQueue(private val redis: StatefulRedisConnection<String, String>) {
    suspend fun publish(jobId: UInt, operation: JobOperation) = withContext(Dispatchers.IO) {
        redis.sync().lpush(operation.queue, jobId.toString())
    }
}