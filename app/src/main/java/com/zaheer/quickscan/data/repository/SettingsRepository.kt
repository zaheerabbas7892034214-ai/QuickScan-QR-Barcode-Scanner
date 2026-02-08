package com.zaheer.quickscan.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {
    
    private object PreferenceKeys {
        val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
        val APP_LOCK_ENABLED = booleanPreferencesKey("app_lock_enabled")
        val USE_BIOMETRIC = booleanPreferencesKey("use_biometric")
        val PIN_CODE = stringPreferencesKey("pin_code")
        val VIBRATE_ON_SCAN = booleanPreferencesKey("vibrate_on_scan")
        val SOUND_ON_SCAN = booleanPreferencesKey("sound_on_scan")
    }
    
    val isDarkMode: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferenceKeys.IS_DARK_MODE] ?: false
        }
    
    val isAppLockEnabled: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferenceKeys.APP_LOCK_ENABLED] ?: false
        }
    
    val useBiometric: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferenceKeys.USE_BIOMETRIC] ?: true
        }
    
    val pinCode: Flow<String?> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferenceKeys.PIN_CODE]
        }
    
    val vibrateOnScan: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferenceKeys.VIBRATE_ON_SCAN] ?: true
        }
    
    val soundOnScan: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferenceKeys.SOUND_ON_SCAN] ?: true
        }
    
    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.IS_DARK_MODE] = enabled
        }
    }
    
    suspend fun setAppLock(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.APP_LOCK_ENABLED] = enabled
        }
    }
    
    suspend fun setUseBiometric(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.USE_BIOMETRIC] = enabled
        }
    }
    
    suspend fun setPinCode(pin: String?) {
        context.dataStore.edit { preferences ->
            if (pin != null) {
                preferences[PreferenceKeys.PIN_CODE] = pin
            } else {
                preferences.remove(PreferenceKeys.PIN_CODE)
            }
        }
    }
    
    suspend fun setVibrateOnScan(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.VIBRATE_ON_SCAN] = enabled
        }
    }
    
    suspend fun setSoundOnScan(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.SOUND_ON_SCAN] = enabled
        }
    }
}
