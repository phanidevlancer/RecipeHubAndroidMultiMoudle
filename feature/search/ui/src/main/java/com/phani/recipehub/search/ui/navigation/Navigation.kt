package com.phani.recipehub.search.ui.navigation


import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.phani.recipehub.common.navigation.FeatureApi
import com.phani.recipehub.common.navigation.NavigationRoute
import com.phani.recipehub.common.navigation.NavigationSubGraphRoute
import com.phani.recipehub.search.ui.screens.details.RecipeDetails
import com.phani.recipehub.search.ui.screens.details.RecipeDetailsScreen
import com.phani.recipehub.search.ui.screens.details.RecipeDetailsViewModel
import com.phani.recipehub.search.ui.screens.list.RecipeList
import com.phani.recipehub.search.ui.screens.list.RecipeListScreen
import com.phani.recipehub.search.ui.screens.list.RecipeListViewModel

interface SearchFeatureApi : FeatureApi

class SearchFeatureApiImpl : SearchFeatureApi {
    override fun registerGraph(
        navGraphBuilder: NavGraphBuilder, navController: NavHostController
    ) {
        navGraphBuilder.navigation(
            route = NavigationSubGraphRoute.Search.route,
            startDestination = NavigationRoute.RecipeList.route
        ) {
            composable(route = NavigationRoute.RecipeList.route) {
                val recipeListViewModel = hiltViewModel<RecipeListViewModel>()
                RecipeListScreen(
                    viewModel = recipeListViewModel, navHostController = navController
                ) {
                    recipeListViewModel.onEvent(RecipeList.Event.GoToRecipeDetails(it))
                }
            }

            composable(route = NavigationRoute.RecipeDetails.route) {
                val recipeDetailsViewModel = hiltViewModel<RecipeDetailsViewModel>()
                val mealId = it.arguments?.getString("id")
                LaunchedEffect(mealId) {
                    mealId?.let {
                        recipeDetailsViewModel.onEvent(RecipeDetails.Event.FetchRecipeDetails(it))
                    }
                }
                RecipeDetailsScreen(viewModel = recipeDetailsViewModel, onBackClick = {
                    navController.navigateUp()
                }, onFavClick = {

                }, onDeleteClick = {

                })
            }
        }
    }

}