package com.example.database.tables

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.UIntIdTable

enum class JobStatus {
    pending, processing, completed, failed
}

enum class JobOperation(val queue: String) {
    resize("bimag:jobs:size"),
    rotate("bimag:jobs:size"),
    thumbnail("bimag:jobs:size"),
    convert("bimag:jobs:format")
}

object Jobs : UIntIdTable("jobs") {
    val source_image_id = reference("source_image_id", Images, onDelete = ReferenceOption.CASCADE)
    val status = enumerationByName<JobStatus>("status", 25)
    val operation = enumerationByName<JobOperation>("operation", 25)
    val width = integer("width").nullable()
    val height = integer("height").nullable()
    val keep_ratio = bool("keep_ratio").nullable()
    val angle = integer("angle").nullable()
    val size = integer("size").nullable()
    val format = varchar("format", 255).nullable()
    val result_image_id = reference("result_image_id", Images, onDelete = ReferenceOption.CASCADE).nullable()
    val error = text("error").nullable()
    val createdAt = long("created_at")
    val updatedAt = long("updated_at")
}