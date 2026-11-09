package com.example.worker

import com.example.config.DatabaseFactory
import com.example.config.RedisFactory
import com.example.database.tables.JobOperation
import com.example.service.ImageService
import com.example.service.JobQueue
import com.example.service.JobService
import kotlinx.coroutines.runBlocking

private fun env(name: String): String =
    System.getenv(name) ?: error("Variable d'environnement $name manquante")

fun runWorker(queue: String) = runBlocking {
    val database = DatabaseFactory.connect(env("DATABASE_URL"), env("DATABASE_USER"), env("DATABASE_PASSWORD"))
    val imageService = ImageService(database)
    val jobService = JobService(database)
    val jobQueue = JobQueue(RedisFactory.connect(env("REDIS_URL")))
    println("Worker démarré sur $queue")

    while (true) {
        val jobId = jobQueue.take(queue) ?: continue           // rien depuis 5 s : on réécoute
        val job = jobService.read(jobId)
        if (job == null || !jobService.markProcessing(jobId)) {
            println("Job $jobId introuvable, ignoré"); continue   // supprimé en cascade entre-temps
        }
        println("Job $jobId : ${job.operation} en cours")
        try {
            val source = imageService.read(job.source_image_id) ?: error("Image ${job.source_image_id} introuvable")
            val result: ByteArray = when (job.operation) {
                JobOperation.rotate    -> imageService.rotate(source.data, requireNotNull(job.angle) { "angle manquant" })
                JobOperation.resize    -> imageService.resize(source.data, job.width, job.height, job.keep_ratio ?: true)
                JobOperation.thumbnail -> imageService.thumbnail(source.data, job.size ?: ImageService.DEFAULT_THUMBNAIL_SIZE)
                JobOperation.convert   -> imageService.convert(source.data, requireNotNull(job.format) { "format manquant" })
            }
            val contentType = if (job.operation == JobOperation.convert) ImageService.OUTPUT_FORMATS.getValue(job.format!!) else "image/png"

            val resultId = imageService.create("${job.operation}-${source.filename}", contentType, result)
            jobService.markCompleted(jobId, resultId)
            println("Job $jobId terminé → image $resultId")
        } catch (e: Exception) {
            jobService.markFailed(jobId, e.message ?: e.toString())
            println("Job $jobId en échec : ${e.message}")
        }
    }
}