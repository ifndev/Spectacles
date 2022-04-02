package com.ifndev.spectacles.model

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoritesStore(val context: Context) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("favorites")
        val FAVORITES_KEY = stringPreferencesKey("favorites")
    }

    val favorites: Flow<Set<String>?> = context.dataStore.data.map { preferences ->
        if (preferences[FAVORITES_KEY] == "") {
            emptySet()
        } else {
            (preferences[FAVORITES_KEY])?.split(",")?.toHashSet() ?: emptySet()
        }
    }

    suspend fun addFavorite(id: String) {
        context.dataStore.edit { preferences ->
            val favorites = (preferences[FAVORITES_KEY])?.split(",")?.toHashSet() ?: emptySet()
            if (preferences[FAVORITES_KEY] != "") {
                preferences[FAVORITES_KEY] = (favorites + id).joinToString(",")
            } else {
                preferences[FAVORITES_KEY] = id
            }
        }
    }

    suspend fun removeFavorite(id: String) {
        context.dataStore.edit { preferences ->
            val favorites = (preferences[FAVORITES_KEY])?.split(",")?.toHashSet() ?: emptySet()
            preferences[FAVORITES_KEY] = (favorites - id).joinToString(",")
        }
    }
}