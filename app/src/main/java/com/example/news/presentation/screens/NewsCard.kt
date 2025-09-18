package com.example.news.presentation.screens

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import coil.compose.AsyncImage
import com.example.news.R
import com.example.news.domain.model.Article
import com.example.news.presentation.util.formatRelativeTime
import java.util.Locale


@Composable
fun NewsCard(
    article: Article,
    isLoading: Boolean,
    isBookmarked: Boolean,
    onClick: () -> Unit,
    onToggleBookmark: () -> Unit,
) {
    val context = LocalContext.current
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        putExtra(Intent.EXTRA_TEXT, article.url)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (!isLoading) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(0.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = article.sourceName?.uppercase(Locale.getDefault()) ?: "",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black
                )
            }

            AsyncImage(
                model = article.imageUrl,
                contentDescription = article.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                placeholder = painterResource(id = R.drawable.placeholder),
                error = painterResource(id = R.drawable.error)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = article.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = article.description ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(5.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(2.dp)
                ) {
                    Text(
                        text = formatRelativeTime(article.publishedAt),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(
                        onClick = onToggleBookmark,
                        modifier = Modifier.size(35.dp)
                    ) {
                        Icon(
                            painter = painterResource(
                                id = if (isBookmarked) R.drawable.baseline_bookmark_24
                                else R.drawable.outline_bookmark_border_24
                            ),
                            contentDescription = "Bookmark",
                            tint = if (isBookmarked) Color(0xFFC201C9)
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = {
                            startActivity(context, shareIntent, null)
                        },
                        modifier = Modifier.size(35.dp).padding(2.dp)
                    ) {
                        Icon(
                            painter = painterResource(
                                id = R.drawable.baseline_share_24
                            ),
                            contentDescription = "Share",
                            tint = Color.DarkGray

                        )
                    }
                }
            }
            Divider(
                modifier = Modifier
                    .fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                thickness = 2.dp
            )
        }
    }
}