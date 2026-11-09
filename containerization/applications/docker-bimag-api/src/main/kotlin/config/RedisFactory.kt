package com.example.config

import io.lettuce.core.RedisClient
import io.lettuce.core.api.StatefulRedisConnection

object RedisFactory {
    fun connect(url: String): StatefulRedisConnection<String, String> =
        RedisClient.create(url).connect()
}