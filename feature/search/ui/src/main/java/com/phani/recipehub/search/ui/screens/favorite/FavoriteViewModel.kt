package com.phani.recipehub.search.ui.screens.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phani.recipehub.common.utils.UiText
import com.phani.recipehub.search.domain.model.Recipe
import com.phani.recipehub.search.domain.usecase.DeleteRecipeUseCase
import com.phani.recipehub.search.domain.usecase.GetAllRecipesFromLocalDBUseCase
import com.phani.recipehub.search.ui.screens.favorite.FavoriteScreen.Navigation.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val gtAllRecipesFromLocalDBUseCase: GetAllRecipesFromLocalDBUseCase,
    private val deleteRecipeUseCase: DeleteRecipeUseCase
) : ViewModel() {

    private var originalList = mutableListOf<Recipe>()


    init {
        getAllRecipeList()
    }

    fun onEvent(event: FavoriteScreen.Event) {
        when (event) {
            FavoriteScreen.Event.AlphabeticalSort -> alphabeticalSort()
            FavoriteScreen.Event.LessIngredientsSort -> lessIngredientsSort()
            FavoriteScreen.Event.ResetSort -> resetSort()
            is FavoriteScreen.Event.ShowDetails -> viewModelScope.launch {
                _navigation.trySend(NavigateToRecipeDetails(event.id))
            }

            is FavoriteScreen.Event.DeleteRecipe -> deleteRecipe(event.recipe)

        }
    }

    private fun deleteRecipe(recipe: Recipe) = deleteRecipeUseCase(recipe).launchIn(
        viewModelScope
    )


    private val _uiState = MutableStateFlow(FavoriteScreen.UiState())
    val uiState get() = _uiState.asStateFlow()

    private val _navigation = Channel<FavoriteScreen.Navigation>()
    val navigation get() = _navigation.receiveAsFlow()

    private fun getAllRecipeList() = viewModelScope.launch {
        gtAllRecipesFromLocalDBUseCase().collectLatest { list ->
            originalList = list.toMutableList()
            _uiState.update {
                FavoriteScreen.UiState(recipes = list)
            }
        }
    }

    fun alphabeticalSort() = _uiState.update {
        FavoriteScreen.UiState(recipes = originalList.sortedBy { recipe ->
            recipe.meal
        })

    }

    fun lessIngredientsSort() = _uiState.update {
        FavoriteScreen.UiState(recipes = originalList.sortedBy { recipe ->
            recipe.instructions?.length
        })

    }

    fun resetSort() = _uiState.update {
        FavoriteScreen.UiState(recipes = originalList)

    }

}


object FavoriteScreen {
    data class UiState(
        val isLoading: Boolean = false,
        val recipes: List<Recipe>? = null,
        val error: UiText = UiText.Idle,
    )

    sealed interface Event {
        data object AlphabeticalSort : Event
        data object LessIngredientsSort : Event
        data object ResetSort : Event
        data class DeleteRecipe(val recipe: Recipe) : Event
        data class ShowDetails(val id: String) : Event
    }

    sealed interface Navigation {
        data class NavigateToRecipeDetails(val id: String) : Navigation

    }
}