package com.example


import com.example.api.configureRouting
import com.example.config.DatabaseFactory
import com.example.config.configureCors
import com.example.config.configureSerialization
import com.example.service.ImageService
import io.ktor.server.application.*
import kotlinx.coroutines.runBlocking

fun Application.module() {
    val database = DatabaseFactory.connect(this)
    val imageService = ImageService(database)

    runBlocking {
        DatabaseFactory.initializeSchema(database)
    }

    configureSerialization()
    configureRouting(imageService)
    configureCors()
}