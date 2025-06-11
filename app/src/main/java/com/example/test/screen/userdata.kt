package com.example.test.util

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore("user_prefs")

object UserDataStore {
    private val EMAIL_KEY = stringPreferencesKey("email")
    private val USERNAME_KEY = stringPreferencesKey("username")
    private val PASSWORD_KEY = stringPreferencesKey("password")

    suspend fun saveUser(context: Context, email: String, username: String, password: String) {
        context.dataStore.edit { prefs ->
            prefs[EMAIL_KEY] = email
            prefs[USERNAME_KEY] = username
            prefs[PASSWORD_KEY] = password
        }
    }

    fun getEmail(context: Context): Flow<String> = context.dataStore.data
        .map { it[EMAIL_KEY] ?: "" }

    fun getUsername(context: Context): Flow<String> = context.dataStore.data
        .map { it[USERNAME_KEY] ?: "" }

    fun getPassword(context: Context): Flow<String> = context.dataStore.data
        .map { it[PASSWORD_KEY] ?: "" }
}
