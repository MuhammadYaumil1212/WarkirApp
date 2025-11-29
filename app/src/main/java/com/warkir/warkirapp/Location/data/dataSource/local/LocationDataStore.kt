package com.warkir.warkirapp.Location.data.dataSource.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.warkir.warkirapp.Location.data.model.UserLocationModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "location_settings")

class LocationDataStore(private val context: Context) {
    companion object {
        val IS_LOCATION_SET = booleanPreferencesKey("is_location_set")
        val USER_ADDRESS = stringPreferencesKey("user_address")
        val userLatitude = doublePreferencesKey("user_latitude")
        val userLongitude = doublePreferencesKey("user_longitude")
    }

    suspend fun saveLocation(address: String, lat: Double, long: Double) {
        context.dataStore.edit { preferences ->
            preferences[IS_LOCATION_SET] = true
            preferences[USER_ADDRESS] = address
            preferences[userLatitude] = lat
            preferences[userLongitude] = long
        }
    }

    suspend fun clearLocation() = context.dataStore.edit { preferences ->
        preferences.clear()
    }

    val getLocationFlow: Flow<UserLocationModel> =
        context.dataStore.data.map { preferences ->
            UserLocationModel(
                isLocationSet = preferences[IS_LOCATION_SET] ?: false,
                userAddress = preferences[USER_ADDRESS] ?: "Belum ada Lokasi",
                userLatitude = preferences[userLatitude] ?: 0.0,
                userLongitude = preferences[userLongitude] ?: 0.0
            )
        }
}