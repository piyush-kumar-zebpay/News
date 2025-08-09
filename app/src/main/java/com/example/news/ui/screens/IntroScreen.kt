package com.example.news.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.news.viewmodel.NewsViewModel
import com.example.news.R
import com.example.news.data.CountryCodes

@Composable
fun IntroScreen(navController: NavController, viewModel: NewsViewModel) {
    val countryMap = CountryCodes().countryCodeToName
    var selectedCountry by remember { mutableStateOf("IN") } // store the code, not name
    val countryList = countryMap.entries.toList() // list of (code, name)
    var expanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.news),
                contentDescription = "Logo",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(100.dp)
            )

            Text(
                text = "Welcome to News App",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Text(
                text = "Select your region",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Button(
                    onClick = { expanded = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .width(200.dp)
                        .align(Alignment.CenterStart)
                ) {
                    Text(
                        text = countryMap[selectedCountry] ?: "Unknown", // show name in button
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary,
                        textAlign = TextAlign.Center
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    countryList.forEach { (code, name) ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = name,
                                    style = MaterialTheme.typography.bodyLarge,
                                )
                            },
                            onClick = {
                                selectedCountry = code // store code
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = {
                    viewModel.saveCountryCode(selectedCountry) // now stores code like "US"
                    navController.navigate("news") {
                        popUpTo("intro") { inclusive = true }
                    }
                },
                modifier = Modifier.align(Alignment.End),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text(
                    text = "Next",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}
