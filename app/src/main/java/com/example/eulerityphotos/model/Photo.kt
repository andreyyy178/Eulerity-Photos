package com.example.eulerityphotos.model


import com.squareup.moshi.JsonClass
import java.util.UUID

@JsonClass(generateAdapter = true)
data class Photo(
    val id: String = UUID.randomUUID().toString(),
    val url: String,
    val created: String,
    val updated: String,
)
