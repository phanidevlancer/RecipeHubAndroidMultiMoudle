package com.phani.recipehub.search.data.repository

import com.phani.recipehub.search.data.mapper.toDomain
import com.phani.recipehub.search.data.remote.SearchApiService
import com.phani.recipehub.search.domian.model.Recipe
import com.phani.recipehub.search.domian.model.RecipeDetails
import com.phani.recipehub.search.domian.repository.SearchRepository

class SearchRepositoryImpl(
    private val searchApiService: SearchApiService
) : SearchRepository {
    override suspend fun getRecipes(s: String): Result<List<Recipe>> {
        val response = searchApiService.getRecipes(s)
        return if (response.isSuccessful) {
            response.body()?.meals?.let {
                Result.success(it.toDomain())
            } ?: run {
                Result.failure(exception = Exception("Something went wrong!"))
            }
        } else {
            Result.failure(exception = Exception("Something went wrong!"))
        }

    }

    override suspend fun getRecipeDetails(id: String): Result<RecipeDetails> {
        val response = searchApiService.getRecipeDetails(id)
        return if (response.isSuccessful) {
            response.body()?.meals?.let {
                if (it.isEmpty()) {
                    Result.success(it.first().toDomain())
                } else {
                    Result.failure(exception = Exception("Something went wrong!"))
                }
            } ?: run {
                Result.failure(exception = Exception("Something went wrong!"))
            }
        } else {
            Result.failure(exception = Exception("Something went wrong!"))
        }
    }
}