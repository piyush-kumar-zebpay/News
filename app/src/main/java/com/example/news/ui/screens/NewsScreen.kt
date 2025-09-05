package com.example.news.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.news.R
//import com.example.news.data.CountryCodes
import com.example.news.model.Article
import com.example.news.utils.Constants.Companion.API_KEY
import com.example.news.viewmodel.NewsViewModel
import kotlinx.coroutines.delay
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen(navController: NavController, viewModel: NewsViewModel) {
    val articles = viewModel.articles
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage
    val code = "us"
    val carouselArticles = articles.mapIndexed { index, article -> index to article }.take(5)
//    val countryName = CountryCodes().getCountryName(code)


    LaunchedEffect(code) {
        viewModel.fetchNews(code, API_KEY)
    }

    PullToRefreshBox(
        isRefreshing = isLoading,
        onRefresh = { viewModel.fetchNews(code, API_KEY) },
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

//                    item {
//                        Box(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(top = 16.dp)
//                                .clip(RoundedCornerShape(12.dp))
//                                .background(MaterialTheme.colorScheme.primaryContainer)
//                                .clickable {
//                                    navController.navigate("intro") {
//                                        popUpTo("news") { inclusive = true }
//                                    }
//                                }
//                                .padding(vertical = 12.dp, horizontal = 16.dp)
//                        ) {
//                            Row(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(horizontal = 8.dp, vertical = 4.dp)
//                                    .heightIn(min = 48.dp),
//                                verticalAlignment = Alignment.CenterVertically
//                            ) {
//                                Text(
//                                    text = "Your Country: $countryName",
//                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
//                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
//                                    modifier = Modifier.weight(1f)
//                                )
//
//                                Icon(
//                                    painter = painterResource(R.drawable.region_change),
//                                    contentDescription = "Change region",
//                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
//                                )
//                            }
//                        }
//                    }

                    item { Spacer(modifier = Modifier.height(8.dp)) }

                    if (articles.isEmpty()) {

                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
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
                                        text = "No news available right now.\nPull down to refresh.",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    } else {
                        // Headlines section
//                        item {
//                            Text(
//                                text = "Headlines",
//                                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 30.sp, fontWeight = FontWeight.ExtraBold),
//                                color = MaterialTheme.colorScheme.primary
//                            )
//                        }
                        item { Carousel(carouselArticles, navController) }

                        item { Spacer(modifier = Modifier.height(16.dp)) }


                        // News list
                        itemsIndexed(
                            items = articles.drop(5),
                            key = { _, article -> article.url }
                        ) { index, article ->
                            NewsCard(article = article) {
                                navController.navigate("detail/${index+5}")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NewsCard(article: Article, onClick: () -> Unit) {
    val title by remember(article.title) { mutableStateOf(article.title) }
    val description by remember(article.description) { mutableStateOf(article.description ?: "") }
    val sourceName by remember(article.source.name) { mutableStateOf(article.source.name) }
    val imageUrl by remember(article.urlToImage) { mutableStateOf(article.urlToImage) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .padding(12.dp)
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(110.dp)
                    .clip(RoundedCornerShape(12.dp)),
                placeholder = painterResource(id = R.drawable.placeholder),
                error = painterResource(id = R.drawable.error)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = sourceName.uppercase(Locale.getDefault()),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 6.dp)
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
