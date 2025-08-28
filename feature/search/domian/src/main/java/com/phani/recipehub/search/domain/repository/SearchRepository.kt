package com.phani.recipehub.search.domain.repository

import com.phani.recipehub.search.domain.model.Recipe
import com.phani.recipehub.search.domain.model.RecipeDetails

interface SearchRepository {

    suspend fun getRecipes(s : String): Result<List<Recipe>>

    suspend fun getRecipeDetails(id: String): Result<RecipeDetails>
}