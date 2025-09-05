package com.example.news.repository

//import android.content.Context
//import androidx.datastore.preferences.core.Preferences
//import androidx.datastore.preferences.core.edit
//import androidx.datastore.preferences.core.stringPreferencesKey
//import androidx.datastore.preferences.preferencesDataStore
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.map
//import androidx.datastore.core.DataStore
//
//val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
//
//class Datastore(private val context: Context) {
//
//    companion object {
//        private val COUNTRY_KEY = stringPreferencesKey("country_code")
//    }
//
//    val countryCodeFlow: Flow<String?> = context.dataStore.data
//        .map { preferences ->
//            preferences[COUNTRY_KEY]
//        }
//
//    suspend fun saveCode(code: String) {
//        context.dataStore.edit { preferences ->
//            preferences[COUNTRY_KEY] = code
//        }
//    }
//}