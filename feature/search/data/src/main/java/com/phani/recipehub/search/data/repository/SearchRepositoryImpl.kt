package com.phani.recipehub.search.data.repository

import com.phani.recipehub.search.data.mapper.toDomain
import com.phani.recipehub.search.data.remote.SearchApiService
import com.phani.recipehub.search.domain.model.Recipe
import com.phani.recipehub.search.domain.model.RecipeDetails
import com.phani.recipehub.search.domain.repository.SearchRepository

class SearchRepositoryImpl(
    private val searchApiService: SearchApiService
) : SearchRepository {
    override suspend fun getRecipes(s: String): Result<List<Recipe>> {
        return try {
            val response = searchApiService.getRecipes(s)
            if (response.isSuccessful) {
                response.body()?.meals?.let {
                    Result.success(it.toDomain())
                } ?: run {
                    Result.failure(exception = Exception("Something went wrong!"))
                }
            } else {
                Result.failure(exception = Exception("Something went wrong!"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRecipeDetails(id: String): Result<RecipeDetails> {
        return try {
            val response = searchApiService.getRecipeDetails(id)
            if (response.isSuccessful) {
                response.body()?.meals?.let {
                    if (it.isNotEmpty()) {
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
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}