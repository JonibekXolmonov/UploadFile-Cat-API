package com.example.catapiapp.networking.services

import com.example.catapiapp.model.Cat
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

@JvmSuppressWildcards
interface ApiService {

    @Multipart
    @POST("upload")
    fun uploadFile(@Part image: MultipartBody.Part, @Part("sub_id") name: String): Call<Cat>

    @GET("search")
    fun search(
        @Query("limit") limit: Int,
        @Query("page") page: Int,
        @Query("mimi_types") type: String
    ): Call<List<Cat>>
}