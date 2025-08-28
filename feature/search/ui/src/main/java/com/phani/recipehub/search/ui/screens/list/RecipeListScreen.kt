package com.phani.recipehub.search.ui.screens.list

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.phani.recipehub.common.utils.UiText

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RecipeListScreen(
    modifier: Modifier = Modifier,
    viewModel: RecipeListViewModel,
    onClick: (String) -> Unit
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val query = rememberSaveable {
        mutableStateOf("")
    }
    Scaffold(
        topBar = {
            TextField(
                placeholder = {
                    Text(
                        "Search Recipe...",
                        style = MaterialTheme.typography.bodySmall
                    )
                },
                value = query.value,
                onValueChange = {
                    query.value = it
                    viewModel.onEvent(RecipeList.Event.SearchRecipe(query.value))
                },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }) {
        if (uiState.value.isLoading) {
            Box(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize(), contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        if (uiState.value.error !is UiText.Idle) {
            Box(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize(), contentAlignment = Alignment.Center
            ) {
                Text(text = "some error")
            }
        }
        uiState.value.data?.let { data ->

            if (data.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "No results found!")

                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .padding(it)
                        .fillMaxSize()
                ) {

                    items(items = data) { recipe ->
                        Card(
                            modifier = Modifier
                                .padding(
                                    horizontal = 12.dp, vertical = 8.dp
                                )
                                .clickable {
                                    onClick(recipe.idMeal.toString())
                                }, shape = RoundedCornerShape(12.dp)
                        ) {
                            AsyncImage(
                                model = recipe.mealThumb,
                                contentDescription = "Recipe Thumbnail",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(250.dp),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.height(12.dp))


                            Column(
                                modifier = Modifier
                                    .padding(vertical = 12.dp)
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            ) {
                                Text(
                                    text = recipe.meal.toString(),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    recipe.instructions.toString(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 4
                                )
                                if (recipe.tags.isNotEmpty()) {
                                    FlowRow {
                                        recipe.tags.split(",").forEach { str ->
                                            Box(
                                                modifier = Modifier
                                                    .wrapContentSize()
                                                    .padding(horizontal = 0.dp, vertical = 8.dp)
                                                    .padding(end = 4.dp)
                                                    .background(
                                                        Color.White,
                                                        shape = RoundedCornerShape(24.dp)
                                                    )
                                                    .clip(RoundedCornerShape(24.dp))
                                                    .border(
                                                        width = 1.dp,
                                                        color = Color.Red,
                                                        shape = RoundedCornerShape(24.dp)
                                                    ),

                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = str,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    modifier = Modifier.padding(
                                                        vertical = 8.dp,
                                                        horizontal = 16.dp
                                                    )
                                                )

                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }

                        }

                    }
                }
            }
        }
    }
}