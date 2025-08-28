package com.phani.recipehub.search.ui.screens.list

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.phani.recipehub.common.navigation.NavigationRoute
import com.phani.recipehub.common.utils.UiText
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RecipeListScreen(
    modifier: Modifier = Modifier,
    viewModel: RecipeListViewModel,
    navHostController: NavHostController,
    onClick: (String) -> Unit
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val query = rememberSaveable { mutableStateOf("") }
    val lifecycleOwner = LocalLifecycleOwner.current
    val focusManager = LocalFocusManager.current

    LaunchedEffect(viewModel.navigation) {
        viewModel.navigation.flowWithLifecycle(lifecycleOwner.lifecycle).collectLatest {
            when (it) {
                is RecipeList.Navigation.GoToRecipeDetails ->
                    navHostController.navigate(NavigationRoute.RecipeDetails.sendId(it.id))
            }
        }
    }

    Scaffold(
        topBar = {
            SearchTopBar(

                value = query.value,
                onValueChange = {
                    query.value = it
                    viewModel.onEvent(RecipeList.Event.SearchRecipe(query.value))
                },
                onClear = {
                    query.value = ""
                    viewModel.onEvent(RecipeList.Event.SearchRecipe(""))
                },
                onSearchDone = { focusManager.clearFocus() }
            )
        },
        contentWindowInsets = WindowInsets(0)
    ) { padding ->

        when {
            uiState.value.isLoading -> {
                LoadingState(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                )
            }

            uiState.value.error !is UiText.Idle -> {
                ErrorState(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                )
            }

            else -> {
                uiState.value.data?.let { data ->
                    if (data.isEmpty()) {
                        EmptyState(
                            modifier = Modifier
                                .padding(padding)
                                .fillMaxSize(),
                            hint = "Try a different keyword (e.g., \"chicken\", \"pasta\")."
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .padding(padding)
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                            contentPadding = PaddingValues(bottom = 24.dp)
                        ) {
                            items(items = data, key = { it.idMeal.toString() }) { recipe ->
                                ElevatedCard(
                                    modifier = Modifier
                                        .padding(horizontal = 12.dp)
                                        .fillMaxWidth()
                                        .animateContentSize()
                                        .clickable { onClick(recipe.idMeal.toString()) },
                                    shape = RoundedCornerShape(20.dp)
                                ) {
                                    // Hero image with gradient overlay
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(220.dp)
                                    ) {
                                        ShimmerBox(height = 220.dp)
                                        AsyncImage(
                                            model = ImageRequest.Builder(LocalContext.current)
                                                .data(recipe.mealThumb)
                                                .crossfade(true)
                                                .build(),
                                            contentDescription = "Recipe Thumbnail",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
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
                        }
                    }
                }
            }
        }
    }
}

/* ----------------- UI Pieces ----------------- */

@Composable
private fun SearchTopBar(
    value: String,
    onValueChange: (String) -> Unit,
    onClear: () -> Unit,
    onSearchDone: () -> Unit
) {
    Surface(tonalElevation = 2.dp) {
        Row(
            modifier = Modifier
                .statusBarsPadding()
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                .padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.width(8.dp))
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .weight(1f)
                    .background(Color.Transparent),
                placeholder = {
                    Text("Search recipes…", style = MaterialTheme.typography.bodyMedium)
                },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onSearchDone() })
            )
            if (value.isNotEmpty()) {
                IconButton(onClick = onClear) {
                    Icon(Icons.Filled.Clear, contentDescription = "Clear")
                }
            }
        }
    }
}

@Composable
private fun Chip(text: String, onClick: () -> Unit) {
    AssistChip(
        onClick = onClick,
        label = { Text(text) },
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.35f)),
        colors = AssistChipDefaults.assistChipColors(
            containerColor = Color.White.copy(alpha = 0.15f),
            labelColor = Color.White
        )
    )
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Box(modifier, contentAlignment = Alignment.Center) {
        CircularProgressIndicator(strokeWidth = 4.dp)
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier, hint: String) {
    Box(modifier, contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "No results",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = hint,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ErrorState(modifier: Modifier = Modifier) {
    Box(modifier, contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Rounded.Warning,
                contentDescription = null,
                modifier = Modifier.size(36.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Something went wrong.\nPlease try again.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

/* --------- Shimmer for image loading --------- */

@Composable
private fun ShimmerBox(height: Dp) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val x by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "x"
    )
    val brush = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.surfaceVariant.copy(0.6f),
            MaterialTheme.colorScheme.surfaceVariant.copy(0.2f),
            MaterialTheme.colorScheme.surfaceVariant.copy(0.6f),
        ),
        start = Offset(x - 200f, 0f),
        end = Offset(x, 200f)
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .background(brush)
    )
}
