package com.phani.recipiehub.di

import android.content.Context
import com.phani.recipehub.search.data.local.RecipeDao
import com.phani.recipehub.search.ui.navigation.SearchFeatureApi
import com.phani.recipiehub.local.AppDatabase
import com.phani.recipiehub.navigation.NavigationSubGraphs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Provides
    fun provideNavigationSubGraphs(searchFeatureApi: SearchFeatureApi): NavigationSubGraphs {
        return NavigationSubGraphs(searchFeatureApi)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context) = AppDatabase.getInstance(context)

    @Provides
    fun provideRecipeDao(appDatabase: AppDatabase) : RecipeDao = appDatabase.recipeDao()
}