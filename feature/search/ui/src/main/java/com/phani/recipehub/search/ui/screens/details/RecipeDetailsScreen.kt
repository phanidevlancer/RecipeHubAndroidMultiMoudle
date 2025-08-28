package com.phani.recipehub.search.ui.screens.details

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults.exitUntilCollapsedScrollBehavior
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.phani.recipehub.common.utils.UiText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailsScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    viewModel: RecipeDetailsViewModel
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = uiState.value.data?.meal ?: "Recipe",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onBackClick() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
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
                uiState.value.data?.let { recipe ->
                    val uri = LocalUriHandler.current
                    val chips = buildList {
//                        if (recipe.category?.isNotBlank()) add(recipe.category)
//                        if (recipe.area?.isNotBlank()) add(recipe.area)
                        // tags may be comma-separated; show a couple
                        val tagList = recipe.tags.split(",", " ", ";")
                            .map { it.trim() }
                            .filter { it.isNotEmpty() }
                        addAll(tagList.take(3))
                    }

                    LazyColumn(
                        modifier = Modifier
                            .padding(padding)
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        // Hero Image with gradient and overlay title
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(320.dp)
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(recipe.mealThumb)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Recipe Image",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(
                                                    Color.Black.copy(alpha = 0.0f),
                                                    Color.Black.copy(alpha = 0.35f),
                                                    Color.Black.copy(alpha = 0.65f)
                                                )
                                            )
                                        )
                                )
                                Column(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        text = recipe?.meal.toString(),
                                        color = Color.White,
                                        style = MaterialTheme.typography.headlineSmall.copy(
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        chips.take(3).forEach { c ->
                                            AssistChip(
                                                onClick = {},
                                                label = { Text(c) },
                                                leadingIcon = {
                                                    Icon(
                                                        imageVector = Icons.Rounded.Search,
                                                        contentDescription = null
                                                    )
                                                },
                                                shape = CircleShape,
                                                border = BorderStroke(
                                                    1.dp,
                                                    Color.White.copy(alpha = 0.35f)
                                                ),
                                                colors = AssistChipDefaults.assistChipColors(
                                                    labelColor = Color.White,
                                                    leadingIconContentColor = Color.White,
                                                    containerColor = Color.White.copy(alpha = 0.12f)
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // YouTube CTA
                        if (recipe.youtubeUrl.isNotBlank()) {
                            item {
                                FilledTonalButton(
                                    onClick = { uri.openUri(recipe.youtubeUrl) },
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp, vertical = 16.dp)
                                        .fillMaxWidth(),
                                    contentPadding = PaddingValues(14.dp),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Icon(Icons.Rounded.PlayArrow, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Watch on YouTube")
                                }
                            }
                        }

                        // Instructions
                        if (recipe.instructions?.isNotBlank() == true) {
                            item {
                                SectionCard(
                                    title = "Instructions",
                                    content = {
                                        Text(
                                            text = recipe.instructions!!,
                                            style = MaterialTheme.typography.bodyLarge,
                                            lineHeight = 20.sp
                                        )
                                    }
                                )
                            }
                        }

                        // Ingredients
                        if (recipe.ingredientsPair.isNotEmpty()) {
                            item {
                                SectionHeader("Ingredients")
                            }
                            items(
                                recipe.ingredientsPair.filter {
                                    it.first.isNotBlank() || it.second.isNotBlank()
                                }
                            ) { ing ->
                                IngredientRow(
                                    name = ing.first,
                                    measure = ing.second
                                )
                            }
                            item { Spacer(Modifier.height(24.dp)) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        modifier = Modifier
            .padding(start = 16.dp, top = 8.dp, bottom = 8.dp),
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
    )
}

@Composable
private fun SectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Spacer(Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
private fun IngredientRow(
    name: String,
    measure: String
) {
    ElevatedCard(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ShimmerCircleImage(
                imageUrl = getIngredientImageUrl(name),
                size = 56.dp,
                contentDescription = "Ingredient"
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = name.ifBlank { "—" },
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                CompositionLocalProvider(
                    LocalContentColor provides LocalContentColor.current.copy(
                        alpha = 0.75f
                    )
                ) {
                    Text(
                        text = measure.ifBlank { "to taste" },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun ShimmerCircleImage(
    imageUrl: String,
    size: Dp,
    contentDescription: String?
) {
    val shimmerBrush = rememberShimmer()
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(shimmerBrush)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = contentDescription,
            modifier = Modifier
                .matchParentSize()
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
private fun rememberShimmer(): Brush {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val xShimmer by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "x"
    )
    val shimmerColors = listOf(
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    )
    return Brush.linearGradient(
        colors = shimmerColors,
        start = androidx.compose.ui.geometry.Offset(xShimmer - 200f, 0f),
        end = androidx.compose.ui.geometry.Offset(xShimmer, 200f)
    )
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Box(modifier, contentAlignment = Alignment.Center) {
        CircularProgressIndicator(strokeWidth = 4.dp)
    }
}

@Composable
private fun ErrorState(modifier: Modifier = Modifier) {
    Box(modifier, contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Rounded.MoreVert,
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

fun getIngredientImageUrl(name: String): String =
    "https://www.themealdb.com/images/ingredients/${name}.png"
