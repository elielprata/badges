package com.emartins.icmbadges.data

import android.content.Context
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {
    companion object {
        val KEY_EMAIL = stringPreferencesKey("email")
        val KEY_PASSWORD = stringPreferencesKey("password")
    }

    val loginFlow: Flow<Pair<String, String?>> = context.dataStore.data
        .map { prefs ->
            val email = prefs[KEY_EMAIL] ?: ""
            val password = prefs[KEY_PASSWORD]
            email to password
        }

    suspend fun saveLogin(email: String, password: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_EMAIL] = email
            prefs[KEY_PASSWORD] = password
        }
    }

    suspend fun clearLogin() {
        context.dataStore.edit { it.clear() }
    }
}