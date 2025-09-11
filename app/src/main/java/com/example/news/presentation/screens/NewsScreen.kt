package com.example.news.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.news.R
import com.example.news.presentation.model.NewsEffect
import com.example.news.presentation.model.NewsUiState
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
@Composable
fun NewsScreen(
    stateFlow: StateFlow<NewsUiState>,
    effectFlow: SharedFlow<NewsEffect>,
    navController: NavController
) {
    val state by stateFlow.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        effectFlow.collect { effect ->
            when (effect) {
                is NewsEffect.NavigateToDetail -> {
                    navController.navigate("detail/${effect.index}")
                }
                is NewsEffect.ShowError -> {
                    snackBarHostState.showSnackbar(
                        message = effect.message,
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    LaunchedEffect(state.isOnline) {
        if (!state.isOnline) {
            snackBarHostState.showSnackbar(
                message = "No internet connection",
                duration = SnackbarDuration.Short
            )
        }
        else {
            snackBarHostState.showSnackbar(
                message = "Internet connected",
                duration = SnackbarDuration.Short
            )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
        ) {
            when {
                state.isLoading && state.articles.isEmpty() -> {
                    ShimmerScreen(stateFlow, navController)
                }
                state.error != null && state.articles.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.error!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background),
                        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
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
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
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
                            // Top 5 articles for carousel
                            item {
                                Carousel(
                                    articles = state.articles.take(5),
                                    stateFlow,
                                    navController = navController
                                )
                            }
                            item { Spacer(modifier = Modifier.height(16.dp)) }
                            itemsIndexed(
                                items = state.articles.drop(5),
                                key = { _, article -> article.url }
                            ) { index, article ->
                                NewsCard(article = article, isLoading = state.isLoading) {
                                    navController.navigate("detail/${index + 5}")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
