package com.example.news

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.news.presentation.screens.Bookmarks
import com.example.news.presentation.viewmodel.NewsViewModel
import com.example.news.presentation.screens.DetailScreen
import com.example.news.presentation.screens.NewsScreen
import com.example.news.presentation.screens.SnackBar
import com.example.news.ui.theme.NewsTheme
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class MainActivity : ComponentActivity() {
    private val viewModel: NewsViewModel by viewModel()
    private val startTime = System.currentTimeMillis()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("Splash startTime: $startTime")
        installSplashScreen()
        Timber.i("App started in ${System.currentTimeMillis() - startTime} ms")
        setContent {
            NewsTheme {
                val navController = rememberNavController()
                var wasOnline by remember { mutableStateOf(true) }
                val state by viewModel.state.collectAsState()

                Scaffold(
                    snackbarHost = {
                        Box(modifier = Modifier.fillMaxSize()) {
                            if (!state.isOnline) {
                                wasOnline = false
                                SnackBar(
                                    message = "No internet connection!",
                                    isAnimation = false
                                )
                            } else if (!wasOnline) {
                                SnackBar(
                                    message = "Back online!",
                                    color = Color(0xFF1C6E1F),
                                )
                                wasOnline = true
                                viewModel.loadNews()
                            }
                        }
                    }
                ) { padding ->
                    NavHost(
                        navController = navController,
                        startDestination = "news",
                        modifier = Modifier.padding(padding)
                    ) {
                        composable("news") {
                            NewsScreen(
                                viewModel = viewModel,
                                stateFlow = viewModel.state,
                                effectFlow = viewModel.effect,
                                navController = navController
                            )
                        }
                        composable(
                            route = "detail/{articleUrl}",
                            arguments = listOf(navArgument("articleUrl") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val articleUrl = backStackEntry.arguments
                                ?.getString("articleUrl")
                                ?.let { Uri.decode(it) }
                            DetailScreen(articleUrl = articleUrl, stateFlow = viewModel.state, navController)
                        }
                        composable("bookmarks") {
                            Bookmarks(
                                navController = navController,
                                stateFlow = viewModel.state,
                                viewModel = viewModel
                            )
                        }
                    }
                }
            }
        }
    }
}


