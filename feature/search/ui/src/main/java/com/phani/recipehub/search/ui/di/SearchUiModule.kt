package com.phani.recipehub.search.ui.di

import com.phani.recipehub.search.ui.navigation.SearchFeatureApi
import com.phani.recipehub.search.ui.navigation.SearchFeatureApiImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent



@InstallIn(SingletonComponent::class)
@Module
object SearchUiModule {

    @Provides
    fun provideSearchFeatureApi() : SearchFeatureApi {
        return SearchFeatureApiImpl()
    }

}