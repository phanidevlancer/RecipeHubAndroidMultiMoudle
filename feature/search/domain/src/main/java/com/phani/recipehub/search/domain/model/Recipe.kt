package com.phani.recipehub.search.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

interface IRecipe {
    val idMeal: String?
    val area: String?
    val meal: String?
    val mealThumb: String?
    val category: String?
    val tags: String
    val youtubeUrl: String
    val instructions: String?
}

@Entity
data class Recipe(
    @PrimaryKey(false)
    override val idMeal: String,
    override val area: String?,
    override val meal: String?,
    override val mealThumb: String?,
    override val category: String?,
    override val tags: String,
    override val youtubeUrl: String,
    override val instructions: String?

) : IRecipe


data class RecipeDetails(
    val recipe: Recipe,
    val ingredientsPair: List<Pair<String, String>>
) : IRecipe by recipe