package com.example.news

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
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
import com.google.android.gms.location.LocationServices
import java.util.Locale

class MainActivity : ComponentActivity() {
    private val viewModel: NewsViewModel by viewModels()

    // Permission launcher
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                fetchCountry(this) { country ->
                    viewModel.saveCountryCode(country)
                }
            } else {
                val fallback = Locale.getDefault().country
                viewModel.saveCountryCode(fallback)
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NewsTheme {
                val navController = rememberNavController()

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
                            DetailScreen(article)
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }
    @SuppressLint("MissingPermission")
    private fun fetchCountry(context: Context, onCountryFetched: (String) -> Unit) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                val countryCode = addresses?.firstOrNull()?.countryCode
                    ?: Locale.getDefault().country
                onCountryFetched(countryCode.lowercase())
            } else {
                // fallback
                onCountryFetched(Locale.getDefault().country.lowercase())
            }
        }.addOnFailureListener {
            onCountryFetched(Locale.getDefault().country.lowercase())
        }
    }
}
