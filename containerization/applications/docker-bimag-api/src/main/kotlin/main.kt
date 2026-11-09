package com.example

import com.example.database.tables.JobOperation
import com.example.worker.runWorker

fun main(args: Array<String>) {
    when (val role = System.getenv("APP_ROLE") ?: "api") {
        "api"           -> io.ktor.server.netty.EngineMain.main(args)
        "size-worker"   -> runWorker(JobOperation.rotate.queue)
        "format-worker" -> runWorker(JobOperation.convert.queue)
        else            -> error("APP_ROLE inconnu : $role")
    }
}