package com.example.news.data

import java.util.Locale

class CountryCodes {
    val countryCodeToName = mapOf(
        "US" to "United States",
        "IN" to "India",
        "GB" to "United Kingdom",
        "CA" to "Canada",
        "AU" to "Australia",
        "DE" to "Germany",
        "FR" to "France",
        "JP" to "Japan",
        "CN" to "China",
        "BR" to "Brazil",
        "RU" to "Russia",
        "ZA" to "South Africa",
        "MX" to "Mexico",
        "IT" to "Italy",
        "ES" to "Spain",
        "KR" to "South Korea",
        "NG" to "Nigeria",
        "AR" to "Argentina",
        "SE" to "Sweden",
        "NL" to "Netherlands",
        "CH" to "Switzerland",
        "SA" to "Saudi Arabia",
    )

    fun getCountryName(code: String?): String {
        if (code.isNullOrBlank()) return "Unknown"
        return countryCodeToName[code.uppercase(Locale.ROOT)] ?: "Unknown"
    }
}
