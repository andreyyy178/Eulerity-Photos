package com.example.eulerityphotos.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UploadUrl(val url: String)
