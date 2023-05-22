package com.example.eulerityphotos.network

import com.example.eulerityphotos.model.UploadUrl
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Url

interface UploadApiService {
    @GET("upload")
    fun getUploadUrl(): Call<UploadUrl>

    @Multipart
    @POST()
    fun uploadImage(
        @Url fullUrl: String?,
        @Part("appid") appid: RequestBody,
        @Part("original") original: RequestBody,
        @Part file: MultipartBody.Part
    ): Call<ResponseBody>
}