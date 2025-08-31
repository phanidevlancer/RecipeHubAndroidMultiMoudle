package com.phani.recipehub.search.ui.screens.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phani.recipehub.common.utils.NetworkResult
import com.phani.recipehub.common.utils.UiText
import com.phani.recipehub.search.domain.usecase.GetRecipeDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeDetailsViewModel @Inject constructor(private val getRecipeDetailsUseCase: GetRecipeDetailsUseCase) :
    ViewModel() {

    private val _uiState = MutableStateFlow(RecipeDetails.UiState())
    val uiState: StateFlow<RecipeDetails.UiState> get() = _uiState.asStateFlow()

    private val _navigation = Channel<RecipeDetails.Navigation>()
    val navigation: Flow<RecipeDetails.Navigation> get() = _navigation.receiveAsFlow()

    fun onEvent(event: RecipeDetails.Event) {
        when (event) {
            is RecipeDetails.Event.FetchRecipeDetails -> {
                getRecipeDetails(event.id)
            }

            RecipeDetails.Event.NavigateBack -> {
                viewModelScope.launch {
                    _navigation.send(RecipeDetails.Navigation.Back)
                }
            }
        }
    }

    private fun getRecipeDetails(id: String) =
        getRecipeDetailsUseCase(id).onEach { result ->
            when (result) {
                is NetworkResult.Error -> {
                    println("NetworkResulttttt Error details")
                    _uiState.update {
                        RecipeDetails.UiState(error = UiText.RemoteString(result.message.toString()))
                    }
                }

                is NetworkResult.Loading -> {
                    println("NetworkResulttttt Loading details")
                    _uiState.update {
                        RecipeDetails.UiState(isLoading = true)
                    }
                }

                is NetworkResult.Success -> {
                    println("NetworkResulttttt Success details")
                    _uiState.update {
                        RecipeDetails.UiState(data = result.data)
                    }
                }
            }
        }.launchIn(viewModelScope)
}

object RecipeDetails {

    data class UiState(
        val isLoading: Boolean = false,
        val error: UiText = UiText.Idle,
        val data: com.phani.recipehub.search.domain.model.RecipeDetails? = null
    )

    sealed interface Navigation {
        data object Back : Navigation
    }

    sealed interface Event {
        data class FetchRecipeDetails(val id: String) : Event

        data object NavigateBack : Event
    }

}