package com.example.eulerityphotos.network


import com.example.eulerityphotos.model.Photo
import com.example.eulerityphotos.model.UploadUrl
import retrofit2.http.GET
import retrofit2.http.POST

interface PhotosApiService {
    @GET("image")
    suspend fun getPhotos(): List<Photo>
}