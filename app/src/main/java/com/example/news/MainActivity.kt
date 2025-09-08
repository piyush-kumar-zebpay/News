package com.example.news

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.news.viewmodel.NewsViewModel
import com.example.news.ui.screens.DetailScreen
import com.example.news.ui.screens.NewsScreen
import com.example.news.ui.theme.NewsTheme

class MainActivity : ComponentActivity() {
    private val viewModel: NewsViewModel by viewModels()
    private val startTime = System.currentTimeMillis()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition {
            //intentionally delayed
            System.currentTimeMillis() < startTime + 1500
        }

        super.onCreate(savedInstanceState)
        setContent {
            NewsTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "news") {
                    composable("news") {
                        NewsScreen(
                            stateFlow = viewModel.state,
                            effectFlow = viewModel.effect,
                            onIntent = { intent -> viewModel.handleIntent(intent) },
                            navController = navController
                        )
                    }
                    composable(
                        "detail/{index}",
                        arguments = listOf(navArgument("index") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val index = backStackEntry.arguments?.getInt("index")
                        index?.let {
                            DetailScreen(articleIndex = it, stateFlow = viewModel.state)
                        }
                    }
                }
            }
        }
    }
}
