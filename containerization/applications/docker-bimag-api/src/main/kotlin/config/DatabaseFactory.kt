package com.example.config

import com.example.database.tables.Images
import io.ktor.server.application.*
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.SchemaUtils
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction

object DatabaseFactory {
    fun connect(application: Application): R2dbcDatabase {
        val config = application.environment.config

        return R2dbcDatabase.connect(
            url = config.property("database.url").getString(),
            user = config.property("database.user").getString(),
            password = config.property("database.password").getString()
        )
    }

    suspend fun initializeSchema(database: R2dbcDatabase) {
        suspendTransaction(db = database) {
            SchemaUtils.create(Images)
        }
    }
}