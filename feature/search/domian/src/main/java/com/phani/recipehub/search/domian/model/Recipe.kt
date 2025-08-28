package com.phani.recipehub.search.domian.model

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

data class Recipe(
    override val idMeal: String?,
    override val area: String?,
    override val meal: String?,
    override val mealThumb: String?,
    override val category: String?,
    override val tags: String,
    override val youtubeUrl: String,
    override val instructions: String?

) : IRecipe


data class RecipeDetails(
    private val recipe: Recipe,
    val ingredientsPair: List<Pair<String, String>>
) : IRecipe by recipe