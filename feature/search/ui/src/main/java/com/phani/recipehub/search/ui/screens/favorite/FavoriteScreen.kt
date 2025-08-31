package com.phani.recipehub.search.ui.screens.favorite

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.phani.recipehub.common.utils.UiText
import com.phani.recipehub.search.domain.model.Recipe
import com.phani.recipehub.search.ui.screens.list.Chip
import com.phani.recipehub.search.ui.screens.list.RecipeList
import com.phani.recipehub.search.ui.screens.list.ShimmerBox
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteScreen(
    modifier: Modifier = Modifier,
    viewModel: FavoriteViewModel,
    onClick: (String) -> Unit
) {

    val showDropDown = rememberSaveable { mutableStateOf(false) }
    val selectedIndex = rememberSaveable { mutableIntStateOf(-1) }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(viewModel.navigation) {
        viewModel.navigation.flowWithLifecycle(lifecycleOwner.lifecycle).collectLatest {
            when (it) {
                is FavoriteScreen.Navigation.NavigateToRecipeDetails -> onClick(it.id)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Favorite Recipes",
                        style = MaterialTheme.typography.titleLarge
                    )
                }, actions = {
                    IconButton(onClick = { showDropDown.value = showDropDown.value.not() }) {
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
                    }

                    if (showDropDown.value) {
                        DropdownMenu(
                            expanded = showDropDown.value,
                            onDismissRequest = { showDropDown.value = !showDropDown.value }) {
                            DropdownMenuItem(text = { Text("Alphabetical") }, onClick = {
                                selectedIndex.intValue = 0
                                showDropDown.value = false
                                viewModel.onEvent(FavoriteScreen.Event.AlphabeticalSort)
                            }, leadingIcon = {
                                RadioButton(
                                    selected = selectedIndex.intValue == 0,
                                    onClick = {
                                        selectedIndex.intValue = 0
                                        showDropDown.value = false
                                        viewModel.onEvent(FavoriteScreen.Event.AlphabeticalSort)
                                    }
                                )
                            }
                            )

                            DropdownMenuItem(text = { Text("Less Ingredients") }, onClick = {
                                selectedIndex.intValue = 1
                                showDropDown.value = false
                                viewModel.onEvent(FavoriteScreen.Event.LessIngredientsSort)
                            }, leadingIcon = {
                                RadioButton(
                                    selected = selectedIndex.intValue == 1,
                                    onClick = {
                                        selectedIndex.intValue = 1
                                        showDropDown.value = false
                                        viewModel.onEvent(FavoriteScreen.Event.LessIngredientsSort)
                                    }
                                )
                            }
                            )


                            DropdownMenuItem(text = { Text("Reset") }, onClick = {
                                selectedIndex.value = 2
                                showDropDown.value = false
                                viewModel.onEvent(FavoriteScreen.Event.ResetSort)
                            }, leadingIcon = {
                                RadioButton(
                                    selected = selectedIndex.intValue == 2,
                                    onClick = {
                                        selectedIndex.intValue = 2
                                        showDropDown.value = false
                                        viewModel.onEvent(FavoriteScreen.Event.ResetSort)
                                    }
                                )
                            }
                            )
                        }
                    }
                }

            )
        }) { padding ->

        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        if (uiState.error !is UiText.Idle) {
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Something went wrong!")
            }
        }

        uiState.recipes?.let { list ->
            if (list.isEmpty()) {
                Box(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No recipes found!")
                }

            } else {
                LazyColumn(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                ) {

                    items(list) { recipe ->
                        Card(onClick = onClick, onDeleteClick = {
                            viewModel.onEvent(FavoriteScreen.Event.DeleteRecipe(recipe))
                        }, recipe = recipe)
                    }
                }
            }
        }

    }
}

@Composable
@Preview
fun PreviewCard() {
    Card(
        onClick = {}, onDeleteClick = {}, recipe = Recipe(
            idMeal = "101",
            area = "Indian",
            meal = "Chicken Biryani",
            mealThumb = "https://picsum.photos/seed/biryani/400/300",
            category = "Main Course",
            tags = "Spicy, Rice",
            youtubeUrl = "https://www.youtube.com/watch?v=IEDEtZ4UVtI",
            instructions = "Marinate chicken with spices. Cook rice. Layer chicken and rice. Dum cook."
        )
    )
}

@Composable
fun Card(onClick: (String) -> Unit, onDeleteClick: () -> Unit, recipe: Recipe) {
    ElevatedCard(
        modifier = Modifier
            .padding(horizontal = 12.dp)
            .fillMaxWidth()
            .animateContentSize()
            .clickable { onClick(recipe.idMeal) },
        shape = RoundedCornerShape(20.dp)
    ) {
        // Hero image with gradient overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        ) {
            ShimmerBox(height = 220.dp)
            Box() {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(recipe.mealThumb)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Recipe Thumbnail",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                IconButton(onClick = {
                    onDeleteClick()
                }) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                }
            }
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            0f to Color.Transparent,
                            0.6f to Color.Black.copy(alpha = 0.25f),
                            1f to Color.Black.copy(alpha = 0.7f)
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = recipe.meal.orEmpty(),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(6.dp))
                // Tags row (uses your FlowRow input)
                val tags = recipe.tags
                    .split(",", ";")
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
                    .take(3)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    tags.forEach { Chip(it, onClick = { }) }
                }
            }
        }

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                recipe.instructions.orEmpty(),
                style = MaterialTheme.typography.bodyMedium,
                overflow = TextOverflow.Ellipsis,
                maxLines = 4
            )

            if (recipe.tags.isNotEmpty()) {
                Spacer(Modifier.height(10.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    recipe.tags.split(",", ";").forEach { raw ->
                        val str = raw.trim()
                        if (str.isNotEmpty()) {
                            // Soft bordered chip for full list
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .border(
                                        width = 1.dp,
                                        color = MaterialTheme.colorScheme.outline.copy(
                                            0.4f
                                        ),
                                        shape = RoundedCornerShape(24.dp)
                                    )
                            ) {
                                Text(
                                    text = str,
                                    style = MaterialTheme.typography.labelLarge,
                                    modifier = Modifier.padding(
                                        vertical = 8.dp,
                                        horizontal = 14.dp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
