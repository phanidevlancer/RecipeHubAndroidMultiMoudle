package com.phani.recipehub.search.domian.repository

import com.phani.recipehub.search.domian.model.Recipe
import com.phani.recipehub.search.domian.model.RecipeDetails

interface SearchRepository {

    suspend fun getRecipes(s : String): Result<List<Recipe>>

    suspend fun getRecipeDetails(id: String): Result<RecipeDetails>
}