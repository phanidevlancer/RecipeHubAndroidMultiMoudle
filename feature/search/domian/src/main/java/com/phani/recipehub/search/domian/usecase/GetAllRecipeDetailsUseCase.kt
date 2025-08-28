package com.phani.recipehub.search.domian.usecase

import com.phani.recipehub.common.utils.NetworkResult
import com.phani.recipehub.search.domian.model.RecipeDetails
import com.phani.recipehub.search.domian.repository.SearchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetAllRecipeDetailsUseCase @Inject constructor(private val searchRepository: SearchRepository) {

    operator fun invoke(id: String) = flow<NetworkResult<RecipeDetails>> {
        emit(NetworkResult.Loading())
        val response = searchRepository.getRecipeDetails(id)
        if (response.isSuccess) {
            emit(NetworkResult.Success(response.getOrThrow()))
        } else {
            emit(NetworkResult.Error(response.exceptionOrNull()?.localizedMessage))
        }
    }.catch {
        emit(NetworkResult.Error(it.message.toString()))
    }.flowOn(Dispatchers.IO)
}