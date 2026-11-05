package com.example.database.tables

import org.jetbrains.exposed.v1.core.dao.id.UIntIdTable

object Images : UIntIdTable("images") {
    val filename = varchar("filename", 255)
    val contentType = varchar("content_type", 100)
    val data = binary("data")
    val createdAt = long("created_at")
}
