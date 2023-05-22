package com.example.eulerityphotos.data

import com.example.eulerityphotos.model.Photo
import com.example.eulerityphotos.model.UploadUrl
import com.example.eulerityphotos.network.PhotosApiService

interface PhotosRepository {
    suspend fun getPhotos(): List<Photo>
}

class NetworkPhotosRepository(
    private val photoApiService: PhotosApiService
) : PhotosRepository {
    override suspend fun getPhotos(): List<Photo> = photoApiService.getPhotos()
}