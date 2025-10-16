package com.abhi.omadagallery.data.remote

import com.abhi.omadagallery.data.remote.dto.PhotoInfoResponseDto
import com.abhi.omadagallery.data.remote.dto.PhotosResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface FlickrApi {
    @GET("services/rest")
    suspend fun getRecent(
        @Query("method") method: String = "flickr.photos.getRecent",
        @Query("api_key") key: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int = 24,
        @Query("format") format: String = "json",
        @Query("nojsoncallback") noCallback: Int = 1
    ): PhotosResponseDto

    @GET("services/rest")
    suspend fun search(
        @Query("method") method: String = "flickr.photos.search",
        @Query("api_key") key: String,
        @Query("text") text: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int = 24,
        @Query("format") format: String = "json",
        @Query("nojsoncallback") noCallback: Int = 1
    ): PhotosResponseDto

    @GET("services/rest")
    suspend fun getInfo(
        @Query("method") method: String = "flickr.photos.getInfo",
        @Query("api_key") key: String,
        @Query("photo_id") id: String,
        @Query("format") format: String = "json",
        @Query("nojsoncallback") noCallback: Int = 1
    ): PhotoInfoResponseDto
}
