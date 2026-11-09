package com.example


import com.example.api.configureRouting
import com.example.config.DatabaseFactory
import com.example.config.RedisFactory
import com.example.config.configureCors
import com.example.config.configureSerialization
import com.example.service.ImageService
import com.example.service.JobQueue
import com.example.service.JobService
import io.ktor.server.application.*
import kotlinx.coroutines.runBlocking

fun Application.module() {
    val database = DatabaseFactory.connect(this)
    val imageService = ImageService(database)
    val redis = RedisFactory.connect(environment.config.property("redis.url").getString())



    runBlocking {
        DatabaseFactory.initializeSchema(database)
    }

    configureSerialization()
    configureRouting(imageService, JobService(database), JobQueue(redis))
    configureCors()
}