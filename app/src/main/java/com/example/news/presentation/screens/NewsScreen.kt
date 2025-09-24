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
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber

@Composable
fun NewsScreen(
    viewModel: NewsViewModel,
    stateFlow: StateFlow<NewsUiState>,
    effectFlow: SharedFlow<NewsEffect>,
    navController: NavController,
) {
    val state by stateFlow.collectAsState()

    LaunchedEffect(Unit) {
        effectFlow.collect { effect ->
            when (effect) {
                is NewsEffect.NavigateToDetail -> {
                    navController.navigate("detail/${effect.index}")
                }
                is NewsEffect.Error -> {}
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
                state.isLoading && state.newsArticles.isEmpty() -> {
                    ShimmerScreen(stateFlow, navController, viewModel)
                }

                state.error != null && state.newsArticles.isEmpty() -> {
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
                            onClick = { viewModel.loadNews()},
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

                            // --- Video Articles Carousel ---
                            if (state.videoArticles.isNotEmpty()) {
                                item {
                                    Carousel(
                                        articles = state.videoArticles,
                                        stateFlow = stateFlow,
                                        navController = navController,
                                        viewModel = viewModel
                                    )
                                }
                                item { Spacer(modifier = Modifier.height(16.dp)) }
                            }

                            // --- News Articles List ---
                            if (state.newsArticles.isNotEmpty()) {
                                itemsIndexed(
                                    items = state.newsArticles,
                                    key = { _, article -> article.url }
                                ) { _, article ->
                                    val isBookmarked = state.bookmarkedArticles.any { it.url == article.url }
                                    val encodedUrl = Uri.encode(article.url)
                                    NewsCard(
                                        article = article,
                                        isLoading = state.isLoading,
                                        isBookmarked = isBookmarked,
                                        onClick = { navController.navigate("detail/$encodedUrl") },
                                        onToggleBookmark = {
                                            if (isBookmarked) viewModel.removeBookmark(article.url)
                                            else viewModel.addBookmark(article)
                                        }
                                    )
                                }
                            } else {
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
                            }
                        }

                    }
                    Spacer(modifier = Modifier.height(800.dp))
                }
            }
        }
    }
}
