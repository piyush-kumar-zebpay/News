package com.example.news.presentation.screens

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.news.R
import com.example.news.presentation.model.NewsEffect
import com.example.news.presentation.model.NewsUiState
import com.example.news.presentation.viewmodel.NewsViewModel
import kotlinx.coroutines.flow.SharedFlow
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import timber.log.Timber

@Composable
fun NewsScreen(
    viewModel: NewsViewModel,
    stateFlow: SharedFlow<NewsUiState>,
    effectFlow: SharedFlow<NewsEffect>,
    navController: NavController,
) {
    val state by stateFlow.collectAsState(initial = NewsUiState())

    LaunchedEffect(Unit) {
        effectFlow.collect { effect ->
            when (effect) {
                is NewsEffect.NavigateToDetail -> {
                    navController.navigate("detail/${effect.index}")
                }
                is NewsEffect.Error -> TODO()
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            Timber.d("${state.bookmarkedArticles.size}")
            AnimatedVisibility(
                visible = state.bookmarkedArticles.isNotEmpty(),
                enter = scaleIn(),
                exit = scaleOut(),
            ) {
                FloatingActionButton(
                    onClick = { navController.navigate("bookmarks") },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_bookmark_24),
                        contentDescription = "Bookmark",
                        tint = Color.White
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End,
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
        ) {
            when {
                state.isLoading && state.articles.isEmpty() -> {
                    ShimmerScreen(stateFlow, navController, viewModel)
                }

                state.error != null && state.articles.isEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = state.error!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                        Button(
                            onClick = { navController.navigate("news") },
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(text = "Retry")
                        }
                    }
                }

                else -> {
                    SwipeRefresh(
                        state = rememberSwipeRefreshState(isRefreshing = state.isLoading),
                        onRefresh = { viewModel.loadNews() }
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.background)
                        ) {
                            item { Spacer(modifier = Modifier.height(8.dp)) }

                            if (state.articles.isEmpty()) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 100.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.error),
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(48.dp)
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = "No news available right now.\nPull to refresh",
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            } else {
                                item {
                                    Carousel(
                                        articles = state.articles.take(5),
                                        stateFlow,
                                        navController = navController,
                                        viewModel = viewModel
                                    )
                                }
                                item { Spacer(modifier = Modifier.height(5.dp)) }
                                itemsIndexed(
                                    items = state.articles.drop(5),
                                    key = { _, article -> article.url }
                                ) { _, article ->
                                    val isBookmarked = state.bookmarkedArticles.any { it.url == article.url }
                                    val encodedUrl = Uri.encode(article.url)
                                    NewsCard(
                                        article = article,
                                        isLoading = state.isLoading,
                                        isBookmarked = isBookmarked,
                                        onClick = { navController.navigate("detail/${encodedUrl}") },
                                        onToggleBookmark = {
                                            if (isBookmarked) {
                                                viewModel.removeBookmark(article.url)
                                            } else {
                                                viewModel.addBookmark(article)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(800.dp))
                }
            }
        }
    }
}
