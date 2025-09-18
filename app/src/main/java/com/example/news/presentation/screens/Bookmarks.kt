package com.example.news.presentation.screens

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.news.presentation.model.NewsUiState
import com.example.news.presentation.viewmodel.NewsViewModel
import kotlinx.coroutines.flow.SharedFlow

@OptIn(ExperimentalMaterial3Api::class)
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bookmarks") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) {padding ->
        Column(modifier = Modifier.padding(padding)) {
            LazyColumn{
                if (bookmarkedFullArticles.isEmpty()) {
                    item {
                        Text(
                            text = "No bookmarks yet.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    itemsIndexed(
                        items = bookmarkedFullArticles,
                        key = { _, article -> article.url }
                    ) { _, article ->
                        val encodedUrl = Uri.encode(article.url)
                        NewsCard(
                            article = article,
                            isLoading = state.isLoading,
                            isBookmarked = true,
                            onClick = {
                                navController.navigate("detail/$encodedUrl")
                            },
                            onToggleBookmark = {

                                viewModel.removeBookmark(article.url)
                            }
                        )

                    }
                }
            }
        }
    }
}