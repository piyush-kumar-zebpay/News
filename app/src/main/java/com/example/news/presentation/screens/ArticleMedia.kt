package com.example.news.presentation.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.news.R
import com.example.news.domain.model.Article
import com.example.news.presentation.util.MediaType

@Composable
fun ArticleMedia(article: Article) {
    val url = article.imageUrl ?: return
    when (MediaType.fromUrl(url)) {
        MediaType.VIDEO -> {
            VideoPlayer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                videoUrl = url
            )
        }
        MediaType.IMAGE -> {
            AsyncImage(
                model = url,
                contentDescription = article.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                placeholder = painterResource(id = R.drawable.placeholder),
                error = painterResource(id = R.drawable.error)
            )
        }
    }
}
