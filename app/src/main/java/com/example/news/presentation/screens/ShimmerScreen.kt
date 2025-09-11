package com.example.news.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.news.R
import com.example.news.domain.model.Article
import com.example.news.presentation.model.NewsUiState
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale


@Composable
fun ShimmerScreen(stateFlow: StateFlow<NewsUiState>, navController: NavController) {
    val state by stateFlow.collectAsState()
    if(state.isLoading){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ){
            Spacer(modifier = Modifier.height(16.dp))
            Carousel(
                articles = emptyList(),
                stateFlow = stateFlow,
                navController = navController,
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(6) {
                    NewsCardShimmer()
                }
                item { Spacer(modifier = Modifier.height(8.dp)) }
            }
        }
    }
}

@Composable
fun NewsCardShimmer(){
    Card(modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .padding(12.dp)){
            Box(modifier = Modifier
                .fillMaxHeight()
                .width(120.dp)
                .clip(RoundedCornerShape(12.dp))
                .shimmerEffect()
            ){

            }
            Spacer(modifier = Modifier.width(20.dp))
            Column( modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween) {
                Box(modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .shimmerEffect()){
                    Box(modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 1.dp)){
                        Text(text = "          ")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Column(modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween) {
                    Box(modifier = Modifier
                        .shimmerEffect()){
                        Box(modifier = Modifier
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 1.dp)){
                            Text(text = "                                       ")
                        }
                    }

                    Box(modifier = Modifier
                        .shimmerEffect()){
                        Box(modifier = Modifier
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 1.dp)){
                            Text(text = "           ")
                        }
                    }
                }
            }
        }
    }
}
