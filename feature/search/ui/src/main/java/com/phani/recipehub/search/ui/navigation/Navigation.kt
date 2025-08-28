package com.phani.recipehub.search.ui.navigation


import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.phani.recipehub.common.navigation.FeatureApi
import com.phani.recipehub.common.navigation.NavigationRoute
import com.phani.recipehub.common.navigation.NavigationSubGraphRoute
import com.phani.recipehub.search.ui.screens.details.RecipeDetailsScreen
import com.phani.recipehub.search.ui.screens.list.RecipeListScreen

interface SearchFeatureApi : FeatureApi

class SearchFeatureApiImpl : SearchFeatureApi {
    override fun registerGraph(
        navGraphBuilder: NavGraphBuilder,
        navController: NavController
    ) {
        navGraphBuilder.navigation(
            route = NavigationSubGraphRoute.Search.route,
            startDestination = NavigationRoute.RecipeList.route
        ) {
            composable(route = NavigationRoute.RecipeList.route) {
                RecipeListScreen()
            }

            composable(route = NavigationRoute.RecipeDetails.route) {
                RecipeDetailsScreen()
            }
        }
    }

}