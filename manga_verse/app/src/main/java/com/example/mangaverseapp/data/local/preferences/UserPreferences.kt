package com.example.mangaverseapp.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class UserPreferences @Inject constructor(
    private val context: Context
) {
    companion object {
        private val isUserLoggedInKey = booleanPreferencesKey("is_user_logged_in")
        private val userEmailKey = stringPreferencesKey("user_email")
    }

    val isUserLoggedIn: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[isUserLoggedInKey] ?: false }

    val userEmail: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[userEmailKey] }

    suspend fun setUserLoggedIn(isLoggedIn: Boolean, email: String? = null) {
        context.dataStore.edit { preferences ->
            preferences[isUserLoggedInKey] = isLoggedIn
            if (email != null) {
                preferences[userEmailKey] = email
            }
        }
    }

    suspend fun clearUserSession() {
        context.dataStore.edit { preferences ->
            preferences[isUserLoggedInKey] = false
            preferences.remove(userEmailKey)
        }
    }
}