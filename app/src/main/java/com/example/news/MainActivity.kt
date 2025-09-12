package com.example.news

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.news.presentation.viewmodel.NewsViewModel
import com.example.news.presentation.viewmodel.NewsViewModelFactory
import com.example.news.presentation.screens.DetailScreen
import com.example.news.presentation.screens.NewsScreen
import com.example.news.ui.theme.NewsTheme
import timber.log.Timber

class MainActivity : ComponentActivity() {
    private val viewModel: NewsViewModel by viewModels { NewsViewModelFactory() }
    private val startTime = System.currentTimeMillis()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("Splash startTime: $startTime")
        installSplashScreen()
        Timber.i("App started in ${System.currentTimeMillis() - startTime} ms")
        setContent {
            NewsTheme {
                Timber.plant(Timber.DebugTree())
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "news") {
                    composable("news") {
                        NewsScreen(
                            stateFlow = viewModel.state,
                            effectFlow = viewModel.effect,
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
