package com.example.news

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.news.presentation.model.NewsUiState
import com.example.news.presentation.screens.Bookmarks
import com.example.news.presentation.viewmodel.NewsViewModel
import com.example.news.presentation.viewmodel.NewsViewModelFactory
import com.example.news.presentation.screens.DetailScreen
import com.example.news.presentation.screens.NewsScreen
import com.example.news.presentation.screens.SnackBar
import com.example.news.ui.theme.NewsTheme
import timber.log.Timber

class MainActivity : ComponentActivity() {
    private val viewModel: NewsViewModel by viewModels { NewsViewModelFactory() }
    private val startTime = System.currentTimeMillis()


    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("Splash startTime: $startTime")
        installSplashScreen()
        Timber.i("App started in ${System.currentTimeMillis() - startTime} ms")
        setContent {
            NewsTheme {
                var wasOnline by remember { mutableStateOf(true) }
                val state by viewModel.state.collectAsState(initial = NewsUiState())
                Scaffold(
                    snackbarHost = {
                        Box(modifier = Modifier.fillMaxSize()){
                            if (!state.isOnline) {
                                wasOnline = false
                                SnackBar(
                                    message = "No internet connection!",
                                    isAnimation = false
                                )
                            }
                            else if(!wasOnline) {
                                SnackBar(
                                    message = "Back online!",
                                    color = Color(0xFF1C6E1F),
                                )
                                wasOnline = false
                            }
                        }
                    }
                ){
                    Timber.plant(Timber.DebugTree())
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "news") {
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
                                ?.let { Uri.decode(it) } // decode safely
                            DetailScreen(articleUrl = articleUrl, stateFlow = viewModel.state)
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
