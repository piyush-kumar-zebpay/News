package com.example.news.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.news.R
import com.example.news.ui.mvi.NewsEffect
import com.example.news.ui.mvi.NewsIntent
import com.example.news.ui.mvi.NewsState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen(
    stateFlow: StateFlow<NewsState>,
    effectFlow: Flow<NewsEffect>,
    onIntent: (NewsIntent) -> Unit,
    navController: NavController
) {
    val state by stateFlow.collectAsState()
    LaunchedEffect(Unit) {
        effectFlow.collectLatest { effect ->
            when (effect) {
                is NewsEffect.NavigateToDetail -> {
                    navController.navigate("detail/${effect.index}")
                }
                is NewsEffect.ShowError -> {
                }
            }
        }
    }
    LaunchedEffect(Unit) {
        onIntent(NewsIntent.LoadNews("us"))
    }
    val articles = state.articles
    val isLoading = state.isLoading
    val errorMessage = state.errorMessage
    val carouselArticles = articles.mapIndexed { index, article -> index to article }.take(5)

    PullToRefreshBox(
        isRefreshing = isLoading,
        onRefresh = { onIntent(NewsIntent.Refresh) },
        modifier = Modifier.fillMaxSize()
    ) {
        when {
            isLoading && articles.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            errorMessage != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = errorMessage,
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
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    flingBehavior = ScrollableDefaults.flingBehavior()
                ) {
                    item { Spacer(modifier = Modifier.height(8.dp)) }

                    if (articles.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
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
                                        text = "No news available right now.\nPull down to refresh.",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    } else {
                        item { Carousel(carouselArticles, navController) }
                        item { Spacer(modifier = Modifier.height(16.dp)) }

                        itemsIndexed(
                            items = articles.drop(5),
                            key = { _, article -> article.url }
                        ) { index, article ->
                            NewsCard(article = article) {
                                onIntent(NewsIntent.SelectArticle(index + 5))
                            }
                        }
                    }
                }
            }
        }
    }
}
