package com.example.news.presentation.screens

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.news.presentation.model.NewsUiState
import com.example.news.presentation.viewmodel.NewsViewModel
import kotlinx.coroutines.flow.SharedFlow

@Composable
fun Bookmarks(
    navController: NavController,
    stateFlow: SharedFlow<NewsUiState>,
    viewModel: NewsViewModel
) {
    val state by stateFlow.collectAsState(initial = NewsUiState())
    val bookmarkedFullArticles = state.articles.filter { article ->
        state.bookmarkedArticles.any { it.url == article.url }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Bookmarks",
            style = MaterialTheme.typography.headlineLarge,
            fontStyle = FontStyle.Italic ,
            modifier = Modifier.padding(top = 12.dp, bottom = 5.dp)
        )

        LazyColumn {
            if (bookmarkedFullArticles.isEmpty()) {
                item {
                    Text(
                        text = "No bookmarks yet.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                item { Spacer(modifier = Modifier.height(16.dp)) }
                itemsIndexed(
                    items = bookmarkedFullArticles,
                    key = { _, article -> article.url }
                ) { index, article ->
                    val encodedUrl = Uri.encode(article.url)
                    NewsCard(
                        article = article,
                        isLoading = state.isLoading,
                        isBookmarked = true,
                        onClick = {
                            navController.navigate("detail/$encodedUrl") // safe
                        },
                        onToggleBookmark = {
                            viewModel.removeBookmark(article.url)
                        }
                    ){

                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

            }
        }
    }
}
