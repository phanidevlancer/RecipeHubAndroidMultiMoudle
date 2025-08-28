package com.phani.recipehub.search.data.di

import com.phani.recipehub.search.data.remote.SearchApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

const val BASE_URL = "https://www.themealdb.com/"

@InstallIn(SingletonComponent::class)
@Module
object SearchDataModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    fun provideSearchApiService(retrofit: Retrofit): SearchApiService = retrofit.create(
        SearchApiService::class.java
    )

}