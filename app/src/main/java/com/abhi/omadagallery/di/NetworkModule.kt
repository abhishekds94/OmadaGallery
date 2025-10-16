package com.abhi.omadagallery.di

import com.abhi.omadagallery.data.remote.FlickrApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideOkHttp(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(ok: OkHttpClient): Retrofit {
        val json = Json { ignoreUnknownKeys = true }
        val jsonMT = "application/json".toMediaType()
        val textMT = "text/plain".toMediaType()

        return Retrofit.Builder()
            .baseUrl("https://www.flickr.com/")
            .client(ok)
            .addConverterFactory(json.asConverterFactory(jsonMT))
            .addConverterFactory(json.asConverterFactory(textMT))
            .build()
    }

    @Provides
    @Singleton
    fun provideFlickr(retrofit: Retrofit): FlickrApi =
        retrofit.create(FlickrApi::class.java)
}
