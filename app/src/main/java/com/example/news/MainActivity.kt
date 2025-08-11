package com.example.news

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.news.ui.screens.DetailScreen
import com.example.news.ui.screens.IntroScreen
import com.example.news.ui.screens.NewsScreen
import com.example.news.ui.theme.NewsTheme
import com.example.news.viewmodel.NewsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NewsTheme {
                val navController = rememberNavController()
                val viewModel: NewsViewModel = viewModel()
                val code by viewModel.countryCode.collectAsState()
                val startDestination = if (code == null) "intro" else "news"
                NavHost(navController = navController, startDestination = startDestination) {
                    composable("intro") {
                        IntroScreen(navController, viewModel = viewModel)
                    }
                    composable("news") {
                        NewsScreen(navController, viewModel = viewModel)
                    }
                    composable(
                        "detail/{index}",
                        arguments = listOf(navArgument("index") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val index = backStackEntry.arguments?.getInt("index")
                        val article = index?.let { viewModel.articles.getOrNull(it) }
                        if (article != null) {
                            DetailScreen(article, navController)
                        } else {
                            // Optional: Handle invalid index
                        }
                    }
                }
            }
        }
    }
}
