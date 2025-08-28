package com.phani.recipehub.search.ui.screens.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phani.recipehub.common.utils.NetworkResult
import com.phani.recipehub.common.utils.UiText
import com.phani.recipehub.search.domain.model.Recipe
import com.phani.recipehub.search.domain.usecase.GetRecipeDetailsUseCase
import com.phani.recipehub.search.domain.usecase.GetAllRecipeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeListViewModel @Inject constructor(
    private val getAllRecipeUseCase: GetAllRecipeUseCase,
    private val getRecipeDetailsUseCase: GetRecipeDetailsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipeList.UiState())
    val uiState: StateFlow<RecipeList.UiState> get() = _uiState.asStateFlow()

    private val _navigation = Channel<RecipeList.Navigation>()
    val navigation: Flow<RecipeList.Navigation> = _navigation.receiveAsFlow()

    fun onEvent(event: RecipeList.Event) {
        when (event) {
            is RecipeList.Event.SearchRecipe -> {
                search(event.q)
            }

            is RecipeList.Event.GoToRecipeDetails -> {
                viewModelScope.launch {
                    _navigation.send(RecipeList.Navigation.GoToRecipeDetails(event.id))
                }
            }
        }
    }

    private fun search(q: String) {
        getAllRecipeUseCase(q).onEach { result ->
            when (result) {

                is NetworkResult.Error -> {
                    println("NetworkResultttt Error")
                    _uiState.update {
                        RecipeList.UiState(error = UiText.RemoteString(result.message.toString()))
                    }
                }

                is NetworkResult.Loading -> {
                    println("NetworkResultttt loading")
                    _uiState.update {
                        RecipeList.UiState(isLoading = true)
                    }
                }

                is NetworkResult.Success -> {
                    println("NetworkResultttt Success : " + result.data)
                    _uiState.update {
                        RecipeList.UiState(data = result.data)
                    }
                }
            }
        }.launchIn(viewModelScope)

    }

}

object RecipeList {
    data class UiState(
        val isLoading: Boolean = false,
        val error: UiText = UiText.Idle,
        val data: List<Recipe>? = null
    )

    sealed interface Navigation {
        data class GoToRecipeDetails(val id: String) : Navigation
    }

    sealed interface Event {
        data class SearchRecipe(val q: String) : Event

        data class GoToRecipeDetails(val id: String) : Event
    }
}