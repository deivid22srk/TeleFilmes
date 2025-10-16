package com.telefilmes.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.telegramConfigDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "telegram_config"
)

class TelegramConfigRepository(private val context: Context) {
    
    companion object {
        private val API_ID_KEY = intPreferencesKey("api_id")
        private val API_HASH_KEY = stringPreferencesKey("api_hash")
        
        // Credenciais padrão (desatualizadas - apenas para fallback)
        const val DEFAULT_API_ID = 94575
        const val DEFAULT_API_HASH = "a3406de8d171bb422bb6ddf3bbd800e2"
    }
    
    val apiId: Flow<Int> = context.telegramConfigDataStore.data.map { preferences ->
        preferences[API_ID_KEY] ?: DEFAULT_API_ID
    }
    
    val apiHash: Flow<String> = context.telegramConfigDataStore.data.map { preferences ->
        preferences[API_HASH_KEY] ?: DEFAULT_API_HASH
    }
    
    val hasCustomCredentials: Flow<Boolean> = context.telegramConfigDataStore.data.map { preferences ->
        preferences.contains(API_ID_KEY) && preferences.contains(API_HASH_KEY)
    }
    
    suspend fun saveCredentials(apiId: Int, apiHash: String) {
        context.telegramConfigDataStore.edit { preferences ->
            preferences[API_ID_KEY] = apiId
            preferences[API_HASH_KEY] = apiHash
        }
    }
    
    suspend fun clearCredentials() {
        context.telegramConfigDataStore.edit { preferences ->
            preferences.remove(API_ID_KEY)
            preferences.remove(API_HASH_KEY)
        }
    }
}
